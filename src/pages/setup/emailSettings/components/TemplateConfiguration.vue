<template>
  <div>
    <el-form
      :model="configureData"
      :label-position="'top'"
      ref="configurationForm"
    >
      <el-form-item class="pT30">
        <el-row class="flex-middle">
          <el-col :span="9">
            <div class="label-txt-black bold">
              {{ $t('setup.setup_profile.email') }}
            </div>
          </el-col>
          <el-col :span="15">
            <div class="label-txt-black bold">{{ moduleName }}</div>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item
        v-for="(preFields, index) in fields"
        :key="index"
        :prop="preFields.name"
      >
        <el-row class="flex-middle">
          <el-col :span="9">
            <div class="label-txt-black bold">{{ preFields.displayName }}</div>
          </el-col>
          <el-col :span="15">
            <el-select
              v-model="configureData[preFields.name]"
              filterable
              clearable
              placeholder="Select"
              class="fc-input-full-border-select2 width100"
              @change="getTargetValue($event, index)"
            >
              <el-option
                v-for="field in preFields.relatedField"
                :key="field.id"
                :label="field.displayName"
                :value="field.name"
              >
                <div v-if="field.dataType === preFields.dataType">
                  {{ field.displayName }}
                </div>
              </el-option>
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
const DATA_TYPE_ENUM = {
  StringField: 1,
}
export default {
  props: [
    'parentFields',
    'targetFields',
    'config',
    'ruleAction',
    'isSave',
    'reset',
    'configData',
  ],
  components: {},
  data() {
    return {
      moduleName: null,
      configureData: {},
      fields: [],
      isMainField: [],
      sourceFields: ['subject', 'from', 'content', 'recipient'],
      rules: [],
      targetValue: null,
    }
  },
  watch: {
    targetFields: {
      handler: function() {
        this.setRelatedFields()
      },
      immediate: true,
    },
    reset: {
      handler: function(newVal) {
        if (newVal) {
          this.resetFields()
        }
      },
    },
    isSave: {
      handler: function(newVal) {
        if (newVal) {
          this.$emit('saveAction', this.saveTemplate())
        }
      },
      immediate: true,
    },
    config: {
      handler: function() {
        if (!isEmpty(this.config)) {
          this.sourceFields = { ...this.config[0].sourceFields }
        }
      },
      immediate: true,
    },
  },
  computed: {},
  created() {},
  mounted() {
    if (!isEmpty(this.config)) {
      this.sourceFields = { ...this.config[0].sourceFields }
      if (!isEmpty(this.parentFields)) {
        this.setRelatedFields()
      }
    }
  },
  methods: {
    setRelatedFields() {
      if (!isEmpty(this.sourceFields)) {
        let preSelectFields = []
        Object.keys(this.sourceFields).forEach(sourcefield => {
          if (this.sourceFields[sourcefield] === 'mailAttachments') {
            preSelectFields.push({
              name: 'mailAttachments',
              displayName: 'Attachment',
              module: { name: 'mailAttachments' },
            })
          } else {
            preSelectFields.push(
              this.parentFields.filter(
                d => d.name === this.sourceFields[sourcefield]
              )[0]
            )
          }
        })
        this.fields = {
          ...preSelectFields,
        }
        Object.keys(preSelectFields).forEach(d => {
          let currField = preSelectFields[d]
          let relatedField = this.targetFields
            ? this.targetFields.filter(field => {
                if (field.mainField) {
                  // validating main field
                  if (!this.isMainField.includes(field.name)) {
                    this.isMainField.push(field.name)
                  }
                }
                if ([30, 10, 11].includes(currField.displayTypeInt)) {
                  if (
                    this.$getProperty(this, 'configData.chooseAction') ===
                    'serviceRequest'
                  ) {
                    if (
                      field.name === 'requester' ||
                      field.displayName === 'Requester' ||
                      field.displayTypeInt === DATA_TYPE_ENUM.StringField
                    ) {
                      return field
                    }
                  } else {
                    // special handling for requester to should all the user field
                    if (field.displayTypeInt === DATA_TYPE_ENUM.StringField) {
                      return field
                    }
                  }
                } else if (currField.name === 'mailAttachments') {
                  if (field.displayType._name === 'FILE') {
                    return field
                  }
                } else if (field.displayTypeInt === currField.displayTypeInt) {
                  return field
                }
              })
            : null
          this.$set(this.configureData, currField.name, currField.defaultValue)
          this.$set(this.fields[d], 'relatedField', relatedField)
        })
        if (!isEmpty(this.ruleAction) && !this.reset) {
          this.setDefaultValue()
        }
        if (this.reset) {
          this.$emit('update:reset', false)
        }
      }
    },
    getTargetValue(el, index) {
      //getting string values and neglate the requester field from first index
      if (index == 2) {
        this.targetValue = el
      }
    },
    resetFields() {
      // if (this.configureData) {
      this.$refs['configurationForm'].resetFields()
      // console.log("this.configureData" + this.configureData)
      //  Object.keys(this.configureData).forEach(fieldName => {})
      //}
      this.$set(this, 'configureData', {})
    },
    setDefaultValue() {
      if (
        !isEmpty(this.ruleAction.template) &&
        !isEmpty(this.ruleAction.template.mappingJson)
      ) {
        let mappingJson = this.ruleAction.template.mappingJson
        this.moduleName = this.ruleAction.template.name
        Object.keys(mappingJson).forEach(rt => {
          let moduleFieldString
          let field
          if (mappingJson[rt] instanceof Object) {
            moduleFieldString = mappingJson[rt].email
              .replace('${', '')
              .replace(new RegExp('}' + '$'), '')
            field = moduleFieldString.split('.')[1]
          } else {
            if (mappingJson[rt] === 'mailAttachments') {
              field = mappingJson[rt]
            } else {
              moduleFieldString = mappingJson[rt]
                .replace('${', '')
                .replace(new RegExp('}' + '$'), '')
              field = moduleFieldString.split('.')[1]
            }
          }
          this.$set(this.configureData, field, rt)
        })
      }
    },
    saveTemplate() {
      let mappingJson = {}
      Object.keys(this.configureData).forEach(fieldName => {
        let sourcefield
        Object.keys(this.fields).forEach(d => {
          if (this.fields[d].name === fieldName) {
            sourcefield = this.fields[d]
          }
        })
        if (this.isMainField) {
          if (
            this.isMainField.includes(fieldName) &&
            !isEmpty(this.configureData[fieldName])
          ) {
            this.isMainField.splice(this.isMainField.indexOf(fieldName), 1)
            // delete this.isMainField[this.isMainField.indexOf(fieldName)]
          }
        }
        if (!isEmpty(this.configureData[fieldName]) && !isEmpty(sourcefield)) {
          let placeHolderString
          if (fieldName === 'mailAttachments') {
            placeHolderString = fieldName
          } else {
            let placeHolder =
              '$' +
              '{' +
              (sourcefield.module ? sourcefield.module.name : '') +
              '.' +
              fieldName +
              '}'
            if (sourcefield.displayTypeInt === 30) {
              if (this.targetValue == 'requester') {
                placeHolderString = { email: placeHolder }
              } else {
                placeHolderString = placeHolder
              }
            } else {
              placeHolderString = placeHolder
            }
          }
          mappingJson[this.configureData[fieldName]] = placeHolderString
        }
      })
      if (!isEmpty(this.isMainField)) {
        this.isMainField.forEach(d => {
          if (!d.name === 'siteId') {
            mappingJson.errorMessage =
              d + ' field is madatory field when creating record'
            mappingJson.error = true
          }
        })
        // mappingJson.errorMessage = "Subject field is madatory field ! "
        // console.log(this.isMainField)
      }
      this.targetValue = null
      return mappingJson
    },
  },
}
</script>
