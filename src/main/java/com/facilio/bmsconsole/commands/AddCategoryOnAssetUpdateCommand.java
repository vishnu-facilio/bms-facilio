package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddCategoryOnAssetUpdateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (asset.getId() > 0  && asset.getCategory() != null && asset.getCategory().getId() > 0) {
			AssetContext oldAsset = AssetsAPI.getAssetInfo(asset.getId());
			long categoryId = -1;
			if (oldAsset.getCategory() != null && oldAsset.getCategory().getId() > 0) {
				if (asset.getCategory().getId() != oldAsset.getCategory().getId()) {
					throw new IllegalArgumentException("Asset category cannot be changed");
				}
			}
			else {
				asset.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				
				Map<String,String> moduleInfo= AssetsAPI.getAssetModuleName(asset.getCategory().getId());
				String moduleName = moduleInfo.get(FacilioConstants.ContextNames.MODULE_NAME);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				
				List<FacilioField> fields = modBean.getAllFields(moduleName).stream().filter(field -> field.getModule().equals(module)).collect(Collectors.toList());
				fields.add(FieldFactory.getIdField(module));
				/*fields.add(FieldFactory.getOrgIdField(module));*/
				fields.add(FieldFactory.getModuleIdField(module));
				
				if (FieldUtil.isSiteIdFieldPresent(module)) {
					fields.add(FieldFactory.getSiteIdField(module));
				}
				
				asset.setSiteId(oldAsset.getSiteId());
				Map<String, Object> prop = getAsProps(asset, fields);
				prop.put("moduleId", module.getModuleId());
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(module.getTableName())
						.fields(fields)
						.addRecord(prop);
				insertBuilder.save();
				
				context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
			}
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, asset.getCategory().getId());
		}
		
		return false;
	}
	
	private Map<String, Object> getAsProps(AssetContext asset, List<FacilioField> fields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> moduleProps = FieldUtil.getAsProperties(asset);
		moduleProps.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		
		for(FacilioField field : fields) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				Object val = moduleProps.get(field.getName());
				if(val != null && val instanceof Map) {
					Map<String, Object> lookupProps = (Map<String, Object>) val; 
					if(lookupProps != null) {
						moduleProps.put(field.getName(), lookupProps.get("id"));
					}
				}
			}
		}
		return moduleProps;
	}

}
