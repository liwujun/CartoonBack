package cn.yicha.cartoon.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import cn.yicha.cartoon.entity.SearchResult;
import cn.yicha.cartoon.index.SearchIndex;
import cn.yicha.cartoon.util.Config;
import cn.yicha.cartoon.util.SplitCaller;
import cn.yicha.cartoon.util.SplitUtil;

/**
 * 搜索业务层
 * @author xrx
 * @since 2012-09-18
 */
public class SearchService {
	SearchIndex searchIndex = SearchIndex.getInstance();
	Doc2JSON doc2json = new Doc2JSON();

	private static final Log log = LogFactory.getLog(Doc2JSON.class);
	
	/**
	 * 搜索分类数据
	 * @param cate
	 * @param pno
	 * @param psize
	 * @return
	 */
	/*public String cateSearch(String cate, int pno, int psize) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(cate)) {
			result.put("error", "cate is null");
			log.error("cate is null");
			return result.toString();
		}
		int start = pno * psize;
		int end = start + psize;
		//搜索数据
		SearchResult searchResult = searchIndex.cateSearch(cate, start, end);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : searchResult.getDocs()) {
			JSONObject data = doc2json.parseCate(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		result.put("count", searchResult.getCount());
		return result.toString();
	}*/
	
	/**
	 * 搜索详细数据
	 * @param id
	 * @return
	 */
	public String detailSearch(String id) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(id)) {
			result.put("error", "id is null");
			log.error("id is null");
			return result.toString();
		}
		//搜索数据
		SearchResult sResult = searchIndex.detailSearch(id);
		List<Document> docs = sResult.getDocs();
		//封装数据
		JSONObject data = new JSONObject();
		if(docs.size() != 0) {
			Document doc = docs.get(0);
			data = doc2json.parseDetail(doc);
		}
		result.put("data", data);
		return result.toString();
	}
	
	/**
	 * 搜索漫画内容，供阅读
	 * @param id
	 * @return
	 */
	public String contentSearch(String id) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(id)) {
			result.put("error", "id is null");
			log.error("id is null");
			return result.toString();
		}
		//搜索数据
		SearchResult sResult = searchIndex.detailSearch(id);
		//封装数据
		JSONObject data = new JSONObject();
		List<Document> docs = sResult.getDocs();
		if(docs.size() != 0) {
			Document doc = docs.get(0);
			data = doc2json.parseContent(doc);
		}
		result.put("data", data);
		return result.toString();
	}
	
	/**
	 * 根据关键词搜索漫画
	 * @param key
	 * @param pno
	 * @param psize
	 * @return
	 */
	/*public String keySearch(String key, int pno, int psize) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(key)) {
			result.put("error", "key is null");
			log.error("key is null");
			return result.toString();
		}
		int start = pno * psize;
		int end = start + psize;
		//去噪
		key = SplitUtil.escapeKeyword(key);
		//分词
		key = SplitCaller.getSplitKeyword(key);
		//搜索数据
		SearchResult sResult = searchIndex.keySearch(key, start, end);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : sResult.getDocs()) {
			JSONObject data = doc2json.parseSearch(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		result.put("count", sResult.getCount());
		return result.toString();
	}*/

	/**
	 * 是否是侵权漫画
	 * @param doc
	 * @return
	 * @date:2013-7-1
	 * @author:gudaihui
	 */
	public static boolean isTortCartoon(Document doc){
		return Config.tortCartoonIds.contains(doc.get("cartoonId"));
	}
	
	/**
	 * 根据关键词搜索漫画（带筛选功能）
	 * @param key
	 * @param cate
	 * @param area
	 * @param state
	 * @param py
	 * @param pno
	 * @param psize
	 * @param f 过滤侵权
	 * @return
	 * @date:2013-7-5
	 * @author:gudaihui
	 */
	public String keySearch(String key, String cate, String area, String state,
			String py, int pno, int psize, boolean f) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(key)) {
			result.put("error", "key is null");
			log.error("key is null");
			return result.toString();
		}
		Map<String, String> params = new HashMap<String, String>();
		if(!StringUtils.isEmpty(cate)) params.put("cartoonCate", cate);
		if(!StringUtils.isEmpty(area)) params.put("cartoonArea", area);
		if(!StringUtils.isEmpty(state)) params.put("cartoonState", state);
		if(!StringUtils.isEmpty(py)) params.put("cartoonNamePy", py.toLowerCase());
		int start = pno * psize;
		int end = start + psize;
		//去噪
		key = SplitUtil.escapeKeyword(key);
		//分词
		key = SplitCaller.getSplitKeyword(key);
		//搜索数据
		SearchResult sResult = searchIndex.keySearch(key, params, start, end, f);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : sResult.getDocs()) {
			JSONObject data = doc2json.parseSearch(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		result.put("count", sResult.getCount());
		return result.toString();
	}
	
	/**
	 * 搜索相关词，不需要过滤
	 * @param key
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String relateWordsSearch(String key, int pno, int psize) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(key)) {
			result.put("error", "key is null");
			log.error("key is null");
			return result.toString();
		}
		int start = pno * psize;
		int end = start + psize;
		//搜索数据
		SearchResult sResult = searchIndex.keySearch(key, start, end);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : sResult.getDocs()) {
			JSONObject data = doc2json.parseRelateWords(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		return result.toString();
	}

	/**
	 * 获取最近更新数据（带筛选）
	 * @param cate
	 * @param area
	 * @param state
	 * @param py
	 * @param pno
	 * @param psize
	 * @param f
	 * @return
	 */
	public String getLatestData(String cate, String area, String state, String py, int pno, int psize, boolean f) {
		final String cacheKey = ifNull(cate) + "#" + ifNull(area) + "#" + ifNull(state) + "#" + ifNull(py) + "#" + pno + "#" + psize;
		if(Config.latestData.containsKey(cacheKey)) {//从缓存获取
			return Config.latestData.get(cacheKey);
		}
		JSONObject result = new JSONObject();
		int start = pno * psize;
		int end = start + psize;
		//搜索数据
		Map<String, String> params = new HashMap<String, String>();
		if(!StringUtils.isEmpty(cate)) params.put("cartoonCate", cate);
		if(!StringUtils.isEmpty(area)) params.put("cartoonArea", area);
		if(!StringUtils.isEmpty(state)) params.put("cartoonState", state);
		if(!StringUtils.isEmpty(py)) params.put("cartoonNamePy", py.toLowerCase());
		SearchResult searchResult = searchIndex.getLatestData(params, start, end, f);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : searchResult.getDocs()) {
			JSONObject data = doc2json.parseLatestData(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		result.put("count", searchResult.getCount());
		result.put("todayUpdate", searchIndex.searchNewDataCount());
		
		final String resultStr = result.toString();
		
		//添加到缓存
		new Thread(new Runnable() {
			
			public void run() {
				Config.latestData.put(cacheKey, resultStr);
			}
		}).start();
		return resultStr;
	}
	
	/**
	 * 筛选搜索
	 * @param cate
	 * @param area
	 * @param state
	 * @param py
	 * @param pno
	 * @param psize
	 * @param f
	 * @return
	 */
	public String cateSearch(String cate, String area, String state, String py,
			int pno, int psize, boolean f) {
		JSONObject result = new JSONObject();
		int start = pno * psize;
		int end = start + psize;
		Map<String, String> params = new HashMap<String, String>();
		if(!StringUtils.isEmpty(cate)) params.put("cartoonCate", cate);
		if(!StringUtils.isEmpty(area)) params.put("cartoonArea", area);
		if(!StringUtils.isEmpty(state)) params.put("cartoonState", state);
		if(!StringUtils.isEmpty(py)) params.put("cartoonNamePy", py.toLowerCase());
		//搜索数据
		SearchResult searchResult = searchIndex.cateSearch(params, start, end, f);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : searchResult.getDocs()) {
			JSONObject data = doc2json.parseCate(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		result.put("count", searchResult.getCount());
		return result.toString();
	}

	/**
	 * 根据作者搜索漫画，根据cid排除当前漫画
	 * @param author
	 * @param cid
	 * @param pno
	 * @param psize
	 * @param f
	 * @return
	 */
	public String authorSearch(String author, String cid, int pno, int psize, boolean f) {
		JSONObject result = new JSONObject();
		if(StringUtils.isEmpty(author)) {
			result.put("error", "author is null");
			log.error("author is null");
			return result.toString();
		}
		int start = pno * psize;
		int end = start + psize;
		//去噪
		author = SplitUtil.escapeKeyword(author);
		//分词
		author = SplitCaller.getSplitKeyword(author);
		//搜索数据
		SearchResult sResult = searchIndex.authorSearch(author, cid, start, end, f);
		//封装数据
		JSONArray dataList = new JSONArray();
		for(Document doc : sResult.getDocs()) {
			JSONObject data = doc2json.parseSearch(doc);
			dataList.add(data);
		}
		result.put("dataList", dataList);
		result.put("count", sResult.getCount());
		return result.toString();
	}
	
	/**
	 * 处理空字符串
	 * @param str
	 * @return
	 */
	private String ifNull(String str) {
		return str == null ? "" : str;
	}
}
