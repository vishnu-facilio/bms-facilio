<template>
  <div class="p30 d-flex flex-direction-column">
    <div class="flex-middle justify-content-space">
      <div class="f13 bold text-uppercase fc-black-13 text-left">
        {{ $t('asset.maintenance.recently_close_wo') }}
      </div>
      <div>
        <i class="el-icon-success f24 fc-green-color"></i>
      </div>
    </div>
    <div
      v-if="$validation.isEmpty(workorder)"
      class="fc-black-13 pT50 text-center"
    >
      <InlineSvg
        src="svgs/emptystate/scheduled"
        iconClass="icon text-center icon-xxxlg"
      ></InlineSvg>
      <div class="empty-grey-desc2">
        {{ $t('asset.maintenance.no_recently_close_pm') }}
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
        <div class="d-flex flex-direction-row mB15">
          <div class="mT5 mR15 summary-widget-icon-bg orange">
            <InlineSvg
              src="svgs/calendar"
              iconClass="icon icon-lg fc-white-color text-center pL3 vertical-baseline"
            ></InlineSvg>
          </div>
          <div>
            <p class="m0 bold" v-if="workorder.actualWorkEnd">
              {{ workorder.actualWorkEnd | formatDate }}
            </p>
            <p v-else class="m0 bold">---</p>
            <div class="f12" style="color: #8ca1ad;">Closed On</div>
          </div>
        </div>
        <div class="d-flex flex-direction-row mB15">
          <user-avatar
            size="md"
            :user="workorder.assignedTo"
            :group="workorder.assignmentGroup"
            :showPopover="true"
            :showLabel="true"
            moduleName="workorder"
          ></user-avatar>
        </div>
        <div class="d-flex flex-direction-row mB15">
          <div class="mT5 mR15 summary-widget-icon-bg coral">
            <InlineSvg
              src="svgs/clock"
              iconClass="icon icon-sm fc-white-color mT4"
            ></InlineSvg>
          </div>
          <div>
            <p class="m0 bold">{{ formattedResolveTime }}</p>
            <div class="f12" style="color: #8ca1ad;">Time Taken to Resolve</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    UserAvatar,
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
      workorder: {},
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
        })
      )
      let url = `/v2/workorders/view/closed?filters=${filters}&page=1&perPage=1&orderBy=actualWorkEnd&orderType=desc&overrideViewOrderBy=true&includeParentFilter=true`
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
      if (isEmpty(this.workorder.actualWorkDuration)) {
        return '00:00 Hrs'
      } else {
        return this.$helpers.getFormattedDuration(
          this.workorder.actualWorkDuration,
          'seconds'
        )
      }
    },
  },
}
</script>
