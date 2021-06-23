package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.actions.ReadingAction;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class ImportPointsFieldsEvaluationCommand extends FacilioCommand {


	private static Logger logger = Logger.getLogger(ImportPointsFieldsEvaluationCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		List<Map<String, Object>> allRowsOfPoints = (List<Map<String, Object>>) context.get("IMPORT_POINTS_DATA");
		long controllerId = (long) context.get("CONTROLLER_ID");
		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
		List<AssetCategoryContext> assetCategory = AssetsAPI.getCategoryList();
		for (Map<String, Object> itr : allRowsOfPoints) {
			String categoryVal = (String) itr.get("Asset Category");
			String fieldVal = (String) itr.get("Reading");
			String resource = (String) itr.get("Assets");

			long val = checkCategory(categoryVal.trim(), assetCategory);
			if (val != 0) {
				itr.put("Asset Category", String.valueOf(val));
			} else {
				throw new IllegalArgumentException("This Asset_Category doesn't exists : " + categoryVal);
			}

			long valasset = checkResources(val, resource.trim());
			if (valasset != 0) {
				itr.put("Assets", String.valueOf(valasset));
			} else {
				throw new IllegalArgumentException(
						"This Asset " + resource + " doesn't exists for this Asset_Category : " + categoryVal);
			}

			long valfield = checkFields(valasset, fieldVal.trim());
			if (valfield != 0) {
				itr.put("Reading", String.valueOf(valfield));
			} else {
				throw new IllegalArgumentException(
						"Field " + fieldVal + " doesn't exists for this Asset_Category : " + categoryVal);
			}

			finalList.add(itr);

		}
		// FacilioContext updatePoinsts = new FacilioContext();
		context.put("POINTS_LIST", finalList);
		context.put("CONTROLLER_ID", controllerId);

		return false;
	}

	private long checkCategory(String assetCategoryDisplayName, List<AssetCategoryContext> assetCategories) throws Exception {

		for (AssetCategoryContext assetCategory : assetCategories) {
			if (StringUtils.equals(assetCategory.getDisplayName(), assetCategoryDisplayName)) {
				return assetCategory.getId();
			}
		}
		return 0;
	}

	private long checkResources(long categoryId, String assetName) throws Exception, IllegalAccessException {

		List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(categoryId);
		for (AssetContext asset : assets) {
			logger.info(asset.getName() +" asset equals "+assetName);
			if (StringUtils.equals(asset.getName(), assetName)) {
				return asset.getId();
			}
		}
		return 0;
	}

	private long checkFields(long parentId, String fieldDisplayName) throws Exception {

		ReadingAction readings = new ReadingAction();
		readings.setParentId(parentId);
		readings.getAssetSpecificReadings();
		List<FacilioModule> fieldsList = readings.getReadings();

		for (FacilioModule fields : fieldsList) {
			for (FacilioField field : fields.getFields()) {
				if (StringUtils.equals(field.getDisplayName(), fieldDisplayName)) {
					return field.getId();
				}
			}
		}
		return 0;
	}
}
