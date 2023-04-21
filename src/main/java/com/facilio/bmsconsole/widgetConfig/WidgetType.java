package com.facilio.bmsconsole.widgetConfig;

import com.facilio.bmsconsole.page.PageWidget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WidgetType {
    PageWidget.WidgetType value();
}

