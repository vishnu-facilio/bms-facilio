package com.facilio.sandbox.utils;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.componentpackage.bean.OrgSwitchBean;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

public class SandboxUtil {
    public static IAMUser constructUserObj(com.facilio.identity.client.dto.User user) throws Exception {
        return IAMUserUtil.getFacilioUser(user.getOrgId(), user.getUid());
    }
    public static Map<Long, String> getComponentTypeMap()  {
        List<ComponentType> componentTypeList = ComponentType.ORDERED_COMPONENT_TYPE_LIST;
        Map<Long, String> componentTypeMap = new  TreeMap<>();

        for (ComponentType componentType : componentTypeList) {
            long order = componentType.ordinal();
            String value = componentType.getValue();
            componentTypeMap.put(order, value);
        }
        return componentTypeMap;
    }
    public static OrgSwitchBean getOrgSwitchBean(long orgId) throws Exception {
        OrgSwitchBean orgBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean", orgId);
        return orgBean;
    }
    public static OrgSwitchBean getOrgSwitchBean() throws Exception {
        OrgSwitchBean orgSwitchBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean");
        return orgSwitchBean;
    }
}