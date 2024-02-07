<template>
  <div class="p30 d-flex flex-direction-column asset-sum-empty-block">
    <div class="f13 bold text-uppercase mB10 fc-black-13 text-left">
      {{ $t('asset.maintenance.workorders') }}
    </div>
    <div class="d-flex flex-direction-row pT10">
      <div class="d-flex flex-direction-column mR60">
        <div class="f45 fc-black-com pointer" @click="goToWoList('open')">
          {{ openWoCount || 0 }}
        </div>
        <div class="f13 text-uppercase bold">
          {{ $t('asset.maintenance.open') }}
        </div>
      </div>
      <div class="d-flex flex-direction-column mL30">
        <div class="f45 fc-black-com pointer" @click="goToWoList('overdue')">
          {{ overdueWoCount || 0 }}
        </div>
        <div class="f13 text-uppercase bold text-coral">
          {{ $t('asset.maintenance.overdue') }}
        </div>
      </div>
    </div>
    <div class="bold mb5 mT40 label-txt-black">
      {{ $t('asset.maintenance.on_time_completion') }}
    </div>
    <div
      class="fc__wo__task__bar fc__wo__task__bar-stacked fc-wo-widget-statusbar mR10"
    >
      <span
        class="fc__task__gradient__bar mL0"
        :style="'width:' + ((onTimeValue || 0) / 100) * 100 + '%'"
      ></span>
    </div>
    <div v-if="!$validation.isEmpty(onTimeValue)" class="f30 mT15">
      {{ onTimeValue.toFixed(2) }}%
    </div>
    <div v-else class="f13 mT15" style="color: rgb(140, 161, 173)">
      {{ $t('common._common.no_data_available') }}
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
    this.loadWoOnTimeCompletionDetails()
  },
  data() {
    return {
      openWoCount: null,
      overdueWoCount: null,
      onTimeValue: null,
    }
  },
  methods: {
    goToWoList(viewName) {
      let filters = {
        resource: [{ operatorId: 36, value: [`${this.details.id}`] }],
      }
      let query = {
        search: JSON.stringify(filters),
        includeParentFilter: true,
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname: viewName },
            query,
          })
        }
      } else {
        this.$router.push({
          path: `/app/wo/orders/${viewName}`,
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
      let countHash = {
        openWoCount: `/v2/workorders/view/open?count=true&filters=${filters}&includeParentFilter=true`,
        overdueWoCount: `/v2/workorders/view/overdue?count=true&filters=${filters}&includeParentFilter=true`,
      }
      Object.keys(countHash).forEach(countName => {
        this.$http
          .get(countHash[countName])
          .then(response => {
            if (response.data.responseCode === 0) {
              this[countName] = !isEmpty(response.data.result.workorderscount)
                ? response.data.result.workorderscount
                : null
            } else {
              // TODO handle errors
              this[countName] = null
            }
          })
          .catch(() => {
            // TODO handle errors
            this[countName] = null
          })
      })
    },
    loadWoOnTimeCompletionDetails() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'a',
              fieldName: 'dueDate',
              moduleName: 'workorder',
              aggregateString: 'count',
              criteria: {
                pattern: '(1 and 2)',
                conditions: {
                  '1': {
                    fieldName: 'resource',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                  '2': {
                    fieldName: 'actualWorkEnd',
                    operatorId: 18,
                    sequence: '2',
                    value: 'DUE_DATE',
                  },
                },
              },
            },
            {
              name: 'b',
              fieldName: 'id',
              moduleName: 'workorder',
              aggregateString: 'count',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'resource',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                },
              },
            },
          ],
          resultEvaluator: '(a/b)*100',
        },
      }
      this.$http
        .post('/v2/executeworkflow', params)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.onTimeValue = !isEmpty(response.data.result.workflowResult)
              ? response.data.result.workflowResult
              : null
          } else {
            // TODO handle errors
            this.onTimeValue = null
          }
        })
        .catch(() => {
          // TODO handle errors
          this.onTimeValue = null
        })
    },
  },
}
</script>
<style scoped></style>
