package com.eastcom_sw.sml.support.cache;

import java.util.Map;

import com.eastcom_sw.sml.plugin.Plugin;

public interface CacheManager extends Plugin{
	  /**
	   * 获取缓存key
	   * @param key
	   * @return
	   */
	  Object get(String key);
	  /**
	   * 增加缓存 
	   * @param key
	   * @param value
	   * @param minutes 分钟
	   */
	  void set(String key,Object value,int minutes);
	  /**
	   * 是否包含
	   * @param key
	   * @return
	   */
	  boolean contain(String key);
	  /**
	   * 移除缓存
	   * @param key
	   */
	  void remove(String key);
	  /**
	   * 移除开始匹配
	   * @param keyStart
	   * @return
	   */
	  int clearKeyStart(String keyStart);
	  /**
	   * 换取开始匹配
	   * @param keyStart
	   * @return
	   */
	  Map<String,Object> getKeyStart(String keyStart);
	  /**
	   * 清除所有
	   * @return
	   */
	  int clear();
}
