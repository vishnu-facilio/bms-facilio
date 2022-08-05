package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
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
        detailsWidgetAssociatedProblems.addToLayoutParams(tab1Sec1, 24, 11);
        detailsWidgetAssociatedProblems.addToWidgetParams("card", "failureclassassociatedproblems");
        tab1Sec1.addWidget(detailsWidgetAssociatedProblems);


        if (record == null) {
            return page;
        }

        return page;
    }
}
