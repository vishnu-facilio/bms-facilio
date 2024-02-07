<template>
  <div class="layout-padding">
    <div class="fc-form">
      <div class="fc-form-title setting-form-title">
        {{ $t('setup.setup_profile.company_profile') }}
      </div>
      {{ uploadurl }}
      <form v-on:submit.prevent="saveOrgSettings">
        <div class="profile-container">
          <div class="profile-left">
            <div class="logo-upload">
              <span
                class="input-upload fc-input-full-border-select2"
                :class="{ customlogo: orgsettings.logoUrl }"
              >
                <input
                  ref="orgLogo"
                  type="file"
                  class="upload "
                  style="width: 200px;height:100px;"
                  @change="getimage($event.target.files[0])"
                />
                <span class="upload-icon-block">
                  <img
                    v-if="logoImageUrl"
                    :src="logoImageUrl"
                    class="upload-img2"
                  />
                  <img
                    v-else-if="orgsettings.logoUrl"
                    :src="orgsettings.logoUrl"
                    class="upload-img2"
                  />
                  <img
                    v-else-if="!orgsettings || !orgsettings.logoUrl"
                    src="~assets/upload.svg"
                    class="upload-img"
                  />
                </span>
                <span class="upload-text">
                  {{ $t('setup.setup_profile.choose_photo') }}
                </span>
              </span>
            </div>
          </div>
          <div class="profile-right">
            <p class="ruletitle">
              {{ $t('setup.setup_profile.general_settings') }}
            </p>
            <el-input
              v-model="orgsettings.name"
              :placeholder="$t('setup.setup_profile.enter_the_name')"
            />
            <div class="profile-input-container mT10">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup.select_language') }}
                </p>
                <el-select
                  v-model="orgsettings.language"
                  :placeholder="$t('setup.setup.select_language')"
                  class="fc-input-full-border-select2"
                  :disabled="!canEditLanguage"
                >
                  <el-option
                    v-for="(language, index) in languageList"
                    :key="index"
                    :label="language.label"
                    :value="language.value"
                  >
                  </el-option>
                </el-select>
              </div>
              <div class="profile-input-group mL30">
                <p class="input-label-text">
                  {{ $t('common.wo_report.select_date_format') }}
                </p>
                <el-select
                  v-model="orgsettings.dateFormat"
                  :placeholder="$t('common.wo_report.select_date_format')"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(dateformat, index) in dateformatList"
                    :key="index"
                    :label="dateformat.label"
                    :value="dateformat.value"
                  >
                  </el-option>
                </el-select>
              </div>
            </div>
            <div class="profile-input-container mT10">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('common.wo_report.select_time_format') }}
                </p>
                <el-select
                  v-model="orgsettings.timeFormat"
                  :placeholder="$t('common.wo_report.select_time_format')"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(timestate, index) in timestateList"
                    :key="index"
                    :label="timestate.label"
                    :value="timestate.value"
                  >
                  </el-option>
                </el-select>
              </div>
            </div>
            <div class="profile-input-container mT10">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.time_zone') }}
                </p>
                <el-select
                  v-model="orgsettings.timezone"
                  :placeholder="$t('setup.setup_profile.ente_time_zone')"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(timezone, index) in timezoneList"
                    :key="index"
                    :label="timezone.label"
                    :value="timezone.value"
                  >
                  </el-option>
                </el-select>
              </div>
              <div class="profile-input-group mL30">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.currency') }}
                </p>
                <el-select
                  v-model="orgsettings.currency"
                  :placeholder="$t('setup.setup_profile.enter_currency')"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="currency in currencyList"
                    :key="currency.value"
                    :label="currency.label"
                    :value="currency.value"
                  >
                  </el-option>
                </el-select>
              </div>
            </div>
            <div class="profile-input-container mT10">
              <div class="profile-input-group">
                <div class="d-flex align-center">
                  <p class="input-label-text">
                    {{ $t('setup.setup_profile.user_specific_timezone') }}
                  </p>
                  <span
                    v-tippy="{
                      arrow: true,
                      arrowType: 'round',
                      animation: 'fade',
                    }"
                    :content="
                      $t('setup.setup_profile.user_specific_timezone_info')
                    "
                    class="el-icon-info timezone-info-icon mL5"
                  >
                  </span>
                </div>
                <el-checkbox
                  v-model="orgsettings.allowUserTimeZone"
                ></el-checkbox>
              </div>
            </div>

            <p class="ruletitle mT15">
              {{ $t('setup.setup_profile.adderss_info') }}
            </p>
            <div class="profile-input-container">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.street') }}
                </p>
                <el-input
                  v-model="orgsettings.street"
                  :placeholder="$t('setup.setup_profile.enter_street')"
                  class="fc-input-full-border-select2"
                />
              </div>
              <div class="profile-input-group mL30">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.city') }}
                </p>
                <el-input
                  v-model="orgsettings.city"
                  :placeholder="$t('setup.setup_profile.entre_city')"
                  class="fc-input-full-border-select2"
                />
              </div>
            </div>
            <div class="profile-input-container">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.state') }}
                </p>
                <el-input
                  v-model="orgsettings.state"
                  :placeholder="$t('setup.setup_profile.enter_state')"
                  class="fc-input-full-border-select2"
                />
              </div>
              <div class="profile-input-group mL30">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.zipcode') }}
                </p>
                <el-input
                  v-model="orgsettings.zip"
                  :placeholder="$t('setup.setup_profile.enter_zipcode')"
                  class="fc-input-full-border-select2"
                />
              </div>
            </div>
            <div class="profile-input-container">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.country') }}
                </p>
                <el-select
                  v-model="orgsettings.country"
                  :placeholder="$t('setup.setup_profile.enter_country')"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="country in countryList"
                    :key="country.value"
                    :label="country.label"
                    :value="country.value"
                  >
                  </el-option>
                </el-select>
              </div>
            </div>
            <div
              class="profile-input-container"
              v-if="
                $account.data.orgInfo.isPlanRequest === 'true' ||
                  $account.data.orgInfo.isPlanRequest === true
              "
            >
              <div class="profile-input-group form-section ruletitle">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.plans_buildings') }}
                </p>
                <table class="payment-table mT20">
                  <thead>
                    <tr>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.current_plan') }}
                      </th>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.payment_info') }}
                      </th>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.customer_id') }}
                      </th>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.billing_cycle') }}
                      </th>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.next_billing') }}:
                      </th>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.no_of_users') }}
                      </th>
                      <th class="table-Heading">
                        {{ $t('setup.setup_profile.no_of_buildings') }}
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td class="table-td">
                        {{ subscription.subscription.plan_id }}
                      </td>
                      <td class="table-td">
                        {{ subscription.card.masked_number }}
                        <div
                          type="text"
                          @click="dialogVisible = true"
                          class="link-text pointer"
                        >
                          {{ $t('setup.setup_profile.update_card') }}
                        </div>
                        <el-dialog
                          :visible.sync="dialogVisible"
                          :fullscreen="true"
                          custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
                          :before-close="closeDialog"
                          style="z-index: 999999"
                        >
                          <div class="new-header-container">
                            <div class="new-header-modal">
                              <div class="new-header-text">
                                <div class="setup-modal-title">
                                  {{
                                    $t(
                                      'setup.setup_profile.update_card_details'
                                    )
                                  }}
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="new-body-modal" style="margin-top: 0;">
                            <div class="updatedialog-body">
                              <div class="mT20">
                                <p class="input-label-text">
                                  {{ $t('setup.setup_profile.first_name') }}
                                </p>
                                <el-input
                                  placeholder="Please input"
                                  v-model="card.firstName"
                                  :autofocus="true"
                                  class="fc-input-full-border-select2"
                                ></el-input>
                              </div>
                              <div class="mT20">
                                <p class="input-label-text">
                                  {{ $t('setup.setup_profile.last_name') }}
                                </p>
                                <el-input
                                  placeholder="Please input"
                                  v-model="card.lastName"
                                  class="fc-input-full-border-select2"
                                ></el-input>
                              </div>
                              <div class="mT20">
                                <p class="input-label-text">
                                  {{ $t('setup.setup_profile.no') }}
                                </p>
                                <el-input
                                  placeholder="Please input"
                                  v-model="card.cardNumber"
                                  class="fc-input-full-border-select2"
                                ></el-input>
                              </div>
                              <div class="mT20">
                                <p class="input-label-text">
                                  {{ $t('setup.setup_profile.expiry_month') }}
                                </p>
                                <el-input
                                  placeholder="Please input"
                                  v-model="card.expiryMonth"
                                  class="fc-input-full-border-select2"
                                ></el-input>
                              </div>
                              <div class="mT20">
                                <p class="input-label-text">
                                  {{ $t('setup.setup_profile.expiry_year') }}
                                </p>
                                <el-input
                                  placeholder="Please input"
                                  v-model="card.expiryYear"
                                  class="fc-input-full-border-select2"
                                ></el-input>
                              </div>
                              <div class="mT20">
                                <p class="input-label-text">
                                  {{ $t('setup.setup_profile.cvv') }}
                                </p>
                                <el-input
                                  placeholder="Please input"
                                  v-model="card.cvv"
                                  class="fc-input-full-border-select2"
                                ></el-input>
                              </div>
                            </div>
                          </div>
                          <div class="modal-dialog-footer">
                            <el-button
                              @click="closeDialog()"
                              class="modal-btn-cancel"
                              >{{ $t('common._common.cancel') }}</el-button
                            >
                            <el-button
                              type="primary"
                              @click="updateCard()"
                              class="modal-btn-save"
                              >{{ $t('common._common.update') }}</el-button
                            >
                          </div>
                        </el-dialog>
                      </td>
                      <td class="table-td">
                        {{ subscription.card.customer_id }}
                      </td>
                      <td class="table-td">
                        {{ subscription.subscription.billing_period_unit }}
                      </td>
                      <td class="table-td">
                        {{ subscription.nextBillingTime }}
                      </td>
                      <td class="table-td">
                        {{ subscription.subscription.addons[0].quantity }}
                      </td>
                      <td class="table-td">
                        {{ subscription.subscription.addons[1].quantity }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <p class="ruletitle mT20">
              {{ $t('setup.setup_profile.contact_info') }}
            </p>
            <div class="profile-input-container">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.phone') }}
                </p>
                <el-input
                  v-model="orgsettings.phone"
                  :placeholder="$t('setup.setup_profile.enter_phone')"
                  class="fc-input-full-border2"
                />
              </div>
              <div class="profile-input-group mL30">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.mobile') }}
                </p>
                <el-input
                  v-model="orgsettings.mobile"
                  :placeholder="$t('setup.setup_profile.enter_mobile')"
                  class="fc-input-full-border2"
                />
              </div>
            </div>
            <div class="profile-input-container">
              <div class="profile-input-group">
                <p class="input-label-text">
                  {{ $t('setup.setup_profile.fax') }}
                </p>
                <el-input
                  v-model="orgsettings.fax"
                  :placeholder="$t('setup.setup_profile.enter_fax')"
                  class="fc-input-full-border2"
                />
              </div>
            </div>
            <p class="ruletitle mT20">
              {{ $t('setup.setup_profile.operating_hour') }}
            </p>

            <!-- operating hours -->
            <div class="d-flex">
              <div class="flex-middle">
                <el-select
                  v-model="businessHourId"
                  :placeholder="$t('common.wo_report.select_operating_hour')"
                >
                  <el-option
                    v-for="(value, index) in operatingHoursData"
                    :key="index"
                    :label="value.name"
                    :value="value.id"
                  ></el-option>
                </el-select>
                <div class="pointer pL10 bold" @click="showOphoursChooser">
                  <inline-svg
                    src="lookup"
                    iconClass="icon icon-sm-md mT5 fc-lookup-icon"
                  ></inline-svg>
                </div>
              </div>
            </div>
            <operating-hours
              class="f14 operatinghours-comp"
              ref="operating-hours-view"
              :changeBH="onchangeBH"
              :selectOnly="selectOnly"
              :hideChangeBtn="true"
            ></operating-hours>
            <!-- operating hours -->
            <el-button
              v-model="submit"
              loader
              type="primary"
              class="setup-el-btn mT20"
              style="width: 120px"
              @click="saveOrgSettings"
            >
              {{ $t('setup.setup_profile.profile_save') }}
              <span slot="loading">{{ $t('common._common._saving') }}</span>
            </el-button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import OperatingHours from '@/widget/OperatingHoursView'
import dateFormats from 'util/data/date-formats'
import timezones from 'util/data/timezones'
import countries from 'util/data/countries'
import currency from 'util/data/currency'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['selectOnly'],
  components: {
    OperatingHours,
  },
  title() {
    return 'Company Settings'
  },
  data() {
    return {
      languageList: Constants.languages,
      dateformatList: dateFormats,
      submit: false,
      businessHourId: null,
      uploadurl: '',
      logoImageUrl: null,
      timezoneList: timezones,
      timestateList: [
        {
          label:
            '12 hour' +
            ' ' +
            new Date().toLocaleTimeString('en-US', { hour12: 2 }),
          value: 2,
        },
        {
          label:
            '24 hour' +
            ' ' +
            new Date().toLocaleTimeString('en-GB', { hour12: false }),
          value: 1,
        },
      ],
      dialogVisible: false,
      countryList: countries,
      subscription: {
        nextBillingTime: moment().format('DD-MMM-YYYY'),
      },
      card: {
        firstName: '',
        lastName: '',
        cardNumber: '',
        expiryMonth: '',
        expiryYear: '',
        cvv: '',
      },
      operatingHoursData: [],
      orgsettings: {
        name: null,
        currency: null,
        timezone: null,
        street: null,
        city: null,
        state: null,
        zip: null,
        country: null,
        phone: null,
        mobile: null,
        fax: null,
        businessHour: null,
        language: null,
        timeFormat: null,
        dateFormat: null,
        allowUserTimeZone: null,
      },
    }
  },
  async created() {
    this.$store.dispatch('loadBuildings')
    this.getOrgSettings()
    this.getSubscription()
    this.getOperatingHours()
  },
  computed: {
    currencyList() {
      return currency.map(cur => ({
        label: this.$t(cur.label),
        value: cur.value,
      }))
    },
    canEditLanguage() {
      const isDevMode = process.env.NODE_ENV === 'development'
      const { query } = this.$route
      return query.editLang === 'true' || isDevMode
    },
  },
  watch: {
    orgsettings: {
      handler(val) {
        if (val.businessHour > 0) {
          this.businessHourId = val.businessHour
        }
      },
      immediate: true,
    },
  },
  methods: {
    getOrgSettings() {
      return API.get('/settings/company').then(({ data, error }) => {
        if (!error && data.org) {
          this.orgsettings = this.formatdata(data.org)
        }
      })
    },
    formatdata(data) {
      let d = {}
      Object.keys(data).forEach(key => {
        let val = data[key]
        if (val === -1) {
          val = null
        }
        this.$set(d, key, val)
      })
      return d
    },
    getSubscription() {
      return API.get('/setup/subscription').then(({ data, error }) => {
        if (!error) this.subscription = data.subscription
        if (this.subscription) {
          this.subscription.nextBillingTime = this.prepareTime(
            data.subscription
          )
        }
      })
    },
    getOperatingHours() {
      return API.get('/v2/businesshours/list').then(({ data, error }) => {
        this.operatingHoursData = !error ? data.list : []
      })
    },
    updateCard() {
      return API.post('/setup/updatecard', { card: self.card }).then(
        ({ error }) => {
          if (!error) {
            this.$message({
              message: self.$t('setup.setup_profile.card_update_success'),
              type: 'success',
            })
          }
          this.$emit('close', true)
        }
      )
    },
    saveOrgSettings() {
      let files = this.$refs.orgLogo.files
      if (this.businessHourId) {
        this.orgsettings.businessHour = this.businessHourId
      }

      let formdata = new FormData()

      let keys = Object.keys(this.orgsettings)
      for (let key of keys) {
        if (!isEmpty(this.orgsettings[key])) {
          formdata.append('org.' + key, this.orgsettings[key])
        }
      }
      if (files.length) {
        formdata.append('org.logo', files[0])
      }

      return API.post('/settings/updateCompanySettings', formdata).then(
        async ({ error }) => {
          if (!error) {
            this.logoImageUrl = null
            this.$message({
              message: this.$t('setup.setup_profile.company_profile_details'),
              type: 'success',
            })
            await this.$store.dispatch('getCurrentAccount')
          }
        }
      )
    },
    closeDialog() {
      this.dialogVisible = false
    },
    openUpdateDialog() {
      this.openUpdateDialog = true
    },
    prepareTime(data) {
      if (data && data.subscription.next_billing_at) {
        let mills = data.subscription.next_billing_at + ''
        let time =
          mills.length > 10
            ? data.subscription.next_billing_at
            : data.subscription.next_billing_at * 1000
        let t = this.$options.filters.formatDate(time, true, false)
        return t
      }
    },
    getimage(file) {
      let self = this
      let picReader = new FileReader()
      picReader.addEventListener('load', function(event) {
        self.logoImageUrl = event.target.result
      })
      picReader.readAsDataURL(file)
    },
    showOphoursChooser() {
      this.$refs['operating-hours-view'].showOperatingHoursDialog = true
    },
    onchangeBH(businessHour) {
      this.businessHourId = businessHour.id
    },
  },
}
</script>
<style>
.layout-padding {
  padding: 1.5rem 2.4rem 40px !important;
}
.profile-right .el-input .el-input__inner,
.profile-right .el-textarea .el-textarea__inner {
  width: 300px;
  border: 1px solid #e2e8ee;
  padding-left: 15px;
  line-height: 40px;
  height: 40px;
  border-radius: 3px;
}
.el-input__inner {
  color: #333333;
}
.input-upload {
  background-color: #ff4a3b;
  border: none;
  border-radius: 100%;
  margin-top: 10px;
  color: #fff;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
  position: relative;
  display: inline-block;
  margin-top: 0;
  width: 100px;
  height: 100px;
}
.input-upload .upload {
  position: absolute;
  top: 0;
  right: 0;
  margin: 0;
  padding: 0;
  font-size: 20px;
  cursor: pointer;
  opacity: 0;
  filter: alpha(opacity=0);
  cursor: pointer;
  font-size: 10px;
  left: 0;
  z-index: 1;
}
.upload-img {
  max-width: 50px;
  position: absolute;
  top: 25px;
  left: 25%;
  vertical-align: middle;
}
.input-upload .upload-icon-block {
  color: #fff;
}
.upload-text {
  position: absolute;
  text-align: center;
  background: rgba(0, 0, 0, 0.6);
  padding: 13px 18px;
  overflow: hidden;
  color: #fff;
  font-size: 10px;
  font-weight: 500;
  left: 1px;
  bottom: 0px;
  overflow: hidden;
  vertical-align: middle;
}
.input-upload .upload-text {
  visibility: hidden;
}
.logo-upload .input-upload:hover .upload-text {
  visibility: visible;
  transition: opacity 0.13s ease-out;
}
.upload-img2 {
  width: 100px;
  height: 100px;
  vertical-align: middle;
  position: absolute;
  top: 0;
  left: 0;
  overflow: hidden;
  object-fit: scale-down;
}
.customlogo {
  background: white !important;
  border-radius: 0% !important;
}
.payment-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e0e0e0;
}
.payment-table thead {
  background: #f0f2f4;
}
.payment-table thead th,
.payment-table tbody td {
  padding: 18px 20px;
  white-space: nowrap;
}
.updatedialog-body {
  padding-top: 20px;
  padding-bottom: 20px;
}
.timezone-info-icon {
  color: #6b7e91;
}
</style>
