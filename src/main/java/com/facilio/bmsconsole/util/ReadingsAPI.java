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
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class ReadingsAPI {
	private static final Logger logger = LogManager.getLogger(ReadingsAPI.class.getName());
	public static final int DEFAULT_DATA_INTERVAL = 15; //In Minutes
	public static final SecondsChronoUnit DEFAULT_DATA_INTERVAL_UNIT = new SecondsChronoUnit(DEFAULT_DATA_INTERVAL * 60); 
	
	public static ReadingInputType getRDMInputTypeFromModuleType(ModuleType type) {
		switch (type) {
			case SCHEDULED_FORMULA:
			case LIVE_FORMULA:
			case SYSTEM_SCHEDULED_FORMULA:
				return ReadingInputType.FORMULA_FIELD;
			default:
				return ReadingInputType.WEB;
		}
	}
	
	public static int updateReadingDataMetaInputType (long resourceId, long fieldId, ReadingDataMeta.ReadingInputType type) throws SQLException {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Map<String, Object> prop = Collections.singletonMap("inputType", type.getValue());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(resourceId), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(fieldId), PickListOperators.IS))
														;
		return updateBuilder.update(prop);
	}
	
	public static int updateReadingDataMetaInputType (List<ReadingDataMeta> metaList, ReadingDataMeta.ReadingInputType type) throws SQLException {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Criteria pkCriteriaList = new Criteria();
		for (ReadingDataMeta meta : metaList) {
			Criteria pkCriteria = new Criteria();
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(meta.getResourceId()), PickListOperators.IS));
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(meta.getFieldId()), PickListOperators.IS));
			pkCriteriaList.orCriteria(pkCriteria);
		}
		
		Map<String, Object> prop = Collections.singletonMap("inputType", type.getValue());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCriteria(pkCriteriaList);
														
		return updateBuilder.update(prop);
	}
	
	public static ReadingDataMeta getReadingDataMeta(long resourceId,FacilioField field) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReadingDataMetaFields())
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere(module.getTableName()+".RESOURCE_ID = ?", resourceId)
				.andCustomWhere(module.getTableName()+".FIELD_ID = ?", field.getFieldId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReadingDataMeta> readingMetaList = getReadingDataFromProps(props, Collections.singletonMap(field.getId(), field));
		if (readingMetaList != null && !readingMetaList.isEmpty()) {
			return readingMetaList.get(0);
		}
		return null;
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(List<Pair<Long, FacilioField>> rdmPairs) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Map<Long, FacilioField> fieldIdMap = new HashMap<>();
		Criteria pkCriteriaList = new Criteria();
		for (Pair<Long, FacilioField> pair : rdmPairs) {
			Criteria pkCriteria = new Criteria();
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(pair.getLeft()), PickListOperators.IS));
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(pair.getRight().getFieldId()), PickListOperators.IS));
			pkCriteriaList.orCriteria(pkCriteria);
			fieldIdMap.put(pair.getRight().getFieldId(), pair.getRight());
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCriteria(pkCriteriaList);
		List<Map<String, Object>> stats = builder.get();	
		return getReadingDataFromProps(stats, fieldIdMap);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Long resourceId, Collection<FacilioField> fieldList) throws Exception {
		return getReadingDataMetaList(resourceId, fieldList, false);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Long resourceId, Collection<FacilioField> fieldList, boolean excludeEmptyFields, ReadingInputType...readingTypes) throws Exception {
		Map<Long, FacilioField> fieldMap = FieldFactory.getAsIdMap(fieldList);
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> redingFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(redingFields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReadingDataMetaFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("resourceId"), String.valueOf(resourceId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("fieldId"), StringUtils.join(fieldMap.keySet(), ","), NumberOperators.EQUALS));
		
		if (excludeEmptyFields) {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T));
		}
		
		if (readingTypes != null && readingTypes.length > 0) {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"), getReadingTypes(readingTypes), PickListOperators.IS));
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),String.valueOf(ReadingInputType.HIDDEN_FORMULA_FIELD.getValue()), PickListOperators.ISN_T));
		}
		
		List<Map<String, Object>> stats = builder.get();	
		return getReadingDataFromProps(stats, fieldMap);
	}
	
	private static List<ReadingDataMeta> getReadingDataFromProps(List<Map<String, Object>> props, Map<Long, FacilioField> fieldMap) {
		if(props != null && !props.isEmpty()) {
			List<ReadingDataMeta> metaList = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ReadingDataMeta meta = FieldUtil.getAsBeanFromMap(prop, ReadingDataMeta.class);
				Object value = meta.getValue();
				FacilioField field = fieldMap.get(meta.getFieldId());
				meta.setValue(FieldUtil.castOrParseValueAsPerType(field, value));
				meta.setField(field);
				metaList.add(meta);
			}
			return metaList;
		}
		return null;
	}
	
	private static String getReadingTypes(ReadingInputType... types) {
		StringJoiner joiner = new StringJoiner(",");
		for (ReadingInputType type : types) {
			joiner.add(String.valueOf(type.getValue()));
		}
		return joiner.toString();
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
				logger.info("Exception occurred ", e);
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
	
	public static int updateReadingDataMeta(List<FacilioField> fieldsList,List<ReadingContext> readingList,Map<String, ReadingDataMeta> metaMap) throws SQLException {


		if(readingList==null || readingList.isEmpty()) {
			return 0;
		}
		String sql = null;
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(fieldsList);
			Set<Long> resources= new HashSet<Long>();
			Set<Long> fields= new HashSet<Long>();
			Map<String, ReadingDataMeta> uniqueRDMs = new HashMap<>();
			for(ReadingContext readingContext:readingList) {
				long resourceId=readingContext.getParentId();
				resources.add(resourceId);
				long timeStamp=readingContext.getTtime();
				long readingId = readingContext.getId();
				Map<String,Object> readings=  readingContext.getReadings();
				for(Map.Entry<String, Object> reading :readings.entrySet()) {
					FacilioField fField = fieldMap.get(reading.getKey());
					if (fField != null) {
						Object val = FieldUtil.castOrParseValueAsPerType(fField, reading.getValue());
						if (val != null) {
							long fieldId = fField.getFieldId();
							if (metaMap != null) {
								ReadingDataMeta meta = metaMap.get(resourceId+"_"+fField.getFieldId());
								if(meta != null)
								{
									Object lastReading = meta.getValue();
									long lastTimeStamp = meta.getTtime();
									if (lastReading != null && lastTimeStamp != -1 && 
											!"-1".equals(lastReading.toString()) && timeStamp < lastTimeStamp) {
										continue;
									}
								}
							}
							fields.add(fieldId);
							String value = val.toString();
							
							String uniqueKey = resourceId + "|" + fieldId;
							ReadingDataMeta rdm = uniqueRDMs.get(uniqueKey);
							if (rdm == null || rdm.getTtime() < timeStamp) {
								rdm = new ReadingDataMeta();
								rdm.setFieldId(fieldId);
								rdm.setTtime(timeStamp);
								rdm.setValue(value);
								rdm.setReadingDataId(readingId);
								rdm.setResourceId(resourceId);
								
								uniqueRDMs.put(uniqueKey, rdm);
							}
						}
						else {
							logger.log(Level.INFO, "Not updating RDM for "+fField.getName()+" from "+readingContext+" because after parsing, value is null");
						}
					}
				}
			}
			
			if (uniqueRDMs.size() == 0) {
				return 0;
			}
			
			StringBuilder timeBuilder= new StringBuilder();
			StringBuilder valueBuilder= new StringBuilder();
			StringBuilder idBuilder= new StringBuilder();
			long orgId=AccountUtil.getCurrentOrg().getOrgId();

			for (ReadingDataMeta rdm : uniqueRDMs.values()) {
				timeBuilder.append(getCase(rdm.getResourceId(),rdm.getFieldId(),rdm.getTtime(),false));
				valueBuilder.append(getCase(rdm.getResourceId(),rdm.getFieldId(),rdm.getValue(),true));
				idBuilder.append(getCase(rdm.getResourceId(),rdm.getFieldId(),rdm.getReadingDataId(),false));
			}
			
			if(timeBuilder.length()<=0 || valueBuilder.length()<=0) {
				return 0;
			}
			String resourceList=StringUtils.join(resources, ",");
			String fieldList=StringUtils.join(fields, ",");
			sql = "UPDATE "+ModuleFactory.getReadingDataMetaModule().getTableName()+" SET TTIME = CASE "+timeBuilder.toString()+ " END, "
					+ "VALUE = CASE "+valueBuilder.toString() + " END, "
					+ "READING_DATA_ID = CASE "+idBuilder.toString() + " END "
					+ "WHERE ORGID = "+orgId+" AND RESOURCE_ID IN ("+resourceList+") AND FIELD_ID IN ("+fieldList+")";
			if(sql != null && !sql.isEmpty()) {
				System.out.println("################ sql: "+sql);
				try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
					int rowCount = pstmt.executeUpdate();
					return rowCount;
				}
			}
		}
		catch(SQLException e) {
			logger.info("Exception occurred ", e);
			throw new SQLException("Query failed : "+sql, e);
		}

		return 0;
	}
	
	private static String getCase(long resource,long field, Object value, boolean insertQuote) {

		String caseString= " WHEN RESOURCE_ID = "+resource+" AND FIELD_ID = "+field+" THEN ";
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
	
	public static List<FacilioField> excludeDefaultAndEmptyReadingFields(List<FacilioField> fields,Long parentId) throws Exception {
		List<Long> fieldsWithValues = null;
		if (parentId != null && parentId > -1) {
			List<ReadingDataMeta> readingMetaDatas = getReadingDataMetaList(parentId, fields, true);
			if (readingMetaDatas != null) {
				fieldsWithValues = readingMetaDatas.stream().map(meta -> meta.getFieldId()).collect(Collectors.toList());
			}
		}
		List<FacilioField> fieldsToReturn = new ArrayList<>();
		for(FacilioField field: fields) {
			if ((parentId == null || (fieldsWithValues != null && fieldsWithValues.contains(field.getId()))) && !DEFAULT_READING_FIELDS.contains(field.getName()) ) {
				fieldsToReturn.add(field);
			}
		}
		return fieldsToReturn;
	}
	
	public static void updateReadingDataMeta() throws Exception {

		List<ResourceContext> resourcesList= ResourceAPI.getAllResources();
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		
		
		Map<Long,List<FacilioModule>> categoryVsModule= new HashMap<Long,List<FacilioModule>>();
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.fields(FieldFactory.getReadingDataMetaFields());
		
		for(ResourceContext resource:resourcesList) {
			List<FacilioModule>	moduleList=null;
			int resourceType=	resource.getResourceType();
			long resourceId=resource.getId();
			FacilioContext context = new FacilioContext();
			
			if(resourceType==ResourceContext.ResourceType.SPACE.getValue()) {
				context.put(FacilioConstants.ContextNames.PARENT_ID, resourceId);
				Chain getSpaceSpecifcReadingsChain = FacilioChainFactory.getSpaceReadingsChain();
				getSpaceSpecifcReadingsChain.execute(context);
				moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
				
				
			}
			else if(resourceType==ResourceContext.ResourceType.ASSET.getValue()) {
				
				
				long category=getParentCategoryId(resource.getId());
				moduleList= categoryVsModule.get(category);
				
				if(moduleList==null) {
					context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
					context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category);
					Chain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
					getCategoryReadingChain.execute(context);
					moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
					categoryVsModule.put(category, moduleList);
				}
				
			}
			if(moduleList==null || moduleList.isEmpty()) {
				continue;
			}
			
			
			for(FacilioModule module:moduleList) {
				
				List<FacilioField> fieldList= module.getFields();
				for(FacilioField field:fieldList) {
					
					ReadingDataMeta oldRdm=  getReadingDataMeta(resourceId,field);
					if(oldRdm!=null) {
						continue;
					}
					ReadingDataMeta rdm= new ReadingDataMeta();
					rdm.setOrgId(orgId);
					rdm.setFieldId(field.getFieldId());
					rdm.setValue("-1");
					rdm.setResourceId(resourceId);
					rdm.setTtime(System.currentTimeMillis());
					rdm.setInputType(ReadingInputType.WEB);
					builder.addRecord(FieldUtil.getAsProperties(rdm));
				}
			}
		}
		builder.save();
	}

	private static long getParentCategoryId(long assetId) throws Exception {

		AssetContext asset= AssetsAPI.getAssetInfo(assetId, true);
		AssetCategoryContext category= asset.getCategory();
		if(category!=null) {
			return category.getId();
		}
		return -1;
	}
	
	public static int getDataInterval(List<WorkflowFieldContext> wFields) throws Exception {
		return getDataInterval(null, wFields);
	}
	
	public static int getDataInterval(WorkflowContext workflow, List<WorkflowFieldContext>... wFields) throws Exception {
		int dataInterval = DEFAULT_DATA_INTERVAL;
		if (workflow.getExpressions() == null) {
			WorkflowUtil.parseStringToWorkflowObject(workflow.getWorkflowString(),workflow);
		}
		List<WorkflowFieldContext> workflowFields = wFields != null && wFields.length == 1 ? wFields[0] : WorkflowUtil.getWorkflowField(workflow);
		if (workflowFields != null) {
			List<ReadingContext> readings = new ArrayList<>();
			for(WorkflowFieldContext field: workflowFields) {
				if (field.getResourceId() != -1) {
					ReadingContext reading = new ReadingContext();
					reading.setModuleId(field.getModuleId());
					reading.setParentId(field.getResourceId());
					readings.add(reading);
				}
			}
			if (!readings.isEmpty()) {
				setReadingInterval(readings, null);
				return readings.stream().mapToInt(reading -> (int) reading.getDatum("interval")).max().getAsInt();
			}
		}
		return dataInterval;
	}
	
	public static void setReadingInterval(Map<String, List<ReadingContext>> readingMap) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<Long,FacilioModule> moduleMap = null;
		List<ReadingContext> readingsList = new ArrayList<>();
		for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
			String moduleName = entry.getKey();
			List<ReadingContext> readings = entry.getValue();
			FacilioModule module = bean.getModule(moduleName);
			moduleMap = readings.stream().collect(Collectors.toMap(r -> r.getId(), r -> module));
			readingsList.addAll(readings);
		}
		setReadingInterval(readingsList, moduleMap);
	}
	
	public static void setReadingInterval(List<ReadingContext> readings, Map<Long, FacilioModule> moduleMap) throws Exception {
//		Map<Long, Integer> intervalMap = new HashMap<>();
		int defaultInterval = DEFAULT_DATA_INTERVAL;
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL, FacilioConstants.OrgInfoKeys.USE_CONTROLLER_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		if (defaultIntervalProp != null && !defaultIntervalProp.isEmpty()) {
			defaultInterval = Integer.parseInt(defaultIntervalProp);
		}
		
		boolean useControllerDataInterval = Boolean.valueOf(orgInfo.get(FacilioConstants.OrgInfoKeys.USE_CONTROLLER_DATA_INTERVAL));
		Map<Long, ControllerContext> controllers = null;
		Map<Long, ResourceContext> resources = null;
		if (useControllerDataInterval) {
			controllers = DeviceAPI.getAllControllersAsMap();
			Set<Long> resourceIds = readings.stream().map(reading -> reading.getParentId()).collect(Collectors.toSet());
			resources = ResourceAPI.getResourceAsMapFromIds(resourceIds);
		}
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for (ReadingContext reading : readings) {
			FacilioModule module = moduleMap != null ? moduleMap.get(reading.getId()) : bean.getModule(reading.getModuleId());
			int minuteInterval = defaultInterval;
			if (module.getDataInterval() != -1) {
				minuteInterval = module.getDataInterval();
			}
			else if (useControllerDataInterval) {
				ResourceContext parent = resources.get(reading.getParentId());
				if (parent.getControllerId() != -1) {
					ControllerContext controller = controllers.get(parent.getControllerId());
					if (controller.getDataInterval() != -1) {
						minuteInterval = (int) controller.getDataInterval();
					}
				}
			}
			reading.setDatum("interval", minuteInterval);
//			intervalMap.put(reading.getId(), minuteInterval);
		}
	}
	
}
