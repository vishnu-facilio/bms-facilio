package com.facilio.wmsv2.handler;

import com.facilio.fw.BeanFactory;
import com.facilio.wmsv2.bean.LongTasksBean;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;

@TopicHandler(
        topic = Topics.Tasks.longRunnningTasks,
        group = Group.LONG_RUNNING_WORKER,
        priority = -5,
        recordTimeout = 900 // 15 mins
)
@Log4j
public class LongRunningTaskHandler extends BaseHandler {

    @Override
    public Message processOutgoingMessage(Message message) {
        Long orgId = message.getOrgId();
        try {
            if(orgId != null && orgId > 0) {
                JSONObject content = message.getContent();

                if(!content.containsKey("methodName")) {
                    LOGGER.info("LONG_TASK_LOG :: methodName not found to process the given record further on " +
                            "ORGID "+ orgId + " with topic :: "+message.getTopic());
                    return null;
                }
                LongTasksBean longTasksBean = LongRunningTaskHandler.getLongTasksBean(orgId);
                String methodName = (String) content.get("methodName");
                Method method = LongTasksBean.class.getDeclaredMethod(methodName, JSONObject.class);
                method.invoke(longTasksBean, content);
                LOGGER.info("LONG_TASK_LOG :: Received data to LongRunningTaskHandler to call " +methodName+ "() "+
                        "for ORGID "+ orgId + " with topic :: "+message.getTopic());
            }
        } catch (Exception e) {
            LOGGER.error("LONG_TASK_ERROR :: ERROR IN [LongRunningTaskHandler] for ORGID "+ orgId +
                    " with topic :: "+message.getTopic(), e);
        }
        return null;
    }

    public static LongTasksBean getLongTasksBean(long orgId) throws Exception {
        return (LongTasksBean) BeanFactory.lookup("LongTasksBean", orgId);
    }

}
