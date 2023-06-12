package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.enums.OfflineUpdateType;
import com.facilio.bmsconsole.util.OfflineSupportUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SendNotificationOnOfflineTaskUpdate extends FacilioCommand implements PostTransactionCommand {

    private String type;
    private Context context;
    public SendNotificationOnOfflineTaskUpdate(Enum type){
        this.type = type.name();
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context = context;

        return false;
    }

    @Override
    public boolean postExecute() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = new ArrayList<>();

        FacilioModule module = modBean.getModule(ModuleFactory.getWorkOrdersModule().getName());
        TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
        if(task !=null) {
            recordIds = Collections.singletonList(task.getParentTicketId());
        }

        OfflineSupportUtil.sendNotificationOnOfflineRecordUpdate(module,recordIds,type);

        return false;
    }
}
