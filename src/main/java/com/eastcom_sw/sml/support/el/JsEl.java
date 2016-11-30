package com.eastcom_sw.sml.support.el;

import java.util.EnumSet;
import java.util.Map;

import javax.script.ScriptException;

import com.eastcom_sw.sml.core.resolver.JsEngine;

public class JsEl implements El{
	public boolean parser(String elp,Map<String,Object> varMap) throws Exception {
		for (Map.Entry<String,Object> entry:varMap.entrySet()) {
			elp = elp.replace("@" + entry.getKey(),
					String.valueOf(entry.getValue()));
		}
		return parser(elp);
	}
	//对js不支持  le ge 等进行重新处理
	public boolean parser(String elp) throws ScriptException{
		for(ElOprator elOp:EnumSet.allOf(ElOprator.class)){
			elp=elp.replace(" "+elOp.name+" ", elOp.op);
		}
		return JsEngine.evelBoolean(elp);
	}
}
