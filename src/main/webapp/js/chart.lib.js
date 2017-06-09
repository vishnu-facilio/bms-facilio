/**
 * 
 */

ChartLibrary = function() {};

ChartLibrary.timeseries = function(selector, chart_obj) {
	
	chart_obj.bindto = selector;
	var chart = c3.generate(chart_obj);
	return chart;
};