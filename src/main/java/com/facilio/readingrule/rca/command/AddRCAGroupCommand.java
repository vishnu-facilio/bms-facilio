package com.facilio.readingrule.rca.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddRCAGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);
        ReadingRuleRCAContext rca = rule.getRca();
        if (rca != null && CollectionUtils.isNotEmpty(rca.getGroups())) {
            List<RCAGroupContext> groups = rca.getGroups();
            for (RCAGroupContext group : groups) {
                ReadingRuleRcaAPI.prepareGroupForInsert(group, rca.getId());
                FacilioContext ctx = V3Util.createRecord(Constants.getModBean().getModule(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE), FieldUtil.getAsProperties(group));
                List<ModuleBaseWithCustomFields> recordList = Constants.getRecordList(ctx);
                if (CollectionUtils.isNotEmpty(recordList)) {
                    group.setId(recordList.get(0).getId());
                }
            }
        }
        return false;
    }


}
