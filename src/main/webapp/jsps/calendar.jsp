<link rel="stylesheet" href="${pageContext.request.contextPath}/js/calendar/fullcalendar.css">
<script src="${pageContext.request.contextPath}/js/calendar/jquery.min.js"></script> 
<script src="${pageContext.request.contextPath}/js/calendar/moment.min.js"></script> 
<script src="${pageContext.request.contextPath}/js/calendar/fullcalendar.js"></script> 

<style>

h2
{
	font-size :21px;
}

a
{
	color:#666;
}

</style>

<script>

$(document).ready(function() {

	$('#calendar').fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,agendaWeek,agendaDay,listWeek'
		},
		editable: true,
		eventLimit: true, // allow "more" link when too many events
		navLinks: true,
	});

});

</script>

<div style="padding:20px;">
<div id='calendar'></div>
</div>