<template>
  <div v-if="!$validation.isEmpty(bannerListData)">
    <div class="position-relative flex-middle" v-if="bannerListData.type === 1">
      <div
        class="fc-feature-nav-banner"
        v-if="showCloseBanner"
        :class="getBackgroundClass()"
        v-html="sanitize(bannerListData.subject)"
      ></div>
      <div
        class="fc-close-banner pointer"
        @click="closeBanner"
        v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
        content="Close"
      >
        <i class="el-icon-close"></i>
      </div>
    </div>
    <div v-if="bannerListData.type === 2" class="display-flex-between-space">
      <div
        class="fc-feature-nav-banner pointer"
        v-if="showCollapseData"
        :class="getBackgroundClass()"
        @click="closeCollapse"
      >
        <div class="pointer pR20 position-absolute fc-ab-right0">
          <i class="el-icon-arrow-up f14 fwBold"></i>
        </div>
        <div
          v-html="sanitize(bannerListData.subject)"
          @click="moveTodelegate"
        ></div>
        <div class="pL20 pointer" @click="moveTodelegate">
          <i class="el-icon-back fwBold f18 rotate180"></i>
        </div>
      </div>
      <div
        v-else
        class="fc-feature-nav-banner-close-state pointer"
        :class="getBackgroundClass()"
        @click="openCollapseState"
      ></div>
    </div>
  </div>
</template>
<script>
import { sanitize } from '@facilio/utils/sanitize'
import { API } from '@facilio/api'
export default {
  props: ['showCollapseData', 'bannerListData', 'showCloseBanner'],
  data() {
    return {
      loading: true,
      activeNames: [1],
    }
  },
  created() {
    this.sanitize = sanitize
  },
  computed: {},
  methods: {
    closeCollapse() {
      this.$emit('closeCollapseData')
    },
    openCollapseState() {
      this.$emit('openCollapseData')
    },
    getBackgroundClass() {
      if (this.bannerListData.priority === 1) {
        return 'bannerBgHigh'
      } else if (this.bannerListData.priority === 2) {
        return 'bannerBgMedium'
      } else if (this.bannerListData.priority === 3) {
        return 'bannerBgLow'
      } else {
        return 'bannerBgHigh'
      }
    },
    moveTodelegate() {
      let route = this.$router.resolve({
        path: '/app/personalsettings/delegate',
      })
      window.open(route.href)
    },
    async closeBanner() {
      this.loading = true
      let { error } = await API.post(`/v2/banner/close`, {
        id: this.bannerListData.id,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success(this.$t('setup.delete.closed_successfully'))
        window.location.reload()
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
.fc-feature-nav-banner {
  width: 100%;
  height: 25px;
  position: fixed;
  top: 0;
  font-size: 12px;
  z-index: 500;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}
.fc-feature-nav-banner-close-state {
  width: 100%;
  height: 5px;
  position: fixed;
  top: 0;
  font-size: 13px;
  z-index: 500;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: left;
  font-weight: 500;
  padding-left: 30px;
}
.bannerBgHigh {
  background: #f2c422;
  color: #324056;
}
.bannerBgMedium {
  background: #00875a;
  color: #fff;
}
.bannerBgLow {
  background: #2461d2;
  color: #fff;
}
.fc-close-banner {
  position: absolute;
  right: 20px;
  top: 5px;
  z-index: 500;
  .el-icon-close {
    color: #fff;
    font-size: 14px;
    font-weight: 600;
  }
}
.fc-ab-right0 {
  right: 0;
}
</style>
