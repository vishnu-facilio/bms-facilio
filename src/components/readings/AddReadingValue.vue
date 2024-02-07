<template>
  <f-dialog
    :visible="true"
    :title="readingObj.field.displayName"
    :loading="isSaving"
    :stayOnSave="true"
    @save="addReadingValue"
    @close="closeDialog"
    width="40%"
    class="reading-dialog"
  >
    <div>
      <div class="pT20">
        {{
          'Enter Reading ' +
            (readingObj.field.unit ? '(in ' + readingObj.field.unit + ')' : '')
        }}
      </div>
      <div v-if="isDateOrDateTimeField">
        <f-date-picker
          v-model="readingObj.value"
          value-format="timestamp"
          format="dd-MM-yyyy"
          type="datetime"
        ></f-date-picker>
      </div>
      <div v-if="isBooleanField(readingObj.field)" class="pT15">
        <el-radio-group v-model="readingObj.value">
          <el-radio :label="true" class="fc-radio-btn">
            {{ readingObj.field.trueVal || 'True' }}
          </el-radio>
          <el-radio :label="false" class="fc-radio-btn">
            {{ readingObj.field.falseVal || 'False' }}
          </el-radio>
        </el-radio-group>
      </div>
      <div v-else-if="isEnumField(readingObj.field)">
        <el-select
          filterable
          collapse-tags
          v-model="readingObj.value"
          class="fc-input-full-border-select2 width500px mT15"
        >
          <el-option
            v-for="item in fieldOptions"
            :key="item.id"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
      <div v-else>
        <el-input v-model="readingObj.value"></el-input>
      </div>
    </div>
    <div class="pT20 pB30">
      <div>Time</div>
      <f-date-picker
        ref="datepick"
        v-model="readingObj.ttime"
        @change="updateDuration"
        type="datetime"
      ></f-date-picker>
    </div>
  </f-dialog>
</template>
<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import FDialog from '@/FDialogNew'
import {
  isBooleanField,
  isEnumField,
  isDateField,
  isDateTimeField,
} from '@facilio/utils/field'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['reading', 'saveAction', 'closeAction', 'recordId', 'recordName', 'moduleName'],
  components: {
    FDatePicker,
    FDialog,
  },
  data() {
    return {
      isSaving: false,
      readingObj: {},
    }
  },
  created() {
    this.readingObj = this.reading
    this.isBooleanField = isBooleanField
    this.isEnumField = isEnumField
  },
  computed: {
    fieldOptions() {
      let options = []

      this.readingObj.field &&
        this.readingObj.field.values.forEach(obj => {
          if (obj.visible) {
            options.push({
              id: obj.id,
              label: obj.value,
              value: Number(obj.index),
            })
          }
        })

      return options
    },
    isDateOrDateTimeField() {
      let { field } = this.readingObj || {}
      if (!isEmpty(field)) {
        return isDateField(field) || isDateTimeField(field)
      }
      return false
    },
  },
  methods: {
    updateDuration() {
      if (!this.readingObj.ttime) {
        this.$nextTick(() => {
          this.$refs.datepick.focus()
        })
      }
    },
    addReadingValue() {
      if (this.readingObj.value !== null) {
        let readingVal = {
          parentId: this.recordId,
          ttime:
            this.readingObj.ttime > this.$options.filters.now()
              ? this.$options.filters.now()
              : this.readingObj.ttime,
          readings: { [this.readingObj.field.name]: this.readingObj.value },
        }
        let data = {
          readingName: this.readingObj.readingName,
          readingValues: [readingVal],
        }

        this.isSaving = true
        if (this.moduleName === 'meter') {
          this.addMeterReading(data)
        }
        else {
        this.$http
          .post('/v2/readings/asset/add', data)
          .then(response => {
            if (response.data.responseCode === 0) {
              this.$message({
                message: this.$t('asset.assets.reading_added_success'),
                type: 'success',
              })
              this.saveAction(this.readingObj)
            } else {
              this.$message({
                message: this.$t('asset.assets.readings_added_failed'),
                type: 'error',
              })
            }
            this.isSaving = false
            this.closeDialog()
          })
          .catch(() => {
            this.$message({
              message: this.$t('asset.assets.readings_added_failed'),
              type: 'error',
            })
            this.isSaving = false
          })
        }
      }
    },
    closeDialog() {
      this.closeAction()
    },
    async addMeterReading(data) {
      let { error } = await API.post('/v2/readings/meter/add', data)
      if (isEmpty(error)) {
        this.$message.success(this.$t('asset.assets.reading_added_success'))
        this.saveAction(this.readingObj)
      } else {
        this.$message.error(this.$t('asset.assets.readings_added_failed'))
      }
      this.isSaving = false
      this.closeDialog()
    },
  },
}
</script>
<style lang="scss">
.reading-dialog .el-date-editor.el-input .el-input__inner {
  padding-left: 30px;
  height: 40px;
}
</style>
