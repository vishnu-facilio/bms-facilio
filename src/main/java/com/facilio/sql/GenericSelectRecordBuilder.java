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
	
	

	@Override
	public GenericSelectRecordBuilder select(List<FacilioField> fields) { 
		this.selectFields = fields;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}

	@Override
	public GenericSelectRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	@Override
	public GenericJoinBuilder innerJoin(String tableName) {
		joinBuilder.append(" INNER JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
					.append(tableName)
					.append(" ");
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericSelectRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public GenericSelectRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder having(String having) {
		this.having = having;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	@Override
	public GenericSelectRecordBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	
	public List<FacilioField> getSelectFields() {
		return selectFields;
	}

	public String getTableName() {
		return tableName;
	}

	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	public WhereBuilder getWhere() {
		return where;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public String getHaving() {
		return having;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}
	
	public boolean isForUpdate() {
		return forUpdate;
	}

	public Connection getConn() {
		return conn;
	}

	@Override
	public GenericSelectRecordBuilder forUpdate() {
		this.forUpdate = true;
		return this;
	}

	private DBSelectRecordBuilder getDBSelectRecordBuilder() throws Exception {
		return (DBSelectRecordBuilder) constructor.newInstance(this);
	}

	@Override
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
//				System.out.println("########### sql : "+ sql);
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
	
	private void checkForNull() {
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(selectFields == null || selectFields.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
	}
	
	
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
	@Override
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
		
		@Override
		public GenericSelectRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}

	}
}
