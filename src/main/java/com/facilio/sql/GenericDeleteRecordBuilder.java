package com.facilio.sql;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.fw.LRUCache;
import com.facilio.transaction.FacilioConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringJoiner;

/**
 * GenericDeleteRecordBuilder class helps in building delete queries and execute it.<br>
 * The  DELETE query is used to delete data from the  database. <br>
 *     <pre>
 * usage:<br>
 * sample table - Agent_Data
 * +-------+--------------+
 * | ID    | message      |
 * +-------+--------------+
 * | 12374 | speed entry  |
 * |  1001 | speed entry2 |
 * |   123 | speed entry3 |
 * |     2 | vijs         |
 * |     2 | vijs         |
 * +-------+--------------+
 * 	 {@code
 *      GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
 * 	                              .table("Agent_Data")
 * 	                              .andCustomWhere("ID=123");
 * 	    deleteBuilder.delete();
 * 	    }
 *     query built - DELETE Agent_Data FROM Agent_Data WHERE (ID = 123)
 *     </pre>
 */
public class GenericDeleteRecordBuilder implements DeleteBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericDeleteRecordBuilder.class.getName());
	private String tableName;
	private FacilioField orgIdField = null;
	private WhereBuilder where = new WhereBuilder();
	private WhereBuilder oldWhere = null;
	private StringBuilder joinBuilder = new StringBuilder();
	private ArrayList<String> tablesToBeDeleted = new ArrayList<>();
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

	/**
	 * Constructs a new GenericDeleteRecordBuilder instance
	 */
	public GenericDeleteRecordBuilder() {
	}

    /**
     * Returns the table in used in the builder.
     * @return the table name used in the GenericDeleteRecordBuilder, null if the table name is not set.
     */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns the where-condition used in the builder.
	 * @return the WHERE-condition used in the GenericDeleteRecordBuilder.
	 */
	public WhereBuilder getWhere() {
		return where;
	}

	/**
	 * Returns the current jointBuilder( which is a stringBuilder ), jointBuilder is used to merge two or more tables.
	 * @return the joinBuilder used in the GenericDeleteRecordBuilder, null if not set.
	 */
	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	/**
	 * Returns tables that are to be deleted, multiple tables can be deleted usind different Joins.
	 * @return the all the tables that are added to the TablesToBeDeleted.
	 */
	public ArrayList<String> getTablesToBeDeleted() {
		return tablesToBeDeleted;
	}

	/**
	 *
	 * Adds the table to the query using table's name<br>
     *     usage: <br>
     *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder().table("tablename");
     * @return GenericDeleteRecordBuilder object instance
	 */
	public GenericDeleteRecordBuilder table(String tableName) {
		this.tableName = tableName;
		tablesToBeDeleted.add(tableName);
		return this;
	}

	/**
	 * Uses the given database connection to execute the query built using GenericDeleteRecordBuilder.<br>
     *   usage:<br><pre>
     *      Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sonoo","username","password");  <br>
     *      GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder().useExternalConnection();<br>
	 *      deleteBuilder.delete();
     *          <br>
	 *              </pre>
     * to know more about Connection class </>@see <a href="https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html">Connection DOCS</a> <br>
     *
	 * @return GenericDeleteRecordBuilder instance
	 */
	public GenericDeleteRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	/**
	 * Adds a  WHERE-clause with AND operation, The WHERE clause is used to extract only those records that fulfil the specified condition <br>
     *     AND condition is similar to AND-operator and it is used to test two or more conditions in a SELECT, INSERT, UPDATE, or DELETE statement.<br>
     *        <pre>
	 *        usage:<br>
	 *             {@code
     *                     GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *                     							.table("Agent_Data")
	 *                     							.andCustomWhere("MESSAGE = 'vijs'");
	 *                     deleteBuilder.delete();
	 * 				 }
	 * 				 query built - DELETE Agent_Data FROM Agent_Data WHERE (MESSAGE = 'vijs')
	 * </pre>
	 * @return GenericDeleteRecordBuilder object instance
	 */
	public GenericDeleteRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 * Adds a custom WHERE-clause with OR operation, the WHERE clause is used to extract only those records that fulfil a specified condition <br>
     *     OR condition is similar to OR-operator and it is used to test two or more conditions in a SELECT, INSERT, UPDATE, or DELETE statement.
     * <pre>
	 * usage:<br>
	 *      {@code
     *             GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
     * 										.table("sample1")
     * 										.orCustomWhere("MESSAGE = 'speed entry'")
	 * 										.orustomWhere(" ID = '12345' ");
	 * 			   deleteBuilder.delete();
	 * 									}
	 * 		query built - DELETE Agent_Data FROM Agent_Data WHERE (MESSAGE = 'speed entry') OR ( ID = '12345' )
     * </pre>
	 * @return GenericDeleteRecordBuilder object instance
	 */
	public GenericDeleteRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 * Adds a condition to the query with AND operation <br>
	 *     usage:<br>
	 *         <pre>
	 *             {@code
	 *         Condition condition = new Condition();   {@link com.facilio.bmsconsole.criteria.Condition}
	 *         condition.setField(FieldFactory.getAgentMessageField(new FacilioModule()));
	 *         condition.setColumnName("MESSAGE");
	 *         condition.setFieldName("message");
	 *         condition.setOperator(NumberOperators.EQUALS);
	 *         condition.setValue(" 'speed entry' ");
	 *         .
	 *         .
	 *         // other Conditions
	 *         .
	 *         .
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *         							  .table("Agent_Data")
	 *         							  .andCondition(condition)
	 *        							  .andCondition(condition1)
	 *        							  .
	 *        							  .
	 *        							  .
	 *        							  ;
	 *        deleteBuilder.delete();
	 * 													}
	 * Query built -  DELETE Agent_Data FROM Agent_Data WHERE MESSAGE =  'speed entry' ..... AND  DEVICE_ID =  '1001'
	 * 													</pre>
	 * @return GenericDeleteRecordBuilder object instance
	 */
	public GenericDeleteRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	/**
	 *  Adds a condition to the query with OR operation<br>
	 *     usage:<br>
	 *         <pre>
	 *             {@code
	 *              Condition condition = new Condition();  {@link com.facilio.bmsconsole.criteria.Condition}
	 *         Condition condition = new Condition();
	 *         condition.setField(FieldFactory.getAgentMessageField(new FacilioModule()));
	 *         condition.setColumnName("MESSAGE");
	 *         condition.setFieldName("message");
	 *         condition.setOperator(NumberOperators.EQUALS);
	 *         condition.setValue(" 'speed entry' ");
	 *         .
	 *         .
	 *         // other Conitions
	 *         .
	 *         .
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder().useExternalConnection(con)
	 *         							  .table("Agent_Data")
	 *         							  .orCondition(condition)
	 *        							  .orCondition(condition1);
	 *        deleteBuilder.delete();
	 * 													}
	 * Query built -  DELETE Agent_Data FROM Agent_Data WHERE MESSAGE =  'speed entry'  OR DEVICE_ID =  '1001'
	 * 													</pre>
     * @return GenericDeleteRecordBuilder object instance
	 */
	public GenericDeleteRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	/**
	 *  Adds criteria to the query with AND operation<br>
	 *  <pre>
	 *      {@code
	 *
	 *         // for more details about Criteria class - {@link com.facilio.bmsconsole.criteria.Criteria }
	 *         criteria.addAndCondition(condition);
	 *         criteria.addAndCondition(condition1);
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *      						   .table("Agent_Data")
	 *                                 .andCriteria(criteria).andCriteria(criteria1);
	 *         deleteBuilder.delete();
	 *         }
	 *          Query built -  DELETE Agent_Data FROM Agent_Data FROM Agent_Data WHERE ((MESSAGE =  'speed entry' ) and DEVICE_ID =  '1001' )
	 *  </pre>
	 */

	public GenericDeleteRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	/**
	 *  Adds criteria to the query with OR operation<br>
	 *  <pre>
	 *      {@code
	 *
	 *         // for more details about Criteria class - {@link com.facilio.bmsconsole.criteria.Criteria }
	 *         criteria.addAndCondition(condition);
	 *         criteria1.addAndCondition(condition1);
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *      				.table("Agent_Data")
	 *      				.orCriteria(criteria)
	 *      				.orCriteria(criteria1);
	 *      	deleteBuilder.delete();
	 *         }
	 *          Query built -  DELETE Agent_Data FROM Agent_Data FROM Agent_Data WHERE ((MESSAGE =  'speed entry' ) or DEVICE_ID =  '1001' )
	 *  </pre>
	 */
	public GenericDeleteRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	/**
	 * Adds string tableName to the GenericSelectRecordBuilder's query with 'INNER JOIN' clause<br>
	 * The  INNER JOIN clause matches rows in one table with rows in other tables and allows you to query rows<br>
	 *     that contain columns from both tables<br>
	 *          By making BOOLEAN parameter TRUE the table can be added to the tablesToBeDeleted table {@link com.facilio.sql.GenericDeleteRecordBuilder #tablesToBeDeleted}.<br>
	 *         For references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 *     <pre>
	 *         {@code
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *         							.useExternalConnection(con)
	 *         							.table("testing");
	 *         							.innerJoin("Agent_Data",false)
	 *         							.on("testing.ID = Agent_Data.ID")
	 *         							.andCustomWhere("TESTING.ID = '2' ");
	 *         deleteBuilder.delete();
	 *
	 *         }
	 *         Query built - DELETE testing FROM testing INNER JOIN Agent_Data ON testing.ID = Agent_Data.ID  WHERE (TESTING.ID = '2' )
	 *     </pre>
	 *
	 */
	public GenericJoinBuilder innerJoin(String tableName, boolean delete) {
		return genericJoin(" INNER JOIN ", tableName, delete);
	}

	/**
	 * Adds string tableName to the GenericSelectRecordBuilder's query with 'LEFT JOIN' clause<br>
	 * When you join the t1 table to the t2 table using the LEFT JOIN clause, if a row from the left table t1 matches<br>
	 *     a row from the right table t2 based on the join condition ( t1.c1 = t2.c1 ), this row will be included in the result set.<br>
	 *     By making BOOLEAN parameter TRUE the table can be added to the tablesToBeDeleted table {@link com.facilio.sql.GenericDeleteRecordBuilder #tablesToBeDeleted}.<br>
	 *          For references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 *     <pre>
	 *         {@code
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *         							.useExternalConnection(con)
	 *         							.table("testing");
	 *         							.leftJoin("Agent_Data",false)
	 *         							.on("testing.ID = Agent_Data.ID")
	 *         							.andCustomWhere("TESTING.ID = '2' ");
	 *         	deleteBuilder.delete();
	 *
	 *         }
	 *         Query built - DELETE testing FROM testing LEFT JOIN Agent_Data ON testing.ID = Agent_Data.ID  WHERE (TESTING.ID = '2' )
	 *     </pre>
	 */
	public GenericJoinBuilder leftJoin(String tableName, boolean delete) {
		return genericJoin(" LEFT JOIN ", tableName, delete);
	}

	/**
	 * Adds string tableName to the GenericSelectRecordBuilder's query with 'RIGHT JOIN' clause<br>
	 * The RIGHT JOIN keyword returns all records from the right table (table2), and the matched records from the<br>
	 *     left table (table1). The result is NULL from the left side when there is no match.<br>
	 *     By making BOOLEAN parameter TRUE the table can be added to the tablesToBeDeleted table {@link com.facilio.sql.GenericDeleteRecordBuilder #tablesToBeDeleted}.<br>
	 *          For references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 *     <pre>
	 *         {@code
	 *         GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
	 *         							.useExternalConnection(con)
	 *         							.table("testing");
	 *         							.rightJoin("Agent_Data",false)
	 *         							.on("testing.ID = Agent_Data.ID")
	 *         							.andCustomWhere("TESTING.ID = '2' ");
	 *         deleteBuilder.delete();
	 *         }
	 *         Query built -   DELETE testing FROM testing RIGHT JOIN Agent_Data ON testing.ID = Agent_Data.ID  WHERE (TESTING.ID = '2' )
	 *     </pre>
	 */
	public GenericJoinBuilder rightJoin(String tableName, boolean delete) {
		return genericJoin(" RIGHT JOIN ", tableName, delete);
	}

	/**
	 * Adds string tableName to the GenericSelectRecordBuilder's query with 'FULL JOIN' clause<br>
	 * The FULL OUTER JOIN keyword return all records when there matches in either left (table1) or right (table2) table records,<br>
	 * by making BOOLEAN parameter TRUE the table can be added to the tablesToBeDeleted table {@link com.facilio.sql.GenericDeleteRecordBuilder #tablesToBeDeleted}.<br>
	 *      for references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 */
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

	/**
	 * Executes the Delete query and deletes.<br>
	 *     example output - Deleted 1 records
	 * @return rowCount , which is the number of deleted rows. 0 if no rows deleted.
	 */
	public int delete() throws SQLException {
	    long startTime = System.currentTimeMillis();
        if(AccountUtil.getCurrentAccount() != null) {
            AccountUtil.getCurrentAccount().incrementDeleteQueryCount(1);
        }
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
			long orgId = -1;
			if(AccountUtil.getCurrentOrg() != null) {
				orgId = AccountUtil.getCurrentOrg().getOrgId();
				if(DBUtil.isQueryCacheEnabled(orgId, tableName)) {
					LOGGER.debug("cache invalidate for query " + sql);
					for (String tablesInQuery : tablesToBeDeleted) {
						LRUCache.getQueryCache().remove(GenericSelectRecordBuilder.getRedisKey(orgId, tablesInQuery));
					}
				}
			}

			return rowCount;
		}
		catch(SQLException e) {
			LOGGER.log(Level.ERROR, "Deletion failed ", e);
			throw e;
		}
		finally {
            if(AccountUtil.getCurrentAccount() != null) {
                AccountUtil.getCurrentAccount().incrementDeleteQueryTime((System.currentTimeMillis()-startTime));
            }
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
	 * creates a new instance of DBDeleteRecordBuilder.
	 * @return DBDeleteRecordBuilder instance
	 * @throws Exception
	 */
	private DBDeleteRecordBuilder getDBDeleteRecordBuilder() throws Exception {
		return (DBDeleteRecordBuilder) constructor.newInstance(this);
	}

	/**
	 * constructs a Delete Query
	 * @return String Query
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
	 * sets the orgIdField using tableName and DBUtil class
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
        /**
         * sets the On Condition and is used in case of JOINDELETES
		 * {@link com.facilio.sql.GenericDeleteRecordBuilder#fullJoin(java.lang.String, boolean) } <br>
		 * {@link com.facilio.sql.GenericDeleteRecordBuilder#rightJoin(String, boolean)} <br>
		 * {@link com.facilio.sql.GenericDeleteRecordBuilder#leftJoin(String, boolean)} <br>
         */
		public GenericDeleteRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}

	}
}
