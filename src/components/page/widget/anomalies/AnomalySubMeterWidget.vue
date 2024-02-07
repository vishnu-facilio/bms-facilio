<template>
  <div class="f-el-group-table-style-1 fc-anamaloy-metertable-page">
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name">Sub-meter Contributions</div>
      </div>
    </portal>
    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else-if="!(subMeterDetails.length > 0)">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="pT20 fc-black-dark f18 bold">No Sub Meter Available!</div>
      </div>
    </div>
    <el-table v-else-if="!loading" :data="subMeterDetails" style="width: 100%">
      <el-table-column
        prop="name"
        label="SUB-METER NAME"
        width="200"
      ></el-table-column>
      <el-table-column label="NO OF OCCURRENCES">
        <el-table-column
          prop="anomalyThisMonth"
          :label="$t('common.date_picker.this_month')"
        ></el-table-column>
        <el-table-column
          prop="anomalyLastMonth"
          label="Last Month"
        ></el-table-column>
      </el-table-column>
      <el-table-column label="ENERGY/CDD (kWh/CDD)">
        <el-table-column
          prop="energyByCdd"
          :label="$t('common.date_picker.this_month')"
        ></el-table-column>
        <el-table-column
          prop="energyByCddLastMonth"
          label="Last Month"
        ></el-table-column>
      </el-table-column>
      <el-table-column label="EXCEEDED ENERGY">
        <el-table-column
          prop="projectedWastage"
          :label="$t('common.date_picker.this_month')"
        ></el-table-column>
        <el-table-column
          prop="lastMProjectedWastage"
          label="Last Month"
        ></el-table-column>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import AnomalyMixin from '@/mixins/AnomalyMixin'
export default {
  props: ['details', 'widget'],
  mixins: [AnomalyMixin],
  mounted() {
    this.loadSubMeter()
  },
  data() {
    return {
      loading: false,
      subMeterDetails: [],
      assetMetricMap: {},
    }
  },
  methods: {
    loadSubMeter() {
      this.loading = true
      if (this.details && this.details.alarm) {
        this.$util
          .getdefaultWorkFlowResult('48', this.details.alarm.id)
          .then(response => {
            this.loading = false
            let subMeters = response.subMeter
            let ids = subMeters.map(meter => meter.id)
            if (ids.length) {
              this.loadData(ids, subMeters)
            }
          })
          .catch(() => {
            this.loading = false
          })
      }
    },
    loadData(resourceIds, subMeters) {
      this.loading = true

      let promises = []
      resourceIds.forEach((d, index) => {
        promises.push(
          this.loadMetrics(
            d,
            this.details.alarm.id,
            this.details.alarm.resource.siteId,
            this.currMntNLastMntRanges,
            true
          )
        )
      })
      Promise.all(promises)
        .then(d => {
          Object.assign(this.assetMetricMap, d)
          subMeters.forEach(meter => {
            if (this.assetMetricMap[meter.id]) {
              let energyByCddThisMonth = this.assetMetricMap[meter.id]
                .energyByCdd[this.currentMonth]
                ? this.assetMetricMap[meter.id].energyByCdd[this.currentMonth]
                    .value
                : null
              let energyByCddLastMonth = this.assetMetricMap[meter.id]
                .energyByCdd[this.lastMonth]
                ? this.assetMetricMap[meter.id].energyByCdd[this.lastMonth]
                    .value
                : null
              let wastage = this.assetMetricMap[meter.id].wastage[
                this.currentMonth
              ]
              let lMWastage = this.assetMetricMap[meter.id].wastage[
                this.lastMonth
              ]
              let anomalyThisMonth = this.assetMetricMap[meter.id]
                .noofanomalies[this.currentMonth]
                ? this.assetMetricMap[meter.id].noofanomalies[this.currentMonth]
                : 0
              let anomalyLastMonth = this.assetMetricMap[meter.id]
                .noofanomalies[this.lastMonth]
                ? this.assetMetricMap[meter.id].noofanomalies[this.lastMonth]
                : 0
              let data = {
                anomalyThisMonth: anomalyThisMonth,
                anomalyLastMonth: anomalyLastMonth,
                energyByCdd:
                  energyByCddThisMonth > 0
                    ? energyByCddThisMonth.toFixed(2)
                    : 0,
                energyByCddLastMonth:
                  energyByCddLastMonth > 0
                    ? energyByCddLastMonth.toFixed(2)
                    : 0,
                projectedWastage: wastage > 0 ? wastage.toFixed(2) : 0,
                lastMProjectedWastage: lMWastage > 0 ? lMWastage.toFixed(2) : 0,
              }
              if (data.projectedWastage > 10000) {
                data.projectedWastage =
                  (data.projectedWastage / 1000).toFixed(2) + ' MWh'
              } else {
                data.projectedWastage += ' kWh'
              }
              if (data.lastMProjectedWastage > 10000) {
                data.lastMProjectedWastage =
                  (data.lastMProjectedWastage / 1000).toFixed(2) + ' MWh'
              } else {
                data.lastMProjectedWastage += ' kWh'
              }
              Object.assign(meter, data)
            }
          })
          this.subMeterDetails = subMeters
        })
        .finally(() => (this.loading = false))
    },
  },
}
</script>
<style lang="scss">
.fc-anamaloy-metertable-page {
  .el-table_1_column_2_column_3:nth-child(1) .cell,
  .el-table_1_column_5_column_6:nth-child(3) .cell,
  .el-table_1_column_8_column_9:nth-child(5) .cell {
    font-size: 12px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.5px;
    color: #39b2c2 !important;
  }
  .el-table_1_column_2_column_4:nth-child(2) .cell,
  .el-table_1_column_5_column_7:nth-child(4) .cell,
  .el-table_1_column_8_column_10:nth-child(6) .cell {
    font-size: 12px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.5px;
    color: #8ca1ad !important;
  }
}
</style>
