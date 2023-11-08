package com.facilio.ns.command;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.connected.IConnectedRule;
import com.facilio.connected.ResourceType;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;

public class AddReadingCategoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Object> list = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(list)) {
            for (Object connectedObj : list) {
                Boolean addCategoryChain = Boolean.TRUE;
                if (connectedObj instanceof ReadingKPIContext) {
                    ReadingKPIContext kpiContext = (ReadingKPIContext) connectedObj;
                    addCategoryChain = BooleanUtils.isNotTrue(kpiContext.getKpiTypeEnum().equals(KPIType.DYNAMIC));
                }
                addReadingCategoryChain(addCategoryChain, (IConnectedRule) connectedObj, context);
            }
        }
        return false;
    }

    private void addReadingCategoryChain(Boolean addCategoryChain,IConnectedRule iConnectedRule, Context context) throws Exception {
        if (addCategoryChain && BooleanUtils.isNotTrue(PackageUtil.isInstallThread())) {
            FacilioChain addReadingChain = TransactionChainFactory.getAddFDDReadingModuleChain(iConnectedRule.getCategory().getResType().equals(ResourceType.METER_CATEGORY));
            addReadingChain.execute(context);
        }
    }

}
