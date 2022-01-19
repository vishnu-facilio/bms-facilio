package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsoleV3.context.ModuleFieldPermission;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ValueGenerator;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GetValueGeneratorsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Reflections reflections = new Reflections("com.facilio.modules");
        Set<Class<? extends ValueGenerator>> classes = reflections.getSubTypesOf(ValueGenerator.class);
        List<ValueGenerator> valueGeneratorList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(classes)){
            for(Class<? extends ValueGenerator> vg : classes){
                ValueGenerator obj = vg.newInstance();
                valueGeneratorList.add(obj);
            }
            context.put(FacilioConstants.ContextNames.VALUE_GENERATORS, valueGeneratorList);
        }


        return false;
    }
}
