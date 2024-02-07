<template>
  <el-dialog
    title="TREND"
    v-if="visible"
    :visible.sync="visible"
    :append-to-body="true"
    custom-class="f-popup-view"
    top="0%"
    :before-close="beforeClose"
  >
    <div
      class="content analytics-txt"
      style="cursor: pointer; color: rgb(57, 178, 194); font-size: 13px; text-align: right; font-weight: 500; margin-right: 20px;"
      @click="openSelectedReadingInAnalytics"
    >
      Go to Analytics
      <img
        style="width:13px; height: 9px;"
        src="~statics/icons/right-arrow.svg"
      />
    </div>
    <f-new-analytic-report
      :config.sync="trendPopup.analyticsConfig"
    ></f-new-analytic-report>
  </el-dialog>
</template>

<script>
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import NewDateHelper from '@/mixins/NewDateHelper'
import JumpToHelper from '@/mixins/JumpToHelper'
import {
  dateOperators,
  aggregateFunctions,
} from 'pages/card-builder/card-constants'
export default {
  mixins: [JumpToHelper],
  props: ['visible', 'closeAction', 'trendData'],
  components: { FNewAnalyticReport },
  data() {
    return {
      dateOperators,
      aggregateFunctions,
      trendPopup: {
        analyticsConfig: {
          period: 0,
          mode: 1,
          dateFilter: NewDateHelper.getDatePickerObject(22),
          dataPoints: [],
          hidechartoptions: true,
          hidecharttypechanger: true,
        },
      },
    }
  },
  mounted() {
    this.prepareTrendData()
  },
  methods: {
    beforeClose() {
      this.closeAction()
    },
    openSelectedReadingInAnalytics() {
      this.cardBuilderJumpToHelper(this.trendData)
    },
    prepareTrendData() {
      let {
        buildingId,
        yAggr,
        dateOperator,
        readings,
        id,
        module,
        linkType,
      } = this.trendData
      let parentId = readings[0].parentId
      let fieldId = readings[0].fieldId
      if (!dateOperator) {
        dateOperator = {
          operatorId: 22,
        }
      }
      this.trendPopup.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
        dateOperator.operatorId
      )
      let datapoint = {
        parentId: parentId,
        yAxis: {
          fieldId: fieldId,
          aggr: yAggr || 3,
        },
        aliases: {
          actual: 'A',
        },
      }
      if (buildingId) {
        this.$set(this.trendPopup.analyticsConfig, 'buildingId', buildingId)
      }
      this.trendPopup.analyticsConfig.dataPoints.push(datapoint)
    },
  },
}
</script>
