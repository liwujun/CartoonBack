package cn.yicha.cartoon.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import cn.yicha.cartoon.index.CoffSearchIndex;
import cn.yicha.cartoon.index.SearchIndex;
import cn.yicha.cartoon.job.UpdateCacheJob;
import cn.yicha.cartoon.util.Config;

/**
 * 初始化接口，所有的初始化工作入口
 * @author xrx
 * @since 2012-09-18
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 2343523378090173411L;
	
	private static final Log log = LogFactory.getLog(InitServlet.class);
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		System.out.println("初始化索引:" + SearchIndex.getInstance().init());
		System.out.println("初始化漫咖啡索引:" + CoffSearchIndex.getInstance().init());
		System.out.println("初始化定时任务：" + new UpdateCacheJob().init());
		
		//初始化log4j
		String file = getInitParameter("log4j");
		if(file != null)
		{
			PropertyConfigurator.configure(Config.path + file);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
