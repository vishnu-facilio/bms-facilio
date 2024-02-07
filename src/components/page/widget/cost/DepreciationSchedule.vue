<template>
  <div class="d-flex">
    <portal :to="widget.key + '-title-section'">
      <div class="widget-header d-flex mB15 mT5 width100">
        <div class="width100">
          <div class="widget-header-name">DEPRECIATION SCHEDULE</div>
        </div>
      </div>
    </portal>

    <depreciation-schedule
      :asset="details"
      :showMethod="true"
    ></depreciation-schedule>
  </div>
</template>
<script>
import DepreciationSchedule from 'pages/setup/depreciation/ScheduleList'

export default {
  props: ['details', 'widget'],

  components: { DepreciationSchedule },
}
</script>
