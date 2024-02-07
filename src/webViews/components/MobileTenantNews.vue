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
      <div class="pT20 pB20 flex-middle justify-content-space fc-black3-16">
        News
      </div>
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card text-center fc-align-center-column"
      >
        <inline-svg
          src="svgs/community-empty-state/news-info"
          iconClass="icon text-center icon-80"
        ></inline-svg>
        <div class="fc-mobile-grey-txt14 text-center">
          No News
        </div>
      </el-card>
    </template>
    <template v-if="!loading && !$validation.isEmpty(moduleRecordList)">
      <div class="pT20 pB20 flex-middle justify-content-space">
        <div class="fc-black3-16">
          News
        </div>
        <div
          class="fc-black3-16 fc-grey9-14 pointer"
          @click="goToList"
          v-if="totalCount > perPage"
        >
          View All
        </div>
      </div>

      <div class="fc-tenant-news-card-con">
        <div class="fc-tenant-grid-column">
          <div
            class="fc-tennat-grid-column-each pointer"
            v-for="moduleRecord in moduleRecordList"
            :key="moduleRecord.id"
            @click="goToSummary(moduleRecord)"
          >
            <div class="width300px">
              <el-carousel
                v-if="getImageAttachements(moduleRecord)"
                :interval="5000"
                height="150px"
                class="fc-news-carousel"
              >
                <el-carousel-item
                  v-for="image in getImageAttachements(moduleRecord)"
                  :key="image.id"
                  value="image.id"
                >
                  <img
                    v-if="image.previewUrl"
                    :src="$prependBaseUrl(image.previewUrl)"
                    class="fc-news-img"
                  />
                </el-carousel-item>
              </el-carousel>
              <div class="pT20 pB20 pL30 pR30">
                <div
                  class="fc-black33-16 fw6 textoverflow-height-ellipsis2 line-height18"
                >
                  {{ moduleRecord.title }}
                </div>
                <div class="fc-grey2-text12 pT5">
                  {{ moduleRecord.sysCreatedTime | formatDate() }}
                </div>
                <div
                  class="label-txt-black pT10 textoverflow-height-ellipsis5"
                  :class="{
                    'textoverflow-height-ellipsis6': !getImageAttachements(
                      moduleRecord
                    ),
                  }"
                  v-html="getDescription(moduleRecord)"
                ></div>
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
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  data() {
    return {
      moduleRecordList: [],
      loading: true,
      attachmentsRecordList: [],
      totalCount: null,
      page: 1,
      perPage: 4,
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
        viewName: this.viewName,
        withCount: true,
        includeParentFilter: this.includeParentFilter,
      }
      let { list, error, meta } = await API.fetchAll(
        'newsandinformation',
        params
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message || 'Error occured while fetching news and information'
        )
      } else {
        this.moduleRecordList = list || []
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.loading = false
    },
    getImageAttachements(record) {
      let { newsandinformationattachments: attachments } = record
      let images = []
      for (let attachment of attachments) {
        if (attachment.contentType.includes('image')) {
          images.push(attachment)
        }
      }
      return isEmpty(images) ? null : images
    },
    goToList() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'newsandinformation',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    getDescription(record) {
      let { description } = record
      return !isEmpty(description) ? sanitize(description) : '---'
    },
    goToSummary(moduleData) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'newsandinformation',
        recordId: moduleData.id,
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
