package com.facilio.metamigration.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.metamigration.util.MetaMigrationAPI;
import org.apache.struts2.ServletActionContext;
import lombok.extern.log4j.Log4j;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Log4j
public class MetaMigrationAction extends FacilioAction {
    private long sourceOrgId;
    private long targetOrgId;

    public String startMetaMigration() throws Exception {

        AccountUtil.setCurrentAccount(targetOrgId);

        MetaMigrationAPI.migrateMetaData(sourceOrgId, targetOrgId);

        setResult("result", "success");
        ServletActionContext.getResponse().setStatus(200);

        return SUCCESS;
    }
}
