package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SettingsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

@Getter
@Setter
public class SFG20JobPlanAction extends V3Action {

    private SFG20SettingsContext setting;

    private long syncId;
    private long flowType;

    public String syncSFG20Data() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getSFG20SyncCommand();

        FacilioContext context = chain.getContext();

        chain.execute();
        setData("syncData", context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY));

        return SUCCESS;
    }
    public String connectSFG20() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getSFG20ConnectTestCommand();

        FacilioContext context = chain.getContext();

        chain.execute();

        setData("accessToken", context.get("accessToken"));
        setData("schedulesList", context.get("schedulesList"));
//        setData("schedulesDetailsList", context.get("schedulesDetailsList"));

        return SUCCESS;
    }
    public String addOrUpdateSFG20Setting() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getSFG20SettingAddOrUpdateCommand();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SFG20.SETTING,setting);

        chain.execute();

        setData(FacilioConstants.ContextNames.SFG20.SETTING, context.get(FacilioConstants.ContextNames.SFG20.SETTING));
        return SUCCESS;
    }
    public String getSFG20Setting() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.GetSFG20SettingsChain();

        FacilioContext context = chain.getContext();
        chain.execute();

        setData(FacilioConstants.ContextNames.SFG20.SETTING, context.get(FacilioConstants.ContextNames.SFG20.SETTING));
        return SUCCESS;
    }

    public String getSFG20SyncHistoryList() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.GetSFG20SyncHistoryListChain();

        FacilioContext context = chain.getContext();
        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());
        pagination.put("withCount",this.getWithCount());
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        if (getFilters() != null) {
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, ModuleFactory.getSFG20SyncHistoryModule().getName());
        chain.execute();

        setData(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY, context.get(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY));
        return SUCCESS;
    }

    public String getSFG20SyncHistoryDetails() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.GetSFG20SyncHistoryDetailsChain();

        FacilioContext context = chain.getContext();
        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());
        pagination.put("withCount",this.getWithCount());
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put("sfgSyncId",syncId);
        context.put("flowType",flowType);
        chain.execute();
        setData(FacilioConstants.ContextNames.SFG20.SCHEDULES_DETAILS_LIST, context.get(FacilioConstants.ContextNames.SFG20.SCHEDULES_DETAILS_LIST));
        return SUCCESS;
    }

    public String getSFG20SyncHistoryDetailsListCount() throws Exception {

        FacilioChain chain = ReadOnlyChainFactoryV3.GetSFG20SyncScheduleDetailsListCountChain();

        FacilioContext context = chain.getContext();
        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());
        pagination.put("withCount",this.getWithCount());
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put("sfgSyncId",syncId);
        context.put("flowType",flowType);
        chain.execute();
        setData(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        return SUCCESS;
    }

}
