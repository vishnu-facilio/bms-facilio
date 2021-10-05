package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WorkorderPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(WorkorderPageFactory.class.getName());

    private static void addSummaryTab(Page page) {
        Page.Tab summaryTab = page.new Tab("summary");
        page.addTab(summaryTab);

        // TODO:: give a more contextual name for the section
        Page.Section sectionOne = page.new Section();
        summaryTab.addSection(sectionOne);

        // tasks completed widget
        PageWidget tasksCompleted = new PageWidget(PageWidget.WidgetType.TASKS_COMPLETED);
        tasksCompleted.addToLayoutParams(sectionOne, 6, 6);
        sectionOne.addWidget(tasksCompleted);

    }

    public static Page getWorkorderPage(WorkOrderContext workorder) throws Exception {
        Page page = new Page();

//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule module = modBean.getModule(workorder.getModuleId());

        addSummaryTab(page);

        Page.Tab tasksTab = page.new Tab("tasks");
        page.addTab(tasksTab);

        Page.Tab relatedRecordsTab = page.new Tab("relatedRecords");
        page.addTab(relatedRecordsTab);

        Page.Tab historyTab = page.new Tab("history");
        page.addTab(historyTab);

        return page;
    }
}
