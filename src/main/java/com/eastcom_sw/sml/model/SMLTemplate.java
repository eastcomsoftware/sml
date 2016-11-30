package com.eastcom_sw.sml.model;

import java.io.Serializable;

public class SMLTemplate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6194103266608363068L;
	
	
	public SMLTemplate() {
		super();
	}
	
	
	public SMLTemplate(String mslStr) {
		super();
		this.mslStr = mslStr;
	}

	/**
	 * 待解析字符串
	 */
	private String mslStr;
	/**
	 * 参数字段
	 */
	private SMLParams smlParams;
	
	public String getMslStr() {
		return mslStr;
	}

	public void setMslStr(String mslStr) {
		this.mslStr = mslStr;
	}


	public SMLParams getSmlParams() {
		return smlParams;
	}


	public void setSmlParams(SMLParams smlParams) {
		this.smlParams = smlParams;
	}
	
	
}
