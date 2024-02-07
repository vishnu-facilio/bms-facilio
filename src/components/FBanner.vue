<template>
  <div>
    <div
      class="banner-block"
      v-if="message"
      :style="{ 'background-color': bannerColor }"
    >
      <div class="banner-content">
        <i
          :class="['banner-icon flLeft', bannerIcon]"
          v-show="!hideIcon"
          :style="{ color: bannerTextColor }"
          aria-hidden="true"
        ></i>
        <p class="banner-txt flLeft" :style="{ color: bannerTextColor }">
          {{ message }}
        </p>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['message', 'icon', 'color', 'textColor', 'hideIcon'],
  computed: {
    bannerColor() {
      let color
      if (this.color) {
        color = this.color
      } else if (!this.isError) {
        color = '#ecf3da'
      }
      return color
    },
    bannerTextColor() {
      let color
      if (this.textColor) {
        color = this.textColor
      } else if (!this.isError) {
        color = '#67933a'
      }
      return color
    },
    bannerIcon() {
      let icon
      if (this.icon) {
        icon = this.icon
      } else if (!this.isError) {
        icon = 'el-icon-circle-check-outline'
      }
      return icon
    },
  },
}
</script>
<style>
.banner-block {
  width: 100%;
  border: none;
  height: 40px;
  padding-top: 8px;
  padding-bottom: 8px;
  position: relative;
  top: 0;
  right: 0;
  left: 0;
}
.banner-content {
  /* position: absolute;
  left: 47%; */
  display: flex;
  align-items: center;
  justify-content: center;
}
.banner-content .banner-txt {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  letter-spacing: 0.4px;
  text-align: center;
}
.banner-content .banner-icon {
  padding-right: 10px;
  font-size: 18px;
  font-weight: 300;
  margin-bottom: 19px;
}
</style>
