<template>
  <div class="publish-container">
    <div v-if="isPlannerListLoading" class="width100">
      <div class="d-flex mT50 planner-body">
        <spinner :show="isPlannerListLoading"></spinner>
      </div>
    </div>
    <template v-else>
      <div :class="publishDescClass">
        <i
          v-if="canShowWarning"
          class="fa fa-exclamation-triangle mR10 f18"
          aria-hidden="true"
        ></i>
        {{ publishDescription }}
      </div>
      <div class="publish-sub-container">
        <div class="section-heading mB10 mT20">
          {{ $t('maintenance._workorder.summary') }}
        </div>
        <div
          v-if="$validation.isEmpty(plannerList)"
          class="planner-empty-state pm-content-width"
        >
          <inline-svg
            :src="`svgs/emptystate/readings-empty`"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="empty-state-txt">
            {{ $t('maintenance.pm.no_planner') }}
          </div>
        </div>
        <div v-else class="planner-list-container">
          <div
            v-for="planner in plannerList"
            :key="planner.id"
            class="summary-item-container"
          >
            <div class="summary-header">{{ planner.name }}</div>

            <div class="summary-container">
              <div class="summary-field-container mR50">
                <span class="summary-field-label"
                  >{{ $t('maintenance.pm.trigger_frequency') }}:
                </span>
                <span class="summary-field-value">
                  {{ getFrequency(planner) }}</span
                >
              </div>

              <div class="summary-field-container">
                <span class="summary-field-label"
                  >{{ getFormattedText($t('maintenance.pm.resource_count')) }}:
                </span>
                <span class="summary-field-value">
                  {{ getResourceCount(planner) }}</span
                >
              </div>
            </div>
            <div
              class="planner-error-msg"
              v-if="!$validation.isEmpty(getErrorMessage(planner))"
            >
              {{ getErrorMessage(planner) }}
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { getPlaceholderText, FREQUENCY_HASH } from '../utils/pm-utils.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['pmProps', 'disableButton'],
  data: () => ({
    plannerList: [],
    isPlannerListLoading: false,
    moduleName: 'pmPlanner',
    pmRecord: {},
    canShowWarning: false,
  }),
  created() {
    this.init()
  },
  computed: {
    publishDescClass() {
      let { canShowWarning } = this
      return !canShowWarning ? 'publish-description' : 'publish-warning'
    },
    publishDescription() {
      let { canShowWarning } = this
      return canShowWarning
        ? this.$t('maintenance.pm.publish_warning_msg')
        : this.$t('maintenance.pm.publish_desc')
    },
  },
  methods: {
    async init() {
      await this.loadPm()
      this.loadPlanners()
    },

    async loadPm() {
      this.isPlannerListLoading = true
      let moduleName = 'plannedmaintenance'
      let { pmProps } = this || {}
      let { id } = pmProps
      let { [moduleName]: data, error } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force: true }
      )
      if (isEmpty(error)) {
        this.pmRecord = data
      } else {
        this.$message.error(
          error.message || 'Error occured while fetching planned maintenance'
        )
      }
      this.isPlannerListLoading = false
    },
    getFrequency(planner) {
      let schedule = this.$getProperty(planner, 'trigger.schedule')
      let facilioFrequency = this.$getProperty(
        this,
        '$constants.FACILIO_FREQUENCY'
      )
      if (!isEmpty(schedule)) {
        schedule = JSON.parse(schedule)
        let { frequencyType } = schedule || {}

        if (
          ['MONTHLY_DATE', 'MONTHLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 3
        } else if (
          ['QUARTERLY_DATE', 'QUARTERLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 4
        } else if (
          ['HALF_YEARLY_DATE', 'HALF_YEARLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 5
        } else if (
          ['ANNUALLY_DATE', 'ANNUALLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 6
        }

        if (facilioFrequency[frequencyType]) {
          return facilioFrequency[frequencyType]
        } else {
          return frequencyType
        }
      }
      return this.$t('maintenance.pm.not_defined')
    },
    getResourceCount(planner) {
      let { resourceCount } = planner || {}
      return resourceCount || 0
    },
    getErrorMessage(planner) {
      let plannerfrequency = this.getFrequency(planner)
      let resourceCount = this.getResourceCount(planner)

      if (plannerfrequency === 'Not Defined' && !resourceCount) {
        return this.getFormattedText(
          this.$t('maintenance.pm.planner_trigger_empty'),
          false,
          true
        )
      } else if (plannerfrequency === 'Not Defined') {
        return this.$t('maintenance.pm.planner_freq_empty')
      } else if (!resourceCount) {
        return this.getFormattedText(
          this.$t('maintenance.pm.planner_resource_empty'),
          false,
          true
        )
      }
      return ''
    },
    async loadPlanners() {
      this.isPlannerListLoading = true
      let { moduleName, pmProps } = this || {}
      let { id } = pmProps || {}
      let params = {}
      if (!isEmpty(id)) {
        let filters = JSON.stringify({
          pmId: { operatorId: 9, value: [`${id}`] },
        })
        params = { ...params, filters }
      }
      let { list, error } = await API.fetchAll(moduleName, params, {
        force: true,
      })
      if (isEmpty(error)) {
        let promises = list.map(planner => {
          return this.loadPlanner(planner)
        })
        let result = await Promise.all(promises)
        this.plannerList = result
        await this.canShowWarningBanner()
      }
      this.isPlannerListLoading = false
    },
    async loadPlanner(planner) {
      let { moduleName } = this || {}
      let { id } = planner || {}
      let { [moduleName]: record, error } = await API.fetchRecord(
        moduleName,
        {
          id,
        },
        { force: true }
      )
      if (isEmpty(error)) return { ...planner, ...record }
      else return planner
    },
    canShowWarningBanner() {
      let { plannerList } = this

      plannerList.forEach(planner => {
        let canShowWrng = false
        let { trigger, resourceCount = null } = planner || {}
        let { schedule } = trigger || {}

        canShowWrng = isEmpty(schedule) || isEmpty(resourceCount)
        if (canShowWrng) {
          this.canShowWarning = canShowWrng
          this.$emit('update:disableButton', canShowWrng)
          return
        }
      })
    },
    async onSave() {
      let { pmProps, viewname } = this || {}
      let { id: pmId } = pmProps || {}

      let { error } = await API.post('/v3/plannedmaintenance/publish', { pmId })
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error occured')
      } else {
        this.$message.success(this.$t('maintenance.pm.published_successfully'))

        let params = {
          id: pmId,
          viewname: !isEmpty(viewname) ? viewname : 'all',
        }
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('plannedmaintenance', pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params,
            })
        } else {
          this.$router.push({
            name: 'pm-summary',
            params,
          })
        }
      }
    },
    onCancel() {
      let { $route } = this || {}
      let { query } = $route || {}
      this.$router.push({ path: '', query: { ...query, tab: 'planner' } })
    },
    getFormattedText(text, isUpperCase, isSingular) {
      let { pmRecord } = this || {}
      return getPlaceholderText({ pmRecord, text, isUpperCase, isSingular })
    },
  },
}
</script>

<style lang="scss" scoped>
.planner-list-container {
  height: calc(100vh - 310px);
  overflow: scroll;
}
.publish-container {
  height: calc(100vh - 210px);
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.publish-sub-container {
  width: 50%;
}
.publish-description {
  background-color: #0478b9;
  color: #ffffff;
  font-weight: 500;
  width: 100%;
  display: flex;
  padding: 13px 0px;
  justify-content: center;
}
.publish-warning {
  color: #3d3d3d;
  font-weight: 500;
  background-color: #ffab00;
  width: 100%;
  display: flex;
  padding: 13px 0px;
  justify-content: center;
}
.summary-item-container {
  background-color: #f5f5f5;
  padding: 20px;
  margin-bottom: 15px;
}
.summary-header {
  color: #324056;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}
.summary-container {
  display: flex;
  justify-content: space-between;
  width: 80%;
}
.summary-field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.summary-field-value {
  padding-left: 6px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
.planner-error-msg {
  color: red;
  font-size: 13px;
  padding-top: 10px;
}
</style>
