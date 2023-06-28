package com.facilio.ims.handler;

import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.wmsv2.bean.LongTasksBean;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;

@Log4j
public class LongRunningTaskHandler extends ImsHandler {

    public static String KEY = "__longrunning_tasks__";
    @Override
    public void processMessage(Message message) {
        Long orgId = message.getOrgId();
        try {
            if(orgId != null && orgId > 0) {
                JSONObject content = message.getContent();

                if(!content.containsKey("methodName")) {
                    LOGGER.info("LONG_TASK_LOG :: methodName not found to process the given record further on " +
                            "ORGID "+ orgId + " with key :: "+message.getKey());
                    return;
                }
                LongTasksBean longTasksBean = LongRunningTaskHandler.getLongTasksBean(orgId);
                String methodName = (String) content.get("methodName");
                Method method = LongTasksBean.class.getDeclaredMethod(methodName, JSONObject.class);
                method.invoke(longTasksBean, content);
                LOGGER.info("LONG_TASK_LOG :: Received data to LongRunningTaskHandler to call " +methodName+ "() "+
                        "for ORGID "+ orgId + " with key :: "+message.getKey());
            }
        } catch (Exception e) {
            LOGGER.error("LONG_TASK_ERROR :: ERROR IN [LongRunningTaskHandler] for ORGID "+ orgId +
                    " with key :: "+message.getKey(), e);
        }
    }

    public static LongTasksBean getLongTasksBean(long orgId) throws Exception {
        return (LongTasksBean) BeanFactory.lookup("LongTasksBean", orgId);
    }

}
