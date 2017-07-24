<style>

.fc-view-container h2
{
	font-size :21px;
}

.fc-view-container a
{
	color:#666;
}

</style>

<script>

$(document).ready(function() {

	$('#calendar_view').fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'agendaDay,agendaWeek,month'
		},
		editable: true,
		eventLimit: true, // allow "more" link when too many events
		navLinks: true,
	});

});

</script>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">Calendar</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="temp-view-content">
<div class="row">
	<div class="col-lg-12">
		<div id="calendar_view"></div>
	</div>
</div>
</div>