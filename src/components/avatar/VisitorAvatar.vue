<template>
  <div v-if="recordData" class="inline">
    <div
      class="fc-avatar-element q-item-division relative-position cursor-pointer"
    >
      <div class="q-item-side q-item-side-left q-item-section relative">
        <img
          class="fc-avatar"
          :class="sizeClass"
          :src="avatarUrl ? $prependBaseUrl(avatarUrl) : defaultIcon"
          v-if="avatarUrl || defaultIcon"
        />
      </div>
      <template v-if="showName">
        <div class="textoverflow-ellipsis width110px pL8">
          {{ getDisplayName() }}
        </div>
      </template>
    </div>
  </div>
</template>
<script>
export default {
  props: ['size', 'recordData', 'name', 'hovercard', 'module'],
  data() {
    return {
      avatarUrl: null,
      defaultIcon: null,
      sizeClass: 'fc-avatar-' + this.size,
      showName: !!this.name,
    }
  },
  watch: {
    recordData: {
      handler() {
        this.loadAvatarUrl()
      },
      immediate: true,
    },
  },
  methods: {
    loadAvatarUrl() {
      let { avatarUrl } = this.recordData || {}

      if (!avatarUrl) {
        if (this.recordData) {
          if (
            ['visitor', 'visitorlog', 'invitevisitor'].includes(this.module)
          ) {
            this.defaultIcon = require('statics/icons/user.png')
          } else {
            this.defaultIcon = require('statics/inventory/item-img.jpg')
          }
        }
      } else {
        this.avatarUrl = avatarUrl
      }
    },
    getDisplayName() {
      let { visitorName, name } = this.recordData || {}

      if (this.module === 'visitorlog') {
        return visitorName || '---'
      } else {
        return name || '---'
      }
    },
  },
}
</script>
<style scoped>
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
