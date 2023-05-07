package com.facilio.permission.handlers.group;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.factory.PageFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.permission.context.module.RelatedListPermissionSet;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class RelatedRecordsPermissionSetHandler implements PermissionSetGroupHandler<RelatedListPermissionSet> {
    @Override
    public List<RelatedListPermissionSet> getPermissions(Long groupId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(groupId);
        List<RelatedListPermissionSet> relatedListPermissionSets = new ArrayList<>();
        if(!PageFactory.SKIP_RELATED_LIST_MOD.contains(module.getName())) {

            List<FacilioModule> subModules =
                    modBean.getSubModules(module.getModuleId(), FacilioModule.ModuleType.BASE_ENTITY,
                            FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                            FacilioModule.ModuleType.Q_AND_A
                    );

            List<String> moduleList = new ArrayList<>();

            moduleList.add(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
            moduleList.add(FacilioConstants.MultiResource.NAME);
            moduleList.add(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL);
            if (CollectionUtils.isNotEmpty(subModules)) {
                subModules = subModules.stream().filter(mod -> !moduleList.contains(mod.getName())).collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(subModules)) {
                for (FacilioModule subModule : subModules) {
                    if (subModule.isModuleHidden()) {
                        continue;
                    }
                    List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
                    List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == module.getModuleId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(fields)) {
                        for (FacilioField field : fields) {
                            RelatedListPermissionSet item = new RelatedListPermissionSet();
                            String relatedListDisplayName = ((LookupField) field).getRelatedListDisplayName();
                            if(StringUtils.isNotEmpty(relatedListDisplayName)){
                                item.setDisplayName(relatedListDisplayName);
                            }
                            else {
                                item.setDisplayName(subModule.getDisplayName());
                            }
                            item.setModuleId(module.getModuleId());
                            item.setRelatedModuleId(subModule.getModuleId());
                            item.setRelatedFieldId(field.getFieldId());
                            relatedListPermissionSets.add(item);
                        }
                    }
                }
            }
        }
        Page page = new Page();
        relatedListPermissionSets.addAll(PageFactory.addOtherRelatedList(module,page,page.new Tab("Related"), page.new Section(),null,true));
        return relatedListPermissionSets;
    }

    @Override
    public Map<String, Long> paramsResolver(Map<String, String> httpParametersMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String,Long> prop = new HashMap<>();
        if(MapUtils.isNotEmpty(httpParametersMap)) {
            String currentModuleName = httpParametersMap.get("moduleName");
            if(currentModuleName != null) {
                FacilioModule currentModule = modBean.getModule(currentModuleName);
                if(currentModule != null) {
                    prop.put("moduleId",currentModule.getModuleId());
                }
            }
            String relatedModuleName = httpParametersMap.get("relatedModuleName");
            if(currentModuleName != null) {
                FacilioModule relatedModule = modBean.getModule(relatedModuleName);
                if(relatedModule != null) {
                    prop.put("relatedModuleId",relatedModule.getModuleId());
                }
                String relatedFieldName = httpParametersMap.get("relatedFieldName");
                if(relatedFieldName != null) {
                    FacilioField relatedField = modBean.getField(relatedFieldName,relatedModuleName);
                    if(relatedField != null) {
                        prop.put("relatedFieldId",relatedField.getFieldId());
                    }
                }
            }
        }
        return prop;
    }
}