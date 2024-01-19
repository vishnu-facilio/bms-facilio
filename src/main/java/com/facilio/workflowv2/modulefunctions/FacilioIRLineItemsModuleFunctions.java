package com.facilio.workflowv2.modulefunctions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.scriptengine.context.DBParamContext;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ScriptModule(moduleName = FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS)
public class FacilioIRLineItemsModuleFunctions extends FacilioModuleFunctionImpl {
    private static final Logger LOGGER = LogManager.getLogger(FacilioIRLineItemsModuleFunctions.class.getName());
    @Override
    public void update(Map<String,Object> globalVariable, List<Object> objects, ScriptContext scriptContext) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = (FacilioModule) objects.get(0);

        Criteria criteria = null;
        if(objects.get(1) instanceof DBParamContext) {
            criteria = ((DBParamContext) objects.get(1)).getCriteria();
        }
        else if (objects.get(1) instanceof Criteria) {
            criteria = (Criteria)objects.get(1);
        }

        if(criteria == null) {
            throw new RuntimeException("criteria cannot be null during update");
        }

        ScriptUtil.fillCriteriaField(criteria, module.getName());

        Map<String, Object> updateMap = (Map<String, Object>)objects.get(2);

        int argumentsCount = objects.size();
        JSONObject bodyParam = null;

        if(argumentsCount > 3){
            bodyParam = FieldUtil.getAsJSON(objects.get(3));
        }


        boolean isV3SupportedModule = ChainUtil.isV3Enabled(module);

        if(isV3SupportedModule && (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCRIPT_CRUD_FROM_V3) || (scriptContext.getIsV3Crud() != null && scriptContext.getIsV3Crud()))) {

            List<FacilioField> fields = Collections.singletonList(FieldFactory.getIdField(module));

            SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                    .module(module)
                    .select(fields)
                    .beanClass(ModuleBaseWithCustomFields.class)
                    .andCriteria(criteria);

            List<Map<String, Object>> props = select.getAsProps();

            List<Long> ids = new ArrayList<Long>();
            if(CollectionUtils.isNotEmpty(props)) {
                for(Map<String, Object> prop : props) {
                    ids.add((Long)prop.get("id"));
                }
            }

            CommonCommandUtil.handleLookupFormData(modBean.getAllFields(module.getName()), updateMap);

            FacilioContext context = V3Util.updateBulkRecords(module.getName(), updateMap,ids,bodyParam,null, true);
            if(CollectionUtils.isNotEmpty(props)) {
                scriptContext.incrementTotalUpdateCount(props.size());
            }
        }
        else {
            if (LookupSpecialTypeUtil.isSpecialType(module.getName())) {

                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(module.getTableName())
                        .fields(modBean.getAllFields(module.getName()))
                        .andCriteria(criteria);
                updateRecordBuilder.update(updateMap);
                scriptContext.incrementTotalUpdateCount();
            }
            else {

                List<FacilioField> fields = modBean.getAllFields(module.getName());

                UpdateRecordBuilder<ModuleBaseWithCustomFields> updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
                        .module(module)
                        .fields(fields)
                        .andCriteria(criteria);

                List<SupplementRecord> supplements = new ArrayList<>();
                CommonCommandUtil.handleFormDataAndSupplement(fields, updateMap, supplements);
                if(!supplements.isEmpty()) {
                    updateRecordBuilder.updateSupplements(supplements);
                }
                updateRecordBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
                updateRecordBuilder.updateViaMap(updateMap);
                scriptContext.incrementTotalUpdateCount();

                try {
                    Map<Long, List<UpdateChangeSet>> recordChanges = updateRecordBuilder.getChangeSet();
                    updateActivity(recordChanges, module.getName(), updateMap);
                } catch(Exception e){
                    LOGGER.info("Exception in update record activity - Facilio script");
                }
            }
        }
    }
    private void updateActivity(Map<Long, List<UpdateChangeSet>> recordChanges,String moduleName,Map<String, Object> updateMap) throws Exception {
        if(recordChanges != null && !recordChanges.isEmpty()) {
            for(Map.Entry<Long,List<UpdateChangeSet>> item : recordChanges.entrySet()) {
                FacilioChain chain = TransactionChainFactory.getConstructUpdateActivitiesChain();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.CHANGE_SET, recordChanges);
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(item.getKey()));
                context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

                if (updateMap != null && updateMap.containsKey("activityModuleName")) {
                    context.put(FacilioConstants.ContextNames.ACTIVITY_MODULE_NAME_FROM_SCRIPT, updateMap.get("activityModuleName"));
                }
                chain.execute();
            }
        }
    }
}
