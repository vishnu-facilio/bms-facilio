<template>
  <div class="map-marker-container">
    <el-image :src="getFullPath(image)" class="map-image-marker">
      <div slot="placeholder" class="image-slot">
        <span class="dot placeholder-loading">...</span>
      </div>
      <div slot="error" class="image-slot">
        <i class="el-icon-office-building placeholder-image "></i>
      </div>
    </el-image>
    <div class="marker-arrow"></div>
  </div>
</template>
<script>
export default {
  props: ['image'],
  methods: {
    getFullPath(image) {
      let fullPath = `https://${document.domain}${image}`
      return fullPath
    },
  },
}
</script>
<style scoped>
.map-marker-container {
  width: 51px;
  height: 54px;
  cursor: pointer;
}
.map-image-marker {
  width: 50px;
  height: 50px;
  border-radius: 30px;
  border: 2px solid;
}
.marker-arrow {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 7px solid #000;
  margin: auto;
  position: relative;
  bottom: 3px;
}
.placeholder-image {
  font-size: 30px;
  width: 50px;
  height: 50px;
  display: -webkit-flex;
  display: flex;
  list-style-type: none;
  padding: 0;
  justify-content: center;
  align-items: center;
  color: #5f6265;
  background: #fff;
}
.placeholder-loading {
  font-size: 20px;
  width: 50px;
  height: 50px;
  display: -webkit-flex;
  display: flex;
  list-style-type: none;
  padding: 0;
  justify-content: center;
  align-items: center;
  color: #5f6265;
  background: #fff;
}
</style>
