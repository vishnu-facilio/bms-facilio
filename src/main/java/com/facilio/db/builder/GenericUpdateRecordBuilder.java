package com.facilio.db.builder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.util.DBConf;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.LRUCache;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.NumberField;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class GenericUpdateRecordBuilder implements UpdateBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericUpdateRecordBuilder.class.getName());
	private List<FacilioField> fields;
	private FacilioField orgIdField = null;
	private Map<String, List<FacilioField>> fieldMap;
	private String baseTableName;
	private String tableName;
	private Map<String, Object> value;
	private StringBuilder joinBuilder = new StringBuilder();
	private WhereBuilder where = new WhereBuilder();
	private WhereBuilder oldWhere = null;
	private Connection conn = null;
	private ArrayList<String> tablesToBeUpdated = new ArrayList<>();
	
	public GenericUpdateRecordBuilder() {
		// TODO Auto-generated constructor stub
	}

	public GenericUpdateRecordBuilder(GenericUpdateRecordBuilder updateBuilder) {
		super();
		this.baseTableName = updateBuilder.baseTableName;
		this.tableName = updateBuilder.tableName;
		this.value = updateBuilder.value;
		this.conn = updateBuilder.conn;
		this.tablesToBeUpdated = updateBuilder.tablesToBeUpdated;
		
		this.joinBuilder = new StringBuilder(updateBuilder.joinBuilder);
		if (updateBuilder.fields != null) {
			this.fields = new ArrayList<>(updateBuilder.fields);
		}
		if (updateBuilder.where != null) {
			this.where = new WhereBuilder(updateBuilder.where);
		}
	}

	public GenericUpdateRecordBuilder table(String tableName) {
		this.tableName = tableName;
		tablesToBeUpdated.add(tableName);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	public GenericUpdateRecordBuilder fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public GenericUpdateRecordBuilder setBaseTableName(String baseTableName) {
		this.baseTableName = baseTableName;
		return this;
	}
	
	public List<FacilioField> getFields() {
		return fields;
	}

	public Map<String, List<FacilioField>> getFieldMap() {
		return fieldMap;
	}

	public String getBaseTableName() {
		return baseTableName;
	}

	public String getTableName() {
		return tableName;
	}

	public Map<String, Object> getValue() {
		return value;
	}

	public StringBuilder getJoinBuilder() {
		return joinBuilder;
	}

	public WhereBuilder getWhere() {
		return where;
	}

	public List<FileField> getFileFields() {
		return fileFields;
	}

	public List<NumberField> getNumberFields() {
		return numberFields;
	}

	@Override
	public GenericJoinBuilder innerJoin(String tableName) {
		joinBuilder.append(" INNER JOIN ")
					.append(tableName)
					.append(" ");
		tablesToBeUpdated.add(tableName);
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
					.append(tableName)
					.append(" ");
		tablesToBeUpdated.add(tableName);
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
					.append(tableName)
					.append(" ");
		tablesToBeUpdated.add(tableName);
		return new GenericJoinBuilder(this);
	}
	
	@Override
	public GenericJoinBuilder fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
					.append(tableName)
					.append(" ");
		tablesToBeUpdated.add(tableName);
		return new GenericJoinBuilder(this);
	}

	@Override
	@Deprecated
	public GenericUpdateRecordBuilder andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	@Deprecated
	public GenericUpdateRecordBuilder orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public GenericUpdateRecordBuilder orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}
	
	private List<FileField> fileFields = new ArrayList<>();
	private void handleFileFields() {
		try {
			fetchFilesAndMarkDeleted(value);
			FieldUtil.addFiles(fileFields, Collections.singletonList(value));
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Insertion failed while updating files", e);
			throw new RuntimeException("Insertion failed while updating files");
		}
	}
	
	private List<NumberField> numberFields = new ArrayList<>();
	private static Constructor constructor;
	
	static {
		String dbClass = DBConf.getInstance().getDBPackage();
		try {
			constructor = Class.forName(dbClass + ".UpdateRecordBuilder").getConstructor(GenericUpdateRecordBuilder.class);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void removeDefaultValues() {
		Iterator<Entry<String, Object>> iter = value.entrySet().iterator();
		while (iter.hasNext()) {
		    Entry<String, Object> entry = iter.next();
		    if(entry.getValue() == null || (entry.getValue() instanceof Number && ((Number)entry.getValue()).intValue() == -1)) {
		        iter.remove();
		    }
		}
	}
	
	@Override
	public int update(Map<String, Object> value) throws SQLException {
	    long startTime = System.currentTimeMillis();
	    if(AccountUtil.getCurrentAccount() != null) {
	        AccountUtil.getCurrentAccount().incrementUpdateQueryCount(1);
        }
		checkForNull();
		handleOrgId();
		splitFields();
		if (value == null) {
			return 0;
		}

		/*if (orgIdField != null) {
			value.put(orgIdField.getName(), AccountUtil.getCurrentOrg().getId());
		}*/
		value.remove("id");
		this.value = value;
		removeDefaultValues();
		
		if(!value.isEmpty()) {
			
			handleFileFields();
			FieldUtil.handleNumberFieldUnitConversion(numberFields, Collections.singletonList(value));
			
			PreparedStatement pstmt = null;
			boolean isExternalConnection = true;
			try {
				if (conn == null) {
					conn = FacilioConnectionPool.INSTANCE.getConnection();
					isExternalConnection = false;
				}

				if ((AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92)) && tableName.equals("Preventive_Maintenance")) {
					LOGGER.info("Connection in Update Builder for "+tableName+" : "+conn);
				}

				fieldMap = convertFieldsToMap(fields);
				String sql = constructUpdateStatement();
				if(sql != null && !sql.isEmpty()) {
					//System.out.println("################ sql: "+sql);
					pstmt = conn.prepareStatement(sql);
					
					int paramIndex = 1;
//					for(Map.Entry<String, Object> entry : value.entrySet()) {
//						List<FacilioField> fields = fieldMap.get(entry.getKey());
//						if (fields != null) {
							for (FacilioField field: fields) {
								if(field != null) {
									if (value.containsKey(field.getName())) {
										if (field.getDataType() == FieldType.ID.getTypeAsInt()) {
											continue;
										}
										FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex++, field, value.get(field.getName()));
									}
								}
							}
//						}
//					}
					
					Object[] whereValues = where.getValues();
					if(whereValues != null) {
						for(int i=0; i<whereValues.length; i++) {
							Object whereValue = whereValues[i];
							pstmt.setObject(paramIndex++, whereValue);
						}
					}
					
					int rowCount = pstmt.executeUpdate();
					System.out.println("Updated "+rowCount+" records.");
					long orgId = -1;
					if(AccountUtil.getCurrentOrg() != null) {
						orgId = AccountUtil.getCurrentOrg().getOrgId();
						if(DBUtil.isQueryCacheEnabled(orgId, tableName)) {
							LOGGER.debug("cache invalidate for query " + sql);
							for (String tablesInQuery : tablesToBeUpdated) {
								LRUCache.getQueryCache().remove(GenericSelectRecordBuilder.getRedisKey(orgId, tablesInQuery));
							}
						}
					}
					return rowCount;
				}
			}
			catch(SQLException e) {
				LOGGER.log(Level.ERROR, "Update failed ", e);
				LOGGER.log(Level.ERROR, (pstmt == null) ? "null" : pstmt);
				throw e;
			}
			finally {
                if(AccountUtil.getCurrentAccount() != null) {
                    AccountUtil.getCurrentAccount().incrementUpdateQueryTime((System.currentTimeMillis()-startTime));
                }
				if (isExternalConnection) {
					DBUtil.close(pstmt);
				}
				else {
					DBUtil.closeAll(conn, pstmt);
					conn = null;
				}
			}
			
			if(orgIdField != null) {
				where = oldWhere;
			}
		}
		return 0;
	}
	
	private DBUpdateRecordBuilder getDBUpdateRecordBuilder() throws Exception {
		return (DBUpdateRecordBuilder) constructor.newInstance(this);
	}

	private String constructUpdateStatement() {
		try {
			DBUpdateRecordBuilder updateRecordBuilder = getDBUpdateRecordBuilder();
			return updateRecordBuilder.constructUpdateStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Map<String, List<FacilioField>> convertFieldsToMap(List<FacilioField> fields) {
		if(fields != null) {
			Map<String, List<FacilioField>> fieldMap = new HashMap<>();
			for(FacilioField field : fields) {
				if (fieldMap.get(field.getName()) == null) {
					fieldMap.put(field.getName(), new ArrayList<>());
				}
				fieldMap.get(field.getName()).add(field);
			}
			return fieldMap;
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
	
	private void checkForNull() {
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
		if (where.isEmpty()) {
			throw new IllegalArgumentException("Cannot update because there's no where condition.");
		}
	}
	
	private void splitFields() {
		for (FacilioField field : fields) {
			if (field instanceof FileField) {
				if (fileFields == null) {
					fileFields = new ArrayList<>();
				}
				fileFields.add((FileField) field);
			}
			else if (field instanceof NumberField) {
				if (numberFields == null) {
					numberFields = new ArrayList<>();
				}
				numberFields.add((NumberField) field);
			}
		}
	}
	
	public static class GenericJoinBuilder implements JoinBuilderIfc<GenericUpdateRecordBuilder> {

		private GenericUpdateRecordBuilder parentBuilder;
		private GenericJoinBuilder(GenericUpdateRecordBuilder parentBuilder) {
			// TODO Auto-generated constructor stub
			this.parentBuilder = parentBuilder;
		}
		
		@Override
		public GenericUpdateRecordBuilder on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
										.append(condition)
										.append(" ");
			return parentBuilder;
		}
		
	}
	
	private void fetchFilesAndMarkDeleted (Map<String, Object> value) throws Exception {
		List<FacilioField> fileFields = fields.stream().filter(field -> field.getDataTypeEnum() == FieldType.FILE).collect(Collectors.toList());
		if (fileFields.isEmpty()) {
			return;
		}
		if (!fileFields.stream().anyMatch(field -> value.containsKey(field.getName())) ) {
			return;
		}

		String tableName = this.tableName;
		if (joinBuilder != null && joinBuilder.length() > 0) {
			tableName += joinBuilder.toString();
		}
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(tableName)
				.andCustomWhere(where.getWhereClause(), where.getValues())
				;
		List<Map<String, Object>> records = builder.get();
		if (records == null || records.isEmpty()) {
			return;
		}
		List<Long> fileIds = new ArrayList<>();
		for(FacilioField field: fileFields) {
			if (records.get(0).containsKey(field.getName())) {
				fileIds.add((Long) records.get(0).get(field.getName()));
			}
		}
		
		if (!fileIds.isEmpty()) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			fs.markAsDeleted(fileIds);
		}
	}
	
}
