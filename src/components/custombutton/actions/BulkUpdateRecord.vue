<template>
  <div v-if="showForm">
    <TransitionForm
      :moduleName="selectedButton.moduleName"
      :formId="selectedButton.formId"
      :transition="selectedButton"
      :saveAction="executeAction"
      :closeAction="closeFlowForm"
      :approvalRule="null"
      :isV3="true"
    ></TransitionForm>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import TransitionForm from '@/stateflow/TransitionForm'
import { isEmpty, isObject } from '@facilio/utils/validation'

export default {
  props: ['selectedButton', 'selectedRecords', 'moduleName'],
  components: { TransitionForm },
  data() {
    return {
      showForm: false,
    }
  },
  created() {
    this.startAction()
  },

  methods: {
    async startAction() {
      this.showForm = true
    },
    closeFlowForm() {
      this.showForm = false
      this.$emit('closed')
    },
    async executeAction(formData) {
      let { selectedButton, selectedRecords, moduleName } = this

      Object.entries(formData).forEach(([key, value]) => {
        if (isEmpty(value)) {
          delete formData[key]
        } else if (isObject(value)) {
          Object.entries(value).forEach(([k, v]) => {
            if (isEmpty(v)) {
              delete value[k]
            }
          })
        }
      })

      let params = {
        customButtonId: selectedButton.id,
        moduleName: moduleName,
        data: {
          [moduleName]: selectedRecords.map(record => {
            return { id: record.id, ...formData }
          }),
        },
      }
      return API.post('v3/modules/data/bulkpatch', params).then(
        this.responseHandler
      )
    },
    responseHandler({ error }) {
      this.$emit('closed')
      if (error) {
        this.$message.error(this.$t('common._common.error_occured'))
        this.$emit('response', false)
      } else {
        this.$message.success(
          this.$t('common.products.record_updated_successfully')
        )
      }
      this.$emit('response', true)
    },
  },
}
</script>

<style></style>
