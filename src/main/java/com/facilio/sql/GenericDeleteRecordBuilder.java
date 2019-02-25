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

/**
 * GenericDeleteRecordBuilder class helps with Deletion query in MySQL DataBase <br>
 * The MySQL DELETE query is used to delete data from the MySQL database. <br>
 *  This class and its functions helps in constructing MySQL Queries like <br>
 *
 * DELETE  FROM tbl_name [[AS] tbl_alias]<br>
 * &emsp;    [PARTITION (partition_name [, partition_name] ...)]<br>
 * &emsp;    [WHERE where_condition]<br>
 * &emsp;    [ORDER BY ...]<br>
 * &emsp;    [LIMIT row_count]
 */
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

	/**
	 * returns Where
	 * @return where
	 */
	public WhereBuilder getWhere() {
		return where;
	}

	/**
	 * returns jointBuilder
	 * @return joinBuilder
	 */
	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	/**
	 * returns tables that are to be deleted
	 * @return tablesToBeDeleted
	 */
	public StringJoiner getTablesToBeDeleted() {
		return tablesToBeDeleted;
	}

	/**
	 *
	 * adds tableNames to GenericDeleteRecordBuilder's tableName and
	 * @return GenericDeleteRecordBuilder
	 */
	public GenericDeleteRecordBuilder table(String tableName) {
		this.tableName = tableName;
		tablesToBeDeleted.add(tableName);
		return this;
	}

	/**
	 * adds conn to GenericDeleteRecordBuilder's conn and
	 * @return GenericDeleteRecordBuilder
	 */
	public GenericDeleteRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	/**
	 * adds and-whereCondition to GenericDeleteRecordBuilder's whereCondition and
	 * @return GenericDeleteRecordBuilder
	 */
	public GenericDeleteRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 * adds or-whereCondition to GenericDeleteRecordBuilder's whereCondition and
	 * @return GenericDeleteRecordBuilder
	 */
	public GenericDeleteRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 * adds and-Condition to GenericDeleteRecordBuilder's Condition and
	 * @return GenericDeleteRecordBuilder
	 */
	public GenericDeleteRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	/**
	 *  loads Where with OR-CONDITION
	 */
	public GenericDeleteRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	/**
	 *  returns Where CONDITION wiht AND
	 */

	public GenericDeleteRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	/**
	 *  returns Where CONDITION with OR
	 */
	public GenericDeleteRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'INNER JOIN' clause
	 * The MySQL INNER JOIN clause matches rows in one table with rows in other tables and allows you to query rows that contain columns from both tables
	 * @return GenericJoinBuilder
	 */
	public GenericJoinBuilder innerJoin(String tableName, boolean delete) {
		return genericJoin(" INNER JOIN ", tableName, delete);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'LEFT JOIN' clause
	 * When you join the t1 table to the t2 table using the LEFT JOIN clause, if a row from the left table t1 matches a row from the right table t2 based on the join condition ( t1.c1 = t2.c1 ), this row will be included in the result set.
	 */
	public GenericJoinBuilder leftJoin(String tableName, boolean delete) {
		return genericJoin(" LEFT JOIN ", tableName, delete);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'RIGHT JOIN' clause
	 * The RIGHT JOIN keyword returns all records from the right table (table2), and the matched records from the left table (table1). The result is NULL from the left side, when there is no match.
	 */
	public GenericJoinBuilder rightJoin(String tableName, boolean delete) {
		return genericJoin(" RIGHT JOIN ", tableName, delete);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'FULL JOIN' clause
	 * The FULL OUTER JOIN keyword return all records when there is a match in either left (table1) or right (table2) table records.
	 */
	public GenericJoinBuilder fullJoin(String tableName, boolean delete) {
		return genericJoin(" FULL JOIN ", tableName, delete);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'FULL JOIN' clause
	 * The FULL OUTER JOIN keyword return all records when there is a match in either left (table1) or right (table2) table records.
	 */
	private GenericJoinBuilder genericJoin(String joinString, String tableName, boolean delete) {
		joinBuilder.append(joinString)
					.append(tableName)
					.append(" ");
		if (delete) {
			tablesToBeDeleted.add(tableName);
		}
		return new GenericJoinBuilder(this);
	}

	/**
	 * Executes the Delete query
	 */
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

	/**
	 * creates a new instance of DBDeleteRecordBuilder
	 * @return DBDeleteRecordBuilder instance
	 * @throws Exception
	 */
	private DBDeleteRecordBuilder getDBDeleteRecordBuilder() throws Exception {
		return (DBDeleteRecordBuilder) constructor.newInstance(this);
	}

	/**
	 * constructs a Delete Query
	 * @return String
	 */
	private String constructDeleteStatement() {
		try {
			DBDeleteRecordBuilder recordBuilder = getDBDeleteRecordBuilder();
			return recordBuilder.constructDeleteStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if tablename is empty
	 */
	private void checkForNull() {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		if (where.isEmpty()) {
			throw new IllegalArgumentException("Cannot delete because there's no where condition.");
		}
	}

	/**
	 * sets orgIdField
	 */
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
