<template>
  <div>
    <el-dialog
      title="Subform Modules List"
      :visible.sync="canShowSubFormsModulesDialog"
      width="30%"
      class="fc-dialog-center-container"
      :before-close="closeDialogBox"
      :append-to-body="true"
    >
      <ErrorBanner
        :error.sync="error"
        :errorMessage.sync="errorMessage"
      ></ErrorBanner>
      <div class="height250">
        <el-form ref="subFormModules" :rules="rules" :model="subFormModule">
          <el-form-item
            label="Type"
            prop="selectedModuleName"
            class="mB20"
            :required="true"
          >
            <el-select
              placeholder="Select"
              v-model="subFormModule.selectedModuleName"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(module, index) in subFormsModules"
                :key="`${index} ${module.name}`"
                :label="module.displayName"
                :value="module.name"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialogBox" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          :loading="isSaving"
          type="primary"
          class="modal-btn-save"
          @click="addSubForm()"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'

import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    ErrorBanner,
  },
  props: [
    'openSubformDialog',
    'subFormsModules',
    'formId',
    'activeSection',
    'activeSectionIndex',
  ],
  computed: {
    canShowSubFormsModulesDialog: {
      get() {
        return this.openSubformDialog
      },
      set(value) {
        this.$emit('update:openSubformDialog', value)
      },
    },
  },
  data() {
    return {
      canRemoveSection: true,
      error: false,
      errorMessage: null,
      rules: {
        selectedModuleName: [
          {
            required: true,
            message: 'Please select module',
            trigger: 'change',
          },
        ],
      },
      subFormModule: {
        selectedModuleName: null,
      },
      isSaving: false,
    }
  },
  methods: {
    closeDialogBox() {
      let { activeSectionIndex, activeSection } = this
      this.canShowSubFormsModulesDialog = false
      if (this.canRemoveSection) {
        this.$emit('removeSection', activeSection, activeSectionIndex)
      }
    },
    addSubForm() {
      this.$refs['subFormModules'].validate(valid => {
        if (valid) {
          let { subFormModule, formId } = this
          let { selectedModuleName: moduleName } = subFormModule
          let url = `v2/forms/addSubForm`
          let data = {
            moduleName,
            form: {
              displayName: `${moduleName} ${formId}`,
            },
            parentFormId: formId,
          }
          this.$set(this, 'isSaving', true)
          let promise = this.$http
            .post(url, data)
            .then(({ data: { message, responseCode, result = {} } }) => {
              if (responseCode === 0) {
                let { form } = result
                if (!isEmpty(form)) {
                  this.$set(this, 'canRemoveSection', false)
                  this.$set(this, 'canShowSubFormsModulesDialog', false)
                  this.$emit('updateSubFormSection', form)
                }
              } else {
                throw new Error(message)
              }
            })
            .catch(({ message }) => {
              this.$message.error(message)
            })
          Promise.all([promise]).finally(() => {
            this.$set(this, 'isSaving', false)
          })
        }
      })
    },
  },
}
</script>
<style lang="scss"></style>
