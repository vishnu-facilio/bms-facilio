package com.facilio.bmsconsoleV3.commands.client;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisassociateClientFromSiteCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map < String, Object > bodyParams = Constants.getBodyParams(context);
        if(bodyParams != null){
            Boolean isDisassociateSite = (Boolean) bodyParams.get("isDisassociateSites");
            if(isDisassociateSite != null && isDisassociateSite ) {
                Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
                String moduleName = Constants.getModuleName(context);
                if (moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) {
                    List records = recordMap.get(moduleName);
                    if(records != null && !records.isEmpty()){
                        V3ClientContext client = (V3ClientContext) records.get(0);
                        List<Long> siteIds = client.getSiteIds();
                        if (siteIds != null && !siteIds.isEmpty()) {
                            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
                            FacilioField clientField = modBean.getField("client", module.getName());
                            List<FacilioField> fields = new ArrayList<FacilioField>();
                            fields.add(clientField);
                            for (Long siteId : siteIds) {
                                V3SiteContext site = new V3SiteContext();
                                site.setId(siteId);
                                site.setClient(null);
                                V3RecordAPI.updateRecord(site, module, fields);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
