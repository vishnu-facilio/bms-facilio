package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;


public class GetGlimpseDetailsCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(GetGlimpseDetailsCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME, null);
        long recordId = (Long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID, -1L);
        String glimpseFieldName = (String) context.getOrDefault(FacilioConstants.ContextNames.GLIMPSE_FIELD_NAME, null);

        try {
            if (recordId > 0 && StringUtils.isNotEmpty(moduleName) && StringUtils.isNotEmpty(glimpseFieldName)) {

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                FacilioField facilioField = modBean.getField(glimpseFieldName, moduleName);

                if (facilioField instanceof LookupField) {

                    LookupField lookupField = (LookupField) facilioField;
                    Object lookupFieldData = getLookupFieldData(lookupField, module, recordId);
                    if (lookupFieldData == null) {
                        return false;
                    }

                    String lookupModuleName = lookupField.getLookupModule().getName();
                    Map<String, Object> lookupFieldProperties = FieldUtil.getAsProperties(lookupFieldData);
                    long lookupModuleRecordId = lookupFieldProperties!=null ? (long) lookupFieldProperties.get("id") : -1l;

                    FacilioContext glimpseModuleSummaryContext = null;

                    if (lookupModuleRecordId > 0) {
                        glimpseModuleSummaryContext = V3Util.getSummary(lookupModuleName, Collections.singletonList(lookupModuleRecordId));
                    }

                    if (glimpseModuleSummaryContext == null) {
                        context.put(FacilioConstants.ContextNames.GLIMPSE_RECORD, null);
                    }else{
                        Map<String, List> glimpseRecordMap = (Map<String, List>) glimpseModuleSummaryContext.get(Constants.RECORD_MAP);
                        List list = glimpseRecordMap.get(lookupModuleName);
                        JSONObject result = new JSONObject();
                        if(CollectionUtils.isNotEmpty(list)){
                            result.put(lookupModuleName, FieldUtil.getAsJSON(list.get(0)));
                            context.put(FacilioConstants.ContextNames.GLIMPSE_RECORD, result);
                        }
                    }

                }

            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
        }

        return false;
    }

    private static Object getLookupFieldData(LookupField lookupField, FacilioModule module, long id) throws Exception {

        Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(module);

        if (moduleClass == null) {
            return null;
        }

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(moduleClass)
                .select(Collections.singletonList(lookupField))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        List<ModuleBaseWithCustomFields> records = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(records)) {
            return FieldUtil.getValue(records.get(0), lookupField);
        } else {
            return null;
        }

    }
}