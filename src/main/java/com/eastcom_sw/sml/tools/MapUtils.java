package com.eastcom_sw.sml.tools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 
 * 主要用于Map操作，内存上存在浪费，后续改进
 * sort方法进行   排序nulls last实现，规避了不必要人工失误报错的影响
 * 重写部分方法，增加分析聚合函数处理
 * group  分组类函数       建立索引快速查找
 * grep   查询类函数       查询过滤
 * merge  合并类函数       合并
 * compare 比较类函数    同环比
 * aggregate 聚合类函数
 * get    获取值
 * @author hw
 * rebuild rebuildObject 可以快速的转化成支持的格式
 * 
 */
public class MapUtils {
	private static final DecimalFormat dcmFmt = new DecimalFormat("0.00");
	static{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	}
	 /**
     * 转置 Map
     */
    public static <K, V> Map<V, K> invert(Map<K, V> source) {
        Map<V, K> target = null;
        if (source!=null&&source.keySet().size()>0) {
            target = new LinkedHashMap<V, K>(source.size());
            for (Map.Entry<K, V> entry : source.entrySet()) {
                target.put(entry.getValue(), entry.getKey());
            }
        }
        return target;
    }
    /**
     * 单分组
     * @param oriDatas 
     * @param groupname 分组key
     * @param names 返回字段
     * @return
     */
    public static <K,V> Map<V,Map<K,V>> groupMpSingle(List<Map<K,V>> oriDatas,K groupname){
    	return groupMpSingle(oriDatas, groupname,null,null);
    }
    public static <K,V> Map<V,Map<K,V>> groupMpSingle(List<Map<K,V>> oriDatas,K groupname,K[] names){
    	return groupMpSingle(oriDatas, groupname, names,null);
    }
    @SuppressWarnings("unchecked")
	public static <K,V> Map<V,Map<K,V>> groupMpSingle(List<Map<K,V>> oriDatas,K[] groupname){
    	return groupMpSingle(oriDatas, groupname,(K)"#",null,null);
    }
    public static <K,V> Map<V,Map<K,V>> groupMpSingle(List<Map<K,V>> oriDatas,K[] groupname,K split){
    	return groupMpSingle(oriDatas, groupname, split,null,null);
    }
    @SuppressWarnings("unchecked")
	public static <K,V> Map<V,Map<K,V>> groupMpSingle(List<Map<K,V>> oriDatas,K[] groupnames,K split,K[] names,K[] renames){
    	Map<V,Map<K,V>> result=newLinkedHashMap();
    	for(Map<K,V> data:oriDatas){
    		StringBuilder sb=new StringBuilder();
    		for(K k:groupnames){
    			sb.append(data.get(k)).append(split);
    		}
    		sb.deleteCharAt(sb.length()-1);
    		result.put((V)sb.toString(),rebuildMp(data,names,renames));
    	}
    	return result;
    }
    public static <K,V> Map<V,Map<K,V>> groupMpSingle(List<Map<K,V>> oriDatas,K groupname,K[] names,K[] renames){
    	Map<V,Map<K,V>> result=newLinkedHashMap();
    	for(Map<K,V> data:oriDatas){
    		result.put(data.get(groupname),rebuildMp(data,names,renames));
    	}
    	return result;
    }
    public static <K,V> List<Map<K,V>> groupMpSingleBack(Map<V,Map<K,V>> oriDatas,K groupname){
    	List<Map<K,V>> result=newArrayList();
    	for(Map.Entry<V,Map<K,V>> entry:oriDatas.entrySet()){
    		if(!entry.getValue().containsKey(groupname)){
    			entry.getValue().put(groupname, entry.getKey());
    		}
    	}
    	return result;
    }
    /**
     * 多分组
     * @param oriDatas
     * @param groupname
     * @param names
     * @return
     */
    public static <K,V> Map<V,List<Map<K,V>>> groupMpLst(List<Map<K,V>> oriDatas,K groupname){
    	return groupMpLst(oriDatas, groupname, null,null);
    }
    public static <K,V> Map<V,List<Map<K,V>>> groupMpLst(List<Map<K,V>> oriDatas,K groupname,K[] names){
    	return groupMpLst(oriDatas, groupname, names,null);
    }
    public static <K,V> Map<V,List<Map<K,V>>> groupMpLst(List<Map<K,V>> oriDatas,K groupname,K[] names,K[] renames){
    	Map<V,List<Map<K,V>>> result=newLinkedHashMap();
    	for(Map<K,V> oriData:oriDatas){
    		if(!result.containsKey(oriData.get(groupname))){
    			result.put(oriData.get(groupname),new ArrayList<Map<K,V>>());
    		}
    		result.get(oriData.get(groupname)).add(rebuildMp(oriData, names,renames));
    	}
    	return result;
    }
    @SuppressWarnings("unchecked")
	public static <K,V> Map<V,List<Map<K,V>>> groupMpLst(List<Map<K,V>> datas,K[] groupnames){
    	return groupMpLst(datas, groupnames, (K)"#",null, null);
    }
    @SuppressWarnings("unchecked")
	public static <K,V> Map<V,List<Map<K,V>>> groupMpLst(List<Map<K,V>> datas,K[] groupnames,K split,K[] names,K[] renames){
    	Map<V,List<Map<K,V>>> result=newLinkedHashMap();
    	for(Map<K,V> data:datas){
    		StringBuilder sb=new StringBuilder();
    		for(K k:groupnames){
    			sb.append(data.get(k)).append(split);
    		}
    		sb.deleteCharAt(sb.length()-1);
    		V v=(V)sb.toString();
    		if(!result.containsKey(v)){
    			result.put(v,new ArrayList<Map<K,V>>());
    		}
    		result.get(v).add(rebuildMp(data, names,renames));
    	}
    	return result;
    }
    public static <K,V> List<Map<K,V>> groupMpLstBack(Map<V,List<Map<K,V>>> data,K groupname){
    	List<Map<K,V>> result=newArrayList();
    	for(Map.Entry<V,List<Map<K,V>>> entry:data.entrySet()){
    		result.addAll(entry.getValue());
    	}
    	return result;
    }
    /**
     * 多分组取对象
     * @param oriDatas
     * @param groupname
     * @param names
     * @param name
     * @param nameLst
     * @return
     */
    public static <T,K,V> List<Map<T,Object>> groupMpLst(List<Map<K,V>> oriDatas,K groupname,K[] names,T name,T nameLst){
 	   return groupMpLst(oriDatas, groupname, names,null, name, nameLst);
    }
   public static <T,K,V> List<Map<T,Object>> groupMpLst(List<Map<K,V>> oriDatas,K groupname,K[] names,K[] renames,T name,T nameLst){
	   List<Map<T,Object>> result=newArrayList();
	   Map<V,List<Map<K,V>>> resultT=groupMpLst(oriDatas,groupname,names,renames);
	   for(Map.Entry<V,List<Map<K,V>>> entry:resultT.entrySet()){
		   result.add(new Maps<T,Object>().put(name,entry.getKey()).put(nameLst,entry.getValue()).getMap());
	   }
	   return result;
   }
   
   /**
    * 多分组取对象默认命名
    * @param oriDatas
    * @param groupname
    * @param names
    * @return
    */
   public static <K,V> List<Map<String,Object>> groupMpLstDefault(List<Map<K,V>> oriDatas,K groupname){
	   return groupMpLst(oriDatas,groupname,null,null,"name","datas");
   }
   public static <K,V> List<Map<String,Object>> groupMpLstDefault(List<Map<K,V>> oriDatas,K groupname,K[] names){
	   return groupMpLst(oriDatas,groupname,names,null,"name","datas");
   }
   public static <K,V> List<Map<String,Object>> groupMpLstDefault(List<Map<K,V>> oriDatas,K groupname,K[] names,K[] renames){
	   return groupMpLst(oriDatas,groupname,names,renames,"name","datas");
   }
   /**
    * 二次分组函数-single
    * @param datas
    * @param key
    * @param subKey
    * @param names
    * @param renames
    * @return
    */
   public static <K,V> Map<V,Map<V,Map<K,V>>> groupMp2Single(List<Map<K,V>> datas,K key,K subKey,K[] names,K[] renames){
		Map<V,List<Map<K,V>>> datasss=groupMpLst(datas,key);
		Map<V,Map<V,Map<K,V>>> result=newLinkedHashMap();
		for(Map.Entry<V,List<Map<K,V>>> entry:datasss.entrySet()){
			result.put(entry.getKey(),groupMpSingle(entry.getValue(),subKey,names,renames));
		}
		return result;
	}
   public static <K,V> Map<V,Map<V,Map<K,V>>> groupMp2Single(List<Map<K,V>> datas,K key,K subKey){
		return groupMp2Single(datas,key,subKey,null,null);
	}
   /**
    * 重定义Map对象
    * @param oriData
    * @param names
    * @param renames
    * @return
    */
    
    @SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> rebuildMp(Map<K,V> oriData,K[] names,K[] renames){
    	Map<K,V> result=newLinkedHashMap();
    	if(names==null||names.length==0){
    		return oriData;
    	}
    	for(int i=0;i<names.length;i++){
    		K key=(renames==null||renames.length==0||names.length!=renames.length)?names[i]:renames[i];
    		V value=oriData.get(names[i]);
    		//额外逻辑处理函数   @表达示语言
    		if(key!=null&&(key instanceof String)){
    			String k=((String)key);
    			if(k.indexOf("@")>-1){
    				String[] ks=k.split("@");
    				if(ks.length>=2){
    					key=(K)ks[0];
    					value=ValueHelper.rebuildMpHandlerValue(ks[1],value);
    				}
    			}
    		}
			result.put(key,value);
		}
		return result;	
    }
    
    /**
     * 重定义Map数组包含累记值
     * @param oriDatas
     * @param names
     * @param renames
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <V> List<Map<String,V>> rebuildMpDist(List<Map<String,V>> oriDatas,String[] keys){
    	Map<String,V> rt=new HashMap<String,V>();
    	for(String key:keys){
    		rt.put(key,null);
    	}
    	for(Map<String,V> data:oriDatas){
    		for(String key:keys){
    			V v=data.get(key);
    			V vt=rt.get(key);
    			if(v==null){
    				continue;
    			}
    			if(vt==null){
    				rt.put(key,v);
    			}else{
    				try{
    				rt.put(key,(V)(Object)(Double.parseDouble(v+"")+Double.parseDouble(vt+"")));
    				}catch(Throwable t){}
    			}
    			data.put(key+"Dist", rt.get(key));
    		}
    	}
    	return oriDatas;
    }
    public static <V> List<Map<String,V>> rebuildMp(List<Map<String,V>> oriDatas,String format){
    	Map<String,String> rt=transMapFromStr(format);
    	return rebuildMp(oriDatas, rt.keySet().toArray(new String[]{}),rt.values().toArray(new String[]{}));
    }
    public static <V> Map<String,V> rebuildMp(Map<String,V> oriData,String format){
    	Map<String,String> rt=transMapFromStr(format);
    	return rebuildMp(oriData, rt.keySet().toArray(new String[]{}),rt.values().toArray(new String[]{}));
    }
    public static <K,V> List<Map<K,V>> rebuildMp(List<Map<K,V>> oriDatas,K[] names,K[] renames){
    	List<Map<K,V>> result=newArrayList();
    	if(oriDatas==null){
    		return result;
    	}
    	for(Map<K,V> oriData:oriDatas){
    		result.add(rebuildMp(oriData, names, renames));
    	}
    	return result;
    }
    public static <K,V> List<Map<K,V>> rebuildMp(List<Map<K,V>> oriDatas){
    	//
    	List<K> ks=grepKs(oriDatas);
    	for(Map<K,V> oriData:oriDatas){
    		for(K k:ks){
    			if(!oriData.containsKey(k)){
    				oriData.put(k,null);
    			}
    		}
    	}
    	return oriDatas;
    }
    public static <T> void rebuildMp(List<T> datas,Builder<T> builder){
		for(T data:datas)
		builder.build(data);
	}
    public static <T,K> void rebuildMp(Map<K,List<T>> datas,Builder<T> builder){
		for(Map.Entry<K,List<T>> entry:datas.entrySet()){
			for(T t:entry.getValue()){
				builder.build(t);
			}
		}
	}
	public static <T> void rebuildMp(T data,Builder<T> builder){
		builder.build(data);
	}
    public static <K,V> List<Map<K,V>> grep(List<Map<K,V>> datas,String format){
    	String thistype="this.type";
    	List<Map<K,V>> result=newArrayList();
    	Map<String,String> t=transMapFromStr(format);
    	boolean or=String.valueOf(t.get(thistype)).equals("or");
    	for(Map<K,V> data:datas){
    		if(or){
    			boolean flag=false;
    			for(Map.Entry<String,String> entry:t.entrySet()){
    				if(entry.getKey().equals(thistype)) continue;
    				if(data.containsKey(entry.getKey())){
    					String[] fs=entry.getValue().split(" ");
    					if(fs.length==2)
    					flag=ValueHelper.matcherOperater(data.get(entry.getKey()),fs[0],fs[1]);
    					if(flag){
    						result.add(data);
    						break;
    					}
    				}
    			}
    		}else{
    			boolean flag=true;
    			for(Map.Entry<String,String> entry:t.entrySet()){
    				if(entry.getKey().equals(thistype)) continue;
    				if(data.containsKey(entry.getKey())){
    					String[] fs=entry.getValue().split(" ");
    					if(fs.length==2)
    					flag=ValueHelper.matcherOperater(data.get(entry.getKey()),fs[0],fs[1]);
    					if(!flag){
    						break;
    					}
    				}
    			}
    			if(flag)
    				result.add(data);
    		}
    	}
    	return result;
    }
    public static <K,V> List<V> grepVsByK(List<Map<K,V>> datas,K k,boolean uq){
    	List<V> result=newArrayList();
    	for(Map<K,V> data:datas){
    		if(uq){
    			if(!result.contains(data.get(k)))
    			result.add(data.get(k));
    		}else{
    			result.add(data.get(k));
    		}
    	}
    	return result;
    }
    public static <K,V> List<K> grepKs(List<Map<K,V>> datas){
    	return grepKs(datas,true);
    }
    public static <K,V> List<K> grepKs(List<Map<K,V>> datas,boolean withNull){
    	List<K> result=newArrayList();
    	for(Map<K,V> data:datas){
    		for(Map.Entry<K,V> entry:data.entrySet()){
    			if(!result.contains(entry.getKey())){
    				if(entry.getValue()==null&&withNull){
    					result.add(entry.getKey());
    				}else{
    					result.add(entry.getKey());
    				}
    			}
    		}
    	}
    	return result;
    }
   
    /**
     * 二次分组函数-lst
     * @param datas
     * @param key
     * @param subKey
     * @param names
     * @param renames
     * @return
     */
    public static <K,V> Map<V,Map<V,List<Map<K,V>>>> groupMp2Lst(List<Map<K,V>> datas,K key,K subKey,K[] names,K[] renames){
		Map<V,List<Map<K,V>>> datasss=groupMpLst(datas,key);
		Map<V,Map<V,List<Map<K,V>>>> result=newLinkedHashMap();
		for(Map.Entry<V,List<Map<K,V>>> entry:datasss.entrySet()){
			result.put(entry.getKey(),groupMpLst(entry.getValue(),subKey,names,renames));
		}
		return result;
	}
    public static <K,V> Map<V,Map<V,List<Map<K,V>>>> groupMp2Lst(List<Map<K,V>> datas,K key,K subKey){
		return groupMp2Lst(datas,key,subKey,null,null);
	}
    /**
     * 合并集合
     * @param oriDatas
     * @param datas
     * @param groupId
     * @return
     */
	public static <K,V> List<Map<K,V>> mergeMp(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] groupIds){
		List<Map<K,V>> result=newArrayList();
		Map<V,Map<K,V>> oriDataIndex=groupMpSingle(oriDatas,groupIds);
		Map<V,Map<K,V>> dataIndex=groupMpSingle(datas,groupIds);
		for(Map.Entry<V,Map<K,V>> entry:oriDataIndex.entrySet()){
			result.add(mergeMp(entry.getValue(),dataIndex.get(entry.getKey())));
		}
		return result;
	}
	public static <K,V> Map<K,V> mergeMp(Map<K,V> oriData,Map<K,V> data){
		if(data==null||oriData==null){
			return oriData;
		}
		Map<K,V> result=newLinkedHashMap();
		result.putAll(oriData);
		for(Map.Entry<K, V> entry:data.entrySet()){
			if(!result.containsKey(entry.getKey())){
				result.put(entry.getKey(),entry.getValue());
			}
		}
		return result;
	}
	//高级比较合并  取舍
	public static <K,V> List<Map<K,V>> mergeMps(K[] groupIds,List<Map<K,V>> ...mps){
		List<Map<K,V>> result=newArrayList();
		for(int i=0;i<mps.length;i++){
			if(i==0){
				result=mps[i];
			}else{
				result=mergeMp(result,mps[i], groupIds);
			}
		}
		return result;
		
	}
	/**
	 * 笛卡而积
	 * @param oriDatas
	 * @param datas
	 * @return
	 */
	public static <K,V> List<Map<K,V>> comparemergeMpCartesianProduct(List<Map<K,V>> oriDatas,List<Map<K,V>> datas){
		return comparemergeMpCartesianProduct(oriDatas, datas,new HashMap<K,K>());
	}
	public static <V> List<Map<String,V>> comparemergeMpCartesianProduct(List<Map<String,V>> oriDatas,List<Map<String,V>> datas,String format){
		return comparemergeMpCartesianProduct(oriDatas, datas,transMapFromStr(format));
	}
	@SuppressWarnings("unchecked")
	public static <K,V> List<Map<K,V>> comparemergeMpCartesianProduct(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,Map<K,K> groupMap){
		return comparemergeMpCartesianProduct(oriDatas,datas,(K[])groupMap.keySet().toArray(),(K[])groupMap.values().toArray());
	}
	public static <K,V> List<Map<K,V>> comparemergeMpCartesianProduct(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] groupIds){
		return comparemergeMpCartesianProduct(oriDatas, datas, groupIds,groupIds);
	}
	public static <K,V> List<Map<K,V>> comparemergeMpCartesianProduct(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] oriGroupIds,K[] dGroupIds){
		List<Map<K,V>> result=newArrayList();
		if(datas==null||datas.size()==0){
			return result;
		}
		//
		if(oriGroupIds==null||oriGroupIds.length==0||dGroupIds==null||dGroupIds.length==0){
			for(Map<K,V> oriData:oriDatas){
				for(Map<K,V> data:datas){
					result.add(mergeMp(oriData, data));
				}
			}
		}else{
			Map<V,List<Map<K, V>>> oriIndex=groupMpLst(oriDatas, oriGroupIds);
			Map<V,List<Map<K, V>>> dIndex=groupMpLst(datas,dGroupIds);
			for(Map.Entry<V,List<Map<K,V>>> entry:oriIndex.entrySet()){
				if(entry.getKey()!=null&&!entry.getKey().equals("null"))
					result.addAll(comparemergeMpCartesianProduct(oriIndex.get(entry.getKey()),dIndex.get(entry.getKey())));
			}
		}
		return result;
	}
	/**
	 * 集合取交集
	 * @param oriDatas
	 * @param datas
	 * @return
	 */
	public static <K,V> List<Map<K,V>> compareMergeMpIntersection(List<Map<K,V>> oriDatas,List<Map<K,V>> datas){
		return compareMergeMpIntersection(oriDatas, datas,null);
	}
	@SuppressWarnings("unchecked")
	public static <K,V> List<Map<K,V>> compareMergeMpIntersection(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] groupIds){
		List<Map<K,V>> result=newArrayList();
		if(!(oriDatas==null||oriDatas.size()==0||datas==null||datas.size()==0)){
			if(!(groupIds==null||groupIds.length==0)){
				result=comparemergeMpCartesianProduct(oriDatas,datas,groupIds,groupIds);
			}else{
				if(oriDatas.get(0).keySet().containsAll(datas.get(0).keySet())){
					K[] ks=(K[]) oriDatas.get(0).keySet().toArray();
					result=comparemergeMpCartesianProduct(oriDatas,datas,ks,ks);
				}
			}
		}
		return result;
	}
	/**
	 * 集合取补集
	 * @param oriDatas
	 * @param datas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> List<Map<K,V>> compareMergeMpComplementary(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] groupIds){
		List<Map<K,V>> result=newArrayList();
		if(!(oriDatas==null||oriDatas.size()==0||datas==null||datas.size()==0)){
			if(!(groupIds==null||groupIds.length==0)){
				Map<V,Map<K,V>> oriMs=groupMpSingle(oriDatas, groupIds);
				Map<V,Map<K,V>>	ms=groupMpSingle(datas, groupIds);
				for(V key:oriMs.keySet()){
					if(!ms.keySet().contains(key)){
						result.add(oriMs.get(key));
					}
				}
				for(V key:ms.keySet()){
					if(!oriMs.keySet().contains(key)){
						result.add(ms.get(key));
					}
				}
			}else{
				if(oriDatas.get(0).keySet().containsAll(datas.get(0).keySet())){
					K[] ks=(K[]) oriDatas.get(0).keySet().toArray();
					Map<V,Map<K,V>> oriMs=groupMpSingle(oriDatas, ks);
					Map<V,Map<K,V>>	ms=groupMpSingle(datas, ks);
					for(V key:oriMs.keySet()){
						if(!ms.keySet().contains(key)){
							result.add(oriMs.get(key));
						}
					}
					for(V key:ms.keySet()){
						if(!oriMs.keySet().contains(key)){
							result.add(ms.get(key));
						}
					}
				}
			}
		}
		return result;
	}
	public static <K,V> List<Map<K,V>> compareMergeMpComplementary(List<Map<K,V>> oriDatas,List<Map<K,V>> datas){
		return compareMergeMpComplementary(oriDatas, datas,null);
	}
	
	/**
	 * 比较集合  ks 值都相同则相对
	 * @param oriData
	 * @param data
	 * @param k
	 * @return
	 */
	public static <K,V> boolean compareMp(Map<K,V> oriData,Map<K,V> data,K[] ks){
		boolean flag=true;
		for(K k:ks){
			if(!oriData.get(k).equals(data.get(k))){
				flag=false;
				break;
			}
		}
		return flag;
	}
	@SuppressWarnings("unchecked")
	public static <K,V> boolean compareMp(Map<K,V> oriData,Map<K,V> data){
		boolean flag=true;
		Set<K> oriSets=oriData.keySet();
		Set<K> dataSets=data.keySet();
		if(!oriSets.containsAll(dataSets)||!dataSets.containsAll(oriSets)){
			flag=false;
		}else{
			K[] ks=(K[]) oriSets.toArray();
			flag=compareMp(oriData, data, ks);
		}
		return flag;
	}
	
	
	//同比环比类计算
	/**
	 * format --field1$field1New= 
	 * thb-同环比 
	 * thbu-同环比增长  
	 * nvl-ori为空取data 
	 * unvl-data为空-取ori 
	 * min-取小 
	 * avg-取平均
	 * field-新增一个字段
	 * 
	 * @param oriData
	 * @param data
	 * @param format
	 * @return
	 */
	
	public static <K,V> List<Map<K,V>> compareMergeMp(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] groupIds,K[] newGroupIds,String format){
		List<Map<K,V>> result=newArrayList();
		Map<V,Map<K,V>> oriDataIndex=groupMpSingle(oriDatas,groupIds);
		Map<V,Map<K,V>> dataIndex=groupMpSingle(datas,newGroupIds);
		for(Map.Entry<V,Map<K,V>> entry:oriDataIndex.entrySet()){
			result.add(compareMergeMp(entry.getValue(),dataIndex.get(entry.getKey()),format));
		}
		return result;
	}
	public static <K,V> List<Map<K,V>> compareMergeMp(List<Map<K,V>> oriDatas,List<Map<K,V>> datas,K[] groupIds,String format){
		return compareMergeMp(oriDatas, datas, groupIds, groupIds,format);
	}
	@SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> compareMergeMp(Map<K,V> oriData,Map<K,V> data,String format){
		if(data==null||oriData==null){
			return oriData;
		}
		Map<K,V> result=newLinkedHashMap();
		result.putAll(oriData);
		Map<String,String> formats=transMapFromStr(format);
		for(Map.Entry<String,String> et:formats.entrySet()){
			String field=et.getKey();//fm 定义
			String[] fs=field.split("\\$");
			String f=fs[0];
			String returnField=fs.length==2?fs[1]:fs[0];
			result.put((K)returnField,null);
			V odV=result.get(f);
			V dV=data.get(f);
			String value=et.getValue();
			try{
				result.put((K)returnField,ValueHelper.choose(f, value, odV, dV));
			}catch(Throwable t){}
		}
		return result;
	}
	public static <K,V> Map<K,V> replaceMpValue(Map<K,V> oriData,Map<K,V> data){
		Map<K,V> result=newLinkedHashMap();
		result.putAll(oriData);
		for(Map.Entry<K, V> entry:data.entrySet()){
			if(oriData.containsKey(entry.getKey())&&entry.getValue()!=null){
				result.put(entry.getKey(),entry.getValue());
			}
		}
		return result;
	}
	public static <K,V> Map<K,V> copy(Map<K,V> oriData){
		Map<K,V> result=newLinkedHashMap();
		result.putAll(oriData);
		return result;
	}

	public static <K,V> List<Map<K,V>> copy(List<Map<K,V>> oriDatas){
		List<Map<K,V>> result=newArrayList();
		result.addAll(oriDatas);
		return result;
	}
	
	public static List<Map<String,Object>> aggregateLstMp(List<Map<String,Object>> datas,String[] groupIds,String format){
		List<Map<String,Object>> result=newArrayList();
		Map<Object,List<Map<String,Object>>> dataT=groupMpLst(datas, groupIds);
		for(Map.Entry<Object,List<Map<String,Object>>> entry:dataT.entrySet()){
			Map<String,Object> resultT=newLinkedHashMap();
			List<Map<String,Object>> value=entry.getValue();
			Map<String,Object> s1=value.get(0);
			for(String groupId:groupIds)
			resultT.put(groupId,s1.get(groupId));
			resultT.putAll(aggregateLstMp(value,format));
			result.add(resultT);
		}
		return result;
	}
    public static  Map<String,Object> aggregateLstMp(List<Map<String,Object>> datas,String format){
    	Map<String,Object> result=newLinkedHashMap();
    	Map<String,String> formats=transMapFromStr(format);
    	Map<String,Integer> keyNotEmptyCounts=newLinkedHashMap();
    	boolean hasConcat=format.contains("wm_concat");
    	Map<String,Set<Object>> concats=null;
    	if(hasConcat){
    		concats=newHashMap();
    	}
    	int isNotEmptyCount=0;
    	for(Map<String,Object> data:datas){
    		//count
    		if(!ValueHelper.isValueEmpty(data)) isNotEmptyCount++;
    		//
    		for(Map.Entry<String,String> entry:formats.entrySet()){
    			//
    			String field=entry.getKey();
    			String[] fs=field.split("\\$");
    			Object value=data.get(fs[0]);
    			String returnField=fs.length==2?fs[1]:fs[0];
    			if(!result.containsKey(returnField)){
    				result.put(returnField,null);
    				keyNotEmptyCounts.put(returnField, 0);
    			}
    			//函数操作
    			if(value!=null){
    				keyNotEmptyCounts.put(returnField,(keyNotEmptyCounts.get(returnField)==null)?1:(keyNotEmptyCounts.get(returnField)+1));
    			}
    			String func=formats.get(field);
    			String f1=func;
    			if(f1.equalsIgnoreCase("sum")){
    				try{
    					if(value!=null){
    						Double newV=Double.parseDouble(String.valueOf(value));
    						Object oldV=result.get(returnField);
    						if(oldV==null){
    							result.put(returnField,newV);
    						}else{
    							result.put(returnField,newV+(Double)(oldV));
    						}
    					}
    				}catch(Throwable t){
    					//t.printStackTrace();
    					}
    			}else if(f1.equalsIgnoreCase("avg")){
    				try{
    					if(value!=null){
    						Double newV=Double.parseDouble(String.valueOf(value));
    						Object oldV=result.get(returnField);
    						if(oldV==null)
    							result.put(returnField,newV);
    						else
    							result.put(returnField,(newV+(Double)oldV*(keyNotEmptyCounts.get(fs[0])-1))/keyNotEmptyCounts.get(fs[0]));
    					}
    				}catch(Throwable t){
    				//	t.printStackTrace();
    					}
    			}else if(f1.equalsIgnoreCase("max")){
    				try{
    					if(value!=null){
    						Object newV=value;
    						Object oldV=result.get(returnField);
    						if(oldV==null)
    							result.put(returnField,newV);
    						else{
    						   if(newV instanceof String){
    							  if(String.valueOf(newV).compareTo(String.valueOf(oldV))>0){
    								  result.put(returnField,newV);
    							  }
    						   }else if(newV.getClass().getSimpleName().toLowerCase().contains("date")||newV.getClass().getSimpleName().toLowerCase().contains("time")){
    							   if(((Date)newV).after((Date)oldV)){
    								   result.put(returnField,newV);
    							   }
    						   }else{
    							   if(Double.parseDouble(String.valueOf(newV))>Double.parseDouble(String.valueOf(oldV))){
    								   result.put(returnField, newV);
    							   }
    						   }
    						}
    						
    					}
    				}catch(Throwable t){
    				//	t.printStackTrace();
    					}
    			}else if(f1.equalsIgnoreCase("min")){
    				try{
    					if(value!=null){
    						Object newV=value;
    						Object oldV=result.get(returnField);
    						if(oldV==null)
    							result.put(returnField,newV);
    						else{
    						   if(newV instanceof String){
    							  if(String.valueOf(newV).compareTo(String.valueOf(oldV))<0){
    								  result.put(returnField,newV);
    							  }
    						   }else if(newV.getClass().getSimpleName().toLowerCase().contains("date")||newV.getClass().getSimpleName().toLowerCase().contains("time")){
    							   if(((Date)newV).before((Date)oldV)){
    								   result.put(returnField,newV);
    							   }
    						   }else{
    							   if(Double.parseDouble(String.valueOf(newV))<Double.parseDouble(String.valueOf(oldV))){
    								   result.put(returnField, newV);
    							   }
    						   }
    						}
    						
    					}
    				}catch(Throwable t){
    				//	t.printStackTrace();
    					}
    			}else if(f1.equalsIgnoreCase("count")){
    				result.put(returnField,isNotEmptyCount);
    			}else if(f1.equalsIgnoreCase("counts")){
    				result.put(returnField,keyNotEmptyCounts.get(returnField));
    			}else if(f1.equalsIgnoreCase("wm_concat")){
    				if(concats.get(returnField)==null){
    					concats.put(returnField,new TreeSet<Object>());
    				}
    				if(value!=null)
    				concats.get(returnField).add(value);
    				result.put(returnField, concats.get(returnField));
    			}
    		}
    		
    	}
    	return result;
    }
    public static <K,V> Map<K,V> buildMp(K[] ks,V[] vs){
    	Map<K,V> result=newLinkedHashMap();
    	for(int i=0;i<ks.length;i++){
    		result.put(ks[i],vs[i]);
    	}
    	return result;
    }
    public static <K,V> Map<K,V> buildMp(List<V> data, K[] ks){
    	Map<K,V> result=newLinkedHashMap();
    	if(data.size()==ks.length){
	    	for(int i=0;i<ks.length;i++){
	    		result.put(ks[i],data.get(i));
	    	}
    	}
    	return result;
    }
    //根时间相关操作
    public static List<Map<String,String>> buildTimeLst(String startTime,String endTime,String format,int minutes,String name){
    	return buildTimeLst(DateTools.parse(startTime),DateTools.parse(endTime),format, minutes, name);
    }
    public static List<Map<String,String>> buildTimeLst(Date startTime,Date endTime,String format,int minutes,String name){
    	List<Map<String,String>> result=newArrayList();
    	List<String> times=DateTools.buildTime(startTime, endTime, format, minutes);
    	for(String time:times){
    		Map<String,String> rt=newLinkedHashMap();
    		rt.put(name,time);
    		result.add(rt);
    	}
    	return result;
    }
    //get操作
	public static <V> V get(Map<String,V> data,String k,V defaultValue){
    	return data.get(k)==null?defaultValue:data.get(k);
    }
	public static <V> Double getDouble(Map<String,V> data,String k){
		return getDouble(data, k, null);
	}
	public static <V> Double getDouble(Map<String,V> data,String k,Double defaultValue){
		V v=get(data, k, null);
		if(v==null){
			return defaultValue;
		}else{
			return Double.parseDouble(String.valueOf(v));
		}
	}
	public static <V> String getString(Map<String,V> data,String k){
		return getString(data, k,null);
	}
	public static <V> String getString(Map<String,V> data,String k,String defaultValue){
		V v=get(data, k, null);
		if(v==null){
			return defaultValue;
		}else{
			return String.valueOf(v);
		}
	}
	public static <V> Integer getInt(Map<String,V> data,String k){
		return getInt(data, k,null);
	}
	public static <V> Integer getInt(Map<String,V> data,String k,Integer defaultValue){
		V v=get(data, k, null);
		if(v==null){
			return defaultValue;
		}else{
			return Integer.parseInt(String.valueOf(v));
		}
	}
	public static <V> Long getLong(Map<String,V> data,String k){
		return getLong(data, k,null);
	}
	public static <V> Long getLong(Map<String,V> data,String k,Long defaultValue){
		V v=get(data, k, null);
		if(v==null){
			return defaultValue;
		}else{
			return Long.parseLong(String.valueOf(v));
		}
	}
	
	public static interface Builder<T>{
		public void build(T data);
	}
	public static <V> Float getFloat(Map<String,V> data,String k){
		return getFloat(data, k,null);
	}
	public static <V> Float getFloat(Map<String,V> data,String k,Float defaultValue){
		V v=get(data, k, null);
		if(v==null){
			return defaultValue;
		}else{
			return Float.parseFloat(String.valueOf(v));
		}
	}
	public static <K,V> Object[] toArrayObj(Map<K,V> data,K[] ks){
		Object[] result=new Object[ks.length];
		for(int i=0;i<ks.length;i++){
			result[i]=data.get(ks[i]);
		}
		return result;
	}
	public static <K> String[] toArrayStr(Map<K,String> data,K[] ks){
		String[] result=new String[ks.length];
		for(int i=0;i<ks.length;i++){
			result[i]=data.get(ks[i]);
		}
		return result;
	}
	public static <K,V> List<Object[]> toArrayObj(List<Map<K,V>> datas,K[] ks){
		List<Object[]> result=newArrayList();
		for(Map<K,V> data:datas){
			result.add(toArrayObj(data, ks));
		}
		return result;
	}
	public static <K> List<String[]> toArrayStr(List<Map<K,String>> datas,K[] ks){
		List<String[]> result=newArrayList();
		for(Map<K,String> data:datas){
			result.add(toArrayStr(data, ks));
		}
		return result;
	}
	


	public static Map<String,Object> reBuilderMap(Map<String,Object> data,String fields[]){
		Map<String,Object> dt=new LinkedHashMap<String,Object>();
		for(String field:fields){
			String[] fs=field.split("@");
			dt.put(fs[0],handerValue(field,null,data));
		}
		return dt;
	}
	public static Object handerValue(String field,String ori, Map<String,Object> data){
		String[] fs=field.split("@");
		Object val=data.get(ori==null?fs[0]:ori);
		if(val!=null&&val.getClass().getSimpleName().toLowerCase().contains("timestamp")){
			val=DateTools.sdf_mi.format(val);
		}
		if(fs.length==1){
			return val;
		}else if(fs.length==2){
			String mark=fs[1].substring(0,fs[1].indexOf("("));
			String two=fs[1].substring(mark.length()+2,fs[1].length()-2);
			if(mark.equalsIgnoreCase("date")){//常规时间处理
				return DateTools.getFormatTime(DateTools.parse(String.valueOf(val)),two);
			}else if(mark.equalsIgnoreCase("wdate")){
				String[] twos=two.split(",");
				return DateTools.getFormatTime(DateTools.getFormatTime(val==null?null:String.valueOf(val),twos[0]),twos[1]);
			}
		}
		return val;
	}
	
	public static String getBiaoShi(String[] strs,int[] match){
		String temp="";
		for(int i:match){
			temp+=strs[i];
		}
		return temp;
	}
	
	public static List<String[]> trans2Array(List<Map<String, Object>> datas,
			String[] clumnFields) {
		List<String[]> ds=new ArrayList<String[]>();
		for(Map<String,Object> data:datas){
			String[] d=new String[clumnFields.length];
			for(int i=0;i<clumnFields.length;i++){
				Object val=data.get(clumnFields[i]);
				d[i]=String.valueOf(val==null?"":val);
			}
			ds.add(d);
		}
		return ds;
	}
	public static <K,V>  void sort(List<Map<K,V>> datas,final String key,final String order){
		Collections.sort(datas,new Comparator<Map<K,V>>(){
			public int compare(Map<K, V> o1, Map<K, V> o2) {
			 try{
					Object v1=o1.get(key);
					Object v2=o2.get(key);
					if(order.equals("asc")){
						if(v1==null){
							return 1;
						}
						if(v2==null){
							return -1;
						}
					}else{
						if(v1==null){
							return 1;
						}
						if(v2==null){
							return -1;
						}
					}
					if(RegexUtils.isNumber(v1)&&RegexUtils.isNumber(v2)){
						double d1=Double.parseDouble(""+v1);
						double d2=Double.parseDouble(""+v2);
						if(order.equals("asc")){
							if(d1>d2){
								return 1;
							}else if(d1<d2){
								return -1;
							}else{
								return 0;
							}
						}else{
							if(d1<d2){
								return 1;
							}else if(d1>d2){
								return -1;
							}else{
								return 0;
							}
						}
				 }else{
					 if(order.equals("asc"))
						 return String.valueOf(v1).compareTo(String.valueOf(v2));
					 else
						 return -String.valueOf(v1).compareTo(String.valueOf(v2));
				 }
			 }catch(Exception e){
			 }
			 return 0;
			}});
	}

	public static <T> Map<String,T> sort(Map<String,T> data){
		Map<String,T> result=new LinkedHashMap<String,T>();
		if(data==null||data.isEmpty()){
			return result;
		}
		Set<String> sets=data.keySet();
		HashMap<String,Integer> map = new HashMap<String,Integer>();  
		ValueComparator vc=new ValueComparator(map);
		Map<String,Integer> sorted_map=new TreeMap<String,Integer>(vc);
		for(String set:sets){
			map.put(set,set==null?0:set.length());
		}
		sorted_map.putAll(map);
		for(Map.Entry<String,Integer> entry:sorted_map.entrySet()){
			result.put(entry.getKey(),data.get(entry.getKey()));
		}
		return result;
	}
	static class ValueComparator implements Comparator<String> {  
		    Map<String, Integer> base;  
		    public ValueComparator(Map<String, Integer> base) {  
		        this.base = base;  
		    }  
		    public int compare(String a, String b) {  
		        if (base.get(a) >= base.get(b)) {  
		            return -1;  
		       } else {  
		           return 1;  
		       } 
		    }  
		}  
	/**
	 * 参数 --a=2 --b=1 转化为map对象
	 * @param args
	 */
	public static Map<String,String> transMapFromStr(String paramsStr){
		if(!paramsStr.startsWith(" ")){
			paramsStr=" "+paramsStr;
			paramsStr=paramsStr.replaceAll("\t|\r|\n"," ");
		}
		Map<String,String> result=new LinkedHashMap<String,String>();
		String[] ps=paramsStr.split(" --");
		for(String p:ps){
			String[] eqs=p.split("=");
			if(eqs.length==2)
			result.put(eqs[0],eqs[1].trim());
			else if(eqs.length>2){
				String sb=p.substring(eqs[0].length()+1,p.length());
				result.put(eqs[0],sb.trim());
			}
			
		}
		return result;
	}
	/**
	 * 创建hashMap对象
	 * @return hashmap
	 */
	public static <K,V> Map<K,V> newHashMap(){
		return new HashMap<K,V>();
	}
	public static <K,V> Map<K,V> newConcurrentHashMap(){
		return new ConcurrentHashMap<K, V>();
	}
	/**
	 * 创建linkedHashMap对象
	 * @param args
	 */
	public static <K,V> Map<K,V> newLinkedHashMap(){
		return new LinkedHashMap<K,V>();
	}
	/**
	 * 创建ArrayList
	 * @return
	 */
	public static <T> List<T> newArrayList(){
		return new ArrayList<T>();
	}
	public static <V> List<Map<String,V>> toLowerCaseForKey(List<Map<String,V>> datas){
		List<Map<String,V>> result=newArrayList();
		for(Map<String,V> data:datas){
			result.add(toLowerCaseForKey(data));
		}
		return result;
	}
	public static <V> Map<String,V> toLowerCaseForKey(Map<String,V> data){
		Map<String,V> result=newLinkedHashMap();
		for(Map.Entry<String, V> entry:data.entrySet()){
			result.put(entry.getKey().toLowerCase(),entry.getValue());
		}
		return result;
	}
	/**
	 * 对树的操作，简单树转复杂树
	 */
	public static List<Map<String,Object>> createTree(List<Map<String,Object>> datas,String id,String pid,String children,String rootId){
		Map<String,Map<String,Object>> dts=newHashMap();
		Map<String,List<Map<String,Object>>> ddts=newHashMap();
		for(Map<String,Object> data:datas){
			dts.put(String.valueOf(data.get(id)), data);
			if(!ddts.containsKey(data.get(pid))){
				ddts.put(String.valueOf(data.get(pid)),new ArrayList<Map<String,Object>>());
			}
			ddts.get(data.get(pid)).add(data);
		}
		if(rootId==null){
			for(Map.Entry<String,List<Map<String,Object>>> entry:ddts.entrySet()){
				if(dts.get(entry.getKey())==null){
					rootId=entry.getKey();
					break;
				}
			}
		}
		List<Map<String,Object>> result=ddts.get(rootId);
		if(result!=null)
		recursion(id,pid,children,result,dts,ddts);
		return result;
	}
	public static void recursion(String id,String pid,String children,List<Map<String,Object>> datas,Map<String,Map<String,Object>> dts,Map<String,List<Map<String,Object>>> pdts){
		for(Map<String,Object> data:datas){
			String idt=String.valueOf(data.get(id));
			String pidt=String.valueOf(data.get(pid));
			if(pdts.containsKey(pidt)){
				List<Map<String,Object>> subPdts =pdts.get(idt);
				if(subPdts!=null&&subPdts.size()>0){
					data.put(children,subPdts);
					recursion(id,pid,children,subPdts,dts,pdts);
				}
			}
		}
	}
	public static class ValueHelper{
		@SuppressWarnings("unchecked")
		public static <V> V rebuildMpHandlerValue(String key,V value){
	    		if(value==null)return value;
	    		try{
					String mark=key.substring(0,key.indexOf("("));
					String two=key.substring(mark.length()+2,key.length()-2);
					if(mark.equalsIgnoreCase("date")){//常规时间处理 @date('yyyy-mm-dd')
						value=(V)DateTools.getFormatTime(DateTools.parse(String.valueOf(value)),two);
					}else if(mark.equalsIgnoreCase("wdate")){//@wdate('yyyyww,yyyy-ww')
						String[] twos=two.split(",");
						value=(V)DateTools.getFormatTime(DateTools.getFormatTime(value==null?null:String.valueOf(value),twos[0]),twos[1]);
					}else if(mark.equalsIgnoreCase("round")){
						value=(V)new DecimalFormat(two).format(value);
					}else if(mark.equalsIgnoreCase("ceil")){
						value=(V)(Object)Math.ceil(Double.parseDouble(String.valueOf(value)));
					}else if(mark.equalsIgnoreCase("floor")){
						value=(V)(Object)Math.floor(Double.parseDouble(String.valueOf(value)));
					}
	    		}catch(Throwable t){}
	    	return value;
	    }
		 public static <V> boolean matcherOperater(V v,String operater,String value){
		    	boolean flag=false;
		    	try{
			    	if(operater.equals("le")||operater.equals("<=")){
			    		flag=v!=null&&(Double.parseDouble(String.valueOf(v))<=Double.parseDouble(value));
			    	}else if(operater.equals("lt")||operater.equals("<")){
			    		flag=v!=null&&(Double.parseDouble(String.valueOf(v))<Double.parseDouble(value));
			    	}else if(operater.equals("ge")||operater.equals(">=")){
			    		flag=v!=null&&(Double.parseDouble(String.valueOf(v))>=Double.parseDouble(value));
			    	}else if(operater.equals("gt")||operater.equals(">")){
			    		flag=v!=null&&(Double.parseDouble(String.valueOf(v))>Double.parseDouble(value));
			    	}else if(operater.equals("eq")||operater.equals("=")||operater.equals("==")){
			    		flag=(String.valueOf(v).equals(value));
			    	}else if(operater.equals("like")||operater.equals("contain")){
			    		flag=v!=null&&(String.valueOf(v).contains(value));
			    	}else if(operater.equals("nlike")){
			    		flag=v!=null&&!(String.valueOf(v).contains(value));
			    	}else if(operater.equals("ilike")){
			    		flag=v!=null&&(String.valueOf(v).toLowerCase().contains(value.toLowerCase()));
			    	}else if(operater.equals("nilike")){
			    		flag=v!=null&&!(String.valueOf(v).toLowerCase().contains(value.toLowerCase()));
			    	}else if(operater.equals("ne")||operater.equals("!=")){
			    		flag=!(String.valueOf(v).equals(value));
			    	}else if(operater.equals("is")){
			    		flag=(v==null&&value.equals("null"))||(String.valueOf(v).trim().length()==0&&value.equals("empty"))
			    				||(v!=null&&value.equalsIgnoreCase("notEmpty"));
			    	}else if(operater.equals("in")){
			    		flag=v!=null&&(Arrays.asList(value.trim().split(",")).contains(String.valueOf(v)));
			    	}else if(operater.equalsIgnoreCase("notIn")){
			    		flag=v!=null&&(!Arrays.asList(value.trim().split(",")).contains(String.valueOf(v)));
			    	}
		    	}catch(Throwable t){}
		    	return flag;
		    }
		 @SuppressWarnings("unchecked")
		public static<V> V choose(String field,String value,V odV,V dV){
			 V result=null;
			  if(value.equalsIgnoreCase("thb")&&odV!=null&&dV!=null){
					Double ov=Double.parseDouble(String.valueOf(odV));
					Double v=Double.parseDouble(String.valueOf(dV));
					Object rt=dcmFmt.format((v/ov)*100);
					result= (V)rt;
				}else if(value.equalsIgnoreCase("thbu")&&odV!=null&&dV!=null){
					Double ov=Double.parseDouble(String.valueOf(odV));
					Double v=Double.parseDouble(String.valueOf(dV));
					Object rt=dcmFmt.format(((v-ov)/ov)*100);
					result=(V)rt;
				}else if(value.equalsIgnoreCase("nvl")){
					result=odV==null?dV:odV;
				}else if(value.equalsIgnoreCase("unvl")){
					result=odV==null?odV:dV;
				}else if(value.equalsIgnoreCase("max")||value.equalsIgnoreCase("min")){
					if(odV==null){
						if(dV!=null){
							result=dV;
						}
					}else{
						if(dV!=null){
							if(dV instanceof String) {
								if(String.valueOf(dV).compareTo(String.valueOf(odV))>0)
									result=value.equals("max")?dV:odV ;
								else
									result=value.equals("max")?odV:dV;
							}else{
								if(Double.parseDouble(String.valueOf(odV))<Double.parseDouble(String.valueOf(dV)))
									result=value.equals("max")?dV:odV;
								else
									result=value.equals("max")?odV:dV;
							}
							
						}
					}
				}else if(value.equalsIgnoreCase("avg")){
					result=(V)(Object)(odV==null&&dV==null?null:(odV==null?Double.parseDouble(String.valueOf(dV)):((Double.parseDouble(String.valueOf(odV))+Double.parseDouble(String.valueOf(dV)))/2)));
				}else if(value.equalsIgnoreCase("sum")){
					result=(V)(Object)(odV==null&&dV==null?null:(odV==null?Double.parseDouble(String.valueOf(dV)):((Double.parseDouble(String.valueOf(odV))+Double.parseDouble(String.valueOf(dV))))));
				}else if(value.equals(field)){
					result=dV;
				}
			  return result;
		 }
		 public static <K,V> boolean isValueEmpty(Map<K,V> data){
		    	boolean flag=true;
		    	if(data!=null){
		    		for(Map.Entry<K, V> entry:data.entrySet()){
		    			flag=entry.getValue()==null;
		    			if(!flag){
		    				break;
		    			}
		    		}
		    	}
		    	return flag;
		    }
	}
	
}
