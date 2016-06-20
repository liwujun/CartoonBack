package cn.yicha.cartoon.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import cn.yicha.cartoon.entity.SearchResult;
import cn.yicha.cartoon.util.Config;
import cn.yicha.cartoon.util.DateUtils;

/**
 * 漫咖啡搜索
 * @author xrx
 * @since 2012-11-08
 */
public class CoffSearchIndex extends BaseSearch {
	private static final Log log = LogFactory.getLog(CoffSearchIndex.class);
	private static Searcher searcher = null;
	private static CoffSearchIndex searchIndex = null;
	
	/**
	 * 初始化
	 */
	@Override
	public boolean init() {
		searcher = getSearcher();
		
		//初始化缓存
		{
			JSONObject json = new JSONObject();
			int totalCount = 0;
			JSONArray ja = new JSONArray();
			for(String cate : getAllCate()) {
				SearchResult searchResult = searchLatest(cate, 0, 1);
				if(searchResult.getDocs().size() > 0) {
					JSONObject cateJson = new JSONObject();
					int count = searchNewDataCount(cate);
					cateJson.put("count", count);
					cateJson.put("cate", cate);
					cateJson.put("updateTime", searchResult.getDocs().get(0).get("createTime"));
					ja.add(cateJson);
					totalCount += count;
				}
			}
			json.put("totalCount", totalCount);
			json.put("dataList", ja);
			
			//漫咖啡首页数据
			Config.generalCache = json.toString();
			
			int maxDoc = 0;
			try {
				maxDoc = searcher.maxDoc();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			Set<String> allCoffIdsTmp = new HashSet<String>();
			try {
				for(int i = 0; i < maxDoc; i++) {
					Document doc = searcher.doc(i);
					allCoffIdsTmp.add(doc.get("coffId"));
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			//漫咖啡全部id
			Config.allCoffIds = allCoffIdsTmp;
			allCoffIdsTmp = null;
		}
		return searcher != null;
	}

	private CoffSearchIndex() {
	}

	public static CoffSearchIndex getInstance() {
		if(searchIndex == null) {
			synchronized (SearchIndex.class) {
				if(searchIndex == null) {
					searchIndex = new CoffSearchIndex();
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
		if(searcher == null && Config.indexPath != null && Config.coffIndexName != null){
			synchronized (SearchIndex.class) {
				if(searcher == null && Config.indexPath != null){
					try {
						searcher = new IndexSearcher(Config.indexPath + Config.coffIndexName);
					} catch (IOException e) {
						log.error(e.toString());
						return null;
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
			SearchResult result = searchMulti(searcherTmp, new HashMap<String, String>(), 0, 10, null);
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
	 * 搜索复数个值，精准匹配，带排序
	 * @param searcher
	 * @param params
	 * @param start
	 * @param end
	 * @param sort
	 * @return
	 */
	@Override
	public SearchResult searchMulti(Searcher searcher, Map<String, String> params, int start, int end, Sort sort) {
		if(searcher == null || params == null || params.isEmpty()){
			return searchWildcard(searcher, "coffCate", "*", start, end, sort);
		}
		SearchResult searchResult = new SearchResult();
		List<Document> result = new ArrayList<Document>();
		try {
			BooleanQuery query = new BooleanQuery();
			for(String field : params.keySet()) {
				BooleanQuery queryInner = new BooleanQuery();
				String []vas = params.get(field).split(Constants.splits);
				for(String va:vas){
					Query term = new TermQuery(new Term(field, va));
					queryInner.add(term, BooleanClause.Occur.SHOULD);
				}
				query.add(queryInner, BooleanClause.Occur.MUST);
			}
			TopDocs tops = searcher.search(query, null, end, sort);
			searchResult.setCount(tops.totalHits);
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start; i < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				result.add(doc);
			}
		} catch (Exception e) {
			log.error(e.toString());
			return searchResult;
		}
		searchResult.setDocs(result);
		return searchResult;
	}
	
	/**
	 * 获取今日更新数据总数
	 * @param cate 获取cate分类下的今日更新总数
	 * @return
	 */
	public int searchNewDataCount(String cate) {
		Searcher searcher = getSearcher();
		if(searcher == null){
			return 0;
		}
		try {
			QueryParser queryParser=new QueryParser("coffCate", new KeywordAnalyzer());
			Query query = queryParser.parse(cate);
			String today = DateUtils.getCurDateStr("yyyy-MM-dd");
			String minTime = today + " 00:00:00.0";
			String maxTime = today + " 23:59:59.0";
			return searchRange(searcher, "createTime", minTime, maxTime, query);
		} catch (Exception e) {
			log.error(e.toString());
			return 0;
		}
	}
	
	/**
	 * 根据分类获取最新数据
	 * @param cate
	 * @return
	 */
	public SearchResult searchLatest(String cate, int start, int end) {
		Searcher searcher = getSearcher();
		if(searcher == null){
			return new SearchResult();
		}
		Sort sort = new Sort(new SortField("createTime", true));
		return searchMulti(searcher, "coffCate", cate, start, end, sort);
	}
	
	/**
	 * 获取漫咖啡所有分类
	 * @return
	 */
	public List<String> getAllCate() {
		Searcher searcher = getSearcher();
		if(searcher == null){
			return null;
		}
		return searchDistinctField(searcher, "coffCate", null);
	}
	
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
		SearchResult result = searchMulti(searcher, "coffId", id, 0, 1);
		return result;
	}
	
	public static void main(String[] args) {
		CoffSearchIndex index = CoffSearchIndex.getInstance();
		System.out.println(index.searchNewDataCount("内涵漫画"));
	}
}
