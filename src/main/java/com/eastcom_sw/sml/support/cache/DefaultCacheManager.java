package com.eastcom_sw.sml.support.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.eastcom_sw.sml.support.ManagedThread;


public class DefaultCacheManager extends ManagedThread implements CacheManager {
	private static   Map<String,Object> caches=new ConcurrentHashMap<String, Object>();
	private static   Map<String,Long> cacheMinute=new ConcurrentHashMap<String, Long>();
	private static DefaultCacheManager cm;
	public DefaultCacheManager(){
		this.setName("defaultCacheManagerThread");
		init();
	}
	public static DefaultCacheManager newInstance(){
		if(cm==null){
			cm=new DefaultCacheManager();
		}
		return cm;
	}
	
	public  void clearOldCache(){
		Set<String> cms=cacheMinute.keySet();
		List<String> removeLst=new ArrayList<String>();
		Iterator<String> iterator=cms.iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			long value=cacheMinute.get(key);
			if(value<System.currentTimeMillis()){
				removeLst.add(key);
			}
		}
		for(String key:removeLst){
			remove(key);
		}
	}
	
	public Object get(String key) {
		Long time=cacheMinute.get(key);
		if(time==null){
			return null;
		}
		if(time>System.currentTimeMillis()){
			return caches.get(key);
		}
		return null;
	}

	public void set(String key, Object value, int minutes) {
		if(null!=value){
			if(value instanceof List){
				List<?> to=(List<?>)value;
				if(to.size()==0){
					return;
				}
			}
			caches.put(key, value);
			if(minutes>0)
				cacheMinute.put(key, System.currentTimeMillis()+minutes*1000*60);
			else
				cacheMinute.put(key, System.currentTimeMillis()+24*60*1000*60);
		}
	}

	public boolean contain(String key) {
		return caches.containsKey(key)&&cacheMinute.containsKey(key);
	}

	public  void remove(String key) {
		if(contain(key)){
			caches.remove(key);
			cacheMinute.remove(key);
		}
	}

	public int clearKeyStart(String keyStart) {
		Set<String> cms=cacheMinute.keySet();
		List<String> removeLst=new ArrayList<String>();
		Iterator<String> iterator=cms.iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			if(key.startsWith(keyStart))
				removeLst.add(key);
		}
		int size=removeLst.size();
		for(String key:removeLst){
			remove(key);
		}
		return size;
	}

	public int clear() {
		int size=caches.keySet().size();
		caches.clear();
		cacheMinute.clear();
		return size;
	}

	@Override
	public Map<String, Object> getKeyStart(String keyStart) {
		Map<String,Object> map=new HashMap<String,Object>();
		Set<String> cms=cacheMinute.keySet();
		Iterator<String> iterator=cms.iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			if(key.startsWith(keyStart)){
				map.put(key,caches.get(key));
			}
		}
		return map;
	}
	public void destroy() {
		this.stopFlag=true;
		cm=null;
	}
	@Override
	protected boolean prepare() {
		return true;
	}
	@Override
	protected void doWorkProcess() {
		clearOldCache();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void cleanup() {
		clear();
	}
	@Override
	protected boolean extraExitCondition() {
		return stopFlag;
	}
	@Override
	public void init() {
		this.start();
	}
}
