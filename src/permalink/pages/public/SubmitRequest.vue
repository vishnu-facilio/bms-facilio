<template>
  <div class="submit-request-container mB30 portal-form-page">
    <div class="header mT20 mB10 d-flex">
      <div class="title mT10">Submit a Request</div>
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
    <div v-if="isLoading" class="mT130">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      v-else
      :form.sync="formObj"
      :module="module"
      :isSaving="isSaving"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
      @save="saveRecord"
      @cancel="redirectToMyRequests"
      class="fc-portal-form"
    ></f-webform>
  </div>
</template>

<script>
import FormsCreationMixin from '@/mixins/forms/FormsCreationMixin'
import FWebform from '@/FWebform'
import http from 'util/http'
import { getApp } from '@facilio/router'

import { isEmpty, isObject } from '@facilio/utils/validation'

export default {
  mixins: [FormsCreationMixin],
  components: { FWebform },
  data() {
    return {
      module: 'workorder',
    }
  },
  computed: {
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
  },
  methods: {
    serializedData(formData, _formModel) {
      let {
        formObj: { sections },
      } = this

      let { subFormData } = _formModel || {}

      sections.forEach(section => {
        let { fields } = section
        if (!isEmpty(fields)) {
          fields.forEach(field => {
            let { name, field: fieldObj, displayTypeEnum } = field
            let { dataTypeEnum } = fieldObj || {}
            let value = _formModel[name]
            if (dataTypeEnum === 'NUMBER' || dataTypeEnum === 'DECIMAL') {
              if (isEmpty(value)) {
                value = -99
              }
            }
            if (!isEmpty(value) && displayTypeEnum !== 'TASKS') {
              // To differentiate between system and custom fields
              if (fieldObj && !fieldObj.default) {
                // For dropdown custom fields have to fetch the value from id
                if (isObject(value) && isEmpty(value.id)) {
                  formData.workorder.data[name] = null
                } else {
                  formData.workorder.data[name] = value.id ? value.id : value
                }
              } else {
                if (name === 'name' || name === 'email') {
                  formData.workorder.requester[name] = value
                } else if (name === 'siteId' || displayTypeEnum === 'URGENCY') {
                  formData.workorder[name] = value.id
                } else if (name === 'assignment') {
                  for (let assignmentObj in value) {
                    if (!isEmpty(value[assignmentObj].id)) {
                      formData.workorder[assignmentObj] = {
                        id: value[assignmentObj].id,
                      }
                    }
                  }
                } else if (isObject(value) && isEmpty(value.id)) {
                  formData.workorder[name] = null
                } else {
                  formData.workorder[name] = value
                }
              }
            }
          })
        }
      })
      if (isEmpty(formData.workorder.requester)) {
        delete formData.workorder.requester
      }
      if (!isEmpty(subFormData)) {
        formData.workorder['subFormData'] = subFormData
      }
      return formData
    },
    saveRecord(formModel) {
      let { ticketattachments } = formModel
      this.isSaving = true
      let formData = {
        workorder: {
          data: {},
          requester: {},
        },
      }
      delete formModel.ticketattachments
      let serializedData = this.serializedData(formData, formModel)
      if (!isEmpty(ticketattachments)) {
        serializedData['ticketattachments'] = ticketattachments
      }
      let promise = http
        .post('v2/workorders/add?formName=serviceWorkOrder', serializedData)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let {
              workorder: { localId },
            } = result
            this.$message.success('Workorder created successfully!')
            this.$router.push({
              name: 'submitThankYou',
              params: { id: localId },
            })
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => (this.isSaving = false))
    },
    redirectToMyRequests() {
      this.$router.replace({
        name: 'login',
      })
    },
  },
}
</script>

<style scoped>
.submit-request-container {
  padding-bottom: 50px;
  max-width: 1000px;
  margin: auto;
}

.submit-request-container
  .el-select.fc-input-full-border-select2
  .el-input__inner {
  border: 1px solid #e9edf1;
}
.portal-form-page .fc-portal-form .f-webform-container {
  height: inherit;
  overflow-y: inherit;
  position: relative;
  margin-bottom: 100px;
}
.portal-form-page .fc-portal-form .f-webform-container form {
  height: calc(100vh - 135px);
  overflow-y: scroll;
  padding-bottom: 100px;
}
.portal-form-page .submit-request-container {
  height: calc(100vh - 200px);
}
.portal-form-page .fc-web-form-action-btn {
  width: 100%;
  position: absolute;
  bottom: 0;
}
.portal-form-page .fc-web-form-action-btn .secondary,
.fc-web-form-action-btn .primary {
  width: 50%;
}
</style>
