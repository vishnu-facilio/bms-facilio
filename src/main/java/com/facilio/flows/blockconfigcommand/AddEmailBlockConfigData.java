package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.flows.context.EmailFlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddEmailBlockConfigData extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        EmailFlowTransitionContext emailFlowTransitionContext = (EmailFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        if(emailFlowTransitionContext == null){
            return false;
        }

        Map<String,Object> prop = FieldUtil.getAsProperties(emailFlowTransitionContext);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getEmailBlockConfigDataFields())
                .table(ModuleFactory.getEmailBlockConfigDataModule().getTableName());

        insertRecordBuilder.insert(prop);
        return false;
    }
}
