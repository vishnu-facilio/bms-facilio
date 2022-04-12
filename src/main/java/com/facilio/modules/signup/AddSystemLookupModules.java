package com.facilio.modules.signup;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BaseSingleRelRecordCRUDHandler;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SystemLookupCRUDHandler;

import java.util.ArrayList;
import java.util.List;

public class AddSystemLookupModules extends SignUpData {

    @Override
    public void addData() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule urlRecord = constructUrlRecordMod();
        modules.add(urlRecord);

		FacilioModule currencyRecord = constructCurrencyRecordMod();
		modules.add (currencyRecord);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

    }

	private FacilioModule constructCurrencyRecordMod () {
		FacilioModule module = new FacilioModule(FacilioConstants.SystemLookup.CURRENCY_RECORD,
				"Currency Record",
				"Currency_Records",
				FacilioModule.ModuleType.SYSTEM_LOOKUP,
				false);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getDefaultField(SystemLookupCRUDHandler.PARENT_ID_FIELD_NAME, "Parent Id", "PARENT_ID", FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField(SystemLookupCRUDHandler.FIELD_ID_FIELD_NAME, "Field Id", "FIELD_ID", FieldType.NUMBER));
		fields.add(FieldFactory.getDefaultField("currencyCode", "Currency Code", "CURRENCY_CODE", FieldType.STRING));
		fields.add(FieldFactory.getDefaultField("currencyValue", "Currency Value", "CURRENCY_VALUE", FieldType.DECIMAL));


		module.setFields(fields);
		return module;
	}

	private FacilioModule constructUrlRecordMod() {
        FacilioModule module = new FacilioModule(FacilioConstants.SystemLookup.URL_RECORD,
                "Url Record",
                "Url_Records",
                FacilioModule.ModuleType.SYSTEM_LOOKUP,
                false);

        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getDefaultField(SystemLookupCRUDHandler.PARENT_ID_FIELD_NAME, "Parent Id", "PARENT_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField(SystemLookupCRUDHandler.FIELD_ID_FIELD_NAME, "Field Id", "FIELD_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("href", "Url", "HREF", FieldType.STRING));
        fields.add(FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true));
        fields.add(FieldFactory.getDefaultField("alt", "Title", "ALT", FieldType.STRING));

        module.setFields(fields);
        return module;
    }
}
