
package com.facilio.sql;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.transaction.FacilioConnectionPool;

public class GenericDeleteRecordBuilder implements DeleteBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericDeleteRecordBuilder.class.getName());
	private String tableName;
	private FacilioField orgIdField = null;
	private WhereBuilder where = new WhereBuilder();
	private WhereBuilder oldWhere = null;
	private StringBuilder joinBuilder = new StringBuilder();
	private StringJoiner tablesToBeDeleted = new StringJoiner(",");
	private Connection conn = null;
	private static Constructor constructor;
	
	static {
		String dbClass = AwsUtil.getDBClass();
		try {
			constructor = Class.forName(dbClass + ".DeleteRecordBuilder").getConstructor(GenericDeleteRecordBuilder.class);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getTableName() {
		return tableName;
	}

	public WhereBuilder getWhere() {
		return where;
	}

	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	public StringJoiner getTablesToBeDeleted() {
		return tablesToBeDeleted;
	}

	@Override
	public GenericDeleteRecordBuilder table(String tableName) {
		this.tableName = tableName;
		tablesToBeDeleted.add(tableName);
		return this;
	}
	
	@Override
	public GenericDeleteRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}
	
	@Override
	public GenericDeleteRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	public GenericDeleteRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public GenericDeleteRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public GenericDeleteRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public GenericDeleteRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public GenericDeleteRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}
	
	@Override
	public GenericJoinBuilder innerJoin(String tableName, boolean delete) {
		return genericJoin(" INNER JOIN ", tableName, delete);
	}
	
	@Override
	public GenericJoinBuilder leftJoin(String tableName, boolean delete) {
		return genericJoin(" LEFT JOIN ", tableName, delete);
	}
	
	@Override
	public GenericJoinBuilder rightJoin(String tableName, boolean delete) {
		return genericJoin(" RIGHT JOIN ", tableName, delete);
	}
	
	@Override
	public GenericJoinBuilder fullJoin(String tableName, boolean delete) {
		return genericJoin(" FULL JOIN ", tableName, delete);
	}
	
	private GenericJoinBuilder genericJoin(String joinString, String tableName, boolean delete) {
		joinBuilder.append(joinString)
					.append(tableName)
					.append(" ");
		if (delete) {
			tablesToBeDeleted.add(tableName);
		}
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public int delete() throws SQLException {
		checkForNull();
		handleOrgId();
		PreparedStatement pstmt = null;
		
		boolean isExternalConnection = true;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isExternalConnection = false;
			}
			
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
			LOGGER.log(Level.ERROR, "Deletion failed ", e);
			throw e;
		}
		finally {
			if (isExternalConnection) {
				DBUtil.close(pstmt);
			}
			else {
				DBUtil.closeAll(conn, pstmt);
				conn = null;
			}
			
			if(orgIdField != null) {
				where = oldWhere;
			}
		}
	}
	
	private DBDeleteRecordBuilder getDBDeleteRecordBuilder() throws Exception {
		return (DBDeleteRecordBuilder) constructor.newInstance(this);
	}
	
	private String constructDeleteStatement() {
		try {
			DBDeleteRecordBuilder recordBuilder = getDBDeleteRecordBuilder();
			return recordBuilder.constructDeleteStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void checkForNull() {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		if (where.isEmpty()) {
			throw new IllegalArgumentException("Cannot delete because there's no where condition.");
		}
	}
	
	public void handleOrgId() {
		if (!DBUtil.isTableWithoutOrgId(tableName)) {
			orgIdField = DBUtil.getOrgIdField(tableName);
			
			/*WhereBuilder whereCondition = new WhereBuilder();
			Condition orgCondition = CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS);
			whereCondition.andCondition(orgCondition);
			
			oldWhere = where;
			where = whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());*/
		}
		
	}
	public static class GenericJoinBuilder implements JoinBuilderIfc<GenericDeleteRecordBuilder> {

		private GenericDeleteRecordBuilder parentBuilder;
		private GenericJoinBuilder(GenericDeleteRecordBuilder parentBuilder) {
			// TODO Auto-generated constructor stub
			this.parentBuilder = parentBuilder;
		}
		
		@Override
		public GenericDeleteRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}
		
	}
}
