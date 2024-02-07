<script>
import DsmForm from 'src/pages/custom-module/DSMFormCreation.vue'
import { API } from '@facilio/api'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: DsmForm,
  methods: {
    async loadModuleData({ moduleDataId, moduleName }) {
      let { error, [moduleName]: record } = await API.fetchRecord(moduleName, {
        id: moduleDataId,
      })
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { calendarEventMappingContextList } = record || {}
        record = {
          ...record,
          calendarEventList: calendarEventMappingContextList,
        }
        this.$set(this, 'moduleData', record)
      }
    },
    async loadFormData(formId) {
      let formObj = {}
      let { selectedForm, moduleName, moduleData, isEdit } = this
      let { id, name, module } = selectedForm
      let { name: formModuleName } = module || {}
      moduleName = isEmpty(formModuleName) ? moduleName : formModuleName
      let formUrl =
        id === -1
          ? `/v2/forms/${moduleName}?formName=${name}&fetchFormRuleFields=true`
          : `/v2/forms/${moduleName}?formId=${formId ||
              id}&fetchFormRuleFields=true`
      if (!isEdit) {
        formUrl = `${formUrl}&forCreate=true`
      }

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
          if (!isEmpty(moduleData)) {
            this.deserializeData(moduleData)
          }
        }
      }
      return formObj
    },
    modifyFieldPropsHook(field) {
      // Overriding field object in consuming component

      //For Calender Event
      let { displayTypeEnum } = field || {}
      if (displayTypeEnum === 'CALENDAR_CONFIGURATION') {
        return { ...field, displayName: '' }
      }
    },
    serializedData(formObj, formModel) {
      let { name, calendarType, client, description, calendarEventList } =
        formModel || {}
      let calendarEventMappingContextList = []
      calendarEventMappingContextList = (calendarEventList || []).map(e => {
        let { calendarTimeSlotContextList: timeSlots, event: calEvent } =
          e || {}
        let calendarTimeSlotContextList = []
        if (!isEmpty(timeSlots)) {
          calendarTimeSlotContextList = (timeSlots || []).map(event => {
            let { startMin, endMin } = event || {}
            startMin = startMin.toString()
            endMin = endMin.toString()
            let startTimehr = startMin.slice(16, 18)
            let startTimemin = startMin.slice(19, 21)
            let endTimehr = endMin.slice(16, 18)
            let endTimemin = endMin.slice(19, 21)

            let timeObj = {
              startMin: parseInt(startTimehr) * 60 + parseInt(startTimemin),
              endMin: parseInt(endTimehr) * 60 + parseInt(endTimemin),
            }
            return timeObj
          })
        }
        return { event: calEvent, calendarTimeSlotContextList }
      })
      let { id } = calendarType || {}
      let eventConfiguration = {
        calendarType: id,
        name,
        description,
        calendarEventMappingContextList,
        client,
      }
      return eventConfiguration
    },
    async saveRecord(formModel) {
      let { formObj, afterSerializeHook, moduleName, moduleDataId } = this
      let { formId } = formModel
      let response = {}
      let data = this.serializedData(formObj, formModel)
      if (!isEmpty(formId)) {
        data.formId = formId
      }
      if (!isEmpty(moduleDataId)) {
        data.id = moduleDataId
      }
      // Hook to overwrite the serialized data, before performing crud operation
      if (!isEmpty(afterSerializeHook) && isFunction(afterSerializeHook)) {
        data = this.afterSerializeHook({
          data,
          formModel,
          formObj,
        })
      }
      this.isSaving = true
      if (isEmpty(moduleDataId)) {
        response = await API.createRecord(moduleName, {
          data,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        })
      }
      this.isSaving = false
      // Hook to handle notification after crud operation
      let { moduleDisplayName } = this
      let { error, [moduleName]: record } = response || {}
      if (error) {
        let { message = 'Error occured' } = error
        this.$message.error(message)
      } else {
        let successMsg = moduleDataId
          ? `${moduleDisplayName} updated successfully`
          : `${moduleDisplayName} created successfully`
        this.$message.success(successMsg)
        this.redirectToSummary(record?.id)
      }
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled() && !isEmpty(id)) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: { id, viewname: 'all' },
          })
      }
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      }
    },
  },
}
</script>
