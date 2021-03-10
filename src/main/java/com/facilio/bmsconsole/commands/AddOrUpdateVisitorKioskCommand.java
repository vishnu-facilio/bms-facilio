package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateVisitorKioskCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		VisitorKioskContext visitorKiosk = (VisitorKioskContext) context.get(FacilioConstants.ContextNames.RECORD);

		DeviceContext device = new DeviceContext();
		device.setDeviceType(DeviceContext.DeviceType.VISITOR_KIOSK);
		device.setName(visitorKiosk.getName());
		device.setDescription(visitorKiosk.getDescription());
		// TO CHECK , why not move all kiosk types as extended module

		device.setSiteId(visitorKiosk.getSiteId());
		device.setAssociatedResource(visitorKiosk.getAssociatedResource());

		if (device.getAssociatedResource() == null) {
			throw new Exception("Space required for visitor  kiosk");
		}

		if (visitorKiosk.getId() > 0) {
			device.setId(visitorKiosk.getId());
			FacilioChain updateAssetChain = TransactionChainFactory.getUpdateAssetChain();

			FacilioContext updateAssetContext = updateAssetChain.getContext();
			updateAssetContext.put(FacilioConstants.ContextNames.DEVICE_CODE,
					context.get(FacilioConstants.ContextNames.DEVICE_CODE));
			updateAssetContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
			updateAssetContext.put(FacilioConstants.ContextNames.RECORD, device);
			updateAssetContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST,
					Collections.singletonList(device.getId()));
			updateAssetContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);

			// cannot update module state directly
			if (device.getModuleState() != null) {
				device.setModuleState(null);
			}
			// context.put(FacilioConstants.ContextNames.TRANSITION_ID, null);
			updateAssetContext.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,
					FacilioConstants.ContextNames.ASSET_ACTIVITY);
			updateAssetContext.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			updateAssetChain.execute();
			Long deviceId = device.getId();

			visitorKiosk.setId(deviceId);

			Map<String, Object> propMap = FieldUtil.getAsProperties(visitorKiosk);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getVisitorKioskModule().getTableName())
					.fields(FieldFactory.getVisitorKioskFields()).andCondition(
							CriteriaAPI.getCondition(FieldFactory.getIdField(ModuleFactory.getVisitorKioskModule()),
									Collections.singletonList(visitorKiosk.getId()), NumberOperators.EQUALS));
			updateBuilder.update(propMap);

		}

		else {

			FacilioChain addDeviceChain = TransactionChainFactory.addDeviceChain();
			FacilioContext addDeviceContext = addDeviceChain.getContext();

			addDeviceContext.put(FacilioConstants.ContextNames.DEVICE_CODE,
					context.get(FacilioConstants.ContextNames.DEVICE_CODE));
			addDeviceContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			addDeviceContext.put(FacilioConstants.ContextNames.RECORD, device);
			addDeviceContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
			addDeviceContext.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE,
					ModuleFactory.getAssetCategoryReadingRelModule());

			device.setCategory(AssetsAPI.getCategory(FacilioConstants.ModuleNames.DEVICES));
			addDeviceContext.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, device.getCategory().getId());
			addDeviceContext.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			addDeviceContext.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			addDeviceContext.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY,
					FacilioConstants.ContextNames.ASSET_ACTIVITY);
			addDeviceChain.execute();

			Long deviceId = device.getId();
			visitorKiosk.setId(deviceId);

			Map<String, Object> propMap = FieldUtil.getAsProperties(visitorKiosk);

			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getVisitorKioskModule().getTableName())
					.fields(FieldFactory.getVisitorKioskFields());

			visitorKiosk.setId(insertRecordBuilder.insert(propMap));

		}

		return false;
	}
}
