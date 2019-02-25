package com.facilio.sql;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.fs.FileInfo;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

/**
 * GenericSelectRecordBuilder class helps with Selection query in MySQL DataBase.<br>
 * The MySQL SELECT query is used to fetch data from the MySQL database.<br>
 * This class and its functions helps in constructing MySQL Queries like<br>
 * SELECT<br>
 * &emsp;    [FROM table_references<br>
 * &emsp;   [WHERE where_condition]<br>
 * &emsp;    [GROUP BY {col_name | expr | position}],<br>
 * &emsp;    [HAVING where_condition]<br>
 * &emsp;    [ORDER BY {col_name | expr | position}<br>
 *&emsp;&emsp;      	 [ASC | DESC], ... [WITH ROLLUP]]<br>
 * &emsp;     [LIMIT {[offset,] row_count | row_count OFFSET offset}]<br>

 *
 */
public class GenericSelectRecordBuilder implements SelectBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericSelectRecordBuilder.class.getName());
	private static final int QUERY_TIME_THRESHOLD = 5000;

	private static Constructor<?> constructor;


	private List<FacilioField> selectFields;
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

	static {
		String dbClass = AwsUtil.getDBClass();
		try {
			constructor = Class.forName(dbClass + ".SelectRecordBuilder").getConstructor(GenericSelectRecordBuilder.class);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public GenericSelectRecordBuilder() {

	}

	/**
	 * assigns values from parameters, to the class's variables
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

		this.joinBuilder = new StringBuilder(selectBuilder.joinBuilder);
		if (selectBuilder.selectFields != null) {
			this.selectFields = new ArrayList<>(selectBuilder.selectFields);
		}
		if (selectBuilder.where != null) {
			this.where = new WhereBuilder(selectBuilder.where);
		}
	}



	/**
	 * loads List<FacilioField> fields into the GenericSelectRecordBuilder
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder select(List<FacilioField> fields) {
		this.selectFields = fields;
		return this;
	}


	/**
	 * loads String tableName into the GenericSelectRecordBuilder
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}

	/**
	 * loads an external Connection into the GenericSelectRecordBuilder <br>
	 *     instead of getting it from the connectionpool
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'INNER JOIN' clause
	 * The MySQL INNER JOIN clause matches rows in one table with rows in other tables and allows you to query rows that contain columns from both tables
	 * @return GenericSelectRecordBuilder
	 */
	public GenericJoinBuilder innerJoin(String tableName) {
		joinBuilder.append(" INNER JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'LEFT JOIN' clause
	 * When you join the t1 table to the t2 table using the LEFT JOIN clause, if a row from the left table t1 matches a row from the right table t2 based on the join condition ( t1.c1 = t2.c1 ), this row will be included in the result set.
	 */
	public GenericJoinBuilder leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'RIGHT JOIN' clause
	 * The RIGHT JOIN keyword returns all records from the right table (table2), and the matched records from the left table (table1). The result is NULL from the left side, when there is no match.
	 */
	public GenericJoinBuilder rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}

	/**
	 * loads String tableName  into the GenericSelectRecordBuilder with 'FULL JOIN' clause
	 * The FULL OUTER JOIN keyword return all records when there is a match in either left (table1) or right (table2) table records.
	 */
	public GenericJoinBuilder fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}

	/**
	 *  loads Custom WHERE clause with AND operation
	 *  The WHERE clause is used to extract only those records that fulfill a specified condition
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 *  loads Custom WHERE clause with OR operation
	 *  The WHERE clause is used to extract only those records that fulfill a specified condition
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	/**
	 * loads Where with AND-CONDITION
	 */
	public GenericSelectRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	/**
	 *  loads Where with OR-CONDITION
	 */
	public GenericSelectRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	/**
	 *  returns Where CONDITION wiht AND
	 */
	public GenericSelectRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	/**
	 *  returns Where CONDITION with OR
	 */
	public GenericSelectRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	/**
	 * The GROUP BY clause groups a set of rows into a set of summary rows by values of columns or expressions. The GROUP BY clause returns one row for each group. In other words, it reduces the number of rows in the result set.
	 */
	public GenericSelectRecordBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}

	/**
	 * The  HAVING clause is used in the SELECT statement to specify filter conditions for a group of rows or aggregates.
	 *
	 * The HAVING clause is often used with the GROUP BY clause to filter groups based on a specified condition. If the GROUP BY clause is omitted, the HAVING clause behaves like the WHERE clause
	 */
	public GenericSelectRecordBuilder having(String having) {
		this.having = having;
		return this;
	}

	/**
	 * sets the orderby,
	 *ORDER BY Clause can be used along with the SELECT statement to sort the data of specific fields in an ordered way. It is used to sort the result-set in ascending or descending order.
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	/**
	 * LIMIT clause is used with the SELECT statement to restrict the number of rows in the result set
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * OFFSET value allows us to specify which row to start from retrieving data
	 * @return GenericSelectRecordBuilder
	 */
	public GenericSelectRecordBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}

	/**
	 *
	 * @return selectFields
	 *
	 */
	public List<FacilioField> getSelectFields() {
		return selectFields;
	}

	/**
	 *
	 * @return tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 *
	 * @return joinBuilder
	 */
	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	/**
	 *
	 * @return where
	 */
	public WhereBuilder getWhere() {
		return where;
	}

	/**
	 *
	 * @return groupBy
	 */
	public String getGroupBy() {
		return groupBy;
	}

	/**
	 *
	 * @return having
	 */
	public String getHaving() {
		return having;
	}

	/**
	 * ORDER BY Clause can be used along with the SELECT statement to sort the data of specific fields in an ordered way. It is used to sort the result-set in ascending or descending order.
	 * @return orderBy variable
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 *LIMIT clause is used with the SELECT statement to restrict the number of rows in the result set
	 * @return limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * OFFSET value allows us to specify which row to start from retrieving data
	 * @return offset value
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Boolean value for Update
	 * @return forUpdate
	 */
	public boolean isForUpdate() {
		return forUpdate;
	}

	/**
	 * get the connection details
	 * @return Connection
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * returns forUpdate value
	 */
	public GenericSelectRecordBuilder forUpdate() {
		this.forUpdate = true;
		return this;
	}

	/**
	 * gets DBSelectRecordBuilder reading Properties file
	 * @return
	 * @throws Exception
	 */
	private DBSelectRecordBuilder getDBSelectRecordBuilder() throws Exception {
		return (DBSelectRecordBuilder) constructor.newInstance(this);
	}

	/**
	 * gives the Query output
	 */
	public List<Map<String, Object>> get() throws Exception {
		long startTime = System.currentTimeMillis();
		checkForNull();
		handleOrgId();
		List<Long> fileIds = null;
		List<Map<String, Object>> records = new ArrayList<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		boolean isExternalConnection = true;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isExternalConnection = false;
			}
			String sql = constructSelectStatement();
			pstmt = conn.prepareStatement(sql);

			Object[] whereValues = where.getValues();
			if(whereValues != null) {
				for(int i=0; i<whereValues.length; i++) {
					Object value = whereValues[i];
					pstmt.setObject(i+1, value);
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
				if (orgIdField != null) {
					record.put(orgIdField.getName(), AccountUtil.getCurrentOrg().getId());
				}

				for(FacilioField field : selectFields) {
					Object val = FieldUtil.getObjectFromRS(field, rs);
					if(field != null) {
						if (field instanceof NumberField) {
							NumberField numberField =  (NumberField)field;
							if(numberField.getMetric() > 0) {

								if(numberField.getUnitId() > 0) {
									Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
									val = UnitsUtil.convert(val, siUnit.getUnitId(), numberField.getUnitId());
								}
								else {
									val = UnitsUtil.convertToOrgDisplayUnitFromSi(val, numberField.getMetric());
								}
							}
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
					records.add(record);
				}
			}

			long mapTimeTaken = System.currentTimeMillis() - mapStartTime;
			LOGGER.debug("Time taken to create map in GenericSelectBuilder : "+mapTimeTaken);

			long timeTaken = System.currentTimeMillis() - startTime;
			LOGGER.debug("Time taken to get records in GenericSelectBuilder : "+timeTaken);

		}
		catch(SQLException e) {
			LOGGER.log(Level.ERROR, "Exception " ,e);
			throw e;
		}
		finally {
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

		if(orgIdField != null) {
			where = oldWhere;
		}
		return records;
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
	 * checks null in tableName,tableName
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
	 *
	 * @return String sql query using constructSelectStatement(
	 */
	public String constructSelectStatement() {
		try {
			DBSelectRecordBuilder selectRecordBuilder = getDBSelectRecordBuilder();
			sql = selectRecordBuilder.constructSelectStatement();
			return sql;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
}
