<style>

.fc-view-container h2
{
	font-size :21px;
}

.fc-day-number
{
	color:#383838 !important;
}

</style>

<script>

$(document).ready(function() {

	$('#calendar_view').fullCalendar({
		now: '2017-05-07',
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'agendaDay,agendaWeek,month'
		},
		editable: true,
		eventLimit: true, // allow "more" link when too many events
		navLinks: true,
		events: [
			{ id: '1', start: '2017-05-07T02:00:00', end: '2017-05-07T07:00:00', title: 'event 1' },
			{ id: '2', start: '2017-05-07T05:00:00', end: '2017-05-07T22:00:00', title: 'event 2' },
			{ id: '3', start: '2017-05-06', end: '2017-05-08', title: 'event 3' },
			{ id: '4', start: '2017-05-07T03:00:00', end: '2017-05-07T08:00:00', title: 'event 4' },
			{ id: '5', start: '2017-05-07T00:30:00', end: '2017-05-07T02:30:00', title: 'event 5' }
		]
	});

});

</script>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">Calendar</h1>
   </div>
</div>
<div class="temp-view-content">
<div class="row">
	<div class="col-lg-12">
		<div id="calendar_view"></div>
	</div>
</div>
</div>