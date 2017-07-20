<style>

	#wrap {
		margin: 0 auto;
	}
		
	#external-events {
		float: left;
		width: 150px;
		padding: 0 10px;
		border: 1px solid #ccc;
		background: #eee;
		text-align: left;
	}
		
	#external-events h4 {
		font-size: 16px;
		margin-top: 0;
		padding-top: 1em;
	}
		
	#external-events .fc-event {
		margin: 10px 0;
		cursor: pointer;
	}
		
	#external-events p {
		margin: 1.5em 0;
		font-size: 11px;
		color: #666;
	}
		
	#external-events p input {
		margin: 0;
		vertical-align: middle;
	}

	#calendar {
		float: right;
		width: 800px;
	}

</style>

<script>

$(function() { // document ready


	/* initialize the external events
	-----------------------------------------------------------------*/

	$('#external-events .fc-event').each(function() {

		// store data so the calendar knows to render an event upon drop
		$(this).data('event', {
			title: $.trim($(this).text()), // use the element's text as the event title
			stick: true // maintain when user navigates (see docs on the renderEvent method)
		});

		// make the event draggable using jQuery UI
		$(this).draggable({
			zIndex: 999,
			revert: true,      // will cause the event to go back to its
			revertDuration: 0  //  original position after the drag
		});

	});


	/* initialize the calendar
	-----------------------------------------------------------------*/

	$('#calendar').fullCalendar({
		now: '2017-05-07',
		editable: true, // enable draggable events
		droppable: true, // this allows things to be dropped onto the calendar
		aspectRatio: 1.8,
		scrollTime: '00:00', // undo default 6am scrollTime
		header: {
			left: 'today prev,next',
			center: 'title',
			right: 'timelineDay,timelineThreeDays,timelineWeek'
		},
		defaultView: 'timelineDay',
		views: {
			timelineThreeDays: {
				type: 'timeline',
				duration: { days: 3 }
			},
			timelineWeek: {
				slotDuration: '04:00'
			}
		},
		resourceLabelText: 'Agents',
		resources: [
			{ id: 'a', title: 'Shiva' },
			{ id: 'b', title: 'Yoge', eventColor: 'green' },
			{ id: 'c', title: 'Suresh', eventColor: 'orange' },
			{ id: 'f', title: 'Magesh', eventColor: 'red' },
			{ id: 't', title: 'Manthosh' },
			{ id: 'u', title: 'Prabhu' },
			{ id: 'v', title: 'Karthick' },
			{ id: 'w', title: 'Muna' },
			{ id: 'x', title: 'Rajavel' },
			{ id: 'y', title: 'Prashanth' }
		],
		events: [
			{ id: '1', resourceId: 'b', start: '2017-05-07T02:00:00', end: '2017-05-07T07:00:00', title: 'event 1' },
			{ id: '2', resourceId: 'c', start: '2017-05-07T05:00:00', end: '2017-05-07T22:00:00', title: 'event 2' },
			{ id: '3', resourceId: 'd', start: '2017-05-06', end: '2017-05-08', title: 'event 3' },
			{ id: '4', resourceId: 'e', start: '2017-05-07T03:00:00', end: '2017-05-07T08:00:00', title: 'event 4' },
			{ id: '5', resourceId: 'f', start: '2017-05-07T00:30:00', end: '2017-05-07T02:30:00', title: 'event 5' }
		],
		drop: function(date, jsEvent, ui, resourceId) {
			console.log('drop', date.format(), resourceId);
			$(this).remove();
		},
		eventReceive: function(event) { // called when a proper external event is dropped
			console.log('eventReceive', event);
		},
		eventDrop: function(event) { // called when an event (already on the calendar) is moved
			console.log('eventDrop', event);
		}
	});

});

</script>
<div class="row">
   <div class="col-lg-12">
       <h1 class="page-header">Central Dispatch</h1>
   </div>
   <!-- /.col-lg-12 -->
</div>
<div class="row">
	<div class="col-lg-12">
		<div id='wrap'>
		<div id='external-events'>
			<h4>Draggable Events</h4>
			<div class='fc-event'>My Event 1</div>
			<div class='fc-event'>My Event 2</div>
			<div class='fc-event'>My Event 3</div>
			<div class='fc-event'>My Event 4</div>
			<div class='fc-event'>My Event 5</div>
		</div>
		<div id='calendar'></div>
		<div style='clear:both'></div>
	</div>
	</div>
</div>