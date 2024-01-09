package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserNotificationContext extends V3Context {


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private Long parentId;


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

    public static enum NotificationStatus implements FacilioIntEnum {
        UNSEEN,
        UNREAD,
        SEEN
        ;

        @Override
        public Integer getIndex() {
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


    public Long getParentModule() {
        return parentModule;
    }

    public void setParentModule(Long parentModule) {
        this.parentModule = parentModule;
    }

    private Long parentModule;

    public Action getAction() throws Exception {
        if (getActionTypeEnum() != null) {
            return Action.getActionObj(this);
        }
        return null;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    private Action action;


    private ActionType actionType;
    public int getActionType() {
        if (actionType == null) {
            return -1;
        }
        return actionType.getIndex();
    }
    public ActionType getActionTypeEnum() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    public void setActionType(int actionType) {
        this.actionType = ActionType.valueOf(actionType);
    }
    public enum ActionType implements FacilioIntEnum {
        SUMMARY
        ;

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return null;
        }

        public static ActionType valueOf(int value) {
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
        if (obj.containsKey("application")) {
            userNotification.setApplication((long) obj.get("application"));
        }
        else {
            userNotification.setApplication(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
        }
        userNotification.setNotificationStatus(UserNotificationContext.NotificationStatus.UNSEEN);

        if (notiObj.containsKey("click_action")) {
            userNotification.addExtraParam("click_action", notiObj.get("click_action"));
        }
        if (notiObj.containsKey("content_available")) {
            userNotification.addExtraParam("content_available", notiObj.get("content_available"));
        }
        if (notiObj.containsKey("sound")) {
            userNotification.addExtraParam("sound", notiObj.get("sound"));
        }
        if (notiObj.containsKey("priority")) {
            userNotification.addExtraParam("priority", notiObj.get("priority"));
        }
        if(notiObj.containsKey("summary_id")){
            userNotification.addExtraParam("summary_id", notiObj.get("summary_id"));
        }

        return  userNotification;
    }

    private Map<String, Object> extraParams = new HashMap<>();
    private void addExtraParam(String key, Object value) {
        extraParams.put(key, value);
    }

    public Map<String, Object> getExtraParams() {
        return extraParams;
    }

    public static JSONObject getFcmObject(UserNotificationContext userNotification) throws Exception{
        JSONObject obj = new JSONObject();
        FacilioModule module = Constants.getModBean().getModule(userNotification.getParentModule());
        obj.put("summary_id",userNotification.getParentId());
        obj.put("module_name",module.getName());
        obj.put("text",userNotification.getSubject());
        obj.put("title",userNotification.getTitle());

        if (MapUtils.isNotEmpty(userNotification.getExtraParams())) {
            obj.putAll(userNotification.getExtraParams());
        }

        JSONObject notificationObj = new JSONObject();
        notificationObj.put("notification",obj);
        notificationObj.put("data",obj);

        return notificationObj;
    }

    public static JSONObject getFcmObjectMaintainence(UserNotificationContext userNotification) throws Exception{
        return getFcmObjectMaintainence(userNotification,null);
    }
    public static JSONObject getFcmObjectMaintainence(UserNotificationContext userNotification,String appLinkaName) throws Exception{
        JSONObject obj = new JSONObject();
        FacilioModule module = Constants.getModBean().getModule(userNotification.getParentModule());
        obj.put("summaryId",userNotification.getParentId());
        obj.put("moduleName",module.getName());
        obj.put("body",userNotification.getSubject());
        obj.put("title",userNotification.getTitle());
        obj.put("notificationId",userNotification.getId());
        obj.put("redirectTo","SUMMARY");
        obj.put("orgId",userNotification.getOrgId());
        if(StringUtils.isNotEmpty(appLinkaName)){
            obj.put("appLinkName",appLinkaName);
        }

        JSONObject notification = new JSONObject();
        notification.put("body",userNotification.getSubject());
        notification.put("title",userNotification.getTitle());
        notification.put("sound","default");

        JSONObject notificationObj = new JSONObject();
        notificationObj.put("notification",notification);
        notificationObj.put("data",obj);

        return notificationObj;
    }

    public static class Action implements Serializable {

        public ActionType getActionType() {
            return actionType;
        }

        public void setActionType(ActionType actionType) {
            this.actionType = actionType;
        }

        ActionType actionType;

        public JSONObject getActionData() {
            return actionData;
        }

        public void setActionData(JSONObject actionData) {
            this.actionData = actionData;
        }

        JSONObject actionData ;

        public Action() { }


        public Action(ActionType actionType, JSONObject actionData) {
            this.actionType = actionType;
            this.actionData = actionData;
        }

        public static Action getActionObj(UserNotificationContext user) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            ActionType value = user.getActionTypeEnum();
            JSONObject jsonObj = new JSONObject();
            switch(value) {
                case SUMMARY:
                    jsonObj.put("moduleId", user.getParentModule());
                    jsonObj.put("recordId", user.getParentId());
                    if (user.getParentModule() != null) {
                        jsonObj.put("module", modBean.getModule(user.getParentModule()));
                    }
                    break;
            }
            return new Action(value, jsonObj);
        }
    }


}

