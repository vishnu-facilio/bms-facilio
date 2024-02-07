<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-form-item>
          <div class="pB15">
            <p class="details-Heading">
              {{ $t('setup.customButton.module_name') }}
            </p>
            <div class="heading-description">
              {{ $t('setup.customButton.module_name_desc') }}
            </div>
          </div>
          <el-select
            v-model="formModuleName"
            clearable
            class="width100 pR20"
            :placeholder="$t('setup.customButton.module_form_placeholder')"
          >
            <el-option
              v-for="option in modulesList"
              :key="option.moduleId"
              :value="option.name"
              :label="option.displayName"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <el-form-item prop="formTemplateCreateRecord">
          <div class="pB20">
            <p class="details-Heading pB10">
              {{ $t('setup.customButton.module_form') }}
            </p>
            <div class="heading-description">
              {{ $t('setup.customButton.module_form_desc') }}
            </div>
          </div>
          <el-select
            :loading="isFormTemplateLoading"
            v-model="formId"
            clearable
            class="width100 pR20"
            :placeholder="$t('setup.customButton.module_form_placeholder')"
          >
            <el-option
              v-for="option in formTemplates"
              :key="option.id"
              :value="option.id"
              :label="option.displayName"
            >
            </el-option>
          </el-select>
          <p v-if="hasSubModuleLookup" style="color: #fd6e6e" class="f12">
            {{ `Please select a form with ${module} lookup` }}
          </p>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'CreateRecordCustomBtn',
  props: ['module', 'customButtonObject'],
  data() {
    return {
      isFormTemplateLoading: false,
      formModuleName: null,
      formTemplates: null,
      lookupFieldId: null,
      hasSubModuleLookup: false,
      formId: null,
    }
  },
  created() {
    this.deserialize()
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    modulesList() {
      return this.getAutomationModulesList()
    },

    subModuleName() {
      return this.formModuleName
    },
    formTemplatesList() {
      return this.formId
    },
  },
  watch: {
    subModuleName: {
      handler: 'getFormTemplates',
      immediate: true,
    },

    formTemplatesList: 'getLookupFieldId',
  },
  methods: {
    async deserialize() {
      let subModuleName = this.$getProperty(
        this,
        'customButtonObject.formModuleName'
      )
      this.formModuleName = subModuleName
    },
    async getFormTemplates() {
      this.isFormTemplateLoading = true
      let { data } = await API.get('/v2/forms', {
        moduleName: this.formModuleName,
        skipTemplatePermission: true,
      })
      this.isFormTemplateLoading = false
      this.formTemplates = this.$getProperty(data, 'forms')
      if (!this.formId) {
        this.formId = this.$getProperty(this, 'customButtonObject.formId')
      }
    },
    async getLookupFieldId() {
      let { data } = await API.get(`/v2/forms/${this.formModuleName}`, {
        formId: this.formId,
      })
      let fields = this.$getProperty(data, 'form.fields')
      if (!isEmpty(fields)) {
        let lookupfield
        fields.forEach(field => {
          if (
            field.displayType === 10 &&
            field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
            field.lookupModuleName === this.module
          ) {
            lookupfield = field.fieldId
          }
        })
        this.lookupFieldId = lookupfield

        if (isEmpty(lookupfield)) {
          this.hasSubModuleLookup = true
        } else {
          this.hasSubModuleLookup = false
        }

        this.$emit('setProperties', {
          formModuleName: this.formModuleName,
          formId: this.formId,
          lookupFieldId: lookupfield,
        })
        return lookupfield
      } else {
        this.hasSubModuleLookup = true
        this.$emit('setProperties', {
          formModuleName: this.formModuleName,
          formId: this.formId,
          lookupFieldId: null,
        })
        return null
      }
    },
  },
}
</script>
