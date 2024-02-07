<template>
  <div class="height100 d-flex">
    <common-widget-chart
      v-bind="$props"
      key="avgrepairtime"
      moduleName="assetbreakdown"
      :customizeChartOptions="customizeChartOptions"
    >
      <template slot="title">{{
        $t('asset.performance.average_time_to_repair')
      }}</template>
    </common-widget-chart>
  </div>
</template>

<script>
import CommonWidgetChart from './charts/CommonWidgetChart'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
  ],
  components: { CommonWidgetChart },
  data() {
    return {
      customizeChartOptions: {
        legend: {
          show: false,
        },
        general: {
          point: {
            show: true,
          },
        },
      },
    }
  },
}
</script>
