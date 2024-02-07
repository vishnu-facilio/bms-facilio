<template>
  <div>
    <div
      class="full-layout-white fc-setup-page-content row portal-form-align mL10 mR10 fc-settings-container-height"
    >
      <div class="col-lg-12 col-md-12">
        <div class="col-12 solid">
          <div>
            <div
              class="col-lg-12 col-md-12 border-bottom2 pB30 visibility-visible-actions"
            >
              <div class="fc-text-pink text-uppercase">URL</div>
              <div class="flex-middle app-link-name">
                <span class="label-txt-black pR5">{{
                  $t('setup.setup_profile.portal_login')
                }}</span>
                URL :
                <a
                  v-bind:href="portalInfo.login_url"
                  target="_blank"
                  class="fc-app-link-color pL10 f14"
                >
                  {{ portalInfo.login_url }}
                </a>

                <div
                  @click="copyLinkName(portalInfo.login_url)"
                  class="pointer pL10 visibility-hide-actions"
                >
                  <inline-svg
                    src="svgs/link-copy"
                    iconClass="icon icon-sm-md vertical-bottom"
                  ></inline-svg>
                </div>
              </div>
            </div>
            <form id="service-form" class="service-form portal-container">
              <div class="row row-height pT10">
                <section class="col-8">
                  <div class="fc-text-pink text-uppercase">
                    {{ $t('setup.setup_profile.user_signup') }}
                  </div>
                  <div class="flex-middle">
                    <div class="">
                      <label class="portal-label">{{
                        $t('setup.setup_profile.allow_users_signup')
                      }}</label>
                    </div>
                    <div class="mL20">
                      <el-switch
                        v-model="portalInfo.signup_allowed"
                        class="Notification-toggle"
                        active-color="rgba(57, 178, 194, 0.8)"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </div>
                </section>
              </div>
              <div class="row row-height mT20" v-if="portalInfo.signup_allowed">
                <section class="col-md-8">
                  <div class="fc-text-pink text-uppercase">
                    {{ $t('setup.setup_profile.helpdesk_restrictions') }}
                  </div>

                  <div class="row row-height">
                    <div class="col-xs-6">
                      <label class="portal-label">{{
                        $t('setup.setup_profile.Who_can_login')
                      }}</label>
                    </div>
                    <div class="col-xs-6 d-flex">
                      <el-radio
                        v-model="portalInfo.is_anyDomain_allowed"
                        :label="true"
                        class="fc-radio-btn pL20"
                      >
                        {{ $t('setup.setup_profile.any_domain') }}
                      </el-radio>
                      <el-radio
                        v-model="portalInfo.is_anyDomain_allowed"
                        :label="false"
                        class="fc-radio-btn"
                      >
                        {{ $t('setup.setup_profile.whitelisted_domains') }}
                      </el-radio>
                    </div>
                    <template v-if="!portalInfo.is_anyDomain_allowed">
                      <div class="col-xs-6"></div>
                      <div class="col-xs-6 d-flex">
                        <div class="mT20">
                          <input
                            type="text"
                            :placeholder="
                              $t('setup.setup_profile.enter_customer_domain')
                            "
                            v-model="portalInfo.whiteListed_domains"
                            required
                            class="help-desk-mail width350px"
                          />
                        </div>
                      </div>
                    </template>
                  </div>
                </section>
              </div>
              <div class="row-height mT20 pB20">
                <section class="col-md-8">
                  <div class="fc-text-pink text-uppercase">
                    {{ $t('setup.setup_profile.user_permissions') }}
                  </div>
                  <div class="row">
                    <div class="col-4">
                      <label class="portal-label">{{
                        $t('setup.setup_profile.new_ticket_submit')
                      }}</label>
                    </div>
                    <div class="col-4 pL20">
                      <el-radio
                        v-model="portalInfo.is_public_create_allowed"
                        :label="true"
                        class="fc-radio-btn"
                      >
                        {{ $t('setup.setup_profile.every_one') }}
                      </el-radio>
                      <el-radio
                        v-model="portalInfo.is_public_create_allowed"
                        :label="false"
                        class="fc-radio-btn"
                      >
                        {{ $t('setup.setup_profile.logged_in_users') }}
                      </el-radio>
                    </div>
                  </div>
                  <div
                    class="row pT20 pB20"
                    v-if="portalInfo.is_public_create_allowed && false"
                  >
                    <div class="col-4 mT20">
                      <label class="portal-label">{{
                        $t('setup.setup_profile.enabale_captcha')
                      }}</label>
                    </div>
                    <div class="col-4 pL20 mT20">
                      <el-switch
                        v-model="portalInfo.captcha_enabled"
                        class="setup-formula-switch"
                        active-color="#39b2c2"
                        inactive-color="#b3c3d3"
                      />
                    </div>
                  </div>
                </section>
              </div>

              <div class="row row-height portal-custom-domain pT10">
                <div class="pT10 pB30">
                  <el-button
                    type="primary"
                    @click="update()"
                    class="setup-el-btn portal-btn pL35 pR35"
                    :loading="saving"
                    >{{
                      saving
                        ? this.$t('common._common._saving')
                        : this.$t('common._common.update')
                    }}
                  </el-button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
export default {
  data() {
    return {
      portalInfo: {
        portalId: -1,
        portalType: 0,
        customDomain: null,
        signup_allowed: false,
        gmailLogin_allowed: false,
        is_public_create_allowed: false,
        is_anyDomain_allowed: false,
        captcha_enabled: false,
        whiteListed_domains: '',
      },
      saving: false,
    }
  },
  created() {
    this.loadPortInfo()
  },
  title() {
    return 'Service Portal'
  },
  methods: {
    update() {
      let { portalInfo: portalInfoMap } = this

      this.saving = true
      API.post('/serviceportal/updateserviceportal', { portalInfoMap }).then(
        ({ error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success(
              this.$t('setup.setup_profile.portal_setting_success')
            )
          }
          this.saving = false
        }
      )
    },
    loadPortInfo() {
      API.get('/serviceportal/getportalinfo').then(({ data }) => {
        this.portalInfo = data.protalInfo
      })
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied - ' + copy,
        type: 'success',
      })
    },
  },
}
</script>
<style>
.portal-inline {
  display: inline-block;
}
.portal-custom-domain {
  margin-bottom: 30px;
}
.portal-btn {
  margin-bottom: 30px;
}
.help-desk-mail {
  width: 100%;
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  margin-left: 20px !important;
  border-radius: 3px;
}
.portal-label {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.portal-form-align {
  margin-left: 10px;
  margin-top: 10px;
  padding: 20px 30px;
}
.fc-settings-container-height {
  height: calc(100vh - 245px) !important;
  overflow-y: scroll;
}
</style>
