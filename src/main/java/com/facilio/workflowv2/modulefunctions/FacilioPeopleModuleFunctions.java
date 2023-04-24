package com.facilio.workflowv2.modulefunctions;

import com.facilio.beans.PermissionSetBean;
import com.facilio.beans.UserScopeBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.scriptengine.annotation.ScriptModule;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ScriptModule(moduleName = FacilioConstants.ContextNames.PEOPLE)
public class FacilioPeopleModuleFunctions extends FacilioModuleFunctionImpl {

    public Object getPeopleWithRoles(Map<String, Object> globalParams, List<Object> objects) throws Exception {

        if(CollectionUtils.isNotEmpty(objects) && objects.size() > 1) {
            FacilioChain chain = ReadOnlyChainFactory.getPeopleDetailsChain();
            chain.getContext().put(FacilioConstants.ContextNames.ID, (Long)objects.get(1));
            chain.execute();

            PeopleContext people = (PeopleContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
            if(people != null) {
                Map<String, Object> map = FieldUtil.getAsProperties(people);
                return map;
            }
        }
        return null;
    }

    public List<Map<String, Object>> getPeopleForRoles(Map<String,Object> globalParams,List<Object> objects) throws Exception {
        List<Long> roleIds = (List<Long>) objects.get(1);
        if(CollectionUtils.isNotEmpty(roleIds)) {
            List<Map<String, Object>> peopleProps = PeopleAPI.getPeopleForRoles(roleIds);
            if(CollectionUtils.isNotEmpty(peopleProps)) {
                return peopleProps;
            }
        }
        return Collections.EMPTY_LIST;
    }

    public String updateScopingForPeople(Map<String,Object> globalParams,List<Object> objects) throws Exception {
        String linkName = (String) objects.get(1);
        List<Long> peopleIds = (List<Long>) objects.get(2);
        Long scopingId = null;
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        if(StringUtils.isNotEmpty(linkName) && CollectionUtils.isNotEmpty(peopleIds)) {
            ScopingContext scoping = userScopeBean.getScopingForLinkname(linkName);
            if(scoping != null) {
                scopingId = scoping.getId();
            }
        }
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            for (Long peopleId : peopleIds) {
                userScopeBean.updatePeopleScoping(peopleId,scopingId);
            }
        }
        return "UPDATED";
    }

    public String updatePermissionSetsForPeople(Map<String,Object> globalParams,List<Object> objects) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<String> linkNames = (List<String>) objects.get(1);
        List<Long> peopleIds = (List<Long>) objects.get(2);
        List<Long> permissionSetIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(linkNames)) {
            List<PermissionSetContext> permissionSets = permissionSetBean.getPermissionSet(linkNames);
            if(CollectionUtils.isNotEmpty(permissionSets)) {
                permissionSetIds = permissionSets.stream().map(PermissionSetContext::getId).collect(Collectors.toList());
            }
        }
        if(CollectionUtils.isNotEmpty(peopleIds)) {
            for (Long peopleId : peopleIds) {
                permissionSetBean.updateUserPermissionSets(peopleId, permissionSetIds);
            }
        }
        return "UPDATED";
    }

    @Override
    public void add(Map<String,Object> globalParams, List<Object> objects) throws Exception {
        v3Add(globalParams, objects);
    }

}
