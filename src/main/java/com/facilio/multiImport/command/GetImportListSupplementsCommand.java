package com.facilio.multiImport.command;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.multiImport.context.ImportDataDetails;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetImportListSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ImportDataDetails> importList = (List<ImportDataDetails>) context.get(FacilioConstants.ContextNames.IMPORT_LIST);
        Map<String, Map<Long, Object>> supplements = new HashMap<>();

        if (CollectionUtils.isEmpty(importList)) {
            return false;
        }
        Set<Long> userIds = new HashSet<>();
        for (ImportDataDetails importDataDetails : importList) {
            if (importDataDetails.getCreatedBy() != 0) {
                userIds.add(importDataDetails.getCreatedBy());
            }
            if (importDataDetails.getModifiedBy() != 0) {
                userIds.add(importDataDetails.getModifiedBy());
            }
        }

        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        Map<Long, User> usersMap = userBean.getUsersAsMap(null, userIds);

        supplements.put("sysCreatedBy", new HashMap<>());
        supplements.put("sysModifiedBy", new HashMap<>());

        for (ImportDataDetails importDataDetails : importList) {
            long createdBy = importDataDetails.getCreatedBy();
            long modifiedBy = importDataDetails.getModifiedBy();
            supplements.get("sysCreatedBy").put(createdBy, usersMap.get(createdBy));
            supplements.get("sysModifiedBy").put(modifiedBy, usersMap.get(modifiedBy));
        }
        context.put(FacilioConstants.ContextNames.SUPPLEMENTS, supplements);
        return false;
    }
}
