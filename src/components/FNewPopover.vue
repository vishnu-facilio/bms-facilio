<template>
  <div v-click-outside="hide" class="position f-new-popover" id="eventpopover">
    <slot name="popoverContent"> </slot>
  </div>
</template>

<script>
export default {
  //page x page y co ordinates of mouse event
  props: ['pageX', 'pageY'],

  mounted() {
    // this.positionPopOver(this.pageX, this.pageY)
    this.$emit('ready')
  },
  data() {
    return {
      popoverStyle: {
        left: '0px',
        top: '0px',
      },
    }
  },
  methods: {
    hide() {
      this.$emit('close')
    },
  },
}
</script>

<style lang="scss">
.f-new-popover {
  background-color: #fff;
  z-index: 1000;
  position: absolute;
}
</style>
