<template>
  <!-- Update Field Value dialog start -->
  <div class="overflow-y-scroll pB20">
    <el-row v-if="isServerity" :gutter="20">
      <el-col :span="12">
        <el-input
          disabled
          v-model="severity"
          class="fc-input-full-border2 width100"
        ></el-input>
      </el-col>
      <el-col :span="12">
        <el-form-item>
          <el-select
            v-if="
              fieldMatchers && fieldMatchers[0] && fieldMatchers[0].templateJson
            "
            class="fc-input-full-border-select2 width100"
            v-model="
              fieldMatchers[0].templateJson.resultWorkflowContext.expressions[0]
                .constant
            "
            collapse-tags
          >
            <el-option
              v-for="label in alarmseverity"
              :key="label.id"
              :label="label.displayName"
              :value="String(label.id)"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <div
      v-if="!(fieldMatcher.fieldName === 'severity')"
      v-for="(fieldMatcher, index) in fieldMatchers"
      :key="index"
    >
      <el-row class="visibility-visible-actions pT10 pB10 pointer">
        <el-col :span="12">
          <el-select
            v-model="fieldMatcher.fieldName"
            filterable
            class="fc-input-full-border-select2 width100 pR10"
          >
            <el-option
              v-for="(field, index) in fields"
              :key="index"
              :label="field.displayName"
              :value="field.name"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="12" class="pL10">
          <el-col :span="9" class="mT8">
            <div class="flex-middle">
              <el-button
                :disabled="fieldMatcher.fieldName ? false : true"
                type="button"
                @click="
                  ;(displayBuilder = true),
                    (selectedIndex = index),
                    (selectedField = fieldMatcher)
                "
                v-bind:class="
                  fieldMatcher.isConfig
                    ? 'success-green-btn'
                    : 'small-border-btn'
                "
                >{{
                  fieldMatcher.isConfig ? 'Configured' : 'Configure'
                }}</el-button
              >
              <div
                v-if="fieldMatcher.isConfig"
                @click="
                  ;(displayBuilder = true),
                    (selectedIndex = index),
                    (selectedField = fieldMatcher)
                "
                class="mL10"
              >
                <i class="el-icon-edit pointer" title="Edit Field" v-tippy></i>
              </div>
            </div>
          </el-col>
          <el-col :span="12" class="mT10 pL20">
            <div class="visibility-hide-actions pointer">
              <img src="~assets/add-icon.svg" @click="addRow" class="pointer" />
              <img
                src="~assets/remove-icon.svg"
                v-if="fieldMatchers.length > 1 && !(fieldMatchers.length === 1)"
                @click="deleteRow(index)"
                class="pointer mL5"
              />
            </div>
          </el-col>
        </el-col>
      </el-row>
    </div>
    <el-dialog
      v-if="selectedField"
      :visible.sync="displayBuilder"
      width="50%"
      class="fieldchange-Dialog pB15 fileupdate-dialog fc-dialog-center-container"
      :append-to-body="true"
      :title="selectedField.fieldName"
    >
      <div class="height300">
        <f-formula-builder
          style="padding-top: 20px;"
          title=""
          module="impact"
          v-model="selectedField.templateJson.resultWorkflowContext"
          :assetCategory="category"
          class="rule-condition-formula"
        ></f-formula-builder>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="displayBuilder = false" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click=";(displayBuilder = false), actionSave(selectedField)"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
export default {
  components: { FFormulaBuilder },
  props: [
    'fields',
    'visibility',
    'editField',
    'category',
    'model',
    'emitData',
    'isServerity',
  ],
  watch: {
    emitData: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.updateField()
      }
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  computed: {
    ...mapState({
      alarmseverity: state => state.alarmSeverity,
    }),
    actions() {
      return this.model
    },
  },
  data() {
    return {
      severity: 'Severity',
      displayBuilder: false,
      selectedField: null,
      selectedIndex: null,
      fieldMatchers: [],
      alarmServerityId: null,
      picklistOptions: {},
    }
  },
  mounted() {
    if (this.editField && this.editField.length > 0) {
      this.editObj()
    } else {
      if (this.isServerity) {
        this.fieldMatchers.push({
          actionType: 17,
          fieldName: 'severity',
          templateJson: {
            resultWorkflowContext: {
              expressions: [
                {
                  name: 'severity',
                  constant: null,
                },
              ],
            },
            metaJson: { fields: [] },
          },
        })
      }
      this.addRow()
    }
  },
  methods: {
    editObj() {
      let actions = this.editField
      actions.forEach(d => {
        this.fieldMatchers.push({
          actionType: 17,
          isConfig: true,
          fieldName: d.template.metaJson.fields[0],
          templateJson: {
            resultWorkflowContext: d.template.originalTemplate.workflowContext,
            metaJson: { fields: [] },
          },
        })
      })
    },
    actionSave() {
      this.fieldMatchers[this.selectedIndex].isConfig = true
    },
    addRow() {
      this.fieldMatchers.push({
        actionType: 17,
        fieldName: null,
        templateJson: {
          resultWorkflowContext: null,
          metaJson: { fields: [] },
        },
      })
      this.$forceUpdate()
    },
    deleteRow(index) {
      this.fieldMatchers.splice(index, 1)
    },
    updateField() {
      let sendData = this.$helpers.cloneObject(this.fieldMatchers)
      let index = []
      sendData.forEach((d, i) => {
        if (!d.templateJson.resultWorkflowContext) {
          index.push(i)
        }
        if (d.fieldName) {
          sendData[i].templateJson.metaJson.fields.push(d.fieldName)
        }
      })
      if (index) {
        index.forEach(d => {
          sendData.splice(sendData.indexOf(d), 1)
        })
      }
      this.$emit('actions', sendData)
    },
  },
}
</script>
<style>
.fileupdate-dialog .height300 {
  overflow-y: scroll;
  padding-bottom: 100px;
  height: 300px;
}
</style>
