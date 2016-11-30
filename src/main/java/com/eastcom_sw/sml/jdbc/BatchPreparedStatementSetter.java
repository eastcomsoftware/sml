package com.eastcom_sw.sml.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchPreparedStatementSetter {
	void setValues(PreparedStatement ps, int i) throws SQLException;
	int getBatchSize();
}