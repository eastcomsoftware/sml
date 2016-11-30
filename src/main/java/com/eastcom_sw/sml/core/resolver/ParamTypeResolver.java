package com.eastcom_sw.sml.core.resolver;

import java.util.List;

import com.eastcom_sw.sml.model.SMLParam;
import com.eastcom_sw.sml.model.SMLParams;
import com.eastcom_sw.sml.support.el.El;
import com.eastcom_sw.sml.tools.Assert;
import com.eastcom_sw.sml.tools.RegexUtils;
/**
 * 
 * @author wen
 *
 */
public class ParamTypeResolver implements SqlResolver{

	public Rst resolve(String dialect, String temp,SMLParams sqlParamMaps) {
		List<String> mathers=null;
		//对数据进行
		if(temp.contains("<jdbcType")){
			mathers=RegexUtils.matchGroup("<jdbcType name=\"\\w+\" type=\"\\S+\"(/?)>",temp);
			for(String mather:mathers){
				String tmt=mather;
				if(!temp.contains(tmt)){
					continue;
				}
				
				if(tmt.endsWith("/>")){
					String name=RegexUtils.subString(tmt, "name=\"", "\" type=\"");
					String type=RegexUtils.subString(tmt,"type=\"","\"/>");
					SMLParam sp=sqlParamMaps.getSmlParam(name);
					if(sp!=null){
						sp.setType(type);
						sp.handlerValue(sp.getValue()==null?null:String.valueOf(sp.getValue()));
					}
					temp=temp.replace(tmt,"");
				}else{
					//取标签值
					String name=RegexUtils.subString(tmt, "name=\"", "\" type=\"");
					String type=RegexUtils.subString(tmt,"type=\"","\">");
					int start=temp.indexOf(tmt);
					int end=temp.indexOf("</jdbcType>",start);
					Assert.isTrue(end!=-1,mather+" must has end!");
					String tm=temp.substring(start,end+("</jdbcType>").length());
					String content=RegexUtils.subString(tm,">",("</jdbcType>"));
					SMLParam sp=sqlParamMaps.getSmlParam(name);
					if(sp!=null){
						sp.setType(type);
						sp.handlerValue(sp.getValue()==null?null:String.valueOf(sp.getValue()));
						Object value=sp.getValue();
						if(value!=null){
							if(value.getClass().isArray()){
								Object[] tos=(Object[])value;
								for(int i=0;i<tos.length;i++){
									tos[i]=JsEngine.evel(content.replace("@value",String.valueOf(tos[i])));
								}
							}else{
								sp.setValue(JsEngine.evel(content.replace("@value",String.valueOf(value))));
							}
						}
					}
					temp=temp.replace(tm,"");
				}
			}
		}
		return new Rst(temp);
	}

	public void setEl(El el) {
	}

}
