package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioEnum;

import java.io.Serializable;
import java.util.List;

public class ApplicationLayoutContext implements Serializable {

    public ApplicationLayoutContext() {

    }

    public ApplicationLayoutContext(long applicationId, AppLayoutType layoutType, LayoutDeviceType deviceType, String appType) {
        this.applicationId = applicationId;
        this.appLayoutType = layoutType;
        this.layoutDeviceType = deviceType;
        this.appType = appType;
    }

    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }


    private long applicationId = -1;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public enum AppLayoutType implements FacilioEnum {
        SINGLE("Single"),
        DUAL("Dual");

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        AppLayoutType(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return getName();
        }
        public static AppLayoutType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private AppLayoutType appLayoutType;
    public int getAppLayoutType() {
        if (appLayoutType != null) {
            return appLayoutType.getIndex();
        }
        return -1;
    }
    public void setAppLayoutType(int type) {
        this.appLayoutType = AppLayoutType.valueOf(type);
    }
    public AppLayoutType getAppLayoutTypeEnum() {
        return appLayoutType;
    }
    public void setAppLayoutType(AppLayoutType type) {
        this.appLayoutType = type;
    }



    public enum LayoutDeviceType implements FacilioEnum {
        WEB("Web"),
        MOBILE("Mobile");

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        LayoutDeviceType(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return getName();
        }
        public static LayoutDeviceType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private LayoutDeviceType layoutDeviceType;
    public int getLayoutDeviceType() {
        if (layoutDeviceType != null) {
            return layoutDeviceType.getIndex();
        }
        return -1;
    }
    public void setLayoutDeviceType(int deviceType) {
        this.layoutDeviceType = LayoutDeviceType.valueOf(deviceType);
    }
    public LayoutDeviceType getLayoutDeviceTypeEnum() {
        return layoutDeviceType;
    }
    public void setLayoutDeviceType(LayoutDeviceType type) {
        this.layoutDeviceType = type;
    }

    private String appType;

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    private List<WebTabGroupContext> webTabGroupList;

    public List<WebTabGroupContext> getWebTabGroupList() {
        return webTabGroupList;
    }

    public void setWebTabGroupList(List<WebTabGroupContext> webTabGroupList) {
        this.webTabGroupList = webTabGroupList;
    }
}
