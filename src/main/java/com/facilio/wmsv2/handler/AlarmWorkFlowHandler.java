package com.facilio.wmsv2.handler;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

@Log4j
public class AlarmWorkFlowHandler extends BaseHandler {

    public static final String TOPIC = "alarm-workflow";

    @Override
    public void processOutgoingMessage(Message message) {

        try {
            LOGGER.info("Current Thread in AlarmWorkFlowHandler---->" + Thread.currentThread());
            Long startTime = currentTimeMillis();
            Map<String, Object> messageMap = message.getContent();
            AccountUtil.setCurrentAccount(message.getOrgId());
            FacilioChain chain = TransactionChainFactory.getV2UpdateAlarmChain();
            FacilioContext context = chain.getContext();
            if (messageMap.get(FacilioConstants.ContextNames.RECORD_MAP) != null) {
                Map<String, Object> recordMap = (Map<String, Object>) messageMap.get(FacilioConstants.ContextNames.RECORD_MAP);
                constructRecordMap(recordMap, context);
            }
            if (messageMap.get(FacilioConstants.ContextNames.CHANGE_SET_MAP) != null) {
                constructChangeSet(messageMap, context);
            }
            if (messageMap.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST) != null) {
                List<String> eventList = (List) messageMap.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
                eventList.stream().forEach(m -> CommonCommandUtil.addEventType(EventType.valueOf(m), context));
            }
            LOGGER.info(String.format("Time taken to execute alarm workflow %d", currentTimeMillis() - startTime));
            chain.execute();
            if ((currentTimeMillis() - startTime) > 5000)
                LOGGER.info(String.format("Time taken to execute alarm workflow %d ms", currentTimeMillis() - startTime));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void constructChangeSet(Map<String, Object> messageMap, FacilioContext context) {
        Map<String, List> oldChangeSetMap = (Map<String, List>) messageMap.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
        Map<Long, List> newChangeSet = new HashMap<>();
        for (Map.Entry oldChangeMap : oldChangeSetMap.entrySet()) {
            List<UpdateChangeSet> updatedList = FieldUtil.getAsBeanListFromMapList((List<Map<String, Object>>) oldChangeMap.getValue(), UpdateChangeSet.class);
            LOGGER.info(FieldUtil.getAsBeanListFromMapList((List<Map<String, Object>>) oldChangeMap.getValue(), UpdateChangeSet.class));
            newChangeSet.put(Long.parseLong((String) oldChangeMap.getKey()), updatedList);
        }
        Map<String, HashMap> changeSetMap = new HashMap<>();
        changeSetMap.put((String) messageMap.get(FacilioConstants.ContextNames.MODULE_NAME), (HashMap) newChangeSet);
        context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, changeSetMap);
    }

    private void constructRecordMap(Map<String, Object> oldRecordMap, FacilioContext context) throws Exception {
        for (Map.Entry oldChangeMap : oldRecordMap.entrySet()) {
            String moduleName = (String) oldChangeMap.getKey();
            FacilioModule module = ChainUtil.getModule(moduleName);
            V3Config v3Config = ChainUtil.getV3Config(module.getName());
            oldChangeMap.setValue(FieldUtil.getAsBeanListFromMapList((List<Map<String, Object>>) oldChangeMap.getValue(), v3Config.getBeanClass()));
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP, oldRecordMap);
    }
}
