package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GetCategoryModuleCommand implements Command {
	private static final int MAX_FIELDS_PER_TYPE_PER_MODULE = 5;
	private static final Logger LOGGER = LogManager.getLogger(CreateReadingModulesCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
		if (categoryReadingRelModule != null && categoryReadingRelModule.equals(ModuleFactory.getAssetCategoryReadingRelModule())) {
			long categoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
			if (categoryId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
				SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
						.module(assetCategoryModule)
						.beanClass(AssetCategoryContext.class)
						.select(modBean.getAllFields(assetCategoryModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(categoryId, assetCategoryModule));
				List<AssetCategoryContext> list = builder.get();
				
				FacilioModule assetModule = null;
				if (list.size() > 0) {
					AssetCategoryContext assetCategory = list.get(0);
					long assetModuleID = assetCategory.getAssetModuleID();
					
					assetModule = modBean.getModule(assetModuleID);
				}
				
				if (assetModule == null) {
					throw new Exception("Cannot find module entry");
				}
				
				context.put(FacilioConstants.ContextNames.PARENT_MODULE, assetModule.getName());
			}
		}
		return false;
	}
	
	private List<FacilioModule> splitFields(FacilioModule module, List<FacilioField> allFields) {
		if (allFields.size() <= MAX_FIELDS_PER_TYPE_PER_MODULE) {
			allFields.addAll(FieldFactory.getDefaultReadingFields(module));
			module.setFields(allFields);
			return Collections.singletonList(module);
		}
		else {
			Map<FieldType, List<FacilioField>> fieldMap = getTypeWiseFields(allFields);
			List<FacilioField> fieldList = new ArrayList<>();
			List<FacilioModule> modules = new ArrayList<>();
			while (!fieldMap.isEmpty()) {
				Iterator<List<FacilioField>> fieldsItr = fieldMap.values().iterator();
				while (fieldsItr.hasNext()) {
					List<FacilioField> fields = fieldsItr.next();
					Iterator<FacilioField> itr = fields.iterator();
					int count = 0;
					while (itr.hasNext() && count < MAX_FIELDS_PER_TYPE_PER_MODULE) {
						fieldList.add(itr.next());
						count++;
						itr.remove();
					}
					if (fields.isEmpty()) {
						fieldsItr.remove();
					}
				}
				
				if (!fieldList.isEmpty()) {
					FacilioModule clone = copyModule(module, fieldList);
					LOGGER.debug("Module : "+clone);
					LOGGER.debug("Fields : "+module.getFields());
					modules.add(clone);		// module addition done here
					fieldList = new ArrayList<>();
				}
				else {
					break;
				}
			}
			return modules;
		}
	}
	
	private FacilioModule copyModule(FacilioModule module, List<FacilioField> fields) {
		FacilioModule newModule = new FacilioModule(module);
		fields.addAll(FieldFactory.getDefaultReadingFields(newModule));
		newModule.setFields(fields);
		return newModule;
	}
	
	private Map<FieldType, List<FacilioField>> getTypeWiseFields(List<FacilioField> fields) {
		Map<FieldType, List<FacilioField>> typeWiseFields = new HashMap<>();
		for (FacilioField field : fields) {
			List<FacilioField> fieldList = typeWiseFields.get(field.getDataTypeEnum());
			if (fieldList == null) {
				fieldList = new ArrayList<>();
				typeWiseFields.put(field.getDataTypeEnum(), fieldList);
			}
			fieldList.add(field);
		}
		return typeWiseFields;
	}

}
