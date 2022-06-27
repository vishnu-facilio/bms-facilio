package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MigrationCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                .module(module)
                .beanClass(V3PeopleContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("People.ORGID", "orgId", String.valueOf(17), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("People.PEOPLE_TYPE", String.valueOf(fieldMap.get("peopleType")), String.valueOf(1), NumberOperators.EQUALS))
                ;
        List<V3PeopleContext> props = builder.fetchDeleted().get();
        List<FacilioField> userFields = new ArrayList<FacilioField>();
        FacilioModule usermodule = ModuleFactory.getOrgUserModule();
        FacilioField peopleid = new FacilioField();
        peopleid.setName("peopleID");
        peopleid.setDataType(FieldType.NUMBER);
        peopleid.setColumnName("PEOPLE_ID");
        peopleid.setModule(usermodule);
        userFields.add(peopleid);

        FacilioField userid = new FacilioField();
        userid.setName("userId");
        userid.setDataType(FieldType.NUMBER);
        userid.setColumnName("USERID");
        userid.setModule(usermodule);
        userFields.add(userid);

        FacilioField orgid = new FacilioField();
        orgid.setName("orgId");
        orgid.setDataType(FieldType.NUMBER);
        orgid.setColumnName("ORGID");
        orgid.setModule(usermodule);
        userFields.add(orgid);

        List<FacilioField> accUserFields = IAMAccountConstants.getAccountsOrgUserFields();
        Map<String, FacilioField> accUserFieldsMap = FieldFactory.getAsMap(accUserFields);
        List<String> email = new ArrayList<>();
        List<FacilioField> finalFields = new ArrayList<>();
        finalFields.addAll(userFields);
        finalFields.addAll(fields);
        for(V3PeopleContext prop : props){
            Long people_id = prop.getId();
            GenericSelectRecordBuilder sbuilder = new GenericSelectRecordBuilder()
                    .select(userFields)
                    .table("ORG_Users")
                    .innerJoin("Account_ORG_Users")
                    .on("Account_ORG_Users.USERID = ORG_Users.USERID")
                    .andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(17), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("ORG_Users.PEOPLE_ID", "peopleID", String.valueOf(people_id), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", String.valueOf(-1), NumberOperators.EQUALS));
            List<Map<String, Object>> pro = sbuilder.get();
            for(Map<String, Object> p: pro){
                Long userId = (Long) p.get("userId");
                GenericSelectRecordBuilder sebuilder = new GenericSelectRecordBuilder()
                        .select(finalFields)
                        .table("ORG_Users")
                        .innerJoin("People")
                        .on("People.ID = ORG_Users.PEOPLE_ID")
                        .andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(17), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("ORG_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
                //peopletype check
                List<Map<String, Object>> pr = sebuilder.get();
                for(Map<String, Object> pi: pr){
                    email.add((String) pi.get("email"));
                }
            }
            for(String eml : email){
                SelectRecordsBuilder<V3PeopleContext> embuilder = new SelectRecordsBuilder<V3PeopleContext>()
                        .module(module)
                        .beanClass(V3PeopleContext.class)
                        .select(fields)
                        .andCondition(CriteriaAPI.getCondition("People.ORGID", "orgId", String.valueOf(17), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("People.EMAIL", "email", eml, StringOperators.IS))
                        ;
                List<V3PeopleContext> em = embuilder.get();
                List<Long> deletedId = new ArrayList<>();
                List<Long> currentId = new ArrayList<>();
                Map<String,Object> up = new HashMap<>();
                for(V3PeopleContext e : em){
                    //got id
                    if(e.isDeleted()){
                        deletedId.add(e.getId());
                    }else{
                        currentId.add(e.getId());
                    }
                }
                up.put("peopleId",Strings.join(currentId,','));
                UpdateRecordBuilder updateBuilder = new UpdateRecordBuilder()
                        .fields(userFields)
                        .module(module)
                        .andCondition(CriteriaAPI.getCondition("People.ORGID", "orgId", String.valueOf(17), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("ORG_Users.PEOPLE_ID", "peopleID", Strings.join(deletedId,','), NumberOperators.EQUALS));
                int res = updateBuilder.updateViaMap(up);
            }
        }
        return false;
    }
}
