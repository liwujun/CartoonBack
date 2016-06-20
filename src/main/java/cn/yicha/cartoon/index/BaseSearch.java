package cn.yicha.cartoon.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;

import cn.yicha.cartoon.entity.SearchResult;
import cn.yicha.cartoon.service.SearchService;
import cn.yicha.cartoon.util.Config;

/**
 * 基础索引搜索类
 * @author xrx
 * @since 2012-11-08
 */
public abstract class BaseSearch {
	private static final Log log = LogFactory.getLog(BaseSearch.class);
	
	/**
	 * lucene搜索实例
	 */
	private static Searcher searcher = null;

	/**
	 * 初始化获取搜索实例
	 * @return
	 */
	protected abstract boolean init();

	/**
	 * 获取搜索实例searcher
	 * @return
	 */
	public abstract Searcher getSearcher();	

	/**
	 * 切换新索引：如果version版本索引可用，则替换
	 * @param version
	 * @return
	 */
	public abstract boolean changeSearcher(String version);
	
	/**
	 * 
	 * @param searcher
	 * @param field
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 * @date:2013-7-5
	 * @author:gudaihui
	 */
	public SearchResult searchMulti(Searcher searcher, String field, String value, int start, int end) {
		return searchMulti(searcher, field, value, start, end, false);
	}
	
	/**
	 * 搜索精准匹配的复数个值（value用分隔符分隔）
	 * @param searcher
	 * 			搜索实例
	 * @param field
	 * 			搜索的字段名称
	 * @param value
	 * 			搜索的值（用Constants.splits分隔）
	 * @param start
	 * 			搜索的起始位置
	 * @param end
	 * 			搜索的终止位置
	 * @return
	 */
	public SearchResult searchMulti(Searcher searcher, String field, String value, int start, int end, boolean f) {

		if(searcher == null || StringUtils.isBlank(value)){
			return new SearchResult();
		}
		SearchResult result = new SearchResult();
		List<Document> docs = new ArrayList<Document>();
		try {
			BooleanQuery query = new BooleanQuery();
			
			//对value中的每一个单值，添加进查询条件。使用SHOULD使得满足任何一个的结果均被搜索
			String []vas=value.split(Constants.splits);
			TopDocs tops=null;
			for(String va:vas){
				Query term = new TermQuery(new Term(field, va));
				query.add(term, BooleanClause.Occur.SHOULD);
			}
			tops = searcher.search(query, null, end + Config.tortCartoonIds.size());
			
			//取终止位置和搜索结果最大条数的最小值
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
				docs.add(doc);
			}
			result.setDocs(docs);
			result.setCount(tops.totalHits);
		} catch (Exception e) {
			log.error(e.toString());
			return result;
		}
		return result;
	}

	public SearchResult searchMulti(Searcher searcher, String field, String value, int start, int end, Sort sort) {
		return searchMulti(searcher, field, value, start, end, sort, false);
	}
	
	/**
	 * 搜索精准匹配的复数个值（value用分隔符分隔），带排序
	 * @param searcher
	 * 			搜索实例
	 * @param field
	 * 			搜索的字段名称
	 * @param value
	 * 			搜索的值（用Constants.splits分隔）
	 * @param start
	 * 			搜索的起始位置
	 * @param end
	 * 			搜索的终止位置
	 * @param sort
	 * 			排序条件
	 * @return
	 */
	public SearchResult searchMulti(Searcher searcher, String field, String value, int start, int end, Sort sort, boolean f) {

		if(searcher == null || StringUtils.isBlank(value)){
			return new SearchResult();
		}
		SearchResult searchResult = new SearchResult();
		List<Document> result = new ArrayList<Document>();
		try {
			BooleanQuery query = new BooleanQuery();
			
			//对value中的每一个单值，添加进查询条件。使用SHOULD使得满足任何一个的结果均被搜索
			String []vas=value.split(Constants.splits);
			for(String va:vas){
				Query term = new TermQuery(new Term(field, va));
				query.add(term, BooleanClause.Occur.SHOULD);
			}
			
			//排序搜索
			TopDocs tops = searcher.search(query, null, end + Config.tortCartoonIds.size(), sort);
			searchResult.setCount(tops.totalHits);
			
			//取终止位置和搜索结果最大条数的最小值
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
				result.add(doc);
			}
		} catch (Exception e) {
			log.error(e.toString());
			return searchResult;
		}
		searchResult.setDocs(result);
		return searchResult;
	}
	
	public SearchResult searchMulti(Searcher searcher,
			Map<String, String> params, int start, int end, Sort sort) {
		return searchMulti(searcher, params, start, end, sort, false);
	}
	
	/**
	 * 搜索精准匹配的复数个值（value用分隔符分隔），带排序。用于过滤搜索
	 * @param searcher
	 * 			搜索实例
	 * @param params
	 * 			搜索的字段名称和值的map
	 * @param start
	 * 			搜索的起始位置
	 * @param end
	 * 			搜索的终止位置
	 * @param sort
	 * 			排序条件
	 * @param f 过滤
	 * @return
	 */
	public SearchResult searchMulti(Searcher searcher, Map<String, String> params, int start, int end, Sort sort, boolean f) {

		//如果params为空，则对所有数据进行排序
		if(searcher == null || params==null || params.isEmpty()){
			return searchWildcard(searcher, "cartoonCate", "*", start, end, sort, f);
		}
		SearchResult searchResult = new SearchResult();
		List<Document> result = new ArrayList<Document>();
		try {
			BooleanQuery query = new BooleanQuery();
			for(String field : params.keySet()) {
				BooleanQuery queryInner = new BooleanQuery();
				
				//对value中的每一个单值，添加进查询条件。使用SHOULD使得满足任何一个的结果均被搜索
				String []vas = params.get(field).split(Constants.splits);
				for(String va:vas){
					Query term = new TermQuery(new Term(field, va));
					queryInner.add(term, BooleanClause.Occur.SHOULD);
				}
				query.add(queryInner, BooleanClause.Occur.MUST);
			}
			TopDocs tops = searcher.search(query, null, end + Config.tortCartoonIds.size(), sort);
			searchResult.setCount(tops.totalHits);
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
				result.add(doc);
			}
		} catch (Exception e) {
			log.error(e.toString());
			return searchResult;
		}
		searchResult.setDocs(result);
		return searchResult;
	}
	
	public SearchResult searchMulti(Searcher searcher, Map<String, String> params, int start, int end) {
		return searchMulti(searcher, params, start, end, null, false);
	}

	/**
	 * 搜索复数个值，精准匹配
	 * @param field
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResult searchMulti(String field, String value, int start, int end) {
		searcher = getSearcher();
		if(searcher == null || StringUtils.isBlank(value)){
			return new SearchResult();
		}

		return searchMulti(searcher, field, value, start, end);
	}

	public SearchResult searchStandard(Searcher searcher, String field, String value, int start, int end){
		return searchStandard(searcher, field, value, start, end, false);
	}
	
	/**
	 * 标准搜索
	 * @param searcher
	 * @param field
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResult searchStandard(Searcher searcher, String field, String value, int start, int end, boolean f){

		SearchResult result = new SearchResult();
		List<Document> docs = new ArrayList<Document>();
		try {
			QueryParser queryParser=new QueryParser(field, new StandardAnalyzer());
			Query query = queryParser.parse(value);
			TopDocs tops = searcher.search(query, null, end + Config.tortCartoonIds.size());
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
				docs.add(doc);
			}
			result.setDocs(docs);
			result.setCount(tops.totalHits);
		} catch (Exception e) {
			log.error(e.toString());
			return result;
		}
		return result;
	}
	
	/**
	 * 标准搜索
	 * @param searcher
	 * @param field
	 * @param params
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	public SearchResult searchStandard(Searcher searcher, String field, Map<String, String> params, String value, int start, int end, boolean f){

		SearchResult result = new SearchResult();
		List<Document> docs = new ArrayList<Document>();
		BooleanQuery query = new BooleanQuery();
		try {
			QueryParser queryParser=new QueryParser(field, new StandardAnalyzer());
			Query parserQuery = queryParser.parse(value);
			query.add(parserQuery, BooleanClause.Occur.MUST);
			for(String fieldTmp : params.keySet()) {
				BooleanQuery queryInner = new BooleanQuery();
				String []vas = params.get(fieldTmp).split(Constants.splits);
				for(String va:vas){
					Query term = new TermQuery(new Term(fieldTmp, va));
					queryInner.add(term, BooleanClause.Occur.SHOULD);
				}
				query.add(queryInner, BooleanClause.Occur.MUST);
			}
			TopDocs tops = searcher.search(query, null, end + Config.tortCartoonIds.size());
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
				docs.add(doc);
			}
			result.setDocs(docs);
			result.setCount(tops.totalHits);
		} catch (Exception e) {
			log.error(e.toString());
			return result;
		}
		return result;
	}
	
	/**
	 * 筛选搜索，
	 * @param searcher
	 * @param field 搜索字段
	 * @param params 筛选参数
	 * @param value 搜索值
	 * @param start
	 * @param end
	 * @param f
	 * @return
	 */
	public SearchResult searchExcept(Searcher searcher, String field, Map<String, String> params, String value, int start, int end, boolean f){

		SearchResult result = new SearchResult();
		List<Document> docs = new ArrayList<Document>();
		try {
			BooleanQuery query = new BooleanQuery();
			QueryParser queryParser=new QueryParser(field, new StandardAnalyzer());
			Query parserQuery = queryParser.parse(value);
			query.add(parserQuery, BooleanClause.Occur.MUST);
			for(String fieldTmp : params.keySet()) {
				BooleanQuery queryInner = new BooleanQuery();
				String []vas = params.get(fieldTmp).split(Constants.splits);
				for(String va:vas){
					Query term = new TermQuery(new Term(fieldTmp, va));
					queryInner.add(term, BooleanClause.Occur.SHOULD);
				}
				query.add(queryInner, BooleanClause.Occur.MUST_NOT);
			}
			TopDocs tops = searcher.search(query, null, end + Config.tortCartoonIds.size());
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
				docs.add(doc);
			}
			result.setDocs(docs);
			result.setCount(tops.totalHits);
		} catch (Exception e) {
			log.error(e.toString());
			return result;
		}
		return result;
	}

	/**
	 * 标准搜索
	 * @param searcher
	 * @param field
	 * @param value
	 * @return
	 */
	public SearchResult searchStandard(Searcher searcher, String field, String value){
		return searchStandard(searcher, field, value, 0, Config.maxResult);
	}

	/**
	 * 模糊搜索
	 * @param searcher
	 * @param field
	 * @param value
	 * @param start
	 * @param end
	 * @param sort
	 * @return
	 */
	public SearchResult searchWildcard(Searcher searcher, String field, String value, int start, int end, Sort sort) {
		return searchWildcard(searcher, field, value, start, end, sort, false);
	}
	
	/**
	 * 模糊搜索
	 * @param searcher
	 * @param field
	 * @param value
	 * @param start
	 * @param end
	 * @param sort
	 * @param f
	 * @return
	 * @date:2013-7-5
	 * @author:gudaihui
	 */
	public SearchResult searchWildcard(Searcher searcher, String field, String value, int start, int end, Sort sort, boolean f) {
		if(searcher == null || StringUtils.isBlank(value)){
			return null;
		}
		SearchResult searchResult = new SearchResult();
		List<Document> result = new ArrayList<Document>();
		try {
			WildcardQuery query = new WildcardQuery(new Term(field, value));
			TopDocs tops;
			if(sort == null) tops = searcher.search(query, null, end);
			else tops = searcher.search(query, null, end + Config.tortCartoonIds.size(), sort);
			searchResult.setCount(tops.totalHits);
			end = Math.min(end, tops.totalHits);
			ScoreDoc[] sds = tops.scoreDocs;
			for (int i = start, c = start; c < end; i++) {
				Document doc = searcher.doc(sds[i].doc);
				// 侵权漫画，跳过
				if(f && SearchService.isTortCartoon(doc)){
					continue;
				}
				c++;
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
	 * 区间搜索，包含两端
	 * @param searcher
	 * @param field
	 * @return
	 */
	public int searchRange(Searcher searcher, String field, String lowerTerm, String upperTerm,
			boolean includeLower, boolean includeUpper, Query query) {
		if(searcher == null){
			return 0;
		}
		RangeFilter filter = new RangeFilter(field, lowerTerm, upperTerm, includeLower, includeUpper);
		try {
			Hits tops = searcher.search(query, filter);
			return tops.length();
		} catch (IOException e) {
			log.error(e.toString());
			return 0;
		}
	}
	
	/**
	 * 获取某个字段的去重值
	 * @param searcher
	 * @param field
	 * @param fieldNames
	 * @return
	 */
	public List<String> searchDistinctField(Searcher searcher, String field, List<String> fieldNames) {
		return searchDistinctField(searcher, field, fieldNames, false);
	}
	
	/**
	 * 获取某个字段的去重值
	 * @param searcher
	 * @param field
	 * @param fieldNames
	 * @return
	 */
	public List<String> searchDistinctField(Searcher searcher, String field, List<String> fieldNames, boolean f) {
		
		//fieldNames为空：第一次搜索，模糊搜索第一条结果的field字段
		if(fieldNames == null) {
			fieldNames = new ArrayList<String>();
			SearchResult searchResult = searchWildcard(searcher, field, "*", 0, 1, null, f);
			if(searchResult == null || searchResult.getDocs().isEmpty()) {
				return fieldNames;
			}
			fieldNames.add(searchResult.getDocs().get(0).get(field));
			
			//递归搜索所有字段值
			return searchDistinctField(searcher, field, fieldNames, f);
		}
		
		//fieldNames不为空，去掉field字段值为fieldNames的数据，搜索数据
		else {
			BooleanQuery query = new BooleanQuery();
			WildcardQuery wildcardQuery = new WildcardQuery(new Term(field, "*"));
			query.add(wildcardQuery, BooleanClause.Occur.MUST);
			BooleanQuery innerQuery = new BooleanQuery();
			for(String fieldName : fieldNames) {
				Query term = new TermQuery(new Term(field, fieldName));
				innerQuery.add(term, BooleanClause.Occur.SHOULD);
			}
			query.add(innerQuery, BooleanClause.Occur.MUST_NOT);
			try {
				//只搜一条数据
				TopDocs tops = searcher.search(query, null, 1);

				//递归搜索所有字段值
				if(tops != null && tops.scoreDocs.length > 0) {
					fieldNames.add(searcher.doc(tops.scoreDocs[0].doc).get(field));
					return searchDistinctField(searcher, field, fieldNames);
				}
				
				//返回结果
				else {
					return fieldNames;
				}
			} catch (IOException e) {
				log.error(e.toString());
				return fieldNames;
			}
		}
	}
	
	/**
	 * 获取区间中的总数
	 * @param field
	 * @param lowerTerm
	 * @param upperTerm
	 * @param query
	 * @return
	 */
	public int searchRange(Searcher searcher, String field, String lowerTerm, String upperTerm, Query query) {
		return searchRange(searcher, field, lowerTerm, upperTerm, true, true, query);
	}
	
	public static void main(String[] args) {
		String str = null;
		System.out.println(StringUtils.isEmpty(str));
	}

}
