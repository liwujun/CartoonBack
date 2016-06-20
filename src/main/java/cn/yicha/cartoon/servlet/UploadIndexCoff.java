package cn.yicha.cartoon.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.yicha.cartoon.service.UploadIndexCoffService;
import cn.yicha.cartoon.util.Config;
import cn.yicha.cartoon.util.FileUtil;
import cn.yicha.cartoon.util.RequestHelper;
import cn.yicha.cartoon.util.ResponseHelper;

/**
 * 漫咖啡索引上传接口
 * @author xrx
 * @since 2012-11-08
 */
public class UploadIndexCoff extends HttpServlet {
	private static final long serialVersionUID = -1338841230048944226L;
	
	private static final Log log = LogFactory.getLog(UploadIndexCoff.class);
	
	UploadIndexCoffService uploadIndexCoffService = new UploadIndexCoffService();
	
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
		
		//获取索引版本号
		if(back.equals("1")) {
			result = Config.coffIndexName;
		}
		
		//上传索引
		else if(back.equals("2")) {
			version = request.getParameter("version");
			if(version == null) {
				result = "error:no version parameter";
			}
			else {
				result = uploadIndexCoffService.uploadIndex(request, version, Config.indexBack, Config.indexPath);
			}
		}
		
		//切换索引
		else if(back.equals("3")) {
			version = request.getParameter("version");
			if(StringUtils.isBlank(version)) {
				result = "error:no version parameter";
			}
			else {
				result = uploadIndexCoffService.changeIndex(version);
			}
		}
		else {
			result = "error:back is null or incorrect";
		}
		if(result.startsWith("error")) {
			log.error("result : " + result + " @@ back : " + back + " @@ version : " + version);
		}
		log.info("back:" + back + " version:" + version + result);
		
		ResponseHelper.writeIndex(response, result);
	}
}
