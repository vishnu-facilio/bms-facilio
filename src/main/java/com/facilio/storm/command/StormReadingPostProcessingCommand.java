package com.facilio.storm.command;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import lombok.extern.log4j.Log4j;
import lombok.val;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Log4j
public class StormReadingPostProcessingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        MessageQueue mq = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());
        long startTime = System.currentTimeMillis();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        try {
            if (MapUtils.isNotEmpty(readingMap)) {

                for (val entry : readingMap.entrySet()) {
                    String moduleName = entry.getKey();
                    List<ReadingContext> readingCtxList = entry.getValue();
                    for (ReadingContext readingContext : readingCtxList) {
                        Map<String, Object> readings = readingContext.getReadings();
                        for (val readingEntry : readings.entrySet()) {
                            String fieldName = readingEntry.getKey();
                            Object readingVal = readingEntry.getValue();

                            if (readingVal == null) {
                                LOGGER.info("reading value is null while pushing reading to storm queue " + fieldName + ",  module : " + moduleName + " reading : " + readingContext);
                                continue;
                            }

                            FacilioField field = modBean.getField(fieldName, moduleName);
                            if (field == null) {//TODO: find the problem and delete this check.
                                LOGGER.info("field is null. field name : " + fieldName + ",  module : " + moduleName + " reading : " + readingContext);
                                continue;
                            }

                            if (field.getModule().getTypeEnum() == FacilioModule.ModuleType.READING && field.getColumnName().equals("SYS_INFO")) {
                                //SYS_INFO for only for debugging purpose. No need to push to storm kafka queue. Intentionally omitted.
                                continue;
                            }

                            JSONObject json = new JSONObject();
                            json.put("orgId", readingContext.getOrgId());
                            json.put("resourceId", readingContext.getParentId());
                            json.put("value", readingVal);
                            json.put("fieldId", field.getFieldId());
                            json.put("ttime", readingContext.getTtime());

                            String partitionKey = "rule-exec/" + readingContext.getOrgId() + "/" + readingContext.getParentId();

                            mq.put(getTopicName(), new FacilioRecord(partitionKey, json));
                        }
                    }
                }
            }
        } finally {
            LOGGER.debug("Time taken for storm reading process. " + (System.currentTimeMillis() - startTime));
        }


        return false;
    }

    private String getTopicName() {
        return FacilioProperties.getEnvironment() + "-storm-queue";
    }
}
