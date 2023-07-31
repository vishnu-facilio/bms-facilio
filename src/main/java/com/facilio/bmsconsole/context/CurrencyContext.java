package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CurrencyContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id = -1;
	private long orgId = -1;
	private String displayName;
	private String currencyCode;
	private String displaySymbol;
	private double exchangeRate;
	private int decimalPlaces;
	private boolean baseCurrency;
	private long sysCreatedTime = -1;
	private long sysCreatedBy = -1;
	private long sysModifiedTime = -1;
	private long sysModifiedBy = -1;
	private Boolean status;
	public boolean status() {
		if(status != null) {
			return status;
		}
		return false;
	}
}
