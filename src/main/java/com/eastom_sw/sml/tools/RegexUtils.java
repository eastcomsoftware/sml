package com.eastom_sw.sml.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 正则工具类
 * @author hw
 *
 */
public  class RegexUtils {  
  
    /**
     * 
     * @param regexp  正则匹配表达示
     * @param data 完整字符串
     * @return
     */
    public static List<String> matchGroup(String regexp, String data) {  
    	List<String> list = new ArrayList<String>(); 
    	if(data==null){
    		return list;
    	}
        Pattern p = Pattern.compile(regexp,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);  
        Matcher matcher = p.matcher(data);  
        while (matcher.find()) {  
           list.add(matcher.group());
        }  
        return list;  
    }  
    public static String[] matchSubString(String pattern_v,String content){
    	Pattern pattern = Pattern.compile(pattern_v, Pattern.DOTALL | Pattern.UNICODE_CASE);
		Matcher matcher=pattern.matcher(content);
		matcher.matches();
		String[] ss = group2Array(matcher);
		return ss;
    }
    public  static String[] group2Array(Matcher matcher) {
		int k = matcher.groupCount();
		String[] ss = new String[k];
		for (int i = 1; i <= k; i++) {
			ss[i - 1] = matcher.group(i).trim();
		}
		return ss;
	}
    /**
     * 
     * @param sql   sql
     * @param firstStr  开始字符串
     * @param firstEnd  结束字符串
     * @return
     */
    public static String subString(String sql,String firstStr,String firstEnd,boolean igCase){
    	String sqlTemp=sql;
    	if(igCase)
    	sqlTemp=sql.toUpperCase();
		if(sql==null||firstStr==null||firstEnd==null){
			return null;
		}
		int first=sqlTemp.indexOf(firstStr);
		if(igCase)
		first=first==-1?sqlTemp.indexOf(firstStr.toUpperCase()):first;
		int end=sqlTemp.indexOf(firstEnd,first);
		if(igCase)
		end=end==-1?sqlTemp.indexOf(firstEnd.toUpperCase(),first):end;
		if(first==-1||end==-1){
			return null;
		}
		return sql.substring(first+firstStr.length(),end);
	}
    public static String subString(String sql,String firstStr,String firstEnd){
    	return subString(sql, firstStr, firstEnd,false);
    }
 
    public static boolean isNumber(Object str){
    	 if(str instanceof Number){
    		 return true;
    	 }
         char[] chars = String.valueOf(str).toCharArray();
         int sz = chars.length;
         boolean hasExp = false;
         boolean hasDecPoint = false;
         boolean allowSigns = false;
         boolean foundDigit = false;
         // deal with any possible sign up front
         int start = (chars[0] == '-') ? 1 : 0;
         if (sz > start + 1 && chars[start] == '0' && chars[start + 1] == 'x') {
             int i = start + 2;
             if (i == sz) {
                 return false; // str == "0x"
             }
             // checking hex (it can't be anything else)
             for (; i < chars.length; i++) {
                 if ((chars[i] < '0' || chars[i] > '9')
                     && (chars[i] < 'a' || chars[i] > 'f')
                     && (chars[i] < 'A' || chars[i] > 'F')) {
                     return false;
                 }
             }
             return true;
         }
         sz--; 
         int i = start;
         while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
             if (chars[i] >= '0' && chars[i] <= '9') {
                 foundDigit = true;
                 allowSigns = false;

             } else if (chars[i] == '.') {
                 if (hasDecPoint || hasExp) {
                     return false;
                 }
                 hasDecPoint = true;
             } else if (chars[i] == 'e' || chars[i] == 'E') {
                 if (hasExp) {
                     return false;
                 }
                 if (!foundDigit) {
                     return false;
                 }
                 hasExp = true;
                 allowSigns = true;
             } else if (chars[i] == '+' || chars[i] == '-') {
                 if (!allowSigns) {
                     return false;
                 }
                 allowSigns = false;
                 foundDigit = false;
             } else {
                 return false;
             }
             i++;
         }
         if (i < chars.length) {
             if (chars[i] >= '0' && chars[i] <= '9') {
                 return true;
             }
             if (chars[i] == 'e' || chars[i] == 'E') {
                 return false;
             }
             if (chars[i] == '.') {
                 if (hasDecPoint || hasExp) {
                     return false;
                 }
                 return foundDigit;
             }
             if (!allowSigns
                 && (chars[i] == 'd'
                     || chars[i] == 'D'
                     || chars[i] == 'f'
                     || chars[i] == 'F')) {
                 return foundDigit;
             }
             if (chars[i] == 'l'
                 || chars[i] == 'L') {
                 return foundDigit && !hasExp && !hasDecPoint;
             }
             return false;
         }
         return !allowSigns && foundDigit;
    }
  
}  
