package com.facilio.v3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

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
            List<SupplementRecord> supplements = new ArrayList<SupplementRecord>();
            
            List<Map<String, Object>> props = FieldUtil.getAsMapList(records, ModuleBaseWithCustomFields.class);
            for(FacilioField field : fields){
                  if(field.getDataTypeEnum() == FieldType.MULTI_LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_ENUM) {
                      for(Map<String, Object> recMap : props) {
                          if(MapUtils.isNotEmpty(recMap)) {
                              if(recMap.containsKey(field.getName())){
                                  supplements.add((SupplementRecord) field);
                                  break;
                              }
                          }
                      }
                  }
              }
            if(CollectionUtils.isNotEmpty(supplements)){
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,supplements);
            }

        }

        return false;
    }
}
