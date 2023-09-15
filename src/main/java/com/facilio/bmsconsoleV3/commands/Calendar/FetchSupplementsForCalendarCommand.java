package com.facilio.bmsconsoleV3.commands.Calendar;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchSupplementsForCalendarCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> calendarModuleFields = modBean.getAllFields(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(calendarModuleFields);

        List<LookupField> additionaLookups = new ArrayList<>();
        additionaLookups.add((LookupField) fieldsAsMap.get("client"));
        additionaLookups.add((LookupField) fieldsAsMap.get("sysCreatedByPeople"));
        additionaLookups.add((LookupField) fieldsAsMap.get("sysModifiedByPeople"));
        if(CollectionUtils.isNotEmpty(additionaLookups)) {
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, additionaLookups);
        }
        return false;
    }
}
