package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.EmailFlowTransitionContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoadEmailBlockCommand extends FacilioCommand {
    boolean listMode;

    public LoadEmailBlockCommand(boolean listMode) {
        this.listMode = listMode;
    }
    public LoadEmailBlockCommand(){

    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<EmailFlowTransitionContext> emailFlowTransitionList = getEmailFlowTransitionListFromContext(context);
        if(CollectionUtils.isEmpty(emailFlowTransitionList)){
            return false;
        }

        Map<Long, EmailFlowTransitionContext> blockIdVsNotificationContextMap = emailFlowTransitionList.stream().collect(Collectors.toMap(EmailFlowTransitionContext::getId, Function.identity()));

        Set<Long> blockIds = emailFlowTransitionList.stream().map(EmailFlowTransitionContext::getId).collect(Collectors.toSet());

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getEmailBlockConfigDataFields())
                .table(ModuleFactory.getEmailBlockConfigDataModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(blockIds,ModuleFactory.getEmailBlockConfigDataModule()));

        List<Map<String,Object>> props = selectRecordBuilder.get();

        if(CollectionUtils.isEmpty(props)){
            return false;
        }

        for(Map<String,Object> prop : props){
            Long blockId = (Long) prop.get("id");
            EmailFlowTransitionContext emailFlowTransitionContext = blockIdVsNotificationContextMap.get(blockId);
            emailFlowTransitionContext.setTemplateId((Long) prop.getOrDefault(Constants.EmailBlockConstants.TEMPLATE_ID,-1l));
            emailFlowTransitionContext.setFromMailId((Long) prop.get(Constants.EmailBlockConstants.FROM_MAIL_ID));
            emailFlowTransitionContext.setTo((String) prop.get(Constants.EmailBlockConstants.TO));
            emailFlowTransitionContext.setCc((String) prop.get(Constants.EmailBlockConstants.CC));
            emailFlowTransitionContext.setBcc((String) prop.get(Constants.EmailBlockConstants.BCC));
            emailFlowTransitionContext.setSendAsSeparateMail((Boolean)prop.getOrDefault(Constants.EmailBlockConstants.SEND_AS_SEPARATE_MAIL,false));
        }
        return false;
    }
    private List<EmailFlowTransitionContext> getEmailFlowTransitionListFromContext(Context context){
        List<EmailFlowTransitionContext> list = new ArrayList<>();
        if(listMode){
            list = (List<EmailFlowTransitionContext>) context.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS);
        }else{
            EmailFlowTransitionContext emailFlowTransitionContext = (EmailFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
            if(emailFlowTransitionContext!=null){
                list.add(emailFlowTransitionContext);
            }

        }
        return list;
    }
}
