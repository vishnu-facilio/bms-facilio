<template>
  <div v-if="isLoading" class="loading-container d-flex">
    <Spinner :show="isLoading"></Spinner>
  </div>
  <div v-else class="scrollable120-y100 mB30 editprofile-portal-container">
    <div class="fc-form">
      <div class="row">
        <div class="col-lg-8 col-md-6">
          <el-form
            :model="user"
            label-width="120px"
            class="mT10"
            label-position="top"
          >
            <div
              v-if="canShowNotificationSettings"
              style="color: #ef4f8f;"
              class=" col-lg-12 col-md-12 fc-modal-sub-title mB10 mT20"
            >
              {{ $t('setup.setup_profile.general_info') }}
            </div>
            <el-form-item :label="$t('common.products.full_name')">
              <el-input
                v-model="user.name"
                class="fc-input-full-border-select2 width80"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('setup.setup_profile.email')">
              <el-input
                disabled
                v-model="user.email"
                class="fc-input-full-border-select2 width80"
              ></el-input>
            </el-form-item>
            <el-form-item :label="$t('setup.setup_profile.language')">
              <el-select
                v-model="user.language"
                class="fc-input-full-border-select2 width80"
                :placeholder="$t('setup.setup.select_language')"
              >
                <el-option
                  v-for="(language, index) in languageList"
                  :key="index"
                  :label="language.label"
                  :value="language.value"
                ></el-option>
              </el-select>
            </el-form-item>

            <el-form-item class="pB10" :label="$t('setup.setup_profile.phone')">
              <el-input
                v-model="user.phone"
                :placeholder="$t('common.placeholders.enter_phone_number')"
                class="fc-input-full-border-select2 width80"
              ></el-input>
            </el-form-item>
            <div
              v-if="canShowNotificationSettings"
              style="color: #ef4f8f;"
              class="col-lg-12 col-md-12 fc-modal-sub-title mT20 mB10 pT10"
            >
              {{ $t('setup.setup_profile.notification_settings') }}
            </div>
            <div v-if="canShowNotificationSettings" class="pB10">
              <div
                v-for="(setting, settingIdx) in notificationSettings"
                :key="`notification-setting${setting.notificationType}`"
                class=" mB10"
              >
                <el-form-item
                  :label="
                    notificationToggleDescription[setting.notificationType]
                  "
                >
                  <el-checkbox
                    :value="!setting.disabled"
                    @change="val => toggleSettingSwitch(val, settingIdx)"
                  ></el-checkbox>
                </el-form-item>
              </div>
            </div>
          </el-form>
          <el-button
            @click="saveProfile"
            :loading="saving"
            class="btn btn--primary setup-el-btn pL60 pR60 mT20"
          >
            {{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
        </div>
        <div class="col-lg-4 col-md-6 avatar-profile-container">
          <Avatar size="shuge" :user="account.user"></Avatar>
          <div class="fc-avatar-actions mT20 mR3">
            <a @click="showAvatarUpload = !showAvatarUpload">{{
              $t('setup.setup_profile.upload_change')
            }}</a>

            <span class="separator" v-if="account.user.avatarUrl">|</span>
            <a @click="deleteProfilePhoto" v-if="account.user.avatarUrl">{{
              $t('setup.setup_profile.photo_delete')
            }}</a>
          </div>
        </div>
      </div>
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
import Avatar from '@/Avatar'
import PhotoUpload from 'vue-image-crop-upload'
import Spinner from '@/Spinner'
import { API } from '@facilio/api'
import EditProfile from 'pages/personalsettings/EditProfile'
export default {
  extends: EditProfile,
  components: { Avatar, PhotoUpload, Spinner },
  methods: {
    async saveProfile() {
      let { notificationSettings } = this
      this.saving = true
      let { user } = this
      let profileObj = this.$helpers.cloneObject(user)

      let { error } = await API.post('/account/update', {
        user: profileObj,
        peopleNotificationSettingsList: notificationSettings,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          this.$t('setup.setup_profile.user_profile_details')
        )
        this.$helpers.delay(1000).then(() => location.reload())
      }

      this.saving = false
    },
  },
}
</script>

<style lang="scss">
.editprofile-portal-container {
  background-color: #ffffff;
  height: calc(100vh - 90px);
  padding: 20px;

  .el-form-item__label {
    height: 33px;
    font-size: 14px;
    font-weight: 400;
    letter-spacing: 0.5px;
    color: #415e7b !important;
  }
  .width80vh {
    width: 80vh;
  }
  .avatar-profile-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 40px;
  }

  .fc-avatar-actions a {
    padding: 5px;
    color: #2e9dfd !important;
    &:hover {
      color: #027be3 !important;
    }
  }
}
</style>
