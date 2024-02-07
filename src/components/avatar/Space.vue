<template>
  <div v-if="space" class="inline">
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
            <img class="fc-avatar-square" :class="sizeClass" :src="photo.url" />
          </el-carousel-item>
        </el-carousel>
      </div>
      <div class="q-item-side q-item-side-left q-item-section relative" v-else>
        <img
          class="fc-avatar-square"
          :class="sizeClass"
          :src="avatarUrl"
          v-if="avatarUrl"
        />
      </div>
      <div v-if="showName">
        <div>
          <span class="q-item-label">{{ space.name }}</span>
        </div>
      </div>
      <div v-if="showUpload">
        <span
          class="upload-space-photos pointer"
          @click="showUploadPhotoDialog"
        >
          <i class="fa fa-camera" aria-hidden="true"></i>
        </span>
        <f-upload-photos
          ref="uploadDialog"
          :list="photos"
          module="basespacephotos"
          :record="space"
        ></f-upload-photos>
      </div>
    </div>
  </div>
</template>
<script>
import FUploadPhotos from '@/FUploadPhotos'

export default {
  props: ['size', 'space', 'name', 'hovercard'],
  data() {
    return {
      avatarUrl: this.getDefaultAvatar(),
      sizeClass: 'fc-avatar-' + this.size,
      hoverCardId: 'spaceHoverCardId-' + this.space.id,
      showName: typeof this.name === 'undefined' || this.name === 'true',
      showHoverCard:
        typeof this.hovercard === 'undefined' || this.hovercard === 'true',
      loadHoverCardData: false,
      photosLoading: true,
      photos: [],
      sliderEnabledSize: ['xxxlg', 'shuge', 'huge', 'xhuge'],
    }
  },
  components: {
    FUploadPhotos,
  },
  mounted() {
    this.loadAvatarUrl()
    if (this.space.avatarUrl) {
      this.avatarUrl = this.space.avatarUrl
    }
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
    space: function() {
      this.avatarUrl = this.getDefaultAvatar()
      this.loadAvatarUrl()
    },
  },
  methods: {
    loadAvatarUrl() {
      if (this.sliderEnabledSize.indexOf(this.size) >= 0) {
        let self = this
        self.photos = []
        if (this.space.id > 0) {
          self.photosLoading = true
          self.$http
            .get('/photos/get?module=basespacephotos&parentId=' + this.space.id)
            .then(function(response) {
              self.photos = response.data.photos || []
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
      }
      if (!this.space.avatarUrl) {
        this.avatarUrl = this.getDefaultAvatar()
        this.space.avatarUrl = this.avatarUrl
      } else {
        this.avatarUrl = this.space.avatarUrl
      }
    },
    getDefaultAvatar(space) {
      let avatarUrl = ''
      if (this.space.type === 'Campus' || this.space.spaceTypeEnum === 'SITE') {
        avatarUrl = require('statics/space/campus.svg')
      } else if (
        this.space.type === 'Building' ||
        this.space.spaceTypeEnum === 'BUILDING'
      ) {
        avatarUrl = require('statics/space/building.svg')
      } else if (
        this.space.type === 'Space' ||
        this.space.spaceTypeEnum === 'SPACE'
      ) {
        let subCategory = ''
        if (subCategory === 'Cafeteria') {
          avatarUrl = require('statics/space/cafeteria.svg')
        } else if (subCategory === 'Toilet') {
          avatarUrl = require('statics/space/toilet.svg')
        } else if (subCategory === 'Dining') {
          avatarUrl = require('statics/space/dining.svg')
        } else {
          // default space avatar
          avatarUrl = require('statics/space/meetingroom.svg')
        }
      } else {
        avatarUrl = require('statics/space/building.svg')
      }
      return avatarUrl
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
