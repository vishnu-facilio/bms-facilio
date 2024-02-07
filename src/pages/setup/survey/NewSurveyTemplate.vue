<template>
  <el-dialog
    :title="surveyTemplateTitle"
    :visible="showCreateDialog"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height400">
      <el-form ref="groupForm" :model="template">
        <el-form-item
          :label="$t('survey.survey_name')"
          prop="name"
          :required="true"
        >
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="template.name"
            type="text"
            :placeholder="$t('survey.enter_the_survey_name')"
          />
        </el-form-item>
        <el-form-item
          :label="$t('common.roles.description')"
          prop="description"
        >
          <el-input
            v-model="template.description"
            :placeholder="$t('survey.enter_description')"
            class="fc-input-full-border-select2 width100"
            type="textarea"
            autofocus
            :autosize="{ minRows: 3, maxRows: 4 }"
          >
          </el-input>
        </el-form-item>
      </el-form>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>

        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="createTemplate()"
        >
          {{ $t('common._common._save') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  name: 'SurveyTemplate',
  props: ['showCreateDialog', 'selectedTemplate', 'isNew'],
  data() {
    return {
      template: {
        name: null,
        description: null,
      },
      saving: false,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },
  created() {
    let { isNew, selectedTemplate } = this
    if (!isNew && !isEmpty(selectedTemplate)) {
      let { name, description } = selectedTemplate || {}
      this.$set(this, 'template', { name, description })
    }
  },
  computed: {
    surveyTemplateTitle() {
      let { isNew } = this
      return isNew
        ? this.$t('survey.create_survey')
        : this.$t('survey.edit_survey')
    },
    targetModuleName() {
      let { moduleName: targetModule } = this.$route.params
      if (targetModule === 'workorder') targetModule = 'workOrder'
      return `${targetModule}SurveyTemplate`
    },
  },
  watch: {
    selectedTemplate: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          this.$set(this, 'template', newVal)
        }
      },
      immediate: true,
    },
  },
  methods: {
    async createTemplate() {
      let { template } = this
      let moduleName = 'surveyTemplate'
      this.saving = true
      let { selectedTemplate } = this
      let { name } = template || {}
      let { id: templateId } = selectedTemplate || {}
      let promise
      let params = { moduleName, data: template }
      if (!isEmpty(name)) {
        if (!isEmpty(templateId)) {
          params = { ...params, id: templateId }
          promise = await API.post('v3/modules/data/patch', params)
        } else {
          promise = await API.post('v3/modules/data/create', params)
        }
        let { error, data } = promise
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { [moduleName]: record } = data || {}

          this.$emit('onSave', record)
          this.saving = false
          this.closeDialog()
        }
      } else {
        this.$message.error('Please fill template name')
        this.saving = false
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
