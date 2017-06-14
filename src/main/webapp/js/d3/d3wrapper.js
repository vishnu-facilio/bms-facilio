jQuery.cachedScript = function( url, options ) {
 
  // Allow user to set any option except for dataType, cache, and url
  options = $.extend( options || {}, {
    dataType: "script",
    cache: true,
    url: url
  });
 
  // Use $.ajax() since it is more flexible than $.getScript
  // Return the jqXHR object so we can chain callbacks
  return jQuery.ajax( options );
};

function loadMeters(parent, width, height, data_url)
{
	$.cachedScript("../js/d3/collapsibletree.js").done(function( script, textStatus ) {
		
		load_chart(parent, width, height, data_url);
		
		});
	
}

function loadMeterPerformance(parent, width, height)
{
	$.cachedScript("../js/d3/linechart.js").done(function( script, textStatus ) {
		
		line_chart(parent, width, height);
		
		});
	
}
