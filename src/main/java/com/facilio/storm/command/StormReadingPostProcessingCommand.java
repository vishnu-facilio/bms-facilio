package com.facilio.storm.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import lombok.val;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class StormReadingPostProcessingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        MessageQueue mq = MessageQueueFactory.getMessageQueue(MessageSourceUtil.getDefaultSource());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        if (MapUtils.isNotEmpty(readingMap)) {

            for (val entry : readingMap.entrySet()) {
                String moduleName = entry.getKey();
                List<ReadingContext> readingCtxList = entry.getValue();
                for (ReadingContext readingContext : readingCtxList) {
                    Map<String, Object> readings = readingContext.getReadings();
                    for (val readingEntry : readings.entrySet()) {
                        String fieldName = readingEntry.getKey();
                        Object readingVal = readingEntry.getValue();

                        FacilioField field = modBean.getField(fieldName, moduleName);

                        JSONObject json = new JSONObject();
                        json.put("orgId", readingContext.getOrgId());
                        json.put("resourceId", readingContext.getParentId());
                        json.put("value", readingVal);
                        json.put("fieldId", field.getFieldId());
                        json.put("ttime", readingContext.getTtime());

                        mq.put("storm-q", new FacilioRecord("rule-exec", json));
                    }
                }
            }
        }

        return false;
    }
}
