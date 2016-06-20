package cn.yicha.cartoon.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.yicha.cartoon.index.SearchIndex;
import cn.yicha.cartoon.util.Config;
import cn.yicha.cartoon.util.ZipUtil;

/**
 * 搜索业务层
 * @author xrx
 * @since 2012-09-18
 */
public class UploadIndexService {
	SearchIndex searchIndex = SearchIndex.getInstance();
	
	private static final Log log = LogFactory.getLog(UploadIndexService.class);
	
	/**
	 * 上传索引
	 * @param request
	 * @param version
	 * @param indexBack
	 * @param indexPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String uploadIndex(HttpServletRequest request, String version, String indexBack, String indexPath) {
		String result = "success";
		String filename = "";
		//获取上传文件
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> items = upload.parseRequest(request);
			for(FileItem item: items){
				if (!item.isFormField()) {		
					File f = new File(indexBack + "/" + item.getName());
					item.write(f);
					filename = item.getName();
				}
			}
			//解压文件
			ZipUtil.unzip(indexBack + "/" + filename, indexPath + "/" + filename.substring(0, filename.indexOf('.')) + "/");
			//更新索引
			result = changeIndex(filename.substring(0, filename.indexOf('.')));
		}
		catch (Exception e) {
			//使用指定的旧版本索引
			result = "error:change index. result:" + changeIndex(version);
			log.error(e.toString());
			return result;
		}
		if(result.startsWith("error:")) {
			result = "error:change index. result:" + changeIndex(version);
			log.error(result);
		}
		return result;
	}
	
	/**
	 * 切换索引
	 * @param version
	 * @return
	 */
	public String changeIndex(String version) {
		String result = "success";
		//如果version版本索引可用，则替换
		if(searchIndex.changeSearcher(version)) {
			try {
				Config.changeIndexName(version);
				
				Config.latestData.clear();
				
				//通知前端索引已更新
				result = noteFront(Config.updateIndexIPs);
			} catch (IOException e) {
				result = "error: change index name error";
				log.error(e.toString());
				log.error(result);
				return result;
			}
		}
		else {
			result = "error: index unavailable";
		}
		if(result.startsWith("error:")) {
			log.error(result);
		}
		return result;
	}
	
	/**
	 * 通知前台已更新索引
	 * @param updateIndexIPs
	 * @return
	 */
	private String noteFront(String updateIndexIPs) {
		String result = "";
		for(String url : updateIndexIPs.split(",")) {
			HttpClient client = new HttpClient();
			PostMethod httpMethod = new PostMethod(url);
			try {
				client.executeMethod(httpMethod);
				byte[] responseBody = null;
				responseBody = httpMethod.getResponseBody();
				String response = new String(responseBody);
				if (!response.contains("success")) {
					result += "error:note index failed" + url;
				}
			} catch (IOException e) {
				result += "error:note index failed" + url;
				e.printStackTrace();
			}
		}
		if(result.equals("")) result = "success";
		if(result.startsWith("error:")) {
			log.error(result);
		}
		return result;
	}
	
	public static void main(String[] args) {
		String updateIndexIPs = "http://122.49.34.20:18041/manhua/refresh.html,http://122.49.34.20:18042/manhua/refresh.html,http://122.49.34.20:18041/qqpush/InitLatestServlet.do,http://122.49.34.20:18042/qqpush/InitLatestServlet.do";
		System.out.println(new UploadIndexService().noteFront(updateIndexIPs));
	}
}
