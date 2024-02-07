<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog70 setup-dialog fc-setup-rightSide-dialog-scroll"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{
            !isNew
              ? $t('rule.create.edit_impact')
              : $t('rule.create.new_impact')
          }}
        </div>
      </div>
    </div>
    <div v-if="isLoading" class="mT20">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <el-row>
        <el-form
          ref="impactForm"
          :model="impactDetails"
          label-width="150px"
          label-position="left"
          class="p50 pT10 pB30"
        >
          <div class="section-container flex-container">
            <div class="form-one-column fc-label-required">
              <el-form-item
                :label="`${this.$t('rule.create.impact_name')}`"
                prop="name"
              >
                <el-input
                  v-model="impactDetails.name"
                  class="fc-input-full-border2"
                  :placeholder="`${this.$t('rule.create.impact_name')}`"
                  :autofocus="true"
                ></el-input>
              </el-form-item>
            </div>
            <div class="form-one-column">
              <el-form-item
                :label="`${this.$t('common.roles.description')}`"
                prop="description"
              >
                <el-input
                  v-model="impactDetails.description"
                  type="textarea"
                  class="mT3 fc-input-full-border-textarea"
                  :autofocus="true"
                  :min-rows="2"
                  :autosize="{ minRows: 3, maxRows: 4 }"
                  :placeholder="`${this.$t('common.roles.description')}`"
                ></el-input>
              </el-form-item>
            </div>
            <div class="form-one-column  fc-label-required">
              <el-form-item
                :label="`${this.$t('alarm.alarm.asset_category')}`"
                prop="assetCategoryId"
              >
                <FLookupField
                  :key="`rules-assetcategory`"
                  :model.sync="impactDetails.assetCategoryId"
                  :field="assetCategoryField"
                  :isRemoteField="false"
                ></FLookupField>
              </el-form-item>
            </div>
          </div>
        </el-form>
      </el-row>
      <el-row>
        <FormulaBuilder
          @setCondition="setCondition"
          :isEditForm="!isNew"
          :ruleCondition="fieldDetails"
          :removeToggle="'true'"
          :preferredExpressionType="'code_view'"
          :preferredCodeBoilerPlate="preferredCodeBoilerPlate"
          :moduleName="moduleName"
          isMainApp="true"
        />
      </el-row>
    </template>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        type="primary"
        @click="saveImpact"
        :loading="impactSaving"
        class="modal-btn-save"
        >{{
          impactSaving
            ? $t('common._common._saving')
            : $t('common._common._save')
        }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import FormulaBuilder from 'src/pages/alarm/rule-creation/component/FormulaBuilder.vue'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import FLookupField from '@/forms/FLookupField'
import { API } from '@facilio/api'
import { deepCloneObject } from 'util/utility-methods'
import {
  validateRule,
  validateFields,
} from 'pages/alarm/rule-creation/ruleValidation'

export default {
  components: { FormulaBuilder, FLookupField },
  props: ['isNew', 'visibility', 'selectedImpact'],
  data() {
    return {
      moduleName: 'faultImpact',
      impactSaving: false,
      impactDetails: {
        name: '',
        description: '',
        type: 1,
        assetCategoryId: null,
      },
      condition: {},
      assetCategoryField: {
        lookupModuleName: 'assetcategory',
        lookupModule: {
          type: -1,
        },
      },
      isLoading: false,
      preferredCodeBoilerPlate:
        'Map calculateImpact(){\n    m={};\n    m.costImpact=0;\n    m.energyImpact=0;\n   \n    return m;\n}',
    }
  },
  computed: {
    ...mapState({
      alarmSeverity: state => state.alarmSeverity,
      metaInfo: state => state.view.metaInfo,
    }),
    faultTypes() {
      let faultTypeArr = []
      let { metaInfo } = this
      let { fields } = metaInfo || {}
      if (!isEmpty(fields)) {
        let faultField = this.metaInfo.fields.find(
          field => field.displayName === 'faultType'
        )
        let { enumMap } = faultField || {}
        faultTypeArr = enumMap || []
      }
      return faultTypeArr
    },
    fieldDetails() {
      let { impactDetails, isNew } = this
      if (!isNew) {
        let { fields = [], workflow } = impactDetails || {}
        fields.forEach(field => {
          let { resource } = field || {}
          let { id: resourceId } = resource || {}

          field.resourceId = resourceId
        })
        let workflowContext = deepCloneObject(workflow)
        let conditionObj = { ...impactDetails, ns: { fields, workflowContext } }

        return conditionObj
      }
      return { ...impactDetails, ns: { fields: [], workflowContext: {} } }
    },
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')

    if (!this.isNew && !isEmpty(this.selectedImpact)) {
      this.prefillImpact()
    }
  },
  methods: {
    async prefillImpact() {
      this.isLoading = true
      let { selectedImpact } = this
      let { id: selectedImpactId } = selectedImpact || {}

      let url = 'v3/modules/data/summary'
      let params = { id: selectedImpactId, moduleName: 'faultImpact' }
      let { error, data } = await API.get(url, params)

      if (error) {
        this.$message.error('Error occured')
      } else {
        let { faultImpact } = data || {}
        let { assetCategory } = faultImpact || {}
        let { id: assetCategoryId } = assetCategory || {}

        faultImpact = { ...faultImpact, assetCategoryId }

        this.impactDetails = faultImpact
      }
      this.isLoading = false
    },
    async saveImpact() {
      this.impactSaving = true
      let { impactDetails, condition, isNew } = this
      let { ns } = condition || {}
      let { fields = [], workflowContext } = ns || {}

      let workflowV2String = this.$getProperty(
        workflowContext,
        'workflowV2String',
        ''
      )

      let { assetCategoryId } = impactDetails || {}
      if (!isEmpty(assetCategoryId)) {
        let assetCategory = { id: assetCategoryId }
        impactDetails = { ...impactDetails, assetCategory }
        delete impactDetails['assetcategory']
      }

      fields = fields.map(field => {
        let { resourceId } = field || {}
        let resource = { id: resourceId }

        field = { ...field, resource }
        delete (field || {}).moduleId
        delete (field || {}).resourceId
        return field
      })

      let workflow = { workflowV2String }
      if (!isNew) {
        let { id } = workflowContext || {}
        workflow = { ...workflow, id }
      }
      let conditionObj = { fields, workflow }
      let dataObj = { ...impactDetails, ...conditionObj }
      let isValidRule = validateRule(dataObj, 'faultImpact')
      let isValidFormula = validateFields(dataObj, 'faultImpact')

      if (isValidRule && isValidFormula) {
        let params = {
          id: dataObj.id,
          data: dataObj,
          moduleName: 'faultImpact',
        }
        let promise
        if (!isNew) {
          promise = await API.post('v3/modules/data/update', params)
        } else {
          promise = await API.post('v3/modules/data/create', params)
        }
        let { data, error } = await promise

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          if (!isEmpty(data)) {
            this.$emit('impactSaved')
            this.closeDialog()
          }
        }
      } else {
        if (!isValidFormula) {
          this.$message.error(this.$t('rule.create.variables_must_be_unique'))
        } else {
          this.$message.error(this.$t('rule.create.fill_mandatory_fields'))
        }
      }
      this.impactSaving = false
    },
    setCondition(condition) {
      this.condition = condition
    },
    closeDialog() {
      this.$emit('close')
      this.$emit('update:visibility', false)
    },
  },
}
</script>
