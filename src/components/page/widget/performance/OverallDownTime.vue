<template>
  <div class="p30 pT35 pB10 d-flex">
    <div v-if="$validation.isEmpty(totalCount)" class="f16 bold fc-black-dark">
      {{ $t('asset.performance.no_downtimes_reported') }}
    </div>
    <div v-else class="f16 bold">
      {{ totalCount }} {{ $t('asset.performance.reported_downtime_ytd') }}
    </div>
    <div class="mT35 d-flex flex-direction-row items-center">
      <div class="d-flex flex-direction-column">
        <span class="f22">
          {{ currentMonthDuration || '00:00 Hrs' }}
          <!-- <span class="f12">Hrs</span> -->
        </span>
        <span class="f12 text-fc-grey mT5">{{
          $t('common.reports.rangeCategory.THIS_YEAR')
        }}</span>
      </div>
      <div class="mL40" v-if="lastMonthDuration">
        <span class="bold mR5">{{ lastMonthDuration }}</span>
        <span>{{ $t('asset.performance.till_last_month') }}</span>
      </div>
    </div>
    <!-- action area for widget-title-section-->
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name">
          {{ $t('asset.performance.overall_downtime') }}
        </div>
        <a
          v-if="!decommission"
          class="f13"
          @click="showAssetBreakdown = true"
          >{{ $t('asset.performance.report_downtime') }}</a
        >
      </div>
    </portal>
    <!-- action area -->
    <asset-breakdown
      v-if="showAssetBreakdown"
      :visibility.sync="showAssetBreakdown"
      :assetBDSourceDetails="breakdownDetails"
      @onSave="updateBreakdownStats"
    ></asset-breakdown>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import AssetBreakdown from '@/AssetBreakdown'

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
  components: { AssetBreakdown },
  data() {
    return {
      totalCount: null,
      currentMonthDuration: null,
      lastMonthDuration: null,
      showAssetBreakdown: false,
      breakdownDetails: {},
    }
  },
  computed: {
    decommission() {
      return this.$getProperty(this, 'details.decommission', false)
    },
  },
  created() {
    this.resetBreakdownDetails()
  },
  mounted() {
    this.loadCount()
    this.loadDurationTillLastMonth()
    this.loadTotalDurationForCurrentYear()
  },
  methods: {
    resetBreakdownDetails() {
      this.breakdownDetails = {
        condition: '',
        assetid: this.details.id,
        sourceId: this.details.id,
        sourceType: 3,
      }
    },
    executeWorkflow(params) {
      return this.$http.post('/v2/executeworkflow', params).then(response => {
        if (response.data.responseCode === 0) {
          return response.data.result.workflowResult
        } else {
          return null
        }
      })
    },

    loadCount() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'Number Of Downtimes',
              fieldName: 'duration',
              moduleName: 'assetbreakdown',
              aggregateString: 'count',
              criteria: {
                pattern: '(1 and 2)',
                conditions: {
                  '1': {
                    fieldName: 'asset',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                  '2': {
                    fieldName: 'fromTime',
                    operatorId: 44,
                    sequence: '2',
                  },
                },
              },
            },
          ],
        },
      }
      this.executeWorkflow(params)
        .then(result => {
          this.totalCount = !isEmpty(result) && result !== 0 ? result : null
        })
        .catch(() => (this.totalCount = null))
    },

    loadDurationTillLastMonth() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'Duration Till Last Month',
              fieldName: 'duration',
              moduleName: 'assetbreakdown',
              aggregateString: 'sum',
              criteria: {
                pattern: '(1 and 2)',
                conditions: {
                  '1': {
                    fieldName: 'asset',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                  '2': {
                    fieldName: 'fromTime',
                    operatorId: 80,
                    sequence: '2',
                  },
                },
              },
            },
          ],
        },
      }
      this.executeWorkflow(params)
        .then(result => {
          this.lastMonthDuration = !isEmpty(result)
            ? this.$helpers.getFormattedDuration(result, 'seconds')
            : null
        })
        .catch(() => (this.lastMonthDuration = null))
    },

    loadTotalDurationForCurrentYear() {
      let params = {
        workflow: {
          expressions: [
            {
              name: 'Total Duration For Current Year',
              fieldName: 'duration',
              moduleName: 'assetbreakdown',
              aggregateString: 'sum',
              criteria: {
                pattern: '(1 and 2)',
                conditions: {
                  '1': {
                    fieldName: 'asset',
                    operatorId: 36,
                    sequence: '1',
                    value: this.details.id,
                  },
                  '2': {
                    fieldName: 'fromTime',
                    operatorId: 44,
                    sequence: '2',
                  },
                },
              },
            },
          ],
        },
      }
      this.executeWorkflow(params)
        .then(result => {
          this.currentMonthDuration = !isEmpty(result)
            ? this.$helpers.getFormattedDuration(result, 'seconds')
            : null
        })
        .catch(() => (this.currentMonthDuration = null))
    },

    updateBreakdownStats() {
      this.eventBus.$emit('asset-downtime-reported')
      this.loadCount()
      this.loadDurationTillLastMonth()
      this.loadTotalDurationForCurrentYear()
    },
  },
}
</script>
