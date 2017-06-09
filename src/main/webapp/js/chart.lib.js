/**
 * 
 */

ChartLibrary = function() {};

ChartLibrary.timeseries = function(selector, chart_obj) {
	
	var chart = c3.generate(chart_obj);
	return chart;
};