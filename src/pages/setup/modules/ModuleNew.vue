<template>
  <div>
    <el-dialog
      :title="isEdit ? 'EDIT MODULE' : 'NEW MODULE'"
      :visible.sync="canShowDialog"
      width="30%"
      class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <ErrorBanner
        :error.sync="error"
        :errorMessage.sync="errorMessage"
      ></ErrorBanner>
      <div class="height330">
        <el-form ref="moduleNew" :rules="rules" :model="moduleObj">
          <el-form-item
            label="Name"
            prop="displayName"
            class="mB10"
            :required="true"
          >
            <el-input
              :placeholder="$t('common._common.enter_name')"
              v-model="moduleObj.displayName"
              class="fc-input-full-border-select2"
            ></el-input>
          </el-form-item>
          <el-form-item
            label="Description"
            prop="description"
            class="mB10"
            :required="true"
          >
            <el-input
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              :placeholder="$t('common._common.enter_name')"
              v-model="moduleObj.description"
              class="fc-input-full-border-select2"
            ></el-input>
          </el-form-item>
          <el-form-item class="mB10">
            <el-checkbox v-model="moduleObj.stateFlowEnabled"
              >Enable Stateflow</el-checkbox
            >
          </el-form-item>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeModuleCreation" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="addNewModule()"
          :loading="isSaving"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ErrorBanner from '@/ErrorBanner'
import http from 'util/http'

export default {
  components: {
    ErrorBanner,
  },
  props: {
    canShowModuleCreation: {
      type: Boolean,
      required: true,
    },
    moduleObj: {
      type: Object,
    },
    isEdit: {
      type: Boolean,
    },
    updateModule: {
      type: Function,
    },
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canShowModuleCreation
      },
      set(value) {
        this.$emit('update:canShowModuleCreation', value)
      },
    },
  },
  data() {
    return {
      error: false,
      errorMessage: null,
      rules: {
        displayName: [
          {
            required: true,
            message: 'Please input module name',
            trigger: 'change',
          },
        ],
        description: [
          {
            required: true,
            message: 'Please input module description',
            trigger: 'change',
          },
        ],
      },
      isSaving: false,
    }
  },
  methods: {
    addNewModule() {
      let { isEdit } = this
      this.setFormValid()
      this.$refs['moduleNew'].validate(valid => {
        if (valid) {
          let {
            moduleObj: { displayName, description, stateFlowEnabled, name },
          } = this
          let data = {
            moduleDisplayName: displayName,
            description,
            stateFlowEnabled,
          }
          if (isEdit) {
            data.moduleName = name
          }
          this.saveRecord(data)
        }
      })
    },
    setFormValid() {
      this.error = false
      this.errorMessage = ''
    },
    setFormInvalid(msg) {
      this.error = true
      this.errorMessage = msg
    },
    closeModuleCreation() {
      this.canShowDialog = false
    },
    saveRecord(data) {
      let { isEdit } = this
      let url = isEdit ? '/v2/module/update' : '/v2/module/add'
      this.isSaving = true
      let promise = http
        .post(url, data)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let {
              module: { name: moduleName },
              module: moduleObj,
            } = result
            if (isEdit) {
              this.updateModule && this.updateModule(moduleObj)
              this.closeModuleCreation()
              this.$message.success('Module updated successfully')
            } else {
              let {
                form: { id },
              } = result || {}
              let currentPath = this.$router.resolve({
                name: 'modules-details',
                params: { moduleName },
              }).href

              this.$router.push({
                path: `${currentPath}/layouts/${id}/edit`,
              })
            }
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.setFormInvalid(message)
        })
      Promise.all([promise]).finally(() => (this.isSaving = false))
    },
  },
}
</script>

<style></style>
