<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog65 setup-dialog fc-securityPolicy-form"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form
      :model="securityPolicy"
      :label-position="'top'"
      ref="securityForm"
      :rules="rules"
    >
      <!-- header section -->
      <div class="form-header inactiveheader">
        <el-row class="flex-middle">
          <el-col :span="14">
            <div class="fc-black2-18 fw6 pT5">
              {{ securityPolicy.name }}
            </div>
          </el-col>
          <el-col :span="10" class="text-right">
            <div>
              <el-button
                class="btn-modal-border"
                @click="closeDialog('securityForm')"
              >
                {{ $t('setup.users_management.cancel') }}
              </el-button>
              <el-button
                class="primarybutton btn-modal-fill mR10"
                type="primary"
                :loading="saving"
                @click="submitForm()"
                :disabled="isDisabled()"
                >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
              >
            </div>
          </el-col>
        </el-row>
      </div>
      <!-- body section -->
      <div class="fc-security-body-con">
        <div class="fc-security-left fc-left-side-bar overflow-y-scroll">
          <a
            id="policy-link"
            @click="scrollTo('policy')"
            class="security-link active"
          >
            {{ $t('setup.setupLabel.policy') }}
          </a>
          <a
            id="password-link"
            @click="scrollTo('password')"
            class="security-link"
          >
            {{ $t('setup.security.pwd_policy') }}
          </a>
          <a id="mfa-link" @click="scrollTo('mfa')" class="security-link">
            {{ $t('setup.setup.mfa') }}
          </a>

          <a
            id="websession-link"
            @click="scrollTo('websession')"
            class="security-link"
          >
            {{ $t('setup.security.web_session') }}
          </a>
        </div>
        <div class="fc-security-right">
          <div id="policy-section">
            <div class="fc-security-header border-top-none">
              <div class="fc-modal-sub-title fw6">
                {{ $t('setup.security.policy_info') }}
              </div>
            </div>
            <div class="p20">
              <div class="label-txt-black line-height20 break-word">
                {{ $t('setup.security.policy_info_desc1') }}
              </div>
              <el-form-item prop="name" class="mT10 mB0" label="Security name">
                <div class="form-input">
                  <el-input
                    :autofocus="true"
                    required
                    class="required fc-input-full-border-select2 width290px"
                    v-model="securityPolicy.name"
                    type="text"
                    autocomplete="off"
                    placeholder="Enter Security Name"
                  />
                </div>
              </el-form-item>
              <el-form-item
                prop="description"
                label="Description"
                class="mT10 mB0"
              >
                <div class="form-input">
                  <el-input
                    :autofocus="true"
                    class="required fc-input-full-border-select2 width100"
                    v-model="securityPolicy.description"
                    autocomplete="off"
                    placeholder="Enter Security description"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 2 }"
                    resize="none"
                  />
                </div>
              </el-form-item>
            </div>
          </div>
          <div id="password-section">
            <div class="fc-security-header">
              <div class="fc-modal-sub-title fw6">
                {{ $t('setup.security.pwd_policy') }}
              </div>
            </div>
            <div class="showHidePolicy" v-if="passwordContentHide">
              <div>
                <div class="label-txt-black pT10 break-word line-height20">
                  {{ $t('setup.security.pwd_policy_desc1') }}
                </div>
                <div class="label-txt-black pT10 break-word line-height20">
                  {{ $t('setup.security.pwd_policy_desc3') }}
                </div>
                <div class="label-txt-black pT10 break-word line-height20">
                  {{ $t('setup.security.pwd_policy_desc4') }}
                </div>
                <div class="label-txt-black pT10 break-word line-height20">
                  {{ $t('setup.security.pwd_policy_desc5') }}
                </div>
              </div>
              <div class="pT20">
                <el-button
                  class="fc-pink-btn-full-width width25 bR5"
                  @click="passwordPolicyInput"
                >
                  {{ $t('setup.security.setup') }}
                </el-button>
              </div>
            </div>
            <div v-if="passwordContentShow" class="mT20 mB20 mL20 mR20">
              <el-row class="flex-middle" :gutter="20">
                <el-col :span="14">
                  <div
                    class="fc-black-14 bold text-left line-height20 break-word"
                  >
                    {{ $t('setup.security.pwd_policy_desc6') }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-select
                    class="fc-input-full-border-select2 width100"
                    v-model="securityPolicy.pwdMinLength"
                    :default-first-option="true"
                    clearable
                  >
                    <el-option
                      v-for="(minLength, indexMinLength) in pwdLengthMin"
                      :value="minLength.value"
                      :key="indexMinLength"
                      :label="minLength.label"
                    >
                    </el-option>
                  </el-select>
                </el-col>
              </el-row>

              <el-row class="flex-middle pT20" :gutter="20">
                <el-col :span="14">
                  <div class="bold fc-black-14 text-left">
                    {{ $t('setup.security.mixed_pwd') }}
                  </div>
                  <div
                    class="pT5 break-word fc-grey-text12-light f13 text-left line-height20"
                  >
                    {{ $t('setup.security.mixed_pwd_desc') }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-switch
                    v-model="securityPolicy.pwdIsMixed"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  >
                  </el-switch>
                </el-col>
              </el-row>

              <el-row class="flex-middle pT20" :gutter="20">
                <el-col :span="14">
                  <div
                    class="fc-black-14 bold text-left line-height20 break-word"
                  >
                    {{ $t('setup.security.min_pwd_desc') }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-select
                    class="fc-input-full-border-select2 width100"
                    v-model="securityPolicy.pwdMinSplChars"
                    :default-first-option="true"
                    clearable
                  >
                    <el-option
                      v-for="(specialCharacters, indexMinSpl) in minSplChars"
                      :value="specialCharacters.value"
                      :key="indexMinSpl"
                      :label="specialCharacters.label"
                    >
                    </el-option>
                  </el-select>
                </el-col>
              </el-row>

              <el-row class="flex-middle pT30" :gutter="20">
                <el-col :span="14">
                  <div
                    class="fc-black-14 bold text-left line-height20 break-word"
                  >
                    {{ $t('setup.security.min_numeric_desc') }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-select
                    class="fc-input-full-border-select2 width100"
                    v-model="securityPolicy.pwdMinNumDigits"
                    :default-first-option="true"
                    clearable
                  >
                    <el-option
                      v-for="(specialCharacters, indexMinSpl) in minSplChars"
                      :value="specialCharacters.value"
                      :key="indexMinSpl"
                      :label="specialCharacters.label"
                    >
                    </el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row class="flex-middle mT20" :gutter="20">
                <el-col :span="14">
                  <div class="pT20 fc-black-14 text-left bold">
                    {{ $t('setup.security.pwd_age') }}
                  </div>
                  <div
                    class="break-word bold fc-grey-text12-light f13 line-height20"
                  >
                    {{ $t('setup.security.pwd_max_age') }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-select
                    class="fc-input-full-border-select2 width100"
                    v-model="securityPolicy.pwdMinAge"
                    :default-first-option="true"
                    clearable
                  >
                    <el-option
                      v-for="(passwordAge, idex) in maximumPasswordAgeData"
                      :value="passwordAge.value"
                      :key="idex"
                      :label="passwordAge.label"
                    >
                    </el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row class="flex-middle mT20" :gutter="20">
                <el-col :span="14">
                  <div class="fc-black-14 bold text-left">
                    {{ $t('setup.security.refusal') }}
                  </div>
                  <div
                    class="break-word fc-grey-text12-light f13 pT5 line-height20"
                  >
                    {{ $t('setup.security.pwd_refusal') }}
                  </div>
                </el-col>
                <el-col :span="10">
                  <el-select
                    class="fc-input-full-border-select2 width100"
                    v-model="securityPolicy.pwdPrevPassRefusal"
                    :default-first-option="true"
                    clearable
                  >
                    <el-option
                      v-for="(specialCharacters, indexMinSpl) in minPrePass"
                      :value="specialCharacters.value"
                      :key="indexMinSpl"
                      :label="specialCharacters.label"
                    >
                    </el-option>
                  </el-select>
                </el-col>
              </el-row>

              <div class="flex-middle pT20" :gutter="20">
                <el-button
                  class="fc-pink-btn-full-width width25 bR5 mT20"
                  @click="passwordPolicyCancel()"
                  v-if="
                    passwordContentShow === true &&
                      securityPolicy.isPwdPolicyEnabled === true
                  "
                >
                  {{ $t('setup.security.remove_policy') }}
                </el-button>
                <el-button
                  class="fc-pink-btn-full-width width25 bR5"
                  @click="passwordPolicyCancel()"
                  v-if="
                    passwordContentShow === true &&
                      securityPolicy.isPwdPolicyEnabled === false
                  "
                >
                  {{ $t('setup.users_management.cancel') }}
                </el-button>
              </div>
            </div>
          </div>
          <div id="mfa-section">
            <div class="fc-security-header">
              <div class="fc-modal-sub-title fw6">
                {{ $t('setup.setup.mfa') }}
              </div>
            </div>
            <div class="showHidePolicy" v-if="hideMfaContent">
              <div class="fc-black-14 text-left line-height20 break-word">
                {{ $t('setup.security.mfa_desc') }}
              </div>
              <el-button
                class="fc-pink-btn-full-width width25 bR5 mT20"
                @click="mfaAction()"
              >
                {{ $t('setup.security.setup') }}
              </el-button>
            </div>
            <div class="mT20 mB20 mL20 mR20" v-if="showMfaInput">
              <el-row class="flex-middle">
                <el-col :span="14">
                  <div class="fc-black-14 fw6 text-left">
                    {{ $t('setup.security.totp') }}
                  </div>
                  <div
                    class="break-word fc-grey-text12-light f13 pT5 line-height20"
                  >
                    {{ $t('setup.security.totp_desc') }}
                  </div>
                </el-col>
                <el-col :span="10" class="pL20">
                  <el-switch
                    v-model="securityPolicy.isTOTPEnabled"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  >
                  </el-switch>
                </el-col>
              </el-row>
              <div class="flex-middle">
                <el-button
                  class="fc-pink-btn-full-width width25 bR5 mR10 mT20"
                  @click="mfaCancel()"
                  v-if="
                    showMfaInput === true &&
                      securityPolicy.isTOTPEnabled === true
                  "
                >
                  {{ $t('setup.security.remove_mfa') }}
                </el-button>
                <el-button
                  class="fc-pink-btn-full-width width25 bR5 mT20"
                  @click="mfaCancel()"
                  v-if="
                    showMfaInput === true &&
                      securityPolicy.isTOTPEnabled === false
                  "
                >
                  {{ $t('setup.users_management.cancel') }}
                </el-button>
              </div>
            </div>
          </div>
          <div id="websession-section">
            <div class="fc-security-header">
              <div class="fc-modal-sub-title fw6">
                {{ $t('setup.security.web_session') }}
              </div>
            </div>

            <div class="showHidePolicy" v-if="hideSessionContent">
              <div class="fc-black-14 text-left line-height20 break-word">
                {{ $t('setup.security.session_desc2') }}
              </div>
              <el-button
                class="fc-pink-btn-full-width width25 bR5 mT20"
                @click="sessionAction()"
              >
                {{ $t('setup.security.setup') }}
              </el-button>
            </div>

            <div class="p20" v-if="showSessionInput">
              <el-row>
                <div
                  class="break-word fc-grey-text12-light f13 pT5 line-height20"
                >
                  {{ $t('setup.security.web_session_lifetime_desc') }}
                </div>
              </el-row>
              <el-row
                :gutter="20"
                class="session-lifetime-input-row flex-middle"
              >
                <el-col :span="10">
                  <div class="fc-black-14 fw6 text-left">
                    {{ $t('setup.security.web_session_lifetime') }}
                  </div>
                </el-col>
                <el-col :span="14">
                  <el-row :gutter="20" class="days-and-hours">
                    <el-col :span="12">
                      <el-form-item prop="days">
                        <el-input v-model.number="securityPolicy.days">
                          <template slot="append">
                            <span
                              v-tippy="{
                                arrow: true,
                                arrowType: 'round',
                                animation: 'fade',
                                theme: 'light',
                              }"
                              content="Days"
                              class="cursor-default"
                              >{{ $t('common._common.DD') }}</span
                            >
                          </template>
                        </el-input>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item prop="hours">
                        <el-input v-model.number="securityPolicy.hours">
                          <template slot="append">
                            <span
                              v-tippy="{
                                arrow: true,
                                arrowType: 'round',
                                animation: 'fade',
                                theme: 'light',
                              }"
                              content="Hours"
                              class="cursor-default"
                              >{{ $t('common._common.HH') }}</span
                            >
                          </template>
                        </el-input>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
              <div class="flex-middle"></div>
              <el-row
                :gutter="20"
                class="session-lifetime-input-row flex-middle"
              >
                <el-col :span="10">
                  <div class="fc-black-14 fw6 text-left">
                    {{ $t('setup.security.idle_session') }}
                  </div>
                </el-col>
                <el-col :span="14">
                  <el-row :gutter="20" class="days-and-hours">
                    <el-col :span="12">
                      <el-form-item prop="idleHours">
                        <el-input v-model.number="securityPolicy.idleHours">
                          <template slot="append">
                            <span
                              v-tippy="{
                                arrow: true,
                                arrowType: 'round',
                                animation: 'fade',
                                theme: 'light',
                              }"
                              content="Hours"
                              class="cursor-default"
                              >{{ $t('common._common.HH') }}</span
                            >
                          </template>
                        </el-input>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item prop="idleMinutes">
                        <el-input v-model.number="securityPolicy.idleMinutes">
                          <template slot="append">
                            <span
                              v-tippy="{
                                arrow: true,
                                arrowType: 'round',
                                animation: 'fade',
                                theme: 'light',
                              }"
                              content="Minutes"
                              class="cursor-default"
                              >{{ $t('common._common.minutes') }}</span
                            >
                          </template>
                        </el-input>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-col>
              </el-row>
              <div class="flex-middle">
                <el-button
                  class="fc-pink-btn-full-width width25 bR5 mT20"
                  @click="sessionCancel()"
                  v-if="showSessionInput === true"
                >
                  {{ $t('setup.users_management.cancel') }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ScrollHandlingMixin from 'pages/setup/securitypolicy/SecurityScrollHandling'
const DAY_IN_SECONDS = 24 * 60 * 60
const HOUR_IN_SECONDS = 60 * 60
const MINUTE_IN_SECONDS = 60
export default {
  props: ['isNew', 'selectedData'],
  mixins: [ScrollHandlingMixin],
  data() {
    return {
      securityPolicy: {
        name: 'Untitled',
        description: '',
        isDefault: false,
        pwdMinLength: null,
        pwdIsMixed: true,
        pwdMinSplChars: null,
        pwdMinNumDigits: null,
        pwdMinAge: null,
        pwdPrevPassRefusal: null,
        webSessLifeTime: null,
        isTOTPEnabled: false,
        isMOTPEnabled: false,
        isPwdPolicyEnabled: false,
        isWebSessManagementEnabled: false,
        webSessLifeTimesec: null,
        hours: 0,
        days: 0,
        idleHours: 0,
        idleMinutes: 0,
        idleSessionTimeOut: null,
      },
      sidebarElements: [
        '#policy-link',
        '#password-link',
        '#mfa-link',
        '#websession-link',
      ],
      sectionElements: [
        '#policy-section',
        '#password-section',
        '#mfa-section',
        '#websession-section',
      ],
      saving: false,
      active: '',
      spacepermission: false,
      passwordContentHide: true,
      passwordContentShow: false,
      showMfaInput: false,
      hideMfaContent: true,
      hideSessionContent: true,
      showSessionInput: false,
      sessionSec: false,
      mfasec: false,
      passwordSec: false,
      policySec: false,
      maximumPasswordAgeData: [
        {
          value: null,
          label: 'Not required',
        },
        {
          value: 30,
          label: '30 Days',
        },
        {
          value: 45,
          label: '45 Days',
        },
        {
          value: 60,
          label: '60 Days',
        },
        {
          value: 90,
          label: '90 Days',
        },
        {
          value: 120,
          label: '120 Days',
        },
      ],
      pwdLengthMin: [
        {
          value: null,
          label: 'Not required',
        },
        {
          value: 8,
          label: 8,
        },
        {
          value: 9,
          label: 9,
        },
        {
          value: 10,
          label: 10,
        },
        {
          value: 11,
          label: 11,
        },
        {
          value: 12,
          label: 12,
        },
        {
          value: 13,
          label: 13,
        },
        {
          value: 14,
          label: 14,
        },
        {
          value: 15,
          label: 15,
        },
      ],
      minSplChars: [
        {
          value: null,
          label: 'Not required',
        },
        {
          value: 1,
          label: 1,
        },
        {
          value: 2,
          label: 2,
        },
        {
          value: 3,
          label: 3,
        },
        {
          value: 4,
          label: 4,
        },
        {
          value: 5,
          label: 5,
        },
        {
          value: 6,
          label: 6,
        },
        {
          value: 7,
          label: 7,
        },
        {
          value: 8,
          label: 8,
        },
        {
          value: 9,
          label: 9,
        },
        {
          value: 10,
          label: 10,
        },
      ],
      minPrePass: [
        {
          value: null,
          label: 'Not required',
        },
        {
          value: 1,
          label: 1 + ' password remembered',
        },
        {
          value: 2,
          label: 2 + ' passwords remembered',
        },
        {
          value: 3,
          label: 3 + ' passwords remembered',
        },
        {
          value: 4,
          label: 4 + ' passwords remembered',
        },
        {
          value: 5,
          label: 5 + ' passwords remembered',
        },
        {
          value: 6,
          label: 6 + ' passwords remembered',
        },
        {
          value: 7,
          label: 7 + ' passwords remembered',
        },
        {
          value: 8,
          label: 8 + ' passwords remembered',
        },
        {
          value: 9,
          label: 9 + ' passwords remembered',
        },
        {
          value: 10,
          label: 10 + ' passwords remembered',
        },
      ],
      rules: {
        days: [
          {
            validator: function(rule, days, callback) {
              let isNumber = Number.isInteger(days)
              if (!isNumber || days < 0 || days > 365) {
                callback(new Error('Please enter valid input'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
        hours: [
          {
            validator: function(rule, hours, callback) {
              let isNumber = Number.isInteger(hours)
              if (!isNumber || hours > 8760 || hours < 0) {
                callback(new Error('Please enter valid input'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
        idleHours: [
          {
            validator: function(rule, hours, callback) {
              let isNumber = Number.isInteger(hours)
              if (!isNumber || hours > 8760 || hours < 0) {
                callback(new Error('Please enter valid input'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
        idleMinutes: [
          {
            validator: function(rule, minutes, callback) {
              let isNumber = Number.isInteger(minutes)
              if (!isNumber || minutes > 1440 || minutes < 0) {
                callback(new Error('Please enter valid input'))
              } else {
                callback()
              }
            },
            trigger: 'blur',
          },
        ],
      },
    }
  },
  computed: {
    webSessLifeTimesec() {
      let { securityPolicy } = this
      let days = securityPolicy?.days
      let hours = securityPolicy?.hours
      if ((!isEmpty(days) && days) || (!isEmpty(hours) && hours)) {
        return days * DAY_IN_SECONDS + hours * HOUR_IN_SECONDS
      } else {
        return null
      }
    },
    webSessLifeTime() {
      let { securityPolicy } = this
      let { webSessLifeTime } = securityPolicy || {}
      return webSessLifeTime
    },
    idleSessionTime() {
      let { securityPolicy } = this
      let idleHours = securityPolicy?.idleHours
      let idleMinutes = securityPolicy?.idleMinutes
      if (
        (!isEmpty(idleHours) && idleHours) ||
        (!isEmpty(idleMinutes) && idleMinutes)
      ) {
        return idleHours * HOUR_IN_SECONDS + idleMinutes * MINUTE_IN_SECONDS
      } else {
        return null
      }
    },
  },
  async created() {
    this.loading = true
    if (!this.isNew) {
      this.securityPolicy = this.selectedData
      let { securityPolicy } = this
      let { webSessLifeTime } = securityPolicy || {}
      if (!isEmpty(webSessLifeTime) && webSessLifeTime) {
        securityPolicy.days = webSessLifeTime
        securityPolicy.hours = 0
      }
      if (!isEmpty(securityPolicy) && isEmpty(webSessLifeTime)) {
        this.setDaysAndHours()
      }
      if (!isEmpty(securityPolicy)) {
        this.setIdleHoursAndMinutes()
      }
      this.securityPolicy = {
        ...this.selectedData,
      }
      this.onLoadWebsession()
      this.onLoadMfa()
      this.onLoadPwdPolicy()
    }
    this.$nextTick(this.registerScrollHandler)
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    passwordPolicyInput() {
      this.passwordContentHide = false
      this.passwordContentShow = true
    },
    passwordPolicyCancel() {
      this.passwordContentHide = true
      this.passwordContentShow = false
      this.securityPolicy.pwdMinLength = null
      this.securityPolicy.pwdIsMixed = false
      this.securityPolicy.pwdMinSplChars = null
      this.securityPolicy.pwdMinNumDigits = null
      this.securityPolicy.pwdMinAge = null
      this.securityPolicy.pwdPrevPassRefusal = null
    },
    mfaAction() {
      this.showMfaInput = true
      this.hideMfaContent = false
    },
    mfaCancel() {
      this.showMfaInput = false
      this.hideMfaContent = true
      this.securityPolicy.isTOTPEnabled = false
    },
    sessionAction() {
      this.showSessionInput = true
      this.hideSessionContent = false
    },
    sessionCancel() {
      this.showSessionInput = false
      this.hideSessionContent = true
      this.securityPolicy.webSessLifeTime = null
      this.securityPolicy.idleSessionTimeOut = null
      this.securityPolicy.days = 0
      this.securityPolicy.hours = 0
      this.securityPolicy.idleHours = 0
      this.securityPolicy.idleMinutes = 0
    },
    submitForm() {
      this.$refs['securityForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let { isNew } = this
        this.securityPolicy.webSessLifeTimesec = this.webSessLifeTimesec
        this.securityPolicy.idleSessionTimeOut = this.idleSessionTime
        let securityPolicy = this.securityPolicy

        if (
          !isEmpty(
            securityPolicy.pwdMinLength ||
              securityPolicy.pwdIsMixed ||
              securityPolicy.pwdMinSplChars ||
              securityPolicy.pwdMinNumDigits ||
              securityPolicy.pwdMinAge ||
              securityPolicy.pwdPrevPassRefusal
          )
        ) {
          securityPolicy.isPwdPolicyEnabled = true
        } else if (isEmpty(securityPolicy.passwordPolicyEnable)) {
          securityPolicy.isPwdPolicyEnabled = false
        }
        let webSessLifeTimesec = securityPolicy?.webSessLifeTimesec
        let idleSessionTimeOut = securityPolicy?.idleSessionTimeOut
        if (!isEmpty(webSessLifeTimesec) && !isEmpty(idleSessionTimeOut)) {
          if (webSessLifeTimesec < idleSessionTimeOut) {
            this.$message.error(
              'Session Life Time should be greater than Idle Timeout Period'
            )
            this.saving = false
            return
          }
        }
        if (
          !isEmpty(securityPolicy.webSessLifeTime) ||
          !isEmpty(webSessLifeTimesec) ||
          !isEmpty(idleSessionTimeOut)
        ) {
          securityPolicy.isWebSessManagementEnabled = true
        } else {
          securityPolicy.isWebSessManagementEnabled = false
        }

        let params = {
          securityPolicy,
        }

        let url = isNew ? 'v2/createSecurityPolicy' : 'v2/updateSecurityPolicy'
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success('Security policy saved successfully')
          this.$emit('onClose')
          this.closeDialog()
        }
        this.saving = false
      })
    },
    isDisabled() {
      if (this.isNew) {
        return false
      } else if (!this.isNew) {
        return !this.$refs['securityForm'] === true
      } else {
        return false
      }
    },
    onLoadWebsession() {
      let isWebSessManagementEnabled = this.$getProperty(
        this,
        'securityPolicy.isWebSessManagementEnabled',
        false
      )
      if (isWebSessManagementEnabled) {
        this.showSessionInput = true
        this.hideSessionContent = false
      } else {
        this.showSessionInput = false
        this.hideSessionContent = true
      }
    },
    onLoadMfa() {
      if (this.securityPolicy.isTOTPEnabled === true) {
        this.showMfaInput = true
        this.hideMfaContent = false
      } else {
        this.showMfaInput = false
        this.hideMfaContent = true
      }
    },
    onLoadPwdPolicy() {
      if (this.securityPolicy.isPwdPolicyEnabled === true) {
        this.passwordContentHide = false
        this.passwordContentShow = true
      } else {
        this.passwordContentHide = true
        this.passwordContentShow = false
      }
    },
    setDaysAndHours() {
      let { securityPolicy } = this
      let { webSessLifeTimesec } = securityPolicy || {}
      if (!isEmpty(webSessLifeTimesec)) {
        if (webSessLifeTimesec < DAY_IN_SECONDS) {
          this.securityPolicy.days = 0
          this.securityPolicy.hours = Math.floor(
            webSessLifeTimesec / HOUR_IN_SECONDS
          )
        } else {
          this.securityPolicy.days = Math.floor(
            webSessLifeTimesec / DAY_IN_SECONDS
          )
          this.securityPolicy.hours = Math.floor(
            (webSessLifeTimesec % DAY_IN_SECONDS) / HOUR_IN_SECONDS
          )
        }
      } else {
        this.securityPolicy.days = 0
        this.securityPolicy.hours = 0
      }
    },
    setIdleHoursAndMinutes() {
      let { securityPolicy } = this
      let { idleSessionTimeOut } = securityPolicy || {}
      if (!isEmpty(idleSessionTimeOut)) {
        if (idleSessionTimeOut < HOUR_IN_SECONDS) {
          this.securityPolicy.idleHours = 0
          this.securityPolicy.idleMinutes = Math.floor(
            idleSessionTimeOut / MINUTE_IN_SECONDS
          )
        } else {
          this.securityPolicy.idleHours = Math.floor(
            idleSessionTimeOut / HOUR_IN_SECONDS
          )
          this.securityPolicy.idleMinutes = Math.floor(
            (idleSessionTimeOut % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS
          )
        }
      } else {
        this.securityPolicy.idleHours = 0
        this.securityPolicy.idleMinutes = 0
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-securityPolicy-form {
  position: relative;
  .session-lifetime-input-row {
    margin-top: 10px;
    padding: 20px;
    padding-top: 10px;
    padding-left: 0px;
  }
  .days-and-hours {
    .el-form-item {
      margin-bottom: 0;
    }
  }
  .inactiveheader {
    margin-top: 0px;
    padding: 20px 20px 15px;
  }
  .input-border-remove .el-input__inner {
    border: none !important;
    color: #324056;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 0.7px;
  }
  .btn-modal-fill {
    width: 125px;
    height: 40px;
    padding: 12px 30px !important;
    font-weight: 500 !important;
    letter-spacing: 0.7px;
  }
  .btn-modal-border {
    width: 125px;
    height: 40px;
    font-weight: 500 !important;
    letter-spacing: 0.7px;
    &:hover {
      background: #39b2c2 !important;
      color: #fff !important;
      border: 1px solid #39b2c2 !important;
    }
  }
  .fc-security-body-con {
    width: calc(100% - 200px);
    position: fixed;
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    .fc-security-left {
      width: 300px;
      height: 100vh;
      overflow-y: scroll;
      display: inline-block;
      background-color: #fff;
      border-right: 1px solid rgb(225, 233, 237);
      border-top: 1px solid rgb(237, 238, 239);
    }
    .fc-security-right {
      width: calc(76% - 300px);
      height: calc(100vh - 100px);
      display: inline-block;
      overflow-y: scroll;
      border-top: 1px solid #edeeef;
      padding-bottom: 600px;
    }
  }
  .fc-security-header {
    padding: 20px 20px;
    background-color: #fcfcfc;
    width: 100%;
    border-bottom: 1px solid #edeeef;
    border-top: 1px solid #edeeef;
    display: flex;
    justify-content: flex-start;
    align-items: center;
  }
  .showHidePolicy {
    border: 1px solid #dee7ef;
    margin: 20px;
    border-radius: 4px;
    padding: 20px 30px;
    background: rgb(248 251 254 / 50%);
  }
}
.security-link {
  display: block;
  position: relative;
  padding: 11px 0px 11px 40px;
  margin: 0;
  color: #555;
  font-size: 14px;
  border-left: 4px solid transparent;
  letter-spacing: 0.2px;
  text-transform: capitalize;
}
.security-link.active {
  background: #f3f4f7;
  border-left: 4px solid #ef4f8f;
  font-weight: 500;
}
</style>

<style lang="scss">
.fc-securityPolicy-form {
  .days-and-hours {
    .el-form-item {
      margin-bottom: 0;
      .el-input {
        input {
          border: 1px solid #d8dce5;
          padding-left: 5px;
          height: 35px;
          border-radius: 2px;
        }
      }
    }
  }
}
</style>
