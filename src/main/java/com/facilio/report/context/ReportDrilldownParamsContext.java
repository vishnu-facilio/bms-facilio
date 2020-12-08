package com.facilio.report.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportDrilldownParamsContext {

	
	public static class DrilldownCriteria{
		private String dimensionValues;
		private HashMap<String, Object> xField;
		ReportFieldContext xAxis;
		
		
		@JSON(serialize = false)
		public ReportFieldContext getxAxis() {
			return xAxis;
		}
		public void setxAxis(ReportFieldContext xAxis) {
			this.xAxis = xAxis;
		}
		private HashMap<String, Object> groupBy;
		private String breadcrumbLabel;// the clicked BAR/DIMENSION from previous report
		public String getBreadcrumbLabel() {
			return breadcrumbLabel;
		}
		public void setBreadcrumbLabel(String breadcrumbLabel) {
			this.breadcrumbLabel = breadcrumbLabel;
		}
		public HashMap<String, Object> getGroupBy() {
			return groupBy;
		}
		public void setGroupBy(HashMap<String, Object> groupBy) {
			this.groupBy = groupBy;
		}
		public HashMap<String, Object> getxField() {
			return xField;
		}
		public void setxField(HashMap<String, Object> xField) throws Exception {
			this.xField = xField;
			long fieldId=(long)this.xField.get("field_id");
			long moduleId=(long)this.xField.get("module_id");
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField field=modBean.getField(fieldId);
			FacilioModule module=modBean.getModule(moduleId);
			this.xAxis=new ReportFieldContext();
			this.xAxis.setField(module, field);

			
		}
		public String getDimensionValues() {
			return dimensionValues;
		}
		public void setDimensionValues(String dimensionValues) {
			this.dimensionValues = dimensionValues;
		}
		private String groupByValues;
		public String getGroupByValues() {
			return groupByValues;
		}
		public void setGroupByValues(String groupByValues) {
			this.groupByValues = groupByValues;
		}
		
		
	
	}
	private Criteria criteria;
	
	

	@JSON(serialize = false)
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private List<DrilldownCriteria> drilldownCriteria;

	

	public List<DrilldownCriteria> getDrilldownCriteria() {
		return drilldownCriteria;
	}
	public void setDrilldownCriteria(List<DrilldownCriteria> drilldownCriteria) {
		this.drilldownCriteria = drilldownCriteria;
	}


	
	
	private Map<String,Object> xField;


	public Map<String, Object> getxField() {
		return xField;
	}
	public void setxField(Map<String, Object> xField) {
		this.xField = xField;
	}

	private AggregateOperator xAggr;
	String seriesAlias;
	public String getSeriesAlias() {
		return seriesAlias;
	}
	public void setSeriesAlias(String seriesAlias) {
		this.seriesAlias = seriesAlias;
	}
	public int getxAggr() {
		if (xAggr != null) {
			return xAggr.getValue();
		}
		return -1;
	}

	public void setxAggr(int xAggr) {
		this.xAggr = AggregateOperator.getAggregateOperator(xAggr);
	}
	
	@JsonIgnore
	public AggregateOperator getxAggrEnum() {
		return xAggr;
	}

	public void setxAggr(AggregateOperator xAggr) {
		this.xAggr = xAggr;
	}
}
