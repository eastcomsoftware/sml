# sml
sql markup language(sql标记语言)

1、mybatis,ibatis书写sql的方便，但调整xml配置文件整体服务需要重新启动

2、一段完整的sql查询包括    查询sql+参数集

3、marks  `isEmpty`,`isNotEmpty`,`select`,`jdbcType`,`if`;

4、elp    jsEl,spl implements El interface;
# example
## sql

      select * from table t where 1=1 
      <isNotEmpty property="a"> and t.a=#a#</isNotEmpty>
      <isNotEmpty property="b"> and t.b in(#b#)</isNotEmpty>

##code
  
    SqlResolvers sqlResolvers=new SqlResolvers(new JsEl());
    
		sqlResolvers.init();
		
		Rst rst=sqlResolvers.resolverLinks(sql,new SMLParams().add("a","v1")
		.add("b",new String[]{"v2","v3","v4"}).reinit());
		
		System.out.println(rst.getSqlString());//print sqlString
		
		System.out.println(rst.getParamObjects());//print params
		
##result
	
	
	  sqlString:select * from table t where 1=1 and t.a=? and t.b in(?,?,?)
	  params   :[v1, v2, v3, v4]

# multi-mark example

##sql
        
    	select * from t_class tc,t_student ts 
	where tc.class_id=ts.class_id
      	<isNotEmpty property="className"> and tc.class_name like '%'||#className#||'%'</isNotEmpty>
      	<isNotEmpty property="sIds"> and ts.s_id in(#sIds#)</isNotEmpty>
	<jdbcType name="sIds" type="array-char">'200802190210'+'@value'</jdbcType>   
	//jdbcType标签 动态改变值的类型及值
	<if test=" '@classId'!='00' "> and tc.class_id=#classId#</if>
	
##code-parser

	Rst rst=sqlResolvers.resolverLinks(sql,new SMLParams().add("className","武术")
	.add("sIds","1001,1002,1003").add("classId","05").reinit());
		
##result
	
	select * from t_class tc,t_student ts where tc.class_id=ts.class_id 
	and tc.class_name like '%'||?||'%' and ts.s_id in(?,?,?) and tc.class_id=?
        [武术, 2008021902101001, 2008021902101002, 2008021902101003, 05]
