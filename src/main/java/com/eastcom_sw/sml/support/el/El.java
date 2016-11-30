package com.eastcom_sw.sml.support.el;

import java.util.Map;

public interface El {
	static enum ElOprator{
		 LT("lt","<"),
		 LE("le","<="),
		 EQ("eq","=="),
		 GE("ge",">="),
		 GT("gt",">"),
		 NE("ne","!=");
		 
		String name;
		String op;
		ElOprator(String name,String op){
			this.name=name;
			this.op=op;
		}
	}
	
	public boolean parser(String elp,Map<String,Object> varMap) throws Exception;
}
