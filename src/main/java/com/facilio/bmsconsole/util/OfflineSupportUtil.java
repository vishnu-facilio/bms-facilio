package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.FacilioModule;
import com.facilio.ims.handler.UpdateOnOfflineRecordHandler;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;


public class OfflineSupportUtil {
    public static void sendNotificationOnOfflineRecordUpdate(FacilioModule module, List<Long> recordIds, String type) throws Exception {
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.OFFLINE_SUPPORT)) {
            return;
        }

        if(module == null || CollectionUtils.isEmpty(recordIds)) {
            return;
        }

        if(!module.getTypeEnum().equals(FacilioModule.ModuleType.BASE_ENTITY)) {
            return;
        }

        List<List<Long>> partitionedRecordIds = Lists.partition(recordIds, 10);

        if(CollectionUtils.isNotEmpty(partitionedRecordIds)) {
            for (List<Long> recordIdList : partitionedRecordIds) {
                Long recordIdForTopic = recordIdList.get(0);
                JSONObject content = new JSONObject();
                content.put("recordIds", recordIdList);
                content.put("moduleName", module.getName());
                content.put("moduleId", module.getModuleId());
                content.put("type", type);

                Messenger.getMessenger().sendMessage(new Message()
                        .setKey(UpdateOnOfflineRecordHandler.KEY+"/"+recordIdForTopic)
                        .setContent(content));
            }
        }
    }

    public static List<Long> getRecordIds(Context context){
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

        if(CollectionUtils.isEmpty(recordIds)){
            if(recordId != null){
                recordIds = Collections.singletonList(recordId);
            }
        }

        return recordIds;
    }
}
