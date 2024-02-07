<template>
  <div class="pB30">
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
          Contact Directory
        </div>
      </div>
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card text-center fc-align-center-column"
      >
        <inline-svg
          src="svgs/emptystate/readings-empty"
          iconClass="icon text-center icon-80"
        ></inline-svg>
        <div class="fc-mobile-grey-txt14 text-center">
          No contacts
        </div>
      </el-card>
    </template>

    <template v-if="!loading && !$validation.isEmpty(moduleRecordList)">
      <div class="pT20 pB20 flex-middle justify-content-space">
        <div class="fc-black3-16">
          Contact Directory
        </div>
        <div
          v-if="totalCount > perPage"
          class="fc-black3-16 fc-grey9-14 pointer"
          @click="goToList"
        >
          View All
        </div>
      </div>

      <div class="fc-tenant-grid-column mB30">
        <div
          v-for="moduleRecord in moduleRecordList"
          :key="moduleRecord.id"
          class="fc-tenant-grid-contacts pointer mR10"
          @click="goToSummary(moduleRecord)"
        >
          <div class="width200px p20">
            <div class="text-center">
              <inline-svg
                src="user"
                class="vertical-middle"
                iconClass="icon icon-md"
              ></inline-svg>
              <div
                class="fc-black3-14 bold pT10 line-height20 textoverflow-height-ellipsis2"
              >
                {{
                  moduleRecord.contactName ? moduleRecord.contactName : '---'
                }}
              </div>

              <div
                class="flex-middle fc-blue-txt4-13 mT10 pT10 pB10 fc-contact-border-bottom fc-contact-border-top"
              >
                <inline-svg
                  src="svgs/smartphone"
                  class="vertical-middle fill-blue5"
                  iconClass="icon icon-md"
                >
                </inline-svg>
                <div class="pL10">
                  {{
                    moduleRecord.contactPhone
                      ? moduleRecord.contactPhone
                      : '---'
                  }}
                </div>
              </div>
              <div class="flex-middle fc-blue-txt4-13 pT10 pB10">
                <inline-svg
                  src="svgs/mail2"
                  class="vertical-bottom fill-blue5 line-height0"
                  iconClass="icon icon-sm"
                >
                </inline-svg>
                <div class="pL10 break-word text-left line-height18">
                  {{
                    moduleRecord.contactEmail
                      ? moduleRecord.contactEmail
                      : '---'
                  }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
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
      moduleRecord: null,
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
        perPage: this.perPage,
        withCount: true,
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
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'contactdirectory',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
