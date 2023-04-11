package com.facilio.bmsconsoleV3.signup.group;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddGroupAndGroupMemberModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule groupModule = constructGroupModule();
        modules.add(groupModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        List<FacilioModule> modules1 = new ArrayList<>();

        FacilioModule groupMemberModule = constructGroupMemberModule(groupModule);
        modules1.add(groupMemberModule);

        FacilioChain chain = TransactionChainFactory.addSystemModuleChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,groupModule.getName());
        chain.execute();
    }

    private FacilioModule constructGroupModule() {

        FacilioModule module = new FacilioModule(
                FacilioConstants.PeopleGroup.PEOPLE_GROUP,
                "Teams",
                "FacilioGroups",
                FacilioModule.ModuleType.BASE_ENTITY,
                true);

        module.setDescription("Organize and manage groups of users with shared access as part of a team.");

        List<FacilioField> fields = new ArrayList<>();

        fields.add(FieldFactory.getDefaultField("groupId","Group Id","GROUPID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("name","Name","GROUP_NAME",FieldType.STRING,true));
        fields.add(FieldFactory.getDefaultField("description","Description","GROUP_DESC",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("email","Email","GROUP_EMAIL",FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("createdBy", "CreatedBy", "CREATED_BY", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("isActive", "Is Active", "IS_ACTIVE", FieldType.BOOLEAN));
        fields.add(FieldFactory.getDefaultField("parent", "Parent", "PARENT", FieldType.NUMBER));

        module.setFields(fields);

        return module;
    }

    private FacilioModule constructGroupMemberModule(FacilioModule groupModule) throws Exception {

        FacilioModule module = new FacilioModule(
                FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER,
                "People Group Members",
                "FacilioGroupMembers",
                FacilioModule.ModuleType.SUB_ENTITY,
                true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField group = FieldFactory.getDefaultField("group","Group Id","GROUPID",FieldType.LOOKUP,true);
        group.setLookupModule(groupModule);

        fields.add(group);

        LookupField people = FieldFactory.getDefaultField("people", "People", "PEOPLE_ID", FieldType.LOOKUP);
        people.setLookupModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        fields.add(people);

        fields.add(FieldFactory.getDefaultField("memberId","Member Id","MEMBERID",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("ouid","Ouid Id","ORG_USERID",FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("memberRole","Member Role","MEMBER_ROLE",FieldType.NUMBER));

        module.setFields(fields);

        return module;
    }
}
