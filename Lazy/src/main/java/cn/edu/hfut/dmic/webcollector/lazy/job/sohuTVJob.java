package cn.edu.hfut.dmic.webcollector.lazy.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.edu.hfut.dmic.dm.example.sohuTvCrawler;

/**
 * @brief 继承了Job接口的任务类
 * 
 * @author Administrator
 */
public class sohuTVJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("执行sohuTvCrawler时间：" + new Date());
		try {
			sohuTvCrawler.execute(2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}