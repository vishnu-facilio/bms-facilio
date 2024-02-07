<template>
  <el-form
    :model="details"
    :rules="rules"
    ref="grouping-form"
    label-position="left"
    label-width="100px"
  >
    <div class="flex-middle mB10">
      <el-form-item
        prop="groupByFieldId"
        :label="$t('common.header.group_by')"
        class="mB10"
      >
        <el-select
          v-model="details.groupByFieldId"
          placeholder="Select the group"
          class="width250px fc-input-full-border-select2"
          filterable
          @change="loadGroupCriteriaOptions"
        >
          <el-option
            v-for="groupFld in groupingFieldList"
            :key="groupFld.id"
            :label="groupFld.displayName"
            :value="groupFld.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <div class="flex-middle">
        <el-form-item
          :label="$t('setup.scheduler.apply_filter')"
          class="mB10 mL40"
        >
          <el-switch
            v-model="filterToggle"
            :disabled="$validation.isEmpty(selectedGroupObj)"
          ></el-switch>
        </el-form-item>
        <el-tooltip
          placement="top"
          :content="$t('setup.scheduler.all_values_selected')"
        >
          <i class="el-icon-info mT4 mL5 mB10"></i>
        </el-tooltip>
      </div>
    </div>
    <el-form-item
      v-if="!isPickList && filterToggle"
      :label="$t('setup.scheduler.based_on')"
    >
      <el-radio v-model="basedOn" class="fc-radio-btn" label="criteria">
        {{ $t('setup.users_management.criteria') }}
      </el-radio>
      <el-radio v-model="basedOn" class="fc-radio-btn" label="value">
        {{ $t('setup.users_management.value') }}
      </el-radio>
    </el-form-item>
    <CriteriaBuilder
      v-if="showGroupCriteria"
      v-model="groupCriteria"
      :moduleName="moduleName"
      ref="group-criteria"
    />
    <div
      v-if="filterToggle && basedOn === 'value'"
      class="flex-middle pT8 pB8"
      style="margin:0.5rem;"
    >
      <div class="criteria-alphabet-block">
        <div class="alphabet-circle">
          {{ 1 }}
        </div>
      </div>
      <el-input
        class="fc-input-full-border2"
        v-model="selectedObjDisplayName"
        :disabled="true"
      ></el-input>
      <el-select
        v-model="valueType.operatorId"
        placeholder="Select"
        class="pL10 fc-input-full-border2 width100"
      >
        <el-option
          v-for="item in valueOperator"
          :key="item.operatorId"
          :label="item.displayName"
          :value="item.operatorId"
        ></el-option>
      </el-select>
      <el-form-item class="timeline-view-values-field width100 height40">
        <el-select
          v-model="groupingValues"
          placeholder="Select"
          class="pL10 fc-input-full-border2 width100"
          :disabled="$validation.isEmpty(valueType.operatorId)"
          filterable
          remote
          :remote-method="query => loadGroupCriteriaOptions(query, true)"
          :loading="valuesLoading"
          multiple
          collapse-tags
        >
          <el-option
            v-for="item in valueOptions"
            :key="parseInt(item.id)"
            :label="item.value"
            :value="parseInt(item.id)"
          ></el-option>
        </el-select>
      </el-form-item>
    </div>
  </el-form>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { dataTypes } from './schedulerViewUtil'
import { CriteriaBuilder } from '@facilio/criteria'
import { getFieldOptions } from 'util/picklist'

const valueOperator = [
  { displayName: 'is', operatorId: 3 },
  { displayName: "isn't", operatorId: 4 },
]

export default {
  props: ['viewDetails', 'isNew', 'moduleFields', 'saveAsNew', 'moduleId'],
  components: { CriteriaBuilder },
  data() {
    return {
      basedOn: 'criteria',
      groupingValues: [],
      valuesLoading: false,
      refreshGroupCriteria: true,
      valueType: { operatorId: null },
      details: { groupByFieldId: null, groupCriteria: null },
      rules: {
        groupByFieldId: {
          required: true,
          message: 'Please select grouping field',
          trigger: 'blur',
        },
      },
      filterToggle: false,
      moduleName: null,
      valueOperator,
      valueOptions: null,
      groupCriteria: null,
    }
  },
  async created() {
    if (!this.isNew || this.saveAsNew) {
      await this.deserialize()
    }
  },
  watch: {
    groupCriteria(newVal) {
      this.$set(this.details, 'groupCriteria', newVal)
    },
    basedOn(newVal) {
      if (newVal === 'value') {
        this.$set(this, 'groupCriteria', null)
      }
    },
    filterToggle(newVal) {
      if (!newVal) this.$set(this, 'groupCriteria', null)
    },
  },
  computed: {
    showGroupCriteria() {
      let { filterToggle, basedOn, refreshGroupCriteria } = this
      return filterToggle && basedOn === 'criteria' && refreshGroupCriteria
    },
    groupingFieldList() {
      let { LOOKUP, ENUM, SYSTEM_ENUM } = dataTypes
      return (this.moduleFields || []).filter(fld =>
        [LOOKUP, ENUM, SYSTEM_ENUM].includes(fld.dataType)
      )
    },
    selectedGroupObj() {
      let { groupingFieldList, details } = this
      let { groupByFieldId } = details || {}
      let selectedGroupField = (groupingFieldList || []).find(
        grpFld => grpFld.id === groupByFieldId
      )
      return selectedGroupField || null
    },
    selectedObjDisplayName() {
      let { selectedGroupObj } = this
      return this.$getProperty(selectedGroupObj, 'displayName', null)
    },
    isLookup() {
      let { dataType } = this.selectedGroupObj || {}
      let { LOOKUP } = dataTypes
      return dataType === LOOKUP
    },
    isPickList() {
      let { dataType } = this.selectedGroupObj || {}
      let { ENUM, SYSTEM_ENUM } = dataTypes
      return [ENUM, SYSTEM_ENUM].includes(dataType)
    },
  },

  methods: {
    async deserialize() {
      const operators = { IS: 3, ISNT: 4 }
      let { groupByFieldId, groupCriteria } = this.viewDetails
      let { conditions } = groupCriteria || {}
      let firstCondition = conditions?.[1] || {}
      let { fieldName, operatorId, computedValues } = firstCondition
      let isValueTypeLookup =
        ['id', 'ouid', 'roleId'].includes(fieldName) &&
        [operators.IS, operators.ISNT].includes(operatorId)
      this.$set(this.details, 'groupByFieldId', groupByFieldId)
      this.groupCriteria = groupCriteria
      await this.loadGroupCriteriaOptions()

      if (this.isPickList || isValueTypeLookup) {
        this.basedOn = 'value'
        this.valueType.operatorId = operatorId || null
        this.groupCriteria = this.isLookup ? groupCriteria : {}
        this.groupingValues = (computedValues || []).map(selected => {
          return parseInt(selected)
        })
      }
      this.filterToggle = !isEmpty(groupCriteria)
    },
    refreshGroupCrit() {
      if (this.filterToggle) {
        this.groupingValues = []
        this.valueType.operatorId = null
        this.refreshGroupCriteria = false
        this.groupCriteria = null
        this.$nextTick(() => (this.refreshGroupCriteria = true))
      }
    },
    async validate() {
      let groupForm = true
      let criteriaValidate = this.showGroupCriteria
        ? await this.$refs['group-criteria'].validate()
        : true
      try {
        groupForm = await this.$refs['grouping-form'].validate()
      } catch {
        groupForm = false
      }
      return criteriaValidate && groupForm
    },
    serialize() {
      let { basedOn, valueType, groupingValues, isLookup, details } = this
      let { groupByFieldId, groupCriteria } = details || {}

      if (this.filterToggle) {
        if (basedOn === 'value') {
          let fieldName = 'index'
          if (isLookup) {
            let {
              lookupModule: { name },
            } = this.selectedGroupObj
            fieldName = ['users', 'requester'].includes(name)
              ? 'ouid'
              : name === 'role'
              ? 'roleId'
              : 'id'
          }
          valueType.fieldName = fieldName
          valueType.value = (groupingValues || []).toString()
          groupCriteria = { conditions: { 1: valueType }, pattern: '1' }
        }
      } else {
        groupCriteria = null
      }

      return { groupByFieldId, groupCriteria }
    },
    async loadGroupCriteriaOptions(query = null, isUserSearching = false) {
      let { selectedGroupObj, moduleId } = this
      if (this.isLookup) {
        this.valuesLoading = true
        let { lookupModule, name } = selectedGroupObj || {}
        let { name: lookupModuleName } = lookupModule || {}

        this.moduleName = lookupModuleName || null
        let payload = { field: { lookupModule } }
        if (isUserSearching) {
          payload.searchText = query
        } else {
          this.basedOn = 'criteria'
          this.refreshGroupCrit()
        }
        if (name === 'moduleState') {
          payload.field.filters = {
            parentModuleId: { operatorId: 9, value: [String(moduleId)] },
          }
        }
        let { error, options } = await getFieldOptions(payload)

        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else
          this.valueOptions = options.map(({ label, value }) => ({
            id: value,
            value: label,
          }))

        this.valuesLoading = false
      } else if (this.isPickList) {
        let { enumMap: allOptions } = this.selectedGroupObj || {}

        this.basedOn = 'value'
        this.valueOptions = Object.entries(allOptions).map(([id, value]) => ({
          id,
          value,
        }))
      }
    },
  },
}
</script>
<style lang="scss">
.timeline-view-values-field {
  margin: 0px !important;
  .el-form-item__content {
    margin: 0px !important;
  }
}
</style>
