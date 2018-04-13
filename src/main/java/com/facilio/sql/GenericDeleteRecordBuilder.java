package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.transaction.FacilioConnectionPool;

public class GenericDeleteRecordBuilder {
	private String tableName;
	private WhereBuilder where = new WhereBuilder();

	public GenericDeleteRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public GenericDeleteRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	public GenericDeleteRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	public GenericDeleteRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	public GenericDeleteRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	public GenericDeleteRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	public GenericDeleteRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}
	
	public int delete() throws SQLException {
		checkForNull();
		PreparedStatement pstmt = null;
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
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
		StringBuilder sql = new StringBuilder("DELETE ");
		sql.append(tableName)
			.append(" FROM ")
			.append(tableName)
			.append(" WHERE ")
			.append(where.getWhereClause());
		
		return sql.toString();
	}
	
	private void checkForNull() {
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		where.checkForNull();
		
	}
}
