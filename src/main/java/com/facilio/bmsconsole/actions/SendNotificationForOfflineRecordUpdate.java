package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.wmsv2.constants.Topics.UpdateOnOfflineRecord.updateOnOfflineRecord;

public class SendNotificationForOfflineRecordUpdate extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule module = modBean.getModule(moduleName);

        if(module == null || CollectionUtils.isEmpty(recordIds)) {
            return false;
        }

        List<List<Long>> partitionedRecordIds = Lists.partition(recordIds, 10);

        if(CollectionUtils.isNotEmpty(partitionedRecordIds)) {
            for (List<Long> recordIdList : partitionedRecordIds) {
                JSONObject content = new JSONObject();
                content.put("recordIds", recordIdList);
                content.put("moduleName", moduleName);
                content.put("moduleId", module.getModuleId());

                Message message = new Message();
                message.setTopic(updateOnOfflineRecord);
                message.setContent(content);
                WmsBroadcaster.getBroadcaster().sendMessage(message);
            }
        }
        return false;
    }
}
