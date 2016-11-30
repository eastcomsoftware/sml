package com.eastcom_sw.sml.jdbc;

import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;


public abstract class JdbcTemplate extends JdbcAccessor{
	public JdbcTemplate(){
		
	}
	public JdbcTemplate(DataSource dataSource){
		this.dataSource=dataSource;
	}
	public abstract int update(String sql,Object... params);
	public  abstract int update(String sql);
	public abstract int update(List<String> sqls,List<Object[]> objs);
	public abstract int update(List<String> sqls);
	public abstract int[] batchUpdate(String sql,List<Object[]> objs);
	public abstract int[] batchUpdate(String sql,BatchPreparedStatementSetter bs);
	public abstract <T> T query(String sql, Object[] params, ResultSetExtractor<T> rset);
	public abstract <T> T query(String sql,ResultSetExtractor<T> rset,Object ...params);
	public abstract <T> List<T> query(String sql,Object[] params,RowMapper<T> rowMapper);
	public abstract <T> List<T> query(String sql,RowMapper<T> rowMapper,Object... params);
	public abstract <T> T queryForObject(String sql,Object[] params,RowMapper<T> rowMapper);
	public abstract <T> T queryForObject(String sql,RowMapper<T> rowMapper,Object... params);
	public abstract int queryForInt(String sql,Object... params);
	public abstract int queryForInt(String sql);
	public abstract long queryForLong(String sql,Object... params) ;
	public abstract long queryForLong(String sql);
	public abstract Map<String,Object> queryForMap(String sql,Object... params);
	public abstract Map<String,Object> queryForMap(String sql);
	public abstract List<Map<String,Object>> queryForList(String sql,Object... params);
	public abstract List<Map<String,Object>> queryForList(String sql);
	public abstract <T> T queryForObject(String sql,Object[] params,Class<T> clazz);
	public abstract <T> T queryForObject(String sql,Class<T> clazz,Object... params);
	public abstract <T> T queryForObject(String sql,Class<T> clazz);
	public abstract <T> List<T> queryForList(String sql,Object[] params,Class<T> clazz);
	public abstract <T> List<T> queryForList(String sql,Class<T> clazz,Object... params);
	public abstract <T> List<T> queryForList(String sql,Class<T> clazz);
	
	public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		String className = null;
		if (obj != null) {
			className = obj.getClass().getName();
		}
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		}
		else if (obj instanceof Clob) {
			obj = rs.getString(index);
		}
		else if (className != null &&
				("oracle.sql.TIMESTAMP".equals(className) ||
				"oracle.sql.TIMESTAMPTZ".equals(className))) {
			obj = rs.getTimestamp(index);
		}
		else if (className != null && className.startsWith("oracle.sql.DATE")) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(index);
			if ("java.sql.Timestamp".equals(metaDataClassName) ||
					"oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
				obj = rs.getTimestamp(index);
			}
			else {
				obj = rs.getDate(index);
			}
		}
		else if (obj != null && obj instanceof java.sql.Date) {
			if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
				obj = rs.getTimestamp(index);
			}
		}
		return obj;
	}
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (name == null || name.length() < 1) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}
	public static void setPreparedState(PreparedStatement ps,int paramIndex,Object inValue) throws SQLException{
		if(inValue==null){
			ps.setNull(paramIndex,Types.NULL);
		}else if (isStringValue(inValue.getClass())) {
			ps.setString(paramIndex, inValue.toString());
		}
		else if (isDateValue(inValue.getClass())) {
			ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
		}
		else if (inValue instanceof Calendar) {
			Calendar cal = (Calendar) inValue;
			ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
		}
		else {
			ps.setObject(paramIndex, inValue);
		}
	}
	private static boolean isStringValue(Class<?> inValueType) {
		return (CharSequence.class.isAssignableFrom(inValueType) ||
				StringWriter.class.isAssignableFrom(inValueType));
	}
	private static boolean isDateValue(Class<?> inValueType) {
		return (java.util.Date.class.isAssignableFrom(inValueType) &&
				!(java.sql.Date.class.isAssignableFrom(inValueType) ||
						java.sql.Time.class.isAssignableFrom(inValueType) ||
						java.sql.Timestamp.class.isAssignableFrom(inValueType)));
	}
}
