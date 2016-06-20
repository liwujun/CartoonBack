package cn.yicha.cartoon.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 往response写数据
 * @author xrx
 * @since 2012-09-18
 */
public class ResponseHelper {
	
	private static final Log log = LogFactory.getLog(ResponseHelper.class);
	private static final String NO_RESULT = "{\"noresult\":\"true\"}";
	
	/**
	 * 写字符串类型的数据
	 * @param response
	 * @param result
	 */
	public static void write(HttpServletResponse response, String result) {
		response.setContentType("text/html");
		if(result == null || result.trim().equals("")) result = NO_RESULT;
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error(e.toString());
		}
		out.println(result);
		out.flush();
		out.close();
	}
	
	/**
	 * 写字符串类型的数据
	 * @param response
	 * @param result
	 */
	public synchronized static void writeIndex(HttpServletResponse response, String result) {
		response.setContentType("text/html");
		if(result == null) result = "";
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error(e.toString());
		}
		out.flush();
		log.error("result:" + result);
		out.println(result);
		out.flush();
		out.close();
	}
}
