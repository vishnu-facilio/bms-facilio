<template>
  <div>
    <el-card
      v-if="loading"
      :body-style="{ padding: '40px' }"
      class="fc-mobile-tenant-card mT20"
    >
      <spinner :show="loading" size="80"></spinner>
    </el-card>

    <template v-if="$validation.isEmpty(moduleRecordList) && !loading">
      <div class="pT20 pB20 flex-middle justify-content-space">
        <div class="fc-black3-16">
          Announcements
        </div>
      </div>
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card text-center fc-align-center-column"
      >
        <inline-svg
          src="svgs/community-empty-state/announcements"
          iconClass="icon text-center icon-80"
        ></inline-svg>
        <div class="fc-mobile-grey-txt14 text-center">
          No Announcements
        </div>
      </el-card>
    </template>

    <template v-if="!loading && !$validation.isEmpty(moduleRecordList)">
      <div class="pT20 pB20 flex-middle justify-content-space">
        <div class="fc-black3-16">
          Announcements
        </div>
        <div
          class="fc-black3-16 fc-grey9-14 poniter"
          @click="goToList"
          v-if="totalCount > perPage"
        >
          View More
        </div>
      </div>

      <el-card
        :body-style="{ padding: '0 20px 20px 20px' }"
        class="fc-mobile-tenant-card"
      >
        <el-row
          class="fc-card-tenant-border-top m0 pT20 flex-middle fc__layout_media_center"
          v-for="moduleData in moduleRecordList"
          :key="moduleData.id"
          @click.native="goToSummary(moduleData)"
        >
          <el-col :span="1" class="mT3 pointer">
            <div class="fc-dot fc-dot-grey-color"></div>
          </el-col>
          <el-col :span="23" class="pL10 pointer">
            <div class="fc-black3-14">
              {{ moduleData.title }}
            </div>
            <div class="fc-grey5-13 f12 bold pT5" v-if="moduleData.expiryDate">
              Expiry: {{ moduleData.expiryDate | formatDate() }}
            </div>
          </el-col>
        </el-row>
      </el-card>
    </template>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { pageTypes } from '@facilio/router'
import { emitEvent } from 'src/webViews/utils/mobileapps'

export default {
  data() {
    return {
      moduleRecordList: [],
      loading: true,
      totalCount: null,
      page: 1,
      perPage: 3,
      viewName: 'all',
    }
  },
  created() {
    this.loadRecords()
  },
  methods: {
    async loadRecords() {
      this.loading = true
      let params = {
        page: this.page,
        perPage: 3,
        viewName: 'all',
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, error, meta } = await API.fetchAll(
        'peopleannouncement',
        params
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error occured while fetching announcements'
        )
      } else {
        this.moduleRecordList = list || []
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.loading = false
    },
    goToSummary(moduleData) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'peopleannouncement',
        recordId: moduleData.id,
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToList() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'peopleannouncement',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
