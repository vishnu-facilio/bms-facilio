package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.AssetAction;
import com.facilio.bmsconsole.actions.AutoCommissionAction;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ControllerAssetContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.AggregateOperator.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;

public class AutoCommissionCommand implements Command {


	private static final Logger LOGGER = LogManager.getLogger(AutoCommissionCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<Map<String, Object>> markedData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA);
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		long spaceId = (long)context.get(FacilioConstants.ContextNames.SPACE_ID);
		long siteId = ResourceAPI.getSiteIDForSpaceOrAsset(spaceId);
		long assetsId = -1;
		
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
					assetsId = assets.getAssetId();
					LOGGER.info("#######Asset creation success for Controller" + assets.getAssetId());
				} catch (Exception e) {
					LOGGER.info("#######Asset creation failed for Controller" + e);
				}
			}
		}
		else {
			assetsId = AutoCommissionAction.getAssetId(controllerId);
			
		}
		createFields(markedData,category);
		context.put(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA, markedData);
		context.put(FacilioConstants.ContextNames.FIELD_ID, getModules());
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetsId);
		return false;
	}


	private void createFields(List<Map<String, Object>> markedData, AssetCategoryContext category) throws Exception {

		// TODO Auto-generated method stub
		List<Map<String, Object>> pointsdata = markedData;
		FacilioField addFields = new FacilioField();

		for (Map<String, Object> map : pointsdata) {
			List<FacilioField> fields = new ArrayList<>();

			String firstName = (String) map.get("instance");
			String secondName = (String) map.get("device");
			String fieldName = firstName + secondName;
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

			try {
			Chain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
			addReadingChain.execute(context);
			List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			setModules(modules);
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
