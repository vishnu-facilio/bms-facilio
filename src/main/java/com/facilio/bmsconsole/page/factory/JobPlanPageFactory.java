package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class JobPlanPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(com.facilio.bmsconsole.page.factory.JobPlanPageFactory.class.getName());

    public static Page getJobPlanPage(JobPlanContext jobPlanContext, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 4);
        tab1Sec1.addWidget(previewWidget);


        Page.Section tab1Sec2 = page.new Section();
        tab1Sec2.setName("Sections");
        tab1.addSection(tab1Sec2);
        PageWidget taskWidget = new PageWidget(PageWidget.WidgetType.JOBPLAN_TASKS_WIDGET);
        taskWidget.addToLayoutParams(tab1Sec2, 24, 10);
        tab1Sec2.addWidget(taskWidget);

        Page.Tab tab2 = page.new Tab("Notes & Information");
        page.addTab(tab2);
        Page.Section tab2Sec1 = page.new Section();
        tab2.addSection(tab2Sec1);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(tab2Sec1, 24, 8);
        notesWidget.setTitle("Notes");
        tab2Sec1.addWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentWidget.addToLayoutParams(tab2Sec1, 24, 6);
        attachmentWidget.setTitle("Attachments");
        tab2Sec1.addWidget(attachmentWidget);

        Page.Tab tab3 = page.new Tab("History");
        page.addTab(tab3);
        Page.Section tab4Sec1 = page.new Section();
        tab3.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.JOB_PLAN_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);

        return  page;
    }
}

