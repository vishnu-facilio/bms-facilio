<template>
  <div>
    <div v-if="isLoading" class="mT100">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.workorder') }}
          </div>
          <div class="f11 bold text-fc-grey">
            {{ $t('common.date_picker.last30days') }}
          </div>
        </div>
      </el-row>
      <el-row class="mT10">
        <el-col :span="8">
          <div
            class="bold f22 flex justify-center"
            v-bind:class="{ 'show-link': isValidCount(totalOpenWorkorders) }"
            @click="redirectToList('open', totalOpenWorkorders)"
          >
            {{ totalOpenWorkorders || '---' }}
          </div>
          <div class="f14 mT10 flex justify-center">
            {{ $t('common.header.open') }}
          </div>
        </el-col>
        <el-col :span="8">
          <div
            class="bold f22 flex justify-center"
            v-bind:class="{ 'show-link': isValidCount(totalOverdueWorkorders) }"
            @click="redirectToList('overdue', totalOverdueWorkorders)"
          >
            {{ totalOverdueWorkorders || '---' }}
          </div>
          <div class="f14 mT10 flex justify-center overdue-warning">
            {{ $t('common.header.overdue') }}
          </div>
        </el-col>
        <el-col :span="8">
          <div
            class="bold f22 flex justify-center"
            v-bind:class="{ 'show-link': isValidCount(totalWorkordersCount) }"
            @click="redirectToList('all', totalWorkordersCount)"
          >
            {{ totalWorkordersCount || '---' }}
          </div>
          <div class="f14 mT10 flex justify-center">
            {{ $t('common.header.total') }}
          </div>
        </el-col>
      </el-row>
      <el-row>
        <div class="mT20 line-hr"></div>
      </el-row>
      <el-row class="mT30 tenant-wo-url mL20">
        <div>
          <div
            class="flex-middle f14 bold text-uppercase fc-black-13 text-center"
          >
            <img
              src="~assets/svgs/tenant/success_green.svg"
              class="mR10"
              style="width:20px;height:20px"
            />
            {{ $t('common._common.recently_closed_workorder') }}
          </div>
          <div
            :title="
              lastClosedWorkorderId
                ? 'Open Workorder'
                : 'No recently closed workorder'
            "
            class="textoverflow-ellipsis pR20 mL30 f14 pT10 fc-black-13 text-left"
            @click="redirectToOverview(lastClosedWorkorderId)"
          >
            {{ lastClosedWorkorder }}
          </div>
        </div>
      </el-row>
      <el-row class="tenant-wo-url mT40 mL20">
        <div>
          <div
            class="flex-middle f14 bold text-uppercase fc-black-13 text-center"
          >
            <img
              src="~assets/svgs/tenant/clock_previous_black.svg"
              class="mR10"
              style="width:20px;height:20px"
            />
            {{ $t('common._common.most_overdue_workorder') }}
          </div>
          <div
            :title="
              mostOverdueWorkorderId ? 'Open Workorder' : 'No overdue workorder'
            "
            class="textoverflow-ellipsis pR20 mL30 f14 pT10 fc-black-13 text-left"
            @click="redirectToOverview(mostOverdueWorkorderId)"
          >
            {{ mostOverdueWorkorder }}
          </div>
        </div>
      </el-row>
    </div>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details'],
  data() {
    return {
      lastClosedWorkorder: '---',
      mostOverdueWorkorder: '---',
      mostOverdueWorkorderId: null,
      lastClosedWorkorderId: null,
      totalWorkordersCount: null,
      totalOpenWorkorders: null,
      totalOverdueWorkorders: null,
      isLoading: false,
    }
  },
  components: { Spinner },
  async created() {
    this.isLoading = true
    await Promise.all([
      this.getOpenWorkordersCount(),
      this.getOverdueWorkordersCount(),
      this.getLastClosedWorkorder(),
      this.getTotalWorkordersCount(),
    ])
    this.isLoading = false
  },
  computed: {
    getAvatarUrl() {
      let { details } = this
      let { tenant } = details || {}
      let { avatarUrl } = tenant || {}
      return avatarUrl
    },
    getTenantName() {
      let { details } = this
      let { tenant } = details || {}
      let { name } = tenant || {}
      return name || '---'
    },
    getTenantPhone() {
      let { details } = this
      let { tenant } = details || {}
      let { primaryContactPhone } = tenant || {}
      return primaryContactPhone || '---'
    },
    getTenantEmail() {
      let { details } = this
      let { tenant } = details || {}
      let { primaryContactEmail } = tenant || {}
      return primaryContactEmail || '---'
    },
    getTime30DaysFromNow() {
      return (this.$helpers.getOrgMoment().valueOf() - 2592000000).toString()
    },
  },
  methods: {
    isValidCount(value) {
      if (value && value !== null && value > 0) {
        return true
      }
      return false
    },
    async getOpenWorkordersCount() {
      let queryParam = {}
      queryParam = {
        withCount: true,
        viewName: 'open',
        includeParentFilter: true,
        moduleName: 'workorder',
      }
      queryParam.filters = JSON.stringify({
        resource: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      })
      let { meta, error } = await API.get('/v3/modules/data/list', queryParam)
      if (!error) {
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        this.totalOpenWorkorders = totalCount || 0
      }
    },
    async getTotalWorkordersCount() {
      let queryParam = {}
      queryParam = {
        withCount: true,
        viewName: 'all',
        includeParentFilter: true,
        moduleName: 'workorder',
      }
      queryParam.filters = JSON.stringify({
        resource: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      })
      let { meta, error } = await API.get('/v3/modules/data/list', queryParam)
      if (!error) {
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        this.totalWorkordersCount = totalCount || 0
      }
    },
    async getOverdueWorkordersCount() {
      let queryParam = {}
      queryParam = {
        withCount: true,
        viewName: 'overdue',
        includeParentFilter: true,
        moduleName: 'workorder',
        orderBy: 'dueDate',
        orderType: 'asc',
      }
      queryParam.filters = JSON.stringify({
        resource: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      })
      let { data, meta, error } = await API.get(
        '/v3/modules/data/list',
        queryParam
      )
      if (!error) {
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        this.totalOverdueWorkorders = totalCount || 0

        let { workorder } = data || {}
        let { subject, id } = workorder.length ? workorder[0] : {}
        this.mostOverdueWorkorder = subject || '---'
        this.mostOverdueWorkorderId = id
      }
    },
    async getLastClosedWorkorder() {
      let queryParam = {}
      queryParam = {
        withCount: true,
        viewName: 'closed',
        includeParentFilter: true,
        moduleName: 'workorder',
        orderBy: 'createdTime',
        orderType: 'desc',
        page: 1,
        perPage: 1,
      }
      queryParam.filters = JSON.stringify({
        resource: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      })
      let { data, error } = await API.get('/v3/modules/data/list', queryParam)
      if (!error) {
        let { workorder } = data || {}
        let { subject, id } = workorder.length ? workorder[0] : {}
        this.lastClosedWorkorder = subject || '---'
        this.lastClosedWorkorderId = id
      }
    },
    redirectToOverview(id) {
      if (!this.$validation.isEmpty(id)) {
        let route
        let params = { id: id, viewname: 'all' }

        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

          if (name) {
            route = this.$router.resolve({
              name,
              params,
            }).href
          }
        } else {
          route = this.$router.resolve({
            name: 'wosummarynew',
            params,
          }).href
        }
        route && window.open(route, '_blank')
      }
    },
    async redirectToList(viewname, value) {
      if (!this.isValidCount(value)) {
        return
      }
      let { details } = this
      let { id } = details
      let params = { viewname }

      if (this.$validation.isEmpty(id)) {
        return
      }
      let filters = {
        resource: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      }
      let query = {}
      query.search = JSON.stringify(filters)
      query.includeParentFilter = true

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params,
            query,
          })
      } else {
        this.$router.push({
          name: 'workorderhomev1',
          params,
          query,
        })
      }
    },
  },
}
</script>
<style scoped>
.line-vr {
  margin-top: 20px;
  width: 1px;
  height: 190px;
  background: #8ca1ad;
  opacity: 0.3;
}
.line-hr {
  margin-top: 25px;
  width: 100%;
  height: 1px;
  background: #8ca1ad;
  opacity: 0.3;
}
.mL55 {
  margin-left: 55px;
}
.mT100 {
  margin-top: 100px;
}
.tenant-wo-url:hover {
  cursor: pointer;
}
.f60 {
  font-size: 60px;
}
.mT65 {
  margin-top: 65px;
}
.tenant-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}
.email-icon {
  width: 15px;
  height: 15px;
}
.overdue-warning {
  color: #eb6a6a;
}
.show-link:hover {
  color: #017aff;
  cursor: pointer;
  text-decoration: underline;
}
</style>
