package com.eastcom_sw.sml.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eastcom_sw.sml.tools.MapUtils;
/**
 * 
 */
public class SMLParams implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1794719971434841200L;
	private List<SMLParam> smlParams=new ArrayList<SMLParam>();
	private Map<String,SMLParam> mapParams=new LinkedHashMap<String,SMLParam>();
	public List<SMLParam> getSmlParams() {
		return smlParams;
	}
	public SMLParams reinit(){
		setSqlParams(getSmlParams());
		return this;
	}
	public void setSqlParams(List<SMLParam> smlParams) {
		for(SMLParam sp:smlParams){
			mapParams.put(sp.getName(),sp);
		}
		this.smlParams =smlParams;
		mapParams=MapUtils.sort(mapParams);//解决字符串替换时的问题
	}
	public SMLParam getSmlParam(String name){
		return mapParams.get(name);
	}
	public SMLParam getSqlParamFromList(String name){
		for(SMLParam sqlParam:smlParams){
			if(sqlParam.getName().equals(name)){
				return sqlParam;
			}
		}
		return null;
	}
	public Map<String, Object> getMap() {
		Map<String,Object> varMap=new LinkedHashMap<String, Object>();
		for(Map.Entry<String,SMLParam> entry:this.mapParams.entrySet()){
			varMap.put(entry.getKey(),entry.getValue().getValue());
		}
		return varMap;
	}
	public Map<String, SMLParam> getMapParams() {
		return mapParams;
	}

	public void setMapParams(Map<String,SMLParam> mapParams) {
		this.mapParams = mapParams;
	}
	
	public SMLParams add(String key,Object value){
		 SMLParam sml=new SMLParam(key,value);
		 getSmlParams().add(sml);
		 return this;
	}
	public SMLParams add(String key,String value,String type){
		SMLParam sml=new SMLParam(key,value);
		sml.setType(type);
		getSmlParams().add(sml);
		sml.handlerValue(value);
		return this;
	}
	
}
