package cn.yicha.cartoon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;

import cn.yicha.cartoon.util.Config;
import net.sf.json.JSONObject;

/**
 * 将document数据组装成json数据
 * @author xrx
 * @since 2012-09-19
 */
public class Doc2JSON {
	
	/**
	 * 组装关键词搜索结果
	 * @param doc
	 * @return
	 */
	public JSONObject parseSearch(Document doc) {
		JSONObject json = new JSONObject();
		json.put("cartoonId", doc.get("cartoonId"));//ID，唯一标识
		json.put("cartoonName", doc.get("cartoonName"));//名称
		json.put("cartoonAuthor", doc.get("cartoonAuthor"));//作者
		json.put("cartoonCate", doc.get("cartoonCate"));//分类
		json.put("cartoonArea", doc.get("cartoonArea"));//区域：大陆，香港等
		json.put("cartoonState", doc.get("cartoonState"));//状态：全本，连载
		json.put("cartoonPopular", doc.get("cartoonPopular"));//人气
		json.put("cartoonUpdateTime", doc.get("cartoonUpdateTime"));//更新时间
		json.put("cartoonCoverImg",Config.getCoverImgHost(doc.get("cartoonName"), doc.get("groupId"))+doc.get("cartoonCoverImg"));//封面
		//json.put("cartoonLocal", Config.getBaseYuduHost(doc.get("cartoonName"))+doc.get("cartoonLocal"));//图片存储地址前缀
		json.put("cartoonNowChapNum", doc.get("cartoonNowChapNum"));//已有章节数
		//json.put("cartoonAllChapNum", doc.get("cartoonAllChapNum"));//总章节数
		json.put("cartoonNowFchapNum", doc.get("cartoonNowFChapNum"));//番外篇数量
//		json.put("cartoonNowImgNum", doc.get("cartoonNowImgNum"));//已有图片数
//		json.put("cartoonAllImgNum", doc.get("cartoonAllImgNum"));//总图片数
		json.put("newestChapName", doc.get("newestChapName"));//最新章节名
		json.put("cartoonStar", doc.get("cartoonStar"));//评分
		json.put("cartoonDesc", doc.get("cartoonDesc"));//描述
		return json;
	}
	
	/**
	 * 组装分类搜索结果
	 * @param doc
	 * @return
	 */
	public JSONObject parseCate(Document doc) {
		JSONObject json = new JSONObject();
		json.put("cartoonId", doc.get("cartoonId"));//ID，唯一标识
		json.put("cartoonName", doc.get("cartoonName"));//名称
		json.put("cartoonAuthor", doc.get("cartoonAuthor"));//作者
		json.put("cartoonCate", doc.get("cartoonCate"));//分类
		json.put("cartoonArea", doc.get("cartoonArea"));//区域：大陆，香港等
		json.put("cartoonState", doc.get("cartoonState"));//状态：全本，连载
		json.put("cartoonPopular", doc.get("cartoonPopular"));//人气
		json.put("cartoonCoverImg",Config.getCoverImgHost(doc.get("cartoonName"), doc.get("groupId"))+doc.get("cartoonCoverImg"));//封面
		json.put("cartoonNowChapNum", doc.get("cartoonNowChapNum"));//已有章节数
		json.put("cartoonNowFchapNum", doc.get("cartoonNowFChapNum"));//番外篇数量
		json.put("cartoonDesc", doc.get("cartoonDesc"));//描述
		json.put("cartoonStar", doc.get("cartoonStar"));//评分
		json.put("newestChapName", doc.get("newestChapName"));//最新章节名
		return json;
	}
	
	/**
	 * 组装详细搜索结果
	 * @param doc
	 * @return
	 */
	public JSONObject parseDetail(Document doc) {
		JSONObject json = new JSONObject();
		json = parseSearch(doc);
		json.put("cartoonDesc", doc.get("cartoonDesc"));//描述
		json.put("cartoonLocal", Config.getBaseYuduHost(doc.get("cartoonName"), doc.get("groupId"))+doc.get("cartoonLocal"));//图片存储地址前缀
		String str = doc.get("chapters");
		String[] charpters = str.split("\\$#\\$"),fields;
		List<Map<String,String>> list = new ArrayList<Map<String, String>>();
		List<Map<String,String>> fanlist = new ArrayList<Map<String, String>>();
		Map<String,String> map = null;
		for(String chapter:charpters){
		    map = new HashMap<String, String>();
		    fields = chapter.split("@#@");
		    map.put("n", fields[0]);
		    map.put("p", fields[2]);
		    map.put("c", fields[3]);
		    map.put("s", fields[4]);
		    if(Boolean.parseBoolean(fields[1])){
		    	fanlist.add(map);
		    }else{
		    	list.add(map);
		    }
		}
		json.put("chapters",list);//所有章节信息
		json.put("fchapters",fanlist);//所有番外篇章节信息
		json.put("updateTime", doc.get("updateTime"));//更新时间
		json.put("clientLocal", Config.getCartoonClientHost(doc.get("clientLocal")));
		json.put("clientVer", doc.get("clientVer"));
		return json;
	}
	
	/**
	 * 组装内容数据
	 * @param doc
	 * @return
	 */
	public JSONObject parseContent(Document doc) {
		JSONObject json = new JSONObject();
		//@#@ $#$
		//TODO name:1卷,hasR:false,imgC:196
		StringBuffer sb=new StringBuffer();
		String str=doc.get("chapters");
		String[] charpters=str.split("\\$#\\$"),fields;
		sb.append("[");
		for(String chapter:charpters){
		    fields=chapter.split("@#@");
		    sb.append("{").append("n :"+fields[0]).append(",fan:"+fields[1]).append(",c:"+fields[2]+"},");
		}
		json.put("chapters",sb.substring(0, sb.length())+"]");//所有章节信息
		return json;
	}
	
	/**
	 * 组装相关词数据
	 * @param doc
	 * @return
	 */
	public JSONObject parseRelateWords(Document doc) {
		JSONObject json = new JSONObject();
		json.put("cartoonName", doc.get("cartoonName"));
		return json;
	}

	/**
	 * 组装最近更新数据
	 * @param doc
	 * @return
	 */
	public JSONObject parseLatestData(Document doc) {
		JSONObject json = new JSONObject();
		json.put("cartoonId", doc.get("cartoonId"));//ID，唯一标识
		json.put("cartoonName", doc.get("cartoonName"));//名称
		json.put("cartoonAuthor", doc.get("cartoonAuthor"));//作者
		json.put("cartoonCate", doc.get("cartoonCate"));//分类
		json.put("cartoonArea", doc.get("cartoonArea"));//区域：大陆，香港等
		json.put("cartoonState", doc.get("cartoonState"));//状态：全本，连载
		json.put("cartoonPopular", doc.get("cartoonPopular"));//人气
		json.put("cartoonCoverImg",Config.getCoverImgHost(doc.get("cartoonName"), doc.get("groupId"))+doc.get("cartoonCoverImg"));//封面
		json.put("cartoonNowChapNum", doc.get("cartoonNowChapNum"));//已有章节数
		json.put("cartoonNowFchapNum", doc.get("cartoonNowFChapNum"));//番外篇数量
		json.put("cartoonDesc", doc.get("cartoonDesc"));//描述
		json.put("cartoonUpdateTime", doc.get("cartoonUpdateTime"));//更新时间
		json.put("cartoonStar", doc.get("cartoonStar"));//评分
		json.put("newestChapName", doc.get("newestChapName"));//最新章节名
//		json.put("createTime", doc.get("createTime"));//创建时间
		json.put("updateTime", doc.get("updateTime"));//更新时间
		return json;
	}
	
	/**
	 * 组装漫咖啡分类数据
	 * @param doc
	 * @return
	 */
	public JSONObject parseCoffCateData(Document doc) {
		JSONObject json = new JSONObject();
		//作者
		json.put("coffAuthor", doc.get("coffAuthor"));
		//分类
		json.put("coffCate", doc.get("coffCate"));
		//描述
		json.put("coffDesc", doc.get("coffDesc"));
		//Id
		json.put("coffId", doc.get("coffId"));
		//名称
		json.put("coffName", doc.get("coffName"));
		//来源
		json.put("coffSourceName", doc.get("coffSourceName"));
		//更新时间
		json.put("coffUpdateTime", doc.get("coffUpdateTime"));
		//源地址
//		json.put("coffUrl", doc.get("coffUrl"));
		//创建时间
		json.put("createTime", doc.get("createTime"));
		//图片根目录
		json.put("imgLocal", Config.getCoverImgHost(doc.get("coffName"), doc.get("groupId"))+doc.get("imgLocal"));
		//图片数量
		json.put("imgNum", doc.get("imgNum"));
		return json;
	}
}
