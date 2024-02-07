<template>
  <div>
    <div v-if="loading" class="flex-middle fc-empty-white block">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else-if="!time.length" class="block">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="pT20 fc-black-dark f18 bold">No Anomaly!</div>
      </div>
    </div>
    <el-table
      v-else
      :data="time"
      style="width: 100%"
      class="anamolay-metrics-table"
    >
      <el-table-column fixed prop="month" label="TIMESTAMP"></el-table-column>
      <el-table-column label="NO OF OCCURRENCES" width="200p">
        <template v-slot="time">
          <div class="table-subheading">
            {{ metrics.noofanomalies[time.row.month] }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="ENERGY/CDD (kWh/CDD)" width="200">
        <template v-slot="time">
          <div class="table-subheading">
            {{
              $getProperty(metrics.energyByCdd[time.row.month], 'value')
                ? $getProperty(
                    metrics.energyByCdd[time.row.month],
                    'value'
                  ).toFixed(2)
                : '0'
            }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="EXCEEDED ENERGY">
        <template v-slot="time">
          <div class="table-subheading">
            {{
              metrics.wastage && metrics.wastage[time.row.month]
                ? getFormattedValue(metrics.wastage[time.row.month])
                : 0 + ' kWh'
            }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="DEVIATION (%)">
        <template v-slot="time">
          <div class="table-subheading">
            {{
              metrics.deviation && metrics.deviation[time.row.month]
                ? metrics.deviation[time.row.month].toFixed(2)
                : 0
            }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="MTTC">
        <template v-slot="time">
          <div class="table-subheading">
            {{
              metrics.mttc && metrics.mttc[time.row.month]
                ? getDuration(metrics.mttc[time.row.month])
                : 0 + ' Hrs'
            }}
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  props: ['details', 'widget', 'tab'],
  data() {
    return {
      activeName: 'first',
      time: [],
      metrics: null,
      loading: false,
    }
  },
  mounted() {
    this.loadData(this.details.alarm.resource.id)
  },
  methods: {
    loadData(resourceId) {
      this.loading = true
      let url = `/v2/mlAnomalyAlarm/metrics?alarmId=${this.details.alarm.id}&resourceId=${resourceId}&siteId=${this.details.alarm.resource.siteId}`
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.metrics = response.data.result.metrics
          if (this.metrics.time) {
            this.time = this.metrics.time.map(time => ({ month: time }))
          }
        }
        this.loading = false
      })
    },
    getDuration(value) {
      let data = ''
      let duration = this.$helpers.getDuration(value, 'seconds')
      for (let key in duration) {
        data += ' ' + duration[key] + ' ' + key
      }
      return data
    },
    getFormattedValue(val) {
      if (val > 10000) {
        val = val / 1000
        val = val.toFixed(2)
        val += ' MWh'
      } else {
        val = val.toFixed(2)
        val += ' kWh'
      }
      return val
    },
  },
}
</script>

<style lang="scss">
.anamolay-metrics-table {
  .el-table--enable-row-transition .el-table__body td,
  th.is-leaf {
    padding-left: 30px;
  }
  td {
    padding-left: 30px;
  }
}
.anomalies-metrics-page {
  .el-table td {
    padding-left: 30px;
  }
  .el-table th {
    padding-left: 30px;
  }
  .el-tabs__item {
    font-size: 11px;
  }
  .el-table th > .cell {
    padding-left: 20px;
  }
}
.anomalies-metrics-page-table {
  overflow: scroll;

  table {
    width: 100%;
    overflow-x: scroll;
  }

  thead {
    border: 0;
    border-bottom: 1px solid #e6e6e6;
  }

  th {
    padding: 15px 0;
    font-size: 11px;
    font-weight: 500;
    text-transform: uppercase;
    line-height: normal;
    letter-spacing: 1px;
    min-width: 100px;
    color: #324056;
  }

  td {
    min-width: 100px;
  }

  .el-tree-node__expand-icon {
    display: none;
  }
}
</style>
