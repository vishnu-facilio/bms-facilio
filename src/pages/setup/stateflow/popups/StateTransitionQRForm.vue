<template>
  <el-dialog
    :visible="true"
    width="40%"
    :title="$t('stateflow.transition.configure_field')"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="close"
    :close-on-click-modal="false"
  >
    <div class="pB50">
      <p class="fc-input-label-txt txt-color">
        {{ $t('stateflow.transition.choose_field_map_qr') }}
      </p>
      <el-row class="mB30">
        <el-col :span="20" v-if="loading">
          <span class="stateflow-transition-qr-line loading-shimmer"></span>
        </el-col>
        <el-col :span="20" v-else>
          <el-cascader
            v-model="selectedQrField"
            :options="fieldOptions"
            :props="{
              expandTrigger: 'hover',
              value: 'fieldId',
              label: 'displayName',
              children: 'fields',
            }"
            :placeholder="$t('stateflow.transition.select_field')"
            class="fc-input-full-border2 width100"
            popper-class="sla-duration-field"
          ></el-cascader>
        </el-col>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button @click="close" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="saveForm">
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
  props: ['module', 'activeQRField'],
  data() {
    return {
      fieldOptions: [],
      selectedQrField: [],
      loading: false,
    }
  },
  async created() {
    if (!isEmpty(this.activeQRField)) {
      this.selectedQrField = [...this.activeQRField]
    }
    await this.loadPlaceholderFieldOptions()
  },
  methods: {
    async loadPlaceholderFieldOptions() {
      this.loading = true
      let { data, error } = await API.get(`v3/placeholders/${this.module}`)

      if (!error) {
        let { placeholders } = data || {}
        let { fields, moduleFields } = placeholders || {}

        if (!isEmpty(fields)) {
          let fieldTypeFilter = fld =>
            !isEmpty(fld.fieldId) && ['STRING'].includes(fld.dataType)
          let lookupModuleFields = {}

          if (!isEmpty(moduleFields)) {
            Object.keys(moduleFields).forEach(fieldName => {
              lookupModuleFields[fieldName] = (
                moduleFields[fieldName] || []
              ).filter(fieldTypeFilter)
            })
          }
          fields.forEach(field => {
            let { module: lookupModuleName } = field || {}
            if (lookupModuleName) {
              field.fields = lookupModuleFields[lookupModuleName] || []
            }
          })

          this.fieldOptions = fields.filter(
            fld => fieldTypeFilter(fld) || !isEmpty(fld.fields)
          )
        }
      }
      this.loading = false
    },
    saveForm() {
      let { selectedQrField } = this

      if (!isEmpty(selectedQrField)) {
        let [qrFieldId = -99, qrLookupFieldId = -99] = selectedQrField

        this.$emit('onSave', { qrFieldId, qrLookupFieldId })
        this.close()
      }
    },
    close() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss" scoped>
.stateflow-transition-qr-line {
  height: 40px;
  border-radius: 5px;
  width: 100%;
}
</style>
