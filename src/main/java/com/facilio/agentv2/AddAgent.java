package com.facilio.agentv2;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

public class AddAgent extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        if(agent == null){
            throw new Exception(" Agent Can't be null");
        }
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(AgentConstants.AGENT_TABLE).fields(FieldFactory.getNewAgentDataFields());
        context.put(AgentConstants.AGENT_ID,builder.insert(FieldUtil.getAsProperties(agent)));
        /*context.put(FacilioConstants.ContextNames.TABLE_NAME,AgentConstants.AGENT_TABLE);
        context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getNewAgentDataFields());
        context.put(FacilioConstants.ContextNames.TO_INSERT_MAP, FieldUtil.getAsProperties(agent));
        bean.genericInsert(context);*/
        if((Long)context.get(AgentConstants.AGENT_ID) > 0){
            return true;
        }
        return false;
    }
}
