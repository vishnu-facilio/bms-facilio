<template>
  <div>
    <div style="padding: 6px 15px">
      <div
        style="padding-bottom: 20px;"
        v-if="config && config.isAutoRefreshEnabled"
      >
        <div style="display: inline; padding-right: 5px;">
          Auto Refresh every
        </div>
        <el-select
          style="width: 80px;"
          v-model="refreshInterval"
          :allow-create="true"
          :filterable="true"
        >
          <el-option :value="0" label="None"></el-option>
          <el-option :value="5" label="5"></el-option>
          <el-option :value="10" label="10"></el-option>
          <el-option :value="15" label="15"></el-option>
          <el-option :value="30" label="30"></el-option>
          <el-option :value="45" label="45"></el-option>
          <el-option :value="60" label="60"></el-option>
        </el-select>
        minutes
      </div>
      <table class="report-settings-table" v-if="showComboChartSettings">
        <tr v-for="(serie, index) in series" :key="index">
          <td>
            <div
              class="report-setting-table-content"
              v-tippy
              :title="serie && serie.title ? serie.title : ''"
            >
              {{ serie.title }}
            </div>
          </td>
          <td>
            <el-radio-group v-model="serie.chartType" size="small">
              <el-radio-button
                v-if="$account.org.id === 88"
                :title="
                  index > 0
                    ? 'You can select Bar type for only base data point.'
                    : ''
                "
                v-tippy
                data-arrow="true"
                label="Bar"
              ></el-radio-button>
              <el-radio-button
                v-else-if="index === 0"
                :title="
                  index > 0
                    ? 'You can select Bar type for only base data point.'
                    : ''
                "
                v-tippy
                data-arrow="true"
                label="Bar"
              ></el-radio-button>
              <el-radio-button label="Line"></el-radio-button>
              <el-radio-button label="Area"></el-radio-button>
            </el-radio-group>
          </td>
          <td>
            <el-color-picker
              v-model="serie.reportColor"
              size="mini"
            ></el-color-picker>
          </td>
        </tr>
      </table>
    </div>
    <div class="row mT30">
      <div class="el-report-cancel-btn col-6 fc-el-btn" @click="cancel">
        cancel
      </div>
      <div class="el-report-save-btn fc-el-btn col-6" @click="save">apply</div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['report', 'config'],
  data() {
    return {
      refreshInterval: 0,
      series: [],
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    showComboChartSettings() {
      return (
        this.report &&
        this.report.data.length >= 2 &&
        !this.report.options.is_highres_data
      )
    },
  },
  watch: {
    report: {
      handler: function(newVal, oldVal) {
        this.init()
      },
      deep: true,
    },
  },
  methods: {
    init() {
      if (this.report) {
        let seriesList = []
        for (let d of this.report.data) {
          seriesList.push({
            id: d.options.id,
            title: d.title,
            chartType: this.$helpers.capitalize(d.options.type),
            reportColor: d.options.color,
          })
        }
        this.series = seriesList
      }

      if (
        this.config &&
        this.config.widget &&
        this.config.widget.dataOptions.refresh_interval >= 120
      ) {
        // converting seconds to minutes
        this.refreshInterval =
          this.config.widget.dataOptions.refresh_interval / 60
      }
    },
    cancel() {
      this.$emit('close', true)
    },
    save() {
      if (this.config && this.config.isAutoRefreshEnabled) {
        let widgetId = this.config.widget.id
        let interval = 0
        if (this.refreshInterval > 0) {
          interval = this.refreshInterval * 60 // converting minute to seconds
        }
        this.$http.post('dashboard/updateRefreshInterval', {
          widgetId: widgetId,
          refreshInterval: interval,
        })

        if (this.config.intervalUpdate) {
          this.config.intervalUpdate(interval)
        }
      }

      if (this.showComboChartSettings) {
        for (let i = 0; i < this.report.data.length; i++) {
          let d = this.report.data[i]
          d.options.type = this.series[i].chartType.toLowerCase()
          d.options.color = this.series[i].reportColor
        }

        if (this.report.options.id > 0) {
          this.$http.post('dashboard/updateComboChart', {
            comboChartList: this.series,
          })
        }

        this.$emit('save', true)
      } else {
        this.$emit('close', true)
      }
    },
  },
}
</script>

<style>
.report-settings .el-popover__title {
  padding: 22px 14px 0;
}
</style>
