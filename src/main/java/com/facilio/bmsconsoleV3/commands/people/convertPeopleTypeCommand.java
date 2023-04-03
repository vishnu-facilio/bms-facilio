package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class convertPeopleTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> peopleIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        Integer peopleTypeInt = (Integer) context.get(FacilioConstants.ContextNames.PEOPLE_TYPE);
        Long lookupId = (Long) context.get("lookupId");

        ModuleBean modBean = Constants.getModBean();
        FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        FacilioField peopleTypeField = modBean.getField(FacilioConstants.ContextNames.PEOPLE_TYPE, FacilioConstants.ContextNames.PEOPLE);
        V3PeopleContext.PeopleType newPeopleType = V3PeopleContext.PeopleType.valueOf(peopleTypeInt);

        if(CollectionUtils.isNotEmpty(peopleIds)){
            for (Long peopleId : peopleIds){
                V3PeopleContext people = V3PeopleAPI.getPeopleById(peopleId);
                FacilioModule existingModule = getModuleForPeopleType(people.getPeopleTypeEnum());
                if(existingModule != null){
                    GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                            .table(existingModule.getTableName())
                            .andCondition(CriteriaAPI.getIdCondition(peopleId,existingModule));
                    builder.delete();
                }
                PeopleAPI.deletePeopleUsers(Collections.singletonList(peopleId));
                FacilioModule newModule = getModuleForPeopleType(newPeopleType);
                if(newModule != null){
                    long localId = getModuleLocalId(newModule.getName());

                    List<FacilioField> addFields = new ArrayList<>();
                    addFields.add(FieldFactory.getIdField(newModule));
                    addFields.add(FieldFactory.getModuleIdField(newModule));
                    addFields.add(modBean.getField("localId", newModule.getName()));


                    Map<String, Object> props = new HashMap<>();
                    props.put("moduleId",newModule.getModuleId());
                    props.put("id",peopleId);
                    props.put("localId",++localId);

                    switch (newModule.getName()){
                        case FacilioConstants.ContextNames.TENANT_CONTACT:
                            addFields.add(modBean.getField("tenant",newModule.getName()));
                            props.put("tenant",lookupId);
                            break;
                        case FacilioConstants.ContextNames.VENDOR_CONTACT:
                            addFields.add(modBean.getField("vendor",newModule.getName()));
                            props.put("vendor",lookupId);
                            break;
                        case FacilioConstants.ContextNames.CLIENT_CONTACT:
                            addFields.add(modBean.getField("client",newModule.getName()));
                            props.put("client",lookupId);
                            break;
                    }


                    GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                            .table(newModule.getTableName())
                            .fields(addFields)
                            .addRecord(props);
                    insertBuilder.save();

                    updateModuleLocalId(newModule.getName(),localId);
                }
                people.setPeopleType(newPeopleType.getIndex());
                V3RecordAPI.updateRecord(people,peopleModule,Collections.singletonList(peopleTypeField));
            }
        }

        return false;
    }

    private FacilioModule getModuleForPeopleType(V3PeopleContext.PeopleType peopleType) throws Exception{
        ModuleBean modBean = Constants.getModBean();
        switch (peopleType){
            case EMPLOYEE:
                return modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
            case TENANT_CONTACT:
                return modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
            case VENDOR_CONTACT:
                return modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
            case CLIENT_CONTACT:
                return modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
        }
        return null;
    }

    private long getModuleLocalId(String moduleName) throws Exception {

        FacilioModule module = ModuleFactory.getModuleLocalIdModule();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getModuleLocalIdFields())
                .andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS))
                .forUpdate()
                ;

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if(props != null && !props.isEmpty()) {
            return (long) props.get(0).get("localId");
        }
        return -1;

    }

    private int updateModuleLocalId(String moduleName, long lastLocalId) throws Exception {

        FacilioModule module = ModuleFactory.getModuleLocalIdModule();
        List<FacilioField> fields = FieldFactory.getModuleLocalIdFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleName"), moduleName, StringOperators.IS))
                ;


        Map<String, Object> prop = new HashMap<>();
        prop.put("localId", lastLocalId);
        return updateRecordBuilder.update(prop);

    }
}
