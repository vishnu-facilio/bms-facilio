package com.facilio.bmsconsole.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modulemapping.ModuleMappingBaseInfoContext;
import com.facilio.bmsconsole.modulemapping.ModuleMappingValidationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ModuleMappings extends ModuleMappingBaseInfoContext {
    private String name;
    private String displayName;
    private String sourceModuleName;
    private String targetModuleName;
    private Long sourceModuleId;
    private Long targetModuleId;
    private List<ModuleMappingContext> mappingList;

    public ModuleMappings(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public ModuleMappings() {
    }

    public ModuleMappingContext addModuleMapping(ModuleMappingContext mappingContext) {
        if (this.mappingList == null) {
            this.mappingList = new ArrayList<>(Arrays.asList(mappingContext));
        } else {
            this.mappingList.add(mappingContext);
        }
        mappingContext.setModuleMappings(this);
        return mappingContext;
    }

    public ModuleMappingContext addModuleMapping(String name, String displayName, String targetModule, boolean isDefault) {

        ModuleMappingContext mappingContext = new ModuleMappingContext(name, displayName, targetModule, isDefault);
        if (this.mappingList == null) {
            this.mappingList = new ArrayList<>(Arrays.asList(mappingContext));
        } else {
            this.mappingList.add(mappingContext);
        }
        mappingContext.setModuleMappings(this);
        return mappingContext;

    }

    public enum Conversion_Type {
        ONE_TO_ONE(1,"One to One conversion"),
        MANY_TO_ONE(2,"Many to One conversion"),
        MANY_TO_MANY(3,"Many to Many conversion"),
        ;

        int type;
        String typeName;

        private Conversion_Type(int type,String typeName) {
            this.type = type;
            this.typeName = typeName;
        }

        public int getType() {
            return type;
        }

        public String getTypeName() {
            return typeName;
        }

    }

}
