package com.eastom_sw.sml.core.resolver;

import java.util.ArrayList;
import java.util.List;

import com.eastom_sw.sml.model.SMLParams;
import com.eastom_sw.sml.support.el.El;

public class SqlResolvers {
	
	private El el;
	
	private List<SqlResolver> extResolvers=new ArrayList<SqlResolver>();
	
	private List<SqlResolver> sqlResolvers;
	public SqlResolvers(){

	}
	public SqlResolvers(El el){
		this.el=el;
	}
	public void init(){
		sqlResolvers=new ArrayList<SqlResolver>();
		sqlResolvers.add(new ParamTypeResolver());
		sqlResolvers.add(new IfSqlResolver());
		sqlResolvers.add(new SelectSqlResolver());
		sqlResolvers.addAll(extResolvers);
		sqlResolvers.add(new ParamSqlResolver());
	}
	
	public void add(SqlResolver sqlResolver){
		sqlResolvers.add(sqlResolver);
	}
	
	public synchronized Rst resolverLinks(String sql,SMLParams smlParams){
		List<Object> paramsObject=new ArrayList<Object>();
		for(SqlResolver sqlResolver:sqlResolvers){
			sqlResolver.setEl(el);
			Rst subRst=sqlResolver.resolve(null, sql,smlParams);
			sql=subRst.getSqlString();
			if(subRst.getParamObjects()!=null&&subRst.getParamObjects().size()>0){
				paramsObject.addAll(subRst.getParamObjects());
			}
		}
		return new Rst(sql,paramsObject);
	}


	public El getEl() {
		return el;
	}

	public void setEl(El el) {
		this.el = el;
	}

	public List<SqlResolver> getSqlResolvers() {
		return sqlResolvers;
	}

	public void setSqlResolvers(List<SqlResolver> sqlResolvers) {
		this.sqlResolvers = sqlResolvers;
	}

	
	public List<SqlResolver> getExtResolvers() {
		return extResolvers;
	}
	public void setExtResolvers(List<SqlResolver> extResolvers) {
		this.extResolvers = extResolvers;
	}
	
}
