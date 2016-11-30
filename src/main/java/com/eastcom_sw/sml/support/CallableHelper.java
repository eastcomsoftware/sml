package com.eastcom_sw.sml.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author hw
 * 
 * 用于异步多次任务进行同时调用统一返回
 * 
 */
public class CallableHelper {
	
	public static  int retriedTimes=100; 
	public static <T> List<T> callresults(Callable<T>...cls){
		return callresults(1200,cls);
	}
	public static interface Callback<T>{
		void call(T t);
	}
	public static <T> List<T> callresults(int retrys,int max_threads,Callable<T> ...cls){
		List<T> result=new ArrayList<T>();
		int times=cls.length%max_threads==0?cls.length/max_threads:(cls.length/max_threads+1);
		for(int i=0;i<times;i++){
			@SuppressWarnings("unchecked")
			List<T> rt=CallableHelper.callresults(Arrays.asList(cls).subList(i*max_threads,(i+1)*max_threads>cls.length?cls.length:(i+1)*max_threads).toArray(new Callable[]{}));
			result.addAll(rt);
		}
		return result;
	}
	public static <T> void callresults(int retrys,int max_threads,Callback<T> callback,Callable<T> ...cls){
		int times=cls.length%max_threads==0?cls.length/max_threads:(cls.length/max_threads+1);
		for(int i=0;i<times;i++){
			@SuppressWarnings("unchecked")
			List<T> rt=CallableHelper.callresults(Arrays.asList(cls).subList(i*max_threads,(i+1)*max_threads>cls.length?cls.length:(i+1)*max_threads).toArray(new Callable[]{}));
			for(T t:rt){
				try{
					callback.call(t);
				}catch(Throwable e){}
			}
		}
	}
	/**
	 * 
	 * @param retrys 超时s
	 * @param cls    各任务
	 * @return 各任务返回数据
	 */
	public static <T> List<T> callresults(int retrys,Callable<T> ...cls){
		List<T> rs=new ArrayList<T>();
		ExecutorService exec = Executors.newCachedThreadPool();
		ArrayList<Future<T>> results = new ArrayList<Future<T>>(); // Future
		for (int i = 0; i < cls.length; i++) {
			results.add(exec.submit(cls[i]));
		}
		try {
			Thread.sleep(10);//对于10ms内的任务不至于升到100s
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try{
			wait(results,(1000/retriedTimes)*retrys);
			for(int i=0;i<results.size();i++){
				try {
					rs.add(results.get(i).get(1,TimeUnit.MICROSECONDS));
				} catch (InterruptedException e) {
					rs.add(null);
					results.get(i).cancel(true);
				} catch (ExecutionException e) {
					rs.add(null);
					results.get(i).cancel(true);
				} catch (TimeoutException e) {
					rs.add(null);
					results.get(i).cancel(true);
				}catch(Throwable e){
					e.printStackTrace();
					rs.add(null);
					e.toString();
					results.get(i).cancel(true);
				}
			}
		}
		catch(Throwable e){
		}finally{
			exec.shutdown();
		}
		return rs;
	}
	
	private static <T> void wait(ArrayList<Future<T>> results,int counts) {
		if(counts<=0){
			return;
		}
		boolean flag=true;
		for(int i=0;i<results.size();i++){
			flag=results.get(i).isDone();
			if(!flag){
				break;
			}
		}
		if(!flag){
			try {
				Thread.sleep(retriedTimes);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
			wait(results,counts-1);
		}
	}
	
}
