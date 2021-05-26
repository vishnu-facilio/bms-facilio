package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetEmployeeOccupantPortalSummaryCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(GetEmployeeOccupantPortalSummaryCommand.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		int count = (int) context.get(FacilioConstants.ContextNames.COUNT);
        
        FacilioModule empModule = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
        List<FacilioField> empFields = modBean.getAllFields(FacilioConstants.ContextNames.EMPLOYEE);
        Map<String, FacilioField> empfieldsAsMap = FieldFactory.getAsMap(empFields);
        
        SelectRecordsBuilder<EmployeeContext> employeebuilder = new SelectRecordsBuilder<EmployeeContext>()
                .moduleName(empModule.getName())
                .select(empFields)
                .beanClass(EmployeeContext.class)
                .limit(count);
        
        if(recordId > -1) {
        	employeebuilder.andCondition(CriteriaAPI.getIdCondition(recordId, empModule));
        }

        List<EmployeeContext> emplist = employeebuilder.get();
        context.put(FacilioConstants.ContextNames.EMPLOYEE, emplist);
        
        
        FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        List<FacilioField> deskFields = modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.DESKS);
        Map<String, FacilioField> deskfieldsAsMap = FieldFactory.getAsMap(deskFields);
        
        SelectRecordsBuilder<V3DeskContext> deskbuilder = new SelectRecordsBuilder<V3DeskContext>()
                .moduleName(deskModule.getName())
                .select(deskFields)
                .beanClass(V3DeskContext.class)
                .limit(count);
        
        if(recordId > -1) {
        	deskbuilder.andCondition(CriteriaAPI.getCondition(deskfieldsAsMap.get("employee"), String.valueOf(recordId), PickListOperators.IS));
        }

        List<V3DeskContext> desklist = deskbuilder.get();
        context.put(FacilioConstants.ContextNames.Floorplan.DESKS, desklist);
        
        
        
        FacilioModule serviceReqModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);
        List<FacilioField> srFields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_REQUEST);
        Map<String, FacilioField> srfieldsAsMap = FieldFactory.getAsMap(srFields);
        FacilioStatus closedStatus = TicketAPI.getStatus(serviceReqModule, "closed");
        
        SelectRecordsBuilder<ServiceRequestContext> srbuilder = new SelectRecordsBuilder<ServiceRequestContext>()
                .moduleName(serviceReqModule.getName())
                .select(srFields)
                .beanClass(ServiceRequestContext.class)
                .andCondition(CriteriaAPI.getCondition(srfieldsAsMap.get(FacilioConstants.ContextNames.MODULE_STATE),String.valueOf(closedStatus.getId()) ,PickListOperators.ISN_T))
                .limit(count);
        
        if(recordId > -1) {
        	srbuilder.andCondition(CriteriaAPI.getCondition(srfieldsAsMap.get("requester"), String.valueOf(recordId), PickListOperators.IS));
        }

        List<ServiceRequestContext> srlist = srbuilder.get();
        context.put(FacilioConstants.ContextNames.SERVICE_REQUEST, srlist);
        
        
        
        FacilioModule visitorInviteMod = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE);
        List<FacilioField> inviteFields = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_INVITE);
        Map<String, FacilioField> invitefieldsAsMap = FieldFactory.getAsMap(inviteFields);
        long currentTime = System.currentTimeMillis();
        
        SelectRecordsBuilder<VisitorInviteContext> invitebuilder = new SelectRecordsBuilder<VisitorInviteContext>()
                .moduleName(visitorInviteMod.getName())
                .select(inviteFields)
                .beanClass(VisitorInviteContext.class)
                .andCondition(CriteriaAPI.getCondition(invitefieldsAsMap.get("expectedStartTime"), String.valueOf(currentTime) , DateOperators.IS_AFTER))
                .limit(count);

        List<VisitorInviteContext> invitelist = invitebuilder.get();
        context.put(FacilioConstants.ContextNames.VISITOR_INVITE, invitelist);
        
        
        
        FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        List<FacilioField> bookingfields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        Map<String, FacilioField> bookingfieldsAsMap = FieldFactory.getAsMap(bookingfields);

        SelectRecordsBuilder<V3FacilityBookingContext> bookingbuilder = new SelectRecordsBuilder<V3FacilityBookingContext>()
                .moduleName(bookingModule.getName())
                .select(bookingfields)
                .beanClass(V3FacilityBookingContext.class)
                .andCondition(CriteriaAPI.getCondition(bookingfieldsAsMap.get("isCancelled"),String.valueOf(false), BooleanOperators.IS))
                .limit(count);
        
        if(recordId > -1) {
        	bookingbuilder.andCondition(CriteriaAPI.getCondition(bookingfieldsAsMap.get("reservedFor"), String.valueOf(recordId), PickListOperators.IS));
        }

        List<V3FacilityBookingContext> bookinglist = bookingbuilder.get();
        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, bookinglist);
        

		return false;
	}

}
