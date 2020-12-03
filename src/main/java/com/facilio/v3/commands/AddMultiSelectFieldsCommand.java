package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddMultiSelectFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fields = modBean.getAllFields(moduleName);
        List<ModuleBaseWithCustomFields> records = recordMap.get(module.getName());

        if(CollectionUtils.isNotEmpty(fields) && CollectionUtils.isNotEmpty(records)){
            List<MultiLookupField> multiLookups = new ArrayList<MultiLookupField>();

            for(FacilioField field : fields){
                  if(field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                      for(ModuleBaseWithCustomFields rec : records) {
                          Map<String, Object> recMap = FieldUtil.getAsProperties(rec);
                          if(MapUtils.isNotEmpty(recMap)) {
                              if(recMap.containsKey(field.getName())){
                                  multiLookups.add((MultiLookupField) field);
                                  break;
                              }
                          }
                      }
                  }
              }
            if(CollectionUtils.isNotEmpty(multiLookups)){
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,multiLookups);
            }

        }

        return false;
    }
}
