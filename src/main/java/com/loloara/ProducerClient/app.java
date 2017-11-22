package com.loloara.ProducerClient;

import java.text.ParseException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class app {	
	private SchedulerFactory schedFact;
	private Scheduler sched;
	
	public app() {	//스케줄러
		try {
		   // 스케쥴 생성후 시작
		   schedFact = new StdSchedulerFactory();
		   sched = schedFact.getScheduler();
		   sched.start();
		   
		   JobDetail job = new JobDetail("job", "group", ProducerClient.class);
		   // Job 생성 (Parameter : 1.Job Name, 2.Job Group Name, 3.Job Class)
		   
		   JobDataMap jobDataMap = job.getJobDataMap(); //job은 실행마다 생성되고 실행이 끝나면 삭제되기 때문에
		   jobDataMap.put("sinceId", 0L);	//이 작업을 해야 맴버 변수처럼 관리할 수 있다.
		   
		   CronTrigger trigger = new CronTrigger("trigger", "group", "0 0/1 * * * ?");		//매 분마다 Tweets 요청
		   // Trigger 생성 (Parameter : 1.Trigger Name, 2.Trigger Group Name, 3.Cron Expression)
		   
		   trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
		   // trigger 가 misfire 되었을 경우에 대한 처리를 설정한다.
		   
		   sched.scheduleJob(job, trigger);
		   
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("I'm PRODUCER");		//jar 파일이 잘 실행 됬는지 확인
		new app();
	}
}
