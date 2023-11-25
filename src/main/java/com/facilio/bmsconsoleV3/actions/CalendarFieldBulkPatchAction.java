package com.facilio.bmsconsoleV3.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class CalendarFieldBulkPatchAction extends V3Action {
    private String moduleName;
    private String parentModuleName;
    private List<V3AssetContext> asset;
    private List<V3SiteContext> site;
    public String bulkUpdateCalendarField() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField calendarField= modBean.getField("calendar",moduleName);
        List<ModuleBaseWithCustomFields> recordList = new ArrayList<>();
        if(moduleName.equalsIgnoreCase(FacilioConstants.ContextNames.ASSET)){
            recordList.addAll(asset);
        }else{
            recordList.addAll(site);
        }
        if(CollectionUtils.isNotEmpty(recordList)){
            V3RecordAPI.batchUpdateRecordsWithHandlingLookup(moduleName , recordList ,  Collections.singletonList(calendarField));
        }
        return SUCCESS;
    }

}
