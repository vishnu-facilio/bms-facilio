package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
@Log4j
public class UpdateCommand extends FacilioCommand {

    private FacilioModule module;

    public UpdateCommand(FacilioModule module) {
        this.module = module;
    }

    private int totalCount = 0;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<V3Context>> recordMap = (Map<String, List<V3Context>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        SaveOptions defaultOptions = SaveOptions.of((FacilioContext) context);
        Set<String> extendedModules = Constants.getExtendedModules(context); //For adding extended module which has module entry only for base module. Like Assets
        if (CollectionUtils.isEmpty(extendedModules)) {
            Map<Long, List<UpdateChangeSet>> changeSet = updateData(module.getName(), defaultOptions, null, recordMap.get(module.getName()));
            CommonCommandUtil.appendChangeSetMapToContext(context,changeSet,module.getName());
        }
        else {
            for (String extendedModule : extendedModules) {
                SaveOptions saveOptions = Constants.getExtendedSaveOption(context, extendedModule);
                Map<Long, List<UpdateChangeSet>> changeSet = updateData(extendedModule, defaultOptions, saveOptions, recordMap.get(extendedModule));
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, extendedModule); // For automation of this module
                CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, module.getName()); // For automation of parent module
            }
        }
        context.put(Constants.ROWS_UPDATED, totalCount);

        return false;
    }

    private Map<Long, List<UpdateChangeSet>> updateData (String moduleName, SaveOptions defaultOptions, SaveOptions options, List<V3Context> records) throws Exception {
        //copied from GenericUpdateModuleDataCommand
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);

        Class beanClass = (Class) getBeanClass(defaultOptions, options);
        List<FacilioField> fields = getPatchFields(defaultOptions, options);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(module)) {
            fields.addAll(FieldFactory.getCurrencyPropsFields(module));
            List<FacilioField> baseCurrencyValueFields = CurrencyUtil.getBaseCurrencyFieldsForModule(module);
            if(CollectionUtils.isNotEmpty(baseCurrencyValueFields)){
                fields.addAll(baseCurrencyValueFields);
            }
        }

        Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<>();
        for (ModuleBaseWithCustomFields record: records) {
            if(record == null || record.getId() < 0) {
                continue;
            }

            long recordId = record.getId();

            UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                    .module(module)
                    .fields(fields)
                    .andCondition(CriteriaAPI.getIdCondition(recordId, module));

            updateBuilder.withChangeSet(beanClass);

            updateBuilder.ignoreSplNullHandling();

            //updating multi select field values
            addSupplements(updateBuilder, defaultOptions);
            addSupplements(updateBuilder, options);

            totalCount += updateBuilder.update(record);

            changeSet.putAll(updateBuilder.getChangeSet());
        }
        return changeSet;
    }

    private Class getBeanClass (SaveOptions defaultOptions, SaveOptions options) {
        return options != null && options.getBeanClass() != null ? options.getBeanClass() :
                defaultOptions != null ? defaultOptions.getBeanClass() : null;
    }

    private List<FacilioField> getPatchFields (SaveOptions defaultOptions, SaveOptions options) {
        List<FacilioField> fields = null;
        fields = getPatchFields(defaultOptions, fields);
        fields = getPatchFields(options, fields);
        return fields;
    }

    private List<FacilioField> getPatchFields (SaveOptions options, List<FacilioField> fields) {
        if (options != null && CollectionUtils.isNotEmpty(options.getFields())) {
            if (fields != null) {
                fields.addAll(options.getFields());
            }
            else {
                return options.getFields();
            }
        }
        return fields;
    }

    private void addSupplements (UpdateRecordBuilder updateRecordBuilder, SaveOptions options) {
        Collection<SupplementRecord> supplementFields = options == null ? null : options.getSupplements();
        LOGGER.info("Update Command - Supplement Field - "+supplementFields);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            updateRecordBuilder.updateSupplements(supplementFields);
        }
    }
}
