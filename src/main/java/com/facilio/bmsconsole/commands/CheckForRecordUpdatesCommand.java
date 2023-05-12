package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class CheckForRecordUpdatesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Map<String,Object>> checkForUpdates = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.CHECK_FOR_RECORD_UPDATES);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule module = modBean.getModule(moduleName);
        if(module.getParentModule() != null){
            module = modBean.getModule(module.getParentModule().getModuleId());
        }
        FacilioField sysModifiedTimeField = FieldFactory.getSystemField("sysModifiedTime",module);
        FacilioField idField = FieldFactory.getIdField(module);
        List<Map<String, Object>> unUpdatedRecordId = new ArrayList<>();

        if(sysModifiedTimeField == null){
            throw new IllegalArgumentException("Default modified time field doesn't exist");
        }

        List<FacilioField> fields = new ArrayList<>();
        fields.add(idField);
        fields.add(sysModifiedTimeField);

        HashMap<Long,Object> checkUpdateRecordMap = new HashMap<>();
        for(Map<String, Object> record : checkForUpdates) {
            checkUpdateRecordMap.put((Long) record.get("id"),record);
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(checkUpdateRecordMap.keySet(),','), NumberOperators.EQUALS));

        List<Map<String, Object>> records = selectBuilder.get();
        for(Map<String,Object> record : records) {
            Long id = (Long) record.get("id");
            Long sysModifiedTime = (Long) record.get("sysModifiedTime");

            HashMap<String,Object> checkUpdateRecord = (HashMap<String, Object>) checkUpdateRecordMap.get(id);
            if(checkUpdateRecord != null && (Long) checkUpdateRecord.get("modifiedTime") < sysModifiedTime){
                unUpdatedRecordId.add(checkUpdateRecord);
            }
        }

        context.put(FacilioConstants.ContextNames.RESULT,unUpdatedRecordId);

        return false;
    }
}
