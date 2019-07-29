package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.AutoCommissionAction;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class updateControllerDataCommand implements Command {


	private static final Logger LOGGER = LogManager.getLogger(updateControllerDataCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<Map<String, Object>> markedData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA);
		long controllerId = (long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.FIELD_ID);
		long assetId = (long) context.get(FacilioConstants.ContextNames.ASSET_ID);
		AssetCategoryContext category = AssetsAPI.getCategory(FacilioConstants.ContextNames.CONTROLLER_ASSET);

		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		for (Map<String, Object> map : markedData) {
			String instanceName = (String) map.get("instance");
			String deviceName = (String) map.get("device");
			long id = (long) map.get("id");
			Map<String, Object> pointsRecord = new HashMap<String, Object>();
			pointsRecord.put("resourceId", assetId);
			pointsRecord.put("categoryId", category.getId());
			pointsRecord.put("fieldId", modules.get(0).getFields().get(0).getId());
			pointsRecord.put("mappedTime", System.currentTimeMillis());

			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().fields(fields)
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), deviceName, StringOperators.IS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), instanceName, StringOperators.IS))
					.andCondition(CriteriaAPI.getIdCondition(id, module))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId),StringOperators.IS));

			try {
				int count = builder.update(pointsRecord);
				if (count != 0) {
					LOGGER.info("#######Points updated successfully in autocommission");
				}
			}
			catch(Exception e) {
				LOGGER.info("AutoCommission is failed to Update :"+e);
			}
		}

		return false;
	}

}
