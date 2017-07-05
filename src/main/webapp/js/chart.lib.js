/**
 * 
 */

ChartLibrary = function() {};

ChartLibrary.timeseries = function(selector, chart_obj) {
	
	chart_obj.bindto = selector;
	var chart = c3.generate(chart_obj);
	return chart;
};

ChartLibrary.barchart = function(selector, chart_obj) {
	chart_obj.bindto = selector;
	var chart = c3.generate(chart_obj);
	return chart;
}

ChartLibrary.donutchart = function(selector, chart_obj) {
	chart_obj.bindto = selector;
	var chart = c3.generate(chart_obj);
	return chart;
}