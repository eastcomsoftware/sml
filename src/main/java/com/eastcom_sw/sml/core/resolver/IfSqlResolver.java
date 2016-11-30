package com.eastcom_sw.sml.core.resolver;

import java.util.List;

import com.eastcom_sw.sml.model.SMLParam;
import com.eastcom_sw.sml.model.SMLParams;
import com.eastcom_sw.sml.support.el.El;
import com.eastcom_sw.sml.tools.Assert;
import com.eastcom_sw.sml.tools.RegexUtils;
/**
 * 复杂逻辑的实现
 * 改变sql走向，借鉴mybatis ibaits语法  不引入ognl xml语法，而是自己实现
 * 减少依赖
 * @author hw
 *后续解决：  if else逻辑    case when 逻辑
 *2016-03-09  empty 把空字符串纳入空
 */
public class IfSqlResolver implements SqlResolver{
	
	private El el;
	public Rst resolve(String dialect, String temp,SMLParams sqlParamMaps) {
		List<String> mathers=null;
		//非空函数   \\d*用于嵌套
		if(temp.contains("<isNotEmpty")){
			mathers=RegexUtils.matchGroup("<isNotEmpty\\d* property=\"\\w+\">",temp);
			for(String mather:mathers){
				String tmt=mather;
				int start=temp.indexOf(tmt);
				if(!temp.contains(tmt)){
					continue;
				}
				//取标签值
				String mark=RegexUtils.subString(tmt, "<", " property=");
				int end=temp.indexOf("</"+mark+">",start);
				Assert.isTrue(end!=-1,mather+" must has end!");
				//整个逻辑字符串 tm
				String tm=temp.substring(start,end+("</"+mark+">").length());
				//属性值
				String property=RegexUtils.subString(tm,"property=\"","\">");
				//内容
				String content=RegexUtils.subString(tm,">",("</"+mark+">"));
				Assert.notRpeatMark(content,mark);
				SMLParam sp=sqlParamMaps.getSmlParam(property);
				Assert.notNull(sp, property+" is not config for "+mark);
				boolean flag=sp!=null?(sp.getValue()!=null&&String.valueOf(sp.getValue()).length()>0):false;
				if(flag){
					temp=temp.replace(tm, content);
				}else{
					temp=temp.replace(tm," ");
				}
			}
		}
		//空函数
		if(temp.contains("<isEmpty")){
			mathers=RegexUtils.matchGroup("<isEmpty\\d* property=\"\\w+\">",temp);
			for(String mather:mathers){
				String tmt=mather;
				if(!temp.contains(tmt)){
					continue;
				}
				int start=temp.indexOf(tmt);
				String mark=RegexUtils.subString(tmt, "<", " property=");
				int end=temp.indexOf("</"+mark+">",start);
				Assert.isTrue(end!=-1,mather+" must has end!");
				String tm=temp.substring(start,end+("</"+mark+">").length());
				String property=RegexUtils.subString(tm,"property=\"","\">");
				String content=RegexUtils.subString(tm,">",("</"+mark+">"));
				Assert.notRpeatMark(content,mark);
				SMLParam sp=sqlParamMaps.getSmlParam(property);
				Assert.notNull(sp, property+" is not config for "+mark);
				boolean flag=sp==null?true:(sp.getValue()==null||String.valueOf(sp.getValue()).length()==0);
				if(flag){
					temp=temp.replace(tm, content);
				}else{
					temp=temp.replace(tm," ");
				}
			}
		}
		//相等函数
		if(temp.contains("<isEqual")){
			mathers=RegexUtils.matchGroup("<isEqual\\d* property=\"\\w+\" compareValue=\"\\w+\">",temp);
			for(String mather:mathers){
				String tmt=mather;
				if(!temp.contains(tmt)){
					continue;
				}
				int start=temp.indexOf(tmt);
				String mark=RegexUtils.subString(tmt, "<", " property=");
				int end=temp.indexOf("</"+mark+">",start);
				Assert.isTrue(end!=-1,mather+" must has end!");
				String tm=temp.substring(start,end+("</"+mark+">").length());
				String property=RegexUtils.subString(tm,"property=\"","\" compareValue");
				String value=RegexUtils.subString(tm,"compareValue=\"","\">");
				String content=RegexUtils.subString(tm,">",("</"+mark+">"));
				Assert.notRpeatMark(content,mark);
				SMLParam sp=sqlParamMaps.getSmlParam(property);
				Assert.notNull(sp, property+" is not config for "+mark);
				boolean flag=sp==null?false:(value.equals(sp.getValue()));
				if(flag){
					temp=temp.replace(tm, content);
				}else{
					temp=temp.replace(tm," ");
				}
			}
		}
		//最复杂函数实现 引入表达示语言实现
		if(temp.contains("<if")){
			mathers=RegexUtils.matchGroup("<if\\d* test=\"\\s+\\S*\\s+\">",temp);
			for(String mather:mathers){
				String tmt=mather;
				if(!temp.contains(tmt)){
					continue;
				}
				int start=temp.indexOf(tmt);
				String mark=RegexUtils.subString(tmt, "<", " test=");
				int end=temp.indexOf("</"+mark+">",start);
				Assert.isTrue(end!=-1,mather+" must has end!");
				String tm=temp.substring(start,end+("</"+mark+">").length());
				String text=RegexUtils.subString(tm,"test=\"","\">");
				String content=RegexUtils.subString(tm,">",("</"+mark+">"));
				Assert.notRpeatMark(content,mark);
				//对text内容进行处理
				//参数报错直接跳出，减少对数据库的压力
				boolean flag=false;
				Assert.notNull(el, "not support elp [el object is null!]");
				try {
					flag = el.parser(text,sqlParamMaps.getMap());
				} catch (Exception e) {//
					throw new IllegalArgumentException("jsElP["+text+"] exception "+e );
				}
				if(flag){
					temp=temp.replace(tm, content);
				}else{
					temp=temp.replace(tm," ");
				}
			}
		}
		return new Rst(temp);
	}


	public El getEl() {
		return el;
	}

	public void setEl(El el) {
		this.el = el;
	}


}
