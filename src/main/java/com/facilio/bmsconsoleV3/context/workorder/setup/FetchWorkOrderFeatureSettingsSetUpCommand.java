package com.facilio.bmsconsoleV3.context.workorder.setup;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class FetchWorkOrderFeatureSettingsSetUpCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("FetchWorkOrderFeatureSettingsCommand");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkOrderFeatureSettingFields(ModuleFactory.getWoFeatureSettingsModule()));
        WorkOrderFeatureSettingType settingsType = (WorkOrderFeatureSettingType) context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_TYPE);
        Long allowedStateId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_ALLOWED_STATE_ID);

        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder();
        genericSelectRecordBuilder
                .table(ModuleFactory.getWoFeatureSettingsModule().getTableName())
                .select(FieldFactory.getWorkOrderFeatureSettingFields(ModuleFactory.getWoFeatureSettingsModule()));

        if (settingsType != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("settingType"), settingsType.getVal() + "", NumberOperators.EQUALS));
        }

        if (allowedStateId != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("allowedTicketStatusId"), allowedStateId + "", NumberOperators.EQUALS));
        }

        List<Map<String, Object>> props = genericSelectRecordBuilder.get();

        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP, props);

        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                workOrderFeatureSettingList.add(FieldUtil.getAsBeanFromMap(prop, V3WorkOrderFeatureSettingsContext.class));
            }
        }
        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST, workOrderFeatureSettingList);

        return false;
    }
}
