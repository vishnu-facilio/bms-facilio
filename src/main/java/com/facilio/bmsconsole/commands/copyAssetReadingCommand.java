package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class copyAssetReadingCommand extends FacilioCommand {
	private static final int LIMIT_GET_READINGS = 30000;// rows
	private static final Logger LOGGER = LogManager.getLogger(copyAssetReadingCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		long sourceOrgId = (long) context.get(FacilioConstants.ContextNames.COPY_SOURCE_ORG_ID);
		long targetOrgId = (long) context.get(FacilioConstants.ContextNames.COPY_TARGET_ORG_ID);
		long sourceOrgStartTime = (long) context.get(FacilioConstants.ContextNames.COPY_START_TIME);
		long sourceOrgEndTime = (long) context.get(FacilioConstants.ContextNames.COPY_END_TIME);
		long timeDiff = (long) context.get(FacilioConstants.ContextNames.COPY_TIME_DIFF);
		long targetModuleIdList = (long) context.get("TARGET_MODULE_LIST");
		long sourceModuleIdList = (long) context.get("SOURCE_MODULE_LIST");
		timeDiff = TimeUnit.HOURS.toMillis(timeDiff);
		List<Map<String, Object>> assetList = (List<Map<String, Object>>) context
				.get(FacilioConstants.ContextNames.COPY_ASSET_LIST);

		boolean isData = true;

		try {
			for (Map<String, Object> asset : assetList) {
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
				AssetContext assetIdSource = AssetsAPI.getAssetInfo(Long.valueOf((String) asset.get("sourceAsset")));
				AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(assetIdSource.getCategory().getId());
				List<FacilioModule> modules = new ArrayList<FacilioModule>() ;
				
				FacilioModule sourceMod  = null;
				if(sourceModuleIdList !=0) {
					sourceMod = bean.getModule(sourceModuleIdList);
					if(sourceMod != null) {
						modules.add(sourceMod);
					}
				}else {
					modules = bean.getSubModules(assetCategory.getAssetModuleID(), ModuleType.READING);
				}
				
				
				for (FacilioModule module : modules) {
					LOGGER.info("copy asset Readings module is " + module.getName() + "for category "
							+ assetCategory.getName() + " for asset " + assetIdSource.getName());
					int offsetValue = 0;
					while (isData) {
						ModuleBean beanOrg = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
						List<FacilioField> fields = beanOrg.getAllFields(module.getName());
						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
						FacilioField parentField = fieldMap.get("parentId");
						FacilioField ttimeField = fieldMap.get("ttime");
						SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
								.select(fields).module(module).beanClass(ReadingContext.class)
								.andCondition(CriteriaAPI.getCondition(parentField,
										String.valueOf(assetIdSource.getId()), PickListOperators.IS))
								.andCondition(CriteriaAPI.getCondition(ttimeField,
										sourceOrgStartTime + "," + sourceOrgEndTime, DateOperators.BETWEEN))
								.limit(LIMIT_GET_READINGS).orderBy("id").offset(offsetValue);

						List<Map<String, Object>> prop = builder.getAsProps();
						if (CollectionUtils.isNotEmpty(prop)) {
							long val = prop.size();
							offsetValue += (int) val;
							AccountUtil.getTransactionalOrgBean(targetOrgId).copyReadingValue(prop, module, targetOrgId,
									Long.valueOf((String) asset.get("targetAsset")), timeDiff, fields,targetModuleIdList);
						} else {
							break;
						}

					}

				}
			}
		} catch (Exception e) {
			LOGGER.info("Exception occured in copy Asset Readings ", e);
			throw e;
		}

		return false;
	}

	public static void insertAssetCopyValue(List<Map<String, Object>> prop, FacilioModule module, long orgId,
			long targetAssetId, long timeDiff, List<FacilioField> fields, long targetmodId) throws Exception {

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		try {
			AssetContext assetIdTarget = AssetsAPI.getAssetInfo(targetAssetId);
			if (assetIdTarget == null) {
				throw new IllegalArgumentException("Asset  doesn't exist in Target Org");
			}
			FacilioModule targetModule = null;
			if(targetmodId != 0) {
				targetModule = bean.getModule(targetmodId);
			}else {
				targetModule = bean.getModule(module.getName());
			}
			String targetModuleName = "";
			if (targetModule == null) {
				String createModule = module.getName();
				createModule = createModule.split("_")[0];
				targetModule = createNewModule(assetIdTarget,createModule,fields);
				targetModuleName = targetModule.getName();
			}else if(module.getName().equals("ahureading") || module.getName().equals("chillerreading") || module.getName().equals("energydata") || module.getName().equals("chillerprimarypumpreading") ||
					module.getName().equals("chillersecondarypumpreading") || module.getName().equals("fcureading") || module.getName().equals("checkratiomllogreadings")) {
				targetModuleName = module.getName();
			} /*
				 * else { if(CollectionUtils.isNotEmpty(fields)) { targetModuleName =
				 * targetModule.getName(); boolean isNextSubModule = false; int count = 0;
				 * while(true) { if(isNextSubModule) { targetModuleName =
				 * targetModuleName.split("_")[0]; targetModuleName =
				 * targetModuleName+"_"+count; } List<FacilioField> targetFields =
				 * bean.getAllFields(targetModuleName);
				 * if(CollectionUtils.isEmpty(targetFields)) { String newModule =
				 * module.getName(); newModule = newModule.split("_")[0]; targetModule =
				 * createNewModule(assetIdTarget,newModule,fields); targetModuleName =
				 * targetModule.getName(); break; } List<String> checksourceFields =
				 * getFieldsName(fields); List<String> checkTargetFields =
				 * getFieldsName(targetFields); if(AccountUtil.getCurrentOrg().getId() == 114 ||
				 * AccountUtil.getCurrentOrg().getId() == 238 ||
				 * AccountUtil.getCurrentOrg().getId() == 255) {
				 * LOGGER.info("TargetFields are : "+checkTargetFields +
				 * " checksourceFields : "+checksourceFields ); } boolean isExistingField =
				 * checkTargetFields.containsAll(checksourceFields); if(isExistingField) {
				 * break; }else { isNextSubModule =true; count++; } }
				 * 
				 * } }
				 */
			targetModuleName = targetModule.getName();
			LOGGER.info("copy Asset Insert Started target AssetId is :" + targetAssetId + " and module is  : "
					+ targetModuleName);
			List<ReadingContext> readings = new ArrayList<ReadingContext>();
			List<FacilioField> targetFields = bean.getAllFields(targetModuleName);
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
						context.setParentId(assetIdTarget.getId());
					} else if (entry.getKey().equals("ttime") || entry.getKey().equals("actualTtime")) {
						long diffval = (long) entry.getValue();
						context.setTtime(diffval - timeDiff);
					} else if (entry.getKey().equals("sysCreatedTime")) {
						context.setSysCreatedTime((long) entry.getValue());
					} else {
						for(FacilioField field : targetFields) {
							if(field.getName().equals(entry.getKey())) {
								if(entry.getValue()==null) {
									continue;
								}
								context.setDatum(entry.getKey(), entry.getValue());
							}
						}
					}
				}
				readings.add(context);
			}

			FacilioChain chain = TransactionChainFactory.onlyAddOrUpdateReadingsChain();
			chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, targetModuleName);
			chain.getContext().put(FacilioConstants.ContextNames.READINGS, readings);
			chain.execute();
			LOGGER.info("copy Asset Insert finished");
		} catch (Exception e) {
			LOGGER.info("Exception occurred in Copy Asset Update ", e);
			throw e;
		}

	}

	private static List<String> getFieldsName(List<FacilioField> fields) {

		List<String> newFields = new ArrayList<String>();
		for(FacilioField itr:fields) {
			if (itr.getName().equals("actualTtime") || itr.getName().equals("ttime")
					|| itr.getName().equals("date") || itr.getName().equals("week")
					|| itr.getName().equals("day") || itr.getName().equals("hour")
					|| itr.getName().equals("parentId") || itr.getName().equals("month")) {
				continue;
			}else {
				newFields.add(itr.getName());
			}
		}
		return newFields;
	}

	private static FacilioModule createNewModule(AssetContext assetIdTarget, String module, List<FacilioField> fields) throws Exception {
		List<FacilioField> field = new ArrayList<>();
		FacilioModule targetModule = null;
		for (FacilioField itr : fields) {
			FacilioField tempfield = new FacilioField();
			if (itr.getName().equals("actualTtime") || itr.getName().equals("ttime")
					|| itr.getName().equals("date") || itr.getName().equals("week")
					|| itr.getName().equals("day") || itr.getName().equals("hour")
					|| itr.getName().equals("parentId") || itr.getName().equals("month")) {
				continue;
			} else {
				tempfield.setColumnName(itr.getColumnName());
				tempfield.setName(itr.getName());
				tempfield.setDataType(itr.getDataTypeEnum());
				tempfield.setDisplayName(itr.getDisplayName());
			}
			field.add(tempfield);
		}

		FacilioChain addReadingChain = TransactionChainFactory.getAddCategoryReadingChain();
		addReadingChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,
				FacilioConstants.ContextNames.ASSET_CATEGORY);
		addReadingChain.getContext().put(FacilioConstants.ContextNames.READING_NAME, module);
		addReadingChain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, field);
		addReadingChain.getContext().put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Readings_4");
		addReadingChain.getContext().put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE,
				ModuleFactory.getAssetCategoryReadingRelModule());
		addReadingChain.getContext().put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID,
				assetIdTarget.getCategory().getId());
		addReadingChain.getContext().put(FacilioConstants.ContextNames.MAX_FIELDS_PER_MODULE, 10);
		addReadingChain.execute();
		 targetModule = (FacilioModule) addReadingChain.getContext().get(FacilioConstants.ContextNames.MODULE);
		if(targetModule == null) {
			List<FacilioModule> modules = (List<FacilioModule>) addReadingChain.getContext().get(FacilioConstants.ContextNames.MODULE_LIST);
			targetModule = modules.get(0);
		}
		return targetModule;
	}
}
