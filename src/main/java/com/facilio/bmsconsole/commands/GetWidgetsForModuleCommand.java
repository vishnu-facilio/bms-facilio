package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class GetWidgetsForModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modbean.getModule(moduleName);
        if(module != null ) {
            long moduleId = module.getModuleId();;
            List<WidgetContext> widgets = WidgetAPI.getWidgetsForModule(moduleId);
            if(CollectionUtils.isNotEmpty(widgets)) {
                widgets.removeIf(f -> f.getWidgetType().getFeatureId() != -1 && !hasLicenseEnabled(f.getWidgetType().getFeatureId()));
                context.put(FacilioConstants.Widget.WIDGETS, FieldUtil.getAsJSONArray(widgets, WidgetContext.class));
            }
        }
        return false;
    }

    @SneakyThrows
    private boolean hasLicenseEnabled(int featureLicense) {
        AccountUtil.FeatureLicense license = AccountUtil.FeatureLicense.getFeatureLicense(featureLicense);
        boolean isEnabled = true;
        if (license != null) {
            isEnabled = AccountUtil.isFeatureEnabled(license);
        }
        return isEnabled;
    }
}
