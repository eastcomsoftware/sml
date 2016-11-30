package com.eastcom_sw.sml.tools;

import java.util.LinkedHashMap;
import java.util.Map;

public class Maps<K,V> {
	
	private Map<K,V> map;
	
	public Maps(){
		map=new LinkedHashMap<K,V>();
	}
	public Maps(Map<K,V> map){
		this.map=map;
	}
	public Maps<K,V> put(K k,V v){
		map.put(k, v);
		return this;
	}
	public Maps<K,V> remove(K k){
		 map.remove(k);
		 return this;
	}
	
	public Map<K, V> getMap() {
		return map;
	}
	
}
