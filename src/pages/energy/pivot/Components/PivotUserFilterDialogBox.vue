<template>
  <div>
    <base-dialog-box
      :visibility.sync="visibility"
      :onConfirm="save"
      :onClearAll="clearAll"
      :disableConfirm="isFilterEmpty"
      :onCancel="closeDialog"
      cancelText="Cancel"
      confirmText="Save"
      :title="$t('pivot.filters')"
      width="642px"
    >
      <div class="dialog-content-body pT20" slot="body" style="margin-bottom:20px;">
        <div class="time-line-filter-section">
          <div class="pivot-user-filter">
            <el-row style="display:flex">
              <el-col style="width:15%;"> {{ $t('pivot.userFilter') }}</el-col>
              <el-col>
                <el-switch
                  v-model="enableUserFilter"
                  style="margin-top: -3px;"
                ></el-switch
              ></el-col>
            </el-row>
          </div>
          <div
            class="userfilter-select-section"
            v-if="enableUserFilter"
            style=""
          >
            <el-row>
              <el-col :span="6">
                <div class="date-filter-label f13">
                  {{ $t('pivot.dateFilter') }}
                </div>
              </el-col>
              <el-col :span="12">
                <el-select
                  v-model="dateFieldIdModel"
                  filterable
                  placeholder="None"
                  class="fc-input-full-border-select2 width60 mT5 date-field-select"
                >
                  <el-option
                    v-for="(dateField, index) in allDateFields"
                    :key="'date-field-option' + index"
                    :label="dateField.displayName"
                    :value="dateField.id"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <div class="date-filter-label f13">
                  {{ $t('pivot.skip_in_drilldown') }}
                </div>
              </el-col>
              <el-col :span="12">
                <el-checkbox
                  v-model="disableFilterInDrilldown"
                  class="mT15 mL5 scope-txt14 scope-border"
                  @change="$emit('disableFilterInDrilldown', disableFilterInDrilldown)"
                >
                </el-checkbox>
              </el-col>
            </el-row>
            <div v-if="dateValidMsg" style="margin-top:15px">
              <span style="color:#f56c6c;">Please select any value</span>
            </div>
          </div>
        </div>
        <div class="mT35">
          <span>Criteria</span
          ><el-switch
            v-model="criteriaEnabled"
            style="margin-left:26px;"
          ></el-switch>
        </div>
        <div
          class="criteria-section"
          v-if="criteriaEnabled"
          @click="criteriaValidMsg = false"
        >
          <!-- <new-criteria-builder
            title="Show options that match"
            class="stateflow-criteria"
            ref="criteriaBuilder"
            v-model="criteriaModel"
            :exrule="criteriaModel"
            :module="moduleName"
            @condition="updateCriteria"
          ></new-criteria-builder> -->
          <CriteriaBuilder v-model="criteriaModel" :moduleName="moduleName">
          </CriteriaBuilder>
          <div v-if="criteriaValidMsg">
            <span style="color:#f56c6c;margin-left:9px;"
              >Please add valid criteria</span
            >
          </div>
        </div>
      </div>
    </base-dialog-box>
  </div>
</template>

<script>
import BaseDialogBox from './BaseDialogBox.vue'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
import { CriteriaBuilder } from '@facilio/criteria'
import isEqual from 'lodash/isEqual'

export default {
  props: ['visibility', 'moduleName', 'criteria', 'dateFieldId', 'dateFields', 'disableUserFilterInDrilldown'],
  components: {
    BaseDialogBox,
    CriteriaBuilder,
  },
  watch: {
    dateFieldIdModel: {
      handler(newVal) {
        if (!isEqual(newVal, this.dateFieldId)) {
          this.isFilterEmpty = false
        }
      },
    },
    criteriaModel: {
      handler(newVal) {
        if (newVal != null) this.criteriaEnabled = true
        if (!isEqual(newVal, this.criteria)) {
          this.isFilterEmpty = false
        }
      },
      deep: true,
    },
    enableUserFilter(newVal) {
      if (newVal == false) {
        this.dateFieldIdModel = -1
      }
    },
    criteriaEnabled(newVal) {
      if (newVal == false) {
        this.criteriaModel = null
      }
    },
    criteria(newVal) {
      this.criteriaModel = deepCloneObject(newVal)
    },
  },
  data() {
    return {
      criteriaModel: null,
      isFilterEmpty: true,
      dateFieldIdModel: null,
      dateValidMsg: null,
      enableUserFilter: false,
      criteriaEnabled: false,
      allDateFields: [],
      criteriaValidMsg: false,
      disableFilterInDrilldown:false,
    }
  },
  created() {
    this.disableFilterInDrilldown = this.disableUserFilterInDrilldown
    this.criteriaValidMsg = false
    this.updateNoneInDateFields()
    if (!(this.dateFieldId < 0)) {
      this.enableUserFilter = true
    }
    this.criteriaModel = deepCloneObject(this.criteria)
    this.dateFieldIdModel = this.dateFieldId > 0 ? this.dateFieldId : null
    if (!this.criteriaEnabled && !this.enableUserFilter)
      this.isFilterEmpty = true
  },
  methods: {
    updateNoneInDateFields() {
      let none_date_field = {
        label: 'None',
        id: -1,
        value: -1,
        displayName: 'None',
      }
      this.allDateFields.push(none_date_field)
      if (this.dateFields && this.dateFields.length) {
        this.dateFields.forEach(date_field => {
          this.allDateFields.push(date_field)
        })
      }
    },
    isValidCriteria(criteria) {
      if (!criteria.conditions) {
        return false
      }
      let conditions = criteria.conditions
      for (const key in conditions) {
        if (Number.isInteger(Number.parseInt(key))) {
          let condition = conditions[key]
          if (
            isEmpty(condition) ||
            isEmpty(condition.operatorId) ||
            isEmpty(condition.fieldName)
          ) {
            return false
          }
        }
      }
      return true
    },

    closeDialog() {
      this.$emit('update:visibility', false)
    },
    save() {
      //on save update saved criteria  state in parent comp
      //   if (!this.criteriaEnabled) {
      //     this.criteriaModel = null
      //   }
      //   if (!(this.criteriaModel && this.isValidCriteria(this.criteriaModel))) {
      //     this.criteriaValidMsg = true
      //     return
      //   }
      //   if (this.enableUserFilter && this.dateFieldIdModel < 0) {
      //     this.dateValidMsg = true
      //     return
      //   }
      //   if (!this.enableUserFilter) this.dateFieldIdModel = -1
      //   this.$emit('save', this.criteriaModel, this.dateFieldIdModel)
      // },
      if (!this.isFilterEmpty) {
        if (
          this.criteriaEnabled &&
          !(this.criteriaModel && this.isValidCriteria(this.criteriaModel))
        ) {
          this.criteriaValidMsg = true
          return
        }
        if (this.enableUserFilter && this.dateFieldIdModel < 0) {
          this.dateValidMsg = true
          return
        }
        if (!this.enableUserFilter) this.dateFieldIdModel = -1
        this.$emit('save', this.criteriaModel, this.dateFieldIdModel)
      }
    },
    clearAll() {
      this.enableUserFilter = false
      this.criteriaEnabled = false
    },
  },
}
</script>

<style scoped>
.userfilter-select-section {
  padding-top: 20px;
}
.date-filter-label {
  height: 100%;

  padding-top: 17px;
  padding-right: 22px;
}
.pivot-user-filter {
  font-size: 13px;
}
.criteria-section {
  margin-left: -7px;
  margin-bottom: 20px;
}
</style>
