package cn.yicha.cartoon.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.yicha.cartoon.service.SearchCoffService;
import cn.yicha.cartoon.util.RequestHelper;
import cn.yicha.cartoon.util.ResponseHelper;

/**
 * 漫咖啡搜索，包含接口：
 * 1.获取总述（type=general，包含今日更新总数和每个分类下的最新更新时间）
 * http://122.49.34.20:18041/CartoonBack/searchcoff?type=general
 * 2.获取某个分类下的分页数据（type=cate，按更新时间倒序排序）
 * http://122.49.34.20:18041/CartoonBack/searchcoff?type=cate&cate=&pno=1&psize=10
 * @author xrx
 * @since 2012-11-08
 */
public class SearchCoff extends HttpServlet {
	private static final long serialVersionUID = -130968168390268491L;
	
	private static final Log log = LogFactory.getLog(Search.class);
	
	SearchCoffService searchCoffService = new SearchCoffService();
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = RequestHelper.getString(request, "type");
		String result = null;
		
		//获取首页数据
		//http://122.49.34.20:18041/CartoonBack/searchcoff?type=general
		if("general".equals(type)) {
			result = searchCoffService.getGeneral();
		}
		
		//搜索分类
		//http://122.49.34.20:18041/CartoonBack/searchcoff?type=cate&cate=色系军团
		//按创建时间排序（默认）：sort=time
		//按顶的数量排序：sort=ding
		//按评论数量排序：sort=comment
		else if("cate".equals(type)) {
			String cate = RequestHelper.getString(request, "cate", "utf-8");
			int pno = RequestHelper.getInt(request, "pno", 0);
			int psize = RequestHelper.getInt(request, "psize", 10);
			String sort = RequestHelper.getString(request, "sort");
			result = searchCoffService.getCate(cate, sort, pno, psize);
		}
		else {
			result = "{\"error\":\"type is null or incorrect.\"}";
			log.error(result + "  type:" + type);
		}
		
		//输出结果
		ResponseHelper.write(response, result);
	}
}