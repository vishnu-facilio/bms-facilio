package com.facilio.bmsconsoleV3.commands.storeroom;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SetLocationObjectFromSiteV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List <V3StoreRoomContext> storeroomRecords= recordMap.get(moduleName);
        V3StoreRoomContext storeroomRec = storeroomRecords.get(0);

        if(StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(storeroomRecords) && storeroomRec.getSite()!=null) {

            Long siteId = storeroomRec.getSite().getId();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
            List<FacilioField> siteFields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);

            SelectRecordsBuilder<SiteContext> selectBuilder = new SelectRecordsBuilder<SiteContext>()
                    .select(siteFields).table(siteModule.getTableName()).moduleName(siteModule.getName())
                    .beanClass(SiteContext.class)
                    .andCondition(CriteriaAPI.getIdCondition(siteId,siteModule));

            List<SiteContext> site = selectBuilder.get();

            storeroomRec.setLocation(site.get(0).getLocation());
        }
        return false;
    }

}
