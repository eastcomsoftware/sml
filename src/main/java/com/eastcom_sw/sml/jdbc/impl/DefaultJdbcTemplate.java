package com.eastcom_sw.sml.jdbc.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.eastcom_sw.sml.jdbc.BatchPreparedStatementSetter;
import com.eastcom_sw.sml.jdbc.DataSourceUtils;
import com.eastcom_sw.sml.jdbc.JdbcTemplate;
import com.eastcom_sw.sml.jdbc.ResultSetExtractor;
import com.eastcom_sw.sml.jdbc.RowMapper;
import com.eastcom_sw.sml.tools.Assert;
import com.eastcom_sw.sml.tools.ClassUtil;
import com.eastcom_sw.sml.tools.MapUtils;

public class DefaultJdbcTemplate extends JdbcTemplate  {
		public DefaultJdbcTemplate(){}
		public DefaultJdbcTemplate(DataSource dataSource){
			super(dataSource);
		}
		public int update(String sql,Object... params){
			Connection con=null;
			PreparedStatement pst = null;
			int result=0;
			try{
				con =DataSourceUtils.getConnection(getDataSource());
				pst = con.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						setPreparedState(pst, i+1,params[i]);
					}
				}
				result=pst.executeUpdate();
			}catch(SQLException  e){
				e.printStackTrace();
			}finally{
				try{
					if(pst!=null){
						pst.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					DataSourceUtils.releaseConnection();
				}
			}
			return result;
		}
		public int[] batchUpdate(String sql,List<Object[]> objs){
			Connection con=null;
			PreparedStatement pst = null;
			try {
				con=DataSourceUtils.getConnection(getDataSource());
				con.setAutoCommit(false);
				pst=con.prepareStatement(sql);
				for(int i=0;i<objs.size();i++){
					Object[] params=objs.get(i);
					for(int j=0;j<params.length;j++){
						setPreparedState(pst, j+1, params[j]);
					}
					pst.addBatch();
				}
				int[] result=pst.executeBatch();
				con.commit();
				return result;
			} catch (SQLException e) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				Assert.isTrue(false, e.getMessage());
			}finally{
				try{
					if(pst!=null)
						pst.close();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					DataSourceUtils.releaseConnection();
				}
				
			}
			return null;
		}
		public <T> T query(String sql, Object[] params, ResultSetExtractor<T> rset) {
			Connection con=null;
			PreparedStatement pst = null;
			ResultSet rs=null;
			try {
				con =DataSourceUtils.getConnection(getDataSource());
				pst = con.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						setPreparedState(pst, i+1,params[i]);
					}
				}
				rs=pst.executeQuery();
				return rset.extractData(rs);
			} catch (SQLException e) {
				Assert.isTrue(false, e.getMessage());
			}finally{
				try{
					if(rs!=null)
					rs.close();
					if(pst!=null)
					pst.close();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					DataSourceUtils.releaseConnection();
				}
			}
			
			return null;
		}
		public int update(String sql){
			return update(sql,new Object[]{});
		}
		public <T> T queryForObject(String sql,Object[] params,RowMapper<T> rowMapper){
			List<T> result=query(sql, params, rowMapper);
			if(result.size()==0){
				Assert.isTrue(false,"not exists objects");
			}
			if(result.size()>1){
				Assert.isTrue(false,"has more objects");
			}
			return result.get(0);
		}
		public int queryForInt(String sql,Object... params){
			return queryForObject(sql, params, Integer.class);
		}
		public long queryForLong(String sql,Object... params) {
			return queryForObject(sql, params, Long.class);
		}
		public Map<String,Object> queryForMap(String sql,Object... params){
			return queryForObject(sql, params, new MapRowMapper());
		}
		public List<Map<String,Object>> queryForList(String sql,Object... params){
			return query(sql, params,new MapRowMapper());
		}
		@SuppressWarnings("unchecked")
		public <T> T queryForObject(String sql,Object[] params,Class<T> clazz){
			Map<String,Object> result=queryForMap(sql, params);
			return (T) ClassUtil.convertValueToRequiredType(result.get(result.keySet().iterator().next()),clazz);
		}
		@SuppressWarnings("unchecked")
		public <T> List<T> queryForList(String sql,Object[] params,Class<T> clazz){
			List<Map<String,Object>> trs=queryForList(sql, params);
			List<T> result=MapUtils.newArrayList();
			for(Map<String,Object> tr:trs){
				result.add((T)ClassUtil.convertValueToRequiredType(tr.get(tr.keySet().iterator().next()),clazz));
			}
			return result;
		}
		public void execute(String sql) throws SQLException{
			execute(sql,null);
		}
		public void execute(String sql,Object[] params){
			update(sql, params);
		}
		public <T> List<T> query(String sql,Object[] params,RowMapper<T> rowMapper){
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs=null;
			List<T> result=MapUtils.newArrayList();
			try{
				con = DataSourceUtils.getConnection(getDataSource());
				stmt=con.prepareStatement(sql);
				if(params!=null){
					for(int i=0;i<params.length;i++){
						setPreparedState(stmt, i+1,params[i]);
					}
				}
				rs=stmt.executeQuery();
				int i=0;
				while(rs.next()){
					T t=rowMapper.mapRow(rs,i++);
					result.add(t);
				}
				return result;
			}catch(SQLException e){
				e.printStackTrace();
				Assert.isTrue(false, e.getMessage());
			}finally{
				try{
					if(rs!=null)
						rs.close();
					if(stmt!=null)
						stmt.close();
				}catch(Exception e){}finally{
					DataSourceUtils.releaseConnection();
				}
			}
			return result;
		}
		class MapRowMapper implements  RowMapper<Map<String,Object>> {
			public Map<String, Object> mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				Map<String, Object> mapOfColValues = MapUtils.newLinkedHashMap();
				for (int i = 1; i <= columnCount; i++) {
					String key = lookupColumnName(rsmd, i);
					Object obj = getResultSetValue(rs, i);
					mapOfColValues.put(key, obj);
				}
				return mapOfColValues;
			}
			
		}
		public int[] batchUpdate(String sql, BatchPreparedStatementSetter bs) {
			Connection con=null;
			PreparedStatement pst = null;
			try {
				con=DataSourceUtils.getConnection(getDataSource());
				con.setAutoCommit(false);
				pst=con.prepareStatement(sql);
				for(int i=0;i<bs.getBatchSize();i++){
					bs.setValues(pst, i);
					pst.addBatch();
				}
				int[] result=pst.executeBatch();
				con.commit();
				return result;
			} catch (SQLException e) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				Assert.isTrue(false, e.getMessage());
			}finally{
				try{
					if(pst!=null)
						pst.close();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					DataSourceUtils.releaseConnection();
				}
			}
			return null;
		}
		public int queryForInt(String sql) {
			return queryForInt(sql,new Object[]{});
		}
		public long queryForLong(String sql) {
			return queryForLong(sql,new Object[]{});
		}
		public Map<String, Object> queryForMap(String sql) {
			return queryForMap(sql, new Object[]{});
		}
		public List<Map<String, Object>> queryForList(String sql) {
			return queryForList(sql,new Object[]{});
		}
		
		public <T> List<T> queryForList(String sql, Class<T> clazz) {
			return queryForList(sql,null, clazz);
		}
		public <T> T queryForObject(String sql, Class<T> clazz) {
			return queryForObject(sql,null,clazz);
		}
		public int update(List<String> sqls){
			return update(sqls,null);
		}
		public int update(List<String> sqls, List<Object[]> objs) {
			Connection con=null;
			PreparedStatement pst = null;
			if(objs!=null)
			Assert.isTrue(sqls.size()==objs.size(),"sqls size["+sqls.size()+"] != objs size["+objs.size()+"]");
			int result=0;
			try {
				con=DataSourceUtils.getConnection(getDataSource());
				con.setAutoCommit(false);
				for(int i=0;i<sqls.size();i++){
					pst=con.prepareStatement(sqls.get(i));
					if(objs!=null){
						Object[] params=objs.get(i);
						if(params!=null){
							for(int j=0;j<params.length;j++){
								setPreparedState(pst,j+1,params[j]);
							}
						}
					}
					result+=pst.executeUpdate();
				}
				con.commit();
				return result;
			}catch(Exception e){
				try {
					con.rollback();
					result=0;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				Assert.isTrue(false, e.getMessage());
			}finally{
				try {
					if(pst!=null){
						pst.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}finally{
					DataSourceUtils.releaseConnection();
				}
			}
			return result;
		}
		public <T> T query(String sql, ResultSetExtractor<T> rset,
				Object... params) {
			return query(sql, params, rset);
		}
		public <T> List<T> query(String sql, RowMapper<T> rowMapper,
				Object... params) {
			return query(sql, params, rowMapper);
		}
		public <T> T queryForObject(String sql, RowMapper<T> rowMapper,
				Object... params) {
			return queryForObject(sql, rowMapper, params);
		}
		public <T> T queryForObject(String sql, Class<T> clazz,
				Object... params) {
			return queryForObject(sql, clazz, params);
		}
		public <T> List<T> queryForList(String sql, Class<T> clazz,
				Object... params) {
			return queryForList(sql, clazz, params);
		}
	 
}
