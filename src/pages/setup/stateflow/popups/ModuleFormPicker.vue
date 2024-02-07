<template>
  <el-dialog
    :visible="true"
    width="40%"
    title="Configure"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="close"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorMessage"
    ></error-banner>
    <div class="height330 overflow-y-scroll pB50 transition-field-dialog">
      <el-row class="pR30">
        <el-col :span="24">
          <p class="fc-input-label-txt txt-color">
            Create a record while performing this transition
          </p>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="20">
          <p class="fc-input-label-txt">Module</p>
          <el-select
            v-model="selectedFormModule"
            :loading="isModuleLoading"
            placeholder="Select Module"
            @change="getModuleForm"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="item in filteredModulesList"
              :key="item.value"
              :label="item.displayName"
              :value="item.name"
            >
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="pT10">
        <el-col :span="20">
          <p class="fc-input-label-txt">Form</p>
          <el-select
            v-model="selectedFormId"
            :loading="isFormLoading"
            :disabled="!selectedFormModule"
            placeholder="Select Form"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="item in relatedModuleForms"
              :key="item.value"
              :label="item.displayName"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
        <el-button
          type="primary"
          :loading="isSaving"
          class="modal-btn-save"
          @click="saveForm()"
          >Save</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script>
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

const DIALOG_TYPE = {
  FIELD: 1,
  FORM: 2,
}

export default {
  props: ['transitionObj', 'module', 'autoSave'],

  components: {
    ErrorBanner,
  },

  data() {
    return {
      isModuleLoading: false,
      isFormLoading: false,
      formModules: [],
      relatedModuleForms: [],
      selectedFormModule: null,
      selectedFormId: null,
      isSaving: false,
      error: false,
      errorMessage: '',
      DIALOG_TYPE,
    }
  },

  created() {
    this.getModules()
    this.init()
  },

  computed: {
    filteredModulesList() {
      return (this.formModules || []).filter(modules => {
        let { custom, name } = modules
        return ['workorder', 'workpermit'].includes(name) || custom
      })
    },
  },

  methods: {
    init() {
      let {
        formModuleName = null,
        formId = null,
        dialogType,
      } = this.transitionObj

      if (dialogType === DIALOG_TYPE.FORM) {
        if (!isEmpty(formModuleName)) {
          this.selectedFormModule = formModuleName
          this.getForms()
          this.selectedFormId = !isEmpty(formId) ? formId : null
        } else {
          this.selectedFormId = null
        }
      }
    },

    async getModules() {
      this.isModuleLoading = true
      try {
        let params = { moduleName: this.module }
        let { error, data } = await API.get('v2/forms/subFormModules', params)

        if (!error) {
          this.formModules = data?.modules || []
        }
      } catch (error) {
        this.formModules = []
      }
      this.isModuleLoading = false
    },

    async getForms() {
      this.isFormLoading = true
      try {
        let params = {
          moduleName: this.selectedFormModule,
          skipTemplatePermission: true,
        }
        let { error, data } = await API.get('v2/forms', params)

        if (!error) {
          this.relatedModuleForms = data?.forms || []
        }
      } catch (error) {
        this.relatedModuleForms = []
      }
      this.isFormLoading = false
    },

    getModuleForm() {
      this.selectedFormId = null
      this.getForms()
    },

    close() {
      this.selectedFormModule = null
      this.selectedFormId = null
      this.$emit('close')
    },

    saveForm() {
      if (!isEmpty(this.selectedFormId)) {
        this.$set(this.transitionObj, 'dialogType', DIALOG_TYPE.FORM)
        this.$set(this.transitionObj, 'formId', this.selectedFormId)
        this.$set(this.transitionObj, 'formModuleName', this.selectedFormModule)
        this.$emit('save')
        this.close()
      }
    },
  },
}
</script>
