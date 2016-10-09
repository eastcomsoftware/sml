package com.eastom_sw.sml;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkConstant {
	public static enum Type{
		FRAMEWORK_CFG_JDBC_SQL,
		FRAMEWORK_CFG_REPORT_SQL,
		FRAMEWORK_CFG_REPORT_DETAIL_SQL,
		FRAMEWORK_CFG_DEFAULT_BUILDER_CLASS
	}
	public static Logger logger=LoggerFactory.getLogger(FrameworkConstant.class);
	
	public static String CFG_JDBC_INFO="cfg_jdbc_info.properties";
	public static String CFG_JDBC_SQL="select id,mainsql,rebuild_info,condition_info,cache_enabled,cache_minutes,db_id  from  DM_CO_BA_CFG_RCPT_IF where id=?";
	
	public static String CFG_REPORT_SQL="select id id,rcpt_name as tablename,name description,db_id from DM_CO_BA_CFG_RCPT where id=?";
	
	public static String CFG_REPORT_DETAIL_SQL="select rcpt_id as table_id,kpi_name_en as field_name,kpi_name_ch as field_name_zn,format,field_type,id as order_index,length,for_insert,for_update,for_import,for_import_update,for_query,is_query from DM_CO_BA_CFG_RCPT_DETAIL where rcpt_id=? and enabled=1 order by id";
	
	public static String CFG_DEFAULT_BUILDER_CLASS="com.eastcom_sw.sml.core.build.lmaps";
	
	public static final String PARAM_TOLOWERCASEFORKEY="toLowerCaseForKey";
	public static final String PARAM_SQLFORMAT="formatSql";
	public static final String PARAM_IGLOG="iglog";
	public static final String PARAM_QUERYTYPE="queryType";
	
	public static String getSupportKey(String frameworkMark,Type type){
		if(type.equals(Type.FRAMEWORK_CFG_JDBC_SQL)){
			if(frameworkMark.equals("default")){
				return CFG_JDBC_SQL;
			}else{
				return properties.getProperty(frameworkMark+".CFG_JDBC_SQL");
			}
		}else if(type.equals(Type.FRAMEWORK_CFG_REPORT_SQL)){
			if(frameworkMark.equals("default")){
				return CFG_REPORT_SQL;
			}else{
				return properties.getProperty(frameworkMark+".CFG_REPORT_SQL");
			}
		}else if(type.equals(Type.FRAMEWORK_CFG_REPORT_DETAIL_SQL)){
			if(frameworkMark.equals("default")){
				return CFG_REPORT_DETAIL_SQL;
			}else{
				return properties.getProperty(frameworkMark+".CFG_REPORT_DETAIL_SQL");
			}
		}else{
			return null;
		}
	}
	
	public static Properties properties=new Properties();
	static {
		try{
			InputStream is=FrameworkConstant.class.getClassLoader().getResourceAsStream(CFG_JDBC_INFO);
			properties.load(is);
			reset("CFG_JDBC_SQL");
			reset("CFG_REPORT_SQL");
			reset("CFG_REPORT_DETAIL_SQL");
			reset("CFG_DEFAULT_BUILDER_CLASS");
		}catch(Exception e){
			logger.error("FrameworkConstant can't found, use default config !");
		}
	}
	static void reset(String key){
		String value=String.valueOf(properties.get(key));
		String defaultValue="";
		if(key.equals("CFG_JDBC_SQL")){
			defaultValue=CFG_JDBC_SQL;
		}else if(key.equals("CFG_REPORT_SQL")){
			defaultValue=CFG_REPORT_SQL;
		}else if(key.equals("CFG_REPORT_DETAIL_SQL")){
			defaultValue=CFG_REPORT_DETAIL_SQL;
		}else if(key.equals("CFG_DEFAULT_BUILDER_CLASS")){
			defaultValue=CFG_DEFAULT_BUILDER_CLASS;
		}
		if(value==null){
			logger.warn(key+" is null used default --->[{}]",defaultValue);
		}else{
			if(key.equals("CFG_JDBC_SQL")){
				CFG_JDBC_SQL=value;
			}else if(key.equals("CFG_REPORT_SQL")){
				CFG_REPORT_SQL=value;
			}else if(key.equals("CFG_REPORT_DETAIL_SQL")){
				CFG_REPORT_DETAIL_SQL=value;
			}else if(key.equals("CFG_DEFAULT_BUILDER_CLASS")){
				CFG_DEFAULT_BUILDER_CLASS=value;
			}
			logger.warn(key+" is  reset used it --->[{}]",value);
		}
	}
}
