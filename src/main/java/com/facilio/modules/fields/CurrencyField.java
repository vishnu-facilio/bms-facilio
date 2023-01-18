package com.facilio.modules.fields;

import com.facilio.bmsconsole.context.CurrencyContext;
import org.apache.commons.lang.StringUtils;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.util.V3Util;
import java.util.Currency;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Map;

@Getter @Setter
public class CurrencyField extends FacilioField implements SupplementRecord {

	private static final long serialVersionUID = 1L;

	public CurrencyField() {
		super();
	}

	@Override
	public String linkName() {
		return super.getName();
	}

	@Override
	public FacilioField selectField() {
		return null;
	}

	@Override
	public FetchSupplementHandler newFetchHandler() {
		return new CurrencyFieldCRUDHandler(this);
	}

	@Override
	public InsertSupplementHandler newInsertHandler() {
		return new CurrencyFieldCRUDHandler(this);
	}

	@Override
	public UpdateSupplementHandler newUpdateHandler() {
		return new CurrencyFieldCRUDHandler(this);
	}

	@Override
	public DeleteSupplementHandler newDeleteHandler() {
		return new CurrencyFieldCRUDHandler(this);
	}

	public void validateRecord (Map< String, Object > lookupRecord) throws Exception {
		String currencyCode = (String) lookupRecord.get("currencyCode");
		CurrencyContext baseCurrency = (CurrencyContext) lookupRecord.get("baseCurrency");
		CurrencyContext currencyContext = (CurrencyContext) lookupRecord.get("currencyObject");

		if (baseCurrency != null)  {
			V3Util.throwRestException(StringUtils.isEmpty (currencyCode), ErrorCode.VALIDATION_ERROR, "Currency code should not be empty");
			V3Util.throwRestException(currencyContext == null, ErrorCode.VALIDATION_ERROR, "Currency not defined");
		}
	}
}
