<template>
  <div class="pivot-add-criteria">
    <el-dialog
      :visible="visibility"
      class="pivot-add-criteria-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="$t('pivot.criteria')"
      width="50%"
    >
      <div class="body">
        <div class="base-module-criteria mT30">
          <new-criteria-builder
            title="Show options that match"
            class="stateflow-criteria"
            ref="criteriaBuilder"
            v-model="criteriaModel"
            :exrule="criteriaModel"
            :module="moduleName"
            @condition="updateCriteria"
          ></new-criteria-builder>
        </div>
      </div>

      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          Cancel</el-button
        >
        <el-button type="primary" class="modal-btn-save mL0" @click="save"
          >Done</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { deepCloneObject } from 'util/utility-methods'

import { isEmpty } from '@facilio/utils/validation'

import NewCriteriaBuilder from '@/NewCriteriaBuilder'

export default {
  props: ['visibility', 'moduleName', 'criteria'],
  components: {
    NewCriteriaBuilder,
  },

  data() {
    return {
      criteriaModel: null,
    }
  },
  created() {
    //make copy of saved criteria state for criteria builder to mutate,sync parent state on save only
    this.criteriaModel = deepCloneObject(this.criteria)
  },
  methods: {
    updateCriteria(newValue) {
      if (this.isValidCriteria(newValue)) {
        this.criteriaModel = newValue
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
          if (isEmpty(condition) || isEmpty(condition.operatorLabel)) {
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
      this.$emit('save', this.criteriaModel)
    },
  },
}
</script>
<style lang="scss">
.pivot-add-criteria-dialog {
  .body {
    height: 250px;
    overflow: scroll;
  }
}
</style>
