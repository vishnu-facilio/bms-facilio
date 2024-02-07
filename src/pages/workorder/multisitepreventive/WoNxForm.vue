<template>
  <div class="fc-pm-form-right-main2">
    <div v-if="!model.isEdit" class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.new_pm') }}
    </div>
    <div v-else class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.edit_pm') }}
    </div>
    <div v-if="loading" class="fc-pm-main-bg">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-show="!loading" class="fc-pm-main-bg">
      <div class="fc-pm-main-content2 pm-form-active-border-remove">
        <!-- form start -->
        <f-webform
          v-if="!loading"
          :form.sync="formObj"
          :module="'workorder'"
          :moduleDisplayName="'Work Order'"
          :isSaving="false"
          :canShowPrimaryBtn="false"
          :canShowSecondaryBtn="false"
          @onFormModelChange="formUpdateHandler"
          :isWidgetsSupported="true"
          :isV3Api="false"
          :isEdit="model.isEdit"
        >
        </f-webform>
        <!-- form end -->
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="moveToPrevious">{{
          $t('maintenance.wr_list.previous')
        }}</el-button>
        <el-button type="primary" class="modal-btn-save" @click="moveToNext">{{
          $t('maintenance.wr_list.proceed_to_next')
        }}</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty, isNullOrUndefined, isObject } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import FWebform from '@/FWebform'
import { deepCloneObject } from 'util/utility-methods'
import { getBaseURL } from 'util/baseUrl'
import Constants from 'util/constant'
import pick from 'lodash/pick'
export default {
  name: 'NxWoForm',
  props: ['model'],
  components: {
    FWebform,
  },
  data() {
    return {
      formObj: {},
      loading: true,
    }
  },
  created() {
    this.loadFormData()
  },
  computed: {
    moduleDataId() {
      let { query } = this.$route || {}
      let { edit } = query || {}
      return edit || null
    },
  },
  methods: {
    convToDaysHours(seconds) {
      if (isObject(seconds)) {
        seconds = this.$util.daysHoursToUnixTime(seconds) || -1
      }
      if (seconds === -1) {
        return { days: 0, hours: 0 }
      }

      const factor = 3600
      const days = seconds / (24 * factor)
      const hours = seconds - days * 24 * factor
      return { days, hours }
    },
    formUpdateHandler(selection) {
      if (!this.model.woData.woModel || !this.formObj) {
        return
      }
      for (const key in selection) {
        let value = selection[key]
        if (value === null || value === undefined) {
          continue
        }

        const formField = this.formObj.fields.filter(f => f.name === key)[0]
        let { field } = formField || {}
        let { dataTypeEnum } = field || {}
        let { displayType } = field || {}
        if (formField && formField.field && formField.field.dataType === 8) {
          value = value.id
        } else if (dataTypeEnum === 'LOOKUP' && isEmpty(value.id)) {
          value = { id: -1 }
        } else if (dataTypeEnum === 'MULTI_LOOKUP') {
          value = (value || []).map(currValue => {
            return { id: currValue }
          })
        } else if (displayType === 'DURATION' && dataTypeEnum === 'NUMBER') {
          value = this.convToDaysHours(value) // FacilioWebForm expects duration in this { days: X, hours: Y } format.
        }

        if (key === 'duration' || key == 'estimatedWorkDuration') {
          value = this.convToDaysHours(value) // FacilioWebForm expects duration in this { days: X, hours: Y } format.
        }
        if (this.model.woData.woModel.data) {
          if (key in this.model.woData.woModel.data) {
            this.model.woData.woModel.data[key] = value
          }
        }

        this.model.woData.woModel[key] = value
      }
    },
    moveToPrevious() {
      this.$emit('previous')
    },
    moveToNext() {
      this.$emit('next')
    },
    async loadFormData() {
      let formObj = {}
      let formUrl = `/v2/forms/workorder?formName=multi_web_pm&fetchFormRuleFields=true`
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
          this.formObj = formObj

          if (this.model.isEdit && !isEmpty(this.model.actualWoData)) {
            this.deserializeData(this.model.actualWoData)
          }
        }
      }
      this.loading = false
      this.$emit('setFormObject', formObj)
      return formObj
    },
    deserializeData(moduleData) {
      let { data } = moduleData || {}
      let { isV3Api } = this
      let { formObj } = this
      if (!isEmpty(formObj)) {
        let { sections } = formObj
        if (!isEmpty(sections)) {
          sections.forEach(section => {
            let { fields } = section
            if (!isEmpty(fields)) {
              fields.forEach(field => {
                let {
                  field: fieldObj,
                  name,
                  displayTypeEnum,
                  displayType,
                } = field

                // Custom fields data extraction
                if (
                  !isEmpty(fieldObj) &&
                  !fieldObj.default &&
                  !isEmpty(data) &&
                  !isV3Api
                ) {
                  if (
                    displayTypeEnum === 'IMAGE' ||
                    displayTypeEnum === 'SIGNATURE'
                  ) {
                    let imageId = moduleData.data[`${name}Id`]
                    let imgUrl = `${getBaseURL()}/v2/files/preview/${
                      moduleData.data[`${name}Id`]
                    }`
                    this.$set(field, 'imgUrl', imageId ? imgUrl : null)
                    this.$set(field, 'value', imageId)
                  } else if (displayTypeEnum === 'FILE') {
                    let fileId = moduleData.data[`${name}Id`]
                    let fileObj = { name: moduleData.data[`${name}FileName`] }
                    this.$set(field, 'fileObj', fileId ? fileObj : null)
                    this.$set(field, 'value', fileId)
                  } else if (displayTypeEnum === 'LOOKUP_SIMPLE') {
                    if (
                      !isEmpty(data[name]) &&
                      this.$getProperty(data[name], 'id') > 0
                    ) {
                      this.$set(field, 'value', (data[name] || {}).id)
                    }
                  } else if (displayTypeEnum === 'DURATION') {
                    this.$set(
                      field,
                      'value',
                      this.$helpers.getDurationInSecs(
                        data[name],
                        !isEmpty((fieldObj || {}).unit) ? fieldObj.unit : 's'
                      )
                    )
                  } else if (
                    displayTypeEnum === 'SADDRESS' ||
                    displayTypeEnum === 'ADDRESS'
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(
                        this,
                        `moduleData.data.${field.name}`,
                        deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
                      )
                    )
                  } else {
                    this.$set(field, 'value', data[name])
                  }
                } else if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
                  let { assignedTo, assignmentGroup } = moduleData
                  let fieldValue = {
                    assignedTo: {
                      id: '',
                    },
                    assignmentGroup: {
                      id: '',
                    },
                  }
                  if (!isEmpty(assignedTo)) {
                    fieldValue.assignedTo = assignedTo
                  }
                  if (!isEmpty(assignmentGroup)) {
                    fieldValue.assignmentGroup = assignmentGroup
                  }
                  this.$set(field, 'value', fieldValue)
                } else {
                  if (
                    displayTypeEnum === 'IMAGE' ||
                    displayTypeEnum === 'SIGNATURE'
                  ) {
                    let imageId = moduleData[`${name}Id`]
                    let imgUrl = `${getBaseURL()}/v2/files/preview/${
                      moduleData[`${name}Id`]
                    }`
                    this.$set(field, 'imgUrl', imageId ? imgUrl : null)
                    this.$set(field, 'value', imageId)
                  } else if (displayTypeEnum === 'FILE') {
                    let fileId = moduleData[`${name}Id`]
                    let fileObj = { name: moduleData[`${name}FileName`] }
                    this.$set(field, 'fileObj', fileId ? fileObj : null)
                    this.$set(field, 'value', fileId)
                  } else if (displayTypeEnum === 'ATTACHMENT') {
                    let attachments = this.$getProperty(
                      moduleData,
                      'attachedFiles.attachments',
                      []
                    )
                    this.$set(field, 'value', attachments)
                  } else if (
                    (displayTypeEnum === 'LOOKUP_SIMPLE' ||
                      displayTypeEnum === 'REQUESTER') &&
                    field.name !== 'siteId'
                  ) {
                    this.$set(field, 'value', (moduleData[name] || {}).id)
                  } else if (displayType === 56) {
                    let { config } = field
                    let {
                      endFieldName,
                      startFieldName,
                      scheduleJsonName,
                    } = config
                    let scheduleValueObj = {
                      startFieldValue: moduleData[startFieldName],
                      endFieldValue: moduleData[endFieldName],
                      scheduleJsonValue: moduleData[scheduleJsonName],
                      isRecurring: moduleData.isRecurring,
                    }
                    this.$set(field, 'scheduleValueObj', scheduleValueObj)
                  } else if (
                    displayTypeEnum === 'SADDRESS' ||
                    displayTypeEnum === 'ADDRESS'
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(
                        this,
                        `moduleData.${field.name}`,
                        deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
                      )
                    )
                  } else if (displayTypeEnum === 'QUOTE_ADDRESS') {
                    this.$set(
                      field,
                      'billToAddress',
                      this.$getProperty(this, 'moduleData.billToAddress', {})
                    )
                    this.$set(
                      field,
                      'shipToAddress',
                      this.$getProperty(this, 'moduleData.shipToAddress', {})
                    )
                  } else if (
                    ['QUOTE_LINE_ITEMS', 'LINEITEMS'].includes(displayTypeEnum)
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(this, `moduleData.${name}`, null)
                    )
                    Constants.QUOTE_LINE_ITEMS_ADDITIONAL_FIELDS.forEach(
                      additionalField => {
                        let value = this.$getProperty(
                          this,
                          `moduleData.${additionalField}`,
                          null
                        )
                        if (additionalField === 'tax') {
                          if (isEmpty(value)) {
                            value = { id: null }
                          } else {
                            value = pick(value, ['id'])
                          }
                        }
                        this.$set(field, additionalField, value)
                      }
                    )
                  } else if (
                    ['INVREQUEST_LINE_ITEMS'].includes(displayTypeEnum)
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(this, `moduleData.lineItems`, null) ||
                        this.$getProperty(
                          this,
                          `moduleData.inventoryrequestlineitems`,
                          null
                        )
                    )
                  } else if (displayTypeEnum === 'BUDGET_AMOUNT') {
                    this.$set(
                      field,
                      'value',
                      this.getFormattedBudgetAmountsData(moduleData)
                    )
                  } else if (displayTypeEnum === 'FACILITY_BOOKING_SLOTS') {
                    this.$set(
                      field,
                      'value',
                      this.getFormattedSlotData(moduleData)
                    )
                    this.$set(field, 'bookingDate', moduleData.bookingDate)
                  } else if (displayTypeEnum === 'FACILITY_AVAILABILITY') {
                    this.$set(
                      field,
                      'value',
                      this.formatForFacilityAvailability(moduleData)
                    )
                  } else {
                    if (
                      ['NUMBER', 'DECIMAL'].includes(displayTypeEnum) &&
                      !isNullOrUndefined(moduleData[name])
                    ) {
                      this.$set(field, 'value', moduleData[name])
                    } else if (displayTypeEnum === 'DURATION') {
                      this.$set(
                        field,
                        'value',
                        this.$helpers.getDurationInSecs(
                          moduleData[name],
                          !isEmpty((fieldObj || {}).unit) ? fieldObj.unit : 's'
                        )
                      )
                    } else if (isEmpty(moduleData[name])) {
                      this.$set(field, 'value', null)
                    } else {
                      this.$set(field, 'value', moduleData[name])
                    }
                  }
                }
              })
            }
          })
        }
      }
    },
    // deserializeData(moduleData) {
    //   if (!moduleData) {
    //     console.log('module data is null/ undefined')
    //     return
    //   }
    //   console.log('deserializing data')
    //   console.log('moduleData:', moduleData)
    //   // let { data } = moduleData

    //   let { isV3Api } = this
    //   let { formObj } = this
    //   if (!isEmpty(formObj)) {
    //     let { sections } = formObj
    //     if (!isEmpty(sections)) {
    //       sections.forEach(section => {
    //         let { fields } = section
    //         if (!isEmpty(fields)) {
    //           fields.forEach(field => {
    //             console.log('f:', field)
    //           })
    //         }
    //       })
    //     }
    //   }
    // },
  },
}
</script>
