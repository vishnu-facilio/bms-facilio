<template>
  <div class="p20 pT0 fc-tenant-contact-dir-page">
    <div>
      <div class="flex-center-row-space pT0 pB10">
        <div class="fc-black-14 bold">
          {{ $t('tenant.tenants.contacts') }}
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
        <template v-if="$validation.isEmpty(moduleRecordList) && !loading">
          <div
            class="flex-middle flex-center-vH height300 flex-col white-bg-block"
          >
            <inline-svg
              src="svgs/emptystate/readings-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="fc-black3-16">
              {{ $t('tenant.tenants.no_contacts') }}
            </div>
          </div>
        </template>

        <template v-if="!loading && !$validation.isEmpty(moduleRecordList)">
          <el-row :gutter="20">
            <el-col
              v-for="(moduleRecord, index) in moduleRecordList"
              :key="index"
              :span="8"
              class="pB15"
            >
              <el-card class="fc-contact-box-card">
                <div class="flex-middle fc-contact-header">
                  <div>
                    <avatar
                      size="lg"
                      :user="{ name: moduleRecord.contactName }"
                      class="pointer"
                    ></avatar>
                  </div>
                  <div>
                    <div
                      class="fc-black3-16 bold pL10 textoverflow-height-ellipsis2 line-height20"
                    >
                      {{ moduleRecord.contactName }}
                    </div>
                  </div>
                </div>

                <div class="mT15 fc-tenant-contact-footer">
                  <div
                    class="fc-blue-txt4-13 text-left"
                    v-if="moduleRecord.contactPhone"
                  >
                    <i class="el-icon-mobile-phone pR5"></i>
                    {{ moduleRecord.contactPhone }}
                  </div>
                  <div class="fc-blue-txt4-13 pT10">
                    <i class="el-icon-message pR5"></i>
                    {{ moduleRecord.contactEmail }}
                  </div>
                  <div
                    class="fc-blue-txt4-13 text-left pT10"
                    v-if="moduleRecord.categoryEnum"
                  >
                    <i class="el-icon-collection-tag pR5"></i>
                    {{ moduleRecord.categoryEnum }}
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Avatar from '@/Avatar'
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  data() {
    return {
      moduleRecordList: [],
      loading: true,
      totalCount: null,
      perPage: 5,
      page: 1,
      viewName: 'all',
    }
  },
  created() {
    this.loadRecords()
  },
  components: {
    Avatar,
  },
  methods: {
    async loadRecords() {
      this.loading = true
      let params = {
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        viewname: this.viewName,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, meta, error } = await API.fetchAll('contactdirectory', params)
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error occured while fetching contact directories'
        )
      } else {
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.moduleRecordList = list || []
      }
      this.loading = false
    },
    goToList() {
      let route = findRouteForModule('contactdirectory', pageTypes.LIST)

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
.fc-tenant-contact-dir-page {
  .el-card {
    width: 100%;
    height: auto;
    box-shadow: 0 2px 13px 0 rgba(230, 233, 236, 0.26);
    border: none;
  }

  .fc-tenant-contact-footer {
    padding: 15px 20px;
    border-top: 1px solid #f2f4fa;
  }

  .fc-contact-box-card {
    padding-left: 0;
    padding-right: 0;
  }

  .el-card__body {
    padding: 0;
  }

  .fc-contact-header {
    padding: 20px 20px 0;
  }
}
</style>
