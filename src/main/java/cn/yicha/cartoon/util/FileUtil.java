package cn.yicha.cartoon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * 读写文件工具类
 * @author xrx
 * @since 2012-11-13
 */
public class FileUtil {

	/**
	 * 使用指定的分隔符，读取文件到list中
	 * @param path	文件路径
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFileAsList(String path) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8"));
		String line = "";
		while((line = reader.readLine()) != null){
			if(StringUtils.isNotBlank(line)){
				list.add(line);
			}
		}
		return list;
	}
}
