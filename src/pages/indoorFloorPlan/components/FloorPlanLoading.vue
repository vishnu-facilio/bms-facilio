<template>
  <div class="floorplanloading ">
    <div class="section__item">
      <facilio-new-loader class=""> </facilio-new-loader>
    </div>
  </div>
</template>
<script>
import facilioNewLoader from 'src/components/facilio-loading'
export default {
  components: {
    facilioNewLoader,
  },
}
</script>
<style lang="scss">
.floorplanloading {
  position: absolute;
  width: 100%;
  z-index: 100;
  height: 100%;
  background: rgb(255 255 255 / 80%);
  left: 0;
  border-left: 1px solid #eee;

  /* display: flex;
  align-items: center;
  justify-content: center; */
}
.section__item {
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  svg {
    width: 70px;
    height: 70px;
  }
}
</style>
