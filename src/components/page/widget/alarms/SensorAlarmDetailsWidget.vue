<template>
  <div>
    <div class="alarm-summary-graph-header">
      <div class="fc-black-16 bold">
        {{ $t('alarm.sensor_alarm.tabular_report') }}
      </div>
    </div>
    <div>
      <div class="height100 overflow-y-scroll">
        <el-table
          :data="recordsToIterate"
          style="width: 100%"
          height="300"
          :fit="true"
          class="fc-sensor-alarm-table fc-table-widget-scroll"
        >
          <el-table-column
            prop="subject"
            :label="$t('alarm.sensor_alarm.alarms')"
            width="260"
          >
            <template v-slot="alarm">
              <div>
                {{ alarm.row.subject }}
              </div>
              <div class="op6" v-if="!$validation.isEmpty(alarm.row.ruleInfo)">
                ({{ alarm.row.ruleInfo }})
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="lastCreatedTimeString"
            :label="$t('alarm.sensor_alarm.previos_occurrence')"
            width="190"
          >
            <template v-slot="alarm">
              <div>
                {{ alarm.row.lastCreatedTimeString }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="id"
            :label="$t('alarm.sensor_alarm.total_duration')"
            width="170"
          >
            <template slot="header">
              <div>
                {{ $t('alarm.sensor_alarm.total_duration') }}
              </div>
              <div class="op6">(Last 12 months)</div>
            </template>
            <template v-slot="alarm">
              <div>
                {{ alarm.row.totalDuration }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" width="260">
            <template slot="header">
              <div>
                {{ $t('alarm.sensor_alarm.average_frequency_failure') }}
              </div>
              <div class="op6">(Last 12 months)</div>
            </template>
            <template v-slot="alarm">
              <div>
                {{ alarm.row.averageFrequencyFailure }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'details'],
  data() {
    return {
      relatedAlarms: [],
      duration: {},
      recordsToIterate: [],
      meterTableData: [
        {
          subject: 'Decremental meter readings',
          lastCreatedTimeString: 'No occurrence found',
          totalDuration: 'Not enough data',
          averageFrequencyFailure: 'Not enough data',
          type: 4,
          ruleInfo: null,
        },
        {
          subject: 'No change in value',
          lastCreatedTimeString: 'No occurrence found',
          totalDuration: 'Not enough data',
          averageFrequencyFailure: 'Not enough data',
          type: 5,
          ruleInfo: null,
        },
        {
          subject: 'Abnormal energy reading',
          lastCreatedTimeString: 'No occurrence found',
          totalDuration: 'Not enough data',
          averageFrequencyFailure: 'Not enough data',
          type: 6,
          ruleInfo: null,
        },
      ],
      nonMeterTableData: [
        {
          subject: 'No change in value',
          lastCreatedTimeString: 'No occurrence found',
          totalDuration: 'Not enough data',
          averageFrequencyFailure: 'Not enough data',
          type: 1,
          ruleInfo: null,
        },
        {
          subject: 'Out of range',
          lastCreatedTimeString: 'No occurrence found',
          totalDuration: 'Not enough data',
          averageFrequencyFailure: 'Not enough data',
          type: 2,
          ruleInfo: null,
        },
      ],
    }
  },
  mounted() {
    this.fetchRelatedAlarms()
  },
  methods: {
    fetchRelatedAlarms() {
      let { details } = this
      let { id, alarm } = details
      let { readingFieldId } = alarm
      let url = `/v2/alarms/fetchRelatedAlarms`
      let params = {
        alarmId: id,
      }
      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error || 'Error Occured')
        } else {
          let { relatedAlarms } = data
          this.relatedAlarms = relatedAlarms
          if (!isEmpty(relatedAlarms)) {
            for (let i = 0; i < relatedAlarms.length; i++) {
              if (readingFieldId > 0) {
                this.nonMeterTableData.forEach(data => {
                  if (data.type === relatedAlarms[i].sensorRuleType) {
                    data.lastCreatedTimeString =
                      relatedAlarms[i].lastCreatedTimeString
                    data.totalDuration = this.getDuration(
                      relatedAlarms[i].totalDuration,
                      false
                    )
                    data.averageFrequencyFailure = this.getDuration(
                      relatedAlarms[i].averageFrequencyFailure,
                      true
                    )
                    if (relatedAlarms[i].sensorRuleType === 1) {
                      data.ruleInfo =
                        'More than ' +
                        relatedAlarms[i].sensorRule.rulePropInfo.timeInterval +
                        ' hours'
                    } else if (relatedAlarms[i].sensorRuleType === 2) {
                      data.ruleInfo =
                        'Range is ' +
                        relatedAlarms[i].sensorRule.rulePropInfo.lowerLimit +
                        ' to ' +
                        relatedAlarms[i].sensorRule.rulePropInfo.upperLimit
                    }
                  }
                })
              } else {
                this.meterTableData.forEach(data => {
                  if (data.type === relatedAlarms[i].sensorRuleType) {
                    data.lastCreatedTimeString =
                      relatedAlarms[i].lastCreatedTimeString
                    data.totalDuration = this.getDuration(
                      relatedAlarms[i].totalDuration,
                      false
                    )
                    data.averageFrequencyFailure = this.getDuration(
                      relatedAlarms[i].averageFrequencyFailure,
                      true
                    )
                    if (relatedAlarms[i].sensorRuleType === 5) {
                      data.ruleInfo =
                        'More than ' +
                        relatedAlarms[i].sensorRule.rulePropInfo.timeInterval +
                        ' hours'
                    } else if (relatedAlarms[i].sensorRuleType === 6) {
                      data.ruleInfo =
                        'Greater than ' +
                        relatedAlarms[i].sensorRule.rulePropInfo
                          .averageBoundPercentage +
                        '%'
                    }
                  }
                })
              }
            }
          }
        }
      })
      if (readingFieldId > 0) {
        this.recordsToIterate = this.nonMeterTableData
      } else {
        this.recordsToIterate = this.meterTableData
      }
    },
    getDuration(duration, avgFreq) {
      if (duration > 0) {
        let days = Math.floor(duration / 86400)
        let hours = (duration / 3600).toFixed(2)
        let mnts = Math.floor(duration / 60)
        if (days > 0.99) {
          return Math.round(days) + (days > 1 ? ' days' : ' day')
        } else if (hours > 0.99) {
          return Math.round(hours) + (hours > 1 ? ' hours' : ' hour')
        } else if (mnts > 0.99) {
          return Math.round(mnts) + (mnts > 1 ? ' minutes' : ' minute')
        }
      } else if (avgFreq) {
        return 'Not enough data'
      }
      return 0
    },
  },
}
</script>
<style lang="scss">
.fc-sensor-alarm-table {
  table {
    width: 100% !important;
  }
  th {
    height: 42px;
    background-color: #f1f4fa !important;
    color: #324056 !important;
    border-top: none !important;
    border-left: none !important;
    font-weight: bold !important;
    border-left: 1px solid #d0d9e2 !important;
    border-bottom: 1px solid #d0d9e2 !important;
  }
  th:last-child {
    border-right: none !important;
  }
  th:first-child {
    border-left: none !important;
  }
  td {
    border-left: 1px solid #d0d9e2;
    border-bottom: 1px solid #d0d9e2;
    text-align: center;
    color: rgb(0, 0, 0) !important;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1px;
  }
  td:last-child {
    border-right: none;
  }
  td:first-child {
    border-left: none !important;
  }
  .cell {
    text-align: left;
  }
}
</style>
