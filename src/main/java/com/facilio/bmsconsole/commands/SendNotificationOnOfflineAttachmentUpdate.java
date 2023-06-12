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
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SendNotificationOnOfflineAttachmentUpdate extends FacilioCommand implements PostTransactionCommand {

    private String type;
    private Context context;
    public SendNotificationOnOfflineAttachmentUpdate(Enum type){
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
        String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if(moduleName == null){
            return false;
        }

        FacilioModule module = modBean.getModule(moduleName);

        List<Long> recordIds = OfflineSupportUtil.getRecordIds(context);

        String currentModuleName = getCurrentModuleName(parentModuleName,moduleName);

        if(moduleName.equals(FacilioConstants.ContextNames.TASK_ATTACHMENTS) && CollectionUtils.isNotEmpty(recordIds)){
            recordIds = getRecordIdsForTask(recordIds.get(0));
            this.type = OfflineUpdateType.TASK.getName();
        }

        if(currentModuleName != null){
            module = modBean.getModule(currentModuleName);
        }else {
            module = modBean.getParentModule(module.getModuleId());
        }

        if(CollectionUtils.isEmpty(recordIds)){
            Set<Long> parentIds = (Set<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
            recordIds = new ArrayList<>(parentIds);
        }

        OfflineSupportUtil.sendNotificationOnOfflineRecordUpdate(module,recordIds,type);

        return false;
    }

    public static List<Long> getRecordIdsForTask(Long recordId) throws Exception {
        List<Long> recordIds = new ArrayList<>();
        TaskContext task = (TaskContext) FieldUtil.getRecord(ModuleFactory.getTasksModule(),recordId);
        if(task !=null) {
            recordIds = Collections.singletonList(task.getParentTicketId());
        }
        return recordIds;
    }

    public static String getCurrentModuleName(String parentModuleName,String moduleName){
        if (moduleName != null && moduleName.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS) || moduleName.equals(FacilioConstants.ContextNames.TASK_ATTACHMENTS)) {
            return ModuleFactory.getWorkOrdersModule().getName();
        }
        else {
            return parentModuleName;
        }
    }
}
