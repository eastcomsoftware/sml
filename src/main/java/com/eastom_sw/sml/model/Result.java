package com.eastom_sw.sml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 为分页简单的写的包装类
 * @author wen
 *
 */
public class Result {
	private Long count;
	private List<Map<String,Object>> datas=new ArrayList<Map<String,Object>>();
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public List<Map<String, Object>> getDatas() {
		return datas;
	}
	public void setDatas(List<Map<String, Object>> datas) {
		this.datas = datas;
	}
	@Override
	public String toString() {
		return "Result [count=" + count + ", datas=" + datas + "]";
	}
	
}
