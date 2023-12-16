package com.facilio.remotemonitoring.handlers.ticketmodulecreate;

import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.context.FlaggedEventWorkorderFieldMappingContext;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderRecordCreationHandler implements TicketModuleRecordCreationHandler {
    @Override
    public Map<String, Object> consructRecordPropsFromFieldMapping(FlaggedEventRuleContext flaggedEventRule, FlaggedEventContext flaggedEvent) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, Object> workOrderProp = new HashMap<>();
        if (flaggedEventRule != null && flaggedEvent != null) {
            List<FlaggedEventWorkorderFieldMappingContext> fieldMapping = flaggedEventRule.getFieldMapping();
            Map<String, Object> flaggedEventProp = FieldUtil.getAsProperties(flaggedEvent);
            if (CollectionUtils.isNotEmpty(fieldMapping)) {
                for (FlaggedEventWorkorderFieldMappingContext mapping : fieldMapping) {
                    FacilioField woField = modBean.getField(mapping.getLeftFieldId(), FacilioConstants.ContextNames.WORK_ORDER);
                    if (
                            woField.getDataTypeEnum() == FieldType.STRING ||
                                    woField.getDataTypeEnum() == FieldType.NUMBER ||
                                    woField.getDataTypeEnum() == FieldType.BIG_STRING ||
                                    woField.getDataTypeEnum() == FieldType.DECIMAL ||
                                    woField.getDataTypeEnum() == FieldType.LARGE_TEXT ||
                                    woField.getDataTypeEnum() == FieldType.BOOLEAN) {
                        String replacedString = WorkflowRuleAPI.replacePlaceholders(FlaggedEventModule.MODULE_NAME, flaggedEvent, mapping.getValueText());
                        workOrderProp.put(woField.getName(), replacedString);
                    } else {
                        if (woField.getDataTypeEnum() == FieldType.LOOKUP) {
                            if (mapping.getValueText() != null) {
                                Map<String, Object> insertProp = new HashMap<>();
                                insertProp.put(RemoteMonitorConstants.ID, Long.parseLong(mapping.getValueText()));
                                workOrderProp.put(woField.getName(), insertProp);
                            } else if (mapping.getRightFieldId() != null) {
                                FacilioField flaggedEventField = modBean.getField(mapping.getRightFieldId(), FlaggedEventModule.MODULE_NAME);
                                if (flaggedEventField != null) {
                                    Map<String, Object> prop = (Map<String, Object>) flaggedEventProp.get(flaggedEventField.getName());
                                    if (MapUtils.isNotEmpty(prop) && prop.containsKey(RemoteMonitorConstants.ID)) {
                                        Map<String, Object> insertProp = new HashMap<>();
                                        String valueFromFlaggedAlarm = (String) prop.get(RemoteMonitorConstants.ID);
                                        if(StringUtils.isNotEmpty(valueFromFlaggedAlarm)){
                                            insertProp.put(RemoteMonitorConstants.ID, Long.parseLong(valueFromFlaggedAlarm));
                                            workOrderProp.put(woField.getName(), insertProp);
                                        }
                                    }
                                }
                            }
                        } else if (StringUtils.isNotEmpty(mapping.getValueText())) {
                            Long value = Long.parseLong(mapping.getValueText());
                            workOrderProp.put(woField.getName(), value);
                        }
                    }
                }
            }
            Controller controller = V3RecordAPI.getRecord(FacilioConstants.ContextNames.CONTROLLER, flaggedEvent.getController().getId());
            if (controller != null) {
                workOrderProp.put(FacilioConstants.ContextNames.SITE_ID, controller.getSiteId());
                ResourceContext resource = new ResourceContext();
                resource.setId(controller.getSiteId());
                workOrderProp.put(FacilioConstants.ContextNames.RESOURCE, FieldUtil.getAsProperties(resource));
            }
            if (!workOrderProp.containsKey(RemoteMonitorConstants.SUBJECT)) {
                workOrderProp.put(RemoteMonitorConstants.SUBJECT, flaggedEvent.getName());
            }

            //compute site id from resource lookup field value
            if (!workOrderProp.containsKey(FacilioConstants.ContextNames.SITE_ID) && workOrderProp.containsKey(FacilioConstants.ContextNames.RESOURCE)) {
                Map<String, Object> resourceProp = (Map<String, Object>) workOrderProp.get(FacilioConstants.ContextNames.RESOURCE);
                if (MapUtils.isNotEmpty(resourceProp) && resourceProp.containsKey(RemoteMonitorConstants.ID)) {
                    V3ResourceContext resource = V3RecordAPI.getRecord(FacilioConstants.ContextNames.RESOURCE, (Long) resourceProp.get("id"), V3ResourceContext.class);
                    if (resource != null && resource.getSiteId() > -1) {
                        workOrderProp.put(FacilioConstants.ContextNames.SITE_ID, resource.getSiteId());
                    }
                }
            }

            workOrderProp.put(RemoteMonitorConstants.FlaggedEvent.FLAGGED_EVENT, ImmutableMap.of(RemoteMonitorConstants.ID, flaggedEvent.getId()));

        }
//        if (flaggedEventRule.getWorkorderTemplateId() != null) {
//            workOrderProp.put(FacilioConstants.ContextNames.FORM_ID, flaggedEventRule.getWorkorderTemplateId());
//        }
        return workOrderProp;
    }

    public void updateInitialRecordStatus(Map<String, Object> recordAsProp, ModuleBaseWithCustomFields record) throws Exception {
        if (recordAsProp.containsKey(FacilioConstants.ContextNames.MODULE_STATE)) {
            Map<String, Object> statusMap = (Map<String, Object>) recordAsProp.get(FacilioConstants.ContextNames.MODULE_STATE);
            if (MapUtils.isNotEmpty(statusMap)) {
                Long statusId = (Long) statusMap.get(RemoteMonitorConstants.ID);
                if (statusId != null) {
                    FacilioStatus status = TicketAPI.getStatus(Long.valueOf(statusId));
                    if (status != null) {
                        FlaggedEventUtil.updateWorkorderStatus(record, status);
                    }
                }
            }
        }
    }

    public void sendClosureCommandToTicketModuleRecord(FlaggedEventRuleContext flaggedEventRule, FlaggedEventContext flaggedEvent) throws Exception {
        if (flaggedEvent.getWorkorder() != null) {
            V3WorkOrderContext workOrder = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WORK_ORDER, flaggedEvent.getWorkorder().getId(), V3WorkOrderContext.class);
            if (workOrder != null && workOrder.getStatus() != null) {
                if (flaggedEventRule != null && flaggedEventRule.getFlaggedEventRuleClosureConfig() != null && flaggedEventRule.getFlaggedEventRuleClosureConfig().getWorkorderCloseStatus() != null) {
                    List<FacilioStatus> workOrderCloseCommandStatuses = flaggedEventRule.getFlaggedEventRuleClosureConfig().getWorkorderCloseCommandCriteria();
                    if (CollectionUtils.isNotEmpty(workOrderCloseCommandStatuses)) {
                        for (FacilioStatus criteriaStatus : workOrderCloseCommandStatuses) {
                            if (criteriaStatus != null && criteriaStatus.getId() == workOrder.getStatus().getId()) {
                                FlaggedEventUtil.updateWorkorderStatus(workOrder, flaggedEventRule.getFlaggedEventRuleClosureConfig().getWorkorderCloseStatus());
                            }
                        }
                    }
                }
            }
        }
    }
}
