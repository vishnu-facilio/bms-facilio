<template>
  <div class="fc-v1-site-page wrapper" id="site-header-wrapper">
    <el-header
      height="114px"
      class="site-header-scroll-show-header scroll-to-active hide"
      id="scroll-to-active"
    >
      <div class="site-header-scroll-show slideInDown scroll-to-active">
        <div>
          <div class="fc-black-13 text-left">{{ `#${record.id}` }}</div>
          <div class="fc-black2-20 bold pT5">
            {{ record.name }}
            <span v-if="status" class="fc-newsummary-chip vertical-middle">
              {{ status }}
            </span>
          </div>
          <div class="flex-middle pT10">
            <div class="pR20 border-right1">
              <div class="label-txt-black">
                {{ count['buildings'] }} {{ $t('space.sites.buildings') }}
              </div>
            </div>
            <div class="pL20 pR20 border-right1">
              <div class="label-txt-black">
                {{ count['allSpaces'] }} {{ $t('space.sites.spaces') }}
              </div>
            </div>
            <div class="pL20">
              <div class="label-txt-black">
                {{ count['independent_spaces'] }}
                {{ $t('space.sites.independent_spaces') }}
              </div>
            </div>
          </div>
        </div>
        <div class="flex-middle justify-content-end p30 flex-shrink-0">
          <div class="z-10 d-flex">
            <TransitionButtons
              class="mL10"
              ref="TransitionButtons"
              :moduleName="moduleName"
              :record="record"
              :transformFn="transformFn"
              :updateUrl="updateUrl"
              :disabled="isApprovalEnabled"
              buttonClass="portfolio-transition-button"
              @transitionSuccess="onTransitionSuccess"
              @transitionFailure="() => {}"
            ></TransitionButtons>

            <el-dropdown
              class="mL10 pointer site-header-button mR10"
              trigger="click"
              v-if="$hasPermission('space:CREATE')"
              @command="moduleName => openNewForm(moduleName)"
            >
              <el-button type="primary" class="" style="width: inherit;">
                {{ $t('common._common.add')
                }}<i class="el-icon-arrow-down pL10 fc-white-12"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :key="1" :command="'building'"
                  >New Building</el-dropdown-item
                >
                <el-dropdown-item :key="2" :command="'space'"
                  >New Space</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>

            <el-button-group
              v-if="$hasPermission('space:UPDATE')"
              class="fc-btn-group-white site-action-btn"
            >
              <el-button
                v-if="canEdit"
                @click="openSiteEditForm"
                type="primary"
                icon="el-icon-edit"
              ></el-button>
              <el-button type="primary">
                <el-dropdown @command="action => dropDownAction(action)">
                  <span class="el-dropdown-link">
                    <inline-svg
                      src="svgs/menu"
                      class="vertical-middle"
                      iconClass="icon icon-sm"
                    >
                    </inline-svg>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :key="2" :command="'changePhoto'">
                      {{ $t('space.sites.add_photo') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :key="3" :command="'siteQrDownload'">
                      Download Qr
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-button>
            </el-button-group>
          </div>
        </div>
      </div>
    </el-header>
    <div class="navbar">
      <div @click="openFilePreview">
        <el-carousel
          ref="siteCarousel"
          indicator-position="none"
          trigger="click"
          :autoplay="false"
          :arrow="carouselArrow"
          height="250px"
          class="navbar"
        >
          <el-carousel-item
            v-for="(photo, photoIndex) in photosList"
            :key="photoIndex"
            class="fc-v1-site-slider-overlay pointer"
          >
            <img
              v-if="photo.originalUrl"
              :src="$helpers.getImagePreviewUrl(photo.photoId)"
              class="fc-carouselsite-image"
              @click="openFilePreview"
            />
            <img
              v-else
              src="~assets/svgs/spacemanagement/site-header.jpg"
              class="fc-carouselsite-image"
            />
          </el-carousel-item>
        </el-carousel>
      </div>
      <div class="fc-v1-site-slider-content">
        <div class="flex-middle justify-content-end p30">
          <div class="z-10 d-flex">
            <TransitionButtons
              class="mL10"
              ref="TransitionButtons"
              buttonClass="portfolio-transition-button"
              :moduleName="moduleName"
              :record="record"
              :transformFn="transformFn"
              :updateUrl="updateUrl"
              :disabled="isApprovalEnabled"
              @transitionSuccess="onTransitionSuccess"
              @transitionFailure="() => {}"
            ></TransitionButtons>

            <el-dropdown
              class="mL10 pointer site-header-button mR10"
              trigger="click"
              v-if="$hasPermission('space:CREATE')"
              @command="moduleName => openNewForm(moduleName)"
            >
              <el-button type="primary" class="">
                {{ $t('common._common.add')
                }}<i class="el-icon-arrow-down pL10 fc-white-12"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :key="1" :command="'building'"
                  >New Building</el-dropdown-item
                >
                <el-dropdown-item :key="2" :command="'space'"
                  >New Space</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>

            <el-button-group
              v-if="$hasPermission('space:UPDATE')"
              class="fc-btn-group-white"
            >
              <el-button
                v-if="canEdit"
                @click="openSiteEditForm"
                type="primary"
                icon="el-icon-edit"
              ></el-button>
              <el-button type="primary">
                <el-dropdown @command="action => dropDownAction(action)">
                  <span class="el-dropdown-link">
                    <inline-svg
                      src="svgs/menu"
                      class="vertical-middle"
                      iconClass="icon icon-sm"
                    >
                    </inline-svg>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :key="2" :command="'changePhoto'">
                      {{ $t('space.sites.add_photo') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :key="3" :command="'siteQrDownload'">
                      Download Qr
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-button>
            </el-button-group>
          </div>
        </div>
        <div class="flex-middle justify-content-space p30 mT40">
          <div class="z-10">
            <div class="fc-white-13 text-left">{{ `#${record.id}` }}</div>
            <div class="fc-white-28">
              {{ record.name }}
              <span v-if="status" class="fc-newsummary-chip vertical-middle">
                {{ status }}
              </span>
            </div>
          </div>
          <div class="flex-middle z-10" v-if="record.managedBy">
            <UserAvatar
              size="lg"
              :user="record.managedBy"
              :name="false"
            ></UserAvatar>
            <div>
              <div class="white-color f13">
                {{ $t('space.sites.managed_by') }}
              </div>
              <div class="white-color f14 fwBold">
                {{ $store.getters.getUser(record.managedBy.id).name }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        v-if="!$validation.isEmpty(count)"
        class="fc-site-building-space-sec"
      >
        <div class="flex-middle justify-content-space">
          <div class="flex-middle">
            <div class="pR20">
              <div class="fc-white-22 text-center">
                {{ count['buildings'] }}
              </div>
              <div class="fc-white-13 op8 pT10">
                {{ $t('space.sites.buildings') }}
              </div>
            </div>
            <el-divider direction="vertical"></el-divider>
            <div class="pL20 pR20">
              <div class="fc-white-22 text-center">
                {{ count['allSpaces'] }}
              </div>
              <div class="fc-white-13 op8 pT10">
                {{ $t('space.sites.spaces') }}
              </div>
            </div>
            <el-divider direction="vertical"></el-divider>
            <div class="pL20">
              <div class="fc-white-22 text-center">
                {{ count['independent_spaces'] }}
              </div>
              <div class="fc-white-13 op8 pT10">
                {{ $t('space.sites.independent_spaces') }}
              </div>
            </div>
          </div>
          <div class="text-right">
            <div
              v-if="record.location"
              @click="
                $helpers.openInMap(record.location.lat, record.location.lng)
              "
              class="fc-white-14 pointer"
            >
              <inline-svg
                src="svgs/maps"
                class="vertical-middle"
                iconClass="icon icon-sm"
              ></inline-svg>
              {{ record.location.name }}
            </div>
            <div class="fc-white-14 op6 pT10">
              {{
                `${
                  record.grossFloorArea > 0 ? record.grossFloorArea : '---'
                } ${getUnit('grossFloorArea')} / ${
                  record.area > 0 ? record.area : '---'
                } ${getUnit('area')}`
              }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <SpacePhotoUpdater
      ref="space-photos-updater"
      :record="record"
      :photosModuleName="'basespacephotos'"
      @photosList="data => setPhotos(data)"
    ></SpacePhotoUpdater>
    <PreviewFile
      :visibility.sync="imagePreviewVisibility"
      v-if="imagePreviewVisibility"
      :previewFile="getFormattedFile[photoPreviewIndex]"
      :files="getFormattedFile"
    ></PreviewFile>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import SpacePhotoUpdater from './PhotosUpdater'
import PreviewFile from '@/PreviewFile'
import SpaceMixin from '../helpers/SpaceHelper'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { mapGetters, mapState } from 'vuex'
import { API } from '@facilio/api'
import { getApp, isWebTabsEnabled } from '@facilio/router'

export default {
  props: [
    'moduleName',
    'record',
    'transformFn',
    'updateUrl',
    'isApprovalEnabled',
    'onTransitionSuccess',
    'canEdit',
  ],
  mixins: [SpaceMixin],
  components: {
    UserAvatar,
    SpacePhotoUpdater,
    PreviewFile,
    TransitionButtons,
  },
  data() {
    return {
      count: {},
      downloadQRurl: null,
      value: null,
      photos: [],
      imagePreviewVisibility: false,
      photoPreviewIndex: 0,
    }
  },
  created() {
    this.init()
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)

    if (!this.newSiteSummary) {
      window.addEventListener('scroll', this.scrollFunction, true)
      window.addEventListener('scroll', this.scrollToView, true)
    }
  },
  beforeDestroy() {
    if (!this.newSiteSummary) {
      window.removeEventListener('scroll', this.scrollFunction, true)
      window.removeEventListener('scroll', this.scrollToView, true)
    }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapGetters(['getTicketStatus']),
    photosList() {
      let { photos } = this
      if (isEmpty(photos)) {
        return [
          {
            originalUrl: null,
          },
        ]
      } else {
        return photos
      }
    },
    carouselArrow() {
      let { photosList } = this
      if (
        !isEmpty(photosList) &&
        isArray(photosList) &&
        photosList.length > 1
      ) {
        return 'hover'
      } else {
        return 'never'
      }
    },
    status() {
      let { record, moduleName, $getProperty, getTicketStatus } = this
      let status =
        $getProperty(record, 'moduleState.id', null) &&
        getTicketStatus(record.moduleState.id, moduleName)

      return status ? status.displayName : null
    },
    siteQrPdfUrl() {
      let url
      let appName = getApp().linkName
      if (isWebTabsEnabled()) {
        url = `/${appName}/pdf/site/${this.record.id}`
      } else {
        url = `/app/pdf/site/${this.record.id}`
      }

      return window.location.protocol + '//' + window.location.host + url
    },
  },
  methods: {
    getUnit(fieldName) {
      let { metaInfo } = this
      if (!isEmpty(metaInfo)) {
        let areaField = metaInfo.fields.find(field => field.name === fieldName)
        if (!isEmpty(areaField) && !isEmpty(areaField.unit)) {
          return areaField.unit
        }
      }
      return 'sq. ft'
    },
    init() {
      let { record } = this
      if (isEmpty((record || {}).id)) return

      API.post(`campus/reportcards`, {
        campusId: (record || {}).id,
        fetchReportCardsMeta: ['spaceCount'],
      }).then(({ data: { reports }, error }) => {
        if (!error) {
          if (reports) this.count = reports
        } else {
          this.$message.error(error.message || 'Error Occurred')
        }
      })
    },
    openSiteEditForm() {
      eventBus.$emit('openSpaceManagementForm', {
        isNew: false,
        data: this.record,
        module: 'site',
        visibility: true,
      })
    },
    openNewForm(moduleName) {
      if (moduleName === 'building') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'building',
          site: this.record,
        })
      } else if (moduleName === 'space') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          site: this.record,
        })
      }
    },
    async invokeDeleteDialog(data) {
      let promptObj = {
        title: this.$t(`space.sites.delete_site`),
        message: this.$t(`space.sites.delete_site_msg`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      }
      let { id } = data
      let value = await this.$dialog.confirm(promptObj)

      if (value)
        API.post('/v2/space/delete', { id: id }).then(({ error }) => {
          if (!error) {
            this.$message.success(this.$t('space.sites.delete_success'))
          } else {
            this.$message.error(error.message)
          }
        })
    },
    dropDownAction(action) {
      if (action === 'delete') {
        this.invokeDeleteDialog(this.record)
      } else if (action === 'changePhoto') {
        this.$refs['space-photos-updater'].open()
      } else if (action === 'downloadQr') {
        this.downloadQR()
      } else if (action === 'siteQrDownload') {
        this.redirectToQrPage()
      }
    },
    redirectToQrPage() {
      window.open(this.siteQrPdfUrl)
    },
    async downloadQR() {
      let { record } = this
      let { id: siteId } = record || {}
      let { data, error } = await API.get('v3/site/qr/download', { siteId })
      if (!error) {
        let { downloadURL } = data || {}
        this.downloadQRurl = downloadURL
      }
    },
    setPhotos(data) {
      this.$set(this, 'photos', data)
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
    scrollFunction() {
      if (
        document.body.scrollTop > 300 ||
        document.getElementById('site-header-wrapper').parentElement.scrollTop >
          100
      ) {
        document.querySelector('.navbar').style.display = 'none'
        document.querySelector('.navbar').style.opacity = '0'
        document.querySelector('.navbar').style.transition = 'height 0.3s;'
      } else {
        document.querySelector('.navbar').style.display = 'block'
        document.querySelector('.navbar').style.opacity = '1'
        document.querySelector('.navbar').style.transition = 'height 5s;'
        document.querySelector('.navbar').style.transitionProperty = 'height'
        document.querySelector('.navbar').style.transitionDuration = '5s'
      }
    },
    scrollToView() {
      if (
        document.body.scrollTop > 230 ||
        document.getElementById('site-header-wrapper').parentElement.scrollTop >
          100
      ) {
        const element = document.querySelector('#scroll-to-active')
        element.classList.add('slideInDown')
        document.querySelector('#scroll-to-active').style.display = 'block'
        document.querySelector('#scroll-to-active').style.opacity = '1'
        document.querySelector('#scroll-to-active').style.animationName =
          'slideInDown'
        document.querySelector('.scroll-to-active').style.animationName =
          'slideInDown'
      } else {
        document.querySelector('#scroll-to-active').style.display = 'none'
        document.querySelector('#scroll-to-active').style.opacity = '0'
        document.querySelector('#scroll-to-active').style.transitionProperty =
          'height'
        document.querySelector('#scroll-to-active').style.transitionDuration =
          '2s'
      }
    },
  },
}
</script>
