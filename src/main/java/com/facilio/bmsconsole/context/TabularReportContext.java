package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.facilio.bmsconsole.context.ReportContext.ReportChartType;

public class TabularReportContext {

	boolean rowGrandTotal;

	public boolean isRowGrandTotal() {
		return rowGrandTotal;
	}

	public void setRowGrandTotal(boolean rowGrandTotal) {
		this.rowGrandTotal = rowGrandTotal;
	}

	public boolean isColGrandTotal() {
		return colGrandTotal;
	}

	public void setColGrandTotal(boolean colGrandTotal) {
		this.colGrandTotal = colGrandTotal;
	}

	boolean colGrandTotal;
	boolean hideRowNumbers;

	ThemeType theme;

	public boolean isHideRowNumbers() {
		return hideRowNumbers;
	}

	public void setHideRowNumbers(boolean hideRowNumbers) {
		this.hideRowNumbers = hideRowNumbers;
	}

	public ThemeType getTheme() {
		return theme;
	}

	public void setTheme(ThemeType theme) {
		this.theme = theme;
	}

	private String styles;

	public String getStyles() {
		return styles;
	}

	public void setStyles(String styles) {
		this.styles = styles;
	}

	public String getConditionalFormatting() {
		return conditionalFormatting;
	}

	public void setConditionalFormatting(String conditionalFormatting) {
		this.conditionalFormatting = conditionalFormatting;
	}

	public String conditionalFormatting;

	public List<TabularColumnContext> getColumns() {
		return columns;
	}

	public void setColumns(List<TabularColumnContext> columns) {
		this.columns = columns;
	}

	private List<TabularColumnContext> columns;

	public enum ThemeType {
		PLAIN(1), BORDERED(2), GRID(3);

		private int layout;

		ThemeType(int layout) {
			this.layout = layout;
		}

		public int getLayout() {
			return layout;
		}

		public void setLayout(int layout) {
			this.layout = layout;
		}

	}

}
