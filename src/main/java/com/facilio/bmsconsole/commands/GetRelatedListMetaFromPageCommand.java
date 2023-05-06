package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;

public class GetRelatedListMetaFromPageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Page page = (Page) context.get(FacilioConstants.ContextNames.PAGE);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if(module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }

        JSONArray relatedListArr = new JSONArray();
        if (page != null) {
            for (Page.Tab pageTab : page.getTabs()) {
                if (pageTab.getName().equalsIgnoreCase("related") && CollectionUtils.isNotEmpty(pageTab.getSections())) {
                    for (Page.Section tabSection : pageTab.getSections()) {
                        if (StringUtils.isNotEmpty(tabSection.getName()) && tabSection.getName().equalsIgnoreCase("related list") && CollectionUtils.isNotEmpty(tabSection.getWidgets())) {
                            for (PageWidget widget : tabSection.getWidgets()) {
                                if (widget.getWidgetType() > 0 &&
                                    (widget.getWidgetType() == PageWidget.WidgetType.RELATED_LIST.getValue() || widget.getWidgetType() == PageWidget.WidgetType.NEW_RELATED_LIST.getValue())) {
                                    relatedListArr.add(widget.getRelatedList());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!relatedListArr.isEmpty()) {
            context.put(FacilioConstants.ContextNames.RELATED_LIST_META, relatedListArr);
        }
        return false;
    }
}
