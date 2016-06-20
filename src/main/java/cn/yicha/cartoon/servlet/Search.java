package cn.yicha.cartoon.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.yicha.cartoon.service.SearchService;
import cn.yicha.cartoon.util.RequestHelper;
import cn.yicha.cartoon.util.ResponseHelper;

/**
 * 搜索接口
 * @author xrx
 * @since 2012-09-18
 */
public class Search extends HttpServlet {

	private static final long serialVersionUID = 4155937350850494036L;
	
	private static final Log log = LogFactory.getLog(Search.class);
	
	SearchService searchService = new SearchService();
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = RequestHelper.getString(request, "type");
		String result = null;
		if("key".equals(type)) {
			
			//搜索关键字
			//http://122.49.34.20:18041/CartoonBack/search?type=key&key=火影忍者&area=内地&py=h&state=连载&cate=爆笑喜剧
			String key = RequestHelper.getString(request, "key", "utf-8");
			String cate = RequestHelper.getString(request, "cate", "utf-8");
			String area = RequestHelper.getString(request, "area", "utf-8");
			String state = RequestHelper.getString(request, "state", "utf-8");
			String py = RequestHelper.getString(request, "py");
			int pno = RequestHelper.getInt(request, "pno", 0);
			int psize = RequestHelper.getInt(request, "psize", 10);
			// f是否过滤， f=exce 表示filter=exception不过滤侵权的漫画，否则都会过滤
			boolean f = !RequestHelper.getString(request, "f", "utf-8").equals("exce");
			result = searchService.keySearch(key, cate, area, state, py, pno, psize, f);
			log.info("type:" + type + " key:" + key + " pno:" + pno + " psize:" + psize + " result:"+ result);
		}
		else if("cate".equals(type)) {
			
			//搜索分类
			//http://122.49.34.20:18041/CartoonBack/search?type=cate&area=内地&py=h&state=连载&cate=爆笑喜剧
			String cate = RequestHelper.getString(request, "cate", "utf-8");
			String area = RequestHelper.getString(request, "area", "utf-8");
			String state = RequestHelper.getString(request, "state", "utf-8");
			String py = RequestHelper.getString(request, "py");
			int pno = RequestHelper.getInt(request, "pno", 0);
			int psize = RequestHelper.getInt(request, "psize", 10);
			// f是否过滤， f=exce 表示filter=exception不过滤侵权的漫画，否则都会过滤
			boolean f = !RequestHelper.getString(request, "f", "utf-8").equals("exce");
			result = searchService.cateSearch(cate, area, state, py, pno, psize, f);
			log.info("type:" + type + " cate:" + cate + " area:" + area + " state:" + state + " py:" + py + " pno:" + pno + " psize:" + psize + " result:"+ result);
		}
		else if("detail".equals(type)) {
			
			//搜索漫画的详细数据
			//http://122.49.34.20:18041/CartoonBack/search?type=detail&id=b8a329cd3779d1c7
			String id = RequestHelper.getString(request, "id");
			result = searchService.detailSearch(id);
			log.info("type:" + type + " id:" + id + " result:"+ result);
		}
		else if("relatewords".equals(type)) {
			
			//搜索漫画相关词 暂不使用...
			//http://122.49.34.20:18041/CartoonBack/search?type=relatewords&key=火影&pno=0&psize=5
			String key = RequestHelper.getString(request, "key", "utf-8");
			int pno = RequestHelper.getInt(request, "pno", 0);
			int psize = RequestHelper.getInt(request, "psize", 10);
			result = searchService.relateWordsSearch(key, pno, psize);
			log.info("type:" + type + " key:" + key + " pno:" + pno + " psize:" + psize + " result:"+ result);
		}
//		else if("content".equals(type)) {
//			
//			//阅读漫画,不使用，用detail接口代替
//			String id = RequestHelper.getString(request, "id");
//			result = searchService.contentSearch(id);
//			log.info("type:" + type + " id:" + id + " result:"+ result);
//		}
		else if("latestdata".equals(type)) {
			
			//获取最近更新
			//http://122.49.34.20:18041/CartoonBack/search?type=latestdata&pno=1&psize=10&area=内地&py=h&state=连载&cate=爆笑喜剧
			String cate = RequestHelper.getString(request, "cate", "utf-8");
			String area = RequestHelper.getString(request, "area", "utf-8");
			String state = RequestHelper.getString(request, "state", "utf-8");
			String py = RequestHelper.getString(request, "py");
			int pno = RequestHelper.getInt(request, "pno", 0);
			int psize = RequestHelper.getInt(request, "psize", 10);
			// f是否过滤， f=exce 表示filter=exception不过滤侵权的漫画，否则都会过滤
			boolean f = !RequestHelper.getString(request, "f", "utf-8").equals("exce");
			result = searchService.getLatestData(cate, area, state, py, pno, psize, f);
		}
		else if("authorsearch".equals(type)) {
			
			//搜索作者的其他作品，暂不使用
			//http://122.49.34.20:18041/CartoonBack/search?type=authorsearch&author=司徒剑桥&pno=1&psize=10&cid=c56aec2b06c720cc
			String author = RequestHelper.getString(request, "author", "utf-8");
			String cid = RequestHelper.getString(request, "cid");
			int pno = RequestHelper.getInt(request, "pno", 0);
			int psize = RequestHelper.getInt(request, "psize", 10);
			// f是否过滤， f=exce 表示filter=exception不过滤侵权的漫画，否则都会过滤
			boolean f = !RequestHelper.getString(request, "f", "utf-8").equals("exce");
			result = searchService.authorSearch(author, cid, pno, psize, f);
			log.info("type:" + type + " author:" + author + " pno:" + pno + " psize:" + psize + " result:"+ result);
		}
		else {
			result = "{\"error\":\"type is null or incorrect.\"}";
			log.error(result + "  type:" + type);
		}
		
		//输出结果
		ResponseHelper.write(response, result);
	}

}
