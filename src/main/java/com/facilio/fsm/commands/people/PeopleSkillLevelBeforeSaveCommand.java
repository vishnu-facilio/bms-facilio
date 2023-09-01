package com.facilio.fsm.commands.people;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.PeopleSkillLevelContext;
import com.facilio.fsm.context.ServiceSkillsContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class PeopleSkillLevelBeforeSaveCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(PeopleSkillLevelContext.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<PeopleSkillLevelContext> peopleSkillLevelContext = (List<PeopleSkillLevelContext>) recordMap.get(context.get("moduleName"));
        for (int i = 0; i < peopleSkillLevelContext.size(); i++) {
            PeopleSkillLevelContext peopleSkillLevel = peopleSkillLevelContext.get(i);
            V3PeopleContext peopleContext = peopleSkillLevel.getPeople();
            ServiceSkillsContext skill = peopleSkillLevel.getSkill();
            int level = peopleSkillLevel.getLevelEnum().getIndex();
            boolean skillAndLevelAlreadyExist = false;
            boolean skillAlreadyExist = false;
            try {
                skillAndLevelAlreadyExist = checkIfAlreadySkillAndLevelAssociatedToPeople(peopleContext, skill, level);
                if (skillAndLevelAlreadyExist) {
                    peopleSkillLevel.setSuccessMsg("Skill And Level Already Associated");
                    return true;
                }
                skillAlreadyExist = checkIfAlreadySkillExists(peopleContext, skill, level, peopleSkillLevel);
                if (skillAlreadyExist) {
                    peopleSkillLevel.setSuccessMsg("Level Updated Succesfully");
                    return true;
                }
            } catch (Exception e) {
                LOGGER.info("Exception occured :: " + e);
            }

        }


        return false;
    }

    private static boolean checkIfAlreadySkillAndLevelAssociatedToPeople(V3PeopleContext peopleContext, ServiceSkillsContext skill, int level) throws Exception {
        if (peopleContext != null && skill != null && level > 0) {
            long peopleId = peopleContext.getId();
            long skillId = skill.getId();
            SelectRecordsBuilder<PeopleSkillLevelContext> skillBuilder = new SelectRecordsBuilder<PeopleSkillLevelContext>()
                    .select(Constants.getModBean().getAllFields(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                    .beanClass(PeopleSkillLevelContext.class)
                    .module(Constants.getModBean().getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                    .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "people", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SKILL_ID", "skill", String.valueOf(skillId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("LEVEL", "level", String.valueOf(level), NumberOperators.EQUALS));
            List<PeopleSkillLevelContext> peopleSkills = skillBuilder.get();
            if (peopleSkills != null && !peopleSkills.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIfAlreadySkillExists(V3PeopleContext peopleContext, ServiceSkillsContext skill, int level, PeopleSkillLevelContext peopleSkillLevelContext) throws Exception {
        if (peopleContext != null && skill != null && level > 0) {
            long peopleId = peopleContext.getId();
            long skillId = skill.getId();

            SelectRecordsBuilder<PeopleSkillLevelContext> skillBuilder = new SelectRecordsBuilder<PeopleSkillLevelContext>()
                    .select(Constants.getModBean().getAllFields(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                    .beanClass(PeopleSkillLevelContext.class)
                    .module(Constants.getModBean().getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                    .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "people", String.valueOf(peopleId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SKILL_ID", "skill", String.valueOf(skillId), NumberOperators.EQUALS));

            List<PeopleSkillLevelContext> peopleSkills = skillBuilder.get();
            if (peopleSkills != null && !peopleSkills.isEmpty()) {
                UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
                        .module(Constants.getModBean().getModule(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                        .fields(Constants.getModBean().getAllFields(FacilioConstants.PeopleSkillLevel.PEOPLE_SKILL_LEVEL))
                        .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "people", String.valueOf(peopleId), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("SKILL_ID", "skill", String.valueOf(skillId), NumberOperators.EQUALS));

                updateRecordBuilder.update(peopleSkillLevelContext);
                return true;
            }

        }
        return false;
    }
}
