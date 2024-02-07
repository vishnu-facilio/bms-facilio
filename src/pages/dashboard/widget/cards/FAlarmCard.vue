<template>
  <div class="dragabale-card booleancard">
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div v-else>
      <div class="booleancard-header">
        <div>
          <div class="headerText">{{ cardData.name }}</div>
          <new-date-picker
            ref="newDatePicker"
            :zone="$timezone"
            class="filter-field date-filter-comp inline"
            :dateObj="configData.dateFilter"
            @date="setDateFilter"
          ></new-date-picker>
        </div>
      </div>
      <div v-if="chartloading" class="no-data-center2">
        <spinner :show="true" size="80"></spinner>
      </div>
      <div
        class="no-data-center"
        v-else-if="!(resources !== null && resources.length > 0)"
      >
        {{ $t('panel.tyre.no_data') }}
      </div>

      <div v-else class="pT20">
        <div v-for="(rca, index) in resources" :key="index" class="clearboth">
          <div class="width100" v-if="rca">
            <el-row class="flex-middle pL20 pR20 width100 pB10">
              <el-col :span="12">
                <div class="fc-black-com bold text-left f14 text-capitalize">
                  {{ rca.subject ? rca.subject : '---' }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="flex-middle justify-content-end">
                  <div class="fc-black3-13 text-capitalize bold">
                    <span class="fc-grey2 text-uppercase f11">{{
                      $t('panel.tyre.alarms')
                    }}</span>
                    {{ rca.count ? rca.count : '---' }}
                  </div>
                  <el-divider
                    direction="vertical"
                    class="mL20 mR20"
                  ></el-divider>
                  <div class="fc-black3-13 text-capitalize bold">
                    <span class="fc-grey2 text-uppercase f11">{{
                      $t('panel.tyre.duration')
                    }}</span>
                    {{
                      rca.duration
                        ? $helpers.getFormattedDuration(rca.duration)
                        : '---'
                    }}
                  </div>
                </div>
              </el-col>
            </el-row>
            <div class="alarm-bar-cell">
              <alarm-bar
                class="fc-widget-alarbar"
                :isResize="resize"
                ref="multiChart"
                :parentId="rca.resource.id"
                :sourceKey="'parentId'"
                :dateOperator="operatorId"
              ></alarm-bar>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import NewDateHelper from '@/mixins/NewDateHelper'
import AlarmBar from '@/AlarmBar'
import NewDatePicker from '@/NewDatePicker'
export default {
  mixins: [NewDataFormatHelper, AnalyticsMixin, NewDateHelper],
  props: ['widget', 'config'],
  components: {
    AlarmBar,
    NewDatePicker,
  },
  computed: {
    operatorId() {
      return this.widget.dataOptions.cardData
        ? this.widget.dataOptions.cardData.carddata.periodId
        : this.widget.dataOptions.paramsJson.dateOperator
    },
  },
  data() {
    return {
      data: null,
      cardData: null,
      resources: null,
      loading: false,
      resize: false,
      nodata: false,
      chartloading: false,
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      configData: {
        height: 100,
        width: 100,
        dateFilter: NewDateHelper.getDatePickerObject(22),
      },
    }
  },
  watch: {
    'config.height': {
      handler(newData, oldData) {
        let self = this
        setTimeout(function() {
          self.resize = !self.resize
          // self.$refs['multiChart'].render()
        }, 100)
      },
    },
  },
  mounted() {
    //this.loadAlarmInsight()
    this.loadCardData()
  },
  methods: {
    loadAlarmInsight() {
      this.loading = true
      let url = `/v2/alarms/insights`
      let paramUrl = {}
      paramUrl.resourceList = this.widget.dataOptions.cardData
        ? this.widget.dataOptions.cardData.selectedObj
        : this.widget.dataOptions.paramsJson.parentIds
      if (
        this.localDateFormat.findIndex(
          rt => rt === this.configData.dateFilter.operatorId
        ) > -1
      ) {
        paramUrl.dateOperator = this.configData.dateFilter.operatorId
      } else {
        paramUrl.dateOperator = 20
        paramUrl.dateOperatorValue =
          this.configData.dateFilter.value[0] +
          ',' +
          this.configData.dateFilter.value[1]
      }
      // paramUrl.dateOperator = this.configData.dateFilter.operatorId
      this.$http.post(url, paramUrl).then(response => {
        if (response.data.responseCode === 0) {
          this.resources = response.data.result.alarms
          this.loading = false
          this.chartloading = false
        }
      })
    },
    loadCardData(data) {
      let self = this
      let params = null
      if (this.widget.id > -1) {
        params = {
          widgetId: self.widget.id,
        }
      } else {
        params = {
          paramsJson: {
            parentIds: this.widget.dataOptions.cardData.selectedObj,
            dateOperator: this.widget.dataOptions.cardData.carddata.periodId,
          },
          staticKey: 'alarmbarwidget',
        }
        let data = {
          params: params.paramsJson,
          cardata: this.widget.dataOptions.cardData,
        }
        this.widget.dataOptions.metaJson = JSON.stringify(data)
      }
      this.chartloading = true
      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.updateCall(response.data.cardResult)
          self.updateMetadata()
          self.loading = false
          self.chartloading = false
          self.nodata = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          if (error) {
            self.loading = false
            self.chartloading = false
            self.nodata = false
          }
        })
    },
    updateCall(params) {
      let data = params.paramsJson
      this.configData.dateFilter = NewDateHelper.getDatePickerObject(
        data.dateOperator,
        data.dateValue
      )
      this.loadAlarmInsight()
    },
    updateMetadata() {
      this.cardData = JSON.parse(
        this.widget.dataOptions.metaJson
      ).cardata.carddata
    },
    setDateFilter(dateFilter) {
      this.configData.dataOptions = dateFilter
      this.configData.dateFilter = dateFilter
      let paramsJson = {}
      if (this.widget.id > -1) {
        paramsJson.parentId = this.widget.dataOptions.paramsJson.parentId
        if (
          this.localDateFormat.findIndex(rt => rt === dateFilter.operatorId) >
          -1
        ) {
          paramsJson.dateOperator = dateFilter.operatorId
        } else {
          paramsJson.dateOperator = 20
          paramsJson.dateValue = dateFilter.value[0] + ',' + dateFilter.value[1]
        }
      } else {
        paramsJson.parentId = JSON.parse(
          this.widget.dataOptions.metaJson
        ).params.parentId
        paramsJson.dateOperator = dateFilter.operatorId
      }
      let params = {
        paramsJson: paramsJson,
        staticKey: 'alarmbarwidget',
      }
      this.loadAlarmInsight(params)
    },
  },
}
</script>
<style>
.booleancard .f-multichart-print {
  display: none;
}
.booleancard .bb-axis-x-label {
  display: none;
}
.booleancard .date-filter-comp {
  position: absolute;
  right: 0;
  top: 0;
  padding-right: 0;
}
.booleancard-chart {
  position: relative;
  top: 50px;
  border-top: 1px solid #eae8e8;
}
.booleancard .headerText {
  font-size: 1.1em;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: rgb(47, 46, 73);
  padding-bottom: 0px;
}
.alarm-spereator {
  padding: 20px;
}
.tooltip-alarm-header {
  display: inline-flex;
  padding: 10px;
}
.tooltip-alarm-newtitle {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.15;
  letter-spacing: 0.4px;
  color: #324056;
}

.tooltip-alarm-subtitle {
  font-size: 11px;
  color: #8ca1ad;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.15;
  letter-spacing: 0.4px;
  padding-top: 5px;
}
.alarm-spereator {
  padding: 20px;
  padding-top: 0;
  font-size: 25px;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  color: #c0c2c6;
}
.booleancard-chart .fc-alarms-chart-title {
  right: 0;
  display: none;
}
.booleancard-chart .bb .bb-axis-x .tick text,
.booleancard-chart .bb .bb-axis-y .tick text,
.booleancard-chart .bb .bb-axis-y2 .tick text {
  font-size: 10px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  fill: #324056;
}
.fc-widget .booleancard-chart .f-multichart {
  padding: 0px;
}
.booleancard-chart .fc-alarms-chart.pdf-chart.bb {
  position: absolute !important;
  padding-top: 20px;
}
.no-data-center {
  position: absolute;
  top: 50%;
  left: 45%;
}
.no-data-center2 {
  position: absolute;
  top: 30%;
  left: 45%;
}
.booleancard .mobile-new-date-filter {
  position: relative;
  top: 10px;
  bottom: 73px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
  opacity: 0.8;
  white-space: nowrap;
}
.fc-widget-alarbar .text-fc-grey {
  padding-left: 0 !important;
}
.booleancard .booleancard-chart {
  /* margin-left: 20px !important;
  margin-right: 20px !important; */
}
.booleancard .fc-widget-alarbar {
  margin-bottom: 20px;
}
/* .booleancard .fc-widget-alarbar .fc-alarms-chart svg{
  width: 100%;
} */
.booleancard-header {
  padding: 15px;
  height: 48px;
  border: none;
  border-bottom: 1px solid #eae8e8;
}
</style>
