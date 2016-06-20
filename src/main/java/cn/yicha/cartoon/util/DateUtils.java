package cn.yicha.cartoon.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getCurDateStr() {
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
	
	public static String getCurDateStr(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
}
