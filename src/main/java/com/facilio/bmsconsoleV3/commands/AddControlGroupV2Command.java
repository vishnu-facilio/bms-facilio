package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.control.ControlGroupAssetCategory;
import com.facilio.control.ControlGroupAssetContext;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupFieldContext;
import com.facilio.control.ControlGroupRoutineContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddControlGroupV2Command extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(controlGroupContext != null) {
			
//			List<FacilioField> controlGroupFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
//			
//			InsertRecordBuilder<ControlGroupContext> insert = new InsertRecordBuilder<ControlGroupContext>()
//	    			.addRecord(controlGroupContext)
//	    			.fields(controlGroupFields)
//	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)
//	    			;
//	        
//	        insert.save();
	        
	        List<ControlGroupSection> sections = controlGroupContext.getSections();
	        for(ControlGroupSection section : sections) {
	        	section.setControlGroup(controlGroupContext);
	        }
	        
	        controlGroupContext.setSections(null);
	        
	        List<FacilioField> controlGroupSectionFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupSection> insert1 = new InsertRecordBuilder<ControlGroupSection>()
	    			.addRecords(sections)
	    			.fields(controlGroupSectionFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME)
	    			;
	        
			insert1.save();
	        
			List<ControlGroupAssetCategory> categories = new ArrayList<ControlGroupAssetCategory>();
			for(ControlGroupSection section : sections) {
				for(ControlGroupAssetCategory category : section.getCategories()) {
					category.setControlGroupSection(section);
					category.setControlGroup(controlGroupContext);
					categories.add(category);
				}
				section.setCategories(null);
			}
	        
	        List<FacilioField> controlGroupCategoryFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupAssetCategory> insert11 = new InsertRecordBuilder<ControlGroupAssetCategory>()
	    			.addRecords(categories)
	    			.fields(controlGroupCategoryFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME)
	    			;
	        
			insert11.save();
	        
	        
	        List<ControlGroupAssetContext> assetGroupList = new ArrayList<ControlGroupAssetContext>();
	        
	        for(ControlGroupAssetCategory category : categories) {
	        	
	        	for(ControlGroupAssetContext asset : category.getControlAssets()) {
	        		
	        		asset.setControlGroup(controlGroupContext);
	        		asset.setControlGroupAssetCategory(category);
	        		
	        		assetGroupList.add(asset);
	        	}
	        	category.setControlAssets(null);
	        }
	        if(!assetGroupList.isEmpty()) {
		        List<FacilioField> controlGroupAssetFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME);
				
				InsertRecordBuilder<ControlGroupAssetContext> insert2 = new InsertRecordBuilder<ControlGroupAssetContext>()
		    			.addRecords(assetGroupList)
		    			.fields(controlGroupAssetFields)
		    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)
		    			;
		        
				insert2.save();
	        }
	        
	        List<ControlGroupFieldContext> fieldLists = new ArrayList<ControlGroupFieldContext>();
	        
	        for(ControlGroupAssetContext asset : assetGroupList) {
	        	
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
		return false;
	}

}
