<template>
  <div class="p20 pT10 fc-overview-news-page pB0">
    <div class="flex-center-row-space pT0 pB10">
      <div class="fc-black-14 bold">
        {{ $t('tenant.news.news') }}
      </div>
      <div class="fc-tenant-view-more-txt pointer bold" @click="goToList">
        {{ $t('tenant.tenants.view_more') }}
      </div>
    </div>
    <div class="">
      <div
        v-if="loading"
        class="flex-middle flex-center-hH height300 flex-col white-bg-block mL20 mR20"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(moduleRecordList)"
        class="height200 flex-center-vH align-center white-bg-block"
      >
        <InlineSvg
          src="svgs/emptystate/news-info"
          iconClass="icon icon-120"
        ></InlineSvg>
        <div class="label-txt-black bold">
          {{ $t('tenant.news.no_data') }}
        </div>
      </div>
      <div class="width100" v-else>
        <el-row :gutter="20">
          <el-col
            :span="8"
            v-for="(moduleRecord, index) in moduleRecordList"
            :key="index"
            :value="moduleRecord"
            class="pB15"
          >
            <el-card class="fc-box-card">
              <div @click="openRecordSummary(moduleRecord.id)" class="pointer">
                <el-carousel
                  v-if="getImageAttachements(moduleRecord)"
                  :interval="5000"
                  height="125px"
                  class="fc-news-carousel"
                  arrow="hover"
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
                <div class="p20">
                  <div
                    class="fc-black33-14 bold textoverflow-height-ellipsis height30"
                  >
                    {{ moduleRecord.title }}
                  </div>
                  <div class="fc-black3-12 pT5 bold">
                    {{ moduleRecord.sysCreatedTime | formatDate() }}
                  </div>
                  <div
                    class="fc-grey10-14 pT10 news-desc-ellipsis"
                    :class="{
                      'news-desc-ellipsis2': !getImageAttachements(
                        moduleRecord
                      ),
                    }"
                    v-html="getDescription(moduleRecord)"
                  ></div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  data() {
    return {
      moduleRecordList: [],
      loading: true,
      attachmentsRecordList: [],
      totalCount: null,
      perPage: 3,
      viewName: 'all',
      page: 1,
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
        viewname: this.viewName,
        orderBy: 'NewsAndInformation.ID',
        orderType: 'desc',
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
      let { newsandinformationattachments: attachments = [] } = record || {}
      let images = []
      for (let attachment of attachments) {
        if (attachment.contentType.includes('image')) {
          images.push(attachment)
        }
      }
      return isEmpty(images) ? null : images
    },
    goToList() {
      let route = findRouteForModule('newsandinformation', pageTypes.LIST)
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
    getDescription(record) {
      let { description } = record
      return !isEmpty(description) ? sanitize(description) : '---'
    },
    openRecordSummary(id) {
      let { name } =
        findRouteForModule('newsandinformation', pageTypes.OVERVIEW) || {}
      if (name) {
        this.$router.push({
          name,
          params: { id, viewname: 'all' },
        })
      }
    },
  },
}
</script>
<style lang="scss">
.fc-overview-news-page {
  .fc-tenant-feed-sidebar {
    .fc-tenant-feed-sidebar-header {
      padding: 20px;
      border-bottom: 1px solid #e9ebef;
    }
  }

  .fc-tenant-news-carousel {
    .el-carousel__arrow {
      width: 16px;
      height: 16px;
    }

    .textoverflow-height-ellipsis {
      span {
        font-size: 14px;
        line-height: 16px;
        letter-spacing: 0.5px;
        color: #324056;
      }
    }
  }

  .fc-news-empty-img {
    padding: 17px 15px 16px 16px;
    border-radius: 7.1px;
    background-color: #f0f4ff;
  }

  .el-carousel__container {
    height: 125px;
  }

  .el-carousel__indicators--outside,
  .el-carousel__indicators--horizontal {
    display: none;
  }

  .fc-box-card {
    .el-card__body {
      padding: 0;
    }
  }

  .el-card.is-always-shadow {
    width: 100%;
    height: 250px;
    box-shadow: 0 2px 13px 0 rgba(230, 233, 236, 0.26);
    border: none;
  }
  .fc-news-img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.news-desc-ellipsis {
  white-space: normal;
  z-index: 1;
  line-height: 1;
  display: block;
  display: -webkit-box;
  min-height: 60px;
  max-height: 60px;
  height: 60px;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
}

.news-desc-ellipsis2 {
  white-space: normal;
  z-index: 1;
  line-height: 1;
  display: block;
  display: -webkit-box;
  min-height: 177px;
  max-height: 177px;
  height: 177px;
  -webkit-line-clamp: 7;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
}
</style>
