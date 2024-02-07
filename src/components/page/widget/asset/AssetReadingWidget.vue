<template>
  <div
    class="d-flex flex-direction-column"
    v-if="details && details.id > 0 && parentId > 0 && reportId > 0"
  >
    <div class="border-bottom1px d-flex justify-content-space">
      <div class="f16 bold mT20 mB20 mL30 inline">
        {{ widget && widget.title ? widget.title : '' }}
      </div>
      <div class="mT10">
        <portal-target :name="'widget-datepicker' + reportId"></portal-target>
      </div>
    </div>
    <div>
      <FNewReport
        :id="reportId"
        :templateJson="templateJson"
        hideTabularReport="true"
        hideChartSettings="true"
        :datePickerTarget="reportId"
        class="asset-summary-reading-report"
      >
      </FNewReport>
    </div>
  </div>
</template>

<script>
import FNewReport from 'pages/report/components/FNewReport'
export default {
  components: {
    FNewReport,
  },
  props: ['moduleName', 'details', 'layoutParams', 'widget'],
  computed: {
    templateJson() {
      let json = {
        categoryFillter: null,
        categoryFillter1: null,
        categoryId: 1,
        categoryTemplate: null,
        chooseValues: [],
        criteria: null,
        defaultValue: this.details.id || this.parentId,
        isVisibleInDashBoard: false,
        parentId: this.details.id || this.parentId,
        show: true,
        siteId: null,
        templateType: null,
      }
      return json
    },
    reportId() {
      let id = this.widget.widgetParams.reportId
      return id
    },
    parentId() {
      return this.details.id
    },
  },
}
</script>
<style>
.asset-summary-reading-report .fc-chart-type {
  display: none;
}
</style>
