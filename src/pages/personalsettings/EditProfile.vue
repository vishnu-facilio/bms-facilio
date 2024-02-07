<template>
  <div class="scrollable120-y100 mB30 editprofile-portal-container p40">
    <div class="setup-modal-title">
      {{ $t('setup.setup_profile.edit_profile') }}
    </div>
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <el-form
      v-else
      :model="user"
      label-width="120px"
      class="mT10"
      label-position="top"
      :rules="rules"
      ref="profileForm"
    >
      <div class="row">
        <div class="col-lg-12 col-md-12 fc-modal-sub-title mT20">
          {{ $t('setup.setup_profile.general_info') }}
        </div>
        <div class="col-lg-6 col-md-6 mT10">
          <el-form-item :label="$t('setup.setup_profile.full_name')">
            <el-input
              v-model="user.name"
              class="fc-input-full-border-select2 width90"
            >
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('setup.setup_profile.email')">
            <el-input
              disabled
              v-model="user.email"
              class="fc-input-full-border-select2 width90"
            >
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('setup.setup_profile.timezone')">
            <el-select
              v-model="user.timezone"
              class="fc-input-full-border-select2 width90"
              placeholder="Select a timezone"
            >
              <el-option
                v-for="(timeZone, index) in timezoneList"
                :key="`${timeZone.value}-${index}`"
                :label="timeZone.label"
                :value="timeZone.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('setup.setup_profile.language')">
            <el-select
              v-model="user.language"
              class="fc-input-full-border-select2 width90"
              placeholder="Select language"
            >
              <el-option
                v-for="(language, index) in languageList"
                :key="`${language.value}-${index}`"
                :label="language.label"
                :value="language.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="col-lg-6 col-md-6">
          <div
            class="logo-upload"
            style="text-align: center; padding-top: 15px;"
          >
            <div class="companylogocontainer">
              <Avatar size="shuge" :user="account.user"></Avatar>
              <div class="fc-avatar-actions mT20 mR3">
                <a @click="toggleShow">{{
                  $t('setup.setup_profile.upload_change')
                }}</a>

                <span class="separator" v-if="account.user.avatarUrl">|</span>
                <a @click="deleteProfilePhoto" v-if="account.user.avatarUrl">{{
                  $t('setup.setup_profile.photo_delete')
                }}</a>
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
          </div>
        </div>
        <div class="col-lg-12 col-md-12 fc-modal-sub-title mT20">
          {{ $t('setup.setup_profile.contact_info') }}
        </div>
        <div class="col-lg-3 col-md-3 mT10">
          <div class="form-input mB10">
            <el-form-item prop="phone" :label="$t('setup.setup_profile.phone')">
              <el-input
                v-model="user.phone"
                class="fc-input-full-border-select2 width80"
              >
              </el-input>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-3 col-md-3 mT10">
          <div class="form-input mB10">
            <el-form-item
              prop="mobile"
              :label="$t('setup.setup_profile.mobile')"
            >
              <el-input
                v-model="user.mobile"
                class="fc-input-full-border-select2 width80"
              >
              </el-input>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-12 col-md-12 fc-modal-sub-title mT20">
          {{ $t('setup.setup_profile.address_info') }}
        </div>
        <div class="col-lg-6 col-md-6 mT10">
          <div class="form-input mB10">
            <el-form-item :label="$t('setup.setup_profile.street')">
              <el-input
                v-model="user.street"
                class="fc-input-full-border-select2 width90"
              >
              </el-input>
            </el-form-item>
          </div>
          <div class="form-input mB10">
            <el-form-item :label="$t('setup.setup_profile.city')">
              <el-input
                v-model="user.city"
                class="fc-input-full-border-select2 width90"
              >
              </el-input>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-12 col-md-12"></div>
        <div class="col-lg-3 col-md-4">
          <div class="form-input mB10">
            <el-form-item :label="$t('setup.setup_profile.state')">
              <el-input
                v-model="user.state"
                class="fc-input-full-border-select2 width90"
              >
              </el-input>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-3 col-md-2">
          <div class="form-input mB10">
            <el-form-item :label="$t('setup.setup_profile.zipcode')">
              <el-input
                v-model="user.zip"
                class="fc-input-full-border-select2 width80"
              >
              </el-input>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-12 col-md-12"></div>
        <div class="col-lg-6 col-md-6">
          <el-form-item :label="$t('setup.setup_profile.country')">
            <el-select
              v-model="user.country"
              class="fc-input-full-border-select2 width90"
              placeholder="Select language"
            >
              <el-option
                v-for="(language, index) in countryList"
                :key="index"
                :label="language.label"
                :value="language.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div
          v-if="canShowNotificationSettings"
          class="col-lg-12 col-md-12 fc-modal-sub-title mT20"
        >
          {{ $t('setup.setup_profile.notification_settings') }}
        </div>
        <div v-if="canShowNotificationSettings" class="col-lg-6 col-md-6 mT10">
          <div
            v-for="(setting, settingIdx) in notificationSettings"
            :key="`notification-setting${setting.notificationType}`"
            class="form-input mB10"
          >
            <el-form-item
              :label="notificationToggleDescription[setting.notificationType]"
            >
              <el-checkbox
                :value="!setting.disabled"
                @change="val => toggleSettingSwitch(val, settingIdx)"
              ></el-checkbox>
            </el-form-item>
          </div>
        </div>
        <div class="col-lg-12 col-md-12 mT30">
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
      </div>
    </el-form>
  </div>
</template>
<script>
import timezones from 'util/data/timezones'
import countries from 'util/data/countries'
import Avatar from '@/Avatar'
import PhotoUpload from 'vue-image-crop-upload'
import { mapState, mapActions } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import { getBaseURL } from 'util/baseUrl'
import { isValidPhoneNumber } from 'src/util/helpers'
import Spinner from '@/Spinner'

export default {
  data() {
    return {
      submit: false,
      showAvatarUpload: false,
      params: {
        userId: 0,
      },
      headers: {},
      avatarUploadUrl: null,
      isLoading: false,
      avatarDataUrl: '',
      timezoneList: timezones,
      countryList: countries,
      languageList: Constants.languages,
      notificationSettings: [],
      user: {
        name: null,
        email: null,
        timezone: null,
        language: null,
        street: null,
        city: null,
        state: null,
        zip: null,
        country: null,
        phone: null,
        mobile: null,
        uid: -1,
      },
      saving: false,
      rules: {
        mobile: [
          {
            validator: function(rule, mobile, callback) {
              if (!isEmpty(mobile) && !isValidPhoneNumber(mobile)) {
                callback(new Error('Please enter valid mobile number'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
        phone: [
          {
            validator: function(rule, phone, callback) {
              let isValid = isEmpty(phone) || phone == '+'
              if (!isValid && !isValidPhoneNumber(phone)) {
                callback(new Error('Please enter valid phone number'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
      },
      notificationToggleDescription: {
        COMMENT_MENTION_EMAIL: 'Email Notification for Comment Mentions',
        COMMENT_MENTION_IN_APP: 'In App Notification for Comment Mentions',
      },
    }
  },
  title() {
    return 'Edit Profile'
  },
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    isNewCommentsEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_COMMENTS')
    },
    canShowNotificationSettings() {
      let { isNewCommentsEnabled, notificationSettings } = this
      return isNewCommentsEnabled && !isEmpty(notificationSettings)
    },
  },
  created() {
    let { isNewCommentsEnabled } = this
    if (isNewCommentsEnabled) {
      this.getAllNotificationPreferences()
    }
    let timezone = null
    if (this.account.user.timezone) {
      timezone = this.timezoneList.find(
        timezone => timezone.value === this.account.user.timezone
      )
    }
    let language = null
    if (this.account.user.language) {
      language = this.languageList.find(
        language => language.value === this.account.user.language
      )
    }
    let country = null
    if (this.account.user.country) {
      country = this.countryList.find(
        country => country.value === this.account.user.country
      )
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
      language: language ? language.value : null,
      timezone: timezone ? timezone.value : null,
      country: country ? country.value : null,
    }

    // avatar upload url
    let baseURL = getBaseURL()

    this.avatarUploadUrl = `${baseURL}/settings/users/avatar/upload`

    let csrf = this.$cookie.get('fc.csrfToken')
    if (!isEmpty(csrf)) {
      this.headers['X-CSRF-Token'] = csrf
    }
  },
  components: {
    Avatar,
    PhotoUpload,
    Spinner,
  },
  methods: {
    ...mapActions({
      updateUserAvatarUrl: 'updateUserAvatarUrl',
    }),
    close() {
      alert('close dialog')
    },

    async getAllNotificationPreferences() {
      this.isLoading = true
      let { data, error } = await API.get('/account/getNotificationPreferences')
      if (!error) {
        let { peopleNotificationSettingsList } = data
        this.notificationSettings = peopleNotificationSettingsList
      }
      this.isLoading = false
    },
    async saveProfile() {
      let { notificationSettings } = this
      this.$refs['profileForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let userObj = this.$helpers.cloneObject(this.user)
        let { error } = await API.post('/account/update', {
          user: userObj,
          peopleNotificationSettingsList: notificationSettings,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
          this.$emit('close', true)
        } else {
          this.$message.success(
            this.$t('setup.setup_profile.user_profile_details')
          )
          this.$helpers.delay(1000).then(() => location.reload())
        }
        this.saving = false
      })
    },
    async deleteProfilePhoto() {
      let value = await this.$dialog.confirm({
        title: this.$t(`setup.setup_profile.header_delete_avatar`),
        message: this.$t(`setup.setup_profile.avatar_delete_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let {
          account: { user },
        } = this
        let { id, uid } = user
        let { error } = await API.post('/settings/users/avatar/delete', {
          userId: uid,
        })
        if (error) this.$message.error(error.message || 'Error Occured')
        else {
          this.updateUserAvatarUrl({
            id: id,
            avatarUrl: null,
          })
          this.account.user.avatarUrl = null
        }
      }
    },
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
    toggleSettingSwitch(value, settingIdx) {
      let setting = this.notificationSettings[settingIdx] || {}
      setting.disabled = !value
      this.$set(this.notificationSettings, settingIdx, setting)
    },
  },
}
</script>
<style lang="scss">
.editprofile-portal-container {
  .el-form-item__label {
    height: 33px;
    font-size: 14px;
    font-weight: 400;
    letter-spacing: 0.5px;
    color: #415e7b !important;
  }

  .fc-avatar-actions {
    padding: 15px;
  }

  .fc-avatar-actions a {
    padding: 5px;
    color: #39b2c2 !important;
  }
}
</style>
