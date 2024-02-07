<template>
  <div v-if="hasSwitch">
    <el-dropdown
      @command="setSwitchValue"
      @click="setFocus"
      trigger="click"
      class="siteDropdown"
      :tabindex="-1"
    >
      <div class="moduleSwitch">
        <div class="header__current__site__switch">
          <div
            class="header__current__site__display new-home-display p0 flex-middle"
          >
            <span
              v-tippy
              :title="$t('setup.decommission.decommissioned')"
              v-if="getSelectedOptionIsDecommission"
              class="pT5"
              ><fc-icon
                group="alert"
                name="decommissioning"
                size="15"
              ></fc-icon
            ></span>
            <span v-else class="switch-dot"></span>
            <span
              key="switch-option-selected"
              class="dropdown-item mL10 max-width170px"
              >{{ getSelectedOptionValue }}</span
            >
          </div>
          <i
            :class="{ 'arrow-push-bottom': getSelectedOptionIsDecommission }"
            class="fa fa-sort-desc wl-icon-downarrow"
          ></i>
        </div>
      </div>
      <el-dropdown-menu slot="dropdown" class="p5">
        <el-input
          @input="loadFilteredOptions"
          prefix-icon="el-icon-search"
          class="site_switch_search_box  p5"
          v-model="searchText"
          ref="site-switch-search-box"
          :placeholder="$t('common._common.search')"
        ></el-input>
        <el-dropdown-item
          v-for="(option, optionKey) in moduleOptionList"
          :key="`option-${optionKey}`"
          class="dropdown-item"
          :command="optionKey"
          >{{ option.label }}
          <span v-if="optionKey === `${selectedId}`" class="success-icon pL8"
            ><i class="el-icon-success"></i
          ></span>
          <span
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            v-if="getWarningVisible(option)"
            ><fc-icon
              group="alert"
              class="fR pT10 pL20"
              name="decommissioning"
              size="16"
            ></fc-icon
          ></span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import { getFieldOptions } from 'util/picklist'
import { getApp } from '@facilio/router'
const RESOURCE_MODULES = [
  'site',
  'building',
  'floor',
  'space',
  'asset',
]
export default {
  data() {
    return {
      isLoading: false,
      moduleName: null,
      moduleLinkName: null,
      moduleOptionList: {
        null: 'All',
      },
      searchText: null,
      hasSwitch: false,
    }
  },

  async created() {
    await this.getSwitchVariable()

    await this.loadModuleOptions()
  },

  computed: {
    getSelectedOptionValue() {
      let { selectedId, moduleOptionList } = this
      if (!isEmpty(moduleOptionList)) {
        if (selectedId in moduleOptionList) {
          return this.$getProperty(
            moduleOptionList,
            `${selectedId}.label`,
            '---'
          )
        }
      }
      return 'All'
    },
    getSelectedOptionIsDecommission() {
      let { selectedId, moduleOptionList } = this
      if (!isEmpty(moduleOptionList)) {
        if (selectedId in moduleOptionList) {
          let { fourthLabel } = moduleOptionList[selectedId] || {}
          return !isEmpty(fourthLabel) ? JSON.parse(fourthLabel) : false
        }
      }
      return false
    },
    selectedId: {
      get() {
        let { linkName } = getApp() || {}
        let cookieValue = this.switchCookieValue

        if (cookieValue && cookieValue !== 'null' && !isEmpty(linkName)) {
          let convertedValue = atob(cookieValue)
          let decodedValue = JSON.parse(convertedValue)
          let { [linkName]: encodedAppSwitchValue } = decodedValue || {}
          if (!isEmpty(encodedAppSwitchValue)) {
            let decodedAppValue = atob(encodedAppSwitchValue)
            let appSpecificValue = JSON.parse(decodedAppValue)
            let { [this.moduleLinkName]: selectedIdArr } =
              appSpecificValue || {}
            let [selectedId] = selectedIdArr || []
            return selectedId || null
          }
        }
        return null
      },
      set(value) {
        let cookieValue = this.switchCookieValue
        if (cookieValue && cookieValue !== 'null' && !isEmpty(cookieValue)) {
          cookieValue = JSON.parse(atob(cookieValue))
        } else {
          cookieValue = {}
        }
        let encodedValue = null
        let appAllEncodedValue = null
        if (!isEmpty(value)) {
          let actualValue = { [this.moduleLinkName]: [value.toString()] }
          encodedValue = btoa(JSON.stringify(actualValue))
        }
        let { linkName } = getApp() || {}
        if (!isEmpty(linkName)) {
          cookieValue[linkName] = encodedValue
          appAllEncodedValue = btoa(JSON.stringify(cookieValue))
        }
        this.$cookie.set('fc.switchValue', appAllEncodedValue, {
          expires: '10Y',
          path: '/',
        })
        this.$router.go()
      },
    },
    switchCookieValue() {
      let cookieValue = this.$cookie.get('fc.switchValue') || null
      return cookieValue
    },
  },

  methods: {
    setFocus() {
      this.setAutofocus('site-switch-search-box')
    },
    loadFilteredOptions: debounce(async function() {
      await this.loadModuleOptions()
    }, 500),

    setSwitchValue(id) {
      if (id && id !== 'null') {
        this.selectedId = parseInt(id)
      } else this.selectedId = null
    },
    async getSwitchVariable() {
      this.isLoading = true

      let url = `v3/switchVariable/getSwitchVariable`
      let { error, data } = await API.get(url, { force: true })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let [switchVariable] = data?.switchVariable || []

        if (!isEmpty(switchVariable)) {
          this.hasSwitch = true

          this.moduleName = switchVariable?.applicableModuleName || null
          this.moduleLinkName = switchVariable?.linkName || null
        }
      }
      this.isLoading = false
    },
    async loadModuleOptions() {
      let { moduleName, searchText } = this
      if (isEmpty(moduleName)) return
      let optionList = {}
        let params = {
          field: {
            lookupModuleName: this.moduleName,
            skipDeserialize: false,
            additionalParams: {
            isToFetchDecommissionedResource: true,
          },
          },
          searchText: searchText || null,
          apiOptions: {
            removeSwitchFilter: true,
          },
          defaultIds: [this.selectedId],
        }

        let { error, options } = await getFieldOptions(params)
        if (!error) {
          optionList = { null: { label: 'All', fourthLabel: 'false' } }
          Object.values(options).forEach(option => {
            let key = option.value
            delete option['value']
            optionList[key] = option
          })
        }

      this.moduleOptionList = optionList
    },
    getWarningVisible(data) {
      return !isEmpty(data?.fourthLabel)
        ? JSON.parse(data.fourthLabel)
        : false
    },
  },
}
</script>

<style scoped>
.success-icon {
  float: right;
  color: #67c23a;
}
.arrow-push-bottom {
  bottom: 5px !important;
}
.moduleSwitch {
  background-color: transparent;
  padding: 0;
  border: none;
}
.moduleSwitch:hover,
.moduleSwitch:focus {
  color: #409eff;
}

.site_switch_search_box:focus {
  border: 1px solid #39b2c2 !important;
}
.site_switch_search_box {
  border: 1px solid #d8dce5;
}
.site_switch_search_box .el-input__inner {
  border-bottom: none !important;
  padding-left: 25px;
}
i.el-icon-success {
  margin-right: 0;
}
li.el-dropdown-menu__item.dropdown-item {
  padding: 0 10px;
}
.switch-dot {
  width: 8px;
  height: 8px;
  background-color: #d8dce5;
  border-radius: 50%;
}
</style>
