package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class SpaceBookingPageFactory extends PageFactory{
   
    public static Page getSpaceBookingPage(V3SpaceBookingContext record, FacilioModule module) throws Exception {
        return getEmployeePortalBookingPage(record, module);
    }

    public static Page getEmployeePortalBookingPage(V3SpaceBookingContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        //  Overview Tab

        Page.Tab tab1 = page.new Tab("Overview");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

//        tab1.setCustomFullPage(true);

        PageWidget bookingFullSmmary = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_FULL_SUMMARY);
        bookingFullSmmary.addToLayoutParams(tab1Sec1,0,0, 16, 5);

        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_SECONDARY_DETAILS);
        detailsWidget.addToLayoutParams(tab1Sec1, 0,0,16, 5);

        PageWidget bookingCardDetails = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_CARD_DETAILS);
        bookingCardDetails.addToLayoutParams(tab1Sec1,16,0,8,18);


        tab1Sec1.addWidget(bookingFullSmmary);
        tab1Sec1.addWidget(detailsWidget);
        tab1Sec1.addWidget(bookingCardDetails);
        addNotesAttachmentsModule(tab1Sec1);


        // Activities Tab

        Page.Tab tab2 = page.new Tab("Activities");
        page.addTab(tab2);
        Page.Section tab4Sec1 = page.new Section();
        tab2.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);

        // Service Request Tab

//        Page.Tab tab3 = page.new Tab("Service Request");
//        page.addTab(tab3);

//        Page.Section tab3Sec1 = page.new Section();
//        tab1.addSection(tab3Sec1);

        // Tasks Tab

//        Page.Tab tab4 = page.new Tab("Tasks");
//        page.addTab(tab4);

//        Page.Section tab4Sec1 = page.new Section();
//        tab1.addSection(tab2Sec1);

        return page;
    }
    private static PageWidget addNotesAttachmentsModule(Page.Section section) {

        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(section, 16, 8);
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
