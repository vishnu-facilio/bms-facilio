<template>
  <el-dialog
    :append-to-body="true"
    :visible="true"
    width="37%"
    :before-close="closeDialog"
    lock-scroll
    custom-class="fc-dialog-center-container"
    class="attribute-list-specification"
  >
    <template #title>
      <div class="el-dialog__title self-center f18">
        {{ $t('setup.classification.attributes.attributes') }}
      </div>
    </template>
    <div
      v-if="$validation.isEmpty(attributesList)"
      class="flex-middle height-100 justify-center"
    >
      {{ $t('setup.classification.attributes.empty_state_classification') }}
    </div>
    <el-form
      v-else
      @submit.prevent="save"
      class="fc-form pT15 form-item"
      label-position="top"
    >
      <el-form-item
        v-for="attr in attributesList"
        :key="attr.id"
        :label="attr.name"
        :prop="attr.name"
      >
        <Select
          v-if="attr.fieldType === dataTypes.BOOLEAN"
          v-model="attr.value"
          :options="options"
          placeholder="Select"
          valueName="value"
          labelName="label"
        ></Select>

        <el-date-picker
          v-if="attr.fieldType === dataTypes.DATE_TIME"
          v-model="attr.value"
          :type="attr.displayType"
          :placeholder="attr.placeholder"
          value-format="timestamp"
          class="fc-input-full-border-select2 width100"
        ></el-date-picker>

        <el-input
          v-if="
            [dataTypes.STRING, dataTypes.NUMBER, dataTypes.DECIMAL].includes(
              attr.fieldType
            )
          "
          v-model="attr.value"
          :type="attr.displayType"
          :placeholder="attr.placeholder"
          class="fc-input-full-border-select2 width100"
        ></el-input>
      </el-form-item>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button
        @click="closeDialog"
        class="modal-btn-cancel attributes-cancel-btn"
      >
        {{ $t('setup.form_builder.validation.cancel') }}
      </el-button>
      <el-button
        type="primary"
        class="modal-btn-save attributes-save-btn"
        :loading="isSaving"
        @click="onSave"
      >
        {{ $t('setup.form_builder.validation.save') }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { dataTypes } from './classificationUtil'
import { Select } from '@facilio/ui/forms'

export default {
  props: ['selectedAttribute', 'isSaving'],
  components: { Select },
  data: () => ({ dataTypes, attributesList: [] }),
  created() {
    this.init()
  },
  computed: {
    options() {
      return [
        { label: 'Yes', value: true },
        { label: 'No', value: false },
      ]
    },
  },
  methods: {
    init() {
      if (!isEmpty(this.selectedAttribute)) {
        this.attributesList = (this.selectedAttribute || []).map(attr => {
          let { fieldType, name } = attr || {}
          let { STRING, NUMBER, DECIMAL, DATE_TIME } = dataTypes || {}
          let displayType = null,
            placeholder = ''

          if ([NUMBER, DECIMAL, STRING].includes(fieldType)) {
            displayType = fieldType === STRING ? 'text' : 'number'
            placeholder = `Enter ${name}`
          } else if (fieldType === DATE_TIME) {
            displayType = 'datetime'
            placeholder = this.$t(
              'setup.classification.attributes.select_date_time'
            )
          }

          return { ...attr, displayType, placeholder }
        })
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
    onSave() {
      let attributesList = this.attributesList.map(attr => {
        delete attr.displayType
        delete attr.placeholder
        return attr
      })

      this.$emit('onSave', attributesList)
    },
  },
}
</script>
<style lang="scss">
.attribute-list-specification {
  .el-dialog {
    border-radius: 10px;

    .el-dialog__body {
      height: 530px;
      overflow: scroll;
      padding: 10px 32px 50px;
    }
    .attributes-cancel-btn {
      border-bottom-left-radius: 10px;
    }
    .attributes-save-btn {
      border-bottom-right-radius: 10px;
    }
    .form-item {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
    }
    .form-item > * {
      flex: 0 48%;
    }
  }
}
</style>
