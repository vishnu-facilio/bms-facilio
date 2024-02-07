<template>
  <div>
    <div v-if="loading">
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card mT20"
      >
        <spinner :show="loading" size="80"></spinner>
      </el-card>
    </div>

    <template v-if="$validation.isEmpty(workorderList) && !loading">
      <div class="pT20 pB20 flex-middle justify-content-space">
        <div class="fc-black3-16">
          Work Requests
        </div>
      </div>
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card fc-align-center-column"
      >
        <inline-svg
          src="svgs/emptystate/workorder"
          iconClass="icon text-center icon-80"
        ></inline-svg>
        <div class="fc-mobile-grey-txt14 text-center">
          No Work Requests
        </div>
      </el-card>
    </template>

    <template v-if="!loading && !$validation.isEmpty(workorderList)">
      <div class="pT20 flex-middle justify-content-space">
        <div class="fc-black3-16">
          Work Request
        </div>
        <div
          class="fc-black3-16 fc-grey9-14 pointer"
          @click="goToList"
          v-if="totalCount > perPage"
        >
          View More
        </div>
      </div>
      <el-card
        v-for="workorder in workorderList"
        :body-style="{ padding: '20px' }"
        class="fc-mobile-tenant-card mT20 pointer"
        :key="workorder.id"
        @click.native="goToSummary(workorder)"
      >
        <div class="fc-id">#{{ workorder.id }}</div>
        <div class="label-txt-black pT5 bold">{{ workorder.subject }}</div>
        <div class="flex-middle justify-between">
          <div
            class="fc-black-12 text-left pT5"
            v-if="!$validation.isEmpty($getProperty(workorder, 'createdTime'))"
          >
            {{ workorder.createdTime | formatDate() }}
          </div>
          <div
            class="fc-mobile-badge text-uppercase"
            v-if="
              !$validation.isEmpty(
                $getProperty(workorder, 'moduleState.displayName')
              )
            "
          >
            {{
              workorder.moduleState.displayName
               ? workorder.moduleState.displayName
               : ''
            }}
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script>
import { pageTypes } from '@facilio/router'
import { API } from '@facilio/api'
import { emitEvent } from 'src/webViews/utils/mobileapps'

export default {
  data() {
    return {
      workorderList: [],
      workorder: null,
      totalCount: null,
      loading: true,
      page: 1,
      perPage: 2,
      viewName: 'open',
    }
  },
  created() {
    this.loadWorkOrderRequests()
  },
  methods: {
    async loadWorkOrderRequests() {
      this.loading = true
      let params = {
        viewName: this.viewName,
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, error, meta } = await API.fetchAll('workorder', params)
      if (error) {
        this.$message.error(error.message || 'Could not fetch Work Permits')
      } else {
        this.workorderList = list
        this.totalCount =
          this.$getProperty(meta, 'pagination.totalCount') || null
      }
      this.loading = false
    },
    goToList() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'workorder',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToSummary(workorder) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'workorder',
        recordId: workorder.id,
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
