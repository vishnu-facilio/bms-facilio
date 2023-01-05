package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.RoutesContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePageFactory extends PageFactory{
    private static final Logger LOGGER = LogManager.getLogger(com.facilio.bmsconsole.page.factory.RoutePageFactory.class.getName());

    public static Page getRoutePage(RoutesContext routeContext, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 8);
//        previewWidget.addToWidgetParams("labelPosition", "top");
        tab1Sec1.addWidget(previewWidget);


        Page.Section tab1Sec2 = page.new Section();
        tab1.addSection(tab1Sec2);
        PageWidget previewWidget2 = new PageWidget(PageWidget.WidgetType.MULTIRESOURCE);
        previewWidget2.addToLayoutParams(tab1Sec1, 24, 9);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule routeModule = modBean.getModule("routes");

        JSONObject moduleData = new JSONObject();
        moduleData.put("summaryWidgetName", "multiResourceWidget");
        moduleData.put("module", routeModule);
        previewWidget2.setWidgetParams(moduleData);
        previewWidget2.setRelatedList(moduleData);
        tab1Sec2.addWidget(previewWidget2);
        
        List<String> formSubModules = new ArrayList<>();

        Page.Tab tab2 = page.new Tab("Related");
        page.addTab(tab2);
        boolean isRelationshipNeeded = addRelationshipSection(page, tab2, routeContext.getModuleId());
        Page.Section tab2Sec1 = getRelatedListSectionObj(page);
        tab2.addSection(tab2Sec1);
        addRelatedListWidgets(tab2Sec1, routeContext.getModuleId(), formSubModules, false);

        return  page;
    }
}
