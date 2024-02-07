<template>
  <div>
    <div id="details-header" class="section-header">
      {{ $t('common.products.details') }}
    </div>
    <el-form
      :model="details"
      :rules="rules"
      ref="details-form"
      label-width="180px"
      label-position="left"
      class="p50 pT10 pR70 pB30"
      @submit.native.prevent
    >
      <el-row class="mB10">
        <el-col :span="24">
          <el-form-item
            prop="name"
            :label="$t('common.products.name')"
            class="mB10"
          >
            <el-input
              :placeholder="$t('common.products.add_a_name_for_this_approval')"
              v-model="details.name"
              @change="name => $emit('updateTitle', name)"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mB10">
        <el-col :span="24">
          <el-form-item
            prop="description"
            :label="$t('common.wo_report.report_description')"
            class="mB10"
          >
            <el-input
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 4 }"
              class="fc-input-full-border-textarea"
              :placeholder="$t('setup.setupLabel.add_a_decs')"
              v-model="details.description"
              resize="none"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mB10">
        <el-col :span="14">
          <el-form-item
            prop="eventType"
            :label="$t('common._common._execute_on')"
            class="mB10"
          >
            <el-select
              v-model="details.eventType"
              :placeholder="$t('common._common.select')"
              class="width300px fc-input-full-border-select2"
            >
              <el-option
                v-for="(type, index) in activityTypes"
                :key="'activity-' + index"
                :label="type.label"
                :value="type.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <div v-if="details.eventType == 1048576" class="d-flex mT15">
            <div
              :class="
                isFieldsConfigured
                  ? 'configured-green'
                  : 'configure-blue pointer'
              "
              class="new-statetransition-config bold"
              @click="canShowFieldPopup = !canShowFieldPopup"
            >
              {{
                isFieldsConfigured
                  ? $t('common._common.configured')
                  : $t('common._common.configure_fields')
              }}
            </div>
            <div v-if="isFieldsConfigured">
              <i
                class="el-icon-edit pointer edit-icon pL30 txt-color"
                @click="canShowFieldPopup = !canShowFieldPopup"
                :title="$t('common.header.edit_fields')"
                v-tippy
              ></i>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row class="mT30">
        <el-col :span="24">
          <div>
            <label class="fc-modal-sub-title" style="color: #385571">
              {{ $t('common._common._criteria') }}
            </label>
            <div class="fc-sub-title-desc">
              {{ $t('common._common.specify_criteria_for_this_approval') }}
            </div>
          </div>
          <new-criteria-builder
            :hideTitleSection="true"
            class="stateflow-criteria"
            ref="criteriaBuilder"
            v-model="details.criteria"
            :exrule="details.criteria"
            @condition="newValue => updateCriteria(newValue)"
            :module="moduleName"
            :isRendering.sync="isCriteriaRendering"
          ></new-criteria-builder>
        </el-col>
      </el-row>
    </el-form>
    <FieldSelectionDialog
      v-if="canShowFieldPopup"
      :moduleName="moduleName"
      :availableFields="moduleFields"
      :selectedFields="selectedFieldsList"
      @save="saveFields"
      @close="canShowFieldPopup = false"
    >
      <template #description>
        {{ $t('common.products.select_fields_based_approval_rule') }}
      </template>
    </FieldSelectionDialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import FieldSelectionDialog from './FieldSelectionDialog'

const activityTypes = [
  {
    label: 'Create',
    value: 1,
  },
  {
    label: 'Field Change',
    value: 1048576,
  },
]

export default {
  name: 'ApprovalDetails',
  props: {
    policy: {
      type: Object,
    },
    moduleFields: {
      type: Array,
    },
  },
  components: { NewCriteriaBuilder, FieldSelectionDialog },
  data() {
    return {
      details: {
        name: '',
        description: '',
        siteId: -1,
        eventType: 1,
        criteria: null,
        fieldIds: [],
      },
      rules: {},
      canShowFieldPopup: false,
      isCriteriaRendering: false,
      canWatchForChanges: false,
    }
  },
  created() {
    this.activityTypes = activityTypes
  },
  computed: {
    id() {
      return this.$route.params.id
    },
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    moduleName() {
      return this.$route.params.moduleName
    },
    isFieldsConfigured() {
      return !isEmpty(this.details.fieldIds)
    },
    selectedFieldsList: {
      get() {
        let { details, moduleFields } = this
        let { fieldIds } = details
        let fields = (fieldIds || [])
          .map(id => (moduleFields || []).find(({ fieldId }) => fieldId === id))
          .filter(field => !isEmpty(field))

        return fields
      },
      set(fields) {
        this.$set(
          this.details,
          'fieldIds',
          fields
            .map(field => field.fieldId)
            .filter(fieldId => !isEmpty(fieldId))
        )
      },
    },
  },
  watch: {
    policy: {
      handler: function() {
        this.init()
      },
      immediate: true,
    },
    details: {
      handler() {
        if (this.canWatchForChanges && !this.isCriteriaRendering)
          this.$emit('modified')
      },
      deep: true,
    },
  },
  methods: {
    init() {
      if (this.isNew) return

      let {
        id,
        name,
        description,
        criteria,
        criteriaId,
        fieldIds,
        eventType,
      } = this.policy

      this.details = {
        ...this.details,
        id,
        name,
        description,
        criteria,
        criteriaId,
        fieldIds,
        eventType,
      }
      this.$nextTick(() => (this.canWatchForChanges = true))
    },
    updateCriteria(value) {
      this.$set(this.details, 'criteria', value)
    },
    saveFields(fields) {
      this.selectedFieldsList = fields
      this.canShowFieldPopup = false
    },
    serializeCriteria(criteria) {
      for (let condition of Object.keys(criteria.conditions)) {
        let hasValidFieldName =
          criteria.conditions[condition].hasOwnProperty('fieldName') &&
          !isEmpty(criteria.conditions[condition].fieldName)
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
    serialize() {
      let {
        id,
        name,
        description,
        criteria,
        criteriaId = null,
        eventType,
        fieldIds,
      } = clone(this.details)
      let data = {
        name,
        description,
        criteria: !isEmpty(criteria) ? this.serializeCriteria(criteria) : null,
        eventType,
        fieldIds: eventType === 1 ? [] : fieldIds,
      }

      if (!this.isNew) data.id = id
      if (!isEmpty(criteria) && !isEmpty(criteriaId))
        data.criteriaId = criteriaId

      return data
    },
  },
}
</script>
