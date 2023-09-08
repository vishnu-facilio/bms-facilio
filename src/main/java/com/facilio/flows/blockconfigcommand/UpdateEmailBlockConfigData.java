package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flows.context.EmailFlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class UpdateEmailBlockConfigData extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        EmailFlowTransitionContext emailFlowTransitionContext = (EmailFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        if(emailFlowTransitionContext == null){
            return false;
        }

        long id = emailFlowTransitionContext.getId();

        Map<String,Object> prop = FieldUtil.getAsProperties(emailFlowTransitionContext);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getEmailBlockConfigDataFields())
                .table(ModuleFactory.getEmailBlockConfigDataModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getEmailBlockConfigDataModule()));

        updateRecordBuilder.update(prop);

        return false;
    }
}
