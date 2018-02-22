package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionSupport;

public class ReadingAction extends ActionSupport {
	
	public String addReading() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, getParentModule());
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		
		Chain addReadingChain = FacilioChainFactory.getAddReadingChain();
		addReadingChain.execute(context);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	public String addSpaceReading() throws Exception {
		return addCategoryReading(FacilioConstants.ContextNames.SPACE_CATEGORY, ModuleFactory.getSpaceCategoryReadingRelModule());
	}
	
	public String addAssetReading() throws Exception {
		return addCategoryReading(FacilioConstants.ContextNames.ASSET_CATEGORY, ModuleFactory.getAssetCategoryReadingRelModule());
	}
	
	private String addCategoryReading(String parentModule, FacilioModule categoryReadingModule) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_MODULE, parentModule);
		context.put(FacilioConstants.ContextNames.READING_NAME, getReadingName());
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, getFields());
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, categoryReadingModule);
		context.put(FacilioConstants.ContextNames.PARENT_ID, getParentCategoryId());
		
		Chain addReadingChain = FacilioChainFactory.getAddCategoryReadingChain();
		addReadingChain.execute(context);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		setReadingId(module.getModuleId());
		
		return SUCCESS;
	}
	
	public String newReadingData() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		fields = modBean.getAllFields(moduleName);
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
	
	private List<FacilioField> fields;
	public List<FacilioField> getFields() {
		return fields;
	}
	public void setFields(List<FacilioField> fields) {
		this.fields = fields;
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
		context.put(FacilioConstants.ContextNames.LIMIT_VALUE, count);
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
		context.put(FacilioConstants.ContextNames.LIMIT_VALUE, count);
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

	private int count = -1;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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
		System.out.println(">>>>>>> readings : "+readings);
		System.out.println(">>>>>>> moduleMap : "+moduleMap);
		return SUCCESS;
	}
	
	Map<Long,List<FacilioModule>> moduleMap;
	public Map<Long, List<FacilioModule>> getModuleMap() {
		return moduleMap;
	}

	public void setModuleMap(Map<Long, List<FacilioModule>> moduleMap) {
		this.moduleMap = moduleMap;
	}
}
