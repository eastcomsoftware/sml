package com.eastom_sw.sml.core.resolver;

import java.util.List;

public class Rst{
	private String sqlString;
	private List<Object> paramObjects;
	
	public Rst() {
		super();
	}
	
	public Rst(String sqlString) {
		super();
		this.sqlString = sqlString;
	}

	public Rst(String sqlString, List<Object> paramObjects) {
		super();
		this.sqlString = sqlString;
		this.paramObjects = paramObjects;
	}

	public String getSqlString() {
		return sqlString;
	}
	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}
	public List<Object> getParamObjects() {
		return paramObjects;
	}
	public void setParamObjects(List<Object> paramObjects) {
		this.paramObjects = paramObjects;
	}
	
}
