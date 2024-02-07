<template>
  <div class="layout-padding white-background">
    <div class="fc-form">
      <form v-on:submit.prevent="saveOrgSettings">
        <p class="ruletitle">
          {{ $t('setup.setup_profile.general_settings') }}
        </p>
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
        <div class="profile-container">
          <div class="profile-right">
            <p class="input-label-text">
              {{ $t('setup.setup.company_name') }}
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
                  :disabled="isCurrencyDisabled"
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
import CompanyProfile from 'src/pages/setup/CompanyProfile.vue'
import { mapState } from 'vuex'

export default {
  extends: CompanyProfile,
  computed: {
    ...mapState({
      account: state => state.account,
    }),
    isCurrencyDisabled() {
      let { currencyInfo } = this.account.data || {}
      return currencyInfo?.multiCurrencyEnabled
    },
  },
}
</script>
