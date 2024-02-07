<template>
  <div>
    <el-dialog
      :title="$t('common._common.tab_types')"
      :visible.sync="canShowTabTypeForm"
      width="30%"
      class="fc-dialog-center-container"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <div class="height200">
        <el-form ref="tabTypeForm" :rules="rules" :model="webtabObj">
          <el-form-item
            :label="$t('common._common.tabtype')"
            prop="type"
            :required="true"
          >
            <el-select
              v-model="webtabObj.type"
              :placeholder="$t('common.products.select_tabtype')"
              filterable
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(tabType, tabTypeId) in tabTypesDisplayName"
                :key="tabTypeId"
                :label="tabType"
                :value="parseInt(tabTypeId)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}
          </el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="openTabForm()"
          >
            {{ $t('panel.dashboard.confirm') }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="canShowTabForm"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog new-web-tab"
      style="z-index: 999999"
      :before-close="closeDialog"
    >
      <el-form
        :model="webtabObj"
        :rules="rules"
        :label-position="'top'"
        ref="webtab"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text flex-center-vH">
            <div class="fc-setup-modal-title">
              {{
                isNew
                  ? $t('common._common.add_tab')
                  : $t('common.products.edit_tab')
              }}
            </div>
            <div class="mT10 f13">
              {{ `Type : ${tabTypesDisplayName[webtabObj.type]}` }}
            </div>
          </div>
        </div>

        <div class="new-body-modal enpi-body-modal">
          <div class="body-scroll">
            <template v-if="loading">
              <template v-for="index in [1, 2]">
                <el-row class="mB10" :key="index">
                  <el-col :span="24">
                    <span class="lines loading-shimmer width50 mB10"></span>

                    <span
                      class="lines loading-shimmer width100 mB10 height40"
                    ></span>
                  </el-col>
                </el-row>
              </template>

              <el-row class="mB10">
                <el-col :span="24">
                  <span class="lines loading-shimmer width50 mB10"></span>
                </el-col>
                <el-col :span="24">
                  <span
                    class="lines loading-shimmer width50 mB10 height40"
                  ></span>
                </el-col>
              </el-row>
            </template>

            <template v-else>
              <el-row class="mB10">
                <el-col :span="24">
                  <el-form-item prop="name">
                    <p class="fc-input-label-txt">
                      {{ $t('common.products.name') }}
                    </p>

                    <el-input
                      class="width100 fc-input-full-border2"
                      autofocus
                      v-model="webtabObj.name"
                      type="text"
                      :placeholder="$t('common._common.enter_tab_name')"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row v-if="!isNew" class="mB10">
                <el-col :span="24">
                  <el-form-item prop="route">
                    <p class="fc-input-label-txt">
                      {{ $t('common.wo_report.route') }}
                    </p>

                    <el-input
                      class="width100 fc-input-full-border2"
                      autofocus
                      v-model="webtabObj.route"
                      type="text"
                      :placeholder="$t('common._common.enter_tab_route')"
                    >
                    </el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <template
                v-if="
                  tabTypes.DASHBOARD === webtabObj.type ||
                    tabTypes.NEW_DASHBOARD === webtabObj.type
                "
              >
                <el-row :gutter="20" class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="dashboardType">
                      <p class="fc-input-label-txt">
                        {{ $t('common.dashboard.show') }}
                      </p>
                      <el-radio
                        v-model="webtabObj.dashboardType"
                        label="all"
                        class="fc-radio-btn"
                      >
                        {{ $t('common.header.all_dashboards') }}
                      </el-radio>
                      <el-radio
                        v-model="webtabObj.dashboardType"
                        label="specific"
                        class="fc-radio-btn"
                      >
                        {{ $t('common._common.selected_dashboard') }}
                      </el-radio>
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row
                  :gutter="20"
                  v-if="webtabObj.dashboardType === 'specific'"
                  class="mB10"
                >
                  <p class="fc-input-label-txt mL10">
                    {{ $t('common.header.dashboards') }}
                  </p>
                  <el-col :span="24">
                    <el-form-item prop="config.dashboardLink">
                      <el-select
                        v-model="webtabObj.config.dashboardLink"
                        filterable
                        class="fc-input-full-border2 width100"
                      >
                        <el-option
                          v-for="dashboard in dashboardList"
                          :key="`${dashboard.id}-${dashboard.linkName}`"
                          :label="dashboard.dashboardName"
                          :value="dashboard.linkName"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>

              <template v-if="tabTypes.APPS === webtabObj.type">
                <el-row :gutter="20" class="mB10">
                  <p class="fc-input-label-txt mL10">
                    {{ $t('common.products.connectedapps') }}
                  </p>
                  <el-col :span="12">
                    <el-form-item prop="connectedApp">
                      <el-select
                        v-model="webtabObj.connectedAppId"
                        filterable
                        @change="webtabObj.config.widgetId = null"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option
                          v-for="app in connectedAppsList"
                          :key="app.id"
                          :label="app.name"
                          :value="app.id"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>

                  <el-col :span="12">
                    <el-form-item prop="config.widgetId">
                      <el-select
                        v-model="webtabObj.config.widgetId"
                        :disabled="!webtabObj.connectedAppId"
                        class="fc-input-full-border2 width100"
                      >
                        <el-option
                          v-for="widget in widgetList"
                          :key="widget.id"
                          :label="widget.widgetName"
                          :value="widget.id"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>

              <template v-if="tabTypes.REPORT === webtabObj.type">
                <el-row :gutter="20" class="mB10">
                  <el-col :span="24">
                    <el-form-item prop="reportType">
                      <p class="fc-input-label-txt">
                        {{ $t('common.wo_report.report_type') }}
                      </p>
                      <el-radio
                        v-model="webtabObj.reportType"
                        label="reading"
                        class="fc-radio-btn"
                      >
                        {{ $t('common.header.reading_report') }}
                      </el-radio>
                      <el-radio
                        v-model="webtabObj.reportType"
                        label="module"
                        class="fc-radio-btn"
                      >
                        {{ $t('common._common.module_report') }}
                      </el-radio>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
              <!-- <template v-if="tabTypes.PIVOTS === webtabObj.type">
                <el-row :gutter="20" class="mB10">
                  <el-col :span="24">
                  </el-col>
                </el-row>
              </template> -->

              <template
                v-if="
                  ![
                    tabTypes.PIVOT,
                    tabTypes.ANALYTICS,
                    tabTypes.APPS,
                    tabTypes.DASHBOARD,
                    tabTypes.NEW_DASHBOARD,
                    tabTypes.PORTAL_OVERVIEW,
                    tabTypes.INDOOR_FLOORPLAN,
                    tabTypes.KPI,
                    tabTypes.NEW_KPI,
                    tabTypes.HOMEPAGE,
                    tabTypes.SERVICE_CATALOG,
                    tabTypes.SHIFT_PLANNER,
                    tabTypes.MY_ATTENDANCE,
                    tabTypes.ATTENDANCE,
                    tabTypes.RULES,
                  ].includes(webtabObj.type)
                "
              >
                <el-row
                  :gutter="20"
                  class="mB10"
                  v-if="webtabObj.reportType !== 'reading'"
                >
                  <el-col :span="24">
                    <el-form-item prop="moduleNames">
                      <p class="fc-input-label-txt">
                        {{
                          [tabTypes.MODULE, tabTypes.TIMELINE].includes(
                            webtabObj.type
                          )
                            ? 'Module'
                            : 'Modules'
                        }}
                      </p>
                      <el-select
                        v-model="webtabObj.moduleNames"
                        :multiple="
                          ![tabTypes.MODULE, tabTypes.TIMELINE].includes(
                            webtabObj.type
                          )
                        "
                        collapse-tags
                        :placeholder="
                          $t('common.header.please_select_the_module')
                        "
                        class="fc-input-full-border2 width100"
                        filterable
                      >
                        <el-option-group
                          v-for="(moduleObj, moduleName) in modulesList"
                          :key="moduleName"
                          :label="moduleName.toUpperCase()"
                        >
                          <el-option
                            v-for="list in moduleObj"
                            :key="list.moduleId"
                            :label="list.displayName"
                            :value="list.name"
                          >
                          </el-option>
                        </el-option-group>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>

              <template v-if="canShowConfigJson">
                <el-row :gutter="20" class="mB10">
                  <el-col :span="24">
                    <p class="fc-input-label-txt">
                      {{ $t('common.wo_report.configuration') }}
                    </p>
                    <el-form-item prop="configJson">
                      <div
                        v-for="(config, index) in webtabObj.configJson"
                        :key="index"
                        class="d-flex mB10"
                      >
                        <el-input
                          class="width50 fc-input-full-border2"
                          autofocus
                          v-model="config.key"
                          type="text"
                          autocomplete="off"
                          :placeholder="$t('common.header.key')"
                        />
                        <el-input
                          class="width50 fc-input-full-border2 mL10 mR10"
                          autofocus
                          v-model="config.value"
                          type="text"
                          autocomplete="off"
                          :placeholder="$t('common.header.value')"
                        />
                        <div class="justify-between width50px flex-middle">
                          <img
                            src="~assets/add-icon.svg"
                            v-if="showAddConfig && index === lastConfigIndex"
                            style="height: 18px; width: 18px"
                            class="delete-icon"
                            @click="addConfig"
                          />
                          <img
                            src="~assets/remove-icon.svg"
                            v-if="webtabObj.configJson.length !== 1"
                            style="height: 18px; width: 18px; margin-right: 3px"
                            class="delete-icon"
                            @click="deleteConfig(index)"
                          />
                        </div>
                      </div>
                    </el-form-item>
                  </el-col>
                </el-row>
              </template>
            </template>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">
            {{ $t('setup.users_management.cancel') }}
          </el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="submitForm()"
            :loading="saving"
          >
            {{
              saving ? $t('common._common._saving') : $t('panel.dashboard.save')
            }}
          </el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'

export default {
  props: [
    'isNew',
    'tabTypes',
    'tabTypesDisplayName',
    'activeWebtab',
    'applicationId',
  ],
  data() {
    return {
      webtabObj: {
        type: null,
        name: null,
        route: null,
        config: {},
        configJson: [],
      },
      loading: false,
      modulesList: [],
      dashboardList: [],
      connectedAppsList: [],
      widgetListloading: false,
      canShowTabTypeForm: false,
      canShowTabForm: false,
      saving: false,
      rules: {
        type: {
          required: true,
          message: this.$t('common.header.please_select_tab_type'),
          trigger: 'blur',
        },
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        moduleNames: {
          validator: function(rule, value, callback) {
            let { webtabObj, tabTypes } = this
            let { type } = webtabObj || {}

            if (
              ![
                tabTypes.ANALYTICS,
                tabTypes.APPS,
                tabTypes.CUSTOM,
                tabTypes.PORTAL_OVERVIEW,
                tabTypes.INDOOR_FLOORPLAN,
                tabTypes.HOMEPAGE,
                tabTypes.SERVICE_CATALOG,
              ].includes(type) &&
              isEmpty(value)
            ) {
              callback(
                new Error(this.$t('common.header.please_select_modules'))
              )
            } else callback()
          }.bind(this),
          trigger: 'blur',
        },
        'config.widgetId': {
          validator: function(rule, value, callback) {
            let { webtabObj, tabTypes } = this
            let { config, type } = webtabObj || {}

            if (type === tabTypes.APPS && isEmpty(config.widgetId)) {
              callback(
                new Error(
                  this.$t('common.header.please_select_connected_app_widget')
                )
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
        },
        'config.dashboardLink': {
          validator: function(rule, value, callback) {
            let { webtabObj, tabTypes } = this
            let { config, type, dashboardType } = webtabObj || {}
            let canThrowError =
              (type === tabTypes.DASHBOARD ||
                type === tabTypes.NEW_DASHBOARD) &&
              dashboardType === 'specific' &&
              isEmpty(config.dashboardLink)

            if (canThrowError) {
              callback(
                new Error(
                  this.$t('common.header.please_select_specific_dashboard')
                )
              )
            } else callback()
          }.bind(this),
          trigger: 'blur',
        },
        configJson: {
          validator: function(rule, value, callback) {
            let { webtabObj, tabTypes } = this
            let { type } = webtabObj || {}
            let config = value.some(
              config => isEmpty(config.value) || isEmpty(config.key)
            )

            if (
              [tabTypes.CUSTOM, tabTypes.PORTAL_OVERVIEW].includes(type) &&
              config
            ) {
              callback(
                new Error(
                  this.$t('common.header.please_enter_special_criteria')
                )
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    this.init()
  },

  computed: {
    widgetList() {
      let { connectedAppsList, webtabObj } = this
      let { connectedAppId } = webtabObj || {}
      let connectedApp =
        connectedAppsList.find(app => app.id === connectedAppId) || {}

      return connectedApp.widgetList || []
    },
    lastConfigIndex() {
      let { webtabObj } = this
      let { configJson } = webtabObj || {}
      let lastIndex = configJson.length - 1
      return lastIndex
    },
    showAddConfig() {
      let { webtabObj, lastConfigIndex } = this
      let { configJson } = webtabObj || {}
      return !isEmpty((configJson[lastConfigIndex] || {}).key)
    },
    canShowConfigJson() {
      let {
        tabTypes,
        webtabObj: { type },
        $route: { query },
      } = this

      let isCustomTab = [tabTypes.CUSTOM, tabTypes.PORTAL_OVERVIEW].includes(
        type
      )
      let canShowConfigForAll = query.showConfig === 'true'

      return isCustomTab || canShowConfigForAll
    },
  },

  watch: {
    'webtabObj.dashboardType'(value) {
      if (value === 'all') {
        this.webtabObj.config = {}
      }
    },
  },

  methods: {
    async init() {
      let { tabTypes, activeWebtab } = this
      let { configJSON, moduleIds, type, modules, specialTypeModules } =
        activeWebtab || {}

      this.loading = true
      if (this.isNew) {
        this.canShowTabTypeForm = true
      } else {
        this.$set(this.webtabObj, 'type', type)
        this.canShowTabForm = true

        let { widgetId, type: configType, dashboardLink } = configJSON || {}
        let reportType = ''
        let config
        let connectedAppId
        let configJson = []
        let dashboardType = ''
        let moduleNames = null

        if (tabTypes.REPORT === type) {
          if (configType === 'analytics_reports') {
            reportType = 'reading'
          } else if (configType === 'module_reports') {
            reportType = 'module'
          }
        } else if ([tabTypes.MODULE, tabTypes.TIMELINE].includes(type)) {
          moduleNames = modules[0].name
        } else if (tabTypes.APPS === type) {
          await this.getConnectedAppsList()
          connectedAppId = this.getConnectedAppFromWidgetId(widgetId)
          config = { widgetId }
        } else if (tabTypes.DASHBOARD === type || tabTypes.NEW_DASHBOARD) {
          dashboardType = !isEmpty(dashboardLink) ? 'specific' : 'all'
          config = { dashboardLink }
          await this.loadDashboard()
        }

        if (isEmpty(config)) {
          if (configJSON) {
            Object.entries(configJSON).forEach(([key, value]) => {
              configJson.push({ key, value })
            })
          } else {
            configJson.push({ key: 'type', value: null })
          }
        }
        if (![tabTypes.MODULE, tabTypes.TIMELINE].includes(type)) {
          if (!isEmpty(moduleIds) || !isEmpty(specialTypeModules)) {
            moduleNames = modules.map(mod => mod.name)
          }
        }

        this.webtabObj = {
          ...this.activeWebtab,
          reportType,
          moduleNames,
          config,
          configJson,
          connectedAppId,
          dashboardType,
        }
      }
      await this.getModulesList()
      this.loading = false
    },
    async getModulesList() {
      let { error, data } = await API.get('/v3/modules/list/all')

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let modulesList = Object.assign(data, {
          'System Modules': data['systemModules'],
          'Custom Modules': data['customModules'],
        })
        delete modulesList['systemModules']
        delete modulesList['customModules']

        this.modulesList = modulesList || {}
      }
    },
    async getConnectedAppsList() {
      let { error, data } = await API.get('/v2/connectedApps/all')

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.connectedAppsList = data.connectedApps.map(app => {
          let { connectedAppWidgetsList, id, name } = app || {}
          let webTabWidget = { WEB_TAB: 1 }
          let widgetList = (connectedAppWidgetsList || [])
            .filter(widget => widget.entityType === webTabWidget.WEB_TAB)
            .map(widget => {
              let { id, widgetName } = widget
              return { id, widgetName }
            })
          return { widgetList, id, name }
        })
      }
    },
    getConnectedAppFromWidgetId(widgetId) {
      let connectedApp = this.connectedAppsList.find(app => {
        let { widgetList } = app
        return widgetList.find(widget => widget.id === widgetId)
      })

      return (connectedApp || {}).id
    },
    async loadDashboard() {
      let { data, error } = await API.get('/dashboardWithFolder')

      if (!error) {
        let { dashboardFolders } = data || {}

        this.dashboardList = (dashboardFolders || []).reduce(
          (dashboards, folder) => {
            if (isArray(folder.dashboards) && !isEmpty(folder.dashboards)) {
              dashboards = [...dashboards, ...folder.dashboards]
            }
            return dashboards
          },
          []
        )
      }
    },
    openTabForm() {
      this.$refs['tabTypeForm'].validate(async valid => {
        if (!valid) return false

        let { tabTypes, webtabObj } = this
        let { type } = webtabObj

        if (tabTypes.REPORT === type) {
          this.$set(this.webtabObj, 'reportType', 'reading')
        } else if ([tabTypes.CUSTOM, tabTypes.PORTAL_OVERVIEW].includes(type)) {
          this.webtabObj.configJson.push({ key: 'type', value: null })
        } else if (tabTypes.APPS === type) {
          this.webtabObj.config = { widgetId: null }
          await this.getConnectedAppsList()
        } else if (
          tabTypes.DASHBOARD === type ||
          tabTypes.NEW_DASHBOARD === type
        ) {
          this.$set(this.webtabObj, 'dashboardType', 'all')
          await this.loadDashboard()
        }
        this.canShowTabTypeForm = false
        this.canShowTabForm = true
      })
    },
    addConfig() {
      let config = { key: null, value: null }
      this.webtabObj.configJson.push(config)
    },
    deleteConfig(index) {
      this.webtabObj.configJson.splice(index, 1)
    },
    submitForm() {
      this.$refs['webtab'].validate(valid => {
        if (!valid) return false

        let {
          applicationId,
          webtabObj,
          isNew,
          tabTypes,
          canShowConfigJson,
          modulesList,
        } = this
        let {
          name,
          route,
          type,
          moduleNames,
          config,
          reportType,
          configJson,
          dashboardType,
          id,
        } = webtabObj
        let configJSON = null

        if (isNew) {
          route = name.replace(/\W+/g, '-').toLowerCase()
        }
        if (tabTypes.APPS === type) {
          configJSON = config
        } else if (
          tabTypes.DASHBOARD === type ||
          tabTypes.NEW_DASHBOARD === type
        ) {
          configJSON = dashboardType === 'all' ? null : config
        } else if (tabTypes.REPORT === type) {
          if (reportType === 'reading') {
            configJSON = { type: 'analytics_reports' }
          } else {
            configJSON = { type: 'module_reports' }
          }
        } else if (tabTypes.ANALYTICS === type) {
          configJSON = { type: 'analytics_building' }
        } else if (canShowConfigJson) {
          configJSON = configJson.reduce((configValue, config) => {
            let { key, value } = config
            configValue[key] = value
            return configValue
          }, {})
        }

        if (!isArray(moduleNames)) {
          moduleNames = !isEmpty(moduleNames) ? [moduleNames] : []
        }
        let modules = [
          ...modulesList['System Modules'],
          ...modulesList['Custom Modules'],
        ]
        let selectedModules = modules.filter(m => moduleNames.includes(m.name))
        let moduleIds = selectedModules
          .filter(m => !isEmpty(m.moduleId))
          .map(m => m.moduleId)
        let specialTypeModules = selectedModules
          .filter(m => isEmpty(m.moduleId))
          .map(m => m.name)

        let params = {
          tab: {
            name,
            route,
            applicationId,
            type,
            moduleIds,
            specialTypeModules,
            id,
          },
        }

        if (!isEmpty(configJSON)) {
          params.tab.config = JSON.stringify(configJSON)
        }

        this.saving = true
        API.post('/v2/tab/addOrUpdate', params).then(({ error, data }) => {
          if (error) {
            this.saving = false
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success(
              this.$t('common._common.tabs_added_or_updated')
            )
            this.$emit('onSave', data.webTab)
            this.$emit('reload')
            this.saving = false
            this.closeDialog()
          }
        })
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.new-web-tab {
  .lines {
    height: 15px;
    border-radius: 5px;
  }
  .height40 {
    height: 40px !important;
  }
}
</style>
