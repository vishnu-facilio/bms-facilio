package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;

public class ImportFieldRollupCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(ImportFieldRollupCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        List<Long> addedBaseSpaceIds = new ArrayList();
        if (context.get(FacilioConstants.ContextNames.RECORD_LIST) instanceof ArrayListMultimap) {
            ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            for (String key : recordsList.keySet()) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(key);
                if (ImportAPI.isBaseSpaceExtendedModule(module) && CollectionUtils.isNotEmpty(recordsList.get(key))) {
                    addedBaseSpaceIds.addAll(recordsList.get(key));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(addedBaseSpaceIds)) {
            FacilioContext jobContext = new FacilioContext();
            FacilioField idField = FieldFactory.getIdField(ModuleFactory.getBaseSpaceModule());
            Condition condition = CriteriaAPI.getCondition(idField, addedBaseSpaceIds, NumberOperators.EQUALS);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(condition);
            Map<String, Criteria> moduleCriteriaMap = new HashMap<>();
            moduleCriteriaMap.put(FacilioConstants.ContextNames.BASE_SPACE, criteria);
            jobContext.put(FacilioConstants.ContextNames.MODULE_CRITERIA_MAP, moduleCriteriaMap);
            FacilioTimer.scheduleInstantJob("ExecuteBulkRollUpFieldJob", jobContext);
            LOGGER.info("Roll Up Job scheduled for BaseSpaceIds: " + StringUtils.join(addedBaseSpaceIds, ","));
        }
        List<String> rollupModulesList = Arrays.asList(FacilioConstants.ContextNames.TENANT_CONTACT, FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        if (context.get(FacilioConstants.ContextNames.RECORD_LIST) instanceof ArrayListMultimap && rollupModulesList.contains(importProcessContext.getModule().getName())) {
            ArrayListMultimap<String, Long> recordList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

            if (recordList != null && recordList.containsKey(FacilioConstants.ContextNames.TENANT_CONTACT)) {
                FacilioChain c = TransactionChainFactory.getUpdatePeoplePrimaryContactChain();
                c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordList);
                c.execute();
                LOGGER.info("Tenant Contact Roll Up Executed");
            }
            if (recordList != null && recordList.containsKey(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)) {
                if (importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.INSERT.getValue()) {
                    FacilioChain c = TransactionChainFactory.getImportRollupTenantSpacesChain();
                    c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordList);
                    c.execute();
                }
                else {
                    FacilioChain c = TransactionChainFactory.getImportRollupTenantSpacesWhileUpdatingChain();
                    c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordList);
                    c.execute();
                }
                LOGGER.info("Tenant Unit Roll Up Executed");
            }
        }

        return false;
    }
}
