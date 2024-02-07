<template>
  <div class="p30 d-flex flex-direction-column">
    <div class="flex-middle justify-content-space">
      <div class="f13 bold text-uppercase text-uppercase fc-black-13 text-left">
        {{ $t('asset.maintenance.next_scheduled_pm') }}
      </div>
      <InlineSvg src="svgs/flag" iconClass="icon icon-xl"></InlineSvg>
    </div>
    <div
      v-if="$validation.isEmpty(workorder)"
      class="empty-grey-13 pT50 text-center"
    >
      <InlineSvg
        src="svgs/emptystate/scheduled"
        iconClass="icon text-center icon-xxxlg"
      ></InlineSvg>
      <div class="empty-grey-desc2">
        {{ $t('asset.maintenance.no_scheduled_pm') }}
      </div>
    </div>
    <template v-else>
      <div
        class="fc-black-13 pT15 text-left pointer"
        @click="goToSummary(workorder.id)"
      >
        {{ workorder.subject }}
      </div>
      <div class="mT25 pointer" @click="goToSummary(workorder.id)">
        <div class="d-flex flex-direction-row mB15 flex-middle">
          <div class="mR15 summary-widget-icon-bg light-green">
            <InlineSvg
              src="svgs/calendar"
              iconClass="icon icon-lg fc-white-color text-center pL3 vertical-baseline"
            ></InlineSvg>
          </div>
          <div>
            <p class="fc-black3-16 bold" style="margin-bottom: 2px;">
              {{ workorder.scheduledStart | formatDate }}
            </p>
            <div class="f12" style="color: #8ca1ad;">
              {{ $t('asset.maintenance.scheduled_on') }}
            </div>
          </div>
        </div>
        <div class="d-flex flex-direction-row mT5 mB15">
          <user-avatar
            size="md"
            :user="workorder.assignedTo"
            :group="workorder.assignmentGroup"
            :showPopover="true"
            :showLabel="true"
            moduleName="workorder"
          ></user-avatar>
        </div>
        <div class="d-flex flex-direction-row mB15 flex-middle">
          <div class="mR15 summary-widget-icon-bg dark-green">
            <InlineSvg
              src="svgs/clock"
              iconClass="icon icon-sm fc-white-color mT4"
            ></InlineSvg>
          </div>
          <div>
            <p class="m0 bold">{{ formattedResolveTime }}</p>
            <div class="f12" style="color: #8ca1ad;">
              {{ $t('asset.maintenance.est_resolve_time') }}
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import InlineSvg from '@/InlineSvg'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    UserAvatar,
    InlineSvg,
  },
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'activeTab',
    'widget',
    'resizeWidget',
  ],
  data() {
    return {
      workorder: null,
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
          sourceType: { operatorId: 9, value: ['5'] },
          createdTime: { operatorId: 73 },
        })
      )
      let url = `/v2/workorders/view/all?filters=${filters}&fetchAllType=true&orderBy=createdTime&orderType=asc&overrideViewOrderBy=true&page=1&perPage=1`
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.workorder = response.data.result.workorders
            ? response.data.result.workorders[0]
            : {}
        } else {
          // TODO handle errors
          this.workorder = {}
          this.loading = false
        }
      })
    },

    goToSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname: 'all', id },
          })
        }
      } else {
        this.$router.push({ path: `/app/wo/orders/summary/${id}` })
      }
    },
  },
  computed: {
    formattedResolveTime() {
      if (
        isEmpty(this.workorder) ||
        isEmpty(this.workorder.dueDate) ||
        isEmpty(this.workorder.scheduledStart)
      ) {
        return '---'
      } else {
        let duration = moment.duration(
          moment(this.workorder.dueDate).diff(
            moment(this.workorder.scheduledStart)
          )
        )
        return this.$helpers.getFormattedDuration(duration, null, true)
      }
    },
  },
}
</script>
