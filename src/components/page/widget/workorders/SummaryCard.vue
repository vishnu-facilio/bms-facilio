<template>
  <div class="p30 d-flex flex-direction-column">
    <div class="bold text-uppercase mB10 fc-black-13 text-left letter-spacing1">
      {{ $t('asset.maintenance.open_workorders') }}
    </div>
    <div class="f45 fc-black-com pointer">
      <div class="inline" @click="goToView">{{ openWoCount || 0 }}</div>
    </div>
    <hr class="separator-line width100" />
    <div class="f13 fc-blue-label mT10 text-capitalize">
      {{ $t('asset.maintenance.next_scheduled_pm') }}
    </div>
    <div
      v-if="$validation.isEmpty(workorder)"
      class="mT15 empty-grey-13 text-left"
    >
      {{ $t('asset.maintenance.no_scheduled_pm') }}
    </div>
    <template v-else>
      <div class="mT15 fc-black-13 text-left">{{ workorder.subject }}</div>
      <div class="fc-black-15 bold mT5">
        {{ workorder.scheduledStart | formatDate }}
      </div>
      <div class="d-flex flex-direction-row mT20 mB15">
        <user-avatar
          size="md"
          :user="workorder.assignedTo"
          :group="workorder.assignmentGroup"
          :showPopover="true"
          :showLabel="true"
          moduleName="workorder"
        ></user-avatar>
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
      openWoCount: null,
      workorder: {},
    }
  },
  mounted() {
    this.loadCount()
    this.loadData()
  },
  methods: {
    loadCount() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
        })
      )
      let url = `/v2/workorders/view/open?count=true&filters=${filters}&includeParentFilter=true`
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.openWoCount = !isEmpty(response.data.result.workorderscount)
              ? response.data.result.workorderscount
              : null
          } else {
            // TODO handle errors
            this.openWoCount = null
          }
        })
        .catch(() => {
          // TODO handle errors
          this.openWoCount = null
        })
    },
    loadData() {
      let filters = encodeURIComponent(
        JSON.stringify({
          resource: { operatorId: 36, value: [this.details.id + ''] },
          sourceType: { operatorId: 9, value: ['5'] },
          createdTime: { operatorId: 73 },
        })
      )
      let url = `/v2/workorders/view/all?filters=${filters}&fetchAllType=true&orderBy=createdTime&orderType=asc&overrideViewOrderBy=true&page=1&perPage=1`
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.workorder = response.data.result.workorders
              ? response.data.result.workorders[0]
              : {}
          } else {
            // TODO handle errors
            this.workorder = {}
          }
        })
        .catch(() => {
          // TODO handle errors
          this.workorder = {}
        })
    },
    goToView() {
      let filters = {
        resource: [{ operatorId: 36, value: [`${this.details.id}`] }],
      }
      let query = { includeParentFilter: true, search: JSON.stringify(filters) }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname: 'open' },
            query,
          })
        }
      } else {
        this.$router.push({
          path: '/app/wo/orders/open',
          query,
        })
      }
    },
  },
}
</script>
<style scoped></style>
