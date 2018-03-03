package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.transaction.FacilioConnectionPool;

public class AddOrUpdateReadingValuesCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReadingContext> readings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.READINGS);
		if(readings == null) {
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
			if(reading != null) {
				readings = Collections.singletonList(reading);
			}
		}
		if(readings == null || readings.isEmpty()) {
			
			return false;
		}
		Map<String, Map<String,Object>> lastReadingMap =(Map<String, Map<String,Object>>)context.get(FacilioConstants.ContextNames.LAST_READINGS);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

		List<ReadingContext> readingsToBeAdded = new ArrayList<>();
		for(ReadingContext reading : readings) {
			if(reading.getTtime() == -1) {
				reading.setTtime(System.currentTimeMillis());
			}
			if(reading.getParentId() == -1) {
				throw new IllegalArgumentException("Invalid parent id for readings of module : "+moduleName);
			}

			if(reading.getId() == -1) {
				readingsToBeAdded.add(reading);
			}
			else {
				updateReading(module, fields, reading,lastReadingMap);
			}
		}
		addReadings(module, fields, readingsToBeAdded,lastReadingMap);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, readingsToBeAdded);
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		return false;
	}
	
	private void addReadings(FacilioModule module, List<FacilioField> fields, List<ReadingContext> readings,
			Map<String, Map<String,Object>> lastReadingMap ) throws Exception {
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.addRecords(readings);
		readingBuilder.save();
		updateLastReading(fields,readings,lastReadingMap);
	}
	
	private void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading,
			Map<String, Map<String,Object>> lastReadingMap) throws Exception {
		UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
																	.module(module)
																	.fields(fields)
																	.andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
		updateBuilder.update(reading);
		updateLastReading(fields,Collections.singletonList(reading),lastReadingMap);
	}
	
	
	private String getCase(long resource,long field, Object value) {

		return " WHEN RESOURCE_ID="+resource+" AND FIELD_ID="+field+" THEN "+value;

	}

	private int updateLastReading(List<FacilioField> fieldsList,List<ReadingContext> readingList,Map<String, Map<String,Object>> lastReadingMap) throws SQLException {


		if(readingList==null || readingList.isEmpty()) {
			return 0;
		}
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(fieldsList);
			Set<Long> resources= new HashSet<Long>();
			Set<Long> fields= new HashSet<Long>();
			StringBuilder timeBuilder= new StringBuilder();
			StringBuilder valueBuilder= new StringBuilder();
			long orgId=AccountUtil.getCurrentOrg().getOrgId();

			for(ReadingContext readingContext:readingList) {
				long resourceId=readingContext.getParentId();
				resources.add(resourceId);
				long timeStamp=readingContext.getTtime();

				Map<String,Object> readings=  readingContext.getReadings();
				for(Map.Entry<String, Object> reading :readings.entrySet()) {

					FacilioField fField=fieldMap.get(reading.getKey());
					long fieldId=fField.getFieldId();
					Map<String,Object> oldStats= lastReadingMap.get(resourceId+"_"+fieldId);
					if(oldStats!=null)
					{
						Double lastReading=(Double)oldStats.get("value");
						Long lastTimeStamp=(Long)oldStats.get("ttime");
						if (lastReading!=null && lastTimeStamp!=null && 
								lastReading!=-1 && timeStamp<lastTimeStamp) {
							continue;
						}
					}
					fields.add(fieldId);
					String value= reading.getValue().toString();
					timeBuilder.append(getCase(resourceId,fieldId,timeStamp));
					valueBuilder.append(getCase(resourceId,fieldId,value));
				}
			}

			if(timeBuilder.length()<=0 || valueBuilder.length()<=0) {
				return 0;
			}
			String resourceList=StringUtils.join(resources, ",");
			String fieldList=StringUtils.join(fields, ",");
			String sql = "UPDATE Last_Reading SET TTIME= CASE "+timeBuilder.toString()+ " END , "
					+ "VALUE= CASE "+valueBuilder.toString()
					+" END WHERE ORGID="+orgId+" AND RESOURCE_ID IN ("+resourceList+") AND FIELD_ID IN ("+fieldList+")";
			if(sql != null && !sql.isEmpty()) {
				System.out.println("################ sql: "+sql);
				try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
					int rowCount = pstmt.executeUpdate();
					return rowCount;
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return 0;
	}

}
