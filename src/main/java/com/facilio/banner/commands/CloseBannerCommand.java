package com.facilio.banner.commands;

import com.facilio.banner.context.BannerContext;
import com.facilio.banner.util.BannerUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CloseBannerCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        BannerContext banner = BannerUtil.getBanner(id);
        if (banner == null) {
            return false;
        }

        if (banner.getTypeEnum() == BannerContext.Type.COLLAPSE) {
            // cannot cancel collapsable banners
            return false;
        }

        FacilioModule module = ModuleFactory.getBannerModule();
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(FieldFactory.getField("cancelled", "CANCELLED", module, FieldType.BOOLEAN)))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        Map<String, Object> map = new HashMap<>();
        map.put("cancelled", true);
        builder.update(map);
        return false;
    }
}
