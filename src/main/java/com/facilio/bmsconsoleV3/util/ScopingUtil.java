package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.MultiFieldOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.reflections.Reflections;
import java.util.*;

public class ScopingUtil {

    public static ValueGenerator getValueGeneratorForLinkName(String linkName) throws Exception{
        Reflections reflections = new Reflections(linkName);
        Set<Class<? extends ValueGenerator>> valueGeneratorClasses = reflections.getSubTypesOf(ValueGenerator.class);
        if(CollectionUtils.isNotEmpty(valueGeneratorClasses)) {
            for (Class<? extends ValueGenerator> valueGenerator : valueGeneratorClasses) {
                ValueGenerator obj = valueGenerator.newInstance();
                return obj;
            }
        }
        return null;
    }
}