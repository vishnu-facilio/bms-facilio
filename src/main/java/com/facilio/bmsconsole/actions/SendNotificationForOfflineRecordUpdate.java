package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.OfflineSupportUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class SendNotificationForOfflineRecordUpdate extends FacilioCommand implements PostTransactionCommand {
    private static final Logger LOGGER = LogManager.getLogger(SendNotificationForOfflineRecordUpdate.class.getName());

    private String type;
    private Context context;

    public SendNotificationForOfflineRecordUpdate(Enum type){
        this.type = type.name();
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;

        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        try {
            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.OFFLINE_SUPPORT)) {
                return false;
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

            if (moduleName == null) {
                return false;
            }

            FacilioModule module = modBean.getModule(moduleName);
            List<Long> recordIds = OfflineSupportUtil.getRecordIds(context);

            OfflineSupportUtil.sendNotificationOnOfflineRecordUpdate(module, recordIds, type);
        }catch(Exception e){
            LOGGER.debug("Exception at SendNotificationForOfflineRecordUpdate: ", e);
        }

        return false;
    }
}
