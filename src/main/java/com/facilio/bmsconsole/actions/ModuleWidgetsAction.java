package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ModuleWidgetsAction extends FacilioAction{

    private String moduleName;
    private long id;
    public String getWidgetsForModule() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getWidgetsForModuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        List<Map<String, Object>> widgets = (List<Map<String, Object>>) context.get(FacilioConstants.Widget.WIDGETS);
        setResult(FacilioConstants.Widget.WIDGETS, widgets);
        return SUCCESS;
    }

    public String getWidgetConfigs() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getWidgetConfigChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        List<WidgetConfigContext> widgetConfigs = (List<WidgetConfigContext>) context.get(FacilioConstants.Widget.WIDGET_CONFIGS);
        setResult(FacilioConstants.Widget.WIDGET_CONFIGS, FieldUtil.getAsJSONArray(widgetConfigs, WidgetConfigContext.class));
        return SUCCESS;
    }
}
