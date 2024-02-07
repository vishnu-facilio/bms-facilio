<template>
  <div>
    <el-header class="fc-v1-tenant-overview-header overflow-x">
      <div class="d-flex">
        <div
          v-for="(tab, index) in tabs"
          :key="index"
          @click="activeName = tab.name"
          class="fc-white-14 pR50 pointer"
        >
          {{ tab.displayName }}
          <div
            v-if="activeName === tab.name"
            class="portal-dashboard-active"
          ></div>
        </div>
      </div>
    </el-header>
    <div class="m10">
      <EditProfile v-if="activeName === 'myprofile'" />
      <ChangePassword v-else-if="activeName === 'resetpassword'" />
      <div v-else class="fc-form-container change-password-container">
        <MFA />
      </div>
    </div>
  </div>
</template>

<script>
import MFA from 'pages/personalsettings/mfa'
import EditProfile from './EditProfile'
import ChangePassword from './ChangePassword'
export default {
  components: { EditProfile, ChangePassword, MFA },
  computed: {
    tabs() {
      return this.tabsList.filter(i => i.canShow)
    },
  },
  data() {
    return {
      canShowMFA: false,
      activeName: 'myprofile',
      tabsList: [
        {
          name: 'myprofile',
          displayName: this.$t('common.profile.myprofile'),
          canShow: true,
        },
        {
          name: 'resetpassword',
          displayName: this.$t('common.profile.changepassword'),
          canShow: true,
        },
        {
          name: 'mfa',
          displayName: 'Multi Factor Authentication',
          canShow: this.$isMFAEnabled,
        },
      ],
    }
  },
}
</script>

<style scoped>
.myprofile-header {
  padding: 20px;
  background-color: #ffffff;
}
.fc-v1-tenant-overview-header {
  width: 100%;
  height: 60px;
  background-color: #3c3681;
  position: relative;
  box-sizing: border-box;
  padding: 24px 30px 22px 30px;
}
.fc-white-14 {
  line-height: normal;
  letter-spacing: 0.39px;
}
.portal-dashboard-active {
  width: 33px;
  height: 1px;
  border: solid 2px #32cbcb;
  position: absolute;
  bottom: 0;
}
</style>
