package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ReportFolderTranslationImplV2;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetReportFolderTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.REPORT.equals(WebTabContext.Type.valueOf(context.getType())),"Invalid webTab Type for fetching Report Fields");

        JSONArray fields = new JSONArray();
        ModuleBean moduleBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = context.getModuleIds();

        if(CollectionUtils.isNotEmpty(moduleIds)) {
            for (long moduleId : moduleIds) {
                FacilioModule module = moduleBean.getModule(moduleId);
                List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(module.getName(),true,null,false, null);
                if(CollectionUtils.isNotEmpty(reportFolders)) {
                    for (ReportFolderContext reportFolder : reportFolders) {
                        String reportFolderId = String.valueOf(reportFolder.getId());
                        String reportFolderKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT_FOLDER,reportFolderId);
                        fields.add(TranslationsUtil.constructJSON(reportFolder.getName(),ReportFolderTranslationImplV2.REPORT_FOLDER,TranslationConstants.DISPLAY_NAME,reportFolderId,reportFolderKey,properties));
                    }
                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",fields);

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
