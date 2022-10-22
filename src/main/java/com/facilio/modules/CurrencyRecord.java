package com.facilio.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyRecord extends BaseSystemLookupRecord{

	private String currencyCode;
	private double currencyValue;
	private double exchangeRate;
	private double baseCurrencyValue;
}
