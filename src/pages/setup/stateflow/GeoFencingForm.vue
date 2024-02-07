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
    <el-form
      class="pB50 geo-fenching-form"
      :model="ruleForm"
      :rules="rule"
      label-position="top"
      ref="ruleForm"
    >
      <el-form-item
        :label="$t('setup.bim.bim_location')"
        prop="selectedGeoField"
        class="txt-color"
      >
        <div v-if="loading">
          <span class="stateflow-transition-qr-line loading-shimmer"></span>
        </div>
        <div v-else>
          <el-cascader
            v-model="ruleForm.selectedGeoField"
            :options="fieldOptions"
            :props="{
              expandTrigger: 'hover',
              value: 'fieldId',
              label: 'displayName',
              children: 'fields',
            }"
            :placeholder="$t('stateflow.transition.select_field')"
            class="fc-input-full-border2 width100"
          ></el-cascader>
        </div>
      </el-form-item>
      <el-form-item
        class="txt-color"
        :label="$t('setup.stateflow.range')"
        prop="range"
      >
        <el-input
          v-model="ruleForm.range"
          class="fc-input-full-border2 width40"
          :placeholder="$t('setup.stateflow.enter_range')"
          type="number"
        >
          <template slot="append">m</template>
        </el-input>
      </el-form-item>
      <div class="modal-dialog-footer">
        <el-button @click="close" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveForm('ruleForm')"
        >
          {{ $t('common._common._save') }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['module', 'selectedGeoFields'],
  data() {
    return {
      loading: false,
      fieldOptions: [],
      ruleForm: {
        selectedGeoField: [],
        range: null,
      },
      rule: {
        selectedGeoField: [
          {
            required: true,
            message: this.$t('setup.stateflow.please_select_field'),
            trigger: 'change',
          },
        ],
        range: [
          {
            required: true,
            message: this.$t('setup.stateflow.please_select_range'),
            validator: (rule, value, callback) => {
              if (isEmpty(value) || value < 1) {
                return callback(
                  new Error(this.$t('setup.stateflow.please_select_range'))
                )
              } else callback()
            },
            trigger: 'blur',
          },
        ],
      },
    }
  },
  async created() {
    if (!isEmpty(this.selectedGeoFields)) {
      let { activeGeoField, range } = this.selectedGeoFields

      this.ruleForm = { selectedGeoField: [...activeGeoField], range: range }
    }
    await this.loadPlaceholderFieldOptions()
  },
  methods: {
    async loadPlaceholderFieldOptions() {
      this.loading = true
      let { data, error } = await API.get(
        `v2/getFields/geoLocation/${this.module}`
      )

      if (!isEmpty(error)) {
        this.loading = false
        this.$message.error(error.message)
        return
      }

      let { fields, moduleFields } = data || {}

      this.fieldOptions = (fields || [])
        .filter(field => {
          let { module: lookupModuleName, dataType } = field || {}
          let moduleFieldList = moduleFields[lookupModuleName] || null
          let isLookupField = dataType === 'LOOKUP'
          let isValidLookupField =
            isLookupField &&
            (!isEmpty(moduleFieldList) || lookupModuleName === 'location')
          return !isLookupField || isValidLookupField
        })
        .map(field => {
          let { module: lookupModuleName } = field || {}
          return { ...field, fields: moduleFields[lookupModuleName] }
        })
      this.loading = false
    },
    close() {
      this.$emit('close')
    },
    saveForm(geoFenchingForm) {
      this.$refs[geoFenchingForm].validate(valid => {
        if (valid) {
          let { selectedGeoField, range } = this.ruleForm
          let [
            locationFieldId = -99,
            locationLookupFieldId = -99,
          ] = selectedGeoField
          let radius = !isEmpty(range) ? parseInt(range) : -99

          this.$emit('save', { locationFieldId, locationLookupFieldId, radius })
          this.close()
        } else {
          return false
        }
      })
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
<style lang="scss">
.geo-fenching-form {
  .el-form-item__content {
    line-height: normal;
  }
}
</style>
