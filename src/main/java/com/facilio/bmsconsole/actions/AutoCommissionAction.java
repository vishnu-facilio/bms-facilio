/**
 * 
 */
package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

/**
 * @author facilio
 *
 */
public class AutoCommissionAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AutoCommissionAction.class.getName());
	private static final int MAX_FIELDS=51;
	
	@SuppressWarnings("unused")
	public String autoCommissionData() throws Exception {
		List<Map<String, Object>> pointsData = new ArrayList<>();
		FacilioContext context = new FacilioContext();
		JSONObject jsonObj = null;
		JSONArray jsonArr = new JSONArray(markedData);
		for (int i = 0; i < jsonArr.length(); i++) {
			jsonObj = jsonArr.getJSONObject(i);

			System.out.println(jsonObj);
			Map<String, Object> dataValue = toMap(jsonObj);
			pointsData.add(dataValue);
		}
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
		context.put(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA, pointsData);
		context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
		if (!pointsData.isEmpty() && pointsData.size() < MAX_FIELDS) {

			if (markedData != null && controllerId != -1) {
				Chain mappingChain = TransactionChainFactory.updateAutoCommissionCommand();
				mappingChain.execute(context);
			}
		} else {
			throw new IllegalArgumentException("Number of Points Should not be more than 50 Points data.");
		}

		setResult("result", "success");
		return SUCCESS;

	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			}

			else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

	@SuppressWarnings("unused")
	public String getControllerAsset() throws Exception {
		// TODO Auto-generated method stub

		if (isExistingControllerId(controllerId)) {
			setResult("controllerId", true);
		}

		return SUCCESS;
	}

	public static boolean isExistingControllerId(long controllerId) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule assetCategoryModule = modBean.getModule("resource");

		SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
				.module(assetCategoryModule).beanClass(AssetCategoryContext.class)
				.select(modBean.getAllFields(assetCategoryModule.getName()));

		System.out.println("####Fields is .." + modBean.getAllFields(assetCategoryModule.getName()));
		List<Map<String, Object>> cist = builder.getAsProps();
		for (Map<String, Object> map : cist) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				if (key.equalsIgnoreCase("controllerId")) {
					long id = (long) entry.getValue();
					if (id == controllerId) {
						System.out.println("Id is " + id + "controlerrid id :" + controllerId);
						return true;
					}

				}
			}
		}

		return false;
	}

	public static long getAssetId(long controllerId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);

		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>()
				.moduleName(module.getName()).beanClass(ResourceContext.class)
				.select(modBean.getAllFields(module.getName())).table(module.getTableName())
				.andCustomWhere("CONTROLLER_ID = ?", controllerId);
		List<ResourceContext> assets = selectBuilder.get();
		if (assets != null && !assets.isEmpty()) {
			return assets.get(0).getId();
		}
		return -1;
	}

	public static void updatePointsData(String deviceName, String instanceName, List<FacilioModule> fieldId,
			long assetId, long controllerId) throws Exception {

		List<FacilioModule> modules = fieldId;
		AssetCategoryContext category = AssetsAPI.getCategory(FacilioConstants.ContextNames.CONTROLLER_ASSET);

		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		Map<String, Object> pointsRecord = new HashMap<String, Object>();
		pointsRecord.put("resourceId", assetId);
		pointsRecord.put("categoryId", category.getId());
		pointsRecord.put("fieldId", modules.get(0).getFields().get(0).getId());
		pointsRecord.put("mappedTime", System.currentTimeMillis());

		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().fields(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), deviceName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), instanceName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId),
						StringOperators.IS));

		try {
			int count = builder.update(pointsRecord);
			if (count != 0) {
				LOGGER.info("#######Points updated successfully in autocommission");
			}
		} catch (Exception e) {
			LOGGER.info("AutoCommission is failed to Update :" + e);
		}
	}

	private long spaceId = -1;

	public long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}

	private long siteId = -1;

	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private String markedData;

	public String getMarkedData() {
		return markedData;
	}

	public void setMarkedData(String markedData) {
		this.markedData = markedData;
	}

	private long controllerId = -1;

	public long getControllerId() {
		return controllerId;
	}

	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
}