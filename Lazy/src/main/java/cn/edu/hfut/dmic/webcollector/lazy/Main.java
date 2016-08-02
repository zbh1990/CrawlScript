/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.lazy;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import cn.edu.hfut.dmic.dm.example.HunanTVSOAPCrawler;
import cn.edu.hfut.dmic.webcollector.lazy.job.HunnanJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.HunnanTVJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.IQIYItvJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.LetvJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.LetvdmJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.PPtvJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.QQtvJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.youKuJob;
import cn.edu.hfut.dmic.webcollector.lazy.job.youKuShowJob;

/**
 *
 * @author hu
 */
public class Main {

	public static void crawl(String[] args) throws Exception {

		String confFileName = args[0];
		LazyConfig lazyConfig = new LazyConfig(confFileName);
		LazyCrawler crawler = new LazyCrawler(lazyConfig);
		crawler.start(lazyConfig.getDepth());
	}

	public static void usage() {
		System.err.println("Usage:Lazy ConfigFileName");
	}

	public static void main(String[] args) throws Exception {
		// 首先，必需要取得一个Scheduler的引用
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		// jobs可以在scheduled的sched.start()方法前被调用

		// PPtvJob 将每天0点执行一次
		JobDetail job = newJob(PPtvJob.class).withIdentity("PPtvJob", "group1").build();
		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("0 0 0,1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		Date ft = sched.scheduleJob(job, trigger);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// LetvdmJob 将每天0：15点执行一次
		job = newJob(LetvdmJob.class).withIdentity("LetvdmJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger2", "group1").withSchedule(cronSchedule("0 5 0,1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// LetvJob 将每天0：30点执行一次
		job = newJob(LetvJob.class).withIdentity("LetvJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger3", "group1").withSchedule(cronSchedule("0 10 0,1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// hunantv 将每天0：45点执行一次
		job = newJob(HunnanJob.class).withIdentity("HunnanJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger4", "group1").withSchedule(cronSchedule("0 15 0,1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// Iqiyi 将每天0：45点执行一次
		job = newJob(IQIYItvJob.class).withIdentity("Iqiyi", "group1").build();
		trigger = newTrigger().withIdentity("trigger5", "group1").withSchedule(cronSchedule("0 20 1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// youku 将每天0：45点执行一次
		job = newJob(youKuJob.class).withIdentity("youKuJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger6", "group1").withSchedule(cronSchedule("0 25 1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// youkushow 将每天0：45点执行一次
		job = newJob(youKuShowJob.class).withIdentity("youKuShowJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger7", "group1").withSchedule(cronSchedule("0 30 1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
		
		// qq 将每天1：45点执行一次
		job = newJob(QQtvJob.class).withIdentity("QQtvJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger8", "group1").withSchedule(cronSchedule("0 45 1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
		
		// qq 将每天1：50点执行一次
		job = newJob(HunnanTVJob.class).withIdentity("HunnanTVJob", "group1").build();
		trigger = newTrigger().withIdentity("trigger9", "group1").withSchedule(cronSchedule("0 50 1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,18,20,21,22,23 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// 开始执行，start()方法被调用后，计时器就开始工作，计时调度中允许放入N个Job
		sched.start();
	}
}
