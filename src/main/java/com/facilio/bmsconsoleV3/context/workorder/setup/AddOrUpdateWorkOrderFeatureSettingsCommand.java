package com.facilio.bmsconsoleV3.context.workorder.setup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class AddOrUpdateWorkOrderFeatureSettingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("AddOrUpdateWorkOrderFeatureSettingsCommand");

        //add the incoming setting list
        long userId = AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId(): -1L;
        long lastModifiedTime = System.currentTimeMillis();

        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettings = (List<V3WorkOrderFeatureSettingsContext>)
                context.getOrDefault(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, new ArrayList<>());

        for (V3WorkOrderFeatureSettingsContext settingsContext: workOrderFeatureSettings){
            settingsContext.setLastModifiedTime(lastModifiedTime);
            settingsContext.setSysModifiedById(userId);
            if(!settingsContext.getIsAllowed()){
                workOrderFeatureSettings.remove(settingsContext);
            }
        }

        List<Map<String, Object>> workOrderFeatureSettingsMap = FieldUtil.getAsMapList(workOrderFeatureSettings, V3WorkOrderFeatureSettingsContext.class);
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
        insertRecordBuilder
                .table(ModuleFactory.getWoFeatureSettingsModule().getTableName())
                .fields(FieldFactory.getWorkOrderFeatureSettingFields(ModuleFactory.getWoFeatureSettingsModule()))
                .addRecords(workOrderFeatureSettingsMap);
        insertRecordBuilder.save();

        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP, workOrderFeatureSettingsMap);
        return false;
    }
}
