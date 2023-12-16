package com.facilio.remotemonitoring.action;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.util.FieldsConfigChainUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.ConfigParams;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FlaggedAlarmProcessAction extends V3Action {
    private Long flaggedAlarmProcessId;
    private String ticketModuleName;
    private String fieldName;
    private String directLookupFieldName;
    private String directLookupModuleName;
    private String secondLevelLookupFieldName;

    private String _default;
    public String getDefault() {
        return _default;
    }
    public void setDefault(String _default) {
        this._default = _default;
    }

    public String getTicketModuleForCreate() throws Exception {
        FacilioModule ticketModule = RemoteMonitorUtils.getTicketModuleForFlaggedAlarmProcessCreate();
        FacilioModule ticketModuleWithRequiredFields = new FacilioModule();
        ticketModuleWithRequiredFields.setModuleId(ticketModule.getModuleId());
        ticketModuleWithRequiredFields.setName(ticketModule.getName());
        setData(RemoteMonitorConstants.TICKET_MODULE, ticketModuleWithRequiredFields);
        return SUCCESS;
    }

    public String getTicketModuleFields() throws Exception {
        FacilioContext fieldsContext = FieldsConfigChainUtil.fetchFieldList(ticketModuleName, -1L, FieldListType.ADVANCED_FILTER_FIELDS, new ArrayList<>());
        setData(FacilioConstants.ContextNames.FIELDS, fieldsContext.get(FacilioConstants.ContextNames.FIELDS));
        return SUCCESS;
    }

    public String getFlaggedAlarmStatusOptions() throws Exception {
        List<Map<String, String>> statusOptions = new ArrayList<>();
        for (FlaggedEventContext.FlaggedEventStatus status : FlaggedEventContext.FlaggedEventStatus.values()) {
            Map<String, String> statusOption = new HashMap<>();
            statusOption.put("label", status.getValue());
            statusOption.put("value", status.getIndex());
            statusOptions.add(statusOption);
        }
        setData(RemoteMonitorConstants.FLAGGED_ALARM_STATUSES, statusOptions);
        return SUCCESS;
    }

    public String getClosureRestrictionOptions() throws Exception {
        List<Map<String, String>> closureRestrictionOptions = new ArrayList<>();
        for (FlaggedEventRuleClosureConfigContext.ClosureRestriction closureRestriction : FlaggedEventRuleClosureConfigContext.ClosureRestriction.values()) {
            Map<String, String> closureRestrictionOption = new HashMap<>();
            closureRestrictionOption.put("label", closureRestriction.getValue());
            closureRestrictionOption.put("value", closureRestriction.getIndex());
            closureRestrictionOptions.add(closureRestrictionOption);
        }
        setData(RemoteMonitorConstants.CLOSURE_RESTRICTION_OPTIONS, closureRestrictionOptions);
        return SUCCESS;
    }

    public String getTicketModuleStatusOptions() throws Exception {
        FacilioContext context = getSubModulePickList(AddFlaggedEventClosureConfigModule.MODULE_NAME);
        setData(FacilioConstants.ContextNames.PICKLIST, context.get(FacilioConstants.ContextNames.PICKLIST));
        return SUCCESS;
    }

    public String getAlarmTypeOptions() throws Exception {
        FacilioContext context = getSubModulePickList(FlaggedEventAlarmTypeRelModule.MODULE_NAME);
        setData(FacilioConstants.ContextNames.PICKLIST, context.get(FacilioConstants.ContextNames.PICKLIST));
        return SUCCESS;
    }

    public String getEvaluationTeamsOptionList() throws Exception {
        FacilioContext context = getSubModulePickList(FlaggedEventBureauEvaluationModule.MODULE_NAME);
        setData(FacilioConstants.ContextNames.PICKLIST, context.get(FacilioConstants.ContextNames.PICKLIST));
        return SUCCESS;
    }

    private FacilioContext getSubModulePickList(String subModuleName) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getFormsPickListChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, fieldName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, subModuleName);

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, this.getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS, this.getFilters());
        context.put(FacilioConstants.ContextNames.DEFAULT,this.getDefault());
        context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER, this.getExcludeParentFilter());
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, this.getClientCriteria());
        context.put(FacilioConstants.ContextNames.ORDER_BY, this.getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, this.getOrderType());
        context.put(FacilioConstants.ContextNames.WITH_COUNT, this.getWithCount());
        chain.execute(context);

        return context;
    }

    public String getPickListForSecondLevelLookup() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        LookupField directLookupField = (LookupField) modBean.getField(directLookupFieldName, FlaggedEventModule.MODULE_NAME);
        FacilioUtil.throwIllegalArgumentException(directLookupField == null, "Ticket module field should be as lookup in source module");

        FacilioChain chain = ReadOnlyChainFactory.getFormsPickListChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, secondLevelLookupFieldName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, directLookupModuleName);

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, this.getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS, this.getFilters());
        context.put(FacilioConstants.ContextNames.DEFAULT,this.getDefault());
        context.put(FacilioConstants.ContextNames.EXCLUDE_PARENT_FILTER, this.getExcludeParentFilter());
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, this.getClientCriteria());
        context.put(FacilioConstants.ContextNames.ORDER_BY, this.getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, this.getOrderType());
        context.put(FacilioConstants.ContextNames.WITH_COUNT, this.getWithCount());
        chain.execute(context);

        setData(FacilioConstants.ContextNames.PICKLIST, context.get(FacilioConstants.ContextNames.PICKLIST));
        return SUCCESS;
    }
}
