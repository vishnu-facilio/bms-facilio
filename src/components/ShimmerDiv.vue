<template>
  <div class="shimmer-container">
    <div
      class="shimmer-bg title-line"
      :style="{ height: height, width: width }"
    ></div>
  </div>
</template>
<script>
export default {
  props: {
    height: {
      default: '20px',
    },
    width: {
      default: '80px',
    },
  },
}
</script>
<style lang="sass">
.shimmer-container
  height: 100%
  overflow: hidden
  width: 100%

  .shimmer-bg
    animation-duration: 2.2s
    animation-fill-mode: forwards
    animation-iteration-count: infinite
    animation-name: shimmer
    animation-timing-function: linear
    background: #ddd
    background: linear-gradient(to right, #F6F6F6 8%, #F0F0F0 18%, #F6F6F6 33%)
    background-size: 1200px 100%

  @-webkit-keyframes shimmer
    0%
    background-position: -100% 0

    100%
    background-position: 100% 0

  @keyframes shimmer
    0%
      background-position: -1200px 0

    100%
      background-position: 1200px 0

  .media
    height: 200px

  .p-32
    padding: 32px

  .title-line
    width: 100%
    border-radius: 2px
</style>
