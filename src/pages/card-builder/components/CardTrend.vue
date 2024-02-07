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
import { cloneDeep } from 'lodash'
import { isEmpty } from '@facilio/utils/validation'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import NewDateHelper from 'src/pages/new-dashboard/components/date-picker/NewDateHelper.js'
import JumpToHelper from '@/mixins/JumpToHelper'
import { API } from '@facilio/api'

import {
  dateOperators,
  aggregateFunctions,
} from 'pages/card-builder/card-constants'
export default {
  mixins: [JumpToHelper],
  props: ['visible', 'closeAction', 'cardComponent'],
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
    async openSelectedReadingInAnalytics() {
      if (
        this.trendPopup.analyticsConfig.dataPoints &&
        this.trendPopup.analyticsConfig.dataPoints.length
      ) {
        let parentType =
          this.$getProperty(
            this,
            'cardComponent.cardParams.reading.parentType'
          ) || ''
        let buildingId = null
        if (parentType === 'asset') {
          let dataPoints =
            this.$getProperty(this, 'trendPopup.analyticsConfig.dataPoints') ||
            []
          let assetId = this.$getProperty(dataPoints, '0.parentId') || null
          if (!isEmpty(assetId)) {
            let params = {
              id: assetId,
              moduleName: parentType,
            }
            let { data, error } = await API.get(
              `v3/modules/${parentType}/${assetId}`,
              params
            )
            if (!error) {
              buildingId =
                this.$getProperty(data, 'asset.space.building.id') || null
            }
          }
        }
        this.visible = false
        this.jumpReadingsToAnalytics(
          this.trendPopup.analyticsConfig.dataPoints,
          null,
          null,
          buildingId
        )
      }
    },
    prepareTrendData() {
      let { cardParams, cardFilters } = this.cardComponent
      let { reading } = cardParams
      let dateFilterState = null
      if (!isEmpty(cardFilters)) {
        dateFilterState = NewDateHelper.getDatePickerObject(
          cardFilters.operatorId,
          cardFilters.dateValueString
        )
      } else {
        dateFilterState = NewDateHelper.getDatePickerObject(
          this.dateOperators.find(rt => {
            return rt.value === cardParams.dateRange
          }).enumValue
        )
      }
      this.trendPopup.analyticsConfig.dateFilter = cloneDeep(dateFilterState)
      let dataPoints = []
      let datapoint = {
        parentId: null,
        yAxis: {
          fieldId: null,
          aggr: 3,
        },
        aliases: {
          actual: 'A',
        },
      }
      let { kpiType = '' } = reading || {}
      let randomstring = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
      if (Array.isArray(reading.parentId)) {
        reading.parentId.forEach((rt, index) => {
          datapoint.parentId = rt
          datapoint.yAxis.fieldId = reading.fieldId
          datapoint.yAxis.aggr = this.aggregateFunctions.find(
            rt => rt.value === reading.yAggr
          ).enumValue
          datapoint.aliases.actual = randomstring[index]
          dataPoints.push(datapoint)
        })
        this.trendPopup.analyticsConfig.dataPoints = dataPoints
      } else if (Array.isArray(reading.yAggr)) {
        reading.yAggr.forEach((rt, index) => {
          datapoint.parentId = reading.parentId
          datapoint.yAxis.fieldId = reading.fieldId
          datapoint.yAxis.aggr = this.aggregateFunctions.find(
            rt => rt.value === rt
          ).enumValue
          datapoint.aliases.actual = randomstring[index]
          dataPoints.push(datapoint)
        })
        this.trendPopup.analyticsConfig.dataPoints = dataPoints
      } else {
        if (kpiType === 'DYNAMIC') {
          let { fieldId = -1 } = reading || {}
          datapoint.yAxis.dynamicKpi = fieldId
          datapoint.kpiType = 'DYNAMIC'
        } else {
          datapoint.yAxis.fieldId = reading.fieldId
        }
        datapoint.parentId = reading.parentId
        datapoint.yAxis.aggr = this.aggregateFunctions.find(
          rt => rt.value === reading.yAggr
        ).enumValue
        dataPoints.push(datapoint)
        this.trendPopup.analyticsConfig.dataPoints = dataPoints
      }
    },
  },
}
</script>
