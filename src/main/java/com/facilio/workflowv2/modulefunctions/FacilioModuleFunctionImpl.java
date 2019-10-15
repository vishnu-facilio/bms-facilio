
package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
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
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.DBParamContext;

public class FacilioModuleFunctionImpl implements FacilioModuleFunction {

	private static final Logger LOGGER = Logger.getLogger(FacilioModuleFunctionImpl.class.getName());
	private static final String RESULT_STRING = "result";
	@Override
	public void add(List<Object> objects) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(modBean.getAllFields(module.getName()));
		
		Object insertObject = objects.get(1);
		
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

	@Override
	public void update(List<Object> objects) throws Exception {
		
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
	public void delete(List<Object> objects) throws Exception {
		
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
	public Object fetch(List<Object> objects) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		DBParamContext dbParamContext = null;
		
		if(objects.get(1) instanceof DBParamContext) {
			dbParamContext = (DBParamContext) objects.get(1);
		}
		else if (objects.get(1) instanceof Criteria) {
			dbParamContext = new DBParamContext();
			dbParamContext.setCriteria((Criteria)objects.get(1));
		}
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = null;
		
		Map<String, List<Map<String, Object>>> cache = dbParamContext.getCache();
		
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
//							if(workflowContext.getCachedRDM() != null && !workflowContext.getCachedRDM().isEmpty()) {
//								String key = ReadingsAPI.getRDMKey(Long.parseLong(parentIdString), modBean.getField(fieldName, moduleName));
//								readingDataMeta = workflowContext.getCachedRDM().get(key);
//							}
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
					returnList.add(prop.get(RESULT_STRING));
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

	@Override
	public String export(List<Object> objects) throws Exception {
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
}
