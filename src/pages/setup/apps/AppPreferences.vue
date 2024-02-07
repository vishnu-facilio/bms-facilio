<template>
  <div class="app-preference-list">
    <AppBanner v-if="showReloadApp">
      App has changed. Click here to
      <span @click="reload" class="fwBold pointer"> reload</span>
      .
    </AppBanner>

    <portal to="app-preference-update">
      <div v-if="saving" class="mR20" :style="msgColor">{{ msg }}</div>
    </portal>

    <template v-for="(option, configKey, index) in appDisplayOptions">
      <div
        v-if="option.appCategory.includes(app.appCategory)"
        :key="`${configKey}_${index}`"
        class="app-preference"
      >
        <div class="flex-center-row-space">
          <div>
            <div class="app-preference-name">
              {{ option.label }}
              <el-popover placement="right" trigger="hover">
                <i
                  v-if="!$validation.isEmpty(option.previewImg)"
                  slot="reference"
                  class="el-icon-info app-info-icon"
                ></i>
                <img
                  :src="option.previewImg"
                  style="max-width: 400px;max-height: 400px;object-fit: contain;"
                />
              </el-popover>
            </div>
            <div class="app-preference-description">
              {{ option.description }}
            </div>
          </div>

          <el-switch
            v-model="option.isEnabled"
            @change="toggleSwitch(option)"
            class="mR20"
          ></el-switch>
        </div>

        <component
          v-if="option.isEnabled && option.configOptions"
          :ref="configKey"
          :is="option.configOptions"
          :configJSON="option.config"
          @setOption="config => updateConfig(config, configKey)"
        ></component>
      </div>
    </template>
  </div>
</template>
<script>
import debounce from 'lodash/debounce'
import delay from 'lodash/delay'
import AppBanner from 'pages/AppBanner.vue'
import { API } from '@facilio/api'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'

const {
  appCategory: { PORTALS, FEATURE_GROUPING },
} = Constants

export default {
  props: ['app', 'currentAppId'],

  components: { AppBanner },

  data() {
    return {
      msg: '',
      msgColor: '',
      saving: false,
      showReloadApp: false,
      appDisplayOptions: {
        canShowProfile: {
          label: 'My Profile',
          description: 'Access and update profile information.',
          appCategory: [FEATURE_GROUPING, PORTALS],
        },
        canShowNotifications: {
          label: 'Notifications',
          description: 'Receive a new notification and access the entire list.',
          appCategory: [FEATURE_GROUPING, PORTALS],
        },
        canShowSitesSwitch: {
          label: 'Show sites',
          description: 'Show list of all your sites.',
          appCategory: [FEATURE_GROUPING],
        },
        canShowAppLauncher: {
          label: 'App Launcher',
          description: 'Display portals, work-centers, and featured-app list.',
          appCategory: [FEATURE_GROUPING],
          previewImg: require('assets/webtab-preference/app-launcher.png'),
        },
        canShowQuickCreate: {
          label: 'Quick Create',
          description: 'Expose quick creation for modules and service catalog.',
          appCategory: [FEATURE_GROUPING],
          previewImg: require('assets/webtab-preference/quick-create.png'),
        },
        canShowSubmitRequest: {
          label: 'Submit Request',
          description: 'Configure shortcut button.',
          appCategory: [PORTALS],
          configOptions: () => import('./preferences/SubmitRequest'),
        },
      },
    }
  },

  created() {
    let {
      app: { configJSON },
    } = this
    this.config = configJSON || {}
  },

  computed: {
    config: {
      get() {
        return Object.entries(this.appDisplayOptions).reduce(
          (configJSON, [key, value]) => {
            let { isEnabled, config } = value
            if (isEnabled) {
              configJSON[key] = !isEmpty(config) ? config : true
            }
            return configJSON
          },
          {}
        )
      },
      set(configJSON) {
        Object.entries(configJSON).forEach(([key, value]) => {
          if (value !== true) {
            this.$set(this.appDisplayOptions[key], 'config', value)
          }
          this.$set(this.appDisplayOptions[key], 'isEnabled', true)
        })
      },
    },
  },

  methods: {
    validate() {
      return Object.entries(this.appDisplayOptions).every(([key, value]) => {
        let { configOptions, isEnabled } = value
        if (isEnabled && !isEmpty(configOptions)) {
          return this.$refs[key][0].validate() //call validate method of configOption components
        }
        return true
      })
    },
    async saveApp() {
      let valid = this.validate()
      if (!valid) return

      let {
        config,
        app: { id },
      } = this
      let params = {
        application: { id, config: JSON.stringify(config) },
      }

      this.saving = true
      this.msgColor = 'color: #39b2c2;'
      this.msg = 'Saving...'

      let { error } = await API.post('/v2/application/addOrUpdate', params)

      if (error) {
        this.msgColor = 'color: #e36666;'
        this.msg = 'Error Occurred'
      } else {
        this.msgColor = 'color: #67c23a;'
        this.msg = 'Saved'
        this.$emit('onSave', config)
        this.toggleBanner()
      }
      delay(() => {
        this.saving = false
        this.msgColor = ''
        this.msg = ''
      }, 1000)
    },
    autoSave: debounce(function() {
      this.saveApp()
    }, 2 * 1000),
    toggleSwitch(option) {
      let { configOptions, isEnabled } = option

      if (!configOptions || !isEnabled) {
        this.autoSave()
      }
    },
    updateConfig(config, configKey) {
      this.appDisplayOptions[configKey] = {
        ...this.appDisplayOptions[configKey],
        config,
      }
      this.saveApp()
    },
    toggleBanner() {
      let {
        currentAppId,
        app: { id },
      } = this

      if (id === currentAppId) {
        this.$emit('reloadApp')
        this.showReloadApp = true
      }
    },
    reload() {
      let { path } = this.$route
      window.location.href = path
    },
  },
}
</script>
<style lang="scss">
.app-preference-list {
  height: calc(100vh - 210px);
  margin: 20px 0;
  overflow: scroll;

  .app-preference {
    padding: 20px;
    margin-bottom: 10px;
    background-color: #fff;
    display: flex;
    flex-direction: column;

    .app-preference-name {
      font-size: 15px;
      font-weight: 500;
      letter-spacing: 0.4px;
      color: #324056;
      line-height: 20px;
      text-transform: capitalize;
    }
    .app-preference-description {
      margin-top: 5px;
      font-size: 13px;
      line-height: 1.31;
      color: #45566f;
      letter-spacing: 0.5px;
    }
    .app-info-icon {
      font-size: 14px;
      opacity: 0.3;
    }
    .config-options {
      margin: 20px;
      display: flex;
      flex-direction: column;

      .save-options {
        height: 30px;
        border: none;
        text-transform: uppercase;
        font-size: 12px;
        letter-spacing: 0.7px;
        font-weight: bold;
        border-radius: 3px;
        padding: 0 25px;
        background-color: #39b2c2;

        &:hover {
          background: #33a6b5;
          border: #33a6b5;
          color: #fff;
          box-shadow: 0 7px 12px 3px rgb(210 211 211 / 50%);
        }
      }
      .error-block {
        padding: 0px;
        height: auto;

        .error-close-icon {
          top: 4px;
        }
      }
    }
  }
}
</style>
