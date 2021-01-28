package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupAssetCategory;
import com.facilio.control.ControlGroupAssetContext;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ControlGroupPublishCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		
		ControlGroupTenentContext groupTenent = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(group), ControlGroupTenentContext.class);
		
		groupTenent.setTenant(tenant);
		groupTenent.setParentGroup(group);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<ControlGroupTenentContext> insert = new InsertRecordBuilder<ControlGroupTenentContext>()
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_TENENT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_TENENT_SHARING_MODULE_NAME))
				.addRecord(groupTenent);
		
		insert.save();
		
		planAssetsForTenant(groupTenent);
		
		FacilioChain chain = TransactionChainFactoryV3.getAddControlGroupAfterSaveChain();
		
		FacilioContext newContext = chain.getContext();
		
		Map<String, List<ControlGroupTenentContext>> map = Collections.singletonMap(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, Collections.singletonList(groupTenent));
		
		newContext.put(FacilioConstants.ContextNames.RECORD_MAP, map);
		
		chain.execute();
		
		return false;
	}

	private void planAssetsForTenant(ControlGroupTenentContext groupTenent) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_SPACES);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<TenantSpaceContext> select1 = new SelectRecordsBuilder<TenantSpaceContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.TENANT_SPACES)
				.beanClass(TenantSpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("tenant"), groupTenent.getTenant().getId()+"", NumberOperators.EQUALS));
		
		List<TenantSpaceContext> tenantSpaces = select1.get();
		
		List<Long> tenantSpaceList = new ArrayList<Long>();
		
		
		for(TenantSpaceContext tenantSpace :tenantSpaces) {
			tenantSpaceList.add(tenantSpace.getSpace().getId());
		}
		
		fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		
		fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<AssetContext> select = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.beanClass(AssetContext.class)
				.moduleName(FacilioConstants.ContextNames.ASSET)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space"), StringUtils.join(tenantSpaceList, ","), BuildingOperator.BUILDING_IS));
		
		List<AssetContext> assets = select.get();
		
		if(assets == null || assets.isEmpty()) {
			throw new Exception("This tenant has no assets associated");
		}
		List<Long> accesableAssetIds = new ArrayList<Long>(); 
		for(AssetContext asset :assets) {
			accesableAssetIds.add(asset.getId());
		}
		
		List<ControlGroupSection> newSectionList = new ArrayList<ControlGroupSection>();
		
		List<ControlGroupAssetContext> assetListToBeMarked = new ArrayList<ControlGroupAssetContext>();
		
		for(ControlGroupSection section : groupTenent.getSections()) {
			
			List<ControlGroupAssetCategory> newAssetCategoryList = new ArrayList<ControlGroupAssetCategory>();
			
			for(ControlGroupAssetCategory category : section.getCategories()) {
				
				List<ControlGroupAssetContext> newAssetList = new ArrayList<ControlGroupAssetContext>();
				for(ControlGroupAssetContext asset : category.getControlAssets()) {
				
					if(accesableAssetIds.contains(asset.getAsset().getId())) {
						
						assetListToBeMarked.add(asset);
						
						newAssetList.add(asset);
					}
				}
				
				if(!newAssetList.isEmpty()) {
					category.setControlAssets(newAssetList);
					newAssetCategoryList.add(category);
				}
			}
			
			if(!newAssetCategoryList.isEmpty()) {
				section.setCategories(newAssetCategoryList);
				
				newSectionList.add(section);
			}
		}
		
		if(newSectionList.isEmpty()) {
			throw new Exception("No asset matched with current group");
		}
		
		groupTenent.setSections(newSectionList);
		
		if(!assetListToBeMarked.isEmpty()) {
			
			Map<String,Object> updateMap = new HashMap<String, Object>();
			
			updateMap.put("status", ControlGroupAssetContext.Status.CONTROL_PASSED_TO_CHILD.getIntVal());
			
			for(ControlGroupAssetContext asset : assetListToBeMarked) {
				
				UpdateRecordBuilder<ControlGroupAssetContext> update = new UpdateRecordBuilder<ControlGroupAssetContext>()
						.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME))
						.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)
						.andCondition(CriteriaAPI.getIdCondition(asset.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)));
						
				update.updateViaMap(updateMap);
				
			}
		}
		
	}
	
	

}
