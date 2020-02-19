
package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
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
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
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
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.util.WorkflowGlobalParamUtil;

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
		
		Map<String, Object> updateMap = (Map<String, Object>)objects.get(2);
		
		UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(modBean.getAllFields(module.getName()))
				.andCriteria(criteria);
		updateRecordBuilder.updateViaMap(updateMap);
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
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.table(module.getTableName())
					.module(module)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
					.andCriteria(dbParamContext.getCriteria())
					;
			
			if (FieldUtil.isSiteIdFieldPresent(module) && AccountUtil.getCurrentSiteId() > 0) {
				selectBuilder.andCondition(CriteriaAPI.getCurrentSiteIdCondition(module));
			}
			
			
			if(dbParamContext.getFieldName() != null) {
				List<FacilioField> selectFields = new ArrayList<>();
				
				FacilioField select = modBean.getField(dbParamContext.getFieldName(), module.getName());
				
				if(select == null) {
					throw new Exception("Field is null for FieldName - "+dbParamContext.getFieldName() +" moduleName - "+module.getName());
				}
				
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
					}
					else if(expAggregateOpp.equals(BmsAggregateOperators.SpecialAggregateOperator.LAST_VALUE)) {
						boolean isLastValueWithTimeRange = false;
						
						Map<String, Condition> conditions = dbParamContext.getCriteria().getConditions();
						for(String key:conditions.keySet()) {
							Condition condition = conditions.get(key);
							if(condition.getFieldName().contains("ttime")) {
								isLastValueWithTimeRange = true;
								break;
							}
						}
						
						if(isLastValueWithTimeRange) {
							
							selectBuilder.limit(1);
							selectBuilder.orderBy("TTIME desc");
							
						}
						else {
							
							String parentIdString = null;
							conditions = dbParamContext.getCriteria().getConditions();
							for(String key:conditions.keySet()) {
								Condition condition = conditions.get(key);
								if(condition.getFieldName().contains("parentId")) {
									parentIdString = condition.getValue();
									break;
								}
							}
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
							return result;
						}
					}
					if(dbParamContext.isIgnoreMarkedReadings() && module.getName().equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
						
						FacilioField selectMarked = modBean.getField("marked", module.getName());
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
				FacilioField orderByField = modBean.getField(dbParamContext.getSortByFieldName(), module.getName());
				String orderByString = orderByField != null ? orderByField.getColumnName() : dbParamContext.getSortByFieldName();
				if(dbParamContext.getSortOrder() != null) {
					orderByString = orderByString +" "+dbParamContext.getSortOrder();
				}
				selectBuilder.orderBy(orderByString);
			}
			
			if(dbParamContext.getLimit() > 0) {
				selectBuilder.limit(dbParamContext.getLimit());
			}
			if(dbParamContext.getGroupBy() != null) {
				FacilioField groupByField = modBean.getField(dbParamContext.getGroupBy(), module.getName());
				selectBuilder.groupBy(groupByField.getColumnName());
			}
			if(dbParamContext.getRange() != null) {
//				selectBuilder.range															// check
			}
			
			if(module != null && module.getName().equals("weather")) {						// temp handling must be removed (predicted weather data will be stored in same table with same module)
				selectBuilder.andCustomWhere("TTIME <= ?", DateTimeUtil.getCurrenTime());
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
		
		return view.getCriteria();
	}

}
