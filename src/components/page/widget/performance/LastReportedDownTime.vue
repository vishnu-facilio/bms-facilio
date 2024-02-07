<template>
  <div class="p30 pT35 pB10 d-flex">
    <div class="f16 bold" v-if="!$validation.isEmpty(downtime)">
      {{ downtime.condition || 'No reason specified.' }}
    </div>
    <div class="f16 bold fc-black-dark" v-else>
      {{ $t('asset.performance.no_downtime_reported_yet') }}
    </div>
    <div
      class="mT35 d-flex flex-direction-row justify-content-space"
      style="max-width: 475px;"
    >
      <div class="d-flex flex-direction-column">
        <span class="bold">{{
          downtime.fromtime
            ? $options.filters.formatDate(downtime.fromtime)
            : '---'
        }}</span>
        <span class="f12 text-fc-grey mT5">{{
          $t('asset.performance.from')
        }}</span>
      </div>
      <div class="d-flex flex-direction-column">
        <span class="bold">{{
          downtime.totime ? $options.filters.formatDate(downtime.totime) : '---'
        }}</span>
        <span class="f12 text-fc-grey mT5">{{
          $t('asset.performance.to')
        }}</span>
      </div>
      <div class="d-flex flex-direction-column">
        <span class="bold">{{
          $helpers.getFormattedDuration(downtime.duration, 'seconds')
        }}</span>
        <span class="f12 text-fc-grey mT5">{{
          $t('asset.performance.duration')
        }}</span>
      </div>
    </div>
    <!-- action area for widget-title-section-->
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name">
          {{ $t('asset.performance.last_reported_downtime') }}
        </div>
      </div>
    </portal>
    <!-- action area -->
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
    'eventBus',
  ],
  data() {
    return {
      downtime: {},
    }
  },
  mounted() {
    this.loadRecentDownTime()
    this.eventBus.$on('asset-downtime-reported', () => {
      this.loadRecentDownTime()
    })
  },
  methods: {
    loadRecentDownTime() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'Recent Downtime',
              moduleName: 'assetbreakdown',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'asset',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                },
              },
              orderByFieldName: 'fromtime',
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
            this.downtime = !isEmpty(response.data.result.workflowResult)
              ? response.data.result.workflowResult
              : {}
          } else {
            // TODO handle errors
            this.downtime = {}
          }
        })
        .catch(() => {
          // TODO handle errors
          this.downtime = {}
        })
    },
  },
}
</script>
