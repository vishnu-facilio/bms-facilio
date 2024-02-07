<template>
  <div>
    <div v-if="isLoading" class="mT100">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.work_orders') }}
          </div>
          <div class="f11 bold text-fc-grey">
            {{ $t('common.date_picker.last30days') }}
          </div>
        </div>
      </el-row>
      <el-row>
        <el-col :span="7">
          <el-row>
            <div class="justify-content-center">
              <div class="f60 text-center">
                {{ totalOpenWorkorders }}
              </div>
              <div class="f14 bold fc-black-13 text-center mT5">
                {{ $t('common.header.open') }}
              </div>
            </div>
          </el-row>
          <el-row class="mT30">
            <div>
              <div class="f60 text-center">
                {{ totalOverdueWorkorders }}
              </div>
              <div class="f14 bold fc-black-13 text-center mT5 text-coral">
                {{ $t('common.header.overdue') }}
              </div>
            </div>
          </el-row>
        </el-col>
        <el-col :span="1">
          <div class="line-vr"></div>
        </el-col>
        <el-col :span="16">
          <el-row class="mT30 tenant-wo-url">
            <div @click="redirectToOverview(lastClosedWorkorderId)">
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
                class="textoverflow-ellipsis pR20 mL30 f14 pT10 fc-black-13 text-left"
              >
                {{ lastClosedWorkorder }}
              </div>
            </div>
          </el-row>
          <el-row class="tenant-wo-url mT65">
            <div @click="redirectToOverview(mostOverdueWorkorderId)">
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
                class="textoverflow-ellipsis pR20 mL30 f14 pT10 fc-black-13 text-left"
              >
                {{ mostOverdueWorkorder }}
              </div>
            </div>
          </el-row>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['details'],
  data() {
    return {
      totalOpenWorkorders: '---',
      totalOverdueWorkorders: '---',
      lastClosedWorkorder: '---',
      mostOverdueWorkorder: '---',
      lastClosedWorkorderId: null,
      mostOverdueWorkorderId: null,
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
    ])
    this.isLoading = false
  },
  computed: {
    getTime30DaysFromNow() {
      return (this.$helpers.getOrgMoment().valueOf() - 2592000000).toString()
    },
  },
  methods: {
    async getOpenWorkordersCount() {
      let queryParam = {}
      queryParam = {
        withCount: true,
        viewName: 'open',
        includeParentFilter: true,
        moduleName: 'workorder',
      }
      queryParam.filters = JSON.stringify({
        tenant: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      })
      let self = this
      let { meta, error } = await API.get('/v3/modules/data/list', queryParam)
      if (!error) {
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        self.totalOpenWorkorders = totalCount || 0
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
        tenant: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        createdTime: {
          operatorId: 19,
          value: [this.getTime30DaysFromNow],
        },
      })
      let self = this
      let { data, meta, error } = await API.get(
        '/v3/modules/data/list',
        queryParam
      )
      if (!error) {
        let { pagination } = meta || {}
        let { totalCount } = pagination || {}
        self.totalOverdueWorkorders = totalCount || 0

        let { workorder } = data || {}
        let { subject, id } = workorder.length ? workorder[0] : {}
        self.mostOverdueWorkorder = subject || '---'
        self.mostOverdueWorkorderId = id
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
        tenant: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
      })
      let self = this
      let { data, error } = await API.get('/v3/modules/data/list', queryParam)
      if (!error) {
        let { workorder } = data || {}
        let { subject, id } = workorder.length ? workorder[0] : {}
        self.lastClosedWorkorder = subject || '---'
        self.lastClosedWorkorderId = id
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
</style>
