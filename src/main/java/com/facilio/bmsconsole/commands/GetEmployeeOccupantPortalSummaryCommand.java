package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;

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
        List<LookupField> empLookups = new ArrayList<LookupField>();
        LookupField departmentField = (LookupField) empfieldsAsMap.get("department");
        empLookups.add(departmentField);
        
        
        SelectRecordsBuilder<EmployeeContext> employeebuilder = new SelectRecordsBuilder<EmployeeContext>()
                .moduleName(empModule.getName())
                .select(empFields)
                .beanClass(EmployeeContext.class)
                .limit(count)
                .fetchSupplements(empLookups);
        
        if(recordId > -1) {
        	employeebuilder.andCondition(CriteriaAPI.getIdCondition(recordId, empModule));
        }

        List<EmployeeContext> emplist = employeebuilder.get();
        context.put(FacilioConstants.ContextNames.EMPLOYEE, emplist);
        
        
        FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
        List<FacilioField> deskFields = modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.DESKS);
        Map<String, FacilioField> deskfieldsAsMap = FieldFactory.getAsMap(deskFields);
        List<LookupField> deskLookups = new ArrayList<LookupField>();
        LookupField buildingField = (LookupField) deskfieldsAsMap.get("building");
        deskLookups.add(buildingField);
        LookupField floorField = (LookupField) deskfieldsAsMap.get("floor");
        deskLookups.add(floorField);
        
        SelectRecordsBuilder<V3DeskContext> deskbuilder = new SelectRecordsBuilder<V3DeskContext>()
                .moduleName(deskModule.getName())
                .select(deskFields)
                .beanClass(V3DeskContext.class)
                .andCondition(CriteriaAPI.getCondition(deskfieldsAsMap.get("isArchived"), String.valueOf(false), BooleanOperators.IS))
                .limit(count)
                .fetchSupplements(deskLookups);
        
        if(recordId > -1) {
        	deskbuilder.andCondition(CriteriaAPI.getCondition(deskfieldsAsMap.get("employee"), String.valueOf(recordId), PickListOperators.IS));
        }

        List<V3DeskContext> desklist = deskbuilder.get();
        context.put(FacilioConstants.ContextNames.Floorplan.DESKS, desklist);
        
        
        
        FacilioModule serviceReqModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST);
        List<FacilioField> srFields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_REQUEST);
        Map<String, FacilioField> srfieldsAsMap = FieldFactory.getAsMap(srFields);
        List<LookupField> srLookups = new ArrayList<LookupField>();
        LookupField srstatusField = (LookupField) deskfieldsAsMap.get(FacilioConstants.ContextNames.MODULE_STATE);
        if(srstatusField != null) {
        	srLookups.add(srstatusField);
        }
        FacilioStatus closedStatus = TicketAPI.getStatus(serviceReqModule, "closed");
        
        SelectRecordsBuilder<ServiceRequestContext> srbuilder = new SelectRecordsBuilder<ServiceRequestContext>()
                .moduleName(serviceReqModule.getName())
                .select(srFields)
                .beanClass(ServiceRequestContext.class)
                .limit(count)
                .fetchSupplements(srLookups);
        if(srfieldsAsMap.get(FacilioConstants.ContextNames.MODULE_STATE) != null && closedStatus != null) {
        	srbuilder.andCondition(CriteriaAPI.getCondition(srfieldsAsMap.get(FacilioConstants.ContextNames.MODULE_STATE),String.valueOf(closedStatus.getId()) ,PickListOperators.ISN_T));
        }
        
        if(recordId > -1) {
        	srbuilder.andCondition(CriteriaAPI.getCondition(srfieldsAsMap.get("requester"), String.valueOf(recordId), PickListOperators.IS));
        }

        List<ServiceRequestContext> srlist = srbuilder.get();
        context.put(FacilioConstants.ContextNames.SERVICE_REQUEST, srlist);
        
        
        
        FacilioModule visitorInviteMod = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
        List<FacilioField> inviteFields = modBean.getAllFields(FacilioConstants.ContextNames.INVITE_VISITOR);
        Map<String, FacilioField> invitefieldsAsMap = FieldFactory.getAsMap(inviteFields);
        long currentTime = System.currentTimeMillis();
        List<LookupField> inviteLookups = new ArrayList<LookupField>();
        LookupField invitestatusField = (LookupField) invitefieldsAsMap.get(FacilioConstants.ContextNames.MODULE_STATE);
        inviteLookups.add(invitestatusField);
        LookupField visitorTypeField =(LookupField) invitefieldsAsMap.get("visitorType");
        inviteLookups.add(visitorTypeField);
        LookupField visitorField =(LookupField) invitefieldsAsMap.get("visitor");
        inviteLookups.add(visitorField);
        
        SelectRecordsBuilder<InviteVisitorContextV3> invitebuilder = new SelectRecordsBuilder<InviteVisitorContextV3>()
                .moduleName(visitorInviteMod.getName())
                .select(inviteFields)
                .beanClass(InviteVisitorContextV3.class)
                .andCondition(CriteriaAPI.getCondition(invitefieldsAsMap.get("expectedStartTime"), String.valueOf(currentTime) , DateOperators.IS_AFTER))
                .limit(count)
                .fetchSupplements(inviteLookups);

        List<InviteVisitorContextV3> invitelist = invitebuilder.get();
        context.put(FacilioConstants.ContextNames.INVITE_VISITOR, invitelist);
        
        
        
        FacilioModule bookingModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        List<FacilioField> bookingfields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
        Map<String, FacilioField> bookingfieldsAsMap = FieldFactory.getAsMap(bookingfields);
        FacilioModule pplModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        LookupFieldMeta facilityField = new LookupFieldMeta((LookupField) bookingfieldsAsMap.get("facility"));
        LookupField facilityLocationField = (LookupField) modBean.getField("location", FacilioConstants.ContextNames.FacilityBooking.FACILITY);
        facilityField.addChildLookupField(facilityLocationField);

        SupplementRecord reservedFor = (SupplementRecord) bookingfieldsAsMap.get("reservedFor");

        MultiLookupMeta internalAttendees = new MultiLookupMeta((MultiLookupField) bookingfieldsAsMap.get("internalAttendees"));
        FacilioField emailField = FieldFactory.getField("email", "EMAIL", pplModule, FieldType.STRING);
        FacilioField phoneField = FieldFactory.getField("phone", "PHONE", pplModule, FieldType.STRING);

        List<FacilioField> selectFieldsList = new ArrayList<>();
        selectFieldsList.add(emailField);
        selectFieldsList.add(phoneField);

        internalAttendees.setSelectFields(selectFieldsList);

        fetchLookupsList.add(facilityField);
        fetchLookupsList.add(reservedFor);
        fetchLookupsList.add(internalAttendees);

        SelectRecordsBuilder<V3FacilityBookingContext> bookingbuilder = new SelectRecordsBuilder<V3FacilityBookingContext>()
                .moduleName(bookingModule.getName())
                .select(bookingfields)
                .beanClass(V3FacilityBookingContext.class)
                .andCondition(CriteriaAPI.getCondition(bookingfieldsAsMap.get("isCancelled"),String.valueOf(false), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(invitefieldsAsMap.get("bookingDate"), String.valueOf(currentTime) , DateOperators.IS_AFTER))
                .limit(count);
        
        if(recordId > -1) {
        	bookingbuilder.andCondition(CriteriaAPI.getCondition(bookingfieldsAsMap.get("reservedFor"), String.valueOf(recordId), PickListOperators.IS));
        }

        List<V3FacilityBookingContext> bookinglist = bookingbuilder.get();
        if(CollectionUtils.isNotEmpty(bookinglist)) {
            for(V3FacilityBookingContext booking : bookinglist) {
                if (booking != null) {
                    booking.setSlotList(FacilityAPI.getBookingSlots(booking.getId()));
                }
            }
        }
        context.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, bookinglist);
        

		return false;
	}

}
