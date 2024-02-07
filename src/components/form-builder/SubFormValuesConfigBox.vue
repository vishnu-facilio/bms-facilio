<template>
  <div>
    <el-dialog
      v-if="canShowConfigureDialog"
      title="Configure default values"
      :visible.sync="canShowConfigureDialog"
      width="50%"
      class="fc-dialog-center-container default-value-container"
      :before-close="closeDialogBox"
      :append-to-body="true"
    >
      <div class="height450 el-form">
        <f-sub-form-wrapper
          class="position-relative pB100"
          :ref="`default-value ~ ${section.subFormId}`"
          :key="`default-value ~  ${section.subFormId}`"
          :initialSubForm="section.subForm"
          :subFormsArr.sync="section.subFormsArr"
        ></f-sub-form-wrapper>
      </div>
      <div class="modal-dialog-footer z-50">
        <el-button @click="closeDialogBox" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          :loading="isSaving"
          type="primary"
          class="modal-btn-save"
          @click="saveDefaultValues()"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import SubFormSetupMixin from '@/mixins/forms/SubFormSetupMixin'
import FSubFormWrapper from '@/FSubFormWrapper'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [SubFormSetupMixin],
  components: {
    FSubFormWrapper,
  },
  props: {
    openSubformDefaultDialog: {
      type: Boolean,
    },
    section: {
      type: Object,
    },
    allowUpdateProp: {
      type: Boolean,
    },
  },
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    canShowConfigureDialog: {
      get() {
        return this.openSubformDefaultDialog
      },
      set(value) {
        this.$emit('update:openSubformDefaultDialog', value)
      },
    },
  },
  created() {
    let { section } = this
    let { subFormId, subForm, subFormValue } = section
    let subFormsArr = []
    if (!isEmpty(subFormId)) {
      subFormsArr = this.constructSubFormValues({
        subForm,
        subFormValue,
      })
      this.$set(section, 'subFormsArr', subFormsArr)
    }
  },
  methods: {
    closeDialogBox() {
      this.$emit('update:allowUpdateProp', true)
      this.$set(this, 'canShowConfigureDialog', false)
    },
    saveDefaultValues() {
      let { $refs, section } = this
      let { subFormId } = section || {}
      let subformWrapperElement = $refs[`default-value ~ ${subFormId}`]
      if (!isEmpty(subformWrapperElement)) {
        subformWrapperElement
          .saveRecord({
            skipDeserialize: true,
          })
          .then(data => {
            this.$emit('update:allowUpdateProp', true)
            this.$set(this.section, 'subFormValue', data)
            this.$set(this, 'canShowConfigureDialog', false)
          })
      }
    },
  },
}
</script>
<style lang="scss">
.default-value-container {
  .el-dialog__body {
    overflow: scroll;
    .f-subform-wrapper {
      border: 1px solid #ebf0f2;
    }
  }
}
</style>
