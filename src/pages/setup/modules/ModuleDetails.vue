<template>
  <div v-if="isLoading">
    <spinner :show="isLoading"></spinner>
  </div>
  <div v-else class="d-flex height100 overflow-hidden">
    <div class="modules-left-pane">
      <ModuleSideBar
        :moduleData="moduleData"
        :fieldCount="fieldCount"
      ></ModuleSideBar>
    </div>
    <div class="modules-right-pane">
      <div class="p20 pB0 white-background">
        <div class="module-detail-header">
          <div>
            <div class="setting-form-title text-capitalize">
              {{ moduleData.displayName }}
            </div>
            <div class="heading-description text-capitalize">
              Setup {{ moduleData.displayName }} module details
            </div>
          </div>
          <portal-target name="module-summary-actions"></portal-target>
        </div>
        <div
          class="module-tabs d-flex justify-content-space width100 overflow-y"
        >
          <div class="d-flex module-tabs-height">
            <router-link
              v-for="(tab, idx) in moduleTabs"
              :key="`${tab.label}_${idx}`"
              :to="tab.path"
              class="module-tab-link"
            >
              {{ tab.label }}
              <div class="border-highlight"></div>
            </router-link>
          </div>
          <div class="d-flex justify-end">
            <portal-target name="module-summary-pagenation"></portal-target>
          </div>
        </div>
      </div>

      <router-view
        :moduleDisplayName="moduleData.displayName"
        :moduleData="moduleData"
        :moduleName="moduleName"
        class="border-separator"
      ></router-view>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import ModuleSideBar from './ModuleSideBar'
import { API } from '@facilio/api'

export default {
  props: ['moduleName'],
  components: { ModuleSideBar },

  beforeRouteLeave(to, from, next) {
    this.$store.dispatch('updateSetupSideBar', true).then(() => {
      next()
    })
  },

  created() {
    let { moduleTabs, moduleName } = this
    this.$store.dispatch('updateSetupSideBar', false)
    this.init()

    let { path } = this.$route
    let tabPath = path.split(moduleName)[1] // return route path after moduleName

    if (isEmpty(tabPath) || tabPath === '/') {
      let { path: currentPath } = this.$route
      let { path } = moduleTabs[0]
      let redirectPath = `${currentPath}${tabPath === '/' ? '' : '/'}${path}`

      this.$router.replace({ path: redirectPath })
    }
  },

  data() {
    return {
      isLoading: false,
      moduleData: {},
      fieldCount: null,
      tabs: [
        {
          label: 'Templates',
          path: 'layouts',
        },
        {
          label: 'Fields',
          path: 'fields',
        },
        {
          label: 'Custom Buttons',
          path: 'custombuttons',
          hasLicense: ['CUSTOM_BUTTON'],
        },
        {
          label: 'Relationships',
          path: 'relationship',
        },
      ],
    }
  },

  computed: {
    activeModuleTabs() {
      return this.tabs
    },
    moduleTabs() {
      let { $account, $helpers, activeModuleTabs } = this
      let moduleTabs = activeModuleTabs.filter(tab => {
        let { showInOrg, hasLicense } = tab
        let orgCheck =
          isEmpty(showInOrg) || showInOrg.includes($account.org.orgId)
        let licenseCheck =
          isEmpty(hasLicense) ||
          hasLicense.every(l => $helpers.isLicenseEnabled(l))

        return orgCheck && licenseCheck
      })

      return this.addCustomTabs(moduleTabs)
    },
  },

  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.init()
        }
      },
    },
  },

  methods: {
    addCustomTabs(tabs) {
      this.addVisitorTabs(tabs)
      return tabs
    },
    addVisitorTabs(tabs) {
      let tab = {
        label: 'Visitor Types',
        path: 'visitortypes',
      }
      let { moduleName } = this
      if (moduleName === 'visitorlog' || moduleName === 'invitevisitor') {
        return tabs.push(tab)
      }
    },
    init() {
      this.loadModuleData()
      this.loadFields()
    },
    async loadFields() {
      let { moduleName } = this
      let { error, data } = await API.get('/v2/modules/fields/fields', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { HideFieldsSpecificToModule } = this.$constants
        let hiddenFields = HideFieldsSpecificToModule[moduleName] || []
        let fieldsLength = data.fields.length - hiddenFields.length

        this.fieldCount = fieldsLength
      }
    },
    async loadModuleData() {
      this.isLoading = true

      let { moduleName } = this
      let { error, data } = await API.get(`v2/module/${moduleName}`)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let { module: moduleData } = data
        let { description } = moduleData

        moduleData.description = !isEmpty(description) ? description : '---'
        this.moduleData = moduleData
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss">
.modules-left-pane {
  flex: 0 0 21%;
  border-right: 1px solid #e6e6e6;
  border-left: 1px solid #e8e8e8;
  background: #fff;
}
.modules-right-pane {
  overflow: scroll;
  flex: 0 0 79%;

  .module-detail-header {
    display: flex;
    justify-content: space-between;
  }
  .module-tabs {
    display: flex;
    width: max-content;
    overflow: scroll;
    align-items: center;
    height: 100%;
    margin-top: 30px;
  }
  .module-tab-link {
    font-size: 12px;
    font-weight: 500;
    color: #50516c;
    margin-right: 30px;
    cursor: pointer;
    height: 100%;
    display: flex;
    flex-direction: column;
    text-transform: uppercase;

    .border-highlight {
      border-bottom: 2px solid transparent;
      width: 30px;
      margin-top: 10px;
    }

    &.active {
      color: #2d2d52;
      font-weight: 700;

      .border-highlight {
        border-bottom: 2px solid #ee518f;
      }
    }
  }
  .module-tabs-height {
    min-height: 40px;
    display: flex;
    align-items: flex-end;
  }
}
.modules-details {
  bottom: 0;
  padding: 25px 30px 30px 30px;
  .calender {
    width: 36px;
    height: 36px;
    border-radius: 50px;
    background-color: #efa25e;
  }
  .fields-count,
  .state-flow {
    div {
      flex: 0 0 50%;
    }
  }
  .fields-count,
  .state-flow,
  .description {
    border-top: solid 1px #f3f5f9;
    padding-top: 15px;
  }
  .description {
    .title {
      letter-spacing: 0.92px;
      color: #8ca1ad;
    }
  }
  .state-flow {
    .status {
      color: #46ca87;
      &.disabled {
        color: #eb696a;
      }
    }
  }
}
</style>
