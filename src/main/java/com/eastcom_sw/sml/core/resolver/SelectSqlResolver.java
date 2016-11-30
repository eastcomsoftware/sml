package com.eastcom_sw.sml.core.resolver;


import java.util.List;

import com.eastcom_sw.sml.model.SMLParams;
import com.eastcom_sw.sml.support.el.El;
import com.eastcom_sw.sml.tools.Assert;
import com.eastcom_sw.sml.tools.RegexUtils;
/**
 * 
 * @author hw
 * 后续处理：增加数据库
 */
public class SelectSqlResolver implements SqlResolver{
	public synchronized Rst resolve(String dialect, String temp,SMLParams sqlParamMaps) {
		List<String> mathers=null;
		if(temp.contains("<select")){
		//单个sql，反复出现的逻辑选择
			mathers=RegexUtils.matchGroup("<select\\d* id=\"\\w+\">",temp);
			for(String mather:mathers){
				String tmt=mather;
				int start=temp.indexOf(tmt);
				if(!temp.contains(tmt)){
					continue;
				}
				//取标签值
				String mark=RegexUtils.subString(tmt, "<", " id=");
				int end=temp.indexOf("</"+mark+">",start);
				Assert.isTrue(end!=-1,mather+" must has end!");
				//整个逻辑字符串 tm
				String tm=temp.substring(start,end+("</"+mark+">").length());
				//属性值
				String id=RegexUtils.subString(tm,"id=\"","\">");
				//内容
				String content=RegexUtils.subString(tm,">",("</"+mark+">"));
				Assert.notRpeatMark(content,mark);
				String replaceId="<included id=\""+id+"\"/>";
				temp=temp.replace(tm,"");
				temp=temp.replace(replaceId,content);
			}
		}
		return new Rst(temp);
	}
	


	public void setEl(El el) {
		
	}


	
	
}
