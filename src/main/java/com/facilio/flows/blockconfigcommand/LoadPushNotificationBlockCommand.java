package com.facilio.flows.blockconfigcommand;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.flowengine.context.Constants;
import com.facilio.flows.context.NotificationFlowTransitionContext;
import com.facilio.flows.context.NotificationSentToContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoadPushNotificationBlockCommand extends FacilioCommand {
    boolean listMode;
    public LoadPushNotificationBlockCommand(boolean listMode) {
        this.listMode = listMode;
    }
    public LoadPushNotificationBlockCommand() {
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NotificationFlowTransitionContext> notificationList = getNotificationFlowTransitionListContextFromContext(context);

        if(CollectionUtils.isEmpty(notificationList)){
            return false;
        }
        Map<Long,NotificationFlowTransitionContext> blockIdVsNotificationContextMap = notificationList.stream().collect(Collectors.toMap(NotificationFlowTransitionContext::getId, Function.identity()));

        Set<Long> blockIds = notificationList.stream().map(NotificationFlowTransitionContext::getId).collect(Collectors.toSet());

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getNotificationBlockConfigDataFields())
                .table(ModuleFactory.getNotificationBlockConfigDataModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(blockIds,ModuleFactory.getNotificationBlockConfigDataModule()));

        List<Map<String,Object>> props = selectRecordBuilder.get();

        if(CollectionUtils.isEmpty(props)){
            return false;
        }

        for(Map<String,Object> prop : props){
            Long blockId = (Long) prop.get("id");
            NotificationFlowTransitionContext notificationFlowTransitionContext = blockIdVsNotificationContextMap.get(blockId);
            notificationFlowTransitionContext.setSubject((String) prop.get(Constants.NotificationBlockConstants.SUBJECT));
            notificationFlowTransitionContext.setMessage((String) prop.get(Constants.NotificationBlockConstants.MESSAGE));
            notificationFlowTransitionContext.setIsSendPushNotification((Boolean) prop.get(Constants.NotificationBlockConstants.IS_SEND_PUSH_NOTIFICATION));
            notificationFlowTransitionContext.setApplication((Long)prop.get(Constants.NotificationBlockConstants.APPLICATION));
            notificationFlowTransitionContext.setRecordModuleId((Long)prop.get(Constants.NotificationBlockConstants.RECORD_MODULE_ID));
            notificationFlowTransitionContext.setRecordId(notificationFlowTransitionContext.getDatumFromConfig(Constants.RECORD_ID));
        }

        selectRecordBuilder = new GenericSelectRecordBuilder()
                 .select(FieldFactory.getNotificationSendToBlockConfigDataFields())
                 .table(ModuleFactory.getNotificationSendToBlockConfigDataModule().getTableName())
                 .andCondition(CriteriaAPI.getCondition("NOTIFICATION_BLOCK_ID","notificationBlockId", StringUtils.join(blockIds,","), NumberOperators.EQUALS));
        List<Map<String,Object>> propList = selectRecordBuilder.get();

        List<NotificationSentToContext> sentToContextList = FieldUtil.getAsBeanListFromMapList(propList, NotificationSentToContext.class);

        Map<Long,List<NotificationSentToContext>> blockIdVsSendToContextMap = sentToContextList.stream()
                .collect(Collectors.groupingBy(NotificationSentToContext::getNotificationBlockId));

        for(NotificationFlowTransitionContext notificationFlowTransitionContext : notificationList){
            notificationFlowTransitionContext.setTo(blockIdVsSendToContextMap.get(notificationFlowTransitionContext.getId()));
        }

        if(!listMode){
            Map<String, Map<Long, Object>> supplements = new HashMap<>();
            fillToUserNameInSupplements(sentToContextList,supplements);
            context.put(FacilioConstants.ContextNames.SUPPLEMENTS, supplements);
        }
        return false;
    }
    private void fillToUserNameInSupplements(List<NotificationSentToContext> sentToContextList, Map<String, Map<Long, Object>> supplements) throws Exception {

        if(CollectionUtils.isEmpty(sentToContextList)){
            return;
        }

        Set<Long> userIds = new HashSet<>();
        for(NotificationSentToContext sentToContext : sentToContextList){
            if(sentToContext.getUserId()!=-1l){
                userIds.add(sentToContext.getUserId());
            }
        }

        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        Map<Long, User> usersMap = userBean.getUsersAsMap(null, userIds);
        supplements.put("user", new HashMap<>());
        for(Map.Entry<Long, User> entry : usersMap.entrySet()){
            Long userId = entry.getKey();
            User user = entry.getValue();
            supplements.get("user").put(userId,user.getName());
        }
    }
    private List<NotificationFlowTransitionContext> getNotificationFlowTransitionListContextFromContext(Context context){
        List<NotificationFlowTransitionContext> list = new ArrayList<>();
        if(listMode){
            list = (List<NotificationFlowTransitionContext>) context.get(FacilioConstants.ContextNames.FLOW_TRANSITIONS);
        }else{
            NotificationFlowTransitionContext notificationFlowTransitionContext = (NotificationFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);
            if(notificationFlowTransitionContext!=null){
                list.add(notificationFlowTransitionContext);
            }

        }
        return list;
    }
}
