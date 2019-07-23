package com.facilio.events.tasker.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fw.BeanFactory;

    public class EventUtil {

        public static final String DATA_TYPE = "PUBLISH_TYPE";
        private Map<String, Integer> eventCountMap = new HashMap<>();
        private long lastEventTime = System.currentTimeMillis();


        public boolean processEvents(long timestamp, JSONObject object, String partitionKey, long orgId, List<EventRuleContext> eventRules) throws Exception {

            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            long currentExecutionTime = bean.processEvents(timestamp, object, eventRules, eventCountMap, lastEventTime, partitionKey);
            if(currentExecutionTime != -1) {
                lastEventTime = currentExecutionTime;
                return true;
            }
            return false;
        }
    }

