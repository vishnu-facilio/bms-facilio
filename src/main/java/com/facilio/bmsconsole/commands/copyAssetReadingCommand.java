package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class copyAssetReadingCommand extends FacilioCommand {
	private static final int LIMIT_GET_READINGS = 30000;// rows

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long sourceOrgId = (long) context.get(FacilioConstants.ContextNames.COPY_SOURCE_ORG_ID);
		long targetOrgId = (long) context.get(FacilioConstants.ContextNames.COPY_TARGET_ORG_ID);
		long sourceOrgStartTime = (long) context.get(FacilioConstants.ContextNames.COPY_START_TIME);
		long sourceOrgEndTime = (long) context.get(FacilioConstants.ContextNames.COPY_END_TIME);
		long timeDiff = (long) context.get(FacilioConstants.ContextNames.COPY_TIME_DIFF);
		timeDiff = TimeUnit.HOURS.toMillis(timeDiff);
		List<String> moduleList = (List<String>) context.get(FacilioConstants.ContextNames.COPY_MODULE_LIST);
		List<Map<String, Object>> assetList = (List<Map<String, Object>>) context
				.get(FacilioConstants.ContextNames.COPY_ASSET_LIST);
		int offsetValue = 1;
		boolean isData = true;
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
		for (Map<String, Object> asset : assetList) {
			long assetIdSource = AssetsAPI.getAssetId(String.valueOf(asset.get("sourceAsset")),
					AccountUtil.getCurrentOrg().getId());
			for (String module : moduleList) {
				FacilioModule mod = bean.getModule(module);
				List<FacilioField> fields = bean.getAllFields(mod.getName());
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
				FacilioField parentField = fieldMap.get("parentId");
				FacilioField ttimeField = fieldMap.get("ttime");
				while (isData) {

					SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
							.select(fields).module(mod).beanClass(ReadingContext.class)
							.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(assetIdSource),
									PickListOperators.IS))
							.andCondition(CriteriaAPI.getCondition(ttimeField,
									sourceOrgStartTime + "," + sourceOrgEndTime, DateOperators.BETWEEN))
							.limit(LIMIT_GET_READINGS).offset(offsetValue);

					List<Map<String, Object>> prop = builder.getAsProps();
					if (CollectionUtils.isNotEmpty(prop)) {
						offsetValue = prop.size() + 1;

						AccountUtil.getTransactionalOrgBean(targetOrgId).copyReadingValue(prop, mod, targetOrgId,
								String.valueOf(asset.get("targetAsset")), timeDiff);
					} else {
						break;
					}

				}

			}
		}
		return false;
	}

	public static void insertAssetCopyValue(List<Map<String, Object>> prop, FacilioModule module, long orgId,
			String targetAssetId, long timeDiff) throws Exception {

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		FacilioModule targetModule = bean.getModule(module.getName());
		if (targetModule == null) {
			long moduleId = bean.addModule(module);
			targetModule = bean.getModule(moduleId);
		}
		long assetIdTarget = AssetsAPI.getAssetId(targetAssetId, orgId);
		if (assetIdTarget == 0) {
			throw new IllegalArgumentException("Asset " + targetAssetId + " doesn't exist in Target Org");
		}
		List<ReadingContext> readings = new ArrayList<ReadingContext>();
		for (int i = 0; i < prop.size(); i++) {
			ReadingContext context = new ReadingContext();
			for (Entry<String, Object> entry : prop.get(i).entrySet()) {
				if (entry.getKey().equals("id") || entry.getKey().equals("orgId") || entry.getKey().equals("date")
						|| entry.getKey().equals("month") || entry.getKey().equals("year")
						|| entry.getKey().equals("week") || entry.getKey().equals("day")
						|| entry.getKey().equals("hour") || entry.getKey().equals("moduleId")) {
					continue;
				}
				if (entry.getKey().equals("parentId")) {
					context.setParentId(assetIdTarget);
				} else if (entry.getKey().equals("ttime") || entry.getKey().equals("actualTtime")) {
					long diffval = (long) entry.getValue();
					context.setTtime(diffval - timeDiff);
				} else if (entry.getKey().equals("sysCreatedTime")) {
					context.setSysCreatedTime((long) entry.getValue());
				} else {
					context.setDatum(entry.getKey(), entry.getValue());
				}
			}
			readings.add(context);
		}

		FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, targetModule.getName());
		chain.getContext().put(FacilioConstants.ContextNames.READINGS, readings);
		chain.execute();

	}

}
