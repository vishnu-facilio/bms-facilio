<template>
  <div class="p20 fc-tenant-wo-page pB0 pT10">
    <div>
      <div class="flex-center-row-space pT0 pB10">
        <div class="fc-black-14 bold">
          {{ $t('common.header.visitors') }}
        </div>
        <div
          class="fc-tenant-view-more-txt pointer bold"
          @click="goToVisitList"
        >
          {{ $t('tenant.tenants.view_more') }}
        </div>
      </div>
      <div class="white-bg-block">
        <div
          v-if="loading"
          class="flex-middle flex-center-hH height300 flex-col"
        >
          <spinner :show="loading" size="80"></spinner>
        </div>
        <el-row v-else>
          <el-col :span="8">
            <div class="text-center border-right5">
              <InlineSvg
                src="svgs/user-latest"
                iconClass="icon icon-xxxl  fill-blue6"
              ></InlineSvg>
              <div class="fc-black-24 bold pT5">
                {{ allVisitortotalCount ? allVisitortotalCount : '---' }}
              </div>
              <div class="fc-black-12 bold pT5">
                {{ $t('tenant.tenants.visitor_expected') }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="text-center border-right5">
              <InlineSvg
                src="svgs/user-latest"
                iconClass="icon icon-xxxl fill-green1"
              ></InlineSvg>
              <div class="fc-black-24 bold pT5">
                {{
                  checkinVisitortotalCount ? checkinVisitortotalCount : '---'
                }}
              </div>
              <div class="fc-black-12 bold pT5">
                {{ $t('tenant.tenants.visitor_checkedin') }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="text-center">
              <InlineSvg
                src="svgs/user-latest"
                iconClass="icon icon-xxxl fill-orange1"
              ></InlineSvg>
              <div class="fc-black-24 bold pT5">
                {{
                  checkoutVisitortotalCount ? checkoutVisitortotalCount : '---'
                }}
              </div>
              <div class="fc-black-12 bold pT5">
                {{ $t('tenant.tenants.visitor_checkedout') }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  data() {
    return {
      loading: false,
      perPage: 50,
      page: 1,
      allVisitortotalCount: null,
      checkoutVisitortotalCount: null,
      checkinVisitortotalCount: null,
      getCheckinVisitorTenantCount: [],
      getCheckoutVisitorTenantCount: [],
      getAllVisitorTenantCount: [],
      viewName: 'invite_all',
    }
  },
  created() {
    this.loadAllVisitorCount()
    this.loadChickinVisitorCount()
    this.loadChickoutVisitorCount()
  },
  methods: {
    async loadAllVisitorCount() {
      this.loading = true
      let params = {
        page: this.page,
        perPage: this.perPage,
        includeParentFilter: true,
        withCount: true,
        filters: JSON.stringify({
          expectedCheckInTime: {
            operatorId: 22,
          },
        }),
        moduleName: 'invitevisitor',
      }

      let { error, data, meta } = await API.fetchAll('invitevisitor', params)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.getAllVisitorTenantCount = data
        this.allVisitortotalCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },

    async loadChickinVisitorCount() {
      this.loading = true
      let params = {
        page: this.page,
        perPage: this.perPage,
        includeParentFilter: true,
        withCount: true,
        filters: JSON.stringify({
          checkInTime: {
            operatorId: 22,
          },
        }),
        moduleName: 'visitorlog',
      }
      let { error, data, meta } = await API.fetchAll('visitorlog', params)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.getCheckinVisitorTenantCount = data
        this.checkinVisitortotalCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },
    async loadChickoutVisitorCount() {
      this.loading = true
      let params = {
        withCount: true,
        includeParentFilter: true,
        page: this.page,
        perPage: this.perPage,
        filters: JSON.stringify({
          checkOutTime: {
            operatorId: 22,
          },
        }),
        moduleName: 'visitorlog',
      }

      let { error, data, meta } = await API.fetchAll('visitorlog', params)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.getCheckoutVisitorTenantCount = data
        this.checkoutVisitortotalCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
      }
      this.loading = false
    },
    goToVisitList() {
      let route = findRouteForModule('invitevisitor', pageTypes.LIST)
      if (route) {
        this.$router.push({
          name: route.name,
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
  },
}
</script>
