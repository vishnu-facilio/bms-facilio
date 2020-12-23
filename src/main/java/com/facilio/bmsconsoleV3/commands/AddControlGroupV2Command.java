package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupAssetCategory;
import con.facilio.control.ControlGroupAssetContext;
import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupFieldContext;
import con.facilio.control.ControlGroupWrapper;

public class AddControlGroupV2Command extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupWrapper controlGroupWrapper = (ControlGroupWrapper) context.get(ControlScheduleUtil.CONTROL_GROUP_WRAPPER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(controlGroupWrapper != null) {
			
			List<FacilioField> controlGroupFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupContext> insert = new InsertRecordBuilder<ControlGroupContext>()
	    			.addRecord(controlGroupWrapper.getControlGroupContext())
	    			.fields(controlGroupFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)
	    			;
	        
	        insert.save();
	        
	        for(ControlGroupAssetCategory category : controlGroupWrapper.getControlGroupAssetCategories()) {
	        	category.setControlGroup(controlGroupWrapper.getControlGroupContext());
	        }
	        
	        
	        List<FacilioField> controlGroupCategoryFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupAssetCategory> insert1 = new InsertRecordBuilder<ControlGroupAssetCategory>()
	    			.addRecords(controlGroupWrapper.getControlGroupAssetCategories())
	    			.fields(controlGroupCategoryFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME)
	    			;
	        
			insert1.save();
	        
	        
	        Map<String, List<ControlGroupAssetContext>> assetVsCategoryMap = controlGroupWrapper.getControlGroupAssetMap();
	        
	        List<ControlGroupAssetContext> assetGroupList = new ArrayList<ControlGroupAssetContext>();
	        
	        for(ControlGroupAssetCategory category : controlGroupWrapper.getControlGroupAssetCategories()) {
	        	
	        	List<ControlGroupAssetContext> assetList = assetVsCategoryMap.get(category.getAssetCategory().getId()+"");
	        	if(assetList != null) {
	        		for(ControlGroupAssetContext asset : assetList) {
		        		asset.setControlGroupAssetCategory(category);
		        		asset.setControlGroup(controlGroupWrapper.getControlGroupContext());
		        		assetGroupList.add(asset);
		        	}
	        	}
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
	        
	        Map<String, List<ControlGroupFieldContext>> fieldMap = controlGroupWrapper.getControlGroupFieldMap();
	        
	        List<ControlGroupFieldContext> fieldLists = new ArrayList<ControlGroupFieldContext>();
	        
	        for(ControlGroupAssetContext asset : assetGroupList) {
	        	
	        	List<ControlGroupFieldContext> fieldList = fieldMap.get(asset.getAsset().getId()+"");
	        	if(fieldList != null) {
	        		for(ControlGroupFieldContext field : fieldList) {
		        		field.setControlGroupAsset(asset);
		        		field.setControlGroup(controlGroupWrapper.getControlGroupContext());
		        		fieldLists.add(field);
		        	}
	        	}
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
