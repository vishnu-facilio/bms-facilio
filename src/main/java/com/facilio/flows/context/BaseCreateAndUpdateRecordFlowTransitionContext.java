package com.facilio.flows.context;


import com.facilio.flowengine.context.Constants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter@Setter
public class BaseCreateAndUpdateRecordFlowTransitionContext extends FlowTransitionContext{
    private JSONObject recordData;

    @Override
    public void updateConfig() throws Exception {
        addConfigData(Constants.RECORD_DATA,recordData);
    }
}
