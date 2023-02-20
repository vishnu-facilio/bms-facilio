package com.facilio.bmsconsole.ModuleSettingConfig.command;

import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class AddGlimpseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        GlimpseContext glimpseContext = (GlimpseContext) context.get(FacilioConstants.ContextNames.GLIMPSE_CONTEXT);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (StringUtils.isNotEmpty(moduleName)) {

            List<GlimpseContext> glimpse = GlimpseUtil.addGlimpse(moduleName, Collections.singletonList(glimpseContext));

            context.put(FacilioConstants.ContextNames.GLIMPSE, glimpse);
        }

        return false;
    }

}
