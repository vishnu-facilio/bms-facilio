
package com.facilio.workflowv2.modulefunctions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.modules.*;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.scriptengine.context.DBParamContext;
import com.facilio.scriptengine.modulefunctions.FacilioModuleFunction;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.scriptengine.util.WorkflowGlobalParamUtil;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.util.QueryUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

import lombok.extern.log4j.Log4j;

@Log4j
public class FacilioModuleFunctionImpl implements FacilioModuleFunction {

	private static final String RESULT_STRING = "result";
	
	@Override
	public Map<String, Object> addTemplateData(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		String templateRef = objects.get(1).toString();
		
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
		if(FacilioUtil.isNumeric(templateRef)) {
			
			context.put(FacilioConstants.ContextNames.FORM_ID, Double.valueOf(templateRef).longValue());
		}
		else {
			context.put(FacilioConstants.ContextNames.FORM_NAME, templateRef);
		}
		
		chain.execute();
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		List<FormField> fields = form.getFields();
		
		Map<String, Object> actualData = new HashMap<>(); 
		
		for(FormField field :fields) {
			actualData.put(field.getName(), field.getValue());
		}
		if(objects.size() > 2) {
			Map<String, Object> data = (Map<String,Object>)objects.get(2);
			for(String name : data.keySet()) {
				actualData.put(name, data.get(name));
			}
		}
		
		CustomModuleData moduleData = (CustomModuleData) FieldUtil.getAsBeanFromMap(actualData, CustomModuleData.class);
		
		FacilioChain addModuleDataChain = FacilioChainFactory.addModuleDataChain();
		context = addModuleDataChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.RECORD, moduleData);
		moduleData.parseFormData();
		
		addModuleDataChain.execute();
		
		return moduleData.getData();
	}
	
	public void v3Add(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		Object insertObject = objects.get(1);
		
		List<Map<String, Object>> dataList = new ArrayList<>();
		
		if(insertObject instanceof Map) {
			dataList.add((Map<String, Object>) insertObject);
		}
		else if (insertObject instanceof Collection) {
			
			List<Object> insertList = (List<Object>)insertObject;
			for(Object insert :insertList) {
				dataList.add((Map<String, Object>) insert);
			}
		}
		
		for(Map<String, Object> rawData : dataList) {
			
			FacilioContext context = V3Util.createRecord(module, rawData);
			ModuleBaseWithCustomFields record = Constants.getRecordMap(context).get(module.getName()).get(0);
			rawData.put("id", record.getId());
		}
	}
	@Override
	public void add(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		Object insertObject = objects.get(1);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(module.getTypeEnum() == ModuleType.READING) {
			
			List<ReadingContext> readings = new ArrayList<>();
			
			if(insertObject instanceof Map) {
				
				ReadingContext reading = FieldUtil.getAsBeanFromMap(((Map<String, Object>) insertObject), ReadingContext.class);
				readings.add(reading);
			}
			else if (insertObject instanceof Collection) {
				List<Map<String,Object>> insertList = (List<Map<String,Object>>)insertObject;
				
				readings = FieldUtil.getAsBeanListFromMapList(insertList, ReadingContext.class);
			}
			
			FacilioChain addReadingChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
			FacilioContext context = addReadingChain.getContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
			context.put(FacilioConstants.ContextNames.READINGS, readings);
			context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.FORMULA);
			
			addReadingChain.execute();
			
		}
		else {
			
			boolean isV3SupportedModule = ChainUtil.isV3Enabled(module);
			
			if(isV3SupportedModule && AccountUtil.isFeatureEnabled(FeatureLicense.SCRIPT_CRUD_FROM_V3)) {
				
				List<Map<String, Object>> dataList = new ArrayList<>();
				
				if(insertObject instanceof Map) {
					dataList.add((Map<String, Object>) insertObject);
				}
				else if (insertObject instanceof Collection) {
					
					List<Object> insertList = (List<Object>)insertObject;
					for(Object insert :insertList) {
						dataList.add((Map<String, Object>) insert);
					}
				}
				
				for(Map<String, Object> data : dataList) {
					CommonCommandUtil.handleLookupFormData(modBean.getAllFields(module.getName()), data);
				}
				
				FacilioContext context = V3Util.createRecord(module, dataList, true, null, null,true);
				
				List<ModuleBaseWithCustomFields> records = Constants.getRecordList(context);
				
				for(int i=0;i<records.size();i++) {
					dataList.get(i).put("id", records.get(i).getId());
				}
				
			}
			else {
				
				List<ModuleBaseWithCustomFields> dataList = new ArrayList<>();
				
				if(insertObject instanceof Map) {
					ModuleBaseWithCustomFields customModuleData = new ModuleBaseWithCustomFields();
					customModuleData.setData((Map<String, Object>) insertObject);
					
					dataList.add(customModuleData);
				}
				else if (insertObject instanceof Collection) {
					
					List<Object> insertList = (List<Object>)insertObject;
					
					for(Object insert :insertList) {
						ModuleBaseWithCustomFields customModuleData = new ModuleBaseWithCustomFields();
						customModuleData.setData((Map<String, Object>) insert);
						dataList.add(customModuleData);
					}
				}
				
				int i=0;
				for(ModuleBaseWithCustomFields moduleData :dataList) {
					FacilioChain addModuleDataChain = FacilioChainFactory.addModuleDataChain();
					FacilioContext context = addModuleDataChain.getContext();
					context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

					context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
					if(moduleData != null && moduleData.getData() != null && moduleData.getData().containsKey("activityModuleName")){
						context.put(FacilioConstants.ContextNames.ACTIVITY_MODULE_NAME_FROM_SCRIPT, moduleData.getData().get("activityModuleName"));
					}

					context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
					context.put(FacilioConstants.ContextNames.RECORD, moduleData);
					moduleData.parseFormData();
					
					context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
					
					addModuleDataChain.execute();
					
					Map<String, Object> insertObjectMap = null;
					if(insertObject instanceof Map) {
						insertObjectMap = (Map<String, Object>) insertObject;
					}
					else if (insertObject instanceof Collection) {
						List<Object> insertList = (List<Object>)insertObject;
						insertObjectMap = (Map<String, Object>) insertList.get(i++);
					}
					insertObjectMap.put("id", moduleData.getId());
				}
			}
		}
		
	}

	@Override
	public void update(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		Criteria criteria = null;
		if(objects.get(1) instanceof DBParamContext) {
			criteria = ((DBParamContext) objects.get(1)).getCriteria();
		}
		else if (objects.get(1) instanceof Criteria) {
			criteria = (Criteria)objects.get(1);
		}
		
		if(criteria == null) {
			throw new RuntimeException("criteria cannot be null during update");
		}
		
		ScriptUtil.fillCriteriaField(criteria, module.getName());
		
		Map<String, Object> updateMap = (Map<String, Object>)objects.get(2);
		
		boolean isV3SupportedModule = ChainUtil.isV3Enabled(module);
		
		if(isV3SupportedModule && AccountUtil.isFeatureEnabled(FeatureLicense.SCRIPT_CRUD_FROM_V3)) {
			
			List<FacilioField> fields = Collections.singletonList(FieldFactory.getIdField(module));
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.select(fields)
					.beanClass(ModuleBaseWithCustomFields.class)
					.andCriteria(criteria);
			
			List<Map<String, Object>> props = select.getAsProps();
			
			List<Long> ids = new ArrayList<Long>();
			if(CollectionUtils.isNotEmpty(props)) {
				for(Map<String, Object> prop : props) {
					ids.add((Long)prop.get("id"));
				}
			}
			
			CommonCommandUtil.handleLookupFormData(modBean.getAllFields(module.getName()), updateMap);
			
			FacilioContext context = V3Util.updateBulkRecords(module.getName(), updateMap,ids, true);
		}
		else {
			if (LookupSpecialTypeUtil.isSpecialType(module.getName())) {
				
				GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
						.table(module.getTableName())
						.fields(modBean.getAllFields(module.getName()))
						.andCriteria(criteria);
				updateRecordBuilder.update(updateMap);
				
			}
			else {
				
				List<FacilioField> fields = modBean.getAllFields(module.getName());
				
				UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
						.module(module)
						.fields(fields)
						.andCriteria(criteria);
				
				List<SupplementRecord> supplements = new ArrayList<>();
				CommonCommandUtil.handleFormDataAndSupplement(fields, updateMap, supplements);
				if(!supplements.isEmpty()) {
					updateRecordBuilder.updateSupplements(supplements);
				}
				updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
				updateRecordBuilder.updateViaMap(updateMap);

				try {
					Map<Long, List<UpdateChangeSet>> recordChanges = updateRecordBuilder.getChangeSet();
					updateActivity(recordChanges, module.getName(), updateMap);
				} catch(Exception e){
					LOGGER.info("Exception in update record activity - Facilio script");
				}
			}
		}
	}

	private void updateActivity(Map<Long, List<UpdateChangeSet>> recordChanges,String moduleName,Map<String, Object> updateMap) throws Exception {
		if(recordChanges != null && !recordChanges.isEmpty()) {
			for(Map.Entry<Long,List<UpdateChangeSet>> item : recordChanges.entrySet()) {
				FacilioChain chain = TransactionChainFactory.getConstructUpdateActivitiesChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.CHANGE_SET, recordChanges);
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(item.getKey()));
				context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

				if (updateMap != null && updateMap.containsKey("activityModuleName")) {
					context.put(FacilioConstants.ContextNames.ACTIVITY_MODULE_NAME_FROM_SCRIPT, updateMap.get("activityModuleName"));
				}
				chain.execute();
			}
		}
	}

	@Override
	public void delete(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		Criteria criteria = null;
		DBParamContext dbParamContext = null;
		if(objects.get(1) instanceof DBParamContext) {
			criteria = ((DBParamContext) objects.get(1)).getCriteria();
		}
		else if (objects.get(1) instanceof Criteria) {
			criteria = (Criteria)objects.get(1);
		}
		else if (objects.get(1) instanceof Map) {
			dbParamContext = FieldUtil.getAsBeanFromMap((Map)objects.get(1), DBParamContext.class);
			criteria = dbParamContext.getCriteria();
		}
		ScriptUtil.fillCriteriaField(criteria, module.getName());
		
		boolean isV3SupportedModule = ChainUtil.isV3Enabled(module);
		
		if(isV3SupportedModule && AccountUtil.isFeatureEnabled(FeatureLicense.SCRIPT_CRUD_FROM_V3)) {
			
			List<FacilioField> fields = Collections.singletonList(FieldFactory.getIdField(module));
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.select(fields)
					.beanClass(ModuleBaseWithCustomFields.class)
					.andCriteria(criteria);
			
			List<Map<String, Object>> props = select.getAsProps();
			
			List<Long> ids = new ArrayList<Long>();
			if(CollectionUtils.isNotEmpty(props)) {
				for(Map<String, Object> prop : props) {
					ids.add((Long)prop.get("id"));
				}
			}
			
			JSONObject deleteObj = new JSONObject();
			deleteObj.put(module.getName(), ids);
			
			FacilioContext context = V3Util.deleteRecords(module.getName(), deleteObj, null,null, true);
		}
		else {
			DeleteRecordBuilder<ModuleBaseWithCustomFields> delete = new DeleteRecordBuilder<>()
					.module(module);
			
			if(criteria == null) {
				throw new Exception("criteria cannot be null during delete");
			}
			if(dbParamContext != null && dbParamContext.isSkipModuleCriteria()) {
				delete.skipModuleCriteria();
			}
			
			delete.andCriteria(criteria);
			
			if (module.isTrashEnabled()) {
	            delete.markAsDelete();
	        }
			else {
				delete.delete();
			}
		}
	}
	
	@Override
	public Object fetchFirst(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		Object result = fetch(globalParams,objects);
		
		if(result instanceof List && !((List)result).isEmpty()) {
			return ((List)result).get(0);
		}
		return result;
	}

	@Override
	public Object fetchAttachment(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		return null;
	}

	@Override
	public Object fetch(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		DBParamContext dbParamContext = null;
		
		if(objects.get(1) instanceof DBParamContext) {
			dbParamContext = (DBParamContext) objects.get(1);
		}
		else if (objects.get(1) instanceof Criteria) {
			dbParamContext = new DBParamContext();
			dbParamContext.setCriteria((Criteria)objects.get(1));
		}
		else if (objects.get(1) instanceof Map) {
			dbParamContext = FieldUtil.getAsBeanFromMap((Map)objects.get(1), DBParamContext.class);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ScriptUtil.fillCriteriaField(dbParamContext.getCriteria(), module.getName());
		
		Map<String, List<Map<String, Object>>> cache = null;
		Map<String, ReadingDataMeta> cachedRDM = null;
		
		if(globalParams != null) {
			cache = (Map<String, List<Map<String, Object>>>) globalParams.get(WorkflowGlobalParamUtil.DATA_CACHE);
			cachedRDM = (Map<String, ReadingDataMeta>) globalParams.get(WorkflowGlobalParamUtil.RDM_CACHE);
		}
		
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = null;
		
		Object result = null;
		
		result = fetchDataFromCache(cache,module,dbParamContext);
		
		if(result != null) {
			return result;
		}
		
		List<Map<String, Object>> props = null;
		if (!LookupSpecialTypeUtil.isSpecialType(module.getName())) {
			
			selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.table(module.getTableName())
					.module(module)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
					.andCriteria(dbParamContext.getCriteria())
					;
			
			if(dbParamContext.isSkipUnitConversion()) {
				selectBuilder.skipUnitConversion();
			}
			if(dbParamContext.isSkipModuleCriteria()) {
				selectBuilder.skipModuleCriteria();
			}
			
//			if (FieldUtil.isSiteIdFieldPresent(module) && AccountUtil.getCurrentSiteId() > 0) {
//				selectBuilder.andCondition(CriteriaAPI.getCurrentSiteIdCondition(module));
//			}
			
			if (AccountUtil.getCurrentUser() == null) {
				User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
				if(user != null) {
					AccountUtil.getCurrentAccount().setUser(user);
				}
			}
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
			if (scopeCriteria != null) {
				selectBuilder.andCriteria(scopeCriteria);
			}
			
			boolean isLimitApplied = false;
			
			String parentIdString = null;
			Map<String, Condition> conditions = dbParamContext.getCriteria().getConditions();
			for(String key:conditions.keySet()) {
				Condition condition = conditions.get(key);
				if(condition.getFieldName().contains("parentId")) {
					parentIdString = condition.getValue();
					break;
				}
			}
			
			if(parentIdString != null && FacilioUtil.isNumeric(parentIdString) && module.getTypeEnum() == ModuleType.READING) {
				FacilioModule parentModule = ReadingsAPI.getParentModuleRelFromChildModule(module);
				if(parentModule != null) {
					List<Map<String, Object>> rec = QueryUtil.fetchRecord(parentModule.getName(), Long.parseLong(parentIdString));
					if(rec == null || rec.isEmpty()) {
						return null;
					}
				}
			}
			
			
			if(dbParamContext.getFieldName() != null && dbParamContext.getFieldCriteria() == null) {
				List<FacilioField> selectFields = new ArrayList<>();
				
				FacilioField selectOriginal = modBean.getField(dbParamContext.getFieldName(), module.getName());
				
				if(selectOriginal == null) {
					throw new Exception("Field is null for FieldName - "+dbParamContext.getFieldName() +" moduleName - "+module.getName());
				}
				
				FacilioField select = selectOriginal.clone();
				
				select.setName(RESULT_STRING);
				
				selectBuilder.andCustomWhere(select.getCompleteColumnName()+" is not null");
				
				if(dbParamContext.getAggregateOpperator() != null) {
					AggregateOperator expAggregateOpp = dbParamContext.getAggregateOpperator();
					
					FacilioField aggrField = select;
					if(dbParamContext.getAggregateFieldName() != null) {
						aggrField = modBean.getField(dbParamContext.getAggregateFieldName(), module.getName());
						selectFields.add(select);
					}
					
					selectBuilder.aggregate(expAggregateOpp, aggrField);
					
					if(expAggregateOpp.equals(BmsAggregateOperators.SpecialAggregateOperator.FIRST_VALUE)) {
						selectBuilder.limit(1);
						isLimitApplied = true;
					}
					else if(expAggregateOpp.equals(BmsAggregateOperators.SpecialAggregateOperator.LAST_VALUE)) {
						boolean isLastValueWithTimeRange = false;
						
						conditions = dbParamContext.getCriteria().getConditions();
						for(String key:conditions.keySet()) {
							Condition condition = conditions.get(key);
							if(condition.getFieldName().contains("ttime")) {
								isLastValueWithTimeRange = true;
								break;
							}
						}
						
						if(isLastValueWithTimeRange) {
							
							selectBuilder.limit(1);
							isLimitApplied = true;
							selectBuilder.orderBy("TTIME desc");
							
						}
						else {
							
							ReadingDataMeta readingDataMeta = null;
							if(cachedRDM != null && !cachedRDM.isEmpty()) {
								String key = ReadingsAPI.getRDMKey(Long.parseLong(parentIdString), modBean.getField(dbParamContext.getFieldName(), module.getName()));
								readingDataMeta = cachedRDM.get(key);
							}
							if(readingDataMeta == null) {
								readingDataMeta = ReadingsAPI.getReadingDataMeta(Double.valueOf(parentIdString).longValue(), select);
							}
							if(readingDataMeta == null) {
								throw new Exception("readingDataMeta is null for FieldName - "+dbParamContext.getFieldName() +" moduleName - "+module.getName()+" parentId - "+parentIdString);
							}
							ReadingsAPI.convertUnitForRdmData(readingDataMeta);
							long actualLastRecordedTime = CommonAPI.getActualLastRecordedTime(module);
							if(actualLastRecordedTime > 0) {
								if(readingDataMeta.getTtime() >= actualLastRecordedTime) {
									result = readingDataMeta.getValue();
								}
							}
							else {
								result = readingDataMeta.getValue();
							}
							if(result != null && result.toString().equals("-1")) {
								return null;
							}
							return result;
						}
					}
					if(module.getName().equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
						FacilioField markedField = modBean.getField("marked", module.getName());
						if(markedField != null) {
							selectBuilder.andCondition(CriteriaAPI.getCondition(markedField, Boolean.FALSE.toString(), BooleanOperators.IS));
						}
					}
					if(dbParamContext.isIgnoreMarkedReadings() && module.getName().equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
						
						FacilioField selectMarkedOriginal = modBean.getField("marked", module.getName());
						
						FacilioField selectMarked = selectMarkedOriginal.clone();
						
						if(selectMarked == null) {
							selectMarked = new FacilioField();
							selectMarked.setColumnName("MAX(MARKED)");
						}
						else {
							selectMarked.setColumnName("MAX("+selectMarked.getColumnName()+")");
						}
						selectMarked.setModule(null);
						selectMarked.setName("hasMarked");
						
						selectFields.add(selectMarked);
					}
				}
				else {
					selectFields.add(select);	// check
				}
				
				selectBuilder.select(selectFields);
			}
			else {
				List<FacilioField> selectFields = new ArrayList<>();
				selectFields.add(FieldFactory.getIdField(module));
				List<FacilioField> allFields = modBean.getAllFields(module.getName());
				for(FacilioField field: allFields) {
					if(field.getDataTypeEnum().isRelRecordField()) {
						selectBuilder.fetchSupplement((SupplementRecord) field);
					}
					else {
						selectFields.add(field);
					}
				}
				selectBuilder.select(selectFields);
			}
			
			if(dbParamContext.getSortByFieldName() != null) {
				String orderByString = null;
				if(dbParamContext.getSortByFieldName().equals(WorkflowV2Util.WORKFLOW_SORT_BY_AGGR_STRING) && dbParamContext.getAggregateFieldName() != null) {	// this is only for dev purpose.. should not expose for client
					orderByString = dbParamContext.getAggregateFieldName();
					if(dbParamContext.getSortOrder() != null) {
						orderByString = orderByString +" "+dbParamContext.getSortOrder();
					}
				}
				else {
					FacilioField orderByField = modBean.getField(dbParamContext.getSortByFieldName(), module.getName());
					orderByString = orderByField != null ? orderByField.getColumnName() : dbParamContext.getSortByFieldName();
					if(dbParamContext.getSortOrder() != null) {
						orderByString = orderByString +" "+dbParamContext.getSortOrder();
					}
				}
				
				selectBuilder.orderBy(orderByString);
			}
			
			if(dbParamContext.getLimit() > 0) {
				selectBuilder.limit(dbParamContext.getLimit());
				isLimitApplied = true;
			}
			if(dbParamContext.getOffset() >= 0) {
				selectBuilder.offset(dbParamContext.getOffset());
			}
			if(!isLimitApplied) {
				selectBuilder.limit(WorkflowV2Util.SELECT_DEFAULT_LIMIT);
			}
			if(dbParamContext.getGroupBy() != null) {
				FacilioField groupByField = modBean.getField(dbParamContext.getGroupBy(), module.getName());
				selectBuilder.groupBy(groupByField.getCompleteColumnName());
			}
			if(dbParamContext.getRange() != null) {
//				selectBuilder.range															// check
			}
			
			if(module != null && module.getName().equals("weather")) {						// temp handling must be removed (predicted weather data will be stored in same table with same module)
				selectBuilder.andCustomWhere("TTIME <= ?", DateTimeUtil.getCurrenTime());
			}
			if(AccountUtil.getCurrentOrg().getId() == 321l) {
				
				Long endTime = WorkflowUtil.demoCheckGetEndTime(module, dbParamContext.getCriteria());
				if(endTime > 0) {
					selectBuilder.andCustomWhere("TTIME <= ?",endTime);
				}
			}
			props = selectBuilder.getAsProps();
			
		}
		else {
			List records = LookupSpecialTypeUtil.getObjects(module.getName(), dbParamContext.getCriteria());
			if (records != null) {
				props = new ArrayList<>();
				for (Object record : records) {
					props.add(FieldUtil.getAsProperties(record));
				}
			}
		}
		LOGGER.debug("selectBuilder -- "+selectBuilder);
		LOGGER.debug("selectBuilder result -- "+props);
		
		if(props != null && !props.isEmpty()) {
			
			if(dbParamContext.getFieldCriteria() != null) {
				List<Map<String, Object>> passedData = new ArrayList<>();

				for(Map<String, Object> prop : props) {
					
					org.apache.commons.collections.Predicate Predicate = dbParamContext.getFieldCriteria().computePredicate(prop);
					if(Predicate.evaluate(prop)) {
						passedData.add(prop);
					}
				}
				if(dbParamContext.getAggregateOpperator() != null) {
					result = dbParamContext.getAggregateOpperator().getAggregateResult(passedData, dbParamContext.getFieldName());
				}
				else {
					result = passedData;
				}
			}
			else {
				
				if((dbParamContext.getFieldName() == null && dbParamContext.getAggregateString() == null) || (dbParamContext.getAggregateString() != null && dbParamContext.getAggregateFieldName() != null && dbParamContext.getGroupBy() != null)) {
					result = props;
				}
				else if(dbParamContext.getAggregateString() == null || dbParamContext.getAggregateString().equals("")) {
					List<Object> returnList = new ArrayList<>(); 
					for(Map<String, Object> prop:props) {
						String fieldName = !LookupSpecialTypeUtil.isSpecialType(module.getName()) ? RESULT_STRING : dbParamContext.getFieldName();
						returnList.add(prop.get(fieldName));
					}
					result = returnList;
				}
				else {
					// Temp check
					String name = LookupSpecialTypeUtil.isSpecialType(module.getName()) ? dbParamContext.getFieldName() : RESULT_STRING;
					result = props.get(0).get(name);
					
					if(dbParamContext.isIgnoreMarkedReadings() && module.getName().equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
						
						Object hasMarked = props.get(0).get("hasMarked");
						
						if(hasMarked != null && "1".equals(hasMarked.toString())) {
							//workflowContext.setTerminateExecution(true); need to handle
						}
	 				}
				}
			}
		}
		LOGGER.debug("EXP -- "+toString()+" RESULT -- "+result);
		return result;
	}
	
	public Object fetchDataFromCache(Map<String, List<Map<String, Object>>> cache,FacilioModule module,DBParamContext dbParamContext) throws Exception {
		
		Object result = null;
		
		if(cache != null) {
			
			String parentId = WorkflowUtil.getParentIdFromCriteria(dbParamContext.getCriteria());
			
			List<Map<String, Object>> cachedDatas = cache.get(WorkflowUtil.getCacheKey(module.getName(), parentId));

			if (cachedDatas != null && !cachedDatas.isEmpty()) {
				List<Map<String, Object>> passedData = new ArrayList<>();
				for(Map<String, Object> cachedData :cachedDatas) {
					org.apache.commons.collections.Predicate Predicate = dbParamContext.getCriteria().computePredicate(cachedData);
					if(Predicate.evaluate(cachedData)) {
						passedData.add(cachedData);
					}
				}
				
				if (!passedData.isEmpty()) {
					if(dbParamContext.getAggregateOpperator() != null) {
						result =  dbParamContext.getAggregateOpperator().getAggregateResult(passedData, dbParamContext.getFieldName());
					}
					if(result != null) {
						return result;
					}
				}
			}
		}
		return result;
	}

	@Override
	public String export(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		String viewName = (String) objects.get(1);
		
		Criteria criteria = null;
		if(objects.size() == 3) {
			criteria = (Criteria)objects.get(2);
		}
		boolean isS3Value = true; 
		if(FacilioProperties.isDevelopment()) {
			isS3Value = false;
		}
		ScriptUtil.fillCriteriaField(criteria, module.getName());
		String fileUrl = ExportUtil.exportModule(FileInfo.FileFormat.XLS, module.getName(), viewName, null,criteria, isS3Value, false, 2000);
		
		return fileUrl;
	}
	
	@Override
	public long exportAsFileId(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		String viewName = (String) objects.get(1);
		
		Criteria criteria = null;
		if(objects.size() == 3) {
			criteria = (Criteria)objects.get(2);
		}
		ScriptUtil.fillCriteriaField(criteria, module.getName());
		long fileUrl = ExportUtil.exportModuleAsFileId(FileInfo.FileFormat.XLS, module.getName(), viewName, null,criteria, false, 2000);
		
		return fileUrl;
	}


	@Override
	public Map<String, Object> asMap(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		FacilioModule module = (FacilioModule) objects.get(0);
		return FieldUtil.getAsProperties(module);
	}

	@Override
	public Criteria getViewCriteria(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		String viewName = (String) objects.get(1);
		
		FacilioChain viewDetailsChain = FacilioChainFactory.getViewDetailsChain();
		
		FacilioContext context = viewDetailsChain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
		
		viewDetailsChain.execute();
		
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		Criteria criteria = view.getCriteria();
		if (criteria != null) {
			return criteria.clone();
		}
		return null;
	}
	@Override
	public Long getId(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		FacilioModule module = (FacilioModule) objects.get(0);
		return module.getModuleId();
	}
	@Override
	public List<Map<String, Object>> getAllStates(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		return FieldUtil.getAsMapList(TicketAPI.getAllStatus(module, true), FacilioStatus.class);
	}
	
	@Override
	public void addNote(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		Long recordId = Long.parseLong(objects.get(1).toString());
		String noteString = (String) objects.get(2);
		
		NoteContext note = new NoteContext();
		
		note.setBody(noteString);
		note.setParentId(recordId);
		note.setParent(new ModuleBaseWithCustomFields(recordId));
		
		FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
		
		String notesModuleName = CommonCommandUtil.getModuleTypeModuleName(module.getName(), FacilioModule.ModuleType.NOTES);
		
		if(notesModuleName == null) {
			throw new Exception("Adding note is not possible, because note module is not available");
		}
		
		FacilioContext context = addNote.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, notesModuleName);
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME,module.getName());
		context.put(FacilioConstants.ContextNames.NOTE, note);

		addNote.execute();
	}
	
	@Override
	public void addAttachments(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		// TODO Auto-generated method stub

		FacilioModule module = (FacilioModule) objects.get(0);
		Long recordId = Long.parseLong(objects.get(1).toString());

		List<Object> filesList = null;
		if(objects.get(2) instanceof List) {
			filesList = (List<Object>) objects.get(2);
		}
		else {
			filesList = Collections.singletonList(objects.get(2).toString());
		}
		Boolean duplicateFiles = false;
		if (objects.size() > 3 && objects.get(3) != null && (objects.get(3) instanceof Boolean)) {
			duplicateFiles = (Boolean) objects.get(3);
		}

		List<Long> fileIds = new ArrayList<>();
		if (filesList != null && !filesList.isEmpty()) {
			for (Object fileObj : filesList) {
				if (fileObj == null) {
					continue;
				}
				try {
					Long fileId = Long.parseLong(fileObj.toString());
					if (duplicateFiles) {
						FileInfo fileInfo = FacilioFactory.getFileStore().getFileInfo(fileId);
						String fileName = fileInfo.getFileName();
						String contentType = fileInfo.getContentType();
						InputStream fileStream = FacilioFactory.getFileStore().readFile(fileId);
						fileId = FacilioFactory.getFileStore().addFileFromStream(fileName, contentType, fileStream);
					}
					fileIds.add(fileId);
				} catch (NumberFormatException e) {
					Long fileId = FacilioFactory.getFileStore().addFileFromURL(fileObj.toString());
					fileIds.add(fileId);
				}
			}
		}

		String customModuleAttachment = CommonCommandUtil.getModuleTypeModuleName(module.getName(), FacilioModule.ModuleType.ATTACHMENTS);

		if(customModuleAttachment == null) {
			throw new Exception("Adding attachment is not possible, because attachment module is not available");
		}

		FacilioChain addAttachmentChain = FacilioChainFactory.getAddAttachmentFromFileIdsChain();

		FacilioContext context = addAttachmentChain.getContext();

		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME,customModuleAttachment);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST,fileIds);
		context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME,module.getName());
		addAttachmentChain.execute();
	}
	
}
