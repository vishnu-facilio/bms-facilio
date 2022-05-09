package com.facilio.bmsconsoleV3.context.floorplan;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class V3IndoorFloorPlanLayerContext extends V3Context {

    private Integer layertype;
    private String layer;

    public V3IndoorFloorPlanContext getIndoorfloorplan() {
        return indoorfloorplan;
    }

    public void setIndoorfloorplan(V3IndoorFloorPlanContext indoorfloorplan) {
        this.indoorfloorplan = indoorfloorplan;
    }

    private V3IndoorFloorPlanContext indoorfloorplan;
    private Integer order;


    public Integer getLayertype() {
        return layertype;
    }

    public void setLayertype(Integer layertype) {
        this.layertype = layertype;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


//    public String getLayerJSON() throws Exception {
//        if (getLayer() != null) {
//            return FieldUtil.getAsJSON(getLayer()).toJSONString();
//        }
//        return layer;
//    }

//    public void setLayerJSON(String layer) throws Exception {
//        if (layer != null && !layer.trim().isEmpty()) {
//            JSONObject layerObj = (JSONObject) new JSONParser().parse(layer);
//            this.customization = FieldUtil.getAsBeanFromJson(layerObj, V3FloorplanCustomizationContext.class);
//            this.customizationJSON=customizationString ;
//        }
//    }





}