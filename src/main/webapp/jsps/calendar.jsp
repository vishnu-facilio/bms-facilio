<link href="${pageContext.request.contextPath}/css/form.css" rel="stylesheet">

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
<div class="row form-header" >
	<div class="col-sm-12" >
  <h4 class="pull-left">Calendar</h4>
  <div class="action-btn text-right">
  </div>
  </div>
</div>
<div class="temp-view-content">
<div class="row">
	<div class="col-lg-12">
		<div id="calendar_view"></div>
	</div>
</div>
</div>