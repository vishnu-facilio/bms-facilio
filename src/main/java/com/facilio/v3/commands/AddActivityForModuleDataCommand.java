package com.facilio.v3.commands;

import com.facilio.activity.ActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

@Getter @Setter
@AllArgsConstructor
public class AddActivityForModuleDataCommand extends BaseActivityForModuleCommand {

    private ActivityType activityType;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            return false;
        }

        String activityContext = Constants.getActivityContext(context);
        if (StringUtils.isEmpty(activityContext)) {
            if (module.isCustom()) {        // only for custom module, we should add this activity type
                activityContext = FacilioConstants.ContextNames.CUSTOM_ACTIVITY;
                Constants.setActivityContext(context, activityContext);
            }
        }

        addActivitiesToContext(modBean, moduleName, activityType, context, activityContext);
        return false;
    }
}
