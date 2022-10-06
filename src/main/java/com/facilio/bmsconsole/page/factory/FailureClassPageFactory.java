package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;


public class FailureClassPageFactory extends PageFactory{
    public static Page getFailureClassPage(V3FailureClassContext record) throws Exception {
        Page page = new Page();


        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();

        tab1.addSection(tab1Sec1);


        PageWidget detailsWidgetFailureClass = new PageWidget(PageWidget.WidgetType.FAILURE_CLASS_DETAILS);
        detailsWidgetFailureClass.addToLayoutParams(tab1Sec1, 24, 5);
        detailsWidgetFailureClass.addToWidgetParams("card", "failureclassdetails");
        tab1Sec1.addWidget(detailsWidgetFailureClass);

        PageWidget detailsWidgetAssociatedProblems = new PageWidget(PageWidget.WidgetType.FAILURE_CLASS_ASSOCIATED_PROBELMS);
        detailsWidgetAssociatedProblems.addToLayoutParams(tab1Sec1, 24, 10);
        detailsWidgetAssociatedProblems.addToWidgetParams("card", "failureclassassociatedproblems");
        tab1Sec1.addWidget(detailsWidgetAssociatedProblems);


        if (record == null) {
            return page;
        }

        Page.Tab tab2 = page.new Tab("Notes and Information");
        page.addTab(tab2);
        Page page1 = new Page();
        Page.Section tab2Sec1 = page1.new Section();
        tab2.addSection(tab2Sec1);
        addSecondaryDetailsWidget(tab2Sec1);
        addNotesAttachmentsModule(tab2Sec1);

        return page;
    }

    private static void addSecondaryDetailsWidget(Page.Section section) {
        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(section, 24, 7);
        section.addWidget(detailsWidget);
    }

    private static PageWidget addNotesAttachmentsModule(Page.Section section) {

        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(section, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        section.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget();
        notesWidget.setWidgetType(PageWidget.WidgetType.COMMENT);
        subModuleGroup.addToWidget(notesWidget);

        PageWidget attachmentWidget = new PageWidget();
        attachmentWidget.setWidgetType(PageWidget.WidgetType.ATTACHMENT);
        subModuleGroup.addToWidget(attachmentWidget);

        return subModuleGroup;
    }
}
