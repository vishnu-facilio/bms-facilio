<template>
  <div>
    <el-row>
      <el-col :span="24">
        <div class="display-flex-between-space pB40 pT30">
          <div>Update Fields for this record</div>
          <div class="fc__layout__has__columns">
            <template v-if="isConfigured">
              <div class="pR10  border-right2 configured-green">
                {{ $t('common._common.configured') }}
              </div>
              <div class="pL10 pointer" @click="editForm()">
                <i class="el-icon-edit"></i>
              </div>
              <div>
                <a @click="removeForm()" class="mL20 cursor-pointer reset-txt">
                  {{ $t('common._common.reset') }}
                </a>
              </div>
            </template>
            <template v-else>
              <div
                class="pR10  border-right2 pointer configure-blue"
                @click="() => (showFieldPickerDialog = true)"
              >
                {{ $t('common._common.configure') }}
              </div>
            </template>
          </div>
        </div>
        <FieldPicker
          v-if="showFieldPickerDialog"
          :availableFields="formFields"
          :selectedList="selectedFields"
          :isInFormConfig="true"
          @save="handleFieldPickerSave"
          @close="() => (showFieldPickerDialog = false)"
        />
        <FormsEdit
          v-if="canShowFormBuilder"
          :moduleName="module"
          :id="activeFormId"
          :onSave="closeForm"
          :showDeleteForField="({ name }) => true"
          :isUpdateForm="true"
          class="formbuilder-container"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import FieldPicker from 'newapp/components/FieldPicker'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
import { v4 as uuid } from 'uuid'
import FormsEdit from 'pages/setup/modules/FormsEdit'

export default {
  name: 'UpdateRecordBehaviour',
  props: ['module', 'isNew', 'formData', 'formId'],
  components: { FieldPicker, FormsEdit },
  data() {
    return {
      isConfigured: false,
      showFieldPickerDialog: false,
      formFields: [],
      selectedFields: [],
      canShowFormBuilder: false,
    }
  },
  created() {
    this.fetchFormFields()
    if (!isEmpty(this.formData)) {
      this.selectedFields = this.formData[0].fields
      this.isConfigured = true
    }
  },
  methods: {
    async fetchFormFields() {
      return API.get(`/v2/forms/fields`, {
        moduleName: this.module,
      }).then(({ data, error }) => {
        if (!error) {
          let fields = this.$getProperty(data, 'fields') || []
          this.formFields =
            fields.filter(field => field.displayTypeEnum !== 'TASKS') || []
        } else {
          this.$message.error(error.message || 'Error Occured')
        }
      })
    },

    handleFieldPickerSave(fields) {
      if (isEmpty(fields)) {
        this.$message.error(this.$t('common.header.please_select_one_field'))
      } else {
        this.isConfigured = true
        this.selectedFields = fields
        this.createForm()
      }
    },
    async createForm() {
      const { module: moduleName, selectedFields } = this
      const { linkName: appLinkName, id: appId } = getApp()
      const url = 'v2/forms/add'

      const params = {
        appLinkName,
        moduleName,
        form: {
          name: `custom_button_form_${uuid()}`,
          displayName: 'Enter details',
          appLinkName,
          appId,
          labelPosition: 1,
          showInMobile: true,
          hideInList: true,
          siteIds: [],
          sections: [{ fields: selectedFields }],
          stateFlowId: -99,
        },
      }

      const { error, data } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        const { form } = data

        this.$emit('setProperties', {
          form: form,
          formId: form.id,
          selectedFields: this.selectedFields,
        })
        this.editForm(form.id)
      }
    },
    editForm(id) {
      let { formId } = this
      this.activeFormId = id || formId
      this.canShowFormBuilder = true
    },
    closeForm() {
      this.activeFormId = null
      this.canShowFormBuilder = false
    },
    async removeForm() {
      this.$emit('setProperties', {
        form: null,
        formId: -99,
        selectedFields: [],
      })
      this.selectedFields = []
      this.isConfigured = false
    },
  },
}
</script>
