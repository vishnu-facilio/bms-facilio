<template>
  <el-dialog
    :visible="true"
    width="60%"
    :title="$t('common._common._criteria')"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :append-to-body="true"
    :before-close="close"
  >
    <error-banner
      :error.sync="error"
      :errorMessage="errorMessage"
    ></error-banner>

    <div class="height330 overflow-y-scroll pB70 pL5 pR5">
      <el-form ref="criteria-builder" :rules="rules" :model="namedCriteria">
        <el-form-item
          :label="$t('common.products.name')"
          prop="name"
          :required="true"
        >
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="namedCriteria.name"
            :placeholder="$t('common._common.enter_crieteria_name')"
          />
        </el-form-item>
      </el-form>

      <CriteriaBuilder
        v-model="criteria"
        :moduleName="module"
        :isOneLevelEnabled="true"
      />
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="close()" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save" @click="saveCriteria()">
        {{ $t('common._common._save') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import CriteriaBuilderComp from 'pages/setup/stateflow/popups/StateTransitionCriteria'
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import { CriteriaBuilder } from '@facilio/criteria'

export default {
  extends: CriteriaBuilderComp,
  components: { CriteriaBuilder },
  props: ['selectedCriteria', 'expressionIdx'],

  data() {
    return {
      criteria: null,
      error: false,
      errorMessage: '',
      criteriaRendered: false,
      namedCriteria: { name: null, criteria: null },
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    if (!isEmpty(this.selectedCriteria)) {
      let { criteria, name } = this.selectedCriteria || {}

      this.criteria = criteria || null
      this.namedCriteria.name = name
    } else {
      this.namedCriteria.name = `Expression ${this.expressionIdx}`
    }
  },

  methods: {
    saveCriteria() {
      // overridden from StateTransitionCriteria(parent component)
      let criteria = clone(this.criteria)
      let activeCriteria = this.serializeCriteria(criteria)

      this.$refs['criteria-builder'].validate(async valid => {
        if (!valid) return false

        if (!isEmpty(activeCriteria)) {
          this.namedCriteria.criteria = activeCriteria
          this.$emit('save', this.namedCriteria)
          this.close()
        } else {
          this.error = true
          this.errorMessage = this.$t('common.header.please_select_fields')
          return
        }
      })
    },
  },
}
</script>
<style scoped>
.pB70 {
  padding-bottom: 70px;
}
</style>
