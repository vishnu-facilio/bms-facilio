<template>
  <div v-if="asset" class="inline">
    <div
      class="fc-avatar-element q-item-division relative-position cursor-pointer"
    >
      <div
        class="q-item-side q-item-side-left q-item-section relative"
        :class="sizeClass"
        v-if="
          showSlide &&
            (typeof disableSlide === 'undefined' || disableSlide === false)
        "
      >
        <el-carousel
          trigger="click"
          class="fc-avatar-carousel"
          :autoplay="false"
          indicator-position="none"
        >
          <template v-for="(photo, index) in photos">
            <el-carousel-item :key="index" v-if="photo.url">
              <img
                @click="openPhotoUrl(photo.originalUrl)"
                target="_blank"
                class="fc-avatar-square"
                :class="sizeClass"
                :src="photo.url"
              />
            </el-carousel-item>
          </template>
        </el-carousel>
      </div>
      <div v-else class="q-item-side q-item-side-left q-item-section relative">
        <img
          class="fc-avatar"
          :class="sizeClass"
          :src="avatarUrl"
          v-if="avatarUrl"
        />
      </div>
      <div v-if="showName">
        <div>
          <span class="q-item-label">{{ asset.name }}</span>
        </div>
      </div>
      <div
        v-if="
          showUpload &&
            (typeof disableUpload === 'undefined' || disableUpload === false)
        "
      >
        <span
          class="upload-space-photos pointer"
          @click="showUploadPhotoDialog"
        >
          <i class="fa fa-camera" aria-hidden="true"></i>
        </span>
        <f-upload-photos
          ref="uploadDialog"
          module="assetphotos"
          :list="photos"
          :record="asset"
          @updatePhotos="handleUpdate()"
        ></f-upload-photos>
      </div>
    </div>
  </div>
</template>
<script>
import FUploadPhotos from '@/FUploadPhotos'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: [
    'size',
    'asset',
    'name',
    'hovercard',
    'disableUpload',
    'disableSlide',
  ],
  data() {
    return {
      avatarUrl: null,
      sizeClass: 'fc-avatar-' + this.size,
      hoverCardId: 'spaceHoverCardId-' + this.asset.id,
      showName: typeof this.name === 'undefined' || this.name === 'true',
      showHoverCard:
        typeof this.hovercard === 'undefined' || this.hovercard === 'true',
      loadHoverCardData: false,
      photosLoading: true,
      photos: [],
      sliderEnabledSize: ['shuge', 'huge', 'xhuge', 'widget'],
    }
  },
  components: {
    FUploadPhotos,
  },
  mounted() {
    this.loadAvatarUrl()
  },
  created() {
    this.$store.dispatch('loadAssetType')
  },
  computed: {
    showSlide() {
      if (!this.photos || !this.photos.length) {
        return false
      } else {
        if (this.sliderEnabledSize.indexOf(this.size) >= 0) {
          let allUrlNull = true
          for (let photo of this.photos) {
            if (photo.url) {
              allUrlNull = false
              break
            }
          }
          return !allUrlNull
        } else {
          return false
        }
      }
    },
    showUpload() {
      return this.sliderEnabledSize.indexOf(this.size) >= 0
    },
    ...mapGetters(['getAssetType']),
  },
  watch: {
    asset: function() {
      this.loadAvatarUrl()
    },
  },
  methods: {
    async loadAvatarUrl() {
      let { asset, size, sliderEnabledSize, disableSlide } = this
      let { avatarUrl, id: assetId } = asset || {}

      if (
        sliderEnabledSize.indexOf(size) >= 0 &&
        (!disableSlide || (disableSlide && disableSlide === 'true'))
      ) {
        if (assetId) {
          this.photosLoading = true
          let { error, data } = await API.get(
            `/photos/get?module=assetphotos&parentId=${assetId}`
          )
          if (!error) {
            let { photos = [] } = data || {}
            if (!isEmpty(photos)) {
              photos.forEach(photo => {
                let { url } = photo || {}
                if (!isEmpty(url)) {
                  avatarUrl = url
                  return
                }
              })
            }
            this.$set(this, 'photos', photos)
          }
          this.photosLoading = false
        }
      }
      if (!isEmpty(avatarUrl)) {
        this.$set(this, 'avatarUrl', avatarUrl)
      } else {
        let defaultAvatarUrl = this.getDefaultAvatar() || ''
        this.$set(this, 'avatarUrl', defaultAvatarUrl)
      }
    },
    handleUpdate() {
      this.loadAvatarUrl()
    },
    openPhotoUrl(url) {
      window.open(url, '_blank')
    },
    getDefaultAvatar() {
      if (this.asset && this.asset.type && this.asset.type.id) {
        let name = this.getAssetType(this.asset.type.id)
          ? this.getAssetType(this.asset.type.id).name
          : ''
        if (name === 'Plumbing Equipment') {
          return require('statics/space/assetempty.svg')
        } else if (name === 'Equipment') {
          return require('statics/space/assetempty.svg')
        } else if (name === 'Furniture') {
          return require('statics/space/chair.svg')
        } else if (name === 'Vehicle') {
          return require('statics/space/truck.svg')
        } else if (name === 'HVAC Equipment') {
          return require('statics/space/assetempty.svg')
        } else {
          return require('statics/space/assetempty.svg')
        }
      } else {
        return require('statics/space/assetempty.svg')
      }
    },
    loadHoverCard() {
      this.loadHoverCardData = true
    },
    hideHoverCard() {
      this.loadHoverCardData = false
    },
    showUploadPhotoDialog() {
      this.$refs.uploadDialog.open()
    },
  },
}
</script>
<style>
.upload-space-photos {
  position: absolute;
  bottom: 5px;
  right: 0px;
  display: none;
  z-index: 10;
  width: 100%;
  text-align: center;
  background: #fff;
  padding: 5px;
}

.fc-avatar-element:hover .upload-space-photos {
  display: block;
}

.fc-avatar-carousel .el-carousel__container {
  height: 120px;
}
</style>
