package cn.yicha.cartoon.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cn.yicha.cartoon.entity.SearchResult;
import cn.yicha.cartoon.index.CoffSearchIndex;
import cn.yicha.cartoon.util.Config;

/**
 * 漫咖啡搜索业务层
 * @author xrx
 * @since 2012-11-08
 */
public class SearchCoffService {
	private static final Log log = LogFactory.getLog(SearchCoffService.class);
	
	CoffSearchIndex coffSearchIndex = CoffSearchIndex.getInstance();

	Doc2JSON doc2json = new Doc2JSON();
	
	private static Map<String, String> cateMap = new HashMap<String, String>();
	
	static {
		cateMap.put("色系军团", "MCoffee_sxjt");
		cateMap.put("内涵漫画", "MCoffee_nhmh");
		cateMap.put("暴走漫画", "MCoffee_bzmh");
		cateMap.put("开心一刻", "MCoffee_kxyk");
	}
	
	/**
	 * 获取首页总述，包含总更新数和每个分类的最近更新时间
	 * 直接从缓存数据中获取
	 * @return
	 */
	public String getGeneral() {
		return Config.generalCache;
	}
	
	/**
	 * 搜索分类
	 * 先从缓存获取
	 * 没有，则从索引中搜索，然后添加到缓存
	 * @param cate
	 * @param sort
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String getCate(String cate, String sort, int pno, int psize) {
		if(sort == null || sort.trim() == "" || sort.equals("time")) {
			return getCate(cate, pno, psize);
		}
		else {
			Calendar cal = Calendar.getInstance();
			long startTime = cal.getTimeInMillis();
			cate = cateMap.get(cate);
			if(cate == null) {
				return null;
			}
			
			List<String> coffIds = Config.sortCache.get(cate + "_" + sort);
			if(coffIds == null) {
				return null;
			}
			
			int start = pno * psize;
			int end = start + psize;
			if(start >= coffIds.size()) {
				return null;
			}
			if(end > coffIds.size()) {
				end = coffIds.size();
			}
			
			//获取当前页id列表
			List<String> thisCoffIds = coffIds.subList(start, end);
			
			JSONObject json = new JSONObject();
			JSONArray dataList = new JSONArray();
			for(String coffId : thisCoffIds) {
				SearchResult searchResult = coffSearchIndex.detailSearch(coffId);
				if(searchResult.getCount() > 0) {
					Document doc = searchResult.getDocs().get(0);
					JSONObject cateJson = doc2json.parseCoffCateData(doc);
					dataList.add(cateJson);
				}
				else {
					coffIds.remove(coffIds.indexOf(coffId));
					return getCate(cate, sort, pno, psize);
				}
			}
			json.put("dataList", dataList);
			json.put("count", coffIds.size());
			cal = Calendar.getInstance();
			long costTime = cal.getTimeInMillis() - startTime;
			System.out.println("search cate cost:" + costTime + "ms");
			//补全 漫咖啡信息
			return json.toString();
		}
	}
	
	
	/**
	 * 搜索分类
	 * 先从缓存获取
	 * 没有，则从索引中搜索，然后添加到缓存
	 * @param cate
	 * @param pno
	 * @param psize
	 * @return
	 */
	public String getCate(String cate, int pno, int psize) {
		final String key = cate + "#" + pno + "#" + psize;
		
		//从缓存获取结果
		String result = Config.cateCache.get(key);
		if(result != null) {
			return result;
		}
		
		//从索引获取结果
		else {
			int start = pno * psize;
			int end = start + psize;
			SearchResult searchResult = coffSearchIndex.searchLatest(cate, start, end);
			JSONObject json = new JSONObject();
			JSONArray dataList = new JSONArray();
			for(Document doc : searchResult.getDocs()) {
				JSONObject cateJson = doc2json.parseCoffCateData(doc);
				dataList.add(cateJson);
			}
			json.put("dataList", dataList);
			json.put("count", searchResult.getCount());
			final String finalResult = json.toString();
			
			//将结果加入缓存
			new Thread(new Runnable() {
				public void run() {
					Config.cateCache.put(key, finalResult);
					log.info("add to catecache(" + key + "," + finalResult + ")");
				}
			}).start();
			
			return finalResult;
		}
	}
	
	public static void main(String[] args) {
		SearchCoffService service = new SearchCoffService();
		for(int i = 0; i < 10; i++) {
			service.getCate("内涵漫画", "", 0, 10);
		}
	}
}
