package com.facilio.sql;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.LRUCache;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.unitconversion.UnitsUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * GenericSelectRecordBuilder class helps with building and execution of selection query in the database.<br>
 * The  SELECT query is used to fetch data from the database.<br>
 *     usage-<br>
 *            <pre>
 * sample table - Agent_Data
 * +-------+--------------+
 * | ID    | message      |
 * +-------+--------------+
 * | 12352 | speed entry  |
 * |  1001 | speed entry2 |
 * |   123 | speed entry3 |
 * |     2 | vijs         |
 * |     2 | vijs         |
 * +-------+--------------+
 *                {@code
 * 	          GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
 * 	         							.table("Agent_Data")
 * 	        						   		 .select(FieldFactory.getAgentDataFields())
 * 	          							.orCustomWhere("MESSAGE = 'speed entry'")
 * 	          							.orCustomWhere("ID=12352");
 * 	          selectBuilder.get();
 * 	          }
 *  Query built - SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,<br>
 *     			  agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`, agent_data.<br>
 *                MESSAGE AS `message` FROM Agent_Data WHERE (MESSAGE = 'speed entry') OR (ID=12352)
 *</pre>
 */
public class GenericSelectRecordBuilder implements SelectBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericSelectRecordBuilder.class.getName());
	private static final int QUERY_TIME_THRESHOLD = 5000;
	private static final Map<Long, Map<String, Map<String, SelectQueryCache>>> QUERY_CACHE = new HashMap<>();

	private static Constructor<?> constructor;


	private Collection<FacilioField> selectFields;
	private FacilioField orgIdField=null;
	private String tableName;
	private StringBuilder joinBuilder = new StringBuilder();
	private WhereBuilder where = new WhereBuilder();
	private WhereBuilder oldWhere = null;
	private String groupBy;
	private String having;
	private String orderBy;
	private int limit = -1;
	private int offset = -1;
	private boolean forUpdate = false;
	private Connection conn = null;
	private ArrayList<String> tables = new ArrayList<>();
	private StringBuilder cacheQuery = new StringBuilder();


	static {
		String dbClass = AwsUtil.getDBClass();
		try {
			constructor = Class.forName(dbClass + ".SelectRecordBuilder").getConstructor(GenericSelectRecordBuilder.class);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

    /**
     * Creates a new GenericSelectRecordBuilder object instance
     */
	public GenericSelectRecordBuilder() {

	}

	/**
	 * Creates a new GenericSelectRecordBuilder instance.<br>
	 * Initializes values to variables like tableName,groupBy,having,orderBy,limit,offset,conn.
	 * @param selectBuilder select builder
	 */
	public GenericSelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) { //Do not call after calling getProps
		// TODO Auto-generated constructor stub
		this.tableName = selectBuilder.tableName;
		this.groupBy = selectBuilder.groupBy;
		this.having = selectBuilder.having;
		this.orderBy = selectBuilder.orderBy;
		this.limit = selectBuilder.limit;
		this.offset = selectBuilder.offset;
		this.conn = selectBuilder.conn;
		this.tables = selectBuilder.tables;

		this.joinBuilder = new StringBuilder(selectBuilder.joinBuilder);
		if (selectBuilder.selectFields != null) {
			this.selectFields = new ArrayList<>(selectBuilder.selectFields);
		}
		if (selectBuilder.where != null) {
			this.where = new WhereBuilder(selectBuilder.where);
		}
	}



	/**
	 * Adds a SELECT clause to a List of fields(FacilioField) in the GenericSelectRecordBuilder's query <br>
     *     usage:<br>
	 *  <pre>
	 *  {@code
     *   GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *   						.table("Agent_Data")
	 *  						.select(FieldFactory.getAgentDataFields());
	 *  selectBuilder.get();
	 *  }
	 *  query built - SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`,<br>
	 * 				 agent_data.TIME_STAMP AS `timestamp`, agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`,<br>
	 * 				 agent_data.ORGID AS `orgId`, agent_data.MESSAGE AS `message` FROM Agent_Data
	 *   </pre>
	 * @return GenericSelectRecordBuilder object instance
	 */
	public GenericSelectRecordBuilder select(Collection<FacilioField> fields) {
		this.selectFields = fields;
		return this;
	}


	/**
	 * Adds the table to the query using table's name.<br>
     *     usage:<br>
	 *         <pre>
	 *             {@code
     *         	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *         							.select(fields)
	 *         							.table("Users");
	 *         	}
	 *         	</pre>
	 * @return GenericSelectRecordBuilder object instance, null if table name is not set.
	 */
	public GenericSelectRecordBuilder table(String tableName) {
		this.tableName = tableName;
		this.tables.add(tableName);
		return this;
	}

	/**
	 * Adds an external connection to the GenericSelectRecordBuilder instead of getting it from the connectionpool<br>
     *     usage: (we give an external  connection)<br>
	 *   <pre>
	 *     {@code
	 *       Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/sonoo","user","password");
	 *       GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().useExternalConnection(con);
	 *
	 *     }
	 *   </pre>
     *
	 * @return GenericSelectRecordBuilder object instance, null if no connection is set.
	 */
	public GenericSelectRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	/**
	 * Adds tableName  to the GenericSelectRecordBuilder's query with 'INNER JOIN' clause, for INNER JOIN functionality.<br>
	 * The  INNER JOIN clause matches rows in one table with rows in other tables and allows <br>
	 * you to query rows that contain columns from both tables.<br>
	 *      for references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 * <pre>
	 *     {@code
	 *      GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *      							.useExternalConnection(con).table("Agent_Data")
	 *      							.select(fieldss);
	 *         						    .innerJoin("testing")
	 *         						    .on("Agent_Data.MESSAGE = testing.MESSAGE");
	 *      selectBuilder.get();
	 *         }
	 *         Query built -  SELECT ORGID AS `orgId`, testing.MESSAGE AS `message` FROM Agent_Data INNER JOIN testing ON Agent_Data.MESSAGE = testing.MESSAGE
	 * </pre>
	 * @return GenericSelectRecordBuilder
	 */
	public GenericJoinBuilder innerJoin(String tableName) {
		joinBuilder.append(" INNER JOIN ")
					.append(tableName)
					.append(" ");
		tables.add(tableName);
		return new GenericJoinBuilder(this);
	}

	/**
	 * Adds tableName  to the GenericSelectRecordBuilder's query with 'LEFT JOIN' clause, for LEFT JOIN functionality.<br>
	 * When you join the t1 table to the t2 table using the LEFT JOIN clause if a row from the left table<br>
	 * t1 matches a row from the right table t2 based on the join condition ( t1.c1 = t2.c1 ), this row will be included in the result set.<br>
	 *      for references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 * <pre>
	 *     {@code
	 *      GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *      							.useExternalConnection(con).table("Agent_Data")
	 *      							.select(fieldss);
	 *         						    .leftJoin("testing")
	 *         						    .on("Agent_Data.MESSAGE = testing.MESSAGE");
	 *      selectBuilder.get();
	 *         }
	 *         Query built -  SELECT ORGID AS `orgId`, testing.MESSAGE AS `message` FROM Agent_Data LEFT JOIN testing ON Agent_Data.MESSAGE = testing.MESSAGE
	 * </pre>
	 */
	public GenericJoinBuilder leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
					.append(tableName)
					.append(" ");
		tables.add(tableName);
		return new GenericJoinBuilder(this);
	}

	/**
	 * Adds tableName  into the GenericSelectRecordBuilder with 'RIGHT JOIN' clause, for RIGHT JOIN functionality.<br>
	 * The RIGHT JOIN keyword returns all records from the right table (table2), and the matched records from the left table (table1).<br>
	 *     The result is NULL from the left side when there is no match.<br>
	 *          for references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 * <pre>
	 *     {@code
	 *      GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *      							.useExternalConnection(con).table("Agent_Data")
	 *      							.select(fieldss);
	 *         						    .rightJoin("testing")
	 *         						    .on("Agent_Data.MESSAGE = testing.MESSAGE");
	 *      selectBuilder.get();
	 *         }
	 *         Query built -  SELECT ORGID AS `orgId`, testing.MESSAGE AS `message` FROM Agent_Data RIGHT JOIN testing ON Agent_Data.MESSAGE = testing.MESSAGE
	 * </pre>
	 */
	public GenericJoinBuilder rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
					.append(tableName)
					.append(" ");
		tables.add(tableName);
		return new GenericJoinBuilder(this);
	}

	/**
	 * Adds tableName  into the GenericSelectRecordBuilder with 'FULL JOIN' clause, for FULL JOIN functionality.<br>
	 * The FULL OUTER JOIN keyword return all records when there is a match in either left (table1) or right (table2) table records.<br>
	 *      for references @see <a href="https://www.w3schools.com/sql/sql_join.asp">JOINS</a>
	 * <pre>
	 *     {@code
	 *      GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *      							.useExternalConnection(con).table("Agent_Data")
	 *      							.select(fieldss);
	 *         						    .fullJoin("testing")
	 *         						    .on("Agent_Data.MESSAGE = testing.MESSAGE");
	 *      selectBuilder.get();
	 *         }
	 *         Query built -  SELECT ORGID AS `orgId`, testing.MESSAGE AS `message` FROM Agent_Data FULL JOIN testing ON Agent_Data.MESSAGE = testing.MESSAGE
	 * </pre>
	 */
	public GenericJoinBuilder fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
					.append(tableName)
					.append(" ");
		tables.add(tableName);
		return new GenericJoinBuilder(this);
	}

	/**
	 *  Adds a WHERE clause with AND operation
	 *  The WHERE clause is used to extract only those records that fulfil a specified condition<br>
     *      usage:<br>
	 *          <pre>
	 *              {@code
     *
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().table("Agent_Data")
	 *        							 .select(FieldFactory.getAgentDataFields())
	 *           						 .andCustomWhere("MESSAGE = 'speed entry'")
	 *           						 .andCustomWhere("ID=12352");
	 *         selectBuilder.get();
	 *	   }
	 *	   query built -  SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`<br>
	 *	   ,agent_data.TIME_STAMP AS `timestamp`, agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`,<br>
	 *	   agent_data.ORGID AS `orgId`, agent_data.MESSAGE AS `message` FROM Agent_Data WHERE (MESSAGE = 'speed entry') <br>
	 *	    AND (ID=12352)<br>
	 *    </pre>
	 *    @return GenericSelectRecordBuilder object instance
	 */
	@Deprecated
	public GenericSelectRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 *  Adds a WHERE clause with OR operation<br>
	 *  The WHERE clause is used to extract only those records that fulfil a specified condition<br>
     *       usage:<br>
     *            <pre>
	 *                {@code
	 * 	          GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 * 	         							.table("Agent_Data")
	 * 	        						    .select(FieldFactory.getAgentDataFields())
	 * 	          							.orCustomWhere("MESSAGE = 'speed entry'")
	 * 	          							.orCustomWhere("ID=12352");
	 * 	          selectBuilder.get();
	 * 	          }
	 *  Query built - SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,<br>
	 *      agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`, agent_data.<br>
	 *          MESSAGE AS `message` FROM Agent_Data WHERE (MESSAGE = 'speed entry') OR (ID=12352)
	 *</pre>
	 * @return GenericSelectRecordBuilder object instance
	 */
	@Deprecated
	public GenericSelectRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	/**
     * Adds a condition with AND operation <br>
     *     usage:<br>
	 *         <pre>
	 *             {@code\
	 *         Condition condition = new Condition();  {@link com.facilio.bmsconsole.criteria.Condition}
	 *         Condition condition = new Condition();
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
     *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *         							  .table("Agent_Data")
	 *         							  .select(FieldFactory.getAgentDataFields())
	 *        							  .andCondition(condition)
	 *        							  .andCondition(condition1);
	 *          selectBuilder.get();
	 * 													}
	 * Query built -  SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,
	 * 				  agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`,
	 * 				  agent_data.MESSAGE AS `message` FROM Agent_Data WHERE MESSAGE =  'speed entry'  AND DEVICE_ID =  '1001'
	 * 													</pre>
     * @return GenericSelectRecordBuilder object instance
	 */
	public GenericSelectRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	/**
     *  Adds a condition with OR operation<br>
	 *     usage:<br>
	 *         <pre>
	 *             {@code
	 *         Condition condition = new Condition();  {@link com.facilio.bmsconsole.criteria.Condition}
	 *         Condition condition = new Condition();
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
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *         							  .table("Agent_Data")
	 *         							  .select(FieldFactory.getAgentDataFields())
	 *        							  .orCondition(condition)
	 *        							  .orCondition(condition1);
	 *          selectBuilder.get();
	 * 													}
	 * Query built -  SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,
	 * 				  agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`,
	 * 				  agent_data.MESSAGE AS `message` FROM Agent_Data WHERE MESSAGE =  'speed entry'  OR DEVICE_ID =  '1001'
	 * 													</pre>
     * @return GenericSelectRecordBuilder object instance
	 */
	public GenericSelectRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	/**
	 *  Adds a criteria with AND operation
	 *  <pre>
	 *      {@code
	 *
	 *         // for more details about Criteria class - {@link com.facilio.bmsconsole.criteria.Criteria }
	 *         criteria.addAndCondition(condition);
	 *         criteria.addAndCondition(condition1);
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *      						   .table("Agent_Data")
	 *      						   .select(FieldFactory.getAgentDataFields())
	 *                                 .andCriteria(criteria);
	 *         selectBuilder.get();
	 *         }
	 *         Query built -  SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,
	 *         				agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`,
	 *         				agent_data.MESSAGE AS `message`
	 *         				FROM Agent_Data WHERE ((MESSAGE =  'speed entry' ) and DEVICE_ID =  '1001' )
	 *  </pre>
	 */
	public GenericSelectRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	/**
	 *  Adds a criteria with OR operation
	 *  <pre>
	 *      {@code
	 *         // for more details about Criteria class - {@link com.facilio.bmsconsole.criteria.Criteria }
	 *         criteria.addAndCondition(condition);
	 *         criteria1.addAndCondition(condition1);
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *      						   .table("Agent_Data")
	 *      						   .select(FieldFactory.getAgentDataFields())
	 *                                 .orCriteria(criteria).orCriteria(criteria1);
	 *         selectBuilder.get();
	 *         }
	 * Query built -  SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,<br>
	 *                agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`,<br>
	 *                agent_data.MESSAGE AS `message`
	 *                    FROM Agent_Data WHERE (MESSAGE =  'speed entry' ) OR (DEVICE_ID =  '1001' )
	 *  </pre>
	 */
	public GenericSelectRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	/**
	 * The GROUP BY clause groups a set of rows into a set of summary rows by values of columns or expressions.<br>
	 * The GROUP BY clause returns one row for each group. In other words, it reduces the number of rows in the result set.
	 * <pre>
	 *     {@code
	 *     GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *     							  .table("Agent_Data")
	 *     							  .select(FieldFactory.getAgentDataFields())
	 *         						  .groupBy("COMMAND");
	 *     selectBuilder.get();
	 *         }
	 *    Qeury built -  SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,
	 *    			agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`,
	 *    			agent_data.MESSAGE AS `message`
	 *    			FROM Agent_Data GROUP BY COMMAND
	 * </pre>
	 */
	public GenericSelectRecordBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	/**
	 * Adds a HAVING clause to the GenericSelectBuilder's query,the  HAVING clause is used in the SELECT statement to specify filter conditions for a group of rows or aggregates.
	 *
	 * The HAVING clause is often used with the GROUP BY clause to filter groups based on a specified condition. <br>
	 *     If the GROUP BY clause is omitted, the HAVING clause behaves like the WHERE clause
	 *   <pre>
	 *       {@code
	 *       GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *       							.table("Agent_Data")
	 *       							.select(FieldFactory.getAgentDataFields())
	 *       							.having("STATUS = '3'");
	 *       selectBuilder.get();
	 *         }
	 *         <br>
	 *         Query Built - SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,
	 *         				agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`, agent_data.MESSAGE AS `message`
	 *         				FROM Agent_Data HAVING STATUS = '3'
	 *   </pre>
	 */
	public GenericSelectRecordBuilder having(String having) {
		this.having = having;
		return this;
	}

	/**
	 * Adds a ORDER BY clause to the GenericSelectBuilder's query,
	 * ORDER BY clause can be used along with the SELECT statement to sort the data of specific fields in an ordered way.<br>
	 *      It is used to sort the result-set in ascending or descending order.<br>
	 *     Usage - <br>
	 *         <pre>
	 *             {@code
	 *
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *         						.table("Agent_Data")
	 *        						    .select(FieldFactory
	 *        						    .getAgentDataFields())
	 *        						    .orderBy("COMMAND");
	 *         selectBuilder.get();
	 *             }
	 *             <br>
	 *  query built - SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`,<br>
	 *      agent_data.TIME_STAMP AS `timestamp`, agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`,<br>
	 *          agent_data.ORGID AS `orgId`, agent_data.MESSAGE AS `message`
	 *          FROM Agent_Data ORDER BY COMMAND <br>
	 *
	 * </pre>
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	/**
	 * Adds a LIMIT clause to the GenericSelectBuilder's query,
	 *  LIMIT clause is used with the SELECT statement to restrict the number of rows in the result set<br>
	 *     usage-<br>
	 *         <br>
	 *             <pre>
	 *                 {@code
	 *
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *                                    .table("Agent_Data")
	 *                                    .select(FieldFactory
	 *                                    .getAgentDataFields())
	 *                                    .limit(3);
	 *         selectBuilder.get();
	 *
	 *             }
	 *   query built - SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`,<br>
	 *                 agent_data.TIME_STAMP AS `timestamp`, agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`,<br>
	 *                 agent_data.ORGID AS `orgId`, agent_data.MESSAGE AS `message`
	 *                 FROM Agent_Data LIMIT 3<br>
	 * </pre>
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * Adds a OFFSET clause to the GenericSelectBuilder's query,
	 * OFFSET value allows us to specify which row to start from retrieving data. USED with LIMIT{@link com.facilio.sql.GenericSelectRecordBuilder#limit}<br>
	 *     usage - <br>
	 *         <br>
	 *             <pre>
	 *                 {@code
	 *
	 *         GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
	 *         							  .table("Agent_Data")
	 *         							  .select(FieldFactory.getAgentDataFields())
	 *         							  .having("MESSAGE = 'hey machi 0' ")
	 *         							  .limit(1)
	 *         							  .offset(10);
	 *         selectBuilder.get();
	 *
	 *                 }
	 *     query built- <br>
	 *             SELECT agent_data.DEVICE_ID AS `deviceid`, agent_data.MSGID AS `msgid`, agent_data.TIME_STAMP AS `timestamp`,
	 *             agent_data.COMMAND AS `command`, agent_data.STATUS AS `status`, agent_data.ORGID AS `orgId`,
	 *             agent_data.MESSAGE AS `message`
	 *             FROM Agent_Data HAVING MESSAGE = 'hey machi 0'  LIMIT 1 OFFSET 10
	 *</pre>
	 *  @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}

	/**
	 * Returns the list of fields being selected {@link GenericSelectRecordBuilder#selectFields}
	 * @return Fields that are selected in the GenericSelectRecordBuilder, null if no fields were selected.
	 *
	 */
	public Collection<FacilioField> getSelectFields() {
		return selectFields;
	}

	/**
	 * Returns name of the current table being used {@link GenericSelectRecordBuilder#table(String)}
	 * @return table that is used in the GenericSelectRecordBuilder, null if no table is set.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns the current Stringbuilder
	 * @return joinBuilder that are constructed in the GenericSelectRecordBuilder.
	 */
	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	/**
	 *  Returns the current where-condition {@link GenericSelectRecordBuilder#where }
	 * @return Where that is used in the GenericSelectRecordBuilder.
	 */
	public WhereBuilder getWhere() {
		return where;
	}

	/**
	 *  Returns the current GroupBy used {@link GenericSelectRecordBuilder#groupBy}
	 * @return groupBy that is used in the GenericSelectRecordBuilder, null if groupBy isn't set.
	 */
	public String getGroupBy() {
		return groupBy;
	}

	/**
	 * Returns the current getHaving used. {@link GenericSelectRecordBuilder#having}
	 * @return Having that is used in the GenericSelectRecordBuilder, null if having isn't set.
	 */
	public String getHaving() {
		return having;
	}

	/**
	 * Returns the current OrderBy value
     * ORDER BY Clause can be used along with the SELECT statement to sort the data of specific fields in an ordered way.<br>
	 *     It is used to sort the result-set in ascending or descending order. {@link GenericSelectRecordBuilder#orderBy}
	 * @return orderBy option that is used in the GenericSelectRecordBuilder,null if orderBy isn't set.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
     * Returns the current limit value {@link GenericSelectRecordBuilder#limit}
	 *LIMIT clause is used with the SELECT statement to restrict the number of rows in the result set
	 * @return limit value that is used in the GenericSelectRecordBuilder, -1 if no limit is set.
	 */
	public int getLimit() {
		return limit;
	}

	/**
     * Returns the current offset value {@link GenericSelectRecordBuilder#offset}
	 * OFFSET value allows us to specify which row to start from retrieving data
	 * @return offset value that is used in the GenericSelectRecordBuilder,  -1 if no offset is set.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns Boolean value for Update {@link GenericSelectRecordBuilder#forUpdate}
	 * @return forUpdate value that is used in the GenericSelectRecordBuilder.
	 */
	public boolean isForUpdate() {
		return forUpdate;
	}

	/**
	 * Returns the current connection {@link GenericSelectRecordBuilder#conn}
	 * @return Connection that is used in the GenericSelectRecordBuilder, null if no connection is set.
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * Sets the forUpdate value in GenericSelectRecordBuilder.
	 * @return GenericSelectRecordBuilder object instance.
	 */
	public GenericSelectRecordBuilder forUpdate() {
		this.forUpdate = true;
		return this;
	}

	/**
	 * Returns DBSelectRecordBuilder reading Properties file
	 *
	 * @throws Exception
	 */
	private DBSelectRecordBuilder getDBSelectRecordBuilder() throws Exception {
		return (DBSelectRecordBuilder) constructor.newInstance(this);
	}

	/**
	 * Returns the Query output - selected rows from the database as a list.
	 * @return List of rows that are selected
	 */
	public List<Map<String, Object>> get() throws Exception {
		long startTime = System.currentTimeMillis();
		checkForNull();
		handleOrgId();
		List<Long> fileIds = null;
		List<Map<String, Object>> records = new ArrayList<>();
		long orgId = -1;

		if(AccountUtil.getCurrentOrg() != null) {
			orgId = AccountUtil.getCurrentOrg().getId();
		}



		PreparedStatement pstmt = null;
		ResultSet rs = null;

		boolean isExternalConnection = true;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isExternalConnection = false;
			}
			sql = constructSelectStatement();
			cacheQuery.append(sql);
			pstmt = conn.prepareStatement(sql);

			Object[] whereValues = where.getValues();
			if(whereValues != null) {
				for(int i=0; i<whereValues.length; i++) {
					Object value = whereValues[i];
					pstmt.setObject(i+1, value);
					cacheQuery.append(',');
					cacheQuery.append(value);
				}
			}

			if(orgId > 0) {
				records = getFromCache(orgId);
				if(records !=  null) {
					return records;
				} else {
					AccountUtil.getCurrentAccount().incrementSelectQueryCount(1);
					records = new ArrayList<>();
				}
			}

			long queryStartTime = System.currentTimeMillis();
			rs = pstmt.executeQuery();
			long queryTime = System.currentTimeMillis() - queryStartTime;

			if (queryTime > QUERY_TIME_THRESHOLD) {
				LOGGER.info("SQL : "+sql);
				LOGGER.info("Values : "+Arrays.toString(whereValues));
				LOGGER.info("Time taken to execute query in GenericSelectBuilder : "+queryTime);
			}

			this.sql = pstmt.toString();

			long mapStartTime = System.currentTimeMillis();

			while(rs.next()) {
				Map<String, Object> record = new HashMap<>();
				for(FacilioField field : selectFields) {
					Object val = FieldUtil.getObjectFromRS(field, rs);
					if(field != null) {
						if (field instanceof NumberField) {
							NumberField numberField =  (NumberField)field;
							val = UnitsUtil.convertToDisplayUnit(val, numberField);
						}
						else if (field.getDataTypeEnum() == FieldType.FILE && val != null ) {
							if (fileIds == null) {
								fileIds= new ArrayList<>();
							}
							fileIds.add((Long) val);
							record.put(field.getName()+"Id", (Long) val);
							val = null;
						}
					}

					if(val != null) {
						record.put(field.getName(), val);
					}
				}
				if(!record.isEmpty()) {
					if (orgIdField != null) {
						record.put(orgIdField.getName(), AccountUtil.getCurrentOrg().getId());
					}
					records.add(record);
				}
			}

			long mapTimeTaken = System.currentTimeMillis() - mapStartTime;
			LOGGER.debug("Time taken to create map in GenericSelectBuilder : "+mapTimeTaken);

			// long timeTaken = System.currentTimeMillis() - startTime;

			// LOGGER.debug("Time taken to get records in GenericSelectBuilder : "+timeTaken);

		}
		catch(SQLException e) {
			LOGGER.error("Error occured in GenericSelectBuilder during execution of "+sql ,e);
			throw e;
		}
		finally {
			if(AccountUtil.getCurrentAccount() != null) {
				AccountUtil.getCurrentAccount().incrementSelectQueryTime((System.currentTimeMillis() - startTime));
			}
			if (isExternalConnection) {
				DBUtil.closeAll(pstmt, rs);
			}
			else {
				DBUtil.closeAll(conn, pstmt, rs);
				conn = null;
			}
		}

		if (fileIds != null && !records.isEmpty()) {
			fetchFileUrl(records, fileIds);
		}

		addToCache(orgId, records);
//		if(orgIdField != null) {
//			where = oldWhere;
//		}
		return records;
	}

	private List<Map<String,Object>> getFromCache(long orgId) {

		if(DBUtil.isQueryCacheEnabled(orgId, tableName)) {
			Map<String, Map<String, SelectQueryCache>> table = QUERY_CACHE.get(orgId);
			List<Map<String, Object>> returnValue = new ArrayList<>();
			if (table != null) {
				Map<String, SelectQueryCache> tableCache = table.get(tableName);
				String queryToCache = getCacheQuery();
				if(tableCache != null) {
                    SelectQueryCache cache = tableCache.get(queryToCache);
                    if (cache != null) {
                        for (String tablesInQuery : cache.getTables()) {
                            Object value = LRUCache.getQueryCache().get(getRedisKey(orgId, tablesInQuery));
                            if (value == null) {
                                tableCache.remove(queryToCache);
                                LOGGER.debug("cache miss for query " + queryToCache);
                                returnValue = null;
                                break;
                            }
                        }
                        if (returnValue != null) {
                            LOGGER.debug("cache hit for query " + queryToCache);
                            return cache.getResult();
                        }
                    }
                }

			}
		}
		return null;
	}

	static String getRedisKey(long orgId, String tableName) {
		return  orgId+'_'+tableName;
	}

	private String getCacheQuery() {
	    return cacheQuery.toString();
    }

	private void addToCache(long orgId, List<Map<String,Object>> records) {
		if (orgId > 0) {
			if(DBUtil.isQueryCacheEnabled(orgId, tableName)) {
				long queryGetTime = System.currentTimeMillis();
				Map<String, Map<String, SelectQueryCache>> table = QUERY_CACHE.getOrDefault(orgId, new HashMap<>());
				Map<String, SelectQueryCache> query = table.getOrDefault(tableName, new HashMap<>());
				String queryToCache = getCacheQuery();
				query.put(queryToCache, new SelectQueryCache(tables, records));
				table.put(tableName, query);
				LOGGER.debug("building cache for query " + queryToCache);
				for (String tablesInQuery : tables) {
					LRUCache.getQueryCache().put(getRedisKey(orgId, tablesInQuery), queryGetTime);
				}
				QUERY_CACHE.put(orgId, table);
			}
		}
	}

	/**
	 * fetches file's URL, File Name and Content type
	 * @param records
	 * @param fileIds
	 * @throws Exception
	 */
	private void fetchFileUrl(List<Map<String,Object>> records, List<Long> fileIds) throws Exception {
		FileStore fs = FileStoreFactory.getInstance().getFileStore();

		// TODO get filePrivateUrl in bulk
		Map<Long, String> fileUrls = new HashMap<>();
		for(Long fileId: fileIds) {
			fileUrls.put(fileId, fs.getPrivateUrl(fileId));
		}
		Map<Long, FileInfo> files = fs.getFileInfoAsMap(fileIds);

		for(Map<String, Object> record: records) {
			for(FacilioField field : selectFields) {
				if(field != null && field.getDataTypeEnum() == FieldType.FILE && record.containsKey(field.getName()+"Id")) {
					Long id = (Long) record.get(field.getName()+"Id");
					record.put(field.getName()+"Url", fileUrls.get(id));
					record.put(field.getName()+"FileName", files.get(id).getFileName());
					record.put(field.getName()+"ContentType", files.get(id).getContentType());
				}
			}
		}
	}

	/**
	 * checks if table's name is null or empty
	 *
	 */
	private void checkForNull() {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}

		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
	}

	/**
	 *  constructs a Select statement(query).
	 * @return  sql query constructed.
	 */
	public String constructSelectStatement() {
		try {
			DBSelectRecordBuilder selectRecordBuilder = getDBSelectRecordBuilder();
			String sql = selectRecordBuilder.constructSelectStatement();
			return sql;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * sets the OrgIdField
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
	private String sql;
	/**
	 * converts query to String<br>
	 *     but returns null until {@link #get() }  method is called
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return sql;
	}

	public static class GenericJoinBuilder implements JoinBuilderIfc<GenericSelectRecordBuilder> {

		private GenericSelectRecordBuilder parentBuilder;
		public GenericJoinBuilder(GenericSelectRecordBuilder parentBuilder) {
			// TODO Auto-generated constructor stub
			this.parentBuilder = parentBuilder;
		}

		/**
		 * sets the On Condition
		 */
		public GenericSelectRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}

	}

	private class SelectQueryCache {

		private ArrayList<String> tables;
		private List<Map<String, Object>> result;

		SelectQueryCache(ArrayList<String> tables, List<Map<String, Object>> result) {
			this.tables = tables;
			this.result = deepCloneResult(result);
		}
		
		private List<Map<String, Object>> deepCloneResult (List<Map<String, Object>> result) {
			if (result != null) {
				List<Map<String, Object>> newResult = new ArrayList<>();
				for (Map<String, Object> prop : result) {
					newResult.add(new HashMap<>(prop));
				}
				return newResult;
			}
			return null;
		}

		public ArrayList<String> getTables() {
			return tables;
		}

		public void setTables(ArrayList<String> tables) {
			this.tables = tables;
		}

		public List<Map<String, Object>> getResult() {
			return deepCloneResult(result);
		}

		public void setResult(List<Map<String, Object>> result) {
			this.result = result;
		}
	}

	public Map<String, Object> fetchFirst() throws Exception {
		List<Map<String, Object>> list = get();
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
}
