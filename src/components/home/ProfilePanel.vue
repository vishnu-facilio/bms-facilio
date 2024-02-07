<template>
  <outside-click :visibility.sync="visibilityProxy">
    <div class="height100vh z-index1234">
      <div class="fc-home-profile-container fc-animated slideInRight">
        <div class="fc-home-profile-header">
          <div
            class="position-absolute close-icon-header pointer"
            @click="closePanel()"
          >
            <i class="el-icon-close op5 white-color f30"></i>
          </div>
          <div class="fc-home-profile-header-avatar text-center">
            <avatar size="xxxlg" :user="account.user" color="#473a9e"></avatar>
            <div class="fc-white-15 mT15">{{ account.user.name }}</div>
            <div class="fc-white-13 mT10">{{ account.user.email }}</div>
            <div
              v-if="canShowProfile"
              class="flex-middle mT30 justify-content-center pL20"
            >
              <el-button
                round
                class="fc-home-profile-btn"
                @click="myprofile(), closePanel()"
                >{{ $t('common.profile.myprofile') }}</el-button
              >
              <div direction="vertical" class="profile-container-divider"></div>
              <el-button
                round
                class="fc-home-profile-btn"
                @click="changePassword(), closePanel()"
                >{{ $t('common.profile.changepassword') }}</el-button
              >
            </div>
          </div>
        </div>
        <div class="fc-home-profile-header-body">
          <div class="fc-profile-org-scroll p20">
            <div v-if="orgs">
              <div class="fc-blue-label">{{ $t('panel.panel.profile') }}</div>
              <div>
                <el-row
                  class="pT20"
                  v-for="(org, index) in orgs"
                  :key="org.orgId + index"
                  v-on:mouseover="hoverOrg = true"
                  v-on:mouseleave="hoverOrg = false"
                >
                  <template v-if="$account.user.email === 'roger@facilio.com'">
                    <el-col :span="16">
                      <div
                        class="fc-violet-14 org-name pointer"
                        @click="switchAccount(org)"
                      >
                        {{ org.name }}
                      </div>
                    </el-col>
                  </template>
                  <template v-else>
                    <el-col :span="16">
                      <div
                        class="fc-violet-14 org-name pointer"
                        v-bind:class="{ active: $account.org.id === org.id }"
                        @click="switchAccount(org)"
                      >
                        {{ org.name }}
                      </div>
                    </el-col>
                    <el-col
                      :span="8"
                      class="text-right pointer"
                      v-if="$account.org.id === org.id"
                      @click="switchAccount(org)"
                    >
                      <img
                        class="profile-c-icon"
                        src="~assets/svgs/profile/active-inactive.svg"
                      />
                    </el-col>
                    <el-col
                      :span="8"
                      class="text-right pointer"
                      v-else
                      @click="switchAccount(org)"
                    >
                      <img
                        class="profile-c-icon"
                        src="~assets/svgs/profile/active-inactiv-gray.svg"
                      />
                    </el-col>
                  </template>
                </el-row>
              </div>
            </div>
          </div>
          <div class="fc-profile-external-links p20">
            <div v-if="!$helpers.isEtisalat()">
              <div
                class="flex-middle pB20 pointer"
                v-if="isReportIssueEnabled"
                @click="reportIssue(true), closePanel()"
              >
                <inline-svg
                  src="svgs/profile/report-issue-icon"
                  iconClass="text-center icon-sm-md"
                  class="vertical-middle"
                ></inline-svg>
                <div class="label-txt-black mL15">
                  {{ $t('panel.panel.report') }}
                </div>
              </div>
              <div class="row">
                <div v-if="!isCustomDomain" class="flex-middle col-6 pB20">
                  <a
                    href="https://facilio.com/help/"
                    target="_blank"
                    class="label-txt-black pointer flex-middle"
                  >
                    <inline-svg
                      src="svgs/profile/help-icon"
                      iconClass="text-center icon-sm-md"
                      class="vertical-middle"
                    ></inline-svg>
                    <div class="label-txt-black mL15">
                      {{ $t('panel.panel.help') }}
                    </div>
                  </a>
                </div>
                <div
                  v-if="!isCustomDomain"
                  class="flex-middle pointer col-6 pB20"
                >
                  <inline-svg
                    src="svgs/profile/shortcuts-icon"
                    iconClass="text-center icon-sm-md"
                    class="vertical-middle shortcut-icon"
                  ></inline-svg>
                  <div class="label-txt-black mL15" @click="canShowShortcut()">
                    {{ $t('panel.panel.shortcuts') }}
                  </div>
                </div>
              </div>
              <div class="row">
                <div v-if="!isCustomDomain" class="flex-middle col-6 pB20">
                  <a
                    href="https://facilio.com/about"
                    target="_blank"
                    class="label-txt-black pointer flex-middle"
                  >
                    <inline-svg
                      src="svgs/profile/about-icon"
                      iconClass="text-center icon-sm-md"
                      class="vertical-middle"
                    ></inline-svg>
                    <div class="label-txt-black mL15">
                      {{ $t('panel.panel.about') }}
                    </div>
                  </a>
                </div>
                <div v-if="!isCustomDomain" class="flex-middle col-6 pB20">
                  <a
                    href="https://blog.facilio.com/"
                    target="_blank"
                    class="label-txt-black pointer flex-middle"
                  >
                    <inline-svg
                      src="svgs/profile/blog-icon"
                      iconClass="text-center icon-sm-md"
                      class="vertical-middle"
                    ></inline-svg>
                    <div class="label-txt-black mL15">
                      {{ $t('panel.panel.blog') }}
                    </div>
                  </a>
                </div>
              </div>
            </div>
            <div class="flex-middle pB20 pointer" @click="logout()">
              <inline-svg
                src="svgs/profile/logout-icon"
                iconClass="text-center icon-sm-md"
                class="vertical-middle"
              ></inline-svg>
              <div class="label-txt-red mL15">
                {{ $t('panel.panel.logout') }}
              </div>
            </div>
          </div>
          <div class="fc-profile-footer">
            <div v-if="!isCustomDomain" class="flex-middle">
              <div class="fc-profile-txt">
                {{ $t('panel.panel.apps') }}
              </div>
              <div class="mL20">
                <a :href="iosApplicationLink" target="_blank" class="mR20">
                  <inline-svg
                    src="svgs/ios-logo"
                    iconClass="text-center icon-sm-md"
                  ></inline-svg>
                </a>
                <a :href="androidApplicationLink" target="_blank">
                  <inline-svg
                    src="svgs/playstore-logo"
                    iconClass="text-center icon-sm-md"
                  ></inline-svg>
                </a>
              </div>
            </div>
          </div>
          <div class="clearboth"></div>
        </div>
      </div>
    </div>
  </outside-click>
</template>
<script>
import Avatar from '@/Avatar'
import OutsideClick from '@/OutsideClick'
import RebrandMixin from 'util/rebrandMixin'
import { isWebTabsEnabled } from '@facilio/router'
import { mapGetters } from 'vuex'
import { eventBus } from '@/page/widget/utils/eventBus'
import { getAppDomain } from 'src/util/helpers'

export default {
  props: [
    'close',
    'reportIssue',
    'account',
    'isTvMode',
    'switchScreen',
    'switchAccount',
    'switchTheme',
    'orgs',
    'screenTheame',
    'logout',
  ],
  mixins: [RebrandMixin],
  components: { Avatar, OutsideClick },
  data() {
    return {
      hoverOrg: false,
    }
  },
  computed: {
    ...mapGetters('webtabs', ['isAppPrefEnabled']),

    isReportIssueEnabled() {
      return false
    },
    visibilityProxy: {
      get() {
        return true
      },
      set(value) {
        if (!value) this.close()
      },
    },
    appName() {
      return window.location.pathname.slice(1).split('/')[0]
    },
    canShowProfile() {
      if (isWebTabsEnabled()) {
        return this.isAppPrefEnabled('canShowProfile')
      }
      return true
    },
    iosApplicationLink() {
      return getAppDomain() + '/auth/apps/ios'
    },
    androidApplicationLink() {
      return getAppDomain() + '/auth/apps/android'
    },
  },
  methods: {
    closePanel() {
      this.$emit('close')
      this.close()
    },
    myprofile() {
      this.$router.push({
        name: 'editprofile',
        params: { app: this.appName },
      })
    },
    changePassword() {
      this.$router.push({
        name: 'userchangepassword',
        params: { app: this.appName },
      })
    },
    canShowShortcut() {
      eventBus.$emit('openShortcutPanel')
    },
  },
}
</script>

<style lang="scss">
.fc-profile-txt {
  font-size: 11px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 0.7px;
  color: #353f54;
}
.label-txt-red {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #dd6160;
}
.fc-profile-external-links {
  width: 100%;
  position: absolute;
  bottom: 40px;
  background: #fff;
}
</style>
<style scoped>
.shortcut-icon {
  fill: #353f54;
  opacity: 0.8;
}
</style>
