package com.facilio.elasticsearch.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.elasticsearch.context.SyncContext;
import com.facilio.elasticsearch.util.ESUtil;
import com.facilio.elasticsearch.util.SyncUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class RemoveSyncFromESCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }

        SyncContext syncContext = SyncUtil.getSyncContext(module.getModuleId());
        if (syncContext == null) {
            return false;
        }

        ESUtil.deleteDataOfModule(syncContext.getSyncModuleId());
        SyncUtil.deleteSyncContext(syncContext.getId());

        return false;
    }
}
