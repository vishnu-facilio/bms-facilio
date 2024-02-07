<template>
  <div class="pm-summary-inner-container mT20 height100">
    <div class="width100 height100 fc-summary-content-scroll">
      <div class="p30 pL80 pT30">
        <ActivitiesView
          :record="workorder"
          :module="currModuleName"
          ref="activitiesDiv"
        ></ActivitiesView>
      </div>
    </div>
  </div>
</template>
<script>
import ActivitiesView from '@/relatedlist/ActivitiesView'

export default {
  name: 'Resource',
  components: {
    ActivitiesView,
  },
  props: ['moduleName', 'details'],
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Resource'
    },
    workorder() {
      return this.details.workorder
    },
  },
}
</script>
