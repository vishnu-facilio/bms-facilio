package com.facilio.bmsconsoleV3.context.floorplan;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class V3IndoorFloorPlanLayerClientContext {

    private String id;
    private String type;
    private String source;
    private JSONObject layout;
    private JSONObject paint;
    private JSONArray filter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public JSONObject getLayout() {
        return layout;
    }

    public void setLayout(JSONObject layout) {
        this.layout = layout;
    }

    public JSONObject getPaint() {
        return paint;
    }

    public void setPaint(JSONObject paint) {
        this.paint = paint;
    }

    public JSONArray getFilter() {
        return filter;
    }

    public void setFilter(JSONArray filter) {
        this.filter = filter;
    }


}