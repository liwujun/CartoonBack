package cn.yicha.cartoon.util;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 获取HttpServletRequest中相应信息
 * @author xrx
 * @since 2012-09-18
 */
public class RequestHelper {
	
	private static final Log log = LogFactory.getLog(RequestHelper.class);
	
	/**
	 * 获取request中的int类型参数,如果不存在或错误返回default参数
	 * @param request
	 * @param paraname
	 * @param defaultVal
	 * @return
	 */
	public static int getInt(HttpServletRequest request, String paraname, int defaultVal) {
		try {
			return Integer.parseInt(request.getParameter(paraname));
		} catch (Exception e) {
			log.error(e.toString());
			return defaultVal;
		}
	}
	
	/**
	 * 获取request中的double类型参数,如果不存在或错误返回default参数
	 * @param request  
	 * @param paraname 
	 * @param defaultVal
	 * @return
	 */
	public static double getDouble(HttpServletRequest request,String paraname,double defaultVal){
		try{
			return Double.parseDouble(request.getParameter(paraname));
		}catch(Exception e){
			log.error(e.toString());
			return defaultVal;
		}
	}
	
	/**
	 * 获取request中的long类型参数,如果不存在或错误返回default参数
	 * @param request
	 * @param paraname
	 * @param defaultVal
	 * @return
	 */
	public static long getLong(HttpServletRequest request, String paraname,int defaultVal) {
		try {
			return Long.parseLong(request.getParameter(paraname));
		} catch (Exception e) {
			log.error(e.toString());
			return defaultVal;
		}
	}

	/**
	 * 获取request中的字符串类
	 * @param request
	 * @param paraname
	 * @return
	 */
	public static String getString(HttpServletRequest request, String paraname) {
		String result = request.getParameter(paraname) == null ? "" : request.getParameter(paraname).trim();
		return result;
	}

	/**
	 * 获取request中的字符串类，带转码
	 * @param request
	 * @param paraname
	 * @return
	 */
	public static String getString(HttpServletRequest request, String paraname ,String charSet) {
		String result = request.getParameter(paraname) == null ? "" : request.getParameter(paraname);
		try {
			result = new String(result.getBytes("iso-8859-1"),charSet);
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取request中的短整型
	 * @param request
	 * @param paraname
	 * @param defaultVal
	 * @return
	 */
	public static short getShort(HttpServletRequest request, String paraname,short defaultVal) {
		try {
			return Short.parseShort(request.getParameter(paraname));
		} catch (Exception e) {
			log.error(e.toString());
			return defaultVal;
		}
	}

}
