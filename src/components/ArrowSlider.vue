<template>
  <div
    class="scroll-container"
    v-bind:class="arrowState"
    :style="{ height: height + 20 + 'px' }"
    @mousemove="handleMousemove"
    @mouseleave="setDefaultArrowState"
  >
    <el-button
      :style="{ height: height + 0 + 'px' }"
      v-if="!disableArrow"
      id="slide"
      type="button"
      @click="clickLeft()"
      class="el-icon-arrow-left arrow-button"
    ></el-button>
    <slot></slot>
    <el-button
      :style="{ height: height + 0 + 'px' }"
      v-if="!disableArrow"
      id="slide1"
      type="button"
      @click="clickRight()"
      class="el-icon-arrow-right arrow-button"
    ></el-button>
  </div>
</template>
<script>
export default {
  props: {
    containerName: {
      type: String,
    },
    cardsLength: {
      type: Number,
    },
    height: {
      type: Number,
      default: () => {
        return 100
      },
    },
  },
  data() {
    return {
      arrowState: 'show-arrow-default',
    }
  },
  computed: {
    disableArrow() {
      return this.cardsLength > 3 ? false : true
    },
  },
  methods: {
    handleMousemove(event) {
      let offset = this.$el.clientWidth < 1200 ? 200 : 400
      if (this.$el?.clientWidth) {
        let centerClient = this.$el.clientWidth / 2
        let { clientX } = event
        if (clientX < centerClient && clientX < offset) {
          this.showArrowLeft()
        } else if (
          clientX > centerClient &&
          clientX > this.$el.clientWidth - offset &&
          clientX < this.$el.clientWidth
        ) {
          this.showArrowRight()
        } else if (
          clientX > centerClient - 100 &&
          clientX < centerClient + 100
        ) {
          this.setDefaultArrowState()
        }
      }
    },
    setDefaultArrowState() {
      this.arrowState = 'show-arrow-default'
    },
    showArrowLeft() {
      this.arrowState = 'show-arrow-left'
    },
    showArrowRight() {
      this.arrowState = 'show-arrow-right'
    },
    clickRight() {
      let { containerName } = this
      document.getElementById(containerName).scrollLeft += 500
    },
    clickLeft() {
      let { containerName } = this
      document.getElementById(containerName).scrollLeft -= 500
    },
  },
}
</script>
<style scoped>
.scroll-container {
  display: flex;
}
.el-icon-arrow-left {
  position: absolute;
  left: 0;
  top: 10px;
  z-index: 3 !important;
  background: #fff;
}
.el-icon-arrow-right {
  position: absolute;
  right: 0;
  top: 10px;
  z-index: 3 !important;
  background: #fff;
}
.show-arrow-default .el-icon-arrow-left,
.show-arrow-default .el-icon-arrow-right,
.show-arrow-left .el-icon-arrow-right,
.show-arrow-right .el-icon-arrow-left {
  display: none;
  transition: width 1s ease-out;
  width: 0;
}
.show-arrow-left .el-icon-arrow-left,
.show-arrow-right .el-icon-arrow-right {
  display: block;
  opacity: 1;
  width: auto;
  transition: opacity 1s ease-out;
}
.arrow-button {
  padding: 9px;
  border-radius: 0;
  background-color: #fff;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 1px 7px 0 rgb(0 0 0 / 9%);
  z-index: 1;
  border: none;
  margin: auto;
}
</style>
