package com.facilio.agentv2.iotmessage;

import com.facilio.agentv2.point.Point;
import org.json.simple.JSONArray;

import java.util.List;

public class MessengerUtil
{

    public static JSONArray getPointsData(List<Point> points){
        JSONArray array = new JSONArray();
        points.forEach(point -> array.add(point.getChildJSON()));
        return array;
    }
}
