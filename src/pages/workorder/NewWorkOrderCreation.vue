<template>
  <div
    class="form-data-creation"
    :class="[!$validation.isEmpty(widgetsArr) ? 'form-creation-widgets' : '']"
  >
    <div
      :class="[!$validation.isEmpty(widgetsArr) ? 'form-left-container' : '']"
      class="height-100"
    >
      <div class="header mT20 mB10 d-flex">
        <div class="title mT10">
          {{ title }}
          <span class="bluetxt" style="cursor:pointer;">
            <a
              v-if="moduleDataId > 0"
              @click="redirectToSummary(moduleDataId)"
              class="f15"
            >
              {{ '#' + moduleDataId }}
            </a>
          </span>
        </div>
        <el-select
          v-if="forms.length > 1"
          v-model="selectedForm"
          value-key="name"
          class="fc-input-full-border-select2 mL-auto width25"
        >
          <el-option
            v-for="(form, index) in forms"
            :key="index"
            :value="form"
            :label="form.displayName"
          ></el-option>
        </el-select>
      </div>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isEdit="!$validation.isEmpty(moduleDataId)"
        :isWidgetsSupported="isWidgetsSupported"
        :modifyFieldPropsHook="modifyFieldPropsHook"
        @onWidgetChange="onWidgetChange"
        @save="saveRecord"
        @cancel="redirectToList"
        :isV3Api="false"
      ></f-webform>
    </div>
    <portal-target class="widgets-container" name="side-bar-widgets">
    </portal-target>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import FWebform from '@/FWebform'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import WorkorderCreation from 'pages/workorder/workorders/v1/mixins/workorderCreation'
import { API } from '@facilio/api'
import Vue from 'vue'
export default {
  extends: FormCreation,
  name: 'WorkorderCreate',
  mixins: [WorkorderCreation],
  components: {
    FWebform,
  },
  data() {
    return {
      moduleName: 'workorder',
      moduleDisplayName: 'Work Order',
    }
  },
  computed: {
    isWebTabsEnabledValue() {
      return isWebTabsEnabled()
    },
    moduleDataId() {
      let { parentWO } = this.$route.query || {}

      return parentWO
    },
    title() {
      let { moduleDisplayName, moduleDataId } = this
      return isEmpty(moduleDataId)
        ? `Create ${moduleDisplayName}`
        : `Create Dependent Workorder - `
    },
    isDependantWO() {
      let { parentWO } = this.$route.query || {}
      return !isEmpty(parentWO)
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    successHandler({ error, data }) {
      if (!error) {
        let {
          workorder: { id },
        } = data

        this.$message.success('Workorder created successfully!')
        this.sendGAEvent()
        this.redirectToSummary(id)
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
    },
    sendGAEvent() {
      Vue.prototype.$gtag &&
        Vue.prototype.$gtag.event('custom_event', {
          event_category: 'Workorder',
          event_action: 'Created',
          event_label: 'new workorder btn',
          event_value: 1,
        })
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id,
            },
          })
        }
      } else {
        this.$router.push({ path: `/app/wo/orders/summary/${id}` })
      }
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'open',
            },
          })
        }
      } else {
        this.$router.replace({ path: '/app/wo/orders/open' })
      }
    },
    async loadFormData(formId) {
      let formObj = {}
      let { selectedForm, moduleName, moduleData } = this
      let { id, name, module } = selectedForm
      let { name: formModuleName } = module || {}
      moduleName = isEmpty(formModuleName) ? moduleName : formModuleName
      let formUrl =
        id === -1
          ? `/v2/forms/${moduleName}?formName=${name}&fetchFormRuleFields=true`
          : `/v2/forms/${moduleName}?formId=${formId ||
              id}&fetchFormRuleFields=true`
      this.isLoading = true
      let { data, error } = await API.get(formUrl)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { form } = data
        if (!isEmpty(form)) {
          formObj = form
          formObj.secondaryBtnLabel = this.$t('common._common.cancel')
          formObj.primaryBtnLabel = this.$t('common._common._save')
          this.$set(this, 'formObj', formObj)
          if (!isEmpty(moduleData) && !this.isDependantWO) {
            this.deserializeData(moduleData)
          }
        }
      }
      this.isLoading = false
      return formObj
    },

    modifyFieldPropsHook(field) {
      let { moduleDataId, moduleData } = this
      let { name } = field || {}
      if (this.isDependantWO && !isEmpty(moduleDataId) && name === 'parentWO') {
        return { ...field, isDisabled: true, value: parseInt(moduleDataId) }
      } else if (
        this.isDependantWO &&
        !isEmpty(moduleData) &&
        name === 'siteId'
      ) {
        let { siteId } = moduleData || {}
        if (!isEmpty(siteId)) return { ...field, value: siteId }
      }
    },
  },
}
</script>

<style></style>
