<template>
  <f-dialog
    :title="getTitle"
    :visible="true"
    :loading="isSaving"
    :stayOnSave="true"
    :record="record"
    @save="saveReading"
    @close="closeDialog"
    :width="$mobile ? '90%' : '30%'"
  >
    <div v-if="!readingObj" class="pB40 mT10">Loading....</div>
    <div slot="content" v-else class="pB30 mT10">
      <div class="mb5">{{ getSubTitle }}</div>
      <div style="display:flex">
        <div v-if="isBooleanField(readingObj.field)" class="pT10 width60">
          {{ readingObj.field.falseVal || 'False' }}
          <el-switch
            :disabled="isNullValueSelected"
            v-model="readingObj.value"
            class="pL10 pR10"
          ></el-switch>
          {{ readingObj.field.trueVal || 'True' }}
        </div>
        <div v-else-if="isEnumField(readingObj.field)" class="width80">
          <el-select
            :disabled="isNullValueSelected"
            filterable
            collapse-tags
            v-model="readingObj.value"
            class="fc-input-full-border-select2 width90"
          >
            <el-option
              v-for="(item, index) in fieldOptions"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <div v-else class="width80">
          <el-input
            :disabled="isNullValueSelected"
            style="width: 90%;text-align: left;"
            class="fc-input-full-border2 control-action-reading-field"
            controls-position="right"
            :type="fieldType"
            v-model="readingObj.value"
          >
            <template v-if="readingObj.field.unit" slot="append"
              >{{ readingObj.field.unit }}
            </template>
          </el-input>
        </div>
        <div style="padding-top:10px">
          <el-checkbox v-model="isNullValueSelected" @change="changeValue()"
            >null</el-checkbox
          >
        </div>
      </div>
    </div>
  </f-dialog>
</template>
<script>
import FDialog from '@/FDialogNew'
import { isEmpty } from '@facilio/utils/validation'
import { constructFieldOptions } from '@facilio/utils/utility-methods'
import {
  isBooleanField,
  isNumberField,
  isDecimalField,
  isEnumField,
} from '@facilio/utils/field'

export default {
  props: [
    'reading',
    'saveAction',
    'closeAction',
    'recordId',
    'recordName',
    'fieldId',
    'groupId',
    'pointId',
  ],
  components: { FDialog },
  data() {
    return {
      readingObj: null,
      isSaving: false,
      record: null,
      recordLoading: false,
      isNullValueSelected: false,
    }
  },
  created() {
    this.isBooleanField = isBooleanField
    this.isEnumField = isEnumField

    if (this.reading) {
      this.readingObj = this.reading
    } else {
      if (this.groupId) {
        this.$http
          .get(
            '/v2/controlAction/getControlGroupMeta?controlGroupId=' +
              this.groupId
          )
          .then(response => {
            if (
              response.data.result &&
              response.data.result.controlActionGroup
            ) {
              this.readingObj = response.data.result.controlActionGroup
            }
          })
      } else if (this.pointId) {
        this.$http
          .get('/v2/controlAction/getControllablePoints')
          .then(response => {
            if (
              response.data.result &&
              response.data.result.controllablePoints &&
              response.data.result.controllablePoints.length
            ) {
              this.readingObj = response.data.result.controllablePoints.find(
                rt => rt.id === this.pointId
              )
            }
          })
      } else {
        this.$util
          .loadLatestReading(this.recordId, false, false, null, this.fieldId)
          .then(fields => {
            if (fields && fields.length) {
              this.readingObj = fields[0]
            }
          })
      }
    }
  },
  mounted() {
    if (this.recordId) {
      this.recordLoading = true
      this.$util.getAssetById(this.recordId).then(assetObj => {
        this.record = assetObj
        this.recordLoading = false
      })
    }
  },
  computed: {
    fieldOptions() {
      if (this.readingObj) {
        return constructFieldOptions(this.readingObj.field.enumMap || [])
      }
      return []
    },
    fieldType() {
      let { readingObj } = this
      let { field } = readingObj || {}

      if (!isEmpty(field) && (isNumberField(field) || isDecimalField(field))) {
        return 'number'
      }
      return 'text'
    },
    getTitle() {
      if (this.readingObj) {
        if (this.recordLoading) {
          return 'Loading...'
        } else if (this.recordId && this.record) {
          return this.record.name
        } else {
          return this.readingObj.field.displayName
        }
      } else {
        return 'Loading...'
      }
    },
    getSubTitle() {
      if (this.readingObj) {
        if (this.recordLoading) {
          return 'Loading...'
        } else if (this.recordId && this.record) {
          return this.readingObj.field.displayName
        } else {
          return 'Set Reading'
        }
      } else {
        return 'Loading...'
      }
    },
  },
  methods: {
    validate() {
      if (isEmpty(this.readingObj.value)) {
        this.error = true
        this.errorMessage = 'Please enter a value for the reading'
      } else {
        this.error = false
        this.errorMessage = ''
      }
      return !this.error
    },
    saveReading() {
      if (!this.isNullValueSelected) {
        if (!this.validate()) return
      }
      this.isSaving = true
      let newValue
      if (this.readingObj.value == null) {
        newValue = null
      } else {
        newValue = this.readingObj.value.toString()
      }

      let data = [
        this.recordId,
        this.readingObj.field.fieldId,
        // this.readingObj.value.toString(),
        newValue,
        this.recordName,
        this.readingObj.field.displayName,
        this.groupId,
      ]
      this.$util
        .setReadingValue(...data)
        .then(() => {
          this.saveAction(this.readingObj)
          this.isSaving = false
        })
        .catch(() => {
          this.isSaving = false
        })
    },
    closeDialog() {
      this.closeAction(this.readingObj)
    },
    changeValue() {
      this.readingObj.value = null
    },
  },
}
</script>
<style>
.control-action-reading-field .el-input__inner {
  border-top-right-radius: 0px;
  border-bottom-right-radius: 0px;
}
</style>
