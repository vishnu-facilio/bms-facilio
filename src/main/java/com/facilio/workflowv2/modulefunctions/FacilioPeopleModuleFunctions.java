package com.facilio.workflowv2.modulefunctions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptModule;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Override
    public void add(Map<String,Object> globalParams, List<Object> objects) throws Exception {
        v3Add(globalParams, objects);
    }

}
