package com.eastom_sw.sml.tools;
/**
 * 对一些配置上的失误进行断言，用于在开发过程中快速定位发现问题
 * @author hw
 *
 */
public class Assert {
	
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void notRpeatMark(String all,String mark){
		if(all.contains("<"+mark+" ")){
			throw new IllegalArgumentException("not support repeat mark <"+mark);
		}
		if(all.contains("</"+mark+">")){
			throw new IllegalArgumentException("not support repeat mark </"+mark+">");
		}
	}
	public static void main(String[] args) {
		notRpeatMark("aaa</if>","if");
	}
}
