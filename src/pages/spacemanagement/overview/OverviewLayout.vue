<template>
  <div class="height100">
    <el-row class="height100">
      <el-col :span="6" class="height100">
        <SpaceNav class="fc-white-bg height100"></SpaceNav>
      </el-col>
      <el-col :span="18" class="height100">
        <router-view></router-view>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import SpaceNav from 'pages/spacemanagement/overview/components/SpaceNavBar'
export default {
  components: {
    SpaceNav,
  },
}
</script>
