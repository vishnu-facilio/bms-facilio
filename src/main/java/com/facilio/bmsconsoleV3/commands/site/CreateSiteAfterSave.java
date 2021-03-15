package com.facilio.bmsconsoleV3.commands.site;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSiteAfterSave extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("resource");

        for (ModuleBaseWithCustomFields record: records) {
            var siteContext = (V3SiteContext) record;

            Map<String, Object> prop = new HashMap<>();
            prop.put("siteId", siteContext.getId());

            new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(Arrays.asList(FieldFactory.getSiteIdField(module)))
                    .andCondition(CriteriaAPI.getIdCondition(siteContext.getId(), module))
                    .update(prop);
            // FIXME make this bulk
            Constants.setRecordId(context, siteContext.getId());
            context.put(FacilioConstants.ContextNames.PARENT_ID, siteContext.getId());

            SpaceAPI.updateHelperFields(siteContext);
        }
        return false;
    }
}
