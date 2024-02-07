<template>
  <div class="dragabale-card height100">
    <ShimmerLoading v-if="isLoading" class="map-shimmer"></ShimmerLoading>
    <div v-else class="cb-card-container d-flex flex-direction-column">
      <div
        v-if="canShowHeader"
        class="card-header-block textoverflow-ellipsis max-width100 f15 bold"
        :style="{
          backgroundColor: cardState.configuration.backgroundColor,
          color: '#000',
        }"
      >
        {{ cardData.title }}
      </div>
      <div
        v-if="$validation.isEmpty(getRecords)"
        class="height100 d-flex flex-col align-center justify-content-center"
      >
        <div>
          <InlineSvg
            src="svgs/file-image-solid"
            iconClass="icon icon-35pc fill-grey2"
            class="d-flex flex-row justify-content-center"
          ></InlineSvg>
        </div>
        <div class="mT20 fc-black-dark f16 bold">No Data Available</div>
      </div>
      <div
        v-else-if="cardState.configuration.mode === 'carousel'"
        class="height100 d-flex flex-direction-column photos-card-container"
        :style="{
          backgroundColor: cardState.configuration.backgroundColor || '#fff',
        }"
      >
        <div
          class="display-flex-between-space height100 dashboard-carousel-photo"
        >
          <el-carousel
            :trigger="cardState.configuration.trigger"
            :interval="Number(cardState.configuration.interval) * 1000 || 4000"
            :type="cardState.configuration.type"
            :autoplay="cardState.configuration.autoplay"
            :arrow="cardState.configuration.arrow"
            :loop="cardState.configuration.loop"
          >
            <el-carousel-item
              v-for="(file, index) in getRecords"
              :key="index"
              class="visibility-visible-actions pointer"
            >
              <div
                v-if="cardState.configuration.showDetails"
                class="fc-photo-widget-user"
              >
                <Avatar
                  size="sm"
                  :user="$store.getters.getUser(file.uploadedBy)"
                  class="pointer avatar-photo-widget"
                ></Avatar>
              </div>
              <img
                v-lazy="file.previewUrl"
                class="photos-image-container"
                @click="openImagePreview(getRecords, index)"
              />

              <div
                v-if="cardState.configuration.showDetails"
                class="fc-content-photo-widget"
              >
                <div
                  class="fc-white-13 line-height20 text-left bold text-left bold textoverflow-ellipsis max-width90"
                >
                  {{ getPrimaryValue(file) || '---' }}
                </div>
              </div>
              <div
                @click="openImagePreview(getRecords, index)"
                class="card-overlay visibility-hide-actions"
              >
                <div
                  class="card-open-link"
                  title="Open Workorder"
                  v-tippy
                  data-arrow="true"
                  @click.stop="routeToSummary(file)"
                >
                  <inline-svg
                    class="pointer fill-white"
                    src="svgs/out"
                    iconClass="icon icon-sm fill-white"
                  ></inline-svg>
                </div>
                <div
                  class="fc-white-15 textoverflow-ellipsis max-width90"
                  :title="getPrimaryValue(file) || '---'"
                  v-tippy
                  data-arrow="true"
                >
                  {{ getPrimaryValue(file) || '---' }}
                </div>
                <div class="fc-white-15 text-left pT5 flex-middle">
                  <div class="">
                    {{
                      ($store.getters.getUser(file.uploadedBy) || {}).name ||
                        '---'
                    }}
                  </div>
                  <el-divider
                    class="fc-white-15 mL5 mR5"
                    direction="vertical"
                  ></el-divider>
                  <div class="">
                    {{ $options.filters.formatDate(file.uploadedTime) }}
                  </div>
                </div>
                <div
                  v-if="getAttachmentModuleName === 'taskattachments'"
                  class="fc-white-15 pT5"
                >
                  {{ getTaskSubject(file) || '---' }}
                  <div class="fc-white-15 text-left pT5">
                    {{ $options.filters.formatDate(file.uploadedTime) }}
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
      <div
        v-else-if="cardState.configuration.mode === 'thumbnail'"
        class="fc-dashboard-photoWidgetGall"
        :style="gridLayoutStyle"
      >
        <div
          v-for="(file, index) in getRecords"
          :key="index"
          class="photo-gallery-image visibility-visible-actions pointer"
        >
          <div
            v-if="cardState.configuration.showDetails"
            class="fc-photo-widget-user-single"
          >
            <Avatar
              size="sm"
              :user="$store.getters.getUser(file.uploadedBy)"
              class="pointer avatar-photo-widget"
            ></Avatar>
          </div>
          <div class="height100">
            <el-image
              class="height100 width100"
              :fit="'cover'"
              @click="openImagePreview(getRecords, index)"
              :src="file.previewUrl"
              :lazy="file.fileId > 0"
            ></el-image>
          </div>
          <div
            v-if="cardState.configuration.showDetails"
            class="photo-gallery-info"
          >
            <div
              class="fc-white-13 text-left bold text-left bold textoverflow-ellipsis max-width90"
            >
              {{ getPrimaryValue(file) || '---' }}
            </div>
          </div>
          <div
            @click="openImagePreview(getRecords, index)"
            v-if="
              getGridOverFlowMode === 'hidden' &&
                getGridCount === index + 1 &&
                getRecords.length > getGridCount
            "
            class="card-overlay-single"
          >
            <div class="fc-white-15 bold f15">
              <i class="el-icon-plus fwBold f15"></i>
              {{ getRecords.length - getGridCount }} more
            </div>
          </div>
          <div
            v-else
            @click="openImagePreview(getRecords, index)"
            class="card-overlay-single visibility-hide-actions"
          >
            <div
              class="card-open-link"
              title="Open Workorder"
              v-tippy
              data-arrow="true"
              @click.stop="routeToSummary(file)"
            >
              <inline-svg
                class="pointer fill-white"
                src="svgs/out"
                iconClass="icon icon-sm fill-white"
              ></inline-svg>
            </div>
            <div
              class="fc-white-15 textoverflow-ellipsis max-width90"
              :title="getPrimaryValue(file) || '---'"
              v-tippy
              data-arrow="true"
            >
              {{ getPrimaryValue(file) || '---' }}
            </div>

            <div class="flex-middle">
              <div class="fc-white-15 text-left">
                {{
                  ($store.getters.getUser(file.uploadedBy) || {}).name || '---'
                }}
              </div>
              <el-divider
                class="fc-white-15 mL5 mR5"
                direction="vertical"
              ></el-divider>
              <div class="fc-white-15 text-left">
                {{ $options.filters.formatDate(file.uploadedTime) }}
              </div>
            </div>
            <div
              v-if="getAttachmentModuleName === 'taskattachments'"
              class="fc-white-15 pT5"
            >
              {{ getTaskSubject(file) || '---' }}
              <div class="fc-white-15 text-left pT5">
                {{ $options.filters.formatDate(file.uploadedTime) }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-dialog
        v-if="visibility"
        :visible.sync="visibility"
        :append-to-body="true"
        class="f-list-attachment-dialog"
      >
        <preview-file
          :visibility.sync="visibility"
          v-if="visibility"
          :previewFile="previewFilesList[selectedIndex]"
          :files="previewFilesList"
        ></preview-file>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import BaseCard from 'pages/card-builder/cards/common/BaseCard'
import ShimmerLoading from '@/ShimmerLoading'
import { isArray, isEmpty } from '@facilio/utils/validation'
import PreviewFile from '@/PreviewFile'
import Avatar from '@/Avatar'
import VueLazyload from 'vue-lazyload'
import Vue from 'vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

Vue.use(VueLazyload, {
  listenEvents: [
    'scroll',
    'wheel',
    'mousewheel',
    'resize',
    'animationend',
    'transitionend',
    'touchmove',
  ],
})
export default {
  extends: BaseCard,
  components: { ShimmerLoading, PreviewFile, Avatar },
  data() {
    return {
      activeTab: '',
      visibility: false,
      selectedIndex: 0,
      previewFilesList: [],
    }
  },
  computed: {
    getRecords() {
      let subModuleVsRecords = this.$getProperty(
        this,
        'cardData.subModuleVsRecords',
        []
      )
      if (!isEmpty(subModuleVsRecords) && isArray(subModuleVsRecords)) {
        return subModuleVsRecords[0].records
      } else {
        return []
      }
    },
    getAttachmentModuleName() {
      let subModuleVsRecords = this.$getProperty(
        this,
        'cardData.subModuleVsRecords',
        []
      )
      if (!isEmpty(subModuleVsRecords) && isArray(subModuleVsRecords)) {
        return subModuleVsRecords[0].moduleName
      }
      return ''
    },
    getMode() {
      let mode = this.$getProperty(
        this,
        'cardData.configuration.mode',
        'carousel'
      )
      return mode || 'carousel'
    },
    canShowHeader() {
      let hideHeader = this.$getProperty(
        this,
        'cardState.configuration.hideHeader',
        false
      )
      return !hideHeader
    },
    gridLayoutStyle() {
      let gridConfig =
        this.$getProperty(this, 'cardState.configuration', {}) || {}
      let col = 100 / Number(gridConfig.cols) + '%'
      let row = 100 / Number(gridConfig.rows) + '%'
      let overflow = gridConfig.overflow || 'column'
      let backgroundColor = gridConfig.backgroundColor || '#fff'
      let layoutStyle = {
        'grid-template-columns': `repeat(auto-fill, minmax(calc(${col} - 10px), 1fr))`,
        'grid-template-rows': `repeat(auto-fill, minmax(calc(${row} - 10px), 1fr))`,
        'grid-auto-flow': `${overflow}`,
        backgroundColor: backgroundColor,
      }
      if (this.canShowHeader) {
        layoutStyle['height'] = 'calc(100% - 35px)'
      } else {
        layoutStyle['height'] = 'calc(100% + 10px)'
      }
      if (overflow === 'column') {
        layoutStyle['grid-auto-columns'] = `minmax(calc(${col} - 10px), 1fr)`
        layoutStyle['overflow-x'] = 'scroll'
      } else if (overflow === 'row') {
        layoutStyle['grid-auto-rows'] = `minmax(calc(${row} - 10px), 1fr)`
        layoutStyle['overflow-y'] = 'scroll'
      }
      return layoutStyle
    },
    getGridOverFlowMode() {
      let config =
        this.$getProperty(this, 'cardState.configuration.overflow', 'column') ||
        'column'
      return config
    },
    getGridCount() {
      let rows = this.$getProperty(this, 'cardState.configuration.rows', 0)
      let cols = this.$getProperty(this, 'cardState.configuration.cols', 0)
      return Number(rows) * Number(cols) > 0 ? Number(rows) * Number(cols) : -1
    },
  },
  created() {
    let { cardData } = this
    let { subModuleVsRecords } = cardData || {}
    if (!isEmpty(subModuleVsRecords) && isArray(subModuleVsRecords)) {
      this.activeTab = subModuleVsRecords[0].name
    }
  },
  methods: {
    openImagePreview(records, index) {
      this.previewFilesList = records
      this.selectedIndex = index
      if (!this.isDashboardEdit) {
        this.visibility = true
      }
    },
    getWoSubject(file) {
      let taskMap =
        this.$getProperty(this, `cardData.taskMeta.${file.parentId}`, {}) || {}
      let woId = taskMap.woId || 0
      return (
        this.$getProperty(
          this,
          `cardData.recordsPrimaryValue.${woId}`,
          '---'
        ) || '---'
      )
    },
    getTaskSubject(file) {
      let taskMap =
        this.$getProperty(this, `cardData.taskMeta.${file.parentId}`, {}) || {}
      let taskName = taskMap.taskSubject || '---'
      return taskName
    },
    getPrimaryValue(file) {
      if (this.getAttachmentModuleName === 'taskattachments') {
        return this.getWoSubject(file)
      }
      return (
        this.$getProperty(
          this,
          `cardData.recordsPrimaryValue.${file.parentId}`,
          '---'
        ) || '---'
      )
    },
    routeToSummary(file) {
      let woId
      if (this.getAttachmentModuleName === 'taskattachments') {
        let taskMap =
          this.$getProperty(this, `cardData.taskMeta.${file.parentId}`, {}) ||
          {}
        woId = taskMap.woId || 0
      } else {
        woId = file.parentId
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id: woId,
            },
          })
        }
      } else {
        this.$router.push({ path: `/app/wo/orders/summary/${woId}` })
      }
    },
  },
}
</script>
