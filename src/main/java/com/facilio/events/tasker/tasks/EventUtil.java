package com.facilio.events.tasker.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fw.BeanFactory;

    public class EventUtil {

        public static final String DATA_TYPE = "PUBLISH_TYPE";

        public boolean processEvents(long timestamp, JSONArray object, long orgId, List<EventRuleContext> eventRules) throws Exception {

            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            bean.processEvents(timestamp, object, eventRules);
            return true;
        }
    }

