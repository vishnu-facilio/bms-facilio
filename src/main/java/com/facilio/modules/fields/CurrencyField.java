package com.facilio.modules.fields;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.CurrencyRecord;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang3.StringUtils;

import java.util.Currency;
import java.util.Map;
import java.util.Optional;

public class CurrencyField extends BaseSystemLookupField< CurrencyRecord >{

	public CurrencyField () {
		super (FacilioConstants.SystemLookup.CURRENCY_RECORD, CurrencyRecord.class);
	}

	@Override
	public void addDefaultPropsToLookupRecordDuringFetch (Map< String, Object > lookupRecord) {

	}

	@Override
	public void addDefaultPropsToLookupRecordDuringFetch (CurrencyRecord lookupRecord) {

	}

	@Override
	public void validateRecord (Map< String, Object > lookupRecord) throws Exception {
		String currencyCode = (String) lookupRecord.get("currencyCode");
		V3Util.throwRestException (StringUtils.isEmpty (currencyCode),ErrorCode.VALIDATION_ERROR,"Currency code should not be empty");
		Optional<Currency> currency = Currency.getAvailableCurrencies().stream().filter(c -> c.getCurrencyCode().equals(currencyCode)).findFirst();
		V3Util.throwRestException(!currency.isPresent (), ErrorCode.VALIDATION_ERROR, "Currency code does not exist");
	}

}
