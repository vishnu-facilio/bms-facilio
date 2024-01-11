package com.facilio.wmsv2.message;

import com.facilio.fms.message.Message;
import com.facilio.modules.FieldUtil;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.endpoint.LiveSession.LiveSessionType;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@Data
public class WebMessage extends Message {

    private LiveSessionType sessionType;

    private String topic;

    private String redisTopic;

    @JsonIgnore
    private LiveSession liveSession;

    private Long appId;

    private String action;

    @Override
    public String toString() {
        return toJson().toString();
    }

    public void setLiveSessionId(String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            this.liveSession = SessionManager.getInstance().getLiveSession(uuid);
        }
    }

    public static WebMessage getMessage(JSONObject object) throws Exception {
        return FieldUtil.getAsBeanFromJson(object, WebMessage.class);
    }

}
