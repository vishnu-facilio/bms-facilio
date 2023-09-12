package com.facilio.bmsconsole.homepage;

import com.facilio.modules.FieldUtil;
import com.facilio.bmsconsole.homepage.HomePage.Section;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePageWidget {
    private static final long serialVersionUID = 1L;

    public HomePageWidget () {}

    public HomePageWidget(WidgetType type) {
        this(type, null);
    }

    public HomePageWidget(WidgetType type, String name) {
        this.widgetType = type;
        this.name = name;
    }

    @Getter @Setter
    private long orgId = -1;
    @Getter @Setter
    private long id = -1;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private long sectionId;
    @Getter @Setter
    private Section section;
    @Getter @Setter
    private Long HomePageId;



    private WidgetType widgetType;
    public WidgetType getWidgetTypeEnum() {
        return widgetType;
    }
    public int getWidgetType() {
        return widgetType != null ?  widgetType.getValue() : -1;
    }
    public void setWidgetType(int widgetType) {
        this.widgetType = WidgetType.valueOf(widgetType);
    }
    public void setWidgetType(WidgetType widgetType) {
        this.widgetType = widgetType;
    }

    @JsonIgnore
    public Map<String, Object> getWidgetTypeObj() throws Exception {
        return FieldUtil.getAsProperties(widgetType);
    }



    private JSONObject widgetParams;
    public JSONObject getWidgetParams() {
        return widgetParams;
    }
    public void setWidgetParams(JSONObject widgetParams) {
        this.widgetParams = widgetParams;
    }
    public void setWidgetParams(String widgetParams) throws Exception {
        if(widgetParams != null) {
            JSONParser parser = new JSONParser();
            this.widgetParams = (JSONObject) parser.parse(widgetParams);
        }
    }

    public void addToWidgetParams (String key, Object value) {
        if (widgetParams == null) {
            widgetParams = new JSONObject();
        }
        widgetParams.put(key, value);
    }

    private JSONObject layoutParams;
    public JSONObject getLayoutParams() {
        return layoutParams;
    }
    public void setLayoutParams(JSONObject layoutParams) {
        this.layoutParams = layoutParams;
    }
    public void setLayoutParams(String layoutParams) throws Exception {
        if(layoutParams != null) {
            JSONParser parser = new JSONParser();
            this.layoutParams = (JSONObject) parser.parse(layoutParams);
        }
    }
    public void setXPoisition(int position) {
        addToLayoutParams("x", position);
    }
    public void setYPoisition(int position) {
        addToLayoutParams("y", position);
    }
    public void setWidth(int width) {
        addToLayoutParams("w", width);
    }
    public void setHeight(int height) {
        addToLayoutParams("h", height);
    }
    public void addToLayoutParams(Section section, int width, int height) {
        int x = section.getX();
        int y = section.getY();
        addToLayoutParams(section, x, y, width, height);
    }
    public void addToLayoutParams(Section section, int x, int y, int width, int height) {
        addToLayoutParams(x, y, width, height);
        x += width;
        if (x >= 12 || width >= 12) {
            y += height;	// Assuming the height will be same for everywidget
            x = 0;
        }
        section.setXY(x, y);
    }
    public void addToLayoutParams(int xPosition, int yPosition, int width, int height) {
        setXPoisition(xPosition);
        setYPoisition(yPosition);
        setWidth(width);
        setHeight(height);
    }

    private void addToLayoutParams (String key, Object value) {
        if (layoutParams == null) {
            layoutParams = new JSONObject();
        }
        layoutParams.put(key, value);
    }

    private List<HomePageWidget> widgets;
    public List<HomePageWidget> getWidgets() {
        return widgets;
    }
    public void setWidgets(List<HomePageWidget> widgets) {
        this.widgets = widgets;
    }
    public void addToWidget (HomePageWidget widget) {
        if (widgets == null) {
            widgets = new ArrayList<HomePageWidget>();
        }
        widgets.add(widget);
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum WidgetType {
        NUT_SHELL_WIDGET("nutshellwidget"),
        GROUPED_ACTION_CARD("groupedactioncard"),
        RESERVED_SPACES("recentreservedspacecard"),
        SPACE_FINDER("spacefinder"),
        TINY_LIST_VIEW("listview"),
        PERSON_SUMMARY("personSummary"),
        ONGOING_WORK("ongoingWork"),
        ONGOING_TRIP("ongoingTrip"),
        OVERDUE_APPOINTMENTS("overdueAppointments"),
        TODAYS_APPOINTMENTS("todaysAppointments");

        private String name;

        WidgetType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return ordinal() + 1;
        }

        public static HomePageWidget.WidgetType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values() [value - 1];
            }
            return null;
        }
    }

}
