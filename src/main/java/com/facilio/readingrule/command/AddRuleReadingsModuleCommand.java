package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.ArrayList;

public class AddRuleReadingsModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        if (readingRule != null) {

            String ruleName = readingRule.getName();

            context.put(FacilioConstants.ContextNames.READING_NAME, readingRule.getName());
            context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, NewReadingRuleAPI.READING_RULE_FIELD_TABLE_NAME);
            context.put(FacilioConstants.ContextNames.READING_DATA_META_TYPE, ReadingDataMeta.ReadingInputType.ALARM_POINT_FIELD);

            ArrayList<FacilioField> fieldList = new ArrayList<FacilioField>() {
                {
                    add(FieldFactory.getField(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_RESULT, ruleName, "BOOLEAN_CF1", null, FieldType.BOOLEAN));
                    add(FieldFactory.getField(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_ENERGY_IMPACT, ruleName + " - Energy Impact", "ENERGY_IMPACT", null, FieldType.DECIMAL));
                    add(FieldFactory.getField(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_COST_IMPACT, ruleName + " - Cost Impact", "COST_IMPACT", null, FieldType.DECIMAL));
                    add(FieldFactory.getField(NewReadingRuleAPI.RuleReadingsConstant.RULE_READING_INFO, ruleName + " - Sys Info", "SYS_INFO", null, FieldType.BIG_STRING));
                }
            };

            context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fieldList);
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
            context.put(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.READING_RULE);
        }
        return false;
    }
}
