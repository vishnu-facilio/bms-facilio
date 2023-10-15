package com.facilio.fsm.commands.people;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.PeopleSkillLevelContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchPeopleSkillLevelCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3PeopleContext> peopleList = Constants.getRecordList((FacilioContext) context);

        if(CollectionUtils.isNotEmpty(peopleList)) {


            List<Long> peopleIds = peopleList.stream().map(V3PeopleContext::getId).collect(Collectors.toList());

            ModuleBean modBean = Constants.getModBean();
            FacilioModule module = modBean.getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL);
            if (module != null) {
                List<FacilioField> fields = modBean.getAllFields(module.getName());

                Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

                List<LookupField> lookupFields = Arrays.asList((LookupField) fieldsAsMap.get("people"), (LookupField) fieldsAsMap.get("skill"));

                SelectRecordsBuilder<PeopleSkillLevelContext> select = new SelectRecordsBuilder<PeopleSkillLevelContext>()
                        .moduleName(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL)
                        .select(fields)
                        .beanClass(PeopleSkillLevelContext.class)
                        .andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("people", FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL), peopleIds, NumberOperators.EQUALS));

                select.fetchSupplements(lookupFields);

                List<PeopleSkillLevelContext> peopleSkillLevel = select.get();

                if (CollectionUtils.isNotEmpty(peopleSkillLevel)) {

                    Map<Long, List<PeopleSkillLevelContext>> peopleIdVsSkillContext = peopleSkillLevel.stream().collect(Collectors.groupingBy(p -> p.getPeople().getId()));

                    peopleList.forEach(people -> people.setPeopleSkillLevel(peopleIdVsSkillContext.get(people.getId())));
                }
            }
        }

        return false;
    }
}

