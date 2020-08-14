package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioEnum;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class UserNotificationContext extends V3Context {

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    private String appName;

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

    private JSONObject info;

    public void setInfoJsonStr(String infoStr) throws ParseException {
        info = FacilioUtil.parseJson(infoStr);
    }

    public String getInfoJsonStr() {
        if(info != null) {
            return info.toJSONString();
        }
        return null;
    }
    public Long getReadAt() {
        return readAt;
    }

    public void setReadAt(Long readAt) {
        this.readAt = readAt;
    }

    private Long readAt;

    public Long getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(Long seenAt) {
        this.seenAt = seenAt;
    }

    private Long seenAt;

    public long getApplication() {
        return application;
    }

    public void setApplication(long application) {
        this.application = application;
    }

    private long application;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private String subject ;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private long parentId;

//    public NotificationStatus getNotificationStatus() {
//        return notificationStatus;
//    }
//
//    public void setNotificationStatus(NotificationStatus notificationStatus) {
//        this.notificationStatus = notificationStatus;
//    }
//    public NotificationStatus getNotificationStatusEnum() {
//        return notificationStatus;
//    }
//
//    public void setNotificationStatus(int notificationStatus) {
//        this.notificationStatus = NotificationStatus.valueOf(notificationStatus);
//    }
//    private NotificationStatus notificationStatus;

    private NotificationStatus notificationStatus;
    public int getNotificationStatus() {
        if (notificationStatus == null) {
            return -1;
        }
        return notificationStatus.getIndex();
    }
    public NotificationStatus getNotificationStatusEnum() {
        return notificationStatus;
    }
    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
    public void setNotificationStatus(int notificationStatus) {
        this.notificationStatus = NotificationStatus.valueOf(notificationStatus);
    }

    public static enum NotificationStatus implements FacilioEnum {
        UNSEEN,
        UNREAD,
        SEEN
        ;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static NotificationStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public static UserNotificationContext instance(JSONObject obj) throws Exception {
        UserNotificationContext userNotification = new UserNotificationContext();
        JSONObject notiObj = (JSONObject) obj.get("notification");
        userNotification.setInfoJsonStr((String) notiObj.get("message"));
        userNotification.setSysCreatedTime(System.currentTimeMillis());
        userNotification.setSubject((String) notiObj.get("text"));
        userNotification.setTitle((String) notiObj.get("title"));
        userNotification.setApplication((long) obj.get("application"));
        userNotification.setNotificationStatus(UserNotificationContext.NotificationStatus.UNSEEN);
        return  userNotification;
    }
}

