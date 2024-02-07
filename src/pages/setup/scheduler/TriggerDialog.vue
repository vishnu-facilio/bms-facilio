<template>
  <el-dialog
    :visible="true"
    width="50%"
    :title="$t('common.header.trigger_conditions')"
    class="fieldchange-Dialog pB15 fc-dialog-center-container schedule-invoke-trigger"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <ErrorBanner :error.sync="error" :errorMessage="errorMessage"></ErrorBanner>

    <div class="height330 overflow-y-scroll pB50">
      <el-form ref="invoke-trigger" :rules="rules" :model="triggerData">
        <el-form-item prop="moduleName">
          <el-row>
            <el-col :span="6">
              <p class="fc-input-label-txt line-height40 pB0">
                {{ $t('common._common.module_name') }}
              </p>
            </el-col>
            <el-col :span="12">
              <el-select
                :placeholder="$t('common.products.select_module_name')"
                class="fc-input-full-border-select2 width100"
                v-model="triggerData.moduleName"
                filterable
                @change="resetTrigger"
              >
                <el-option
                  v-for="moduleList in automationModulesList"
                  :key="moduleList.name"
                  :label="moduleList.displayName"
                  :value="moduleList.name"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item prop="triggerId">
          <el-row>
            <el-col :span="6">
              <p class="fc-input-label-txt line-height40 pB0">
                {{ $t('common.wo_report.trigger') }}
              </p>
            </el-col>
            <el-col :span="12">
              <FieldLoader v-if="loading" :isLoading="loading"></FieldLoader>

              <el-select
                v-else
                class="fc-input-full-border-select2 width100"
                v-model="triggerData.triggerId"
                filterable
                :disabled="$validation.isEmpty(triggerData.moduleName)"
                :placeholder="$t('common.products.select_triggers')"
              >
                <el-option
                  v-for="trigger in triggersList"
                  :key="trigger.id"
                  :label="trigger.name"
                  :value="trigger.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>

      <NewCriteriaBuilder
        class="stateflow-criteria relative"
        ref="criteriaBuilder"
        v-model="criteria"
        :exrule="criteria"
        @condition="newValue => (criteria = newValue)"
        :module="triggerData.moduleName"
        :isRendering.sync="criteriaRendered"
        :hideTitleSection="true"
        :disable="$validation.isEmpty(triggerData.moduleName)"
      ></NewCriteriaBuilder>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel">
        {{ $t('common._common.cancel') }}
      </el-button>
      <el-button type="primary" @click="submitForm()" class="modal-btn-save">
        {{ $t('common._common._save') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import ErrorBanner from '@/ErrorBanner'
import FieldLoader from '@/forms/FieldLoader'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { API } from '@facilio/api'

export default {
  props: ['selectedTrigger'],
  components: { NewCriteriaBuilder, ErrorBanner, FieldLoader },

  data() {
    return {
      triggerData: { moduleName: null, triggerId: null },
      triggersList: [],
      loading: false,
      criteria: null,
      error: false,
      errorMessage: '',
      criteriaRendered: false,
      rules: {
        moduleName: {
          required: true,
          message: this.$t('common.header.please_select_module_name'),
          trigger: 'change',
        },
        triggerId: {
          required: true,
          message: this.$t('common.header.please_select_trigger'),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    if (!isEmpty(this.selectedTrigger)) {
      let { moduleName, criteria, triggerId } = this.selectedTrigger

      this.triggerData = { moduleName, triggerId }
      this.criteria = criteria
      this.loadTriggers()
    }
  },

  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),

    automationModulesList() {
      return this.getAutomationModulesList()
    },
  },

  methods: {
    resetTrigger() {
      this.triggerData.triggerId = null
      this.loadTriggers()
    },
    async loadTriggers() {
      this.loading = true

      let { moduleName } = this.triggerData
      let { error, data } = await API.post('v3/trigger/list', { moduleName })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.triggersList = data.triggerList || []
      }
      this.loading = false
    },
    serializeCriteria(criteria) {
      for (let condition of Object.keys(criteria.conditions)) {
        let hasValidFieldName = !isEmpty(
          criteria.conditions[condition].fieldName
        )

        if (!hasValidFieldName) {
          delete criteria.conditions[condition]
        } else {
          let discardKeys = [
            'valueArray',
            'operatorsDataType',
            'operatorLabel',
            'operator',
          ]

          discardKeys.forEach(key => {
            delete criteria.conditions[condition][key]
          })
        }
      }
      if (criteria && criteria.conditions) {
        if (Object.keys(criteria.conditions).length === 0) {
          criteria = null
        }
      }
      return criteria
    },
    submitForm() {
      this.$refs['invoke-trigger'].validate(async valid => {
        if (!valid) return false

        let criteria = { ...this.criteria }
        let activeCriteria = this.serializeCriteria(criteria)

        if (!isEmpty(activeCriteria)) {
          let { moduleName, triggerId } = this.triggerData

          this.$emit('onSave', {
            invokeTriggerType: 'criteria',
            moduleName,
            triggerId,
            criteria: activeCriteria,
          })
          this.closeDialog()
        } else {
          this.error = true
          this.errorMessage = this.$t('common.header.please_select_fields')
          return
        }
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.schedule-invoke-trigger {
  .disable-overlay {
    height: calc(100vh - 600px);
  }
  .el-form-item__error {
    margin-left: 25%;
  }
}
</style>
