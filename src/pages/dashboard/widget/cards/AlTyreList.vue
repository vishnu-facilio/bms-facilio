<template>
  <div class="dragabale-card height100 fcu-list-view">
    <div class="fcu-table-header row">
      <div class="fcu-table-name-new col-6">{{ $t('panel.tyre.FCU') }}</div>
      <div class="fcu-table-name-new-1 col-6">
        {{ $t('panel.tyre.total') }}
        {{ data.length }}
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{
          $t('panel.tyre.running')
        }}
        {{ data.length ? data.filter(rt => rt.fanStatus > 1).length : 0 }}
      </div>
    </div>
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div v-else>
      <div class="fcu-list-container">
        <table>
          <tr
            class="fcu-list-header"
            style="background: #fff;border-bottom: solid 1px #e6ecf3;"
          >
            <th>
              <div>{{ $t('panel.tyre.id') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.tyre.name') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.tyre.location') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.tyre.fan') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.tyre.temp_return') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.tyre.set') }}</div>
            </th>
          </tr>
          <tbody>
            <tr v-for="(list, index) in data" :key="index">
              <td class="fcu-td">
                <div
                  class="fc-id fcu-id pointer"
                  @click="redirectToAsset(list.id)"
                >
                  <div class="fc-id fcu-id" v-if="list.id">#{{ list.id }}</div>
                </div>
              </td>
              <td class="fcu-td pointer" @click="redirectToAsset(list.id)">
                <div class="fcu-name" v-if="list.name">
                  {{ list.name }}
                </div>
              </td>
              <td class="fcu-td pointer">
                <div
                  class="fcu-spacename pointer"
                  v-if="list.spaceName"
                  @click="openInSpace(list.spaceId)"
                >
                  {{ list.spaceName }}
                </div>
              </td>
              <td class="fcu-td">
                <div
                  class="fcu-runstatus pointer"
                  v-if="list.fanStatus !== null"
                  @click="openInAnalytics(list.id, 'runStatusField')"
                >
                  <span v-if="list.fanStatus === 1" class="low-value">LOW</span>
                  <span v-else-if="list.fanStatus === 2" class="medium-value">{{
                    $t('panel.tyre.medium')
                  }}</span>
                  <span v-else-if="list.fanStatus === 3" class="high-value">{{
                    $t('panel.tyre.high')
                  }}</span>
                  <span v-else-if="list.fanStatus < 1" class="off-value">{{
                    $t('panel.tyre.off')
                  }}</span>
                </div>
              </td>
              <td class="fcu-td">
                <div
                  class="fcu-Rt pointer"
                  v-if="list.roomTemperature"
                  @click="openInAnalytics(list.id, 'roomTemperature')"
                >
                  {{ list.roomTemperature.toFixed(2)
                  }}<span v-html="'&degC'"></span>
                </div>
              </td>
              <td class="fcu-td">
                <div
                  class="fcu-vf pointer"
                  v-if="list.setPointTemp"
                  @click="openInAnalytics(list.id, 'setPointTempField')"
                >
                  {{ list.setPointTemp.toFixed(2)
                  }}<span v-html="'&degC'"></span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import JumpToHelper from '@/mixins/JumpToHelper'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  props: ['widget', 'config'],
  mixins: [JumpToHelper],
  data() {
    return {
      data: [],
      cardData: null,
      loading: false,
      buildingList: [],
      fields: {},
      selectedBuilding: null,
      listLevel: 'Level 1',
      lists: [
        {
          label: 'Level 1',
          value: 'Level 1',
        },
        {
          label: 'Level 2',
          value: 'Level 2',
        },
        {
          label: 'Level 3',
          value: 'Level 3',
        },
      ],
    }
  },
  mounted() {
    this.loadCardData()
    this.updateMeta()
    this.loadBuilding()
  },
  computed: {},
  methods: {
    selectlevel() {
      this.loadCardData(this.listLevel)
    },
    updateMeta() {
      if (this.widget.dataOptions.cardData) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.cardData
        )
        this.cardData = this.widget.dataOptions.cardData
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.cardData = JSON.parse(this.widget.dataOptions.metaJson)
      }
    },
    openInAnalytics(id) {
      if (id && !this.$mobile) {
        let datePicker = {
          operatorId: 22,
          value: null,
        }
        let dataPoint = {}
        let dataPoints = []
        let self = this
        Object.keys(this.fields).forEach(function(field) {
          dataPoint = {
            parentId: id,
            yAxis: {
              fieldId: self.fields[field].id,
              aggr: 3,
            },
          }
          dataPoints.push(dataPoint)
        })

        let analyticsConfig = {
          analyticsType: 2,
          mode: 1,
          period: 0,
          dateFilter: NewDateHelper.getDatePickerObject(
            datePicker.operatorId,
            datePicker.value
          ),
          dataPoints: dataPoints,
        }
        if (!this.$mobile) {
          if (isWebTabsEnabled()) {
            let { name } = findRouteForTab(pageTypes.ANALYTIC_BUILDING) || {}

            if (name) {
              this.$router.push({
                name,
                query: { filters: JSON.stringify(analyticsConfig) },
              })
            }
          } else {
            this.$router.push({
              path: '/app/em/analytics/building',
              query: { filters: JSON.stringify(analyticsConfig) },
            })
          }
        }
      }
    },
    openInSpace(id) {
      if (id && !this.$mobile) {
        this.jumpToBuilding(this.$store.getters.getSpace(id))
      }
    },
    loadCardData(key) {
      let self = this
      let params = null
      if (this.widget && this.widget.id > -1) {
        params = {
          widgetId: self.widget.id,
        }
      } else {
        params = {
          workflow: {
            expressions:
              this.widget.dataOptions.data &&
              this.widget.dataOptions.data.expressions
                ? this.widget.dataOptions.data.expressions
                : [],
            workflowUIMode: 1,
          },
          staticKey: 'kpiCard',
        }
      }

      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getCardData(response.data)
          self.updateMeta(response.data)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    getCardData(data) {
      if (data && data.cardResult) {
        this.data =
          data.cardResult &&
          data.cardResult.result &&
          data.cardResult.result.resultList
            ? data.cardResult.result.resultList
            : []
        this.fields['controlvalvecommand'] =
          data.cardResult && data.cardResult.result.controlvalvecommand
            ? data.cardResult.result.controlvalvecommand
            : null
        this.fields['runStatusField'] =
          data.cardResult && data.cardResult.result.runStatusField
            ? data.cardResult.result.runStatusField
            : null
        this.fields['roomTemperature'] =
          data.cardResult && data.cardResult.result.roomTemperature
            ? data.cardResult.result.roomTemperature
            : null
        this.fields['setPointTempField'] =
          data.cardResult && data.cardResult.result.setPointTempField
            ? data.cardResult.result.setPointTempField
            : null
      }
    },
    redirectToAsset(id) {
      if (id) {
        if (!this.$mobile) {
          if (isWebTabsEnabled()) {
            let { name } =
              findRouteForTab(pageTypes.OVERVIEW, { moduleName: 'asset' }) || {}

            if (name) {
              this.$router.push({
                name,
                params: { viewname: 'all', id },
              })
            }
          } else {
            this.$router.push({
              path: '/app/at/assets/all/' + id + '/overview',
            })
          }
        }
      }
    },
    loadBuilding() {
      let self = this
      const formData = new FormData()
      let url = '/report/alarms/getAllBuildings'
      self.$http.post(url, formData).then(function(response) {
        self.buildingList = response.data
      })
    },
  },
}
</script>
<style>
.fcu-list-container th,
.fcu-list-container td {
  padding: 10px;
}
.fcu-list-container th {
  font-size: 11px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1px;
  color: #324056;
  text-transform: uppercase;
  white-space: nowrap;
  padding-bottom: 15px;
  padding-top: 15px;
}
td.fcu-td {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 40px;
  letter-spacing: 0.6px;
  color: #324056;
  white-space: nowrap;
}
.fcu-list-view {
  overflow: auto;
  height: 100%;
}
.low-value {
  font-size: 12px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: right;
  color: #46a6d8;
}
.medium-value {
  font-size: 12px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: right;
  color: #33bb9d;
}
.high-value {
  font-size: 12px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: right;
  color: #f3bd47;
}
.off-value {
  font-size: 12px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: right;
  color: #e41f1f;
}
.fcu-id {
  /* padding-bottom: 7px; */
}
.fcu-name {
  font-weight: 500;
  min-width: 200px;
}
.fcu-list-header {
  border-bottom: solid 1px #e6ecf3;
}
.fcu-list-header tr {
  background: #fff !important;
}
.fcu-list-container table {
  width: 100%;
}
.fcu-table-name-new {
  font-size: 13px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: left;
  color: #324056;
  text-transform: uppercase;
  justify-content: left;
  align-items: left;
  padding-top: 17px;
  padding-left: 20px;
}
.fcu-table-name-new-1 {
  font-size: 13px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: right;
  color: #324056;
  text-transform: uppercase;
  justify-content: right;
  align-items: right;
  padding-top: 17px;
  padding-left: 20px;
  padding-right: 20px;
}
.fcu-table-header {
  height: 50px;
  border-bottom: solid 1px #f6f6f7;
}
.fcu-select-level .el-input .el-input__inner,
.fcu-select-level .el-textarea .el-textarea__inner {
  border: 0px;
}
.fcu-select-level {
  padding-top: 10px;
  justify-content: center;
  padding-right: 80px;
}
/* .fcu-select-level i.el-select__caret.el-input__icon.el-icon-arrow-up {
    position: relative !important;
    top: 5px !important;
    height: 20px !important;
} */
.fcu-list-container tr:nth-child(odd) {
  background-color: #fbfbfb;
}
.fcu-list-container tr th:first-child,
.fcu-list-container tr td:first-child {
  padding-left: 20px;
}
.fcu-list-container tr th:last-child,
.fcu-list-container tr td:last-child {
  padding-right: 20px;
}
.fcu-list-container .el-icon-arrow-up:before {
  position: relative !important;
  bottom: -2px !important;
}
.fcu-list-container
  i.el-select__caret.el-input__icon.el-icon-arrow-up.is-reverse {
  position: relative !important;
  top: -4px !important;
}
.fcu-list-container
  i.el-select__caret.el-input__icon.el-icon-arrow-up.is-reverse {
  position: relative !important;
  top: -2px !important;
}
.fcu-select-level .el-input__icon {
  line-height: 22px !important;
}
.mobile-dashboard-container .fcu-table-name {
  text-align: left;
}
.mobile-dashboard-container .fcu-table-header.row > .col-10 {
  max-width: 66.6667%;
  flex: 0 0 66.6667%;
}
.mobile-dashboard-container .fcu-table-header.row > .col-2 {
  max-width: 33.3333%;
  flex: 0 0 33.3333%;
}
.mobile-dashboard-container .fcu-select-level {
  padding-top: 5px;
  padding-right: 20px;
}
</style>
