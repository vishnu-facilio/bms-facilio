<template>
  <el-dialog
    title="Select user filter"
    :visible.sync="visibility"
    width="25%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    @keydown.esc="closeDialog"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <div class="height330 overflow-y-scroll pB50">
      <div class="">
        <div
          class="pT10 pB10"
          v-for="(moduleField, moduleFieldIdx) in moduleFields"
          :key="moduleFieldIdx"
        >
          <el-checkbox
            @change="addField(moduleField.fieldId, $event)"
            v-model="selectedFields[moduleField.fieldId]"
            >{{
              moduleField.displayName
                ? moduleField.displayName
                : moduleField.name
            }}</el-checkbox
          >
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button
        @click="addUserFilter()"
        type="primary"
        class="btn-green-full"
        >{{ $t('common._common.add') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import modularUserFilter from 'src/pages/report/mixins/modularUserFilter'
export default {
  props: ['visibility', 'moduleName', 'existingConfig'],
  mixins: [modularUserFilter],
  data() {
    return {
      selectedFields: {},
      moduleFields: [],
      moduleOperators: [],
      fieldConfigs: {},
    }
  },
  watch: {
    existingConfig: {
      handler: function(oldValue, newValue) {
        if (typeof this.existingConfig !== 'undefined') {
          if (this.existingConfig === null) {
            this.resetUserFilters()
          } else {
            this.fieldConfigs = {}
            for (let config of this.existingConfig) {
              this.$set(this.selectedFields, config.fieldId, true)
              this.$set(this.fieldConfigs, config.fieldId, config)
            }
          }
        }
      },
      deep: true,
    },
    moduleName: {
      handler: function(oldValue, newValue) {
        if (oldValue !== newValue) {
          this.render()
        }
      },
      immediate: true,
    },
  },
  methods: {
    addField(fieldId) {
      if (this.selectedFields[fieldId]) {
        let facilioField = this.moduleFields.filter(
          field => field.fieldId === fieldId
        )
        if (facilioField.length !== 0) {
          if (
            typeof this.existingConfig !== 'undefined' &&
            this.existingConfig !== null
          ) {
            let field = this.existingConfig[fieldId]
            if (field) {
              this.$set(this.fieldConfigs, field.fieldId, field)
            } else {
              this.getDefaultSettingsForField(facilioField[0], this.moduleName)
            }
          } else {
            this.getDefaultSettingsForField(facilioField[0], this.moduleName)
          }
        } else {
          console.log('Field ID not found.')
        }
      } else {
        delete this.fieldConfigs[fieldId]
      }
    },
    addUserFilter() {
      for (let field in this.fieldConfigs) {
        let config = this.fieldConfigs[field]
        config.chooseValue.values = []
        for (let val of config.allValues) {
          config.chooseValue.values.push(Object.keys(val)[0])
        }
      }
      this.$emit('setFilter', Object.values(this.fieldConfigs))
    },
    resetUserFilters() {
      for (let fieldId in this.selectedFields) {
        this.$set(this.selectedFields, fieldId, false)
      }
      this.fieldConfigs = {}
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    render() {
      this.moduleFields = []
      this.$http
        .get('/module/meta?moduleName=' + this.moduleName)
        .then(response => {
          this.moduleOperators = response.data.meta.operators
          for (let fieldObj of response.data.meta.fields) {
            if (fieldObj.dataType === 7) {
              this.moduleFields.push(fieldObj)
            }
          }
          if (
            typeof this.moduleName !== 'undefined' &&
            typeof this.existingConfig !== 'undefined' &&
            this.existingConfig !== null
          ) {
            for (let fieldConfig of this.existingConfig) {
              this.$set(this.selectedFields, fieldConfig.fieldId, true)
            }
            for (let config of this.existingConfig) {
              this.$set(this.fieldConfigs, config.fieldId, config)
            }
          }
        })
    },
  },
  created() {
    // this.render()
  },
}
</script>

<style></style>
