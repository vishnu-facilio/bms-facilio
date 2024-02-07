<template>
  <div class="fc-page-backTo-top">
    <el-backtop target=".fc-page-scroll-top" :bottom="40" :right="20">
    </el-backtop>
  </div>
</template>
<style lang="scss">
.fc-page-backTo-top {
  .el-backtop {
    color: #656cb6 !important;
    font-size: 25px !important;
    z-index: 900 !important;
    &:hover {
      background-color: #fff;
    }
  }
}
</style>
