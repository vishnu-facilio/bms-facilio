package com.facilio.v3.context;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ConfigParams {

    public static void addConfigParams(FacilioContext context, ConfigParams configParams) throws Exception {
         if (configParams == null){
             return;
         }
    }

}
