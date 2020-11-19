package com.facilio.bmsconsole.commands.picklist;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HandleDefaultIdAndOrderByForPicklist extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module name for picklist");
        String orderBy = RecordAPI.getDefaultOrderByForModuleIfAny(moduleName);
        if (StringUtils.isEmpty(orderBy)) {
            orderBy = "ID DESC";
        }
        if (module.getTypeEnum() != FacilioModule.ModuleType.PICK_LIST) {
            List<Long> defaultIds = (List<Long>) context.get(FacilioConstants.PickList.DEFAULT_ID_LIST);
            if (CollectionUtils.isNotEmpty(defaultIds)) {
                orderBy = RecordAPI.getDefaultIdOrderBy(module, defaultIds, orderBy);
            }
        }
        context.put(FacilioConstants.ContextNames.SORTING_QUERY, orderBy);
        return false;
    }
}
