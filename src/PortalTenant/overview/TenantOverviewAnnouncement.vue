<template>
  <div>
    <div class="flex-center-row-space pB10 pL20 pR20">
      <div class="fc-black-14 bold">
        {{ $t('tenant.announcement.announcements') }}
      </div>
      <div class="fc-tenant-view-more-txt pointer bold" @click="goToList">
        {{ $t('tenant.tenants.view_more') }}
      </div>
    </div>
    <div>
      <!-- loading -->
      <div
        v-if="loading"
        class="flex-middle flex-center-hH height300 white-bg-block flex-col mL20 mR20"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <!-- empty state -->
      <template v-else-if="$validation.isEmpty(moduleRecordList) && !loading">
        <div
          class="flex-middle flex-center-vH height300 flex-col white-bg-block mL20 mR20"
        >
          <inline-svg
            src="svgs/community-empty-state/announcements"
            iconClass="icon text-center icon-xxxxlg"
          >
          </inline-svg>
          <div class="fc-black3-16">
            {{ $t('tenant.announcement.no_data') }}
          </div>
        </div>
      </template>
      <template v-else-if="!$validation.isEmpty(moduleRecordList) && !loading">
        <div class="pL10 pR10">
          <el-row class="fc-card-tenant-border-top m0" :gutter="20">
            <el-col
              :span="8"
              v-for="(moduleData, index) in moduleRecordList"
              :key="index"
              class="pB15"
            >
              <el-card
                :body-style="{ padding: '0 20px 20px 20px' }"
                class="fc-mobile-tenant-card pT10"
              >
                <div @click="openRecordSummary(moduleData.id)">
                  <div class="flex-middle justify-between">
                    <div>
                      <div class="fc-new-badge" v-if="!moduleData.isRead">
                        {{ $t('tenant.announcement.new') }}
                      </div>
                    </div>
                    <div
                      class="fc-grey5-13 f12 bold"
                      v-if="moduleData.expiryDate"
                    >
                      Expiry:
                      {{ moduleData.expiryDate | formatDate() }}
                    </div>
                  </div>
                  <div class="fc-black3-14 pT5 bold">
                    {{ moduleData.title }}
                  </div>
                  <div class="fc-black3-12 pT5 bold">
                    {{ moduleData.sysCreatedTime | formatDate() }}
                  </div>
                  <div
                    class="label-txt-black pT5 truncate-text"
                    v-if="moduleData.longDescription"
                  >
                    {{ moduleData.longDescription | htmlToText }}
                  </div>
                  <div class="pT10 fc-right-icon-align">
                    <i class="fc-blue-txt4-13 el-icon-right fc-right-icon"></i>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'

export default {
  data() {
    return {
      moduleRecordList: [],
      loading: true,
      totalCount: null,
      perPage: 3,
      page: 1,
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
        viewname: this.viewName,
        page: this.page,
        perPage: 3,
        withCount: true,
        orderBy: 'People_Announcements.ID',
        orderType: 'desc',
        includeParentFilter: true,
        filters: JSON.stringify({
          isCancelled: {
            operatorId: 15,
            value: ['false'],
          },
        }),
      }
      let { list, error, meta } = await API.fetchAll(
        'peopleannouncement',
        params
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error occured while fetching Announcement'
        )
      } else {
        this.moduleRecordList = list || []
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.loading = false
    },
    openRecordSummary(id) {
      let { name } =
        findRouteForModule('peopleannouncement', pageTypes.OVERVIEW) || {}
      if (name) {
        this.$router.push({
          name,
          params: { viewname: 'all', id: id },
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    goToList() {
      let route = findRouteForModule('peopleannouncement', pageTypes.LIST)
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
.fc-new-badge {
  width: 36px;
  height: 16px;
  text-align: center;
  border-radius: 3px;
  background-color: #4ba973;
  font-size: 10px;
  font-weight: 500;
  line-height: 16px;
  letter-spacing: 0.36px;
  text-align: center;
  color: #ffffff;
}
.fc-mobile-tenant-card {
  height: 135px !important;
  position: relative;
  cursor: pointer;
  box-shadow: 0 3px 7px 0 rgba(226, 229, 233, 0.5);
}
.fc-right-icon {
  font-size: 24px !important;
  font-weight: 500 !important;
}
.fc-right-icon-align {
  position: absolute;
  bottom: 10px;
}
.fc-tenant-view-more-txt {
  color: #a3a6b1;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: normal;
}
</style>
