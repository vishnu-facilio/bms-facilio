package com.facilio.readingrule.command;

import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.context.RuleAlarmDetails;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j
public class AddAlarmDetailsCommand extends FacilioCommand {


    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewReadingRuleContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (NewReadingRuleContext readingRule : list) {
                RuleAlarmDetails alarmDetails = readingRule.getAlarmDetails();
                alarmDetails.setRuleId(readingRule.getId());
                AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(alarmDetails.getSeverity());
                Objects.requireNonNull(alarmSeverity, "Invalid severity value found!");
                alarmDetails.setSeverityId(alarmSeverity.getId());

                Map<String, Object> props = FieldUtil.getAsProperties(alarmDetails);

                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName())
                        .fields(FieldFactory.getRuleAlarmDetailsFields());
                long id = builder.insert(props);
                LOGGER.info("new reading rule : alarm details added ");
                alarmDetails.setId(id);
            }
        }
        return false;
    }
}


