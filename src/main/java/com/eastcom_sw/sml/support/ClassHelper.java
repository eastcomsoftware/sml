package com.eastcom_sw.sml.support;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 加载修改后builderclasses，包含新增
 * 这样可以实现服务不需要重启，可自由改变返回结果格式
 * @author hw
 */
public class ClassHelper {
	
	/**
	 * 动态加载jar包并加载class生成对象
	 * @param filepath
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Object newInstance(String filepath,String className) throws Exception{
		URLClassLoader cl=new URLClassLoader(new URL[]{new URL(filepath)});
		return cl.loadClass(className).newInstance();
	}
	
}
