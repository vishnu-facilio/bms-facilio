package com.facilio.bmsconsole.homepage.factory;

import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.bmsconsole.homepage.HomePage.Section;
import com.facilio.bmsconsole.homepage.HomePageWidget;


public class EmplpoyeePortalHome extends HomePageFactory {
    public static HomePage getDefaultPage(String appLinkName) throws Exception {
        HomePage defaultPage = new HomePage();

        defaultPage.setDisplayName("Default Home page");
        defaultPage.setLinkName("default");

        Section section1 = new Section();
        section1.setName("In a Nutshell");


        Section section2 = new Section();
        section2.setName("Actions");
        section2.setH(4);

        Section section3 = new Section();
        section3.setName("");
        section3.setH(6);

        defaultPage.addSection(section1);
        defaultPage.addSection(section2);
        defaultPage.addSection(section3);


        HomePageWidget widget = new HomePageWidget(HomePageWidget.WidgetType.NUT_SHELL_WIDGET);
        widget.addToLayoutParams(section1, 12, 2);
        section1.addWidget(widget);
        section1.setH(3);
        section1.setY(0);

        HomePageWidget widget1 = new HomePageWidget(HomePageWidget.WidgetType.GROUPED_ACTION_CARD);
        widget1.addToLayoutParams(section2, 12, 3);
        section2.addWidget(widget1);

        HomePageWidget widget3 = new HomePageWidget(HomePageWidget.WidgetType.RESERVED_SPACES);
        widget3.addToLayoutParams(section3, 6, 2);
        section3.addWidget(widget3);
        section3.setY(6);

        HomePageWidget widget4 = new HomePageWidget(HomePageWidget.WidgetType.TINY_LIST_VIEW);
        widget4.addToLayoutParams(section3, 6, 5);
        widget4.addToWidgetParams("moduleName", "servicerequest");
        section3.addWidget(widget4);

        HomePageWidget widget5 = new HomePageWidget(HomePageWidget.WidgetType.SPACE_FINDER);
        widget5.addToLayoutParams(section3, 6, 3);
        section3.addWidget(widget5);
//
//        HomePageWidget widget6 = new HomePageWidget(HomePageWidget.WidgetType.TINY_LIST_VIEW);
//        widget6.addToLayoutParams(section3, 6, 5);
//        widget6.addToWidgetParams("moduleName", "visitor");
//        section3.addWidget(widget6);
//
//        HomePageWidget widget7 = new HomePageWidget(HomePageWidget.WidgetType.TINY_LIST_VIEW);
//        widget7.addToLayoutParams(section3, 6, 5);
//        widget7.addToWidgetParams("moduleName", "deliveries");
//        section3.addWidget(widget7);



        return defaultPage;
    }
}
