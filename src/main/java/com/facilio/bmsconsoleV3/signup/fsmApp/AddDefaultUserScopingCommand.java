package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;

import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddDefaultUserScopingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<ScopingConfigContext> userScopingConfigList = new ArrayList<>();
        ScopingContext userScoping = new ScopingContext();
        userScoping.setScopeName(FacilioConstants.ContextNames.FieldServiceManagement.FIELD_AGENT_SCOPING);
        userScoping.setStatus(true);

        context.put(FacilioConstants.ContextNames.RECORD,userScoping);
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdateUserScopingChain();
        chain.execute(context);

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        context.put("scopingId", id);

        FacilioModule serviceAppointmentModule = moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        if(serviceAppointmentModule != null){
            Criteria fieldAgentCriteria = new Criteria();
            fieldAgentCriteria.addAndCondition(CriteriaAPI.getCondition(moduleBean.getField(FacilioConstants.ServiceAppointment.FIELD_AGENT,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT), FacilioConstants.Criteria.LOGGED_IN_PEOPLE, PickListOperators.IS));

            ScopingConfigContext scopingConfigContext = new ScopingConfigContext();
            scopingConfigContext.setModuleId(serviceAppointmentModule.getModuleId());
            scopingConfigContext.setCriteria(fieldAgentCriteria);
            userScopingConfigList.add(scopingConfigContext);
        }
        FacilioModule tripModule = moduleBean.getModule(FacilioConstants.Trip.TRIP);
        if(tripModule != null){
            Criteria fieldAgentCriteria = new Criteria();
            fieldAgentCriteria.addAndCondition(CriteriaAPI.getCondition(moduleBean.getField(FacilioConstants.ContextNames.PEOPLE,FacilioConstants.Trip.TRIP), FacilioConstants.Criteria.LOGGED_IN_PEOPLE, PickListOperators.IS));

            ScopingConfigContext scopingConfigContext = new ScopingConfigContext();
            scopingConfigContext.setModuleId(tripModule.getModuleId());
            scopingConfigContext.setCriteria(fieldAgentCriteria);
            userScopingConfigList.add(scopingConfigContext);
        }
        FacilioModule timeSheetModule = moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        if(timeSheetModule != null){
            Criteria fieldAgentCriteria = new Criteria();
            fieldAgentCriteria.addAndCondition(CriteriaAPI.getCondition(moduleBean.getField(FacilioConstants.ServiceAppointment.FIELD_AGENT,FacilioConstants.TimeSheet.TIME_SHEET), FacilioConstants.Criteria.LOGGED_IN_PEOPLE, PickListOperators.IS));

            ScopingConfigContext scopingConfigContext = new ScopingConfigContext();
            scopingConfigContext.setModuleId(timeSheetModule.getModuleId());
            scopingConfigContext.setCriteria(fieldAgentCriteria);
            userScopingConfigList.add(scopingConfigContext);
        }
        FacilioModule timeOffModule = moduleBean.getModule(FacilioConstants.TimeOff.TIME_OFF);
        if(timeOffModule != null){
            Criteria fieldAgentCriteria = new Criteria();
            fieldAgentCriteria.addAndCondition(CriteriaAPI.getCondition(moduleBean.getField(FacilioConstants.ContextNames.PEOPLE,FacilioConstants.TimeOff.TIME_OFF), FacilioConstants.Criteria.LOGGED_IN_PEOPLE, PickListOperators.IS));

            ScopingConfigContext scopingConfigContext = new ScopingConfigContext();
            scopingConfigContext.setModuleId(timeOffModule.getModuleId());
            scopingConfigContext.setCriteria(fieldAgentCriteria);
            userScopingConfigList.add(scopingConfigContext);
        }


        if(CollectionUtils.isNotEmpty(userScopingConfigList)) {
            context.put(FacilioConstants.ContextNames.RECORD, userScopingConfigList);
            FacilioChain configChain = TransactionChainFactoryV3.addOrUpdateUserScopingConfigChain();
            configChain.execute(context);
        }

        return false;
    }
}
