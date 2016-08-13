package cn.edu.hfut.dmic.webcollector.lazy.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.edu.hfut.dmic.dm.example.IQIYITV2Crawler;

/**
 * @brief 继承了Job接口的任务类
 * 
 * @author Administrator
 */
public class IQIYItvJob2 implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			IQIYITV2Crawler.execute(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}