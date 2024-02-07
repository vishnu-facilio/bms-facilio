<template>
  <el-dialog
    :title="$t('fields.properties.base_field_properties')"
    :visible.sync="canShowLookupDialog"
    width="40%"
    class="fc-dialog-center-container default-field-prop-container"
    :before-close="closeDialogBox"
    :append-to-body="true"
  >
    <div class="height250">
      <el-form ref="lookupFieldProps" :model="field">
        <el-form-item
          :label="$t('fields.properties.field_label')"
          prop="displayName"
          class="mB10"
          :required="true"
        >
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="field.displayName"
            class="fc-input-full-border-select2"
            @input="changeFieldName"
          ></el-input>
        </el-form-item>
        <el-form-item prop="name" class="mB10" :required="true">
          <div class="d-flex align-center pB6">
            <p class="fc-input-label-txt pB0">
              {{ $t('fields.properties.link_name') }}
            </p>
            <span
              v-tippy="{
                arrow: true,
                arrowType: 'round',
                animation: 'fade',
              }"
              :content="$t('fields.properties.link_name_desc')"
              class="el-icon-info mL5"
            >
            </span>
          </div>
          <el-input
            :placeholder="$t('common._common.enter_name')"
            v-model="field.name"
            class="fc-input-full-border-select2"
            @change="changeNameInteractedBoolean"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialogBox" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button type="primary" class="modal-btn-save" @click="addField">{{
        $t('common._common.confirm')
      }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: {
    canShowDialog: {
      type: Boolean,
    },
    fieldObj: {
      type: Object,
    },
  },
  created() {
    let { field } = this
    let { displayName } = field || {}

    if (!isEmpty(displayName)) {
      let fieldName = this.getSnakeCase(displayName)
      this.$set(field, 'name', fieldName)
    }
  },
  data() {
    return {
      fieldProperties: {},
      isNameFieldInteracted: false,
    }
  },
  computed: {
    canShowLookupDialog: {
      get() {
        return this.canShowDialog
      },
      set(value) {
        this.$emit('update:canShowDialog', value)
      },
    },
    field: {
      get() {
        return this.fieldObj
      },
      set(value) {
        this.$emit('update:fieldObj', value)
      },
    },
  },
  methods: {
    closeDialogBox() {
      let { field } = this
      let { rowIndex, columnIndex } = field
      this.canShowLookupDialog = false
      this.$emit('removeField', field, rowIndex, columnIndex)
    },
    addField() {
      let { field } = this
      this.$emit('addNewDefaultField', field)
    },
    getSnakeCase(string) {
      return string.replace(/[\W_]/g, '_').toLowerCase()
    },
    changeFieldName(val) {
      let { field, isNameFieldInteracted } = this
      if (!isNameFieldInteracted) {
        let fieldName = this.getSnakeCase(val)
        this.$set(field, 'name', fieldName)
      }
    },
    changeNameInteractedBoolean() {
      this.isNameFieldInteracted = true
    },
  },
}
</script>

<style lang="scss">
.default-field-prop-container {
  .fc-required-label {
    &::after {
      content: '*';
      color: red;
      font-size: 15px;
    }
  }
}
</style>
