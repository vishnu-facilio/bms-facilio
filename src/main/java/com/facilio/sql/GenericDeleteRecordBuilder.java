package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class GenericDeleteRecordBuilder {
	private String tableName;
	private WhereBuilder where = new WhereBuilder();
	private Connection conn = null;

	public GenericDeleteRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	public GenericDeleteRecordBuilder connection(Connection conn) {
		this.conn = conn;
		return this;
	}
	
	public GenericDeleteRecordBuilder where(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	public int delete() throws SQLException {
		checkForNull();
		PreparedStatement pstmt = null;
		
		try {
			String sql = constructDeleteStatement();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			int paramIndex = 1;
			Object[] whereValues = where.getValues();
			if(whereValues != null) {
				for(int i=0; i<whereValues.length; i++) {
					Object whereValue = whereValues[i];
					pstmt.setObject(paramIndex++, whereValue);
				}
			}
			
			int rowCount = pstmt.executeUpdate();
			System.out.println("Deleted "+rowCount+" records.");
			return rowCount;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private String constructDeleteStatement() {
		StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(tableName)
			.append(" WHERE ")
			.append(where.getWhereClause());
		
		return sql.toString();
	}
	
	private void checkForNull() {
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(conn == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		
		where.checkForNull();
		
	}
}
