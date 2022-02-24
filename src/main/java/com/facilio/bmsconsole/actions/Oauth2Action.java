package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.APIClient;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class Oauth2Action extends FacilioAction {

    private long id;
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private APIClient apiClient;
    public APIClient getApiClient() {
        return this.apiClient;
    }

    public void setApiClient(APIClient apiClient) {
        this.apiClient = apiClient;
    }

    public String enableOauth2() throws Exception {
        FacilioChain enableOauth2Chain = FacilioChainFactory.getEnableOauth2Chain();
        FacilioContext context = new FacilioContext();
        enableOauth2Chain.execute(context);
        return SUCCESS;
    }

    public String createAPIClient() throws Exception {
        FacilioChain createAPIClient = FacilioChainFactory.getCreateAPIClient();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE, apiClient);
        createAPIClient.execute(context);
        String token = (String) context.get(FacilioConstants.ContextNames.TOKEN);
        String client = (String) context.get(FacilioConstants.ContextNames.CLIENT);
        setResult("token", token);
        setResult("clientId", client);
        return SUCCESS;
    }

    public String listApiClients() throws Exception {
        FacilioChain listApiClients = FacilioChainFactory.getListAPIClient();
        FacilioContext context = new FacilioContext();
        listApiClients.execute(context);
        setResult("apiClients", context.get(FacilioConstants.ContextNames.RESULT));
        return SUCCESS;
    }

    public String deleteClient() throws Exception {
        FacilioChain deleteClient = FacilioChainFactory.getDeleteClient();
        FacilioContext context = new FacilioContext();
        context.put("id", getId());
        deleteClient.execute(context);
        setResult("message", "success");
        return SUCCESS;
    }
}
