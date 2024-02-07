<template>
  <div>
    <el-row :gutter="0" class="height100">
      <el-col
        v-if="isSideBar"
        :xs="6"
        :sm="6"
        :md="6"
        :lg="6"
        :xl="6"
        class="m0"
        style="width:24%;"
      >
        <div
          class="height100vh full-layout-white fc-border-left fc-border-right"
        >
          <SpaceNav></SpaceNav>
        </div>
      </el-col>
      <el-col
        :xs="isSideBar ? 18 : 24"
        :sm="isSideBar ? 18 : 24"
        :md="isSideBar ? 18 : 24"
        :lg="isSideBar ? 18 : 24"
        :xl="isSideBar ? 18 : 24"
        class="scrollabel height100vh pB60"
      >
        <router-view></router-view>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import SpaceNav from 'pages/spacemanagement/SpaceNavbar'
export default {
  components: {
    SpaceNav,
  },
  computed: {
    siteId() {
      return parseInt(this.$route.params.siteid)
    },
  },
  data() {
    return {
      isSideBar: true,
    }
  },
  created() {
    if (this.siteId) {
      this.isSideBar = true
    } else {
      this.isSideBar = false
    }
    this.loadData()
  },
  watch: {
    siteId() {
      this.loadData()
    },
  },
  methods: {
    loadData() {
      this.$store.dispatch('space/fetchSites')
      this.$store.dispatch('space/fetchBuildings', { id: this.siteId })
    },
  },
}
</script>
<style>
.hideside {
  display: none;
}
</style>
