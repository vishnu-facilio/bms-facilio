package com.facilio.bmsconsoleV3.commands.vendor;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoadVendorsExtraFieldsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);

        if(fetchOnlyViewGroupColumn && view!=null){
            List<FacilioField> extraReceivableFields = new ArrayList<>();

            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);

            extraReceivableFields.add((allFieldsAsMap.get("vendorLogoId")));
            extraReceivableFields = extraReceivableFields.stream().filter(Objects::nonNull).collect(Collectors.toList());

            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS,extraReceivableFields);

        }

        return false;
    }
}