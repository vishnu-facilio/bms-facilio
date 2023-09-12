package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetConnectedAppRelatedListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONNECTEDAPPS)) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<Long> moduleIds = module.getExtendedModuleIds();
            if (CollectionUtils.isNotEmpty(moduleIds)) {
                String entityId = StringUtils.join(moduleIds, ",");
                List<ConnectedAppWidgetContext> widgets = ConnectedAppAPI.getConnectedAppWidgets(ConnectedAppWidgetContext.EntityType.RELATED_LIST, entityId);
                if (CollectionUtils.isNotEmpty(widgets)) {
                    List<Map<String, Object>> connectedAppRelListWidgets = new ArrayList<>();

                    widgets.forEach(widget -> {
                        Map<String, Object> connectedAppRelListMap = new HashMap<>();
                        connectedAppRelListMap.put("displayName", widget.getWidgetName());
                        connectedAppRelListMap.put("connectedAppWidgetId", widget.getId());
                        connectedAppRelListWidgets.add(connectedAppRelListMap);
                    });
                    context.put(FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS, connectedAppRelListWidgets);
                }
            }
        }

        return false;
    }
}
