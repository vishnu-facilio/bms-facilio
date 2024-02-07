<template>
  <div class="p20 fc-tenant-wo-page pB0 pT10">
    <div>
      <div class="flex-center-row-space pB10 pT10">
        <div class="fc-black-14 bold">
          {{ $t('common.products.service_request') }}
        </div>
        <div class="fc-tenant-view-more-txt pointer bold" @click="goToWoList">
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
                src="svgs/monitor-latest"
                iconClass="icon icon-xxxll fill-orange1"
              ></InlineSvg>
              <div class="fc-black-24 bold pT10">
                {{ $getProperty(getServiceCount, 'open') }}
              </div>
              <div class="fc-black-12 bold pT5 text-uppercase">
                {{ $t('asset.maintenance.open') }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="text-center border-right5">
              <InlineSvg
                src="svgs/monitor-latest"
                iconClass="icon icon-xxxll fill-green1"
              ></InlineSvg>
              <div class="fc-black-24 bold pT10">
                {{ $getProperty(getServiceCount, 'closed') }}
              </div>
              <div class="fc-black-12 bold pT5 text-uppercase">
                {{ $t('asset.maintenance.closed') }}
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="text-center">
              <InlineSvg
                src="svgs/monitor-latest"
                iconClass="icon icon-xxxll fill-blue6"
              ></InlineSvg>
              <div class="fc-black-24 bold pT10">
                {{ $getProperty(getServiceCount, 'total') }}
              </div>
              <div class="fc-black-12 bold pT5 text-uppercase">
                {{ $t('maintenance.wr_list.all_requests') }}
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
      getServiceCount: [],
      viewName: 'all',
    }
  },
  created() {
    this.loadWoRequestCount()
  },
  methods: {
    loadWoRequestCount() {
      this.loading = true
      let params = this.$portaluser?.peopleId
      API.get(
        `/v2/workflow/getDefaultWorkflowResult?defaultWorkflowId=202&paramList=${params}`
      ).then(({ data }) => {
        this.getServiceCount = data.workflow && data.workflow.returnValue
      })
      this.loading = false
    },
    goToWoList() {
      let route = findRouteForModule('serviceRequest', pageTypes.LIST)

      if (route) {
        this.$router.push({
          name: route.name,
          params: {
            viewname: this.viewName,
          },
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
  },
}
</script>
<style lang="scss">
.fc-tenant-wo-page {
  .white-bg-block {
    padding: 45px 0;
  }
}
</style>
