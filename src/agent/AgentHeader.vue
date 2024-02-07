<template>
  <div>
    <el-header class="fc-agent-header">
      <div class>
        <slot name="logo">
          <img class="fc-rebrand-logo" :src="brandConfig.logoLight" />
        </slot>
      </div>
      <div>
        <div @click="drawer = true">
          <avatar
            size="md"
            :user="$account.user"
            :style="{
              border: '1px solid #666580',
              'border-radius': '16px',
            }"
            color="#473a9e"
            class="pointer"
          ></avatar>
        </div>
      </div>
    </el-header>

    <el-drawer
      :visible.sync="drawer"
      :direction="direction"
      :append-to-body="true"
      width="30%"
      class="fc-drawer-hide-header"
    >
      <div class="agent-profile-panel-header">
        <avatar
          size="xxxlg"
          :user="$account.user"
          color="#473a9e"
          class="pointer"
        ></avatar>
        <div class="fc-white-15 mT15">{{ $account.user.name }}</div>
        <div class="fc-white-13 mT10">{{ $account.user.email }}</div>
        <div class="agent-profile-extra-link">
          <el-button round class="fc-home-profile-btn" @click="myprofile()">{{
            $t('common.profile.myprofile')
          }}</el-button>
          <div direction="vertical" class="profile-container-divider"></div>
          <el-button
            round
            class="fc-home-profile-btn"
            @click="changePassword()"
            >{{ $t('common.profile.changepassword') }}</el-button
          >
        </div>
      </div>
      <div class="agent-profile-body">
        <div class="">
          <div class="fc-profile-external-links p20">
            <div class="flex-middle pB20 pointer">
              <router-link
                v-if="canShowSwitchproduct"
                :to="{ path: '/app' }"
                target="_blank"
                class="flex-middle"
              >
                <div class="flex-middle">
                  <img
                    src="~assets/svgs/profile/switch.svg"
                    width="20"
                    height="20"
                  />
                </div>
                <div class="label-txt-black mL15">
                  {{ $t('agent.agent.switch_product') }}
                </div>
              </router-link>
            </div>
            <div class="flex-middle pB20">
              <a
                href="https://guide.facilio.com/"
                target="_blank"
                class="label-txt-black pointer flex-middle"
              >
                <div class="">
                  <inline-svg
                    src="svgs/profile/help-icon"
                    iconClass="text-center icon-sm-md"
                    class="vertical-middle"
                  ></inline-svg>
                </div>
                <div class="label-txt-black mL15">
                  {{ $t('agent.agent.help') }}
                </div>
              </a>
            </div>
            <div class="flex-middle pB20">
              <a
                href="https://facilio.com/about"
                target="_blank"
                class="label-txt-black pointer flex-middle"
              >
                <div class="">
                  <inline-svg
                    src="svgs/profile/about-icon"
                    iconClass="text-center icon-sm-md"
                    class="vertical-middle"
                  ></inline-svg>
                </div>
                <div class="label-txt-black mL15">
                  {{ $t('agent.agent.about') }}
                </div>
              </a>
            </div>
            <div class="flex-middle pB20">
              <a
                href="https://blog.facilio.com/"
                target="_blank"
                class="label-txt-black pointer flex-middle"
              >
                <div class="">
                  <inline-svg
                    src="svgs/profile/blog-icon"
                    iconClass="text-center icon-sm-md"
                    class="vertical-middle"
                  ></inline-svg>
                </div>
                <div class="label-txt-black mL15">
                  {{ $t('agent.agent.blog') }}
                </div>
              </a>
            </div>
            <div class="flex-middle pB20 pointer" @click="logout()">
              <div class="" @click="logout()">
                <inline-svg
                  src="svgs/profile/logout-icon"
                  iconClass="text-center icon-sm-md"
                  class="vertical-middle"
                ></inline-svg>
              </div>
              <div class="label-txt-red mL15" @click="logout()">
                {{ $t('agent.agent.logout') }}
              </div>
            </div>
          </div>
        </div>
        <div class="fc-profile-footer">
          <div class="flex-middle">
            <div class="fc-profile-txt">
              {{ $t('agent.agent.download') }}
            </div>
            <div class="mL20">
              <a
                href="https://apps.apple.com/us/developer/facilio-inc/id1438082478"
                target="_blank"
                class="mR20"
              >
                <inline-svg
                  src="svgs/ios-logo"
                  iconClass="text-center icon-sm-md"
                ></inline-svg>
              </a>
              <a
                href="https://play.google.com/store/apps/developer?id=Facilio+Inc"
                target="_blank"
              >
                <img
                  src="~assets/svgs/playstore-logo.svg"
                  width="20"
                  height="20"
                />
              </a>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      proilePanelShow: false,
      drawer: false,
      direction: 'rtl',
    }
  },
  components: {
    Avatar,
  },
  computed: {
    canShowSwitchproduct() {
      const isDevMode = process.env.NODE_ENV === 'development'
      const isAgentApp = ['iot'].includes(getApp().linkName)

      // ADD option to toggle this from app config
      return isDevMode || isAgentApp
    },
    brandConfig()
    {
      return window.brandConfig
    },
  },
  methods: {
    logout() {
      API.get('logout').then(({ error }) => {
        if (!error) {
          this.$store.commit('LOGIN_REQUIRED', true)
          this.$helpers.logout(self.$route.query.redirect)
        }
      })
    },
    myprofile() {
      this.$router.push({
        path: '/app/personalsettings/editprofile',
      })
    },
    changePassword() {
      this.$router.push({
        path: '/app/personalsettings/userchangepassword',
      })
    },
  },
}
</script>
<style lang="scss">
.agent-profile-panel-header {
  width: 100%;
  box-sizing: border-box;
  height: 245px;
  background-color: #483a9e;
  position: relative;
  padding: 20px;
  .fc-avatar-inline {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .fc-avatar-xxxlg {
    border: 1px solid #fff;
  }
  .agent-profile-extra-link {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 23px;
  }
  .fc-home-profile-btn {
    background-color: #483a9e;
    font-size: 13px;
    font-weight: normal;
    letter-spacing: 0.5px;
    color: #ffffff;
    font-weight: 500;
    border: 1px solid #483a9e;
    &:hover {
      background-color: #3a2c8f;
      color: #ffffff;
      border: 1px solid #3a2c8f;
    }
  }
}

.agent-profile-body {
  height: calc(100vh - 240px);
  position: relative;
  .fc-profile-footer {
    position: absolute;
    left: 0;
    bottom: 0;
    right: 0;
    border-top: 1px solid #eaecf1;
    box-shadow: 0 20px 22px 0 rgba(234, 234, 234, 0.6);
    padding: 20px;
    z-index: 1;
    background-color: #f7f8f9;
  }
}
</style>
