
package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
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
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.util.QueryUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.util.WorkflowGlobalParamUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class FacilioModuleFunctionImpl implements FacilioModuleFunction {

	private static final Logger LOGGER = Logger.getLogger(FacilioModuleFunctionImpl.class.getName());
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
			
			InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(modBean.getAllFields(module.getName()));
			
			if(insertObject instanceof Map) {
				ModuleBaseWithCustomFields moBaseWithCustomFields = new ModuleBaseWithCustomFields();
				moBaseWithCustomFields.setData((Map<String, Object>) insertObject);
				insertRecordBuilder.addRecord(moBaseWithCustomFields);
			}
			else if (insertObject instanceof Collection) {
				List<Object> insertList = (List<Object>)insertObject;
				
				for(Object insert :insertList) {
					ModuleBaseWithCustomFields moBaseWithCustomFields = new ModuleBaseWithCustomFields();
					moBaseWithCustomFields.setData((Map<String, Object>) insert);
					insertRecordBuilder.addRecord(moBaseWithCustomFields);
				}
			}
			
			insertRecordBuilder.save();
			
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
		
		WorkflowV2Util.fillCriteriaField(criteria, module.getName());
		
		Map<String, Object> updateMap = (Map<String, Object>)objects.get(2);
		
		if (LookupSpecialTypeUtil.isSpecialType(module.getName())) {
			
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(modBean.getAllFields(module.getName()))
					.andCriteria(criteria);
			updateRecordBuilder.update(updateMap);
			
		}
		else {
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(modBean.getAllFields(module.getName()))
					.andCriteria(criteria);
			updateRecordBuilder.updateViaMap(updateMap);
		}
	}

	@Override
	public void delete(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		Criteria criteria = null;
		if(objects.get(1) instanceof DBParamContext) {
			criteria = ((DBParamContext) objects.get(1)).getCriteria();
		}
		else if (objects.get(1) instanceof Criteria) {
			criteria = (Criteria)objects.get(1);
		}
		
		if(criteria == null) {
			throw new Exception("criteria cannot be null during delete");
		}
		
		WorkflowV2Util.fillCriteriaField(criteria, module.getName());
		
		DeleteRecordBuilder<ModuleBaseWithCustomFields> delete = new DeleteRecordBuilder<>()
				.module(module)
				.andCriteria(criteria);
		
		delete.delete();
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
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		WorkflowV2Util.fillCriteriaField(dbParamContext.getCriteria(), module.getName());
		
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
			
			if (FieldUtil.isSiteIdFieldPresent(module) && AccountUtil.getCurrentSiteId() > 0) {
				selectBuilder.andCondition(CriteriaAPI.getCurrentSiteIdCondition(module));
			}
			
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
			
			if(parentIdString != null && FacilioUtil.isNumeric(parentIdString)) {
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
				List<FacilioField> fields = new ArrayList<>(modBean.getAllFields(module.getName()));
				fields.add(FieldFactory.getIdField(module));
				selectBuilder.select(fields);
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
		LOGGER.fine("selectBuilder -- "+selectBuilder);
		LOGGER.fine("selectBuilder result -- "+props);
		
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
		LOGGER.fine("EXP -- "+toString()+" RESULT -- "+result);
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
		WorkflowV2Util.fillCriteriaField(criteria, module.getName());
		String fileUrl = ExportUtil.exportModule(FileInfo.FileFormat.XLS, module.getName(), viewName, null,criteria, isS3Value, false, 2000);
		
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

}
