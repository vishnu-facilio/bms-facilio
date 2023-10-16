package com.facilio.alarms.sensor.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.facilio.accounts.util.AccountUtil.FeatureLicense.SENSOR_RULE;

public class AddSensorRuleFromReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isSensorRuleEnabled = AccountUtil.isFeatureEnabled(SENSOR_RULE);
        if (isSensorRuleEnabled) {

            List<SensorRuleContext> sensorRuleList = (List<SensorRuleContext>) context.get(FacilioConstants.ContextNames.SENSOR_RULE_LIST);
            Long moduleId = (Long) context.get(FacilioConstants.ContextNames.MODULE_ID);

            if (CollectionUtils.isNotEmpty(sensorRuleList)) {
                List<Long> fieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
                List<Long> filteredFieldIds = getModuleFieldIdsBasedOnSensorRule(sensorRuleList, fieldIds);

                AtomicInteger indexHolder = new AtomicInteger();
                for (SensorRuleContext m : sensorRuleList) {
                    addSensorRules(m, filteredFieldIds.get(indexHolder.getAndIncrement()), moduleId);
                }

            }
        }
        return false;
    }

    private List<Long> getModuleFieldIdsBasedOnSensorRule(List<SensorRuleContext> sensorRules, List<Long> fieldIds) {
        List<Long> filteredList = fieldIds.stream().limit(sensorRules.size()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(filteredList)) {
            return filteredList;
        }
        return new ArrayList<>();
    }

    private void addSensorRules(SensorRuleContext sensorRule ,Long fieldId,Long moduleId) throws Exception {
        if (CollectionUtils.isNotEmpty(sensorRule.getSensorRuleTypes())) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

            sensorRule.setSensorFieldId(fieldId);
            sensorRule.setSensorModuleId(moduleId);

            V3Util.createRecord(module, FieldUtil.getAsProperties(sensorRule));

        }
    }
}
