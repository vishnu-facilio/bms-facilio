<template>
  <div v-if="tenant" class="inline">
    <div
      class="fc-avatar-element q-item-division relative-position cursor-pointer"
    >
      <!-- :data-html="'#' + hoverCardId"
      data-distance="15"
      data-theme="light"
      data-interactive="true"
      data-animation="scale"
      data-arrowsize="big"
      data-animatefill="false"
      data-arrow="true"
      data-delay="1000"
      data-hidedelay="0"
      data-hideduration="0"
      @shown="loadHoverCard"
      @hidden="hideHoverCard"
      v-tippy -->
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
        <div v-if="avatarUrl">
          <img class="fc-avatar" :class="sizeClass" :src="avatarUrl" />
        </div>
        <div class="fc-avatar" :class="sizeClass" :style="bgColor" v-else>
          {{ trimmedName }}
        </div>
      </div>

      <div v-if="showName">
        <div>
          <span class="q-item-label">{{ tenant.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['size', 'tenant', 'name', 'color', 'hovercard'],
  data() {
    return {
      avatarUrl: null,
      sizeClass: 'fc-avatar-' + this.size,
      hoverCardId: 'spaceHoverCardId-' + this.tenant.id,
      showName: typeof this.name === 'undefined' || this.name === 'true',
      showHoverCard:
        typeof this.hovercard === 'undefined' || this.hovercard === 'true',
      loadHoverCardData: false,
      photosLoading: true,
      photos: [],
      trimmedName: '',
      bgColor:
        'background-color: ' +
        (this.color ? this.color : this.getRandomBgColor()),
      sliderEnabledSize: ['shuge', 'huge', 'xhuge'],
    }
  },
  mounted() {
    this.loadAvatarUrl()
  },
  computed: {
    showSlide() {
      if (this.photos && this.photos.length) {
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
      return false
    },
    showUpload() {
      return this.sliderEnabledSize.indexOf(this.size) >= 0
    },
  },
  watch: {
    tenant: function() {
      this.loadAvatarUrl()
    },
  },
  methods: {
    loadAvatarUrl() {
      if (this.tenant.avatarId && this.tenant.avatarId > 0) {
        this.avatarUrl = this.tenant.avatarUrl
      } else {
        this.trimmedName = this.getAvatarName(this.tenant.name)
      }
    },
    getAvatarName(name) {
      let parts = name.split(/[ -]/)

      let initials = ''
      let initialLen = 2
      let count = 0
      for (let i = 0; i < parts.length; i++) {
        if (parts[i].trim() !== '') {
          initials += parts[i].charAt(0)
          count++
          if (count >= initialLen) {
            break
          }
        }
      }

      if (initials.length < initialLen && name.length >= initialLen) {
        initials = name.trim().substring(0, initialLen)
      }
      let avatarName = initials.toUpperCase()
      return avatarName
    },
    getDefaultAvatar() {
      let avatarUrl = ''
      avatarUrl = require('statics/space/building.svg')
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
    getRandomBgColor() {
      let colors = [
        '#FFBA51',
        '#34BFA3',
        '#FF2F82',
        '#29D9A7',
        '#ECDC74',
        '#927FED',
        '#FF61A8',
        '#fbf383',
        '#ac4352',
        '#6db1f4',
      ]

      let userKey = this.tenant.name
      let userUniqueNum = Array.from(userKey)
        .map(letter => letter.charCodeAt(0))
        .reduce((current, previous) => previous + current)

      let colorIndex = userUniqueNum % colors.length
      let color = colors[colorIndex]
      return color
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
