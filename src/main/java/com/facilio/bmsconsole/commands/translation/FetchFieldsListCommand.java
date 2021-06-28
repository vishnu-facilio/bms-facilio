package com.facilio.bmsconsole.commands.translation;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.lang.i18n.translation.TranslationConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

@Log4j
public class FetchFieldsListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand ( Context context ) throws Exception {
        Map<String, List<FacilioModule>> modules = (Map<String, List<FacilioModule>>)context.get(FacilioConstants.ContextNames.MODULE_LIST);
        String langCode = (String)context.get(TranslationConstants.LANG_CODE);
        JSONArray array = new JSONArray();
        JSONObject metaData = new JSONObject();
        metaData.put("systemModules",constructMetaData(modules.get("systemModules"),langCode));
        metaData.put("customModules",constructMetaData(modules.get("customModules"),langCode));
        array.add(metaData);
        context.put("translationList",array);
        return false;
    }

    private JSONArray constructMetaData ( List<FacilioModule> modules,String langCode ) throws Exception {
        JSONArray array = new JSONArray();
        if(CollectionUtils.isEmpty(modules)) {
            return array;
        }
        ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        long fileId = TranslationUtils.getFileId(langCode);
        FileStore fs = FacilioFactory.getFileStore();
        String filePath = fs.getFileInfo(fileId).getFilePath();
        modules.forEach(module -> {
            try {
                List<FacilioField> fields = bean.getModuleFields(module.getName());
                array.add(TranslationUtils.constructJSONObject(fields,module,filePath));
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred while adding fields in Translation",e);
            }
        });
        return array;
    }


}
