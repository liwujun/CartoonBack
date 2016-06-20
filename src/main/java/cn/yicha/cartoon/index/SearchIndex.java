package cn.yicha.cartoon.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.WildcardQuery;


import cn.yicha.cartoon.entity.SearchResult;
import cn.yicha.cartoon.util.Config;
import cn.yicha.cartoon.util.DateUtils;

/**
 * 漫画搜索
 * @author xrx
 * @since 2012-11-08
 */
public class SearchIndex extends BaseSearch{
	private static final Log log = LogFactory.getLog(SearchIndex.class);
	private static Searcher searcher = null;
	private static SearchIndex searchIndex = null;
	
	SearchResult searchResult = new SearchResult();

	/**
	 * 初始化
	 */
	@Override
	public boolean init() {
		searcher = getSearcher();
		return searcher != null;
	}

	private SearchIndex() {
	}

	public static SearchIndex getInstance() {
		if(searchIndex == null) {
			synchronized (SearchIndex.class) {
				if(searchIndex == null) {
					searchIndex = new SearchIndex();
				}
			}
		}
		return searchIndex;
	}

	/**
	 * 获取searcher
	 */
	@Override
	public Searcher getSearcher(){
		if(searcher == null && Config.indexPath != null && Config.indexName != null){
			synchronized (SearchIndex.class) {
				if(searcher == null && Config.indexPath != null){
					try {
						searcher = new IndexSearcher(Config.indexPath + Config.indexName);
					} catch (IOException e) {
						log.error(e.toString());
						return searcher;
					}
				}
			}
		}
		return searcher;
	}

	/**
	 * 切换新索引：如果version版本索引可用，则替换
	 */
	@Override
	public boolean changeSearcher(String version){
		Searcher searcherTmp = null;
		try {
			searcherTmp = new IndexSearcher(Config.indexPath + version);
			SearchResult result = searchMulti(searcherTmp, new HashMap<String, String>(), 0, 10);
			if(result.getDocs().size() == 10) {//如果version版本索引可用，则替换
				searcher = searcherTmp;
				searcherTmp = null;
				return true;
			}
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		}
		return false;
	}

	/**
	 * 根据关键字搜索
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResult keySearch(String key, int start, int end) {
		Searcher searcher = getSearcher();
		if(searcher == null || StringUtils.isEmpty(key)){
			return new SearchResult();
		}
//		SearchResult result = searchStandard(searcher, "cartoonName", key, start, end);
		searchResult = searchStandard(searcher, "cartoonName", key, start, end);
//		searchResult.setCount(end);
//		return result;
		return searchResult;
	}
	
	/**
	 * 根据关键字搜索
	 * @param key
	 * @param params
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResult keySearch(String key, Map<String, String> params, int start, int end, boolean f) {
		Searcher searcher = getSearcher();
		if(searcher == null || StringUtils.isEmpty(key)){
			return new SearchResult();
		}
//		SearchResult result = searchStandard(searcher, "cartoonName", params, key, start, end);
//		return result;
//		SearchResult result = searchStandard(searcher, "cartoonName", key, start, end);
		searchResult = searchStandard(searcher, "cartoonName", params, key, start, end, f);
//		return result;
		return searchResult;
	}

	/**
	 * 根据分类搜索
	 * @param cate
	 * @param start
	 * @param end
	 * @return
	 */
	/*public SearchResult cateSearch(String cate, int start, int end) {
		Searcher searcher = getSearcher();
		if(searcher == null || StringUtils.isEmpty(cate)){
			return null;
		}
		Sort sort = new Sort(new SortField("cartoonPopular", SortField.LONG, true));
		SearchResult searchResult;
		if(cate.equals("所有")) 
			searchResult = searchWildcard(searcher, "cartoonCate", "*", start, end, sort);
		else 
			searchResult = searchMulti(searcher, "cartoonCate", cate, start, end, sort);
		return searchResult;
	}*/

	/**
	 * 根据ID搜索详细
	 * @param id
	 * @return
	 */
	public SearchResult detailSearch(String id) {
		Searcher searcher = getSearcher();
		if(searcher == null || StringUtils.isEmpty(id)){
			return new SearchResult();
		}
		SearchResult result = searchMulti(searcher, "cartoonId", id, 0, 1, false);
		return result;
	}

	/**
	 * 获取最近更新数据（带筛选参数params）
	 * @param params
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResult getLatestData(Map<String, String> params, int start, int end, boolean f) {
		Searcher searcher = getSearcher();
		Sort sort = new Sort(new SortField("updateTime", SortField.STRING, true));
		SearchResult searchResult = searchMulti(searcher, params, start, end, sort, f);
		return searchResult;
	}

	/**
	 * 分类搜索（带筛选参数，按人气排序）
	 * @param params
	 * @param start
	 * @param end
	 * @param f
	 * @return
	 */
	public SearchResult cateSearch(Map<String, String> params, int start,
			int end, boolean f) {
		Searcher searcher = getSearcher();
		Sort sort = new Sort(new SortField("cartoonPopular", SortField.LONG, true));
		SearchResult searchResult;
		searchResult = searchMulti(searcher, params, start, end, sort, f);
		return searchResult;
	}

	/**
	 * 根据作者搜索，排除cid指定的漫画
	 * @param author
	 * @param cid
	 * @param start
	 * @param end
	 * @param f
	 * @return
	 */
	public SearchResult authorSearch(String author, String cid, int start, int end, boolean f) {
		Searcher searcher = getSearcher();
		if(searcher == null || StringUtils.isEmpty(author)){
			return new SearchResult();
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("cartoonId", cid);
		SearchResult result = searchExcept(searcher, "cartoonAuthor", params, author, start, end, f);
		return result;
	}
	
	/**
	 * 获取所有分类
	 * @return
	 */
	public List<String> getAllCate() {
		Searcher searcher = getSearcher();
		if(searcher == null){
			return null;
		}
		return searchDistinctField(searcher, "cartoonCate", null);
	}
	
	/**
	 * 获取今日更新数据总数
	 * @return
	 */
	public int searchNewDataCount() {
		Searcher searcher = getSearcher();
		if(searcher == null){
			return 0;
		}
		try {
			WildcardQuery query = new WildcardQuery(new Term("cartoonCate", "*"));
			String today = DateUtils.getCurDateStr("yyyy-MM-dd");
			String minTime = today + " 00:00:00.0";
			String maxTime = today + " 23:59:59.0";
			return searchRange(searcher, "updateTime", minTime, maxTime, query);
		} catch (Exception e) {
			log.error(e.toString());
			return 0;
		}
	}
	
	public static void main(String[] args) {
		SearchIndex searchIndex = SearchIndex.getInstance();
		/*List<String> cateList = searchIndex.getAllCate();
		for(String cate : cateList) {
			System.out.println(cate);
		}
		*/
		SearchResult searchResult1 = searchIndex.keySearch("火影忍者", 0, 10);
		SearchResult searchResult2 = searchIndex.keySearch("海贼王", 0, 20);
//		System.out.println(searchResult1.getCount());
//		System.out.println(searchResult2.getCount());
		/*SearchResult searchResult = searchIndex.authorSearch("司徒剑桥", "94cff4c57701b6a9", 1, 10);
		
		for(Document doc : searchResult.getDocs()) {
			System.out.println(doc.get("cartoonId"));
		}*/
	}
}
