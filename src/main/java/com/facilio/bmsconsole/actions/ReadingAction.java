package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class ReadingAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String addReading() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, getParentModule());
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		addNumberFieldsToFields();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		Chain addReadingChain = TransactionChainFactory.getAddReadingsChain();
		addReadingChain.execute(context);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	private void addNumberFieldsToFields() {
		if(numberFields != null && !numberFields.isEmpty()) {
			fields = getFields() != null ? getFields() : new ArrayList<>();
			for(NumberField numberField :numberFields) {
				fields.add(numberField);
			}
		}
		
	}

	String resourceType;
	
	
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
		addNumberFieldsToFields();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		Chain addReadingChain = TransactionChainFactory.addResourceReadingChain();
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
		addNumberFieldsToFields();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, categoryReadingModule);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getParentCategoryId());
		
		Chain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
		addReadingChain.execute(context);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	public String updateAssetCategoryReading() throws Exception {
		FacilioContext context = new FacilioContext();
		
		List<List<ReadingRuleContext>> readingRules = Arrays.asList(getField().getReadingRules());
		readingRules.stream().flatMap(List::stream).forEach((r) -> {
			r.setReadingFieldId(getField().getFieldId());
			r.getEvent().setModuleId(getModuleId());
		});
		List<List<List<ActionContext>>> actionsList = readingRules.stream().map(l -> {return l.stream().map(ReadingRuleContext::getActions).collect(Collectors.toList());}).collect(Collectors.toList());
		
		context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
		context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
		context.put(FacilioConstants.ContextNames.MODULE_FIELD, getField());
		context.put(FacilioConstants.ContextNames.DEL_READING_RULE_IDS, getDelReadingRulesIds());
		
		Chain c = FacilioChainFactory.getUpdateReadingChain();
		c.execute(context);
		
		return SUCCESS;
	}
	
	public String addSpaceReading () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		addNumberFieldsToFields();
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		
		Chain addSpaceReadingChain = TransactionChainFactory.addResourceReadingChain();
		addSpaceReadingChain.execute(context);
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	public String getSpaceSpecificReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		
		Chain getSpaceSpecifcReadingsChain = FacilioChainFactory.getSpaceReadingsChain();
		getSpaceSpecifcReadingsChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		return SUCCESS;
	}
	
	public String getAssetSpecificReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentId());
		context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, excludeEmptyFields != null ? excludeEmptyFields : true);
		
		Chain getSpaceSpecifcReadingsChain = FacilioChainFactory.getAssetReadingsChain();
		getSpaceSpecifcReadingsChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		return SUCCESS;
	}
	
	public String getSpaceSpecificLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		Chain addCurrentOccupancy = FacilioChainFactory.getGetLatestSpaceReadingValuesChain();
		addCurrentOccupancy.execute(context);
		
		return setReadingValues(context);
	}
	public String getResourcesOccupantLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		System.out.println("FIEL NAME " + fieldName);
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourcesId);
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, fieldName);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		Chain occupantReadingValue = FacilioChainFactory.getResourcesOccupantReadingValuesChain();
		occupantReadingValue.execute(context);
		occupantReadingValues = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READINGS);
		setResult("readingValues", occupantReadingValues);
		return SUCCESS;
	}
	public String getAssetSpecificLatestReadingData() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
		
		Chain latestAssetData = FacilioChainFactory.getGetLatestAssetReadingValuesChain();
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
		ReadingsAPI.updateReadingDataMeta();
		setResult(FacilioConstants.ContextNames.MESSAGE, "success");
		return SUCCESS;
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
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, getParentCategoryId());
		
		Chain getCategoryReadingChain = FacilioChainFactory.getCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		
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
	
	public NumberField numberField;
	
	
	public NumberField getNumberField() {
		return numberField;
	}

	public void setNumberField(NumberField numberField) {
		this.numberField = numberField;
		if(field == null) {
			field = numberField;
		}
	}

	private List<FacilioField> fields;
	public List<FacilioField> getFields() {
		return fields;
	}
	public void setFields(List<FacilioField> fields) {
		this.fields = fields;
	}
	
	private List<NumberField> numberFields;
	
	public List<NumberField> getNumberFields() {
		return numberFields;
	}

	public void setNumberFields(List<NumberField> numberFields) {
		this.numberFields = numberFields;
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
	
	public String addReadingData() throws Exception {
		return addReadingData(getReadingName());
	}
	
	private String addReadingData(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.READINGS, getReadingValues());
		Chain addCurrentOccupancy = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
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
		
		Chain addCurrentOccupancy = FacilioChainFactory.getGetLatestReadingValuesChain();
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
		
		Chain addCurrentOccupancy = FacilioChainFactory.getGetReadingValuesChain();
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
		Chain getSpaceCategoriesChain = FacilioChainFactory.getAllSpaceCategoriesChain();
		getSpaceCategoriesChain.execute(context);
		setSpaceCategories((List<SpaceCategoryContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
	}
	
	public String getAllReadings() throws Exception {
		List<Long> assetCategoryIds = getAssetCategoryIds();
		FacilioModule module = ModuleFactory.getAssetCategoryReadingRelModule();
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, assetCategoryIds);
		Chain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
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
		Chain getSpaceTypeReading = FacilioChainFactory.getReadingsForSpaceTypeChain();
		getSpaceTypeReading.execute(context);
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);
		spaces = (Map<Long, BaseSpaceContext>)context.get(FacilioConstants.ContextNames.SPACES);
		System.out.println(">>>>>>> getAllSpaceTypeReadings.readings : "+readings);
		System.out.println(">>>>>>> getAllSpaceTypeReadings.moduleMap : "+moduleMap);
		System.out.println(">>>>>>> getAllSpaceTypeReadings.spaces : "+spaces);
		return SUCCESS;
	}
	
	Map<Long, BaseSpaceContext> spaces;
	
	public Map<Long, BaseSpaceContext> getSpaces() {
		return spaces;
	}

	public void setSpaces(Map<Long, BaseSpaceContext> spaces) {
		this.spaces = spaces;
	}

	public String getAllSpaceReadings() throws Exception {
		
		spaceCategoriesList();
		List<Long> spaceCategoryIds = getCategoryIds();
		
		FacilioModule module = ModuleFactory.getSpaceCategoryReadingRelModule();
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, module);
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_IDS, spaceCategoryIds);
		
		Chain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
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
		
		Chain getCategoryReadingChain = FacilioChainFactory.getAllCategoryReadingsChain();
		getCategoryReadingChain.execute(context);
		
		readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		moduleMap = (Map<Long,List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_MAP);
		assetCount = (Map<String, Long>)context.get(FacilioConstants.ContextNames.COUNT);
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

	private FormulaFieldContext formula;
	public FormulaFieldContext getFormula() {
		return formula;
	}
	public void setFormula(FormulaFieldContext formula) {
		this.formula = formula;
	}

	public String addFormulaField() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formula);
		if (formula.getInterval() == -1) {
			int interval = ReadingsAPI.getDataInterval(formula.getWorkflow());
			formula.setInterval(interval);
		}
		
		Chain addEnpiChain = TransactionChainFactory.addFormulaFieldChain();
		addEnpiChain.execute(context);
		
		return SUCCESS;
	}
	
	public String editFormula() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD, formula);
		WorkflowContext workflow = formula.getWorkflow();
		if(workflow!= null && workflow.getExpressions() == null) {
			WorkflowUtil.parseStringToWorkflowObject(workflow.getWorkflowString(), workflow);
		}
		
		Chain updateEnPIChain = FacilioChainFactory.updateFormulaChain();
		updateEnPIChain.execute(context);
		
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
		
		Chain deleteEnPIChain = FacilioChainFactory.deleteFormulaChain();
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
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD_TYPE, type);
		
		Chain getAllENPIsChain = FacilioChainFactory.getAllFormulasOfTypeChain();
		getAllENPIsChain.execute(context);
		
		formulaList = (List<FormulaFieldContext>) context.get(FacilioConstants.ContextNames.FORMULA_LIST);
		
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
		formula = FormulaFieldAPI.getFormulaField(id);
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

	public String calculateHistoryForFormula() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORMULA_FIELD, id);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
		context.put(FacilioConstants.ContextNames.RESOURCE_ID, resourceId);
		context.put(FacilioConstants.ContextNames.HISTORY_ALARM, historicalAlarm);
		
		Chain historicalCalculation = TransactionChainFactory.historicalFormulaCalculationChain();
		historicalCalculation.execute(context);
		
		setResult("success", "Historical Calculation is started and will be notified when done");
		
		return SUCCESS;
	}
	
	public String runThroughRule() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, id);
		context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(startTime, endTime));
		
		Chain runThroughRuleChain = TransactionChainFactory.runThroughReadingRuleChain();
		runThroughRuleChain.execute(context);
		
		setResult("success", "Rule evaluation for the readings in the given period has been started");
		
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
		
		Chain latestAssetData = ReadOnlyChainFactory.fetchLatestReadingDataChain();
		latestAssetData.execute(context);
		
		setResult("readingValues", context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		
		return SUCCESS;
	}
 }
