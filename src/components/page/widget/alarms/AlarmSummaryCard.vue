<template>
  <div class="p30 d-flex flex-direction-column">
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      {{ $t('common._common.fdd_alarms') }}
    </div>
    <div class="f45 fc-black-com pointer">
      <div @click="goToView('fdd')">{{ activeAlarmCount || 0 }}</div>
    </div>
    <hr class="separator-line width100 mT60" />
    <div
      class="f12 bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
    >
      {{ $t('common._common.bms_alarms') }}
    </div>
    <div class="f45 fc-black-com pointer">
      <div @click="goToView('bms')">{{ activeBmsAlarmCount || 0 }}</div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'

export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'activeTab',
    'widget',
    'resizeWidget',
  ],
  mounted() {
    this.loadCount()
    this.loadAlarmRuleDatails()
    this.loadBmsAlarmCount()
  },
  data() {
    return {
      activeAlarmCount: null,
      alarm: null,
      alarmDuration: null,
      activeBmsAlarmCount: null,
    }
  },
  methods: {
    goToView(type) {
      let filters = {
        resource: [{ operatorId: 36, value: [`${this.details.id}`] }],
      }
      let query = {
        includeParentFilter: true,
        search: JSON.stringify(filters),
      }

      let path = '/app/fa/faults/active'
      if (type === 'bms') {
        path = '/app/fa/bmsalarms/bmsActive'
      }
      if (isWebTabsEnabled()) {
        let moduleName = this.$helpers.isLicenseEnabled('NEW_ALARMS')
          ? 'newreadingalarm'
          : 'alarm'
        let viewname = 'active'
        if (type === 'bms') {
          moduleName = 'bmsalarm'
          viewname = 'bmsActive'
        }

        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname },
            query,
          })
        }
      } else {
        this.$router.push({
          path: path,
          query,
        })
      }
    },
    loadCount() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
        })
      )
      let url = ''
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        url = `/v2/readingalarms/view/active?fetchCount=true&filters=${filters}&includeParentFilter=true`
      } else {
        url = `/v2/alarms/view/active?isCount=true&filters=${filters}&includeParentFilter=true`
      }
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.activeAlarmCount = !isEmpty(response.data.result.count)
              ? response.data.result.count
              : null
          } else {
            // TODO handle errors
            this.activeAlarmCount = null
          }
        })
        .catch(() => {
          // TODO handle errors
          this.activeAlarmCount = null
        })
    },
    loadBmsAlarmCount() {
      let queryParam = {
        viewName: 'bmsActive',
        includeParentFilter: true,
        moduleName: 'bmsalarm',
      }
      queryParam.filters = JSON.stringify({
        resource: { operatorId: 36, value: [this.details.id + ''] },
      })
      let url = ''
      url = `/v3/modules/data/count`
      API.get(url, queryParam).then(response => {
        let { error, data } = response || {}
        if (isEmpty(error)) {
          let { count } = data || {}
          this.activeBmsAlarmCount = count
        }
      })
    },
    loadAlarmRuleDatails() {
      let workFlowId
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        workFlowId = 5
      } else {
        workFlowId = 6
      }
      this.$util
        .getdefaultWorkFlowResult(workFlowId, this.details.id)
        .then(response => {
          this.loading = false
          this.alarm = !isEmpty(response.commonRule) ? response.commonRule : ''
          if (response.duration > 0) {
            this.alarmDuration = response.durationInSeconds
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    loadCommonAlarmDetails() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'commonAlarmDetails',
              fieldName: 'subject',
              moduleName: 'alarm',
              criteria: {
                pattern: '(1)',
                conditions: {
                  1: {
                    fieldName: 'resource',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                },
              },
              groupBy: 'entityId',
              orderByFieldName: 'count(*)',
              sortBy: 'desc',
              limit: 1,
            },
          ],
        },
      }

      this.$http
        .post('/v2/executeworkflow', params)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.alarm = !isEmpty(response.data.result.workflowResult)
              ? response.data.result.workflowResult[0]
              : ''
          } else {
            // TODO handle errors
            this.alarm = null
          }
        })
        .catch(() => {
          // TODO handle errors
          this.alarm = null
        })
    },
    loadCommonAlarmDuration() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'a',
              fieldName: 'clearedTime',
              moduleName: 'alarm',
              aggregateString: 'sum',
              criteria: {
                pattern: '(1 and 2 and 3)',
                conditions: {
                  1: {
                    fieldName: 'resource',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                  2: {
                    fieldName: 'clearedTime',
                    operatorId: 2,
                    sequence: '2',
                  },
                  3: {
                    fieldName: 'createdTime',
                    operatorId: 28,
                    sequence: '3',
                  },
                },
              },
            },
            {
              name: 'b',
              fieldName: 'createdTime',
              moduleName: 'alarm',
              aggregateString: 'sum',
              criteria: {
                pattern: '(1 and 2 and 3)',
                conditions: {
                  1: {
                    fieldName: 'resource',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                  2: {
                    fieldName: 'clearedTime',
                    operatorId: 2,
                    sequence: '2',
                  },
                  3: {
                    fieldName: 'createdTime',
                    operatorId: 28,
                    sequence: '3',
                  },
                },
              },
            },
          ],
          resultEvaluator: '(a-b)/1000',
        },
      }

      this.$http
        .post('/v2/executeworkflow', params)
        .then(response => {
          if (response.data.responseCode === 0) {
            if (!isEmpty(response.data.result.workflowResult)) {
              this.alarmDuration = response.data.result.workflowResult
            } else {
              this.alarmDuration = null
            }
          } else {
            // TODO handle errors
            this.alarmDuration = null
          }
        })
        .catch(() => {
          // TODO handle errors
          this.alarmDuration = null
        })
    },
  },
}
</script>
<style scoped></style>
