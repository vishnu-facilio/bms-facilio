<template>
  <div class="h100 w100">
    <div v-if="isLoading" class="spinner">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="spacedetails-card-container">
      <div class="spacedetails-card" v-if="space">
        <el-row :gutter="20" class="mR0 mL0" id="space-detail-section">
          <el-col :span="24" class="carousel-container p0">
            <div
              v-if="photosList.length"
              @click="openFilePreview"
              style="width:100%"
            >
              <el-carousel
                ref="siteCarousel"
                indicator-position="none"
                trigger="click"
                :autoplay="false"
                :arrow="showArrow"
                height="150px"
                class="navbar"
              >
                <el-carousel-item
                  v-for="(photo, photoIndex) in photosList"
                  :key="photoIndex"
                  class="fc-v1-site-slider-overlay pointer"
                >
                  <div class="emp-image-con">
                    <img
                      v-if="photo.originalUrl"
                      :src="$helpers.getImagePreviewUrl(photo.photoId)"
                      class="fc-carouselsite-image"
                      height="150px"
                      width="100%"
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
          </el-col>
          <el-col :span="24" class="spacedetails-card-content">
            <div class="sp-id-header pT10">
              {{ `#${space.id}` }}
            </div>
            <div class="sp-sm-header ">
              {{ `${space.name}` }}
            </div>
            <div class="inline-block pB10" v-if="space">
              <div class="flex-middle">
                <div class="space-container">
                  <div class="ellipsis sub-content">
                    <div v-if="space.building">
                      {{ `${space.building.name} ` }}
                    </div>
                    <div v-if="space.floor">
                      {{ `, ${space.floor.name}` }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="pB10 spacedetails-card-content"
            v-if="space.maxOccupancy > 0"
          >
            <el-tooltip placement="top" trigger="hover" content="Capacity">
              <div class="inline-block people-container">
                <div class="flex-middle card-occupancy">
                  <inline-svg
                    style="padding-right: 8px;"
                    src="svgs/employeePortal/people"
                    iconClass="icon text-center icon-sm15 vertical-bottom"
                  ></inline-svg>
                  <div class="ellipsis main-content">
                    {{ `${space.maxOccupancy} ` }}
                  </div>
                </div>
              </div>
            </el-tooltip>
          </el-col>
          <el-col
            :span="24"
            class="pB10 spacedetails-card-content"
            v-if="amenities.length > 0"
          >
            <div class="inline-block people-container">
              <div class="flex-middle pB5" style="flex-wrap: wrap;">
                <div v-for="name in amenities" :key="name">
                  <el-tag v-if="name" class="filter-amenity-name">
                    {{ name }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="mT10 calender-section" :style="{ height: calenderHeight }">
          <SpaceBookingCalander
            moduleName="spacebooking"
            :events="events"
            class="h100"
            :startHour="bookingHour"
            :startDate="bookingStartDay"
            :endDate="bookingEndDay"
            :selectDate="bookingDate"
            :spaceId="newSpaceId"
            :spaceCategory="spaceCategory"
            :showToday="true"
            :goToBooking="goToBooking"
            :bookingHour="bookingHour"
            :bookingDate="bookingStartDay"
            :isBookingCancel="isBookingCancel"
          >
          </SpaceBookingCalander>
        </div>
      </div>
    </div>
    <PreviewFile
      :visibility.sync="imagePreviewVisibility"
      v-if="imagePreviewVisibility"
      :previewFile="getFormattedFile[photoPreviewIndex]"
      :files="getFormattedFile"
    ></PreviewFile>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
import Spinner from '@/Spinner'
import SpaceBookingCalander from 'src/components/SpaceBookingCalendar.vue'
import moment from 'moment-timezone'
import debounce from 'lodash/debounce'
import { isEmpty } from 'lodash'
import PreviewFile from '@/PreviewFile'

export default {
  props: [
    'widget',
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'approvalMeta',
    'hideTitleSection',
    'goToBooking',
  ],
  data() {
    return {
      space: null,
      isLoading: false,
      bookings: null,
      events: [],
      spaceCategory: null,
      clonedFormObject: null,
      newSpaceId: null,
      photosList: [],
      imagePreviewVisibility: false,
      photoPreviewIndex: null,
      calenderHeight: 'calc(100% - 400px)',
    }
  },
  components: { Spinner, SpaceBookingCalander, PreviewFile },
  computed: {
    showArrow() {
      if (this.photosList?.length && this.photosList.length > 1) {
        return 'hover'
      }
      return 'never'
    },
    isBookingCancel() {
      let { details } = this
      return details?.isCancelled || false
    },
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
    spaceId() {
      return this.details?.space?.id ? this.details.space.id : null
    },
    bookingStartDay() {
      return this.details?.bookingStartTime
        ? moment(this.details.bookingStartTime)
            .startOf('day')
            .valueOf()
        : moment(new Date())
            .startOf('day')
            .valueOf()
    },
    bookingEndDay() {
      return this.details?.bookingEndTime
        ? moment(this.details.bookingEndTime)
            .endOf('day')
            .valueOf()
        : moment(new Date())
            .startOf('day')
            .valueOf()
    },
    bookingDate() {
      let dt = new Date(this.bookingStartDay || this.getCurrentTime)
      return dt.format('YYYY-MM-DD')
    },
    bookingHour() {
      let dt = this.details?.bookingStartTime
        ? new Date(this.details?.bookingStartTime)
        : new Date()
      return dt.getHours()
    },

    header() {
      let header = ''
      if (this.space?.building?.name) {
        header += `${this.space.building.name} - `
      }
      if (this.space?.floor?.name) {
        header += `${this.space.floor.name}`
      }
      return header
    },
    amenities() {
      let amenities = []
      if (this.space?.amenities && this.space?.amenities.length) {
        this.space.amenities.map(rt => amenities.push(rt.name))
      }
      return amenities
    },
  },
  beforeDestroy() {
    eventBus.$off('FORM_CHANGED', () => {})
  },
  watch: {
    newSpaceId: {
      handler() {
        this.loadPhotos()
      },
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      if (this.spaceId) {
        await this.loadSpaceDetails()
      }
      this.initEvents()
      this.initWidgetHeight()
    },
    initWidgetHeight() {
      let el = document.getElementById('space-detail-section')
      if (el?.offsetHeight) {
        this.isLoading = true
        let height = el.offsetHeight + 50
        this.calenderHeight = `calc(100% - ${height}px)`
        this.$nextTick(() => {
          this.isLoading = false
        })
      }
    },
    initEvents() {
      eventBus.$on('FORM_CHANGED', formData => {
        this.handeleFormDataEvent(formData)
      })
    },
    handeleFormDataEvent: debounce(function(formData) {
      this.handeleFormData(formData)
    }, 1000),
    async handeleFormData(formData) {
      if (
        this.clonedFormObject?.space?.id &&
        formData?.space?.id &&
        formData.space.id === this.clonedFormObject.space.id
      ) {
        let { id } = formData.space
        await this.loadSpaceDetails(id)
      }
      this.clonedFormObject = formData
      this.prepareSchdulerData(formData)
      console.log('form data', formData)
      this.initWidgetHeight()
    },
    prepareSchdulerData(formData) {
      let event = {}
      this.events = []
      if (formData.name) {
        this.$set(event, 'title', formData.name)
      }
      if (
        formData.bookingStartTime &&
        formData.bookingEndTime &&
        formData.bookingStartTime < formData.bookingEndTime
      ) {
        let startDate = new Date(formData.bookingStartTime)
        let endDate = new Date(formData.bookingEndTime)
        this.$set(event, 'start', startDate.format('YYYY-MM-DD HH:mm'))
        this.$set(event, 'end', endDate.format('YYYY-MM-DD HH:mm'))
      }
      this.$set(event, 'class', 'new-booking')
      this.events.push(event)
    },
    async loadSpaceDetails(id) {
      let { error, data } = await API.get(
        `/v3/modules/data/summary?id=${id || this.spaceId}&moduleName=space`
      )
      if (error) {
        this.$message.success('Error while loading ')
      } else {
        let { space } = data
        if (space != null) {
          this.space = space
          this.newSpaceId = this.space.id
          this.isLoading = false
          this.spaceCategory = space.spaceCategory?.name || 'Space'
        }
      }
    },
    async loadPhotos() {
      let { data } = await API.get(
        `/photos/get?module=basespacephotos&parentId=${this.newSpaceId}`
      )
      if (data) {
        let { photos = [] } = data || []
        this.photosList = photos || []
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
.emp-image-con {
  display: flex;
  justify-content: center;
}
.fc-carouselsite-image {
  width: auto;
}
.h100 {
  height: 100%;
}
.calender-section {
  height: 100%;
  border-top: 1px solid #ebedf4;
}
.carousel-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  background-color: rgb(222 236 255);
}
.floor-img-container {
  width: 100%;
  height: 100%;
}
.space-icon-container {
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  display: grid;
  background: rgb(222 236 255);
  width: 100%;
  margin: auto;
  height: 150px;
}
.spacedetails-card {
  height: 100%;
  font-size: 12px;
  font-weight: 400;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #5f6368;
  background-color: #fff;
  flex-direction: column;
  display: flex;
}
.spacedetails-card .detail-container {
  min-height: 32px;
  max-height: 64px;
  padding: 10px 20px 10px 20px;
}
.spacedetails-card .detail-container .flex-middle {
  align-items: normal;
}
.spacedetails-card .people-container {
  padding: 2px 10px 0px 0px;
}
.spacedetails-card .space-container {
  display: flex;
  flex-direction: column;
}
.spacedetails-card .space-container .main-content {
  font-size: 14px;
}
.spacedetails-card .space-container .icon-container {
  align-items: normal;
}
.spacedetails-card .space-container .sub-content {
  font-size: 12px;
  display: flex;
  flex-direction: row;
  padding-top: 5px;
}
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
.filter-amenity-name-test.el-tag {
  border: 1px solid #0053cc;
  background: no-repeat;
  color: #0053cc;
  font-weight: 600;
  padding-left: 10px;
  padding-right: 10px;
  height: 25px;
  line-height: 22px;
}

.spacedetails-card .em-scn-text {
  padding-top: 10px;
  padding-left: 10px;
  font-size: 14px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #334056;
}
/* .spacedetails-card .el-carousel__container {
  height: 200px;
  width: 400px;
  background: #a5fba5;
  margin: auto;
  border-radius: 2px;
} */
.sp-sm-header {
  font-size: 16px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #334056;
}
.sp-id-header {
  font-weight: normal;
  font-size: 12px;
  color: #24b096;
}
.space-header {
  font-size: 12px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #949a9e;
  max-width: 100%;
  width: auto;
  padding: 5px;
  word-break: break-word;
}
.space-desc {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.29;
  letter-spacing: normal;
  color: #949a9e;
}
.spacedetails-card-container {
  height: 100%;
}
.spinner {
  background-color: #fff;
  height: 100%;
}
.card-occupancy {
  border: solid 1px #ebedf4;
  border-radius: 2px;
  padding: 1px 8px 1px 8px !important;
  color: #5f6368;
  background: #f7f6f6;
  height: 21px;
}
.spacedetails-card-content {
  padding-left: 15px !important;
  padding-right: 15px !important;
  padding-top: 5px;
}
</style>
