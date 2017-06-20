<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="facilio-tags" prefix="f" %>
<script>
	function treeOnMove(data) {
		console.log('Node moved ::: '+JSON.stringify(data));
	}
	
	function treeOnClick(data) {
		console.log('Node clicked ::: '+JSON.stringify(data));
	}
</script>
<div id="content">
<f:chart id="eb-meter" type="tree-collapsible" width="900" height="550" url="../js/d3/sample.json" onmove="treeOnMove" onclick="treeOnClick"/>

<br/><br/>
<f:chart id="eb-meter-performance" type="line-timeseries" width="900" height="550"/>
</div>
<%--

The widget automatically fetch the data from server using AJAX for the given attributes (id, type).

<f:widget type="chart/tree" datasource="meter" subtype="tree/collapsible" widgetid="unique-id" width="900" height="550" />

<f:widget type="chart/line" subtype="line/timeseries" widgetid="unique-id" width="900" height="550"/>

<f:widget type="chart/pie" subtype="pie/classic" widgetid="unique-id" width="900" height="550"/>

<f:widget type="chart/pie" subtype="pie/donut" widgetid="unique-id" width="900" height="550"/>

--%>
