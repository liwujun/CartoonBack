package cn.yicha.cartoon.util;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 发送索引（测试用类）
 * @author xrx
 * @since 2012-11-08
 */
public class SendUtil {
	
	private static final Log log = LogFactory.getLog(SendUtil.class);
	
	/**
	 * 发送文件
	 * @param url
	 * @param file
	 * @return
	 */
	public static boolean publishImg(String url, File file) {
		PostMethod filePost = new UTF8PostMethod(url);
		
		Part[] parts = new Part[1];
		FilePart fp = null;
		try {
			fp = new FilePart(file.getName(), file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		parts[0] = fp;
		filePost.setRequestEntity(new MultipartRequestEntity(parts,filePost.getParams()));
		HttpClient client = new HttpClient();
		client.getParams().setParameter(
			      HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		client.getHttpConnectionManager().getParams().setConnectionTimeout(
				5000);
		int status = -1;
		try {
			status = client.executeMethod(filePost);
			log.info(filePost.getResponseBodyAsString());
		} catch (Exception e) {
			log.error(e.toString());
		}
		if (status != HttpStatus.SC_OK) {
			return false;
		}
		return true;
	}
	
	/**
	 * UTF8编码的post方法
	 * @author xrx
	 * @since 2012-09-18
	 */
	public static class UTF8PostMethod extends PostMethod {
		public UTF8PostMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			return "utf-8";
		}
	}
	
	public static void main(String[] args) {
//		String url = "http://192.168.1.211:8080/CartoonBack/uploadindexcoff?back=2&version=coffeeIndex201211081803";
		String url = "http://192.168.1.211:8080/CartoonBack/uploadindex?back=2&version=cartoonIndex201212101012";
		File file = new File("F:/work/index/back/cartoonIndex201212101013.zip");
		publishImg(url, file);
		file = new File("F:/work/index/back/cartoonIndex201212101014.zip");
		publishImg(url, file);
	}
}
