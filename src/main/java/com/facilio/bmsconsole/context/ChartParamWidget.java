package com.facilio.bmsconsole.context;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.util.Map;

@Getter
public class ChartParamWidget extends PageSectionWidgetContext {

    public Map<String, Object> getChartParams() {
        return chartParams;
    }

    public void setChartParams(Map<String, Object> chartParams) {
        this.chartParams = chartParams;
    }

    private Map<String, Object> chartParams;
}
