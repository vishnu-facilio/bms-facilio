<template>
  <el-popover
    trigger="hover"
    :visible-arrow="false"
    :open-delay="800"
    :disabled="!showWigetFields"
    @hide="onHidePopover"
    @show="onShowpopover = true"
  >
    <div
      slot="reference"
      class="width-fit  quick-summary-card-header pointer"
      @mouseenter="onHoverFields"
      @mouseover="showEyeIcon = true"
    >
      <div
        v-if="canShowLink"
        @click="redirectToLookUpFieldSummary(field, record)"
        class="bluetxt f13"
        :class="[canShowEyeIcon ? 'qs-field' : 'bluetxt']"
      >
        {{ displayValue }}
      </div>
      <div v-else>{{ displayValue }}</div>
      <div
        :class="[canShowEyeIcon ? 'qs-selection-bar' : 'selection-bar']"
      ></div>
    </div>
    <GlimpseCardWrapper
      v-if="showWigetFields"
      v-bind="$attrs"
      :field="field"
      :details="details"
      :displayValue="displayValue"
      :lookupModuleName="lookupModuleName"
      :canShowRedirect="canShowLink"
      :moduleName="moduleName"
      :recordId="recordId"
      :siteList="siteList"
    ></GlimpseCardWrapper>
  </el-popover>
</template>

<script>
import GlimpseCardWrapper from 'src/newapp/components/GlimpseCardWrapper.vue'
import { mapActions, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { findRouterForModuleInApp } from 'src/newapp/viewmanager/routeUtil.js'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  mixins: [FetchViewsMixin],
  props: ['field', 'record', 'siteList', 'hideGlimpse', 'recordModuleName'],
  components: { GlimpseCardWrapper },
  data() {
    return {
      showWigetFields: false,
      showEyeIcon: false,
      onShowpopover: false,
    }
  },
  mounted() {
    const channel = new BroadcastChannel('glimpse')

    channel.addEventListener('message', event => {
      const message = event.data
      if (message === 'resetDetails') {
        this.resetGlimpseDetails()
      }
    })
  },
  beforeDestroy() {
    this.resetGlimpse()
  },
  computed: {
    ...mapState('glimpse', {
      glimpseDetails: state => state.glimpseDetails,
      deletedGlimpse: state => state.deletedGlimpse,
    }),
    canShowEyeIcon() {
      return this.showWigetFields && this.showEyeIcon
    },
    recordId() {
      let { id } = this.record || {}
      return id
    },
    moduleName() {
      let { recordModuleName, record, field, $route } = this || {}
      let { moduleName } = record || {}
      let name = this.$getProperty(field.field, 'module.name') || ''
      let { moduleName: recordMetaModuleName } = $route?.meta || {}

      return recordModuleName || moduleName || name || recordMetaModuleName
    },
    details() {
      let { name } = this.field || {}
      let recordValue = this.record[name]

      if (name === 'siteId') {
        let siteObj = (this.siteList || []).find(
          site => site.value === recordValue
        )
        let { label: displayName, value } = siteObj || {}

        return {
          displayName,
          id: value,
        }
      }
      return recordValue || {}
    },
    webTabRouteName() {
      let { lookupModuleName } = this
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(lookupModuleName, pageTypes.OVERVIEW) || {}

        return name
      }
      return null
    },
    lookupModuleName() {
      if (this.field?.name === 'siteId') {
        return 'site'
      } else {
        let { lookupModuleName, field: { lookupModule: { name } = {} } = {} } =
          this.field || {}

        return lookupModuleName || name || null
      }
    },
    displayValue() {
      let { primaryValue, name, displayName, subject } = this.details || {}
      let { displayValue } = this.field || {}
      let value = displayName || name || subject || displayValue || '---'
      return !isEmpty(primaryValue) ? primaryValue : value
    },
    canShowLink() {
      let {
        isLookUpRedirectApplicable,
        field,
        displayValue,
        hideGlimpse,
      } = this
      return (
        isLookUpRedirectApplicable(field) &&
        displayValue != '---' &&
        !hideGlimpse
      )
    },
  },
  methods: {
    ...mapActions('glimpse', [
      'loadGlimpseMetaFields',
      'resetGlimpse',
      'resetGlimpseDetails',
    ]),
    onHidePopover() {
      this.showEyeIcon = false
      this.onShowpopover = false
      this.showWigetFields = false
    },
    async onHoverFields() {
      let { lookupModuleName, glimpseDetails, hideGlimpse } = this

      if (hideGlimpse) return

      let currentModuleMeta = glimpseDetails[lookupModuleName] || null

      if (isEmpty(currentModuleMeta)) return
      await this.loadGlimpseMetaFields({ moduleName: lookupModuleName })

      this.showWigetFields =
        glimpseDetails[lookupModuleName]?.active && this.displayValue != '---'
    },
    async redirectToLookUpFieldSummary(lookUpField, details) {
      if (!isEmpty(this.deletedGlimpse[this.details?.id])) return
      let { lookupModuleName, field } = lookUpField || {}
      let { lookupModule, name: fieldName } = field || {}
      let { name, custom } = lookupModule || {}

      let id = null,
        moduleName = null

      if (fieldName === 'siteId') {
        let siteObj = (this.siteList || []).find(
          site => site.value === details[fieldName]
        )
        let { value } = siteObj || {}

        id = value
        moduleName = 'site'
      } else {
        id = details[fieldName]?.id || null
        moduleName = lookupModuleName || name
      }

      let viewname = await this.fetchView(moduleName)

      if (isEmpty(id)) return

      if (isWebTabsEnabled()) {
        if (!isEmpty(this.webTabRouteName)) {
          this.$router.push({
            name: this.webTabRouteName,
            params: { viewname, id },
          })
        }
      } else {
        let currentModuleName = custom ? 'custom' : moduleName
        let routeObj =
          findRouterForModuleInApp(currentModuleName, pageTypes.OVERVIEW) || {}

        if (!isEmpty(routeObj))
          this.$router.push(routeObj(id, viewname, moduleName))
      }
    },
    isLookUpRedirectApplicable(field) {
      let { webTabRouteName } = this
      let { lookupModuleName, field: fieldObj } = field || {}
      let { lookupModule } = fieldObj || {}
      let { custom, typeEnum, name } = lookupModule || {}
      let lookupName = lookupModuleName || name || null
      let routeObj = {}
      if (!custom)
        routeObj = findRouterForModuleInApp(lookupName, pageTypes.OVERVIEW)

      let hasMainAppRoute =
        !isWebTabsEnabled() && (!isEmpty(routeObj) || custom)
      let hasWebTabRoute = isWebTabsEnabled() && !isEmpty(webTabRouteName)
      let hasRoute = hasMainAppRoute || hasWebTabRoute

      return lookupName && typeEnum === 'BASE_ENTITY' && hasRoute
    },
  },
}
</script>
<style lang="scss" scoped>
.quick-summary-card-header:hover {
  .qs-selection-bar {
    border-bottom: 1px solid #368eb7;
    animation: underlineTxt 700ms;
  }

  @keyframes underlineTxt {
    from {
      width: 0%;
    }
    to {
      width: 100%;
    }
  }
}
.quick-summary-card-header {
  .selection-bar {
    border-bottom: 1px solid transparent;
  }
}
</style>
