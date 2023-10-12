package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GetCategoryModuleCommand extends FacilioCommand {
    private static final int MAX_FIELDS_PER_TYPE_PER_MODULE = 5;
    private static final Logger LOGGER = LogManager.getLogger(CreateReadingModulesCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("Inside GetCategoryModuleCommand");
        String parentModuleName = (String) context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.METER);
        FacilioModule categoryReadingRelModule = (FacilioModule) context.get(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE);
        long categoryId = (long) context.get(FacilioConstants.ContextNames.PARENT_CATEGORY_ID);
        if (categoryId > 0) {
            if (ModuleFactory.getAssetCategoryReadingRelModule().equals(categoryReadingRelModule)) { //spl handling for asset category
                handleAssetCategoryChain(categoryId, context);
            } else if (FacilioConstants.ContextNames.METER.equals(parentModuleName)) { //spl handling for meter category
                handleMeterCategoryChain(categoryId, context);
            }
        }
        return false;
    }

    private static void handleAssetCategoryChain(long categoryId, Context context) throws Exception {
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

    private void handleMeterCategoryChain(long categoryId, Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule assetCategoryModule = modBean.getModule("utilitytype");
        SelectRecordsBuilder<V3UtilityTypeContext> builder = new SelectRecordsBuilder<V3UtilityTypeContext>()
                .module(assetCategoryModule)
                .beanClass(V3UtilityTypeContext.class)
                .select(modBean.getAllFields(assetCategoryModule.getName()))
                .andCondition(CriteriaAPI.getIdCondition(categoryId, assetCategoryModule));
        List<V3UtilityTypeContext> list = builder.get();

        if (CollectionUtils.isEmpty(list)) {
            throw new Exception("Cannot find utility type");
        }

        V3UtilityTypeContext assetCategory = list.get(0);
        long assetModuleID = assetCategory.getMeterModuleID();
        FacilioModule assetModule = modBean.getModule(assetModuleID);

        context.put(FacilioConstants.ContextNames.PARENT_MODULE, assetModule.getName());
    }

    private List<FacilioModule> splitFields(FacilioModule module, List<FacilioField> allFields) {
        if (allFields.size() <= MAX_FIELDS_PER_TYPE_PER_MODULE) {
            allFields.addAll(FieldFactory.getDefaultReadingFields(module));
            module.setFields(allFields);
            return Collections.singletonList(module);
        } else {
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
                    LOGGER.debug("Module : " + clone);
                    LOGGER.debug("Fields : " + module.getFields());
                    modules.add(clone);        // module addition done here
                    fieldList = new ArrayList<>();
                } else {
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
