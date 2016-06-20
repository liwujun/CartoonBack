package cn.yicha.cartoon.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 读取配置文件
 * @author xrx
 * @since 2012-09-18
 */
public class Config {
	private static final Log log = LogFactory.getLog(Config.class);

	//项目根目录
	public static String path = Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceAll("%20", " ");
	
	//索引根路径
	public static String indexPath;
	
	//索引文件夹名
	public static String indexName;

	//索引备份路径
	public static String indexBack;	
	
	//CMS数据的路径
	public static String cmsDataPath;
	
	//最大结果数
	public static int maxResult = 300;
	
	//最小结果数
	public static int minResult = 30;
	
	//预读服务器
	public static String[] yuDuhost1;
	public static String[] yuDuhost2;
	
	//数据服务器
	public static String[] cartoonCoverImgG1;
	public static String[] cartoonCoverImgG2;
	private static int g1Index = 0;
	private static int g2Index = 0;
	
	//最近更新数据的缓存
	public static Map<String, String> latestData = new HashMap<String, String>();
	
	//漫咖啡总述数据的缓存
	public static String generalCache = null;
	
	//漫咖啡分类数据缓存
	public static Map<String, String> cateCache = new HashMap<String, String>();
	
	//漫咖啡排序数据
	public static Map<String, List<String>> sortCache = new HashMap<String, List<String>>();
	
	//漫咖啡索引文件
	public static String coffIndexName;

	//前端更新索引ip
	public static String updateIndexIPs;
	
	//允许上传索引的ip
	public static String acceptIPPath;
	
	//定时更新排序缓存，表达式
	public static String UPDATE_EXPR;
	
	//漫咖啡按评论数排序接口
	public static String MCOFFEE_COMMENT;
	
	//漫咖啡按顶排序接口
	public static String MCOFFEE_DING;
	
	//漫咖啡全部分类
	public static String[] MCOFFEE_CATES;
	
	public static Set<String> allCoffIds;
	
	public static String[] CARTOON_CLIENT_HOST;
	
	public static HashSet<String> tortCartoonIds;
	
	static{
		//读取配置文件
		try {
			Properties property = new Properties();
			property.load(new FileInputStream(path + "config.properties"));
			indexPath = property.getProperty("indexPath");
			indexName = property.getProperty("indexName");
			indexBack = property.getProperty("indexBack");
			cmsDataPath = property.getProperty("cmsDataPath");
			//漫画阅读
			yuDuhost1=property.getProperty("cartoon.yuDuhost1").split(",");
			yuDuhost2=property.getProperty("cartoon.yuDuhost2").split(",");
			//漫画封面host
			cartoonCoverImgG1=property.getProperty("cartoon.coverImgHost.G1").split(",");
			cartoonCoverImgG2=property.getProperty("cartoon.coverImgHost.G2").split(",");
//			/cartoonCoverImg2=property.getProperty("cartoon.coverImgHost").split(",");
			
			coffIndexName = property.getProperty("coffIndexName");
			updateIndexIPs = property.getProperty("updateIndexIPs");
			acceptIPPath = property.getProperty("acceptIPPath");
			
			UPDATE_EXPR = property.getProperty("UPDATE_EXPR");
			MCOFFEE_COMMENT = property.getProperty("MCOFFEE_COMMENT");
			MCOFFEE_DING = property.getProperty("MCOFFEE_DING");
			MCOFFEE_CATES = property.getProperty("MCOFFEE_CATES").split(",");
			CARTOON_CLIENT_HOST = property.getProperty("CARTOON_CLIENT_HOST").split(",");
			
			String tortPath = path.replace("WEB-INF/classes/", "") + Config.cmsDataPath + "tortCartoonIds.txt";
			Config.initTortCartoonIds(tortPath);
		} catch (Exception e) {
			log.error("读取配置文件出错" + e.getMessage());
		}
	}
	
	/**
	 * 加载侵权漫画列表
	 * @return
	 * @date:2013-7-1
	 * @author:gudaihui
	 */
	public static String initTortCartoonIds(String tortPath){
		String result = "success";
		tortCartoonIds = new HashSet<String>();
		List<String> list;
		try {
			list = FileUtil.readFileAsList(tortPath);
			for(String line : list){
				if(line.matches("\\w{16}")){
					tortCartoonIds.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = "failure";
		}
		return result;
	}

	private static void changeField(String key, String value) throws FileNotFoundException, IOException {
		Properties property=new Properties();
		property.load(new FileInputStream(path + "config.properties"));
		property.setProperty(key, value);
		FileOutputStream out = new FileOutputStream(path + "config.properties");
		property.store(out, null);
		out.flush();
		out.close();
	}

	public static void changeIndexName(String indexName) throws FileNotFoundException, IOException {
		changeField("indexName", indexName);
		Config.indexName = indexName;
	}
	
	public static void changeCoffIndexName(String coffIndexName) throws FileNotFoundException, IOException {
		changeField("coffIndexName", coffIndexName);
		Config.coffIndexName = coffIndexName;
	}
	
	public static String getBaseYuduHost(String key, String groupId){
		if(groupId.equals("g1")) {
			g1Index = ++g1Index % yuDuhost1.length;
			return yuDuhost1[g1Index];
		}
		else {
			g2Index = ++g2Index % yuDuhost2.length;
			return yuDuhost2[g2Index];
		}
	}
	
	public static String getCoverImgHost(String key, String groupId){
		if(groupId.equals("g1")) {
			g1Index = ++g1Index % cartoonCoverImgG1.length;
			return cartoonCoverImgG1[g1Index];
		}
		else {
			g2Index = ++g2Index % cartoonCoverImgG2.length;
			return cartoonCoverImgG2[g2Index];
		}
	}
	
	public static String getCartoonClientHost(String key){
		if(StringUtils.isBlank(key)) {
			return "";
		}
		else {
			return CARTOON_CLIENT_HOST[Math.abs(key.hashCode()%CARTOON_CLIENT_HOST.length)] + key.trim();
		}
	}
	
	/*public static String getCoverImgHost(String key, String state){
		if(state.equals("a")) {
			return cartoonCoverImg[Math.abs(key.hashCode()%cartoonCoverImg.length)];
		}
		else if(state.equals("b")){
			return cartoonCoverImg2[Math.abs(key.hashCode()%cartoonCoverImg.length)];
		}
	}*/
}
