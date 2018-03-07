package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ReadingsAPI {
	
	public static Object getLastReadingValue(Long resourceId,FacilioField field) throws Exception {
		FacilioModule module = ModuleFactory.getLastReadingModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getLastReadingFields())
				.table(ModuleFactory.getLastReadingModule().getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere(module.getTableName()+".RESOURCE_ID = ?", resourceId)
				.andCustomWhere(module.getTableName()+".FIELD_ID = ?", field.getFieldId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			Map<String, Object> prop = props.get(0);
			
			Object value = prop.get("value");
			
			return FieldUtil.castOrParseValueAsPerType(field.getDataTypeEnum(), value);
		}
		return null;
	}
	
	public static void loadReadingParent(Collection<ReadingContext> readings) throws Exception {
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
			
			try {
				SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																				.select(fields)
																				.module(module)
																				.beanClass(EnergyMeterContext.class);
				
				Map<Long, EnergyMeterContext> parents = selectBuilder.getAsMap();
				
				for(ReadingContext reading : readings) {
					Long parentId = reading.getParentId();
					if(parentId != null) {
						reading.setParent(parents.get(parentId));
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	public static Map<String, Object> getInstanceMapping(String deviceName, String instanceName) throws Exception {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("DEVICE_NAME= ?",deviceName)
				.andCustomWhere("INSTANCE_NAME=?",instanceName);

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {

			return stats.get(0); 
		}
		return null;
	}
	
	private static List<Map<String, Object>> getDeviceVsInstances() throws Exception {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.andCustomWhere("ORGID=?",orgId);

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {

			return stats; 
		}
		return null;
	}
	
	public static Map<String, Map<String,Map<String,Object>>> getDeviceMapping() throws Exception {
		
		List<Map<String, Object>> deviceVsInstances= getDeviceVsInstances();
		if(deviceVsInstances==null) {
			return null;
		}
		//deviceName: Instancename:stats
		Map<String, Map<String,Map<String,Object>>> deviceMapping=new HashMap<String, Map<String,Map<String,Object>>>();
		
		for(Map<String, Object> data: deviceVsInstances) {
			
			String deviceName=(String)data.remove("device");
			String instanceName= (String)data.remove("instance");
			Map<String,Map<String,Object>> instanceMapping= deviceMapping.get(deviceName);
			if(instanceMapping==null) {
				instanceMapping=new HashMap<String,Map<String,Object>>();
				deviceMapping.put(deviceName, instanceMapping);
			}
			instanceMapping.put(instanceName, data);
		}
		return deviceMapping;
		
	}
	
	public static List<Map<String, Object>> getUnmodeledData(List<String> deviceList) throws Exception {

		List<FacilioField> fields= getUnmodeledFields();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Unmodeled_Instance")
				.innerJoin("Unmodeled_Data")
				.on("Unmodeled_Data.INSTANCE_ID=Unmodeled_Instance.ID")
				.andCustomWhere("ORGID=?",orgId)
				.andCondition(CriteriaAPI.getCondition("DEVICE_NAME", "device", StringUtils.join(deviceList,","), StringOperators.IS))
				.orderBy("TTIME");

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {
			return stats; 
		}
		return null;
	}
	
	public static int updateLastReading(List<FacilioField> fieldsList,List<ReadingContext> readingList,Map<String, Map<String,Object>> lastReadingMap) throws SQLException {


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
					if (lastReadingMap != null) {
						Map<String,Object> oldStats= lastReadingMap.get(resourceId+"_"+fField.getName());
						if(oldStats!=null)
						{
							String lastReading=(String)oldStats.get("value");
							Long lastTimeStamp=(Long)oldStats.get("ttime");
							if (lastReading!=null && lastTimeStamp!=null && 
									lastReading!="-1" && timeStamp<lastTimeStamp) {
								continue;
							}
						}
					}
					fields.add(fieldId);
					String value= reading.getValue().toString();
					timeBuilder.append(getCase(resourceId,fieldId,timeStamp,false));
					valueBuilder.append(getCase(resourceId,fieldId,value,true));
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
	
	private static String getCase(long resource,long field, Object value, boolean insertQuote) {

		String caseString= " WHEN RESOURCE_ID="+resource+" AND FIELD_ID="+field+" THEN ";
		if(insertQuote) {
			return caseString+"'"+value+"'";
		}
		return caseString+value;
	}
	
	private static List<FacilioField> getUnmodeledFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("device", "DEVICE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("instance", "INSTANCE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("ttime", "TTIME",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("value", "VALUE",FieldType.STRING ));
		return fields;
	}
	
	private static final  List<String> DEFAULT_READING_FIELDS = Collections.unmodifiableList(getDefaultReadingFieldNames());
	private static final List<String> getDefaultReadingFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		List<FacilioField> fields = FieldFactory.getDefaultReadingFields(null);
		for(FacilioField field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}
	
	public static List<FacilioField> excludeDefaultReadingFields(List<FacilioField> fields) {
		return fields.stream().filter(field -> !DEFAULT_READING_FIELDS.contains(field.getName())).collect(Collectors.toList());
	}

}
