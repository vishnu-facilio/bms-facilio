package com.facilio.bmsconsoleV3.signup.scopeVariable;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ValueGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddValueGenerators extends SignUpData {
    @Override
    public void addData() throws Exception {
        Reflections reflections = new Reflections("com.facilio.modules");
        Set<Class<? extends ValueGenerator>> valueGeneratorClasses = reflections.getSubTypesOf(ValueGenerator.class);
        List<ValueGeneratorContext> valueGeneratorList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(valueGeneratorClasses)){
            ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            for(Class<? extends ValueGenerator> valueGenerator : valueGeneratorClasses){
                ValueGenerator obj = valueGenerator.newInstance();
                FacilioModule module = modBean.getModule(obj.getModuleName());
                if(module != null) {
                    ValueGeneratorContext valueGeneratorContext = new ValueGeneratorContext();
                    valueGeneratorContext.setLinkName(obj.getLinkName());
                    valueGeneratorContext.setDisplayName(obj.getValueGeneratorName());
                    valueGeneratorContext.setIsConstant(false);
                    valueGeneratorContext.setIsSystem(true);
                    valueGeneratorContext.setIsHidden(obj.getIsHidden());
                    if(module.getModuleId() > 0){
                        valueGeneratorContext.setModuleId(module.getModuleId());
                    } else {
                        valueGeneratorContext.setSpecialModuleName(module.getName());
                    }
                    valueGeneratorContext.setOperatorId(obj.getOperatorId());
                    valueGeneratorList.add(valueGeneratorContext);
                }
            }
            valGenBean.addValueGenerators(valueGeneratorList);
        }
    }
}

