package com.facilio.alarms.sensor.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddSensorReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        SensorRuleContext sensorRule = (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        if (sensorRule != null) {

            List<FacilioField> fieldList = new ArrayList<>();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField sensorField = modBean.getField(sensorRule.getSensorFieldId());
            String sensorFieldName = sensorField.getName();

            fieldList.add(FieldFactory.getField("sensorRuleResult", sensorFieldName + "_sensor", "RESULT", null, FieldType.BOOLEAN));

            context.put(FacilioConstants.ContextNames.READING_NAME, sensorFieldName);
            context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, FacilioConstants.SensorRule.SENSOR_RULE_TABLE_NAME);

            context.put(FacilioConstants.ContextNames.PARENT_MODULE, FacilioConstants.ContextNames.ASSET_CATEGORY);
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, sensorRule.getCategoryId());
            context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());

            context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fieldList);
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
            context.put(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.SENSOR_RULE);
        }
        return false;

    }
}
