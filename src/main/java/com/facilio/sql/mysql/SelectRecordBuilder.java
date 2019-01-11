package com.facilio.sql.mysql;

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

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.sql.DBSelectRecordBuilder;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class SelectRecordBuilder extends DBSelectRecordBuilder {

	private static final Logger LOGGER = LogManager.getLogger(SelectRecordBuilder.class.getName());
	private static final int QUERY_TIME_THRESHOLD = 5000;
	
	public static SelectRecordBuilder getInstance(GenericSelectRecordBuilder selectRecordBuilder) {
		return new SelectRecordBuilder(selectRecordBuilder);
	}
	
	public SelectRecordBuilder(GenericSelectRecordBuilder selectBuilder) { //Do not call after calling getProps
		super(selectBuilder);
	}
	
	public List<Map<String, Object>> get() throws Exception {
		long startTime = System.currentTimeMillis();
		checkForNull(false);
		
		List<Long> fileIds = null;
		List<Map<String, Object>> records = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
		try {
			
			String sql = constructSelectStatement();
//			System.out.println("########### sql : "+ sql);
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
			DBUtil.closeAll(conn,pstmt, rs);
		}
		
		if (fileIds != null && !records.isEmpty()) {
			fetchFileUrl(records, fileIds);
		}
		return records;
	}
	
	@Override
	public String constructSelectStatement() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		
		boolean isFirst = true;
		for(FacilioField field : selectFields) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append(field.getCompleteColumnName())
				.append(" AS `")
				.append(field.getName())
				.append("`");
		}
		
		sql.append(" FROM ")
			.append(tableName);
		
		sql.append(joinBuilder.toString());
		
		if(where.getWhereClause() != null && !where.getWhereClause().isEmpty()) {
			sql.append(" WHERE ")
				.append(where.getWhereClause());
		}
		
		if(groupBy != null && !groupBy.isEmpty()) {
			sql.append(" GROUP BY ")
				.append(groupBy);
		}
		
		if(having != null && !having.isEmpty()) {
			sql.append(" HAVING ")
				.append(having);
		}
		
		if(orderBy != null && !orderBy.isEmpty()) {
			sql.append(" ORDER BY ")
				.append(orderBy);
		}
		
		if(limit != -1) {
			sql.append(" LIMIT ")
				.append(limit);
			
			if(offset != -1) {
				sql.append(" OFFSET ")
					.append(offset);
			}
		}
		
		return sql.toString();
	}
}
