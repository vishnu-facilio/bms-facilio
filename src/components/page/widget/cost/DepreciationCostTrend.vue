<template>
  <div class="height100 d-flex">
    <common-widget-chart
      v-bind="$props"
      key="depreciationcosttrend"
      moduleName="assetDepreciationCalculation"
      type="bar"
      isWidget="true"
      class="depreciationcosttrend"
      :customizeChartOptions="options"
      :isDateFixed="true"
      @reportLoaded="onReportLoaded"
    >
      <template slot="title">Depreciation Cost Trend</template>
    </common-widget-chart>
    <!-- <div
      v-show="showLegends"
      class="fc-maintenance-dot flex-middle width100 pB20 pL60 bottom-0 position-absolute height60px"
    >
      <div class="flex-middle">
        <div class="fc-dot fc-dot-red2"></div>
        <div class="fc-black-12 pL10">
          Opening Book Value
        </div>
      </div>
      <div class="flex-middle mL20">
        <div class="fc-dot fc-dot-yellow3"></div>
        <div class="fc-black-12 pL10">
          DDB
        </div>
      </div>
    </div> -->
  </div>
</template>
<script>
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'

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
      options: {
        axis: {
          y: {
            label: {
              text: 'BOOK VALUE',
            },
          },
          x: {
            label: {
              text: 'TIME',
            },
          },
        },
        isDepreciationCostTrend: true,
        customizeC3: {
          data: {
            colors: {
              'Current Price': '#ec6363',
              'Depreciated Amount': '#ecb163',
            },
            names: {
              'Current Price': 'Current Price',
              'Depreciated Amount': 'DDB',
              OBV: 'Opening Book Value',
            },
            order: 'asc',
          },
        },
        // general: {
        //   hideZeroes: true,
        // },
        legend: {
          show: false,
        },
      },
      showLegends: false,
    }
  },
  methods: {
    onReportLoaded(report, result) {
      if (
        report &&
        report.data &&
        Object.keys(report.data).filter(key => key !== 'x').length
      ) {
        this.showLegends = true
      } else {
        this.showLegends = false
      }
    },
  },
}
</script>
<style>
.depreciationcosttrend
  .bb-shapes.bb-shapes-Current-Price.bb-bars.bb-bars-Current-Price {
  visibility: hidden;
}
/* .depreciationcosttrend
  .bb-shapes.bb-shapes-Current-Price.bb-bars.bb-bars-Current-Price
  .bb-bar-0 {
  visibility: visible;
} */
.depreciationcosttrend .bb-axis-x-label,
.depreciationcosttrend .bb-axis-y-label,
.depreciationcosttrend .bb-axis-y2-label,
.depreciationcosttrend .bb .bb-axis-x .tick text,
.depreciationcosttrend .bb .bb-axis-y .tick text,
.depreciationcosttrend .bb .bb-axis-y2 .tick text {
  fill: #324056 !important;
}
.depreciationcosttrend .bb .bb-axis-y path.domain,
.depreciationcosttrend .bb .bb-axis-x path.domain {
  opacity: 1 !important;
  stroke: #eceef1 !important;
}
</style>
