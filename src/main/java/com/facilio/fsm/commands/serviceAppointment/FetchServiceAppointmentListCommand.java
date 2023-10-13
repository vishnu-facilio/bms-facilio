package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;

import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FetchServiceAppointmentListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        Long boardId = (Long) context.getOrDefault(FacilioConstants.Dispatcher.BOARD_ID,-1L);
        Long criteriaId = (Long) context.getOrDefault(FacilioConstants.ContextNames.CRITERIA,-1L);
        String orderBy = (String) context.get(FacilioConstants.ContextNames.ORDER_BY);
        String orderType = (String) context.getOrDefault(FacilioConstants.ContextNames.ORDER_TYPE,"desc");
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioField> selectFields = new ArrayList<>();
        List<String> defaultFieldNames = new ArrayList<>(Arrays.asList("code","name","location","site","priority","resolutionDueStatus","scheduledStartTime","scheduledEndTime"));
        for (String fieldName : defaultFieldNames){
            FacilioField selectField = moduleBean.getField(fieldName,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            selectFields.add(selectField);
        }

        String sortBy = "name";
        String sortOrder = "desc";

        int perPage = (int) context.get(FacilioConstants.ContextNames.PER_PAGE);
        int page = (int) context.get(FacilioConstants.ContextNames.PAGE);
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        FacilioModule module = moduleBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

        List<FacilioField> serviceAppointmentFields = moduleBean.getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        Map<String, FacilioField> serviceAppointmentFieldsMap = FieldFactory.getAsMap(serviceAppointmentFields);

        List<LookupField>lookUpfields = new ArrayList<>();
        lookUpfields.add((LookupField) serviceAppointmentFieldsMap.get("location"));
        lookUpfields.add((LookupField) serviceAppointmentFieldsMap.get("site"));
        lookUpfields.add((LookupField) serviceAppointmentFieldsMap.get("priority"));

        ServiceAppointmentTicketStatusContext scheduledStatus = ServiceAppointmentUtil.getStatus(FacilioConstants.ServiceAppointment.SCHEDULED);
        Criteria serverCriteria = new Criteria();
        if(scheduledStatus != null) {
            serverCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(scheduledStatus.getId()), NumberOperators.EQUALS));
        } else {
            throw new FSMException(FSMErrorCode.UNKNOWN_ERROR);
        }

        if(criteriaId != null && criteriaId>0) {
            Criteria viewCriteria = CriteriaAPI.getCriteria(orgId, criteriaId);

            if (viewCriteria != null) {
                serverCriteria.andCriteria(viewCriteria);
            }
        }
        if(boardId != null && boardId > 0) {
            DispatcherSettingsContext board = DispatcherUtil.getDispatcher(boardId);
            if (board != null) {

                String workorderConfig = board.getWorkorderConfigJson();
                JSONObject workorderConfigJson = FacilioUtil.parseJson(workorderConfig);
                if (workorderConfigJson != null) {
                    if (workorderConfigJson.get("sortConfig") != null) {
                        JSONObject sortConfig = (JSONObject) workorderConfigJson.get("sortConfig");
                        if (sortConfig.get("sortBy") != null) {
                            sortBy = (String) sortConfig.get("sortBy");
                        }
                        if (sortConfig.get("sortOrder") != null) {
                            sortOrder = (String) sortConfig.get("sortOrder");
                        }
                    }
                    if (workorderConfigJson.get("fields") != null) {
                        List<JSONObject> fields = (List<JSONObject>) workorderConfigJson.get("fields");
                        if (CollectionUtils.isNotEmpty(fields)) {
                            for (JSONObject field : fields) {
                                Long fieldId = (Long) field.get("fieldId");
                                if (fieldId != null && fieldId > 0) {
                                    FacilioField selectField = moduleBean.getField(fieldId);
                                    if (!defaultFieldNames.contains(selectField.getName())) {
                                        selectFields.add(selectField);
                                        if (isLookupField(selectField)) {
                                            lookUpfields.add((LookupField) selectField);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

            List<ServiceAppointmentContext> serviceAppointments = new ArrayList<>();
            if (CollectionUtils.isEmpty(selectFields)) {
                selectFields = serviceAppointmentFields;
            }

            if (sortBy != null && !sortBy.isEmpty()) {
                sortBy = moduleBean.getField(sortBy, FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT).getCompleteColumnName()+ " " + sortOrder;
            } else {
                sortBy = FieldFactory.getIdField(module).getCompleteColumnName() + " " + sortOrder;
            }
            if(orderBy != null && !orderBy.isEmpty() && orderType != null && !orderType.isEmpty()){
                sortBy = moduleBean.getField(orderBy, FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT).getCompleteColumnName() + " " + orderType;
            }

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);

        List<ServiceAppointmentContext> serviceAppointmentList = ServiceAppointmentUtil.getServiceAppointmentsList(module,selectFields,lookUpfields,serverCriteria,sortBy,filterCriteria,searchCriteria,perPage,offset);

        if (CollectionUtils.isNotEmpty(serviceAppointmentList)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointmentList) {

                    serviceAppointments.add(serviceAppointment);
            }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("serviceAppointment", serviceAppointments);
            jsonObject.put("count", ServiceAppointmentUtil.getServiceAppointmentsCount(module,serverCriteria,sortBy,filterCriteria,searchCriteria));

            context.put(FacilioConstants.ContextNames.DATA, jsonObject);

        return false;
    }
    private static boolean isLookupField(FacilioField field){
        return field.getDataTypeEnum() == FieldType.LOOKUP;
    }

}
