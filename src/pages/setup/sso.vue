<template>
  <div class="height100 d-flex flex-direction-column overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="fc-black2-18 text-left bold pB0">
          {{ $t('setup.setup.sso_short') }}
        </div>
        <div class="heading-description" v-if="showHeader">
          {{ $t('setup.setup.sso_sign_in') }}
        </div>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout overflow-y fc-sso-portal-con pL10 pR10">
        <el-card
          class="box-card width100"
          style="box-shadow: 0 4px 15px 0 rgba(14, 15, 43, 0.03);"
          shadow="never"
        >
          <div slot="header" class="clearfix" v-if="showContent">
            <div class="d-flex justify-content-space">
              <div v-if="showContent">
                <span class="f15 setting-form-title line-height20">
                  {{ $t('common._common.enable_sso') }}
                </span>
                <el-switch
                  v-model="isEnabled"
                  @change="changeSSO()"
                  class="Notification-toggle p20"
                  active-color="rgba(57, 178, 194, 0.8)"
                  inactive-color="#e5e5e5"
                ></el-switch>
                <el-dropdown
                  @command="handleCommand"
                  class="sso-more-options"
                  trigger="click"
                >
                  <span class="el-dropdown-link">
                    <i class="el-icon-more f18 pointer"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-edit mR3" command="edit">{{
                      $t('common._common.edit')
                    }}</el-dropdown-item>
                    <el-dropdown-item
                      icon="el-icon-download mR3"
                      command="download"
                      ><a
                        class="label-txt-black"
                        :href="`${ssoData.spMetadataURL}`"
                        >{{ $t('common._common.download_metadata') }}</a
                      ></el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-delete mR3"
                      command="delete"
                      >{{ $t('common._common.delete') }}</el-dropdown-item
                    >
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </div>
          <template v-if="ssoStatusLoading">
            <div
              class="mB20 flex-center-vH"
              style="height: calc(100vh - 245px);"
              v-loading="loading"
            ></div>
          </template>
          <template v-else-if="showContent && !ssoStatusLoading">
            <div class="mB20">
              <el-row v-if="ssoType === 'domainSSO'">
                <el-col :span="12">
                  <div class="grid-content">
                    <div>
                      <div class="text-fc-grey f13 line-height20">
                        {{ $t('common.header.create_user_if_not_present') }}
                      </div>
                      <el-switch
                        v-model="isCreateUserEnabled"
                        @change="updateCreateUserStatus()"
                        class="Notification-toggle p20"
                        active-color="rgba(57, 178, 194, 0.8)"
                        inactive-color="#e5e5e5"
                      ></el-switch>
                    </div>
                  </div>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content">
                    <div>
                      <div class="text-fc-grey f13 line-height20">
                        {{ $t('common.products.service_provider_sp') }}
                      </div>
                      <div class="label-txt-black bold line-height25">
                        {{ ssoData.spEntityId }}
                      </div>
                    </div>
                    <div class="mT25">
                      <div class="text-fc-grey f13 line-height20">
                        {{ $t('common.header.identity_provider_idp') }}
                      </div>
                      <div class="label-txt-black bold line-height25">
                        {{ ssoData.loginUrl }}
                      </div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content">
                    <div>
                      <div class="text-fc-grey f13 line-height20">
                        {{ $t('common.header.assertion_cnsumer_service') }}
                      </div>
                      <div class="label-txt-black bold line-height25">
                        {{ ssoData.spAcsURL }}
                      </div>
                    </div>
                    <div class="mT25">
                      <div class="text-fc-grey f13 line-height20">
                        {{ $t('common.header.identity_provider_idp_logout') }}
                      </div>
                      <div class="label-txt-black bold line-height25">
                        {{ ssoData.logoutUrl ? ssoData.logoutUrl : '--' }}
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
          <template v-else>
            <div class="saml-container">
              <inline-svg
                src="svgs/emptystate/readings-empty"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="m15 mT0 fc-black2-18 f14 line-height22">
                {{ $t('common.header.single_sso_feature') }}
              </div>
              <el-button
                class="el-button setup-el-btn uppercase el-button--default pL30 pR30 pT15 pB15"
                @click="setUpSSO()"
              >
                {{ $t('common.header.setup_now') }}
              </el-button>
              <div class="m15 label-txt-black f14 line-height22">
                <a download :href="downloadMetaUrl"
                  ><i class="el-icon-download mR3"></i>Download Metadata</a
                >
              </div>
            </div>
          </template>
        </el-card>
      </div>
    </div>
    <ssoForm
      v-if="showDialog"
      :entityId="ssoData.entityId"
      :loginUrl="ssoData.loginUrl"
      :logoutUrl="ssoData.logoutUrl"
      :certificate="ssoData.certificate"
      :visibility.sync="showDialog"
      :getSSOStatus="getSSOStatus"
      :updateUrl="updateUrl"
      :ssoType="ssoType"
      :showSSOLink="ssoData.showSSOLink"
    >
    </ssoForm>
  </div>
</template>
<script>
import ssoForm from 'src/pages/setup/ssoForm'
export default {
  props: ['showHeader'],
  data() {
    return {
      loading: false,
      disabled: false,
      showContent: false,
      ssoStatusLoading: false,
      isEnabled: false,
      isCreateUserEnabled: false,
      isNew: false,
      showDialog: false,
      ssoData: {
        spAcsURL: null,
        spEntityId: null,
        spMetadataURL: null,
        certificate: null,
        entityId: null,
        loginUrl: null,
        logoutUrl: null,
        id: null,
      },
      ssoType: '',
      updateUrl: '',
    }
  },
  computed: {},
  created() {
    this.getSSOStatus('oncreate')
    this.ssoType = this.getSSOType()
    this.updateUrl = this.getUpdateUrl()
    this.downloadMetaUrl = this.downloadMetaUrl()
  },
  mounted() {},
  components: {
    ssoForm,
  },
  methods: {
    setUpSSO() {
      this.ssoData.certificate = null
      this.ssoData.entityId = null
      this.ssoData.loginUrl = null
      this.ssoData.logoutUrl = null
      this.ssoData.showSSOLink = false
      this.showDialog = true
    },
    configureSSO() {
      this.getSSOStatus('onedit')
    },
  },
}
</script>
<style lang="scss">
.saml-container {
  max-width: 700px;
  margin: 0 auto;
  text-align: center;
  padding-bottom: 50px;
}
.sso-more-options {
  .el-icon-more {
    opacity: 0.6;
    &:hover {
      opacity: 1;
    }
  }
}
</style>
