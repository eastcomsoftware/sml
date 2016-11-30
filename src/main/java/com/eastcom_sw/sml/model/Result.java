package com.eastcom_sw.sml.model;

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
	private int page;
	private int limit;
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
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	@Override
	public String toString() {
		return "Result [count=" + count + ", datas=" + datas + "]";
	}
	
}
