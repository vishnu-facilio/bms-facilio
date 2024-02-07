<template>
  <div>
    <p class="fc-input-label-txt txt-color">
      {{ $t('stateflow.transition.make_qr_mandatory') }}
    </p>
    <div class="d-flex">
      <div
        :class="isConfiguredQR ? 'configured-green' : 'configure-blue pointer'"
        class="new-statetransition-config"
        @click="editQRField"
      >
        {{
          isConfiguredQR
            ? $t('stateflow.transition.configured')
            : $t('stateflow.transition.configure')
        }}
      </div>
      <div v-if="isConfiguredQR" class="d-flex">
        <i
          class="el-icon-edit pointer edit-icon pL30 txt-color"
          @click="editQRField"
          :title="$t('stateflow.transition.edit')"
          v-tippy
        ></i>
        <span class="reset-txt pointer mL20" @click="resetQR()">
          {{ $t('stateflow.transition.reset') }}
        </span>
      </div>
    </div>
    <StateTransitionQRForm
      v-if="canShowQR"
      :module="module"
      :activeQRField="activeQRField"
      @onSave="save"
      @onClose="close"
    ></StateTransitionQRForm>
    <hr class="separator-line mR40 mT20" />
  </div>
</template>
<script>
import StateTransitionQRForm from './popups/StateTransitionQRForm'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['module', 'transition', 'setProps'],
  components: { StateTransitionQRForm },
  data() {
    return {
      canShowQR: false,
      activeQRField: null,
    }
  },
  computed: {
    isConfiguredQR() {
      let { qrFieldId, qrLookupFieldId } = this.transition || {}
      return (
        (!isEmpty(qrFieldId) && qrFieldId !== -99) ||
        (!isEmpty(qrLookupFieldId) && qrLookupFieldId !== -99)
      )
    },
  },
  methods: {
    editQRField() {
      let { qrFieldId, qrLookupFieldId } = this.transition || {}

      this.activeQRField = []
      if (!isEmpty(qrFieldId) && qrFieldId !== -99) {
        this.activeQRField.push(qrFieldId)
        if (!isEmpty(qrLookupFieldId) && qrLookupFieldId !== -99) {
          this.activeQRField.push(qrLookupFieldId)
        }
      }
      this.canShowQR = true
    },
    resetQR() {
      this.setProps({ qrFieldId: -99, qrLookupFieldId: -99 })
      this.activeQRField = null
      this.$emit('onSave')
    },
    close() {
      this.activeQRField = null
      this.canShowQR = false
    },
    save(qrFieldObj) {
      this.activeQRField = null
      this.setProps({ ...(qrFieldObj || {}) })
      this.$emit('onSave')
    },
  },
}
</script>
