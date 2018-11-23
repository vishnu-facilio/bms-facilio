package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.FileField;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.transaction.FacilioConnectionPool;

public class GenericInsertRecordBuilder implements InsertBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericInsertRecordBuilder.class.getName());

	private List<FacilioField> fields;
	private String tableName;
	private List<Map<String, Object>> values = new ArrayList<>();
	private String sql;
	
	@Override
	public GenericInsertRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}

	@Override
	public GenericInsertRecordBuilder fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}

	@Override
	public GenericInsertRecordBuilder addRecord(Map<String, Object> value) {
		this.values.add(value);
		return this;
	}

	@Override
	public GenericInsertRecordBuilder addRecords(List<Map<String, Object>> values) {
		this.values.addAll(values);
		return this;
	}
	
	@Override
	public long insert (Map<String, Object> value) throws Exception {
		addRecord(value);
		save();
		Long id = (Long) value.get("id");
		if (id != null) {
			return id;
		}
		return -1; 
	}

	@Override
	public List<Map<String, Object>> getRecords() {
		return values;
	}

	private List<FileField> fileFields = new ArrayList<>();
	private void handleFileFields() {
		try {
			FieldUtil.addFiles(fileFields, values);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Insertion failed while adding files", e);
			throw new RuntimeException("Insertion failed while adding files");
		}
	}
	
	private List<NumberField> numberFields = new ArrayList<>();
	
	@Override
	public void save() throws SQLException, RuntimeException {
		
		if(values.isEmpty()) {
			return;
		}
		
		checkForNull();
		splitFields();
		
		handleFileFields();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			sql = constructInsertStatement();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			for(Map<String, Object> value : values) {
				pstmt.clearParameters();
				int paramIndex = 1;
				for(FacilioField field : fields) {
					try {
						FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex++, field, value.get(field.getName()));
					}
					catch (Exception e) {
						LOGGER.error("Error in parsing field : "+field+" during insertion.", e);
						throw e;
					}
				}
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			rs = pstmt.getGeneratedKeys();
			List<Long> ids = new ArrayList<>();
			int itr = 0;
			while(rs.next()) {
				long id = rs.getLong(1);
				ids.add(id);
				Map<String, Object> props = values.get(itr++);
				if(props != null) {
					props.put("id", id);
				}
			}
			//System.out.println("Added records with IDs : "+ids);
		}
		catch(SQLException | RuntimeException e) {
			StringBuilder builder = new StringBuilder();
			builder.append("SQL Statement : ")
					.append(sql)
					.append("\nProps : \n")
					.append(values);
			CommonCommandUtil.emailException("GenericInsertRecord", "Insertion failed - ", e, builder.toString());
			LOGGER.log(Level.ERROR, "Insertion failed ", e);
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private String constructInsertStatement() {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (");
		
		boolean isFirst = true;
		for(FacilioField field : fields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append(field.getColumnName());
		}
		
		sql.append(") VALUES (");
		
		isFirst = true;
		for(FacilioField field : fields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append("?");
		}
		
		sql.append(")");
		
		return sql.toString();
	}
	
	private void checkForNull() {
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
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
}
