<template>
  <el-dialog
    :visible="true"
    width="50%"
    title="Criteria"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="close"
  >
    <error-banner
      :error.sync="error"
      :errorMessage="errorMessage"
    ></error-banner>
    <div class="height330 overflow-y-scroll pB50">
      <span class="new-body-modal">
        <new-criteria-builder
          class="stateflow-criteria"
          ref="criteriaBuilder"
          v-model="criteria"
          :exrule="criteria"
          @condition="newValue => (criteria = newValue)"
          :module="module"
          :isRendering.sync="criteriaRendered"
          :hideTitleSection="true"
        ></new-criteria-builder>
      </span>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="close()" class="modal-btn-cancel">CANCEL</el-button>
      <el-button type="primary" class="modal-btn-save" @click="saveCriteria()"
        >Save</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'

export default {
  components: { NewCriteriaBuilder, ErrorBanner },
  props: ['module', 'activeCriteria'],

  data() {
    return {
      criteria: null,
      error: false,
      errorMessage: '',
      criteriaRendered: false,
    }
  },

  created() {
    this.criteria = this.activeCriteria
  },

  methods: {
    saveCriteria() {
      let criteria = clone(this.criteria)
      let activeCriteria = this.serializeCriteria(criteria)
      if (!isEmpty(activeCriteria)) {
        this.$emit('save', activeCriteria)
      } else {
        this.error = true
        this.errorMessage = ' Please select fields'
        return
      }
    },

    close() {
      this.$emit('close')
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
  },
}
</script>
