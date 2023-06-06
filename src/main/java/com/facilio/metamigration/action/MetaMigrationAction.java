package com.facilio.metamigration.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.metamigration.util.MetaMigrationAPI;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import lombok.extern.log4j.Log4j;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Log4j
public class MetaMigrationAction extends FacilioAction {
    private long sourceOrgId;
    private long targetOrgId;
    private String components;

    public String startMetaMigration() throws Exception {

        AccountUtil.setCurrentAccount(targetOrgId);

        if (StringUtils.isNotEmpty(components)) {
            List<Integer> componentsToMigrate = Arrays.stream(components.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            MetaMigrationAPI.migrateMetaData(sourceOrgId, targetOrgId, componentsToMigrate);
        } else {
            throw new RuntimeException("Select atleast one component type");
        }

        setResult("result", "success");
        ServletActionContext.getResponse().setStatus(200);

        return SUCCESS;
    }
}
