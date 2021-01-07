package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupAssetCategory;
import con.facilio.control.ControlGroupAssetContext;
import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupFieldContext;
import con.facilio.control.ControlGroupSection;

public class UpdateControlGroupRelatedV2Command extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		
		ControlGroupContext controlGroupContextOld = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT_OLD);

		List<ControlGroupSection> newSections = updateControlGroupSection(controlGroupContext, controlGroupContextOld, modBean);
		
		List<ControlGroupAssetCategory> newCategories = updateControlGroupAssetCategory(newSections, controlGroupContextOld.getSections(), modBean);
		
		List<ControlGroupAssetContext> newAssets = updateControlGroupAsset(newCategories, getCategoryList(controlGroupContextOld), modBean);
		
		deleteAndAddFields(controlGroupContext,newAssets,modBean);
		return false;
	}
	
	private void deleteAndAddFields(ControlGroupContext controlGroupContext, List<ControlGroupAssetContext> newAssets,ModuleBean modBean) throws Exception {
		
		List<FacilioField> controlGroupFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(controlGroupFields);
		
		DeleteRecordBuilder<ControlGroupFieldContext> delete = new DeleteRecordBuilder<ControlGroupFieldContext>()
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controlGroup"), controlGroupContext.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("routine"), "", CommonOperators.IS_EMPTY))
				;
		
		delete.delete();
		
		List<ControlGroupFieldContext> fieldLists = new ArrayList<ControlGroupFieldContext>();
        
        for(ControlGroupAssetContext asset : newAssets) {
        	
        	for(ControlGroupFieldContext controlField : asset.getControlFields()) {
        		controlField.setControlGroup(controlGroupContext);
        		controlField.setControlGroupAsset(asset);
        		fieldLists.add(controlField);
        	}
        	
        	asset.setControlFields(null);
        }
        
        if(!fieldLists.isEmpty()) {
	        List<FacilioField> controlGroupFieldsFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupFieldContext> insert3 = new InsertRecordBuilder<ControlGroupFieldContext>()
	    			.addRecords(fieldLists)
	    			.fields(controlGroupFieldsFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME)
	    			;
	        
			insert3.save();
        }
	}

	private List<ControlGroupAssetContext> updateControlGroupAsset(List<ControlGroupAssetCategory> categories,List<ControlGroupAssetCategory> oldCategories,ModuleBean modBean) throws Exception {
		
		List<ControlGroupAssetContext> addAssets = new ArrayList<ControlGroupAssetContext>();
		
		List<ControlGroupAssetContext> allAssets = new ArrayList<ControlGroupAssetContext>();
		
		List<Long> deletedAssetIdList = new ArrayList<Long>();
		
		List<Long> newIdList = new ArrayList<Long>();
		
		for(ControlGroupAssetCategory category :categories) {
			
			for(ControlGroupAssetContext asset : category.getControlAssets()) {
				
				asset.setControlGroupAssetCategory(category);
				asset.setControlGroup(category.getControlGroup());
				allAssets.add(asset);
				
				if(asset.getId() < 0) {
					addAssets.add(asset);
				}
				else {
					newIdList.add(asset.getId());
					List<FacilioField> controlGroupAssetFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME);
					
					UpdateRecordBuilder<ControlGroupAssetContext> update = new UpdateRecordBuilder<ControlGroupAssetContext>()
							.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)
							.fields(controlGroupAssetFields)
							.andCondition(CriteriaAPI.getIdCondition(asset.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)));
					
					update.update(asset);
				}
			}
			
			category.setControlAssets(null);
		}
		
		for(ControlGroupAssetCategory category : oldCategories) {
			
			for(ControlGroupAssetContext asset : category.getControlAssets()) {
				if(!newIdList.contains(asset.getId())) {
					deletedAssetIdList.add(asset.getId());
				}
			}
		}
		
		if(!deletedAssetIdList.isEmpty()) {
			DeleteRecordBuilder<ControlGroupAssetContext> delete = new DeleteRecordBuilder<ControlGroupAssetContext>()
					.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)
					.andCondition(CriteriaAPI.getIdCondition(deletedAssetIdList, modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)));
			
			delete.delete();
		}
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME, addAssets);
		
		return allAssets;
	}

	private List<ControlGroupAssetCategory> updateControlGroupAssetCategory(List<ControlGroupSection> sections,List<ControlGroupSection> oldSections,ModuleBean modBean) throws Exception {
		
		List<ControlGroupAssetCategory> addCategories = new ArrayList<ControlGroupAssetCategory>();
		
		List<ControlGroupAssetCategory> allCategories = new ArrayList<ControlGroupAssetCategory>();
		
		List<Long> deletedCategoryIdList = new ArrayList<Long>();
		
		List<Long> newIdList = new ArrayList<Long>();
		
		for(ControlGroupSection section :sections) {
			
			for(ControlGroupAssetCategory category : section.getCategories()) {
				
				category.setControlGroupSection(section);
				category.setControlGroup(section.getControlGroup());
				allCategories.add(category);
				
				if(section.getId() < 0) {
					addCategories.add(category);
				}
				else {
					newIdList.add(category.getId());
					List<FacilioField> controlGroupCategoryFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME);
					
					UpdateRecordBuilder<ControlGroupAssetCategory> update = new UpdateRecordBuilder<ControlGroupAssetCategory>()
							.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME)
							.fields(controlGroupCategoryFields)
							.andCondition(CriteriaAPI.getIdCondition(category.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME)));
					
					update.update(category);
				}
			}
			
			section.setCategories(null);
		}
		
		for(ControlGroupSection section : oldSections) {
			
			for(ControlGroupAssetCategory category : section.getCategories()) {
				if(!newIdList.contains(category.getId())) {
					deletedCategoryIdList.add(category.getId());
				}
			}
		}
		
		if(!deletedCategoryIdList.isEmpty()) {
			DeleteRecordBuilder<ControlGroupAssetCategory> delete = new DeleteRecordBuilder<ControlGroupAssetCategory>()
					.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME)
					.andCondition(CriteriaAPI.getIdCondition(deletedCategoryIdList, modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME)));
			
			delete.delete();
		}
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME, addCategories);
		
		return allCategories;
	}
	
	private List<ControlGroupSection> updateControlGroupSection(ControlGroupContext controlGroupContext, ControlGroupContext controlGroupContextOld,ModuleBean modBean) throws Exception {
		
		List<ControlGroupSection> addSections = new ArrayList<ControlGroupSection>();
		
		List<Long> deletedSectionIdList = new ArrayList<Long>();
		
		List<Long> newIdList = new ArrayList<Long>();
		
		List<ControlGroupSection> newSections = controlGroupContext.getSections();
		
		controlGroupContext.setSections(null);
		for(ControlGroupSection section : newSections) {
			section.setControlGroup(controlGroupContext);
			if(section.getId() < 0) {
				addSections.add(section);
			}
			else {
				newIdList.add(section.getId());
				List<FacilioField> controlGroupSectionFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME);
				
				UpdateRecordBuilder<ControlGroupSection> update = new UpdateRecordBuilder<ControlGroupSection>()
						.moduleName(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME)
						.fields(controlGroupSectionFields)
						.andCondition(CriteriaAPI.getIdCondition(section.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME)));
				
				update.update(section);
			}
		}
		
		if(!deletedSectionIdList.isEmpty()) {
			DeleteRecordBuilder<ControlGroupSection> delete = new DeleteRecordBuilder<ControlGroupSection>()
					.moduleName(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME)
					.andCondition(CriteriaAPI.getIdCondition(deletedSectionIdList, modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME)));
			
			delete.delete();
		}
		
		ControlScheduleUtil.addRecord(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME, addSections);
		
		return newSections;
		
	}
	
	
	private List<ControlGroupAssetCategory> getCategoryList(ControlGroupContext controlGroupContextOld) {
		List<ControlGroupAssetCategory> categories = new ArrayList<ControlGroupAssetCategory>();
		
		for(ControlGroupSection section : controlGroupContextOld.getSections()) {
			for(ControlGroupAssetCategory categroy : section.getCategories()) {
				categories.add(categroy);
			}
		}
		return categories;
	}
}
