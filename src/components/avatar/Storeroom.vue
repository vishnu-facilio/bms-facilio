<template>
  <div v-if="storeRoom" class="inline">
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
          height="180px"
          :autoplay="false"
          indicator-position="none"
        >
          <el-carousel-item
            v-for="(photo, index) in photos"
            :key="index"
            v-if="photo.url"
          >
            <img
              @click="openPhotoUrl(photo.originalUrl)"
              target="_blank"
              class="fc-avatar-square"
              :class="sizeClass"
              :src="photo.url"
            />
          </el-carousel-item>
        </el-carousel>
      </div>
      <div
        class="q-item-side q-item-side-left q-item-section relative"
        v-else-if="isSummary"
      >
        <img
          class="fc-avatar-square"
          :class="sizeClass"
          :src="avatarUrl"
          v-if="avatarUrl"
        />
      </div>
      <div class="q-item-side q-item-side-left q-item-section relative" v-else>
        <img
          class="fc-avatar"
          :class="sizeClass"
          :src="avatarUrl"
          v-if="avatarUrl"
        />
      </div>
      <div v-if="showName">
        <div>
          <span class="q-item-label">{{ storeRoom.name }}</span>
        </div>
      </div>
      <div
        v-if="
          showUpload &&
            (typeof disableUpload === 'undefined' || disableUpload === false)
        "
      >
        <span class="upload-space-photos pointer" @click="showUploadPhotoDialog"
          ><i class="fa fa-camera" aria-hidden="true"></i
        ></span>
        <f-upload-photos
          ref="uploadDialog"
          :list="photos"
          module="storeroomphotos"
          :record="storeRoom"
        ></f-upload-photos>
      </div>
    </div>
  </div>
</template>
<script>
import FUploadPhotos from '@/FUploadPhotos'

export default {
  props: [
    'size',
    'storeRoom',
    'name',
    'hovercard',
    'disableUpload',
    'disableSlide',
    'isSummary',
  ],
  data() {
    return {
      avatarUrl: null,
      sizeClass: 'fc-avatar-' + this.size,
      hoverCardId: 'spaceHoverCardId-' + this.storeRoom.id,
      showName: typeof this.name === 'undefined' || this.name === 'true',
      showHoverCard:
        typeof this.hovercard === 'undefined' || this.hovercard === 'true',
      loadHoverCardData: false,
      photosLoading: true,
      photos: [],
      sliderEnabledSize: ['shuge', 'huge', 'xhuge', 'mxhuge'],
    }
  },
  components: {
    FUploadPhotos,
  },
  mounted() {
    this.loadAvatarUrl()
  },
  computed: {
    showSlide() {
      if (!this.photos.length) {
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
  },
  watch: {
    storeRoom: function() {
      this.loadAvatarUrl()
    },
  },
  methods: {
    loadAvatarUrl() {
      let self = this
      if (this.storeRoom.id > 0) {
        self.photosLoading = true
        self.$http
          .get(
            '/photos/get?module=storeroomphotos&parentId=' + this.storeRoom.id
          )
          .then(function(response) {
            self.photos = response.data.photos ? response.data.photos : []
            self.photosLoading = false
            if (self.photos.length) {
              for (let photo of self.photos) {
                if (photo.url) {
                  self.avatarUrl = photo.url
                  break
                }
              }
            }
          })
      }

      if (!this.storeRoom.avatarUrl) {
        this.avatarUrl = this.getDefaultAvatar()
        this.storeRoom.avatarUrl = this.avatarUrl
      } else {
        this.avatarUrl = this.storeRoom.avatarUrl
      }
    },
    openPhotoUrl(url) {
      window.open(url, '_blank')
    },
    getDefaultAvatar() {
      if (this.storeRoom) {
        return require('statics/inventory/store-img.jpg')
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
</style>
