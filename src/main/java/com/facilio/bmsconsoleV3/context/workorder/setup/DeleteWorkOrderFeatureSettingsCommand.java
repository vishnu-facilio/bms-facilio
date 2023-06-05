package com.facilio.bmsconsoleV3.context.workorder.setup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import java.util.Map;

@Log4j
public class DeleteWorkOrderFeatureSettingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("DeleteWorkOrderFeatureSettingsCommand");

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkOrderFeatureSettingFields(ModuleFactory.getWoFeatureSettingsModule()));
        WorkOrderFeatureSettingType settingsType = (WorkOrderFeatureSettingType)context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_TYPE);

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
        deleteRecordBuilder.table(ModuleFactory.getWoFeatureSettingsModule().getTableName());

        if(settingsType != null) {
            LOGGER.info("delete settingType " + settingsType.name());
            deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("settingType"), settingsType.getVal() + "", NumberOperators.EQUALS));
        }else {
            for (WorkOrderFeatureSettingType settingType: WorkOrderFeatureSettingType.values()){
                deleteRecordBuilder.orCondition(CriteriaAPI.getCondition(fieldMap.get("settingType"), settingType.getVal() + "", NumberOperators.EQUALS));
            }
        }
        //TODO: Before deleting log the previous & new setting for debugging purpose.
        int deleted = deleteRecordBuilder.delete();
        LOGGER.info("deleted workOrder feature settings count: " + deleted + ", by " + AccountUtil.getCurrentUser());

        return false;
    }
}
