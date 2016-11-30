package com.eastcom_sw.sml.support.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eastcom_sw.sml.support.ManagedThread;
/**
 * quene managed
 * @author wen
 *
 */
public class ManagedQuene {
	public Logger logger=LoggerFactory.getLogger(getClass());
	/**
	 * 队列管理名称
	 */
	private String manageName;
	/**
	 * 队列深度
	 */
	private int depth=10000;
	
	/**
	 * 消费者数量
	 */
	private int consumerThreadSize=1;
	
	/**
	 * 线程名称
	 */
	private String threadNamePre;
	/**
	 * 队列名称
	 */
	private BlockingQueue<Task> queue;
	
	private String errorMsg; 
	
	private boolean stop=false;
	
	private List<Execute> executes=new ArrayList<Execute>();
	
	private int timeout;
	
	private boolean ignoreLog=true;
	
	
	public  void init(){
		if(queue==null){
			queue=new ArrayBlockingQueue<Task>(depth);
			logger.info("manageName [{}] has init depth {} !",getManageName(),depth);
		}
		for(int i=1;i<=consumerThreadSize;i++){
			Execute execute=new Execute();
			execute.setName(getThreadNamePre()+"-"+i);
			executes.add(execute);
			execute.start();
		}
	}
	
	public void destroy(){
		this.stop=true;
		for(Execute execute:executes){
			execute.shutdown();
		}
		executes.clear();
	}
	
	public void add(Task task){
		queue.add(task);
		if(!ignoreLog)
			logger.info("add {} total-{},current-{}.",getManageName(),getDepth(),queue.size());
			
	}
	
	private class Execute extends ManagedThread{
		protected boolean prepare() {
			return queue!=null;
		}
		protected void doWorkProcess() {
			Task task=null;
			ExecutorService exec=null;
			Future<Integer> future=null;
			try {
				task=queue.take();
				final Task t=task;
				if(!ignoreLog)
				logger.info("{} total-{},current-{}.",getManageName(),getDepth(),queue.size());
				if(timeout<=0)
					task.execute();
				else{
					exec = Executors.newCachedThreadPool();
					Callable<Integer> call=new Callable<Integer>() {
						public Integer call() throws Exception {
							return new Inner(t).exe();
						}
					};
					future=exec.submit(call);
					future.get(timeout, TimeUnit.SECONDS);
				}
			}  catch (TimeoutException e) {
				logger.warn("task[{}] timeout!",task.toString());
				if(future!=null)
				future.cancel(true);
			}catch (Exception e) {
				logger.info(getErrorMsg(),e.toString());
			}finally{
				if(exec!=null)
					exec.shutdown();
			}
		}
		protected void cleanup() {
		}
		protected boolean extraExitCondition() {
			return stop;
		}
	}

	

	public String getManageName() {
		if(manageName==null){
			manageName=getClass().getSimpleName();
		}
		return manageName;
	}

	public void setManageName(String manageName) {
		this.manageName = manageName;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getConsumerThreadSize() {
		return consumerThreadSize;
	}

	public void setConsumerThreadSize(int consumerThreadSize) {
		if(consumerThreadSize>=1)
		this.consumerThreadSize = consumerThreadSize;
	}

	public String getThreadNamePre() {
		if(threadNamePre==null){
			threadNamePre=getManageName()+"-consumer";
		}
		return threadNamePre;
	}

	public void setThreadNamePre(String threadNamePre) {
		this.threadNamePre = threadNamePre;
	}

	public BlockingQueue<Task> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<Task> queue) {
		this.queue = queue;
	}

	public String getErrorMsg() {
		if(errorMsg==null){
			errorMsg=getManageName()+" of manageName has Error msg like [{}]!";
		}
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	 class Inner{
			private Task task;
			public Inner(Task task){
				this.task=task;
			}
			public Integer exe() throws Exception{
				task.execute();
				return 1;
			}
		}



	public boolean isIgnoreLog() {
		return ignoreLog;
	}

	public void setIgnoreLog(boolean ignoreLog) {
		this.ignoreLog = ignoreLog;
	}
	
	
}
