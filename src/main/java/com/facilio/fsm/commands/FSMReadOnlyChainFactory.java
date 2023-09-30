package com.facilio.fsm.commands;

import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.GenerateSearchConditionCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.fsm.commands.dispatchBoard.FetchDispatcherEventsCommand;
import com.facilio.fsm.commands.dispatchBoard.FetchDispatcherMapDataCommand;
import com.facilio.fsm.commands.dispatchBoard.FetchDispatcherResourcesCommand;
import com.facilio.fsm.commands.serviceAppointment.FetchAllAppointments;
import com.facilio.fsm.commands.serviceAppointment.FetchServiceAppointmentListCommand;
import com.facilio.fsm.commands.serviceAppointment.FetchViewCriteriaCommand;

public class FSMReadOnlyChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain fetchPeopleListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterCommand());
        c.addCommand(new GenerateSearchConditionCommand());
        c.addCommand(new FetchDispatcherResourcesCommand());
        return c;
    }

    public static FacilioChain fetchEventsListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchDispatcherEventsCommand());
        return c;
    }

    public static FacilioChain fetchServiceAppointmentListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchViewCriteriaCommand());
        c.addCommand(new GenerateCriteriaFromFilterCommand());
        c.addCommand(new GenerateSearchConditionCommand());
        c.addCommand(new FetchServiceAppointmentListCommand());
        return c;
    }
    public static FacilioChain fetchDispatcherMapDataChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchDispatcherMapDataCommand());
        return c;
    }
    public static FacilioChain fetchAppointmentsChain(){
        FacilioChain c=getDefaultChain();
        c.addCommand(new FetchAllAppointments());
        return c;
    }

}
