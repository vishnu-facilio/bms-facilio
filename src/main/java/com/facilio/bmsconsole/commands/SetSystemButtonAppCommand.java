package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.SystemButtonAppRelContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetSystemButtonAppCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SystemButtonRuleContext systemButtonRule = (SystemButtonRuleContext) context.get(FacilioConstants.ContextNames.SYSTEM_BUTTON);

        if(systemButtonRule == null){
            return false;
        }

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getSystemButtonAppRelModule().getTableName())
                    .select(FieldFactory.getSystemButtonAppRelFields())
                    .andCondition(CriteriaAPI.getCondition("SYSTEM_BUTTON_ID","systemButtonId", String.valueOf(systemButtonRule.getId()), NumberOperators.EQUALS));

        List<Map<String, Object>> systemButtonAppRelProps = builder.get();
        List<SystemButtonAppRelContext> systemButtonAppRels = systemButtonAppRelProps != null ? FieldUtil.getAsBeanListFromMapList(systemButtonAppRelProps, SystemButtonAppRelContext.class) : null;
        systemButtonRule.setSystemButtonAppRels(systemButtonAppRels);

        context.put(FacilioConstants.ContextNames.SYSTEM_BUTTON,systemButtonRule);
        return false;
    }
}
