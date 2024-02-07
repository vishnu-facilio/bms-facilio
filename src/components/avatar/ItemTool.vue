<template>
  <div v-if="recordData" class="inline">
    <div
      class="fc-avatar-element q-item-division relative-position cursor-pointer"
    >
      <div
        class="q-item-side q-item-side-left q-item-section relative"
        :class="sizeClass"
        v-if="showSlide"
      >
        <el-carousel
          trigger="click"
          height="120px"
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
      <div class="q-item-side q-item-side-left q-item-section relative" v-else>
        <img
          class="fc-avatar"
          :class="sizeClass"
          :src="avatarUrl"
          v-if="avatarUrl"
        />
      </div>
      <div v-if="showName">
        <div class="textoverflow-ellipsis width110px pL8">
          {{ module === 'others' ? recordData : recordData.name }}
        </div>
      </div>
      <!-- <div v-if="showUpload">
        <span class="upload-space-photos pointer" @click="showUploadPhotoDialog"><i class="fa fa-camera" aria-hidden="true"></i></span>
        <f-upload-photos ref="uploadDialog" :list="photos" module="assetphotos" :record="asset"></f-upload-photos>
      </div> -->
    </div>
  </div>
</template>
<script>
export default {
  props: ['size', 'recordData', 'name', 'hovercard', 'module'],
  data() {
    return {
      avatarUrl: null,
      sizeClass: 'fc-avatar-' + this.size,
      hoverCardId: 'spaceHoverCardId-' + this.recordData.id,
      showName: typeof this.name === 'undefined' || this.name === 'true',
      showHoverCard:
        typeof this.hovercard === 'undefined' || this.hovercard === 'true',
      loadHoverCardData: false,
      photosLoading: true,
      photos: [],
      sliderEnabledSize: ['shuge', 'huge', 'xhuge'],
    }
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
    recordData: function() {
      this.loadAvatarUrl()
    },
  },
  methods: {
    loadAvatarUrl() {
      this.avatarUrl = this.recordData.photoUrl
      if (!this.recordData.photoUrl) {
        this.avatarUrl = this.getDefaultAvatar()
      } else {
        this.avatarUrl = this.recordData.photoUrl
      }
    },
    openPhotoUrl(url) {
      window.open(url, '_blank')
    },
    getDefaultAvatar() {
      if (this.recordData) {
        if (this.module === 'item') {
          return require('statics/inventory/item-img.jpg')
        } else if (this.module === 'tool') {
          return require('statics/inventory/tool-img.jpg')
        } else if (this.module === 'others') {
          return require('statics/inventory/item-img.jpg')
        }
      }
      return require('statics/inventory/item-img.jpg')
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
