<template>
  <el-dialog
    title="Multi Line field Properties"
    :visible.sync="canShowDialog"
    width="40%"
    class="fc-dialog-center-container multi-line-prop-container"
    :before-close="closeDialogBox"
    :append-to-body="true"
  >
    <div class="height350">
      <el-form ref="lookupFieldProps" :model="field">
        <el-form-item
          label="Field Label"
          prop="displayName"
          class="mB20"
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
        <el-form-item label="Character Length" :required="true">
          <el-select
            placeholder="Select"
            v-model="selectedDataType"
            filterable
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="propertie in properties"
              :key="propertie.dataType"
              :label="propertie.label"
              :value="propertie.dataType"
            ></el-option> </el-select
        ></el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialogBox" class="modal-btn-cancel"
        >Cancel</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="addMainField()"
        >Confirm</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: {
    openMultiLinePropDialog: {
      type: Boolean,
    },
    multiLineField: {
      type: Object,
    },
    moduleName: {
      type: String,
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
      properties: [
        {
          label: 'Small (2000 characters)',
          dataType: 18,
        },
        {
          label: 'Large (32000 characters)',
          dataType: 17,
        },
      ],
      selectedDataType: 17,
      canRemoveField: true,
      error: false,
      errorMessage: null,
      isNameFieldInteracted: false,
    }
  },
  computed: {
    canShowDialog: {
      get() {
        return this.openMultiLinePropDialog
      },
      set(value) {
        this.$emit('update:openMultiLinePropDialog', value)
      },
    },
    field: {
      get() {
        return this.multiLineField
      },
      set(value) {
        this.$emit('update:multiLineField', value)
      },
    },
  },
  methods: {
    addMainField() {
      let { field, selectedDataType } = this
      this.$set(field, 'dataType', selectedDataType)
      this.canRemoveField = false
      this.$emit('addNewDefaultField', field)
    },
    closeDialogBox() {
      let { field } = this
      let { rowIndex, columnIndex } = field
      this.canShowDialog = false
      if (this.canRemoveField) {
        this.$emit('removeField', field, rowIndex, columnIndex)
      }
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
.multi-line-prop-container {
  .fc-required-label {
    &::after {
      content: '*';
      color: red;
      font-size: 15px;
    }
  }
}
</style>
