package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.facilio.accounts.util.AccountUtil.FeatureLicense.NEW_READING_RULE;

@Getter
@Setter
public class ReadingAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private Map<String, List<ReadingContext>> readingMap;


	public String addHistoryReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS,true);
		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
		context.put(FacilioConstants.ContextNames.READINGS_MAP , getReadingMap());
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.IMPORT);
		context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
		FacilioChain importDataChain = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		importDataChain.execute(context);
		return SUCCESS;
	}


	public String addReading() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, getParentModule());
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		FacilioChain addReadingChain = TransactionChainFactory.getAddReadingsChain();
		addReadingChain.execute(context);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	public String getSubModuleRel() throws Exception {
		
		FacilioChain addReadingChain = ReadOnlyChainFactory.getSubModuleRelChain();
		FacilioContext context = addReadingChain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.ContextNames.MODULE_ID, getModuleId());
		
		addReadingChain.execute();
		
		
		List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.SUB_MODULES);

		setResult(FacilioConstants.ContextNames.SUB_MODULES, modules);
		return SUCCESS;
	}
	

	String resourceType;

	List<SensorRuleContext> sensorRuleList;

	
	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public String addReadings() throws Exception {
		switch(resourceType) {
		case "Asset":
			 addAssetCategoryReading();
			break;
		case "Space":
			 addSpaceCategoryReading();
			break;
			case "meter":
				addMeterReading();
				break;
		case "Building":
		case "floor":
		case "site":
			addSpaceTypeReading();
		}
		
		return SUCCESS;
	}
	
	public String addSpaceTypeReading() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentCategoryId());
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, ContextNames.SITE);
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		FacilioChain addReadingChain = TransactionChainFactory.addResourceReadingChain();
		addReadingChain.execute(context);
		
		return SUCCESS;
	}

	public String addSpaceCategoryReading() throws Exception {
		return addCategoryReading(FacilioConstants.ContextNames.SPACE_CATEGORY, ModuleFactory.getSpaceCategoryReadingRelModule());
	}
	
	public String addAssetCategoryReading() throws Exception {
		return addCategoryReading(FacilioConstants.ContextNames.ASSET_CATEGORY, ModuleFactory.getAssetCategoryReadingRelModule());
	}

	private String addCategoryReading(String parentModule, FacilioModule categoryReadingModule) throws Exception {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModule);
			context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, categoryReadingModule);
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getParentCategoryId());
			context.put(FacilioConstants.ContextNames.VALIDATION_RULES, getFieldReadingRules());
			context.put(ContextNames.SENSOR_RULE_LIST,getSensorRuleList());

			FacilioChain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
			addReadingChain.execute(context);

		return SUCCESS;
	}
	
	public String updateReading() throws Exception {
		FacilioContext context = new FacilioContext();
		
		List<List<ReadingRuleContext>> readingRules = getFieldReadingRules();
		if (readingRules != null && readingRules.size() > 0) {
			readingRules.stream().flatMap(List::stream).forEach((r) -> {
				r.setReadingFieldId(getField().getFieldId());
				r.setModuleId(getModuleId());
			});
			List<List<List<ActionContext>>> actionsList = readingRules.stream().map(l -> {return l.stream().map(ReadingRuleContext::getActions).collect(Collectors.toList());}).collect(Collectors.toList());
			
			context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
			context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
		}
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, getField());
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE_IDS, getDelReadingRulesIds());
		
		FacilioChain c = FacilioChainFactory.getUpdateReadingChain();
		c.execute(context);
		
		return SUCCESS;
	}
	
	public String addSpaceReading () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		
		FacilioChain addSpaceReadingChain = TransactionChainFactory.addResourceReadingChain();
		addSpaceReadingChain.execute(context);
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	public String getSpaceSpecificReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.EXCLUDE_FORECAST, excludeForecastReadings != null ? excludeForecastReadings : true);
		
		FacilioChain getSpaceSpecifcReadingsChain = FacilioChainFactory.getSpaceReadingsChain();
		getSpaceSpecifcReadingsChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		return SUCCESS;
	}
	
	public String getAssetSpecificReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		
		FacilioChain getSpaceSpecifcReadingsChain = FacilioChainFactory.getAssetReadingsChain();
		getSpaceSpecifcReadingsChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		return SUCCESS;
	}
	
	public String getSpaceSpecificLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		FacilioChain addCurrentOccupancy = FacilioChainFactory.getGetLatestSpaceReadingValuesChain();
		addCurrentOccupancy.execute(context);
		
		return setReadingValues(context);
	}
	public String getResourcesOccupantLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourcesId);
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, fieldName);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		FacilioChain occupantReadingValue = FacilioChainFactory.getResourcesOccupantReadingValuesChain();
		occupantReadingValue.execute(context);
		occupantReadingValues = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READINGS);
		setResult("readingValues", occupantReadingValues);
		return SUCCESS;
	}
	public String getAssetSpecificLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		FacilioChain latestAssetData = FacilioChainFactory.getGetLatestAssetReadingValuesChain();
		latestAssetData.execute(context);
		
		return setReadingValues(context);
	}
	
	private String setReadingValues(FacilioContext context) {
		readingData = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS);
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		
		readingAndValue = new JSONObject();
		readingAndValue.put("readingData", readingData);
		readingAndValue.put("readings", readings);
		return SUCCESS;
	}
	
	public String newReadingData() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		fields = modBean.getAllFields(moduleName);
		return SUCCESS;
	}
	
	public String updateReadingDataMeta() throws Exception{

		LOGGER.info("updated reading data meta : assetcategory id " + assetCategoryId + " , module ids : " + readingModuleIds);
		ReadingsAPI.updateReadingDataMeta(getAssetCategoryId(),getReadingModuleIds());
		setResult(FacilioConstants.ContextNames.MESSAGE, "success");
		return SUCCESS;
	}

	public String updateReadingDataMeta1() throws Exception{
		LOGGER.info("updated reading data meta : asset category id " + assetCategoryId);
		ReadingDataMetaAPI.updateReadingDataMeta(getAssetCategoryId());
		setResult(FacilioConstants.ContextNames.MESSAGE, "success");
		return SUCCESS;
	}
	
	public String updateRDM() throws Exception{
		ReadingsAPI.updateReadingDataMeta(readingDataMeta);
		setResult(FacilioConstants.ContextNames.MESSAGE, "success");
		return SUCCESS;
	}
	
	private Long assetCategoryId;
	public Long getAssetCategoryId() {
		return assetCategoryId;
	}
	public void setAssetCategoryId(Long assetCategoryId) {
		this.assetCategoryId = assetCategoryId;
	}

	private List<Long> readingModuleIds;
	public List<Long> getReadingModuleIds() {
		return readingModuleIds;
	}

	public void setReadingModuleIds(List<Long> readingModuleIds) {
		this.readingModuleIds = readingModuleIds;
	}
	
	private ReadingDataMeta readingDataMeta;
	public ReadingDataMeta getReadingDataMeta() {
		return readingDataMeta;
	}
	public void setReadingDataMeta(ReadingDataMeta readingDataMeta) {
		this.readingDataMeta = readingDataMeta;
	}

	public List getFormlayout()
	{
		return FormLayout.getNewAssetLayout(fields);
	}
	
	public String getSpaceReadings() throws Exception {
		return getCategoryReadings(ModuleFactory.getSpaceCategoryReadingRelModule());
	}
	
	public String getAssetReadings() throws Exception {
		return getCategoryReadings(ModuleFactory.getAssetCategoryReadingRelModule());
	}
	
	private String getCategoryReadings(FacilioModule module) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.FETCH_CONTROLLABLE_FIELDS, fetchControllableFields);
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getParentCategoryId());
		if (StringUtils.isNotEmpty(getReadingType())) {
			context.put(FacilioConstants.ContextNames.FILTER, getReadingType());
		}
		
		FacilioChain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if (getFetchValidationRules()) {
			fieldVsRules = (Map<Long, List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.VALIDATION_RULES);
		}
		
		return SUCCESS;
	}
	
	private Map<Long, List<ReadingRuleContext>> validationRules;
	public Map<Long, List<ReadingRuleContext>> getValidationRules() {
		return validationRules;
	}
	
	public void setValidationRules(Map<Long, List<ReadingRuleContext>> validationRules) {
		this.validationRules = validationRules;
	}
	
	
	private List<FacilioModule> readings;
	public List<FacilioModule> getReadings() {
		return readings;
	}
	public void setReadings(List<FacilioModule> readings) {
		this.readings = readings;
	}

	private long parentCategoryId = -1;
	public long getParentCategoryId() {
		return parentCategoryId;
	}
	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	private String parentModule;
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private String moduleName;

	public String getParentModule() {
		return parentModule;
	}
	public void setParentModule(String parentModule) {
		this.parentModule = parentModule;
	}
	
	private String readingName;
	public String getReadingName() {
		return readingName;
	}
	public void setReadingName(String readingName) {
		this.readingName = readingName;
	}
	
	private FacilioField field;
	public FacilioField getField() {
		return this.field;
	}
	
	public void setField(FacilioField field) {
		this.field = field;
	}
	
	private List<FacilioField> fields;
	public List<FacilioField> getFields() {
		return fields;
	}
	public void setFields(List<FacilioField> fields) {
		this.fields = fields;
	}
	
	private JSONObject fieldJson;
	
	public JSONObject getFieldJson() {
		return fieldJson;
	}

	public void setFieldJson(JSONObject fieldJson) throws Exception {
		this.fieldJson = fieldJson;
		setField(FieldUtil.parseFieldJson(this.fieldJson));
	}
	
	private JSONArray fieldJsons;

	public JSONArray getFieldJsons() {
		return fieldJsons;
	}

	public void setFieldJsons(JSONArray fieldJsons) throws Exception {
		this.fieldJsons = fieldJsons;
		setFields(FieldUtil.parseFieldJson(this.fieldJsons));
	}

	private long readingId = -1;
	public long getReadingId() {
		return readingId;
	}
	public void setReadingId(long readingId) {
		this.readingId = readingId;
	}
	
	public String addCurrentOccupancyReading() throws Exception {
		return addReadingData(FacilioConstants.ContextNames.CURRENT_OCCUPANCY_READING);
	}
	
	public String addAssignedOccupancyReading() throws Exception {
		return addReadingData(FacilioConstants.ContextNames.ASSIGNED_OCCUPANCY_READING);
	}
	
	public String addEnergyData() throws Exception {
		return addReadingData(FacilioConstants.ContextNames.ENERGY_DATA_READING);
	}
	
	public String v2addReadingData() throws Exception {
		addReadingData(getReadingName());
		setResult(ContextNames.RESULT, "success");
		return SUCCESS;
	}
	
	public String addReadingData() throws Exception {
		return addReadingData(getReadingName());
	}
	
	private String addReadingData(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.READINGS, getReadingValues());
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.WEB_ACTION);
		context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
		FacilioChain addCurrentOccupancy = ReadOnlyChainFactory.getAddOrUpdateReadingValuesChain();
		addCurrentOccupancy.execute(context);
		return SUCCESS;
	}
	
	private Map<String, ReadingDataMeta> occupantReadingValues;
	
	public Map<String, ReadingDataMeta> getOccupantReadingValues() {
		return occupantReadingValues;
	}

	public void setOccupantReadingValues(Map<String, ReadingDataMeta> occupantReadingValues) {
		this.occupantReadingValues = occupantReadingValues;
	}

	private List<ReadingContext> readingValues;
	public List<ReadingContext> getReadingValues() {
		return readingValues;
	}

	public void setReadingValues(List<ReadingContext> readingValues) {
		this.readingValues = readingValues;
	}

	public String getSpaceLatestReadingData() throws Exception {
  		return getCategoryLatestReadingData(ModuleFactory.getSpaceCategoryReadingRelModule());
  	}
	
	public String getAssetLatestReadingData() throws Exception {
  		return getCategoryLatestReadingData(ModuleFactory.getAssetCategoryReadingRelModule());
  	}
  	
  	private String getCategoryLatestReadingData(FacilioModule module) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getParentCategoryId());
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		FacilioChain addCurrentOccupancy = FacilioChainFactory.getGetLatestReadingValuesChain();
		addCurrentOccupancy.execute(context);
		
		readingData = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS);
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		
		readingAndValue = new JSONObject();
		readingAndValue.put("readingData", readingData);
		readingAndValue.put("readings", readings);
		return SUCCESS;
	}
  	
  	public String getSpaceReadingData() throws Exception {
  		return getCategoryReadingData(ModuleFactory.getSpaceCategoryReadingRelModule());
  	}
	
	public String getAssetReadingData() throws Exception {
  		return getCategoryReadingData(ModuleFactory.getAssetCategoryReadingRelModule());
  	}
  	
  	private String getCategoryReadingData(FacilioModule module) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getParentCategoryId());
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioModule> moduleList = new ArrayList<>();
		moduleList.add(modBean.getModule(moduleName));
		
		context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
		
		FacilioChain addCurrentOccupancy = FacilioChainFactory.getGetReadingValuesChain();
		addCurrentOccupancy.execute(context);
		
		readingData = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS);
		
		readingAndValue = new JSONObject();
		readingAndValue.put("readingData", readingData);
		return SUCCESS;
	}
  	
  	private JSONObject readingAndValue;
	public JSONObject getReadingAndValue() {
		return readingAndValue;
	}
	public void setReadingAndValue(JSONObject readingAndValue) {
		this.readingAndValue = readingAndValue;
	}

	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private List<Long> resourcesId;
	
	public List<Long> getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(List<Long> resourcesId) {
		this.resourcesId = resourcesId;
	}

	private Map<String, List<ReadingContext>> readingData;
	public Map<String, List<ReadingContext>> getReadingData() {
		return readingData;
	}
	public void setReadingData(Map<String, List<ReadingContext>> readingData) {
		this.readingData = readingData;
	}
	
	List<SpaceCategoryContext> spaceCategories;
	
	public List<SpaceCategoryContext> getSpaceCategories() {
		return spaceCategories;
	}
	
	public List<Long> getCategoryIds()
	{
		List<Long> spaceCategoryIds = new ArrayList();
		for(int i=0; i < spaceCategories.size();i++)
		{
			SpaceCategoryContext context = spaceCategories.get(i);
			Long categoryId = context.getId();
			spaceCategoryIds.add(categoryId);
		}
		return spaceCategoryIds;
	}

	public void setSpaceCategories(List<SpaceCategoryContext> spaceCategories) {
		this.spaceCategories = spaceCategories;
	}
	
	public void spaceCategoriesList() throws Exception {
		FacilioContext context = new FacilioContext();
		FacilioChain getSpaceCategoriesChain = FacilioChainFactory.getAllSpaceCategoriesChain();
		getSpaceCategoriesChain.execute(context);
		setSpaceCategories((List<SpaceCategoryContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
	}
	
	public String getAllReadings() throws Exception {
		List<Long> assetCategoryIds = getAssetCategoryIds();
		FacilioModule module = ModuleFactory.getAssetCategoryReadingRelModule();
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, assetCategoryIds);
		FacilioChain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);	
		
		return SUCCESS;
	}
	
	public String getSiteSpecificReadings(FacilioModule module,FacilioField field) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.READING_FIELD, field);
		FacilioChain getSiteSpecificReadingChain = FacilioChainFactory.getSiteSpecificReadingsChain();
		getSiteSpecificReadingChain.execute(context);
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);	
		
		return SUCCESS;
	}
	
	public String spaceType;
	
	
	public String getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}

	public String getAllSpaceTypeReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SPACE_TYPE,getSpaceType());
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.FETCH_CONTROLLABLE_FIELDS, fetchControllableFields);
		if (StringUtils.isNotEmpty(getReadingType())) {
			context.put(FacilioConstants.ContextNames.FILTER, getReadingType());
		}
		FacilioChain getSpaceTypeReading = FacilioChainFactory.getReadingsForSpaceTypeChain();
		getSpaceTypeReading.execute(context);
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);
		spaces = (Map<Long, BaseSpaceContext>)context.get(FacilioConstants.ContextNames.SPACES);
		return SUCCESS;
	}
	
	Map<Long, BaseSpaceContext> spaces;
	
	public Map<Long, BaseSpaceContext> getSpaces() {
		return spaces;
	}

	public void setSpaces(Map<Long, BaseSpaceContext> spaces) {
		this.spaces = spaces;
	}
	
	private List<Long> spaceId;
	public List<Long> getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(List<Long> spaceId) {
		this.spaceId = spaceId;
	}


	public String getAllSpaceReadings() throws Exception {
		
		spaceCategoriesList();
		List<Long> spaceCategoryIds = getCategoryIds();
		
		FacilioModule module = ModuleFactory.getSpaceCategoryReadingRelModule();
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, spaceCategoryIds);
		
		FacilioChain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);
		System.out.println(">>>>>>> getAllSpaceReadings.readings : "+readings);
		System.out.println(">>>>>>> getAllSpaceReadings.moduleMap : "+moduleMap);
		return SUCCESS;
	}
	
	public List<Long> getAssetCategoryIds() throws Exception
	{
		List<AssetCategoryContext> assetCategories =  AssetsAPI.getCategoryList();
		
		List<Long> assetCategoryIds = new ArrayList();
		
		for(int i=0; i< assetCategories.size();i++)
		{
			AssetCategoryContext context = assetCategories.get(i);
			Long catId = context.getId();
			assetCategoryIds.add(catId);
		}
		
		return assetCategoryIds;
	}
	
	public String getAllAssetReadings() throws Exception {
			
		List<Long> assetCategoryIds;
		if (this.parentCategoryIds != null && !this.parentCategoryIds.isEmpty()) {
			assetCategoryIds = this.parentCategoryIds;
		} else {
			assetCategoryIds = getAssetCategoryIds();
		}
		
		FacilioModule module = ModuleFactory.getAssetCategoryReadingRelModule();
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, assetCategoryIds);
		
		FacilioChain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);
		assetCount = (Map<String, Long>)context.get(FacilioConstants.ContextNames.COUNT);
		fieldVsRules = (Map<Long, List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.VALIDATION_RULES);

		return SUCCESS;
	}
	
	public String v2getAllAssetReadings() throws Exception {
		FacilioChain getCategoryReadingChain = ReadOnlyChainFactory.getAllAssetReadingsChain();
		FacilioContext context = getCategoryReadingChain.getContext();
		constructListContext(context);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, parentCategoryIds);
		
		if (getSearch() != null) {
			context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
		}
		
		if (StringUtils.isNotEmpty(getReadingType())) {
			context.put(FacilioConstants.ContextNames.FILTER, getReadingType());
		}

		getCategoryReadingChain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.COUNT));
		}
		else {
			setResult("readingFields", context.get(FacilioConstants.ContextNames.READING_FIELDS));
			setResult("fieldVsRules", context.get(FacilioConstants.ContextNames.VALIDATION_RULES));
		}
		
		return SUCCESS;
	}
	
	
	private List<Long> parentCategoryIds;
	public List<Long> getParentCategoryIds() {
		return this.parentCategoryIds;
	}
	
	public void setParentCategoryIds(List<Long> ids) {
		this.parentCategoryIds = ids;
	}
	
	Map<Long,List<FacilioModule>> moduleMap;
	public Map<Long, List<FacilioModule>> getModuleMap() {
		return moduleMap;
	}

	public void setModuleMap(Map<Long, List<FacilioModule>> moduleMap) {
		this.moduleMap = moduleMap;
	}
	
	private Map<String,Long> assetCount;
	public Map<String, Long> getAssetCount() {
		return assetCount;
	}
	public void setAssetCount(Map<String, Long> assetCount) {
		this.assetCount = assetCount;
	}

	public Map<Long, List<ReadingRuleContext>> getFieldVsRules() {
		return fieldVsRules;
	}

	public void setFieldVsRules(Map<Long, List<ReadingRuleContext>> fieldVsRules) {
		this.fieldVsRules = fieldVsRules;
	}

	private Map<Long, List<ReadingRuleContext>> fieldVsRules;

	private FormulaFieldContext formula;
	public FormulaFieldContext getFormula() {
		return formula;
	}
	public void setFormula(FormulaFieldContext formula) {
		this.formula = formula;
	}
	
	private String formulaFieldUnit;
	public String getFormulaFieldUnit() {
		return formulaFieldUnit;
	}
	public void setFormulaFieldUnit(String formulaFieldUnit) {
		this.formulaFieldUnit = formulaFieldUnit;
	}
	
	private int unitId = -1;
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	private int metricId = -1;
	public int getMetricId() {
		return metricId;
	}
	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}
	
	
	private List<ReadingRuleContext> readingRules;
	public List<ReadingRuleContext> getReadingRules() {
		return readingRules;
	}

	private List<List<ReadingRuleContext>> fieldReadingRules;

	public List<List<ReadingRuleContext>> getFieldReadingRules() {
		return this.fieldReadingRules;
	}

	public void setFieldReadingRules(List<List<ReadingRuleContext>> fieldReadingRules) {
		this.fieldReadingRules = fieldReadingRules;
	}

	public void setReadingRules(List<ReadingRuleContext> readingRules) {
		this.readingRules = readingRules;
	}
	

	public String addFormulaField() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formula);
		context.put(FacilioConstants.ContextNames.FORMULA_UNIT_STRING, formulaFieldUnit);
		context.put(FacilioConstants.ContextNames.FORMULA_UNIT, unitId);
		context.put(FacilioConstants.ContextNames.FORMULA_METRIC, metricId);
		context.put(FacilioConstants.ContextNames.READING_RULES_LIST,readingRules);
		context.put(FacilioConstants.ContextNames.VALIDATION_RULES, getFieldReadingRules());
		context.put(FacilioConstants.ContextNames.DEPENDENT_FIELD_RESOURCE_CONTEXT_LIST,dependentFieldResourceContextList);
//		context.put(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING,true);

		FacilioChain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
		addEnpiChain.execute(context);
		
		setResult(ContextNames.FORMULA_FIELD, formula);
		
		return SUCCESS;
	}
	
	public String editFormula() throws Exception {
		FacilioChain updateEnPIChain = TransactionChainFactory.updateFormulaChain();
		FacilioContext context = updateEnPIChain.getContext();
		
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formula);
		context.put(FacilioConstants.ContextNames.FORMULA_UNIT_STRING, formulaFieldUnit);
		context.put(FacilioConstants.ContextNames.FORMULA_UNIT, unitId);
		context.put(FacilioConstants.ContextNames.FORMULA_METRIC, metricId);
		context.put(FacilioConstants.ContextNames.DEPENDENT_FIELD_RESOURCE_CONTEXT_LIST,dependentFieldResourceContextList);
//		context.put(FacilioConstants.ContextNames.SKIP_FORMULA_HISTORICAL_SCHEDULING,true);
		
	    List<List<ReadingRuleContext>> readingRules = getFieldReadingRules();
	    List<List<List<ActionContext>>> actionsList = new ArrayList<>();
	    if (CollectionUtils.isNotEmpty(readingRules)) {
		    	actionsList = readingRules.stream().map(l -> {return l.stream().map(ReadingRuleContext::getActions).collect(Collectors.toList());}).collect(Collectors.toList());
		    	readingRules.stream().flatMap(List::stream).forEach((r) -> {
		    		// r.setReadingFieldId(getField().getFieldId());
		    		r.setModuleId(getModuleId());
		    	});
	    }
		context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
		context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE_IDS, getDelReadingRulesIds());
		
		WorkflowContext workflow = formula.getWorkflow();
		if(!workflow.isV2Script() && workflow!= null && workflow.getExpressions() == null) {
			WorkflowUtil.getWorkflowContextFromString(workflow.getWorkflowString(), workflow);
		}
		updateEnPIChain.execute();
		
		setResult(FacilioConstants.ContextNames.MESSAGE, context.get(FacilioConstants.ContextNames.RESULT));
		
		return SUCCESS;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String deleteFormula() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		
		FacilioChain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
		deleteEnPIChain.execute(context);
		
		setResult(FacilioConstants.ContextNames.MESSAGE, context.get(FacilioConstants.ContextNames.RESULT));
		
		return SUCCESS;
	}
	
	private List<FormulaFieldContext> formulaList;
	public List<FormulaFieldContext> getFormulaList() {
		return formulaList;
	}

	public void setFormulaList(List<FormulaFieldContext> formulaList) {
		this.formulaList = formulaList;
	}
	
	private FormulaFieldType type;
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = FormulaFieldType.valueOf(type);
	}

	public String allFormulasOfType() throws Exception {
		
		FacilioChain getAllENPIsChain = ReadOnlyChainFactory.getAllFormulasOfTypeChain();
		
		FacilioContext context = getAllENPIsChain.getContext();
		constructListContext(context);
		context.put(ContextNames.MODULE_NAME, ContextNames.FORMULA_FIELD);
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD_TYPE, type);
		
		getAllENPIsChain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.COUNT));
		}
		else {
			formulaList = (List<FormulaFieldContext>) context.get(FacilioConstants.ContextNames.FORMULA_LIST);
			fieldVsRules = (Map<Long, List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.VALIDATION_RULES);
			setResult(ContextNames.FORMULA_LIST, CollectionUtils.isNotEmpty(formulaList) ? formulaList : Collections.EMPTY_LIST);
		}

		return SUCCESS;
	}
	
	public String calculateFormulField() throws Exception {
		List<DateRange> intervals = DateTimeUtil.getTimeIntervals(startTime, endTime, minuteInterval);
		readingValues = FormulaFieldAPI.calculateFormulaReadings(resourceId, fieldName, fieldName, intervals, workflow, false, false);
		return SUCCESS;
	}
	
	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	private List<FormulaFieldResourceContext> dependentFieldResourceContextList;

	public List<FormulaFieldResourceContext> getDependentFieldResourceContextList() {
		return dependentFieldResourceContextList;
	}

	public void setDependentFieldResourceContextList(List<FormulaFieldResourceContext> dependentFieldResourceContextList) {
		this.dependentFieldResourceContextList = dependentFieldResourceContextList;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private int minuteInterval = -1;
	public int getMinuteInterval() {
		return minuteInterval;
	}
	public void setMinuteInterval(int minuteInterval) {
		this.minuteInterval = minuteInterval;
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	private Boolean excludeEmptyFields;
	public Boolean getExcludeEmptyFields() {
		return excludeEmptyFields;
	}
	public void setExcludeEmptyFields(Boolean excludeEmptyFields) {
		this.excludeEmptyFields = excludeEmptyFields;
	}
	
	private Boolean fetchControllableFields;
	
	private long moduleId;
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	public long getModuleId() {
		return this.moduleId;
	}
	
	private List<Long> delReadingRulesIds;
	public void setDelReadingRulesIds(List<Long> delReadingRulesIds) {
		this.delReadingRulesIds = delReadingRulesIds;
	}
	
	public List<Long> getDelReadingRulesIds() {
		return this.delReadingRulesIds;
	}
	
	
	
	/**********  V2 apis *************/
	
	public String getFormulaField() throws Exception {
		formula = FormulaFieldAPI.getFormulaField(id, true);
		setResult(FacilioConstants.ContextNames.FORMULA_FIELD, formula);			
		return SUCCESS;
	}
	
	private boolean historicalAlarm = false;
	public boolean isHistoricalAlarm() {
		return historicalAlarm;
	}
	public void setHistoricalAlarm(boolean historicalAlarm) {
		this.historicalAlarm = historicalAlarm;
	}
	
	private boolean skipOptimisedWorkflow = false;

	public boolean isSkipOptimisedWorkflow() {
		return skipOptimisedWorkflow;
	}

	public void setSkipOptimisedWorkflow(boolean skipOptimisedWorkflow) {
		this.skipOptimisedWorkflow = skipOptimisedWorkflow;
	}

	public String calculateHistoryForFormula() throws Exception {
		
		if(startTime >= endTime)
		{
			throw new Exception("Start time should be less than the Endtime");
		}
		
		try {
			
			FacilioChain historicalCalculation = TransactionChainFactory.historicalFormulaCalculationChain();
			FacilioContext context = historicalCalculation.getContext();
			context.put(FacilioConstants.ContextNames.FORMULA_FIELD, id);
			context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
			context.put(FacilioConstants.ContextNames.RESOURCE_LIST, historicalLoggerAssetIds);
			context.put(FacilioConstants.ContextNames.IS_INCLUDE,isInclude);
			context.put(FacilioConstants.OrgInfoKeys.CALCULATE_VM_THROUGH_FORMULA,calculateVmThroughFormula);
			context.put(FacilioConstants.ContextNames.SKIP_OPTIMISED_WF, skipOptimisedWorkflow);
			
			historicalCalculation.execute();
		}
		catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Error occured during formula field historical calculation", e);
			throw e;
		}
		
		setResult("success", "Historical Formula Calculation is started and will be notified when done");
		
		return SUCCESS;
	}
	
	public String getHistoricalFormulaFieldParentLoggers() throws Exception {
		List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();	
		Collection<LoggerContext> historicalFormulaFieldParentLoggerList = LoggerAPI.getAllParentLoggerAPI(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, getFormulaId());
		setResult("historicalFormulaFieldParentLoggers", historicalFormulaFieldParentLoggerList);
		return SUCCESS;		
	}
	
	public String getHistoricalFormulaFieldChildLoggers() throws Exception {
		List<FacilioField> loggerfields = FieldFactory.getFormulaFieldHistoricalLoggerFields();		
		List<LoggerContext> historicalFormulaFieldChildLoggerList = LoggerAPI.getGroupedLogger(ModuleFactory.getFormulaFieldHistoricalLoggerModule(), loggerfields, getLoggerGroupId());
		setResult("historicalFormulaFieldChildLoggers", historicalFormulaFieldChildLoggerList);
		return SUCCESS;	
	}
	
	public String convertVMToFormulaMig() throws Exception {
		EnergyMeterUtilAPI.convertVMToFormulaMig();
		setResult("success", "VM Formula Migration Done ");
		return SUCCESS;	
	}
	
	private long ruleId;
	
	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	private long formulaId;

	public long getFormulaId() {
		return formulaId;
	}

	public void setFormulaId(long formulaId) {
		this.formulaId = formulaId;
	}

	private long parentRuleLoggerId;
	public long getParentRuleLoggerId() {
		return parentRuleLoggerId;
	}

	public void setParentRuleLoggerId(long parentRuleLoggerId) {
		this.parentRuleLoggerId = parentRuleLoggerId;
	}

	private long parentRuleResourceId;
	public long getParentRuleResourceId() {
		return parentRuleResourceId;
	}

	public void setParentRuleResourceId(long parentRuleResourceId) {
		this.parentRuleResourceId = parentRuleResourceId;
	}

	private long loggerGroupId;
	public long getLoggerGroupId() {
		return loggerGroupId;
	}

	public void setLoggerGroupId(long loggerGroupId) {
		this.loggerGroupId = loggerGroupId;
	}
	
	private Boolean isInclude;

	public Boolean getIsInclude() {
		return isInclude;
	}
	
	public void setIsInclude(Boolean isInclude) {
		this.isInclude = isInclude;
	}
	
	private Boolean calculateVmThroughFormula;

	public Boolean getCalculateVmThroughFormula() {
		return calculateVmThroughFormula;
	}

	public void setCalculateVmThroughFormula(Boolean calculateVmThroughFormula) {
		this.calculateVmThroughFormula = calculateVmThroughFormula;
	}

	private Integer ruleJobType;	

	public Integer getRuleJobType() {
		return ruleJobType;
	}

	public void setRuleJobType(Integer ruleJobType) {
		this.ruleJobType = ruleJobType;
	}

	private List<Long> internalRuleIds;
	
	public List<Long> getInternalRuleIds() {
		return internalRuleIds;
	}

	public void setInternalRuleIds(List<Long> internalRuleIds) {
		this.internalRuleIds = internalRuleIds;
	}
	
	private String internalRuleId;

	public String getInternalRuleId() {
		return internalRuleId;
	}

	public void setInternalRuleId(String internalRuleId) {
		this.internalRuleId = internalRuleId;
	}

	public List<Long> getIdListFromInternalRuleId() {
		List<String> listofStringNumbers = Arrays.asList(internalRuleId.split(","));
		List<Long> listofRuleIds = new ArrayList<>();
		for (String number : listofStringNumbers) {
			listofRuleIds.add(Long.valueOf(number.trim()));
		}
		return listofRuleIds;
	}
	
	public JSONObject loggerInfo;
	
	public JSONObject getLoggerInfo() {
		return loggerInfo;
	}

	public void setLoggerInfo(JSONObject loggerInfo) {
		this.loggerInfo = loggerInfo;
	}

	public String runThroughRule() throws Exception {	
		try {
			FacilioChain runThroughRuleChain = TransactionChainFactory.runThroughHistoricalRuleChain(AccountUtil.isFeatureEnabled(NEW_READING_RULE));
			FacilioContext context = runThroughRuleChain.getContext();
			
			if(getLoggerInfo() == null) {
				JSONObject loggerRuleInfo = new JSONObject();
				loggerRuleInfo.put("rule", getId());
				loggerRuleInfo.put("resource", getHistoricalLoggerAssetIds());
				setLoggerInfo(loggerRuleInfo);
			}
			context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
			context.put(FacilioConstants.ContextNames.RULE_JOB_TYPE, getRuleJobType());
			context.put(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER_PROPS, getLoggerInfo());
			context.put(FacilioConstants.ContextNames.IS_INCLUDE, isInclude);
//			context.put(JobConstants.LOGGER_LEVEL, 2); //debug
			runThroughRuleChain.execute();
			
			setResult("success", "Rule evaluation for the readings in the given period has been started");	
		}
		catch(Exception userException) {
			setResult("Failed", userException.getMessage());
		}	
		
		return SUCCESS;
	}
	
	public String runHistoricalRule() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
		context.put(FacilioConstants.ContextNames.RULE_JOB_TYPE,getRuleJobType());
		
		List<Long> ruleIds = null;
		if(internalRuleIds != null && !internalRuleIds.isEmpty()) {
			ruleIds = internalRuleIds;
		}
		else if(internalRuleId != null && !internalRuleId.isEmpty() && StringUtils.isNotEmpty(internalRuleId)) {
			ruleIds = getIdListFromInternalRuleId();
		}
		
		if(ruleIds != null && !ruleIds.isEmpty()) {
			for(long ruleId:ruleIds)
			{
				JSONObject loggerRuleInfo = new JSONObject();
				loggerRuleInfo.put("rule", ruleId);
				context.put(FacilioConstants.ContextNames.HISTORICAL_RULE_LOGGER_PROPS, loggerRuleInfo);
				FacilioChain runThroughRuleChain = TransactionChainFactory.runThroughHistoricalRuleChain();
				runThroughRuleChain.execute(context);
			}	
			setResult("success", "Historical rule evaluation in the given period has been started for all the given rules");
		}
		else {
			setResult("success", "Requested RuleId List is Empty");			
		}
		return SUCCESS;	
	}
	
	public String runThroughSensorRule() throws Exception {	
		try {
			if(startTime > endTime) {
				throw new Exception("Start time should be less than the Endtime");
			}
			List<Long> resourceIds = null;
			if(historicalLoggerAssetIds != null && !historicalLoggerAssetIds.isEmpty()) {
				resourceIds = historicalLoggerAssetIds;
			}
			else if(historicalLoggerAssetId != null && !historicalLoggerAssetId.isEmpty() && StringUtils.isNotEmpty(historicalLoggerAssetId)) {
				resourceIds = getIdListFromAssetId();
			}
			FacilioChain runThroughRuleChain = TransactionChainFactory.runThroughSensorRuleChain();
			FacilioContext context = runThroughRuleChain.getContext();
			
			context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
			context.put(FacilioConstants.ContextNames.ASSET_CATEGORY, getId());
			context.put(FacilioConstants.ContextNames.ASSET_ID, resourceIds);
			runThroughRuleChain.execute();
			
			setResult("success", "Sensor Rule evaluation for the readings in the given period has been started");	
		}
		catch(Exception userException) {
			setResult("Failed", userException.getMessage());	
		}	
		
		return SUCCESS;
	}
	
	public String addRollUpFieldMig() throws Exception
	{
		RollUpFieldUtil.addRollUpForBaseSpaceFields();
		RollUpFieldUtil.addRollUpForSubSpaceFields();
		setResult("success", "Rollup Fields added for the current org");	
		return SUCCESS;	
	}
	
	public String runRollUpFieldRule() throws Exception
	{
		List<Long> ruleIds = null;
		if(internalRuleIds != null && !internalRuleIds.isEmpty()) {
			ruleIds = internalRuleIds;
		}
		else if(internalRuleId != null && !internalRuleId.isEmpty() && StringUtils.isNotEmpty(internalRuleId)) {
			ruleIds = getIdListFromInternalRuleId();
		}
		
		if(ruleIds != null && !ruleIds.isEmpty()) {
			RollUpFieldUtil.runInternalBulkRollUpFieldRules(ruleIds);	
			setResult("success", "Rollup Field bulk execution has been started for the given rules");
		}
		else {
			setResult("success", "Requested RuleId List is Empty");			
		}
		return SUCCESS;	
	}
	
	public String runRollUpFieldForModule() throws Exception {
		RollUpFieldUtil.runRollUpFieldForModule(moduleName);
		setResult("Module Data_Migration","Success");	
		return SUCCESS;
	}
	
	public String calculateAggregatedEnergyConsumption() throws Exception
	{
		if(startTime > endTime) {
			throw new Exception("Start time should be less than the Endtime");
		}
		List<Long> resourceIds = null;
		if(historicalLoggerAssetIds != null && !historicalLoggerAssetIds.isEmpty()) {
			resourceIds = historicalLoggerAssetIds;
		}
		else if(historicalLoggerAssetId != null && !historicalLoggerAssetId.isEmpty() && StringUtils.isNotEmpty(historicalLoggerAssetId)) {
			resourceIds = getIdListFromAssetId();
		}
		AggregatedEnergyConsumptionUtil.calculateHistoryForAggregatedEnergyConsumption(startTime, endTime, resourceIds);
		setResult("success", "Historical for AggregatedEnergyConsumption has been started.");
		return SUCCESS;	
	}
	
	public String runMigForDemoReadingsRollUp() throws Exception
	{
		try{
        	LOGGER.info("DemoSingleRollUpYearlyCommand runMigForDemoReadingsRollUp start orgid -- "+AccountUtil.getCurrentOrg().getId());
			FacilioTimer.scheduleOneTimeJobWithDelay(321l, "DemoSingleRollUpYearlyJob", 30, "facilio");				
        	LOGGER.info("DemoSingleRollUpYearlyCommand runMigForDemoReadingsRollUp end orgid -- "+AccountUtil.getCurrentOrg().getId());
    		setResult("success", "Migration for DemoReadingsRollUp is done.");
        }
        catch(Exception e){
        	LOGGER.error("DemoSingleRollUpYearlyCommand Error Mig" +e+ "orgid -- "+AccountUtil.getCurrentOrg().getId());
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
    		setResult("success", "Failed DemoReadingsRollUp Migration.");
        }
		return SUCCESS;	
	}
	
	public String runDefaultFieldsMigration() throws Exception
	{
		try{
    	 	LOGGER.info("NewSystemFieldsMigration Started For -- "+AccountUtil.getCurrentOrg().getId());     
            FacilioChain chain = TransactionChainFactory.runDefaultFieldsMigration();
        	chain.execute();
 			LOGGER.info("NewSystemFieldsMigration Completed For -- "+AccountUtil.getCurrentOrg().getId());
    		setResult("success", "NewSystemFieldsMigration Done.");		
        }
        catch(Exception e){
        	LOGGER.error("NewSystemFieldsMigration Error Mig -- " +e+ "orgid -- "+AccountUtil.getCurrentOrg().getId());
    		setResult("success", "Failed NewSystemFieldsMigration.");
        }
		return SUCCESS;	
	}
	
	public String getWorkflowRuleParentLoggers() throws Exception {
		Collection<WorkflowRuleHistoricalLoggerContext> parentWorkflowRuleHistoricalLoggerList = WorkflowRuleHistoricalLoggerUtil.getAllParentWorkflowRuleHistoricalLogger(getRuleId());
		setResult("workflowRuleParentHistoricalLoggers", parentWorkflowRuleHistoricalLoggerList);
		return SUCCESS;
	}
	
	public String getWorkflowRuleChildLoggers() throws Exception {
		List<WorkflowRuleHistoricalLoggerContext> childWorkflowRuleHistoricalLoggerList = WorkflowRuleHistoricalLoggerUtil.getGroupedWorkflowRuleHistoricalLogger(getLoggerGroupId());
		setResult("workflowRuleChildHistoricalLoggers", childWorkflowRuleHistoricalLoggerList);
		return SUCCESS;	
	}
	
	public String getWorkflowRuleLoggers() throws Exception {

		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}  
		
		FacilioChain workflowRuleLoggersChain = ReadOnlyChainFactory.getWorkflowRuleLoggersCommand();
		FacilioContext constructListContext = workflowRuleLoggersChain.getContext();
		constructListContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, getRuleId());
		constructListContext.put(FacilioConstants.ContextNames.RULE_JOB_TYPE, getRuleJobType());
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getWorkflowRuleLoggerModule().getName());
		constructListContext(constructListContext);
		workflowRuleLoggersChain.execute();

		setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGGERS, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGGERS));
		return SUCCESS;		
	}
	
	public String getWorkflowRuleResourceLoggers() throws Exception {

		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}  
		
		FacilioChain workflowRuleResourceLoggersChain = ReadOnlyChainFactory.getWorkflowRuleResourceLoggersCommand();
		FacilioContext constructListContext = workflowRuleResourceLoggersChain.getContext();
		constructListContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE_PARENT_LOGGER_ID, getParentRuleLoggerId());
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getWorkflowRuleResourceLoggerModule().getName());
		constructListContext(constructListContext);
		workflowRuleResourceLoggersChain.execute();

		setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_RESOURCE_LOGGERS, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_RESOURCE_LOGGERS));
		return SUCCESS;		
	}
	
	public String getWorkflowRuleResourceHistoricalLogs() throws Exception {

		if(getPerPage() < 0) {
			setPerPage(50);
		}
		if(getPage() < 0) {
			setPage(1);
		}  
		
		FacilioChain workflowRuleHistoricalLogsChain = ReadOnlyChainFactory.getWorkflowRuleHistoricalLogsCommand();
		FacilioContext constructListContext = workflowRuleHistoricalLogsChain.getContext();
		constructListContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE_RESOURCE_ID, getParentRuleResourceId());
		constructListContext(constructListContext);
		constructListContext.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getWorkflowRuleResourceLoggerModule().getName());
		workflowRuleHistoricalLogsChain.execute();

		setResult(FacilioConstants.ContextNames.WORKFLOW_RULE_HISTORICAL_LOGS, constructListContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_HISTORICAL_LOGS));
		return SUCCESS;		
	}
	
	public String historicalScheduledRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, id);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
		
		FacilioChain runThroughRuleChain = TransactionChainFactory.historicalScheduledRuleChain();
		runThroughRuleChain.execute(context);
		
		setResult("success", "Historical run for the scheduled rule in the given period has been started");
		
		return SUCCESS;
	}
	
	public String getFormulaFromReadingField () throws Exception {
		formula = FormulaFieldAPI.getFormulaFieldFromReadingField(id);
		setResult(FacilioConstants.ContextNames.FORMULA_FIELD, formula);			
		return SUCCESS;
	}

	public String v2GetLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.EXCLUDE_FORECAST, excludeForecastReadings != null ? excludeForecastReadings : true);
		context.put(FacilioConstants.ContextNames.FETCH_READING_INPUT_VALUES, fetchInputValues);
		context.put(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI, true);
		context.put(ContextNames.FIELD_ID, fieldId);

		if (isFetchCount()) {
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, isFetchCount());
		}
		if (getSearch() != null) {
			context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
		}
		if (getPerPage() != -1) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());

			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
		if (StringUtils.isNotEmpty(getReadingType())) {
			context.put(FacilioConstants.ContextNames.FILTER, getReadingType());
		}

		FacilioChain latestAssetData = ReadOnlyChainFactory.fetchLatestReadingDataChain();
		latestAssetData.execute(context);

		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.COUNT));
		} else {
			setResult("readingValues", context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		}


		return SUCCESS;
	}

	public String v2GetLatestRelatedReadingData() throws Exception {

		V3Util.throwRestException(getRelMapLinkName() == null, ErrorCode.RESOURCE_NOT_FOUND, "relMapLinkName cannot be null");
		V3Util.throwRestException(getResourceId() == -1, ErrorCode.RESOURCE_NOT_FOUND, "resourceId cannot be null");

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, resourceId);
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.EXCLUDE_FORECAST, excludeForecastReadings != null ? excludeForecastReadings : true);
		context.put(FacilioConstants.ContextNames.FETCH_READING_INPUT_VALUES, fetchInputValues);
		context.put(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI, true);
		context.put(ContextNames.FIELD_ID, fieldId);

		if (isFetchCount()) {
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, isFetchCount());
		}
		if (getSearch() != null) {
			context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
		}
		if (getPerPage() != -1) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());

			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
		if (StringUtils.isNotEmpty(getReadingType())) {
			context.put(FacilioConstants.ContextNames.FILTER, getReadingType());
		}

		context.put(ContextNames.RELATION_MAPPING, getRelMapLinkName());
		FacilioChain latestAssetData = ReadOnlyChainFactory.fetchLatestRelationReadingDataChain();
		latestAssetData.execute(context);

		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.COUNT));
		} else {
			List<Long> relIds = (List<Long>) context.get(ContextNames.PARENT_ID_LIST);
			List<ResourceContext> resourcesList = CollectionUtils.isNotEmpty(relIds) ? ResourceAPI.getResources(relIds, true) : new ArrayList<>();
			Map<Long, ResourceContext> resMap = resourcesList.stream().collect(Collectors.toMap(ResourceContext::getId, Function.identity()));
			setResult("readingValues", context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
			setResult("resources", resMap);
		}

		return SUCCESS;
	}
	
	public String v2GetLatestReadingDataForSpaces() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, spaceId);
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.FETCH_READING_INPUT_VALUES, fetchInputValues);
		context.put(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI, true);
		
		FacilioChain latestAssetData = ReadOnlyChainFactory.fetchLatestReadingDataChain();
		latestAssetData.execute(context);
		
		setResult("readingValues", context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		
		return SUCCESS;
	}

	public String getReadingsRelationships() throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(moduleName);

		Map<RelationRequestContext, List<Long>> allRelationsWithReverseResourceList = RelationUtil.getAllRelationsWithReverseResourceList(module, resourceId);
		List<RelationRequestContext> rels = new ArrayList<>();

		for(Map.Entry<RelationRequestContext, List<Long>> entry : allRelationsWithReverseResourceList.entrySet()) {
			RelationRequestContext relContext = entry.getKey();
			List<Long> resourceList = entry.getValue();
			System.out.println(resourceList);

			FacilioChain latestDataChain = ReadOnlyChainFactory.fetchLatestReadingDataChain();
			FacilioContext ctx = latestDataChain.getContext();
			ctx.put(FacilioConstants.ContextNames.PARENT_ID_LIST, resourceList);
			ctx.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, Boolean.FALSE);
			ctx.put(FacilioConstants.ContextNames.FETCH_READING_INPUT_VALUES, Boolean.TRUE);
			ctx.put(FacilioConstants.ContextNames.IS_FETCH_RDM_FROM_UI, Boolean.TRUE);
			latestDataChain.execute();

			List<ReadingDataMeta> metaDataList = (List<ReadingDataMeta>) ctx.get(ContextNames.READING_DATA_META_LIST);
			if (CollectionUtils.isNotEmpty(metaDataList)) {
				boolean isReadingField = metaDataList.stream()
						.anyMatch(rdm -> rdm.getField().getModule() != null && rdm.getField().getModule().getTypeEnum() == FacilioModule.ModuleType.READING);
				if(isReadingField) {
					rels.add(relContext);
				}
			}
		}

		setResult("result", rels);

		return SUCCESS;
	}
	
	public String v2GetLastValue() throws Exception {

		
		FacilioChain chain = ReadOnlyChainFactory.getRDMforRestAPI();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.FIELD_ID, fieldId);
		
		chain.execute();
		
		if(fieldId > 0) {
			
			setResult("lastestdata",context.get(FacilioConstants.ContextNames.READING_DATA_META));
		}
		else {
			setResult("lastestdata",context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		}
		
		return SUCCESS;
	}
	
	private Boolean fetchInputValues;
	public Boolean getFetchInputValues() {
		return fetchInputValues;
	}
	public void setFetchInputValues(Boolean fetchInputValues) {
		this.fetchInputValues = fetchInputValues;
	}

	private static final Logger LOGGER = LogManager.getLogger(ReadingAction.class.getName());


	public String setReading () throws Exception {
		PublishData data = IoTMessageAPI.setReadingValue(assetId, fieldId, value);
		if (data != null) {
			setResult("data", data);
		}
		
		return SUCCESS;
	}
	
	public String getLiveReading () throws Exception {
		Map<String, Object> instance = TimeSeriesAPI.getMappedInstance(assetId,fieldId);
		if (instance != null && AccountUtil.getCurrentOrg()!= null) {
			IoTMessageAPI.publishIotMessage(Collections.singletonList(instance), IotCommandType.GET);
		}
		return SUCCESS;
	}
	
	public String setReadingInputValues() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READING_DATA_META_ID, readingId);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, inputValues);
		
		FacilioChain chain = TransactionChainFactory.getSetReadingInputValuesChain();
		chain.execute(context);
		return SUCCESS;
	}
	
	List<Map<String, Object>> inputValues;
	public List<Map<String, Object>> getInputValues() {
		return inputValues;
	}
	public void setInputValues(List<Map<String, Object>> inputValues) {
		this.inputValues = inputValues;
	}

	long assetId;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	List<Long> historicalLoggerAssetIds;
	public List<Long> getHistoricalLoggerAssetIds() {
		return historicalLoggerAssetIds;
	}
	public void setHistoricalLoggerAssetIds(List<Long> historicalLoggerAssetIds) {
		this.historicalLoggerAssetIds = historicalLoggerAssetIds;
	}
	
	String historicalLoggerAssetId;
	public String getHistoricalLoggerAssetId() {
		return historicalLoggerAssetId;
	}

	public void setHistoricalLoggerAssetId(String historicalLoggerAssetId) {
		this.historicalLoggerAssetId = historicalLoggerAssetId;
	}

	public List<Long> getIdListFromAssetId() {
		List<String> listofStringNumbers = Arrays.asList(historicalLoggerAssetId.split(","));
		List<Long> listofResourceIds = new ArrayList<>();
		for (String number : listofStringNumbers) {
			listofResourceIds.add(Long.valueOf(number.trim()));
		}
		return listofResourceIds;
	}
	
	long fieldId;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	private String search;
	public void setSearch(String search) {
		this.search = search;
	}
	public String getSearch() {
		return this.search;
	}

	private int page;
	public void setPage(int page) {
		this.page = page;
	}
	public int getPage() {
		return this.page;
	}

	public int perPage = -1;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public int getPerPage() {
		return this.perPage;
	}
	
	// connected, logged, writable, readable, available, formula or nonformula
	private String readingType;
	public String getReadingType() {
		return readingType;
	}
	public void setReadingType(String readingType) {
		this.readingType = readingType;
	}

	@Getter @Setter
	private String relMapLinkName;

	private Boolean fetchCount;
	public boolean isFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(boolean fetchCount) {
		this.fetchCount = fetchCount;
	}

	public String resetReading() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST, resetCounterMetaList);
		FacilioChain chain = TransactionChainFactory.getResetReadingsChain();
		chain.execute(context);
		return SUCCESS;
	}
	
	public String getResetReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		FacilioChain chain = ReadOnlyChainFactory.getResetCounterMetaChain();
		chain.execute(context);
		resetCounterMetaList = (List<ResetCounterMetaContext>)context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		setResult("resetCounterMetaList", resetCounterMetaList);
		return SUCCESS;
	}
	List<ResetCounterMetaContext> resetCounterMetaList;

	public List<ResetCounterMetaContext> getResetCounterMetaList() {
		return resetCounterMetaList;
	}

	public void setResetCounterMetaList(List<ResetCounterMetaContext> resetCounterMetaList) {
		this.resetCounterMetaList = resetCounterMetaList;
	}
	
	private Boolean assetReading;
	public Boolean getAssetReading() {
		return assetReading;
	}
	public void setAssetReading(Boolean assetReading) {
		this.assetReading = assetReading;
	}

	private Boolean site;

	public String v2getCategoryReadings() throws Exception {
		setParentCategoryId(id);
		if (assetReading != null && assetReading == false) {
			getSpaceReadings();
		}
		else if(BooleanUtils.isTrue(site)){
			getAllSpaceTypeReadings();
		}
		else {
			getAssetReadings();
		}
		// Temp...needs to move this to reading list command
		List<FacilioField> fields = new ArrayList<>();
		if (this.readings != null) {
			fields = this.readings.stream().map(r -> r.getFields()).flatMap(r -> r.stream()).collect(Collectors.toList());
		}
		setResult("readings", fields);
		if (getFetchValidationRules()) {
			setResult("fieldVsRules", fieldVsRules);
		}
		return SUCCESS;
	}
	
	private Boolean fetchValidationRules;
	public Boolean getFetchValidationRules() {
		if (fetchValidationRules == null) {
			fetchValidationRules = false;
		}
		return fetchValidationRules;
	}
	public void setFetchValidationRules(Boolean fetchValidationRules) {
		this.fetchValidationRules = fetchValidationRules;
	}

	public Boolean getFetchControllableFields() {
		return fetchControllableFields;
	}

	public void setFetchControllableFields(Boolean fetchControllableFields) {
		this.fetchControllableFields = fetchControllableFields;
	}
	
	private Boolean excludeForecastReadings;
	public Boolean getExcludeForecastReadings() {
		return excludeForecastReadings;
	}
	public void setExcludeForecastReadings(Boolean excludeForecastReadings) {
		this.excludeForecastReadings = excludeForecastReadings;
	}

	private Boolean status;
	public Boolean getStatus() {
		if (status == null) {
			return false;
		}
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String changeKPIStatus() throws Exception {
		FormulaFieldContext context = new FormulaFieldContext();
		context.setActive(status);
		context.setId(ruleId);
		FormulaFieldAPI.updateKPIReadingsStatus(context);
		setResult("result", "success");
		return SUCCESS;
	}

	private long parentUtilityTypeId = -1;
	public long getParentUtilityTypeId() {
		return parentUtilityTypeId;
	}
	public void setParentUtilityTypeId(long parentUtilityTypeId) {
		this.parentUtilityTypeId = parentUtilityTypeId;
	}

	public String v2getUtilityTypeReadings() throws Exception {
		setParentUtilityTypeId(id);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		context.put(FacilioConstants.ContextNames.FETCH_CONTROLLABLE_FIELDS, fetchControllableFields);
		context.put(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID, getParentUtilityTypeId());
		if (StringUtils.isNotEmpty(getReadingType())) {
			context.put(FacilioConstants.ContextNames.FILTER, getReadingType());
		}

		FacilioChain getCategoryReadingChain = FacilioChainFactory.getUtilityTypeReadingsChain();
		getCategoryReadingChain.execute(context);

		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if (getFetchValidationRules()) {
			fieldVsRules = (Map<Long, List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.VALIDATION_RULES);
		}
		List<FacilioField> fields = new ArrayList<>();
		if (this.readings != null) {
			fields = this.readings.stream().map(r -> r.getFields()).flatMap(r -> r.stream()).collect(Collectors.toList());
		}
		setResult("readings", fields);
		if (getFetchValidationRules()) {
			setResult("fieldVsRules", fieldVsRules);
		}
		return SUCCESS;
	}

	public String addMeterReading() throws Exception {
		String parentModule = FacilioConstants.Meter.UTILITY_TYPE;
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModule);
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		context.put(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID, getParentUtilityTypeId());
		context.put(FacilioConstants.ContextNames.VALIDATION_RULES, getFieldReadingRules());

		FacilioChain addReadingChain = TransactionChainFactory.getAddUtilityTypeReadingChain();
		addReadingChain.execute(context);

		return SUCCESS;
	}
}

