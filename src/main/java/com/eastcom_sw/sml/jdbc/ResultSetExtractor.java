package com.eastcom_sw.sml.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetExtractor<T> {
	T extractData(ResultSet rs) throws SQLException;
}
