package com.facilio.bmsconsole.ModuleSettingConfig.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.util.ModuleSettingConfigUtil;
import com.facilio.bmsconsole.context.ModuleSettingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetModuleSettingConfigurationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String settingConfigurationName = (String) context.get(FacilioConstants.ContextNames.MODULE_SETTING_NAME);

        if (StringUtils.isNotEmpty(moduleName) && StringUtils.isNotEmpty(settingConfigurationName) ) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module summary");


            FacilioModule moduleConfigurationModule = ModuleFactory.getModuleConfigurationModule();
            List<FacilioField> moduleConfigurationFields = FieldFactory.getModuleConfigurationFields();

            GenericSelectRecordBuilder moduleConfigurationBuilder = new GenericSelectRecordBuilder()
                    .table(moduleConfigurationModule.getTableName())
                    .select(moduleConfigurationFields)
                    .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("CONFIGURATION_NAME", "configurationName", settingConfigurationName, StringOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(Boolean.TRUE), BooleanOperators.IS));

            Map<String, Object> moduleConfigProps = moduleConfigurationBuilder.fetchFirst();

            List<String> configurationList = ModuleSettingConfigUtil.COMMON_CONFIGURATION_LIST;
            Object result = new Object();

            if(configurationList.contains(settingConfigurationName) && MapUtils.isEmpty(moduleConfigProps) ){

                ModuleSettingContext setting = new ModuleSettingContext();
                setting.setName(settingConfigurationName);
                setting.setConfigurationName(settingConfigurationName);
                setting.setModuleId(module.getModuleId());

                if(Objects.equals(settingConfigurationName, FacilioConstants.ContextNames.STATE_FLOW)){
                    setting.setStatus(module.isStateFlowEnabled());
                }else {
                    setting.setStatus(false);
                }
                result = setting;
            }else{
                result = ModuleSettingConfigUtil.getModuleConfigurationDetails(settingConfigurationName, module);
            }

            context.put(settingConfigurationName, result);

        }

        return false;
    }
}
