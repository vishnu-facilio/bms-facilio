package com.facilio.wmsv2.handler;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.rules.Constants;
import com.facilio.wmsv2.message.Message;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;


public class InspectionGenerationHandler extends BaseHandler {

    public static final String TOPIC = "inspection-generation-process";
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(InspectionGenerationHandler.class.getName());

    @SneakyThrows
    @Override
    public void processOutgoingMessage(Message message) {
        LOGGER.info("Reached Inspection generation handler");
        try{
            if (message != null && message.getContent()!=null) {
                Map<String, Object> messageMap = message.getContent();
                List<Map<String,Object>> baseScheduleListMap = (List<Map<String,Object>>) messageMap.get(Constants.Command.BASESCHEDULES);
                List<BaseScheduleContext> baseSchedules= FieldUtil.getAsBeanListFromMapList(baseScheduleListMap,BaseScheduleContext.class);
                if(baseSchedules!=null && !baseSchedules.isEmpty()) {
                    for (BaseScheduleContext baseScheduleContext : baseSchedules) {
                        LOGGER.info("Baseschedule ID --"+baseScheduleContext.getId());
                        List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
                        List<? extends ModuleBaseWithCustomFields> childRecords = InspectionUtil.inspectionGeneration(baseScheduleContext, parentRecordProps);
                        if (childRecords != null && !childRecords.isEmpty()) {
                            LOGGER.info("Count of inspections to be generated -- " + childRecords.size());
                            baseScheduleContext.saveAsV3PreCreate(childRecords);
                        }
                    }
                }
            }
        } catch(Exception e){
            LOGGER.error("Error in Inspection Generation : "+ e);
        }
    }

}
