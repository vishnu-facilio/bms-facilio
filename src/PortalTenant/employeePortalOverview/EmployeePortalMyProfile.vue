<template>
  <div>
    <div class="avatar-container" style="background-color:#fff">
      <div class="position-relative d-flex">
        <Avatar size="shuge" :user="account.user"></Avatar>
        <div class="edit-img" @click="toggleShow">
          <InlineSvg
            src="svgs/employeePortal/ic-edit"
            icon-class="xxxs"
          ></InlineSvg>
        </div>
      </div>
      <div class="user-name">{{ account.user.name }}</div>
    </div>
    <el-header class="profile-header ">
      <div class="flex-center">
        <div
          v-for="(tab, index) in tabs"
          :key="index"
          class="header-item mR30 pointer"
          @click="tabChanged(tab.name)"
        >
          <div :class="{ active: activeName === tab.name }">
            {{ tab.displayName }}
          </div>
          <div
            v-if="activeName === tab.name"
            class="portal-dashboard-active"
          ></div>
        </div>
      </div>
    </el-header>
    <div class="content-container">
      <router-view></router-view>
    </div>
    <photo-upload
      langType="en"
      field="user.avatar"
      @crop-success="avatarCropSuccess"
      @crop-upload-success="avatarCropUploadSuccess"
      @crop-upload-fail="avatarCropUploadFail"
      :withCredentials="true"
      :width="120"
      :height="120"
      :params="params"
      :headers="headers"
      :url="avatarUploadUrl"
      img-format="png"
      v-model="showAvatarUpload"
    >
    </photo-upload>
  </div>
</template>
<script>
import EditProfile from 'src/PortalTenant/employeePortalOverview/EPEditProfile.vue'
import ChangePassword from 'src/PortalTenant/employeePortalOverview/EPChangePassword.vue'
import Avatar from '@/Avatar'
import InlineSvg from '../../components/InlineSvg.vue'
import PhotoUpload from 'vue-image-crop-upload'

import { mapState, mapActions } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { getBaseURL } from 'util/baseUrl'

export default {
  components: { EditProfile, ChangePassword, Avatar, InlineSvg, PhotoUpload },
  data() {
    return {
      uploadDialog: false,
      showAvatarUpload: false,
      params: {
        userId: 0,
      },
      headers: {},
      avatarUploadUrl: null,
      avatarDataUrl: '',
      canShowMFA: false,
      activeName: 'myprofile',
      tabsList: [
        {
          name: 'myprofile',
          displayName: this.$t('common.profile.myprofile'),
          canShow: true,
        },
        {
          name: 'changepassword',
          displayName: this.$t('common.profile.changepassword'),
          canShow: true,
        },
        {
          name: 'mfa',
          displayName: 'Multi Factor Authentication',
          canShow: this.$isMFAEnabled,
        },
        {
          name: 'notifications',
          displayName: 'Notifications',
          canShow: true,
        },
      ],
    }
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    tabs() {
      return this.tabsList.filter(i => i.canShow)
    },
  },

  created() {
    if (this.$route?.name == 'notifications') {
      this.activeName = this.$route.name
    }
    this.params.userId = this.account.user.uid
    let {
      account: { user },
    } = this
    let { name, email, phone, mobile, street, state, city, zip, uid } = user
    this.user = {
      name,
      uid: uid || -1,
      email,
      phone,
      mobile,
      street,
      state,
      city,
      zip,
    }

    // avatar upload url
    let baseURL = getBaseURL()

    this.avatarUploadUrl = `${baseURL}/settings/users/avatar/upload`

    let csrf = this.$cookie.get('fc.csrfToken')
    if (!isEmpty(csrf)) {
      this.headers['X-CSRF-Token'] = csrf
    }
  },
  methods: {
    ...mapActions({
      updateUserAvatarUrl: 'updateUserAvatarUrl',
    }),
    toggleShow() {
      this.showAvatarUpload = !this.showAvatarUpload
    },
    avatarCropSuccess(avatarDataUrl) {
      this.avatarDataUrl = avatarDataUrl
    },
    avatarCropUploadSuccess(jsonData) {
      this.updateUserAvatarUrl({
        id: this.account.user.id,
        avatarUrl: jsonData.avatarUrl,
      })
      this.account.user.avatarUrl = jsonData.avatarUrl
    },
    avatarCropUploadFail() {
      this.$message.error('Avatar upload fail')
    },
    tabChanged(tabName) {
      this.activeName = tabName
      this.$router.push(`/employee/profile/${tabName}`)
    },
  },
}
</script>
<style scoped>
.portal-dashboard-active {
  width: 30px;
  height: 3px;
  background-color: #0a7aff;
  position: absolute;
  bottom: 0;
}
.profile-header {
  width: 100%;
  height: 44px !important;
  padding: 14px;
  background-color: #fff;
  position: relative;
  box-sizing: border-box;
}
.flex-center {
  display: flex;
  justify-content: center;
}
.header-item {
  font-size: 14px;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.26px;
  color: #324056;
}
.active {
  font-weight: 500;
}
.myprofile-header {
  padding: 20px;
  background-color: #ffffff;
}
.content-container {
  height: calc(100vh - 380px);
  margin: 20px;
}
.avatar-container {
  height: 240px;
  justify-content: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.edit-img {
  position: absolute;
  top: 85px;
  right: 8px;
  background-color: #fff;
  border-radius: 14px;
  height: 28px;
  width: 28px;
  border: solid 1px #e0e0e0;
  padding: 5px 5px 5px;
  box-shadow: 0 0 6px 0 rgb(0 0 0 / 16%);
  cursor: pointer;
}
.user-name {
  margin: 25px 0;
  font-size: 28px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: 0.3px;
  color: #324056;
  line-height: 30px;
}
</style>
