package com.facilio.bmsconsoleV3.commands.communityFeatures.contactdirectory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddPeopleIfNotExistsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ContactDirectoryContext> contacts = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);

        if(CollectionUtils.isNotEmpty(contacts)) {
            List<V3PeopleContext>  peopleList =  new ArrayList<>();
            for(ContactDirectoryContext contact : contacts) {
                if(contact.getPeople() == null || contact.getPeople().getId() <= 0){
                    if(StringUtils.isEmpty(contact.getContactName())){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Name is needed for a contact entry");
                    }
                    V3PeopleContext people= new V3PeopleContext();
                    people.setName(contact.getContactName());
                    people.setEmail(contact.getContactEmail());
                    people.setPhone(contact.getContactPhone());
                    people.setPeopleType(V3PeopleContext.PeopleType.OTHERS.getIndex());
                    if(StringUtils.isNotEmpty(contact.getContactEmail()) && V3PeopleAPI.checkForDuplicatePeople(people)) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "People with the same email id already exists");
                    }
                    peopleList.add(people);
                }
                else {
                    V3PeopleContext people = (V3PeopleContext) V3RecordAPI.getRecord(module.getName(), contact.getPeople().getId(), V3PeopleContext.class);
                    contact.setContactName(people.getName());
                    contact.setContactEmail(people.getEmail());
                    contact.setContactPhone(people.getPhone());
                }
            }
            if(CollectionUtils.isNotEmpty(peopleList)){
                V3RecordAPI.addRecord(true, peopleList, module, fields );
            }
        }

        return false;
    }
}
