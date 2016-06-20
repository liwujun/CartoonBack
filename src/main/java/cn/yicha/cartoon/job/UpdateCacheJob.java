package cn.yicha.cartoon.job;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import cn.yicha.cartoon.util.Config;
import cn.yicha.net.HTTPVisitor;
import cn.yicha.net.HTTPVisitorFactory;

/**
 * 定时更新漫咖啡排序缓存，使用quarz实现定时任务
 * @author xrx
 * @since 2013-03-07
 */
public class UpdateCacheJob implements Job {
	
	private static final Log LOG = LogFactory.getLog(UpdateCacheJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		//从评论系统获取顶踩排序数据并更新缓存
		HTTPVisitor visitor = HTTPVisitorFactory.getJavaVisitor();
		boolean flag = true;
		for(String cate : Config.MCOFFEE_CATES) {
			String url = Config.MCOFFEE_DING.replace("%cate%", cate);
			String content = visitor.getHtmlUseGet(url).trim();
			List<String> dataList = new ArrayList<String>();
			if(content.startsWith("var datas=")) {
				content = content.replaceFirst("var datas=", "");
				JSONObject joContent = JSONObject.fromObject(content);
				JSONArray ja = joContent.getJSONArray("data");
				for(int i = 0; i < ja.size(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					String uniqueId = jo.getString("uniqueId");
					
					//去掉索引中不存在的数据
					if(Config.allCoffIds.contains(uniqueId)) {
						dataList.add(uniqueId);
					}
				}
				Config.sortCache.put(cate + "_ding", dataList);
			}
			else {
				LOG.error("获取顶数排序错误" + url);
				flag = false;
			}
		}
		
		//从评论系统获取评论数排序数据并更新缓存
		for(String cate : Config.MCOFFEE_CATES) {
			String url = Config.MCOFFEE_COMMENT.replace("%cate%", cate);
			String content = visitor.getHtmlUseGet(url).trim();
			List<String> dataList = new ArrayList<String>();
			if(content.startsWith("var datas=")) {
				content = content.replaceFirst("var datas=", "");
				JSONObject joContent = JSONObject.fromObject(content);
				JSONArray ja = joContent.getJSONArray("data");
				for(int i = 0; i < ja.size(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					String uniqueId = jo.getString("cartoonName");
					
					//去掉索引中不存在的数据
					if(Config.allCoffIds.contains(uniqueId)) {
						dataList.add(uniqueId);
					}
				}
				Config.sortCache.put(cate + "_comment", dataList);
			}
			else {
				LOG.error("获取评论数排序错误" + url);
				flag = false;
			}
		}
		
		LOG.info("更新排序缓存：" + flag);
	}
	
	/** 
	 * 初始化定时更新新消息缓存任务
	 * @return
	 */
	public boolean init() {
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			JobDetail jobDetail = new JobDetail("UpdateCacheJob", "CacheJob", UpdateCacheJob.class);
			CronTrigger trigger = new CronTrigger("UpdateCacheJob", "CacheJob", Config.UPDATE_EXPR);
			scheduler.start();
			scheduler.scheduleJob(jobDetail, trigger);
			execute(null);
			return true;
		} catch (Exception e) {
			LOG.error(e.toString());
			return false;
		}
	}

}
