package cn.yicha.cartoon.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.yicha.cartoon.service.UploadIndexService;
import cn.yicha.cartoon.util.Config;
import cn.yicha.cartoon.util.FileUtil;
import cn.yicha.cartoon.util.RequestHelper;
import cn.yicha.cartoon.util.ResponseHelper;

/**
 * 上传索引接口
 * @author xrx
 * @since 2012-09-18
 */
public class UploadIndex extends HttpServlet {
	
	private static final long serialVersionUID = -605943184358449264L;
	
	private static final Log log = LogFactory.getLog(UploadIndex.class);
	
	UploadIndexService uploadIndexService = new UploadIndexService();
	
	public void service(HttpServletRequest request, HttpServletResponse response)  
	throws ServletException {
		
		String remoteIp = request.getRemoteHost();
		List<String> acceptIPs = null;
		try {
			acceptIPs = FileUtil.readFileAsList(Config.path + Config.acceptIPPath);
		} catch (IOException e) {
			log.error("读取可用IP失败>>" + e.toString());
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		//判断ip是否符合规则，不符合则返回404
		boolean flag = false;
		if(acceptIPs != null) {
			for(String acceptIP : acceptIPs) {
				if(remoteIp.matches(acceptIP)) {
					flag = true;
					break;
				}
			}
		}
		if(!flag) {
			log.error("IP不符合规则>>" + remoteIp);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		String result = "";
		String back = RequestHelper.getString(request, "back");
		String version = null;
		if(back.equals("1")) {
			
			//获取索引版本号
			result = Config.indexName;
		}
		else if(back.equals("2")) {
			
			//上传索引
			version = request.getParameter("version");
			if(StringUtils.isBlank(version)) {
				result = "error:no version parameter";
			}
			else {
				result = uploadIndexService.uploadIndex(request, version, Config.indexBack, Config.indexPath);
			}
		}
		else if(back.equals("3")) {
			
			//切换索引
			version = request.getParameter("version");
			if(version == null) {
				result = "error:no version parameter";
			}
			else {
				result = uploadIndexService.changeIndex(version);
			}
		}
		else {
			result = "error:back is null or incorrect";
		}
		if(result.startsWith("error")) {
			log.error("result : " + result + " @@ back : " + back + " @@ version : " + version);
		}
		log.info("back:" + back + " version:" + version + " result:" + result + " @@remoteIP : " + remoteIp);
		
		ResponseHelper.writeIndex(response, result);
	}
}