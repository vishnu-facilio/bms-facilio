package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.actions.AssetAction;
import com.facilio.bmsconsole.actions.AutoCommissionAction;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ControllerAssetContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AutoCommissionCommand extends FacilioCommand {


	private static final Logger LOGGER = LogManager.getLogger(AutoCommissionCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		long assetsId=-1;
		List<Map<String,Object>> dataPoints = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA);
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		long spaceId = (long)context.get(FacilioConstants.ContextNames.SPACE_ID);
		long siteId = ResourceAPI.getSiteIDForSpaceOrAsset(spaceId);
		
		AssetCategoryContext category = AssetsAPI.getCategory(FacilioConstants.ContextNames.CONTROLLER_ASSET);
		ControllerContext controllerList = getControllerId(controllerId);

		if(!AutoCommissionAction.isExistingControllerId(controllerId)) {
			
			if (controllerList != null) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("new", "");
				
				ControllerAssetContext asset =  new ControllerAssetContext();
				asset.setName(controllerList.getName());
				asset.setCategory(category);
				asset.setSiteId(siteId);
				asset.setData(prop);
				asset.setControllerId(controllerId);
				asset.setSpaceId(spaceId);
				AssetAction assets = new AssetAction();
				assets.setAsset(asset);
				assets.setModuleName("asset");
				try {
					assets.addAsset();
					assetsId= assets.getAssetId();
					System.out.println("#######Asset creation success for Controller" + assets.getAssetId());
					LOGGER.info("#######Asset creation success for Controller" + assets.getAssetId());
				} catch (Exception e) {
					LOGGER.info("#######Asset creation failed for Controller" + e);
					System.out.println("#######Asset creation failed for Controller" + e);
				}
			}
		}
		else {
			assetsId = AutoCommissionAction.getAssetId(controllerId);
		}
		createFields(dataPoints,category,controllerId,assetsId);
//		context.put(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA, dataPoints);
//		context.put(FacilioConstants.ContextNames.FIELD_ID, getModules());
//		context.put(FacilioConstants.ContextNames.ASSET_ID, getAssetsId());
		return false;
	}

	
	private void createFields(List<Map<String, Object>> markedData, AssetCategoryContext category, long controllerId, long assetsId) throws Exception {

		// TODO Auto-generated method stub
		List<Map<String, Object>> pointsdata = markedData;
		FacilioField addFields = new FacilioField();

		for (Map<String, Object> map : pointsdata) {
			List<FacilioField> fields = new ArrayList<>();

			String instanceName = (String) map.get("instance");
			String deviceName = (String) map.get("device");
			String fieldName = instanceName + deviceName;
			addFields.setDisplayName(fieldName);
			addFields.setDataType(FieldType.DECIMAL);
			fields.add(addFields);


			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
			context.put(FacilioConstants.ContextNames.READING_NAME, fieldName);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE,ModuleFactory.getAssetCategoryReadingRelModule());
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,ModuleFactory.getControllerReadingsModule().getTableName());
			context.put(FacilioConstants.ContextNames.MAX_FIELDS_PER_MODULE, 10);
			try {
			FacilioChain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
			addReadingChain.execute(context);
			List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			setModules(modules);
			AutoCommissionAction.updatePointsData(deviceName,instanceName,getModules(),assetsId,controllerId);
			}
			catch(Exception e) {
				LOGGER.info("#####Fields Creation Failed in AutoCommission :"+e);
			}
			

		}
	}

	private ControllerContext getControllerId(long controllerId) throws Exception {
		// TODO Auto-generated method stub

		ControllerContext controllerListId = ControllerAPI.getController(controllerId);

		if (controllerListId != null) {
			return controllerListId;
		}

		return null;
	}

	
	private List<FacilioModule> modules;

	public List<FacilioModule> getModules() {
		return modules;
	}

	public void setModules(List<FacilioModule> modules) {
		this.modules = modules;
	}



}
