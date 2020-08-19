package com.facilio.wmsv2.filters;

import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.message.Message;

@WMSFilter(priority = 0)
public class PopulateMessage extends BaseFilter {

    @Override
    public Message incoming(Message message) {
        fillBasicData(message);

        // TODO - need to get device info
//        if (session.getLiveSessionType() == com.facilio.wms.endpoints.LiveSession.LiveSessionType.REMOTE_SCREEN) {
//            try {
//                RemoteScreenContext remoteScreen = FacilioService.runAsServiceWihReturn(() ->  ScreenUtil.getRemoteScreen(id));
//                message.setOrgId(remoteScreen.getOrgId());
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
        return message;
    }

    private void fillBasicData(Message message) {
        LiveSession session = message.getLiveSession();
        message.setSessionType(session.getLiveSessionType());
        message.setOrgId(session.getOrgId());
    }

    @Override
    public Message outgoing(Message message) {
        return message;
    }
}
