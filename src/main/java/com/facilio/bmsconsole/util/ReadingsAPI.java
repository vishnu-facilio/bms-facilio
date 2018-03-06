package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ReadingsAPI {
	
	public static Object getLastReadingValue(Long orgId,Long resourceId,FacilioField field) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getLastReadingFields())
				.table(ModuleFactory.getLastReadingModule().getTableName())
				.andCustomWhere(ModuleFactory.getLastReadingModule().getTableName()+".ORGID = ?", orgId)
				.andCustomWhere(ModuleFactory.getLastReadingModule().getTableName()+".RESOURCE_ID = ?", resourceId)
				.andCustomWhere(ModuleFactory.getLastReadingModule().getTableName()+".FIELD_ID = ?", field.getFieldId());
		
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
	
	
	
	private static List<FacilioField> getUnmodeledFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("device", "DEVICE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("instance", "INSTANCE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("ttime", "TTIME",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("value", "VALUE",FieldType.STRING ));
		return fields;
	}

}
