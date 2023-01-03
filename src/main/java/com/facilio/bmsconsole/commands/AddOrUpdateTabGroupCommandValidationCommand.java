package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AddOrUpdateTabGroupCommandValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WebTabGroupContext tabGroup = (WebTabGroupContext) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP);

        FacilioUtil.throwIllegalArgumentException(tabGroup==null,"Invalid tabGroup payLoad data");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(tabGroup.getName()),"Name connot be empty");
        FacilioUtil.throwIllegalArgumentException((tabGroup.getIconType()==-1&&tabGroup.getIconTypeEnum()==null),"IconType or IconTypeEnum either one should cannot be empty");
        if (checkRouteAlreadyFound(tabGroup)) {
            throw new IllegalArgumentException("Route is already found for this layout");
        }
        return false;
    }
    private boolean checkRouteAlreadyFound(WebTabGroupContext tabGroup) throws Exception {
        if (StringUtils.isEmpty(tabGroup.getRoute())) {
            throw new IllegalArgumentException("Route cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", tabGroup.getRoute(), StringOperators.IS));

        if (tabGroup.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(tabGroup.getId()), NumberOperators.NOT_EQUALS));
        }

        if (tabGroup.getLayoutId() <= 0) {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", "", CommonOperators.IS_EMPTY));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(tabGroup.getLayoutId()), NumberOperators.EQUALS));
        }
        Map<String, Object> map = builder.fetchFirst();
        return map != null;
    }
}
