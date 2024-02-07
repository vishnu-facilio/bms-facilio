<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-form-item>
          <div class="pB15">
            <p class="details-Heading">
              {{ $t('setup.customButton.module_name') }}
            </p>
            <div class="heading-description">
              {{ $t('setup.customButton.module_name_desc') }}
            </div>
          </div>
          <el-select
            v-model="targetModuleName"
            clearable
            filterable
            class="width100 pR20"
            :placeholder="$t('common.products.select_module_name')"
            @change="emitAction()"
          >
            <el-option
              v-for="option in modulesList"
              :key="option.moduleId"
              :value="option.name"
              :label="option.displayName"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="selectedAction === 'Open Form'">
      <el-col>
        <div class="heading-description">
          {{ $t('common._common.please_enter_fieldJson') }}
        </div>
        <PlaceholderPicker
          placement="left"
          :title="$t('common.header.placeholders')"
          @change="addPlaceholder"
          class="mR20 float-right mB10 placeholder-text"
          :module="currentModuleName"
        ></PlaceholderPicker>
        <el-input
          class="mT3 whitespace-pre-wrap"
          ref="formDataInput"
          :min-rows="2"
          type="textarea"
          :autosize="{ minRows: 3, maxRows: 4 }"
          @input.native="emitAction"
          v-model="formDataJson"
        >
        </el-input>
        <p class="fc-sub-title-desc pT5">
          {{
            `Eg: {
        "name": "value/placeholderValue",
        "lookup": 113,
      } in JSON Format `
          }}
        </p>
      </el-col>
    </el-row>
    <el-row v-else>
      <el-col>
        <p class="details-Heading pB10">
          {{ $t('common._common.summary_record') }}
        </p>
        <el-col>
          <div class="heading-description">
            {{ $t('common._common.please_enter_id') }}
          </div>
          <PlaceholderPicker
            placement="right"
            key="id-placeholder"
            :title="$t('common.header.placeholders')"
            @change="addPlaceholder"
            class="mR20 mL15 float-right mB10 placeholder-text"
            :module="currentModuleName"
          ></PlaceholderPicker>
        </el-col>
        <div class="mT10">
          <el-input
            v-model="overviewRecordId"
            class="fc-input-full-border-select2 mL3 width100 mT10"
            clearable
            @input="emitAction()"
          ></el-input>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PlaceholderPicker from '@/placeholder/PlaceholderPicker'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'

const navigationOptions = {
  'Redirect URL': 'URL',
  'Open Form': 'Form',
  'Open Summary': 'Summary',
}

export default {
  name: 'InternalRedirectBehaviour',
  props: ['selectedAction', 'customButtonObject', 'currentModuleName'],
  components: { PlaceholderPicker },
  data() {
    return {
      targetModuleName: '',
      overviewRecordId: '',
      formDataJson: '',
      placeHolderValue: {
        name: '${bags.name:-}',
        lookup: 113,
      },
    }
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    modulesList() {
      return this.getAutomationModulesList()
    },
  },
  watch: {
    selectedAction: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(newVal, oldVal)) {
          this.resetFields()
        }
      },
      immediate: true,
    },
  },
  created() {
    this.deserialize()
  },
  methods: {
    emitAction() {
      let { selectedAction } = this
      let { targetModuleName } = this
      let params = {
        navigateTo: navigationOptions[selectedAction],
        moduleName: targetModuleName,
      }
      if (navigationOptions[selectedAction] === 'Summary') {
        params = { ...params, recordId: this.overviewRecordId }
      } else {
        params = { ...params, formDataJson: this.formDataJson }
      }
      this.$emit('setProperties', { config: params })
    },
    async deserialize() {
      let { config = {} } = this.customButtonObject
      let { navigateTo } = config || {}

      if (navigateTo) {
        this.targetModuleName = this.$getProperty(
          this,
          'customButtonObject.config.moduleName'
        )
        if (navigateTo === 'Form') {
          let { targetModuleName } = this
          if (!isEmpty(targetModuleName)) {
            this.formDataJson = this.$getProperty(
              this,
              'customButtonObject.config.formDataJson'
            )
          }
        } else {
          this.overviewRecordId = config.recordId
        }
      }
    },
    addPlaceholder({ placeholderString }) {
      let { selectedAction } = this
      if (navigationOptions[selectedAction] === 'Form') {
        let formDataElement = this.$refs['formDataInput']
        let { $refs } = formDataElement
        let cursorInitialPosition = this.$getProperty(
          $refs,
          'textarea.selectionStart',
          ''
        )
        let endPosition = this.$getProperty($refs, 'textarea.selectionEnd', '')

        if (!isEmpty(cursorInitialPosition) && !isEmpty(endPosition)) {
          let preCursorText = formDataElement.value.substring(
            0,
            cursorInitialPosition
          )
          let postCursorText = formDataElement.value.substring(
            endPosition,
            formDataElement.value.length
          )

          this.formDataJson = `${preCursorText}${placeholderString}${postCursorText}`
        } else {
          this.formDataJson += placeholderString
        }
      } else {
        this.overviewRecordId = ''
        this.overviewRecordId += placeholderString
        this.emitAction()
      }
    },
    resetFields() {
      this.targetModuleName = ''
      this.overviewRecordId = ''
      this.formDataJson = ''
    },
  },
}
</script>
<style>
.placeholder-text {
  margin-top: -15px !important;
}
</style>
