package com.eastcom_sw.sml.test;
import com.eastom_sw.sml.support.queue.ManagedQuene;
import com.eastom_sw.sml.support.queue.Task;


public class TaskDemo {
	public static void main(String[] args) {
		ManagedQuene mq=new ManagedQuene();
		mq.setConsumerThreadSize(3);//处理线程任务数
		mq.setTimeout(10);//超时s
		mq.init();
		for(int i=0;i<30;i++){
			mq.add(new Task1());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	 static int count=0;
	 static class Task1 implements Task{
		public synchronized void execute() throws Exception {
			int i=count++;
			System.out.println(i);
			Thread.sleep(i*1000);
		}
		
	}
}
