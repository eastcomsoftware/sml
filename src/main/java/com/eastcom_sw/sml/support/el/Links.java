package com.eastcom_sw.sml.support.el;

import java.util.Arrays;
import java.util.Map;

import com.eastcom_sw.sml.tools.Assert;
import com.eastcom_sw.sml.tools.MapUtils;
import com.eastcom_sw.sml.tools.RegexUtils;
/**
 * @author wen
 */
public class Links {
	/**
	 * update=>  delete-insert-update
	 * query =>  delete${1|index=2}-insert${2|groupname=2~3~4}-update${3|groupname=4~5}
	 */
	private String linkEl;
	
	private String[] opLinks;
	
	private Map<String,Map<String,String>> opLinksInfo=MapUtils.newLinkedHashMap();
	
	private String[] split=new String[]{"-","\\$","\\|"};
	
	public Links(String[] split,String linkEl){
		this.split=split;
		this.linkEl=linkEl;
	}
	public Links(String linkEl){
		this.linkEl=linkEl;
	}
	/**
	 * parse  links
	 */
	public Links parseLinks(){
		Assert.isTrue(split!=null&&split.length>0,"links split can't null or empty");
		Assert.notNull(linkEl,"links linkEl can't null");
		//--
		String[] opt=linkEl.split(split[0]);
		opLinks=new String[opt.length];
		for(int i=0;i<opt.length;i++){
			String[] ops=opt[i].split(split[1]);
			opLinks[i]=ops[0];
			Map<String,String> param=MapUtils.newLinkedHashMap();
			if(ops.length>1){
				String linkparamEl=RegexUtils.subString(ops[1],"{","}");
				if(linkparamEl==null||linkparamEl.trim().length()==0){
					param.put("classpath","0");
					continue;
				}else{
					if(split.length>=3){
						String[] linkparamEls=linkparamEl.split(split[2]);
						for(int j=0;j<linkparamEls.length;j++){
							String[] ps=linkparamEls[j].split("=");
							if(ps.length>1){
								param.put(ps[0],ps[1]);
							}
						}
					}
				}
			}
			opLinksInfo.put(ops[0],param);
		}
		return this;
	}
	//--
	public String getLinkEl() {
		return linkEl;
	}

	public void setLinkEl(String linkEl) {
		this.linkEl = linkEl;
	}
	public String[] getSplit() {
		return split;
	}
	public void setSplit(String[] split) {
		this.split = split;
	}
	
	public String[] getOpLinks() {
		return opLinks;
	}
	public void setOpLinks(String[] opLinks) {
		this.opLinks = opLinks;
	}
	
	public Map<String, Map<String, String>> getOpLinksInfo() {
		return opLinksInfo;
	}
	public void setOpLinksInfo(Map<String, Map<String, String>> opLinksInfo) {
		this.opLinksInfo = opLinksInfo;
	}
	public static void main(String[] args) {
		String[] links=new Links("delete-insert-update").parseLinks().getOpLinks();
		System.out.println(Arrays.asList(links));
		System.out.println(new Links("delete${classpath=1|index=2}-insert${2|groupname=2~3~4}-update${3|groupname=4~5}").parseLinks().getOpLinksInfo());
	}

	
}
