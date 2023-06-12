package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.enums.OfflineUpdateType;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.util.OfflineSupportUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.wmsv2.constants.Topics.UpdateOnOfflineRecord.updateOnOfflineRecord;

public class SendNotificationForOfflineRecordUpdate extends FacilioCommand implements PostTransactionCommand {

    private String type;
    private Context context;

    public SendNotificationForOfflineRecordUpdate(Enum type){
        this.type = type.name();
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.OFFLINE_SUPPORT)) {
            return false;
        }

        this.context = context;

        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule module = modBean.getModule(moduleName);
        List<Long> recordIds = OfflineSupportUtil.getRecordIds(context);

        OfflineSupportUtil.sendNotificationOnOfflineRecordUpdate(module,recordIds,type);

        return false;
    }
}
