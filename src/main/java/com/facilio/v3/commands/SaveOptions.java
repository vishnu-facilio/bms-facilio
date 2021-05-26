package com.facilio.v3.commands;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveOptions {
    private boolean setLocalId;
    private List<FacilioField> fields;
    private Collection<SupplementRecord> supplements;
    private Class beanClass;

    public static SaveOptions of (FacilioContext context) {
        boolean localId = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, false);
        List<FacilioField> fields = (List<FacilioField>) context.get(Constants.PATCH_FIELDS);
        Collection<SupplementRecord> supplementFields = (Collection<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        return new SaveOptions(localId, fields, supplementFields, beanClass);
    }
}
