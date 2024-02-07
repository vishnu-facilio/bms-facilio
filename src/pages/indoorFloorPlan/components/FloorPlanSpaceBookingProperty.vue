<template>
  <div class="properties-section new-properties-section bL0">
    <div class="floating-icons">
      <div class="flex-middle mR10 pointer">
        <TabCheckRedirect
          v-if="record"
          :moduleName="ObjectModuleName"
          :id="record.id"
          src="svgs/external-link2"
          iconClass="icon icon-xs  fill-blue"
          class="flex-middle"
        ></TabCheckRedirect>
      </div>
      <div class="mR15 pointer flex-middle" @click="handleClose()">
        <i class="el-icon-close pointer f17 bold"></i>
      </div>
    </div>
    <el-tabs class="m0 h100 space-card-tab" v-model="activeName">
      <el-tab-pane label="Quick view" name="quickview">
        <div class="h100">
          <spinner :show="loading" size="80" v-if="loading"></spinner>
          <template v-else>
            <div class="100">
              <div class="pT0">
                <div class="fp-properties-image">
                  <div class="carousel-container">
                    <div
                      v-if="photosList && photosList.length"
                      @click="openFilePreview"
                      style="width:320px"
                    >
                      <el-carousel
                        ref="siteCarousel"
                        indicator-position="none"
                        trigger="click"
                        :autoplay="false"
                        arrow="hover"
                        height="150px"
                        class="navbar"
                      >
                        <el-carousel-item
                          v-for="(photo, photoIndex) in photosList"
                          :key="photoIndex"
                          class="fc-v1-site-slider-overlay pointer"
                        >
                          <div>
                            <img
                              v-if="photo.originalUrl"
                              :src="$helpers.getImagePreviewUrl(photo.photoId)"
                              class="fc-carouselsite-image"
                              height="150px"
                              width="320px"
                              @click="openFilePreview"
                            />
                          </div>
                        </el-carousel-item>
                      </el-carousel>
                    </div>
                    <div class="space-icon-container" v-else>
                      <InlineSvg
                        :src="`svgs/spacemanagement/space`"
                        iconClass="icon icon-xxxlg "
                        style="background: rgb(222 236 255);"
                      ></InlineSvg>
                    </div>
                  </div>
                </div>
                <div class="fp-properties-text mB20" v-if="space">
                  <div class="flex-col">
                    <div class="sp-id-header">
                      {{ `#${space.id}` }}
                    </div>
                    <div class="space-name">{{ space.name }}</div>
                    <div class="inline-block" v-if="spaceSummary">
                      <div class="flex-middle">
                        <div class="space-container">
                          <div class="ellipsis sub-content">
                            <div v-if="spaceSummary.building">
                              {{ `${spaceSummary.building.name} ` }}
                            </div>
                            <div v-if="spaceSummary.floor">
                              {{ `, ${spaceSummary.floor.name}` }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  class="spacedetails-card-content"
                  v-if="
                    spaceSummary &&
                      spaceSummary.maxOccupancy &&
                      spaceSummary.maxOccupancy > 0
                  "
                >
                  <el-tooltip
                    placement="top"
                    trigger="hover"
                    content="Capacity"
                  >
                    <div class="inline-block">
                      <div class="flex-middle card-occupancy">
                        <inline-svg
                          style="padding-right: 8px; display: flex;"
                          src="svgs/employeePortal/people"
                          iconClass="icon text-center icon-sm15 vertical-bottom"
                        ></inline-svg>
                        <div class="ellipsis main-content">
                          {{ `${spaceSummary.maxOccupancy} ` }}
                        </div>
                      </div>
                    </div>
                  </el-tooltip>
                </div>
                <div
                  class=" spacedetails-card-content "
                  v-if="amenities && amenities.length > 0"
                >
                  <div class="inline-block">
                    <div class="flex-middle" style="flex-wrap: wrap;">
                      <div v-for="(amenity, j) in amenities" :key="j">
                        <el-tag v-if="amenity" class="filter-amenity-name">
                          {{ amenity.name }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
                <template v-if="isReservable">
                  <!-- <div
                    class="fp-prop-status"
                    v-bind:class="{ partial: this.bookingList.length }"
                  >
                    {{ bookingState }}
                  </div> -->
                  <div class="fp-properties-text pB5 opacity7">
                    <div class="flex">
                      <inline-svg
                        src="svgs/employeePortal/ic-date-range-2"
                        iconClass="icon text-center icon-sm15 vertical-bottom"
                      ></inline-svg>
                      <div class="pL10 dateString">
                        {{ dateString }}
                      </div>
                    </div>
                  </div>
                  <div
                    class="fp-properties-text opacity7"
                    style="padding-top: 2px;"
                  >
                    <div class="flex">
                      <inline-svg
                        src="svgs/employeePortal/clock"
                        iconClass="icon text-center icon-sm15 vertical-bottom"
                      ></inline-svg>
                      <div class="pL10 dateString">
                        {{ timeString }}
                      </div>
                    </div>
                  </div>
                </template>
              </div>
              <div class="fp-prop-btn" v-if="isReservable">
                <el-button
                  type="primary"
                  class="fc-white-btn mR10"
                  @click="$emit('book', [])"
                  >{{ 'Book' }}</el-button
                >
              </div>
            </div>
          </template>
        </div>
      </el-tab-pane>
      <el-tab-pane
        label="Schedule"
        v-if="isReservable"
        class="h100"
        name="schedule"
      >
        <spinner :show="loading" size="80" v-if="loading"></spinner>

        <div class="h100" v-else>
          <div class="h100">
            <SpaceBookingCalander
              class="h100"
              moduleName="spacebooking"
              :startHour="bookingHour"
              :startDate="bookingStartDay"
              :endDate="bookingEndDay"
              :selectDate="bookingDate"
              :spaceId="spaceId"
              :showToday="false"
              :goToBooking="false"
              :height="height"
            >
            </SpaceBookingCalander>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <PreviewFile
      :visibility.sync="imagePreviewVisibility"
      v-if="imagePreviewVisibility"
      :previewFile="getFormattedFile[photoPreviewIndex]"
      :files="getFormattedFile"
    ></PreviewFile>
  </div>
</template>
<script>
import BookingProperty from 'src/pages/indoorFloorPlan/components/FloorPlanBookingPropertyNew.vue'
import { API } from '@facilio/api'
import { isEmpty } from 'lodash'
import cardHelper from 'pages/card-builder/card-helpers.js'
import PreviewFile from '@/PreviewFile'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import SpaceBookingCalander from 'src/components/SpaceBookingCalendar.vue'
import moment from 'moment-timezone'
import TabCheckRedirect from 'src/components/TabCheckRedirect.vue'
export default {
  extends: BookingProperty,
  mixins: [cardHelper],
  components: { SpaceBookingCalander, PreviewFile, TabCheckRedirect },
  data() {
    return {
      photosList: [],
      imagePreviewVisibility: false,
      photoPreviewIndex: null,
      spaceSummary: null,
      amenities: null,
      activeName: 'quickview',
    }
  },
  watch: {
    spaceId: {
      handler() {
        this.setDefaultTab()
        this.loadPhotos()
        this.loadSpaceDetails()
      },
    },
  },
  computed: {
    getFormattedFile() {
      let { photosList } = this
      let filePreviewList = []
      photosList.forEach(photo => {
        filePreviewList.push({
          contentType: 'image',
          previewUrl: photo.originalUrl,
          downloadUrl: this.$helpers.getFileDownloadUrl(photo.photoId),
        })
      })
      return filePreviewList
    },
    isReservable() {
      if (this.moduleName === 'desks') {
        return this.record?.reservable ? this.record.reservable : false
      }
      return this.properties?.object?.isReservable
        ? this.properties.object.isReservable
        : false
    },
    showSummaryLinkIcon() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.ObjectModuleName, pageTypes.OVERVIEW) || {}

        if (name) {
          return true
        }
      }
      return false
    },
    icons() {
      let { ObjectModuleName } = this
      if (ObjectModuleName === 'parkingstall') {
        return 'svgs/employeePortal/parking5'
      } else if (ObjectModuleName === 'desks') {
        return 'svgs/employeePortal/icon-hot-desk-2'
      } else if (ObjectModuleName === 'space') {
        return 'svgs/employeePortal/meeting-room5'
      }
      return 'svgs/employeePortal/icon-hot-desk-2'
    },
    ObjectModuleName() {
      return this.properties?.moduleName ? this.properties.moduleName : null
    },
    space() {
      return this.record
    },
    bookingList() {
      if (this.space) {
        return this.properties?.spacebookingmap &&
          this.properties.spacebookingmap[this.space.id]
          ? this.properties.spacebookingmap[this.space.id]
          : []
      }
      return []
    },
    bookingState() {
      return this.bookingList.length ? 'Partially available' : 'Fully available'
    },
    location() {
      let { space } = this
      return `${space.building?.name ? space.building.name + ' - ' : ''}${
        space.floor?.name ? space.floor.name : ''
      }`
    },
    dateString() {
      let { startTime, endTime } = this.timeRange
      let startDate = this.getUserDate(startTime)
      let endDate = this.getUserDate(endTime)
      if (startDate !== endDate) {
        return `${startDate} to ${endDate}`
      }
      return startDate
    },
    spaceId() {
      return this.space?.id ? this.space.id : null
    },
    bookingStartDay() {
      let { startTime } = this.timeRange
      return startTime
        ? moment(startTime)
            .startOf('day')
            .valueOf()
        : null
    },
    bookingEndDay() {
      let { endTime } = this.timeRange
      return endTime
        ? moment(endTime)
            .endOf('day')
            .valueOf()
        : null
    },
    bookingDate() {
      let dt = new Date(this.bookingStartDay || this.getCurrentTime)
      return dt.format('YYYY-MM-DD')
    },
    bookingHour() {
      let { startTime } = this.timeRange
      let dt = new Date(startTime)
      return dt.getHours()
    },
    timeString() {
      let { startTime, endTime } = this.timeRange
      let start = this.getUserTime(startTime)
      let end = this.getUserTime(endTime)
      if (start !== end) {
        return `${start} to ${end}`
      }
      return start
    },
  },
  methods: {
    setDefaultTab() {
      this.activeName = 'quickview'
    },
    openRecordSummary(id) {
      let viewname = 'all'
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.ObjectModuleName, pageTypes.OVERVIEW) || {}

        if (name) {
          let routeData = this.$router.resolve({
            name,
            params: {
              viewname,
              id,
            },
          })
          window.open(routeData.href, '_blank')
        } else {
          this.$message.warning('No summary page found found')
        }
      }
    },
    async loadSpaceDetails() {
      let { error, data } = await API.get(
        `/v3/modules/data/summary?id=${this.spaceId}&moduleName=space`
      )
      if (error) {
        this.$message.success('Error while loading ')
      } else {
        let { space } = data
        let { amenities } = space
        if (space != null) {
          this.spaceSummary = space
          this.amenities = amenities || []
        }
      }
    },
    async getPropertiesData() {
      this.moveText = ''
      let params = {
        objectId: this.objectId,
        viewMode: this.viewerMode,
        startTime: this.timeRange.startTime,
        endTime: this.timeRange.endTime,
        newBooking: true,
      }
      this.loading = true

      await API.get(`/v3/floorplan/propertiesdata`, params)
        .then(({ data }) => {
          if (data?.properties) {
            let { properties } = data
            this.properties = this.$helpers.cloneObject(properties)
            if (properties?.moduleName) {
              this.moduleName = properties.moduleName
            }
            if (properties?.record) {
              this.record = properties.record
            }
            if (
              properties?.facilitybooking &&
              !isEmpty(properties.facilitybooking)
            ) {
              if (
                this.record &&
                this.record?.id &&
                properties.facilitybooking[this.record.id]
              ) {
                this.bookings = properties.facilitybooking[this.record.id]
              }
            }
            if (properties?.facility && !isEmpty(properties.facility)) {
              if (
                this.record &&
                this.record?.id &&
                properties.facility[this.record.id]
              ) {
                this.facility = properties.facility[this.record.id]
              }
            }

            if (properties?.object) {
              this.feature = properties.object
            }
          }
          this.loading = false
        })
        .catch(error => {
          this.loading = false
          if (error) {
            this.$message.error(`Error While fetching the data`)
          }
        })
    },
    async loadPhotos() {
      this.loading = true
      let { data } = await API.get(
        `/photos/get?module=basespacephotos&parentId=${this.spaceId}`
      )
      if (data) {
        let { photos = [] } = data || []
        this.photosList = photos || []
        this.loading = false
      }
    },
    openFilePreview() {
      if (!isEmpty(this.photosList)) {
        this.imagePreviewVisibility = true
      }
      let siteCarouselRef = this.$refs.siteCarousel
      if (!isEmpty(siteCarouselRef)) {
        this.photoPreviewIndex = siteCarouselRef.activeIndex
      } else {
        this.photoPreviewIndex = 0
      }
    },
  },
}
</script>
<style>
.filter-amenity-name.el-tag {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 15px;
  letter-spacing: normal;
  text-align: justify;
  color: #5f6368;
  border-radius: 2px;
  border: solid 1px #ebedf4;
  height: 21px;
  background: #f7f6f6;
  padding: 1px 8px;
  margin-right: 4px !important;
  margin-bottom: 5px;
}
.card-occupancy {
  border: solid 1px #ebedf4;
  border-radius: 2px;
  padding: 1px 8px 1px 8px !important;
  color: #5f6368;
  background: #f7f6f6;
  height: 21px;
}
.people-container {
  padding: 2px 10px 0px 0px;
}

.flex-col {
  display: flex;
  flex-direction: column;
}
.floor-img-container {
  width: 100%;
  height: 100%;
}
.new-properties-section {
  width: 320px;
  border-radius: 8px;
  z-index: 4;
}
.fp-properties-text {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #1d2222;
  padding: 0 20px;
}
.fp-properties-subtext {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #969a9e;
  margin-left: 26px;
  margin-top: 2px;
}
.fp-prop-status {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #117203;
  margin-left: 45px;
}
.fp-prop-status.partial {
  color: #fa6b03;
}
.fp-properties-image {
  background: #f7f7f7;
  border-radius: 4px;
  height: 150px;
  margin-top: 0px;
  margin-bottom: 20px;
  bottom: 15px;
}
.fp-prop-btn {
  width: 100%;
  padding: 0 20px;
  position: absolute;
  bottom: 0;
}
.fp-prop-btn .el-button {
  width: 100%;
  background: #ff3184;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: normal;
  color: #fff;
  text-transform: inherit;
  border: 1px solid #0053cc;
  border-radius: 5px;
}
.fp-prop-btn .el-button:hover {
  border-color: #ff3184;
  background: #ff3184;
}
.new-properties-section .el-tabs__nav-scroll {
  padding-left: 20px;
}
.new-properties-section .el-tabs__nav {
  display: flex;
}
.new-properties-section .el-tabs__item.is-active {
  font-weight: 500;
}
.new-properties-section .el-tabs__item {
  display: flex;
  align-items: center;
  height: 45px;
  text-transform: inherit;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: 0.3px;
  color: #464646;
}
.new-properties-section .el-tabs__header {
  margin-bottom: 1px;
}
.h100 {
  height: 100%;
}
.w100 {
  width: 100%;
}
.new-properties-section .el-tabs__content {
  height: 85%;
  position: relative;
}
.floating-icons {
  height: 45px;
  position: absolute;
  right: 0;
  display: flex;
  align-items: center;
  z-index: 10;
}
.f17 {
  font-size: 17px;
}
.employee-portal-homepage .fp-prop-btn .el-button {
  background: #0053cc;
  border: 1px solid #0053cc;
}
.employee-portal-homepage .fp-prop-btn .el-button:hover {
  border-color: #0145a9;
  background: #0145a9;
}
.carousel-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  background-color: rgb(222 236 255);
}
.bL0 {
  border-left: 0;
}
.space-name {
  font-size: 16px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #334056;
}
.space-container .sub-content {
  font-size: 12px;
  display: flex;
  flex-direction: row;
  padding-top: 2px;
  font-weight: 400;
  opacity: 0.7;
}
.sp-id-header {
  font-weight: normal;
  font-size: 12px;
  color: #24b096;
}
.space-container {
  display: flex;
  flex-direction: column;
}
.dateString {
  color: #303133;
  font-weight: 400;
  font-size: 13px;
}
</style>
<style scoped>
.spacedetails-card-content {
  padding: 0 20px;
  line-height: 15px;
  margin-bottom: 20px;
}
.opacity7 {
  opacity: 0.7;
}
.pB15 {
  padding-bottom: 15px !important;
}
</style>
