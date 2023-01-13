package com.facilio.connectedapp.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppDeploymentContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class GetConnectedAppDeploymentListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long connectedAppId = (Long) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP_ID);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getConnectedAppDeploymentsModule().getTableName())
                .select(FieldFactory.getConnectedAppDeploymentsFields());

        builder.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", connectedAppId+"", NumberOperators.EQUALS));

        builder.orderBy("VERSION_NUMBER desc");

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        builder.limit(50);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            builder.limit(perPage).offset(offset);
        }

        List<ConnectedAppDeploymentContext> deploymentContexts = FieldUtil.getAsBeanListFromMapList(builder.get(), ConnectedAppDeploymentContext.class);
        if (deploymentContexts != null && !deploymentContexts.isEmpty()) {
            for (ConnectedAppDeploymentContext deploymentContext : deploymentContexts) {
                deploymentContext.setSysCreatedByUser(AccountUtil.getUserBean(AccountUtil.getCurrentOrg().getId()).getUser(deploymentContext.getSysCreatedBy(), true));
                deploymentContext.setSysModifiedByUser(AccountUtil.getUserBean(AccountUtil.getCurrentOrg().getId()).getUser(deploymentContext.getSysModifiedBy(), true));
            }
        }
        context.put(ConnectedAppHostingAPI.Constants.CONNECTED_APP_DEPLOYMENT_LIST, deploymentContexts);

        return false;
    }
}
