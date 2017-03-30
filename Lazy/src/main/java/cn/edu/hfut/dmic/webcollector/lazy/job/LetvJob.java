package cn.edu.hfut.dmic.webcollector.lazy.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.edu.hfut.dmic.dm.example.DBUtil;
import cn.edu.hfut.dmic.dm.example.LetvCrawler;
import cn.edu.hfut.dmic.dm.example.LetvDMCrawler;
import cn.edu.hfut.dmic.dm.example.domain.Vodinfo;

/**
 * @brief 继承了Job接口的任务类
 * 
 * @author Administrator
 */
public class LetvJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("执行LetvJob时间：" + new Date());
		try {
			LetvCrawler.execute(2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}