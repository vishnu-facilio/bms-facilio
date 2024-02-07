<template>
  <div class="booking-info">
    <div class="text">
      Booking Date :
      {{
        !$validation.isEmpty(details.bookingDate)
          ? $options.filters.formatDate(details.bookingDate, true)
          : '---'
      }}
      <span class="separator self-center mL10 mR10">|</span> Attendees :
      {{
        `${details.noOfAttendees} ${
          details.noOfAttendees > 1 ? 'Members' : 'Member'
        }` || '---'
      }}
    </div>
    <div class="text">
      <currency :value="details.bookingAmount || 0"></currency>
    </div>
  </div>
</template>
<script>
export default {
  props: ['details'],
}
</script>
