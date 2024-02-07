<template>
  <div>
    <el-dialog
      :visible.sync="visible"
      :width="'45%'"
      maxHeight="300px"
      :title="moduleName + ' Duplication'"
      confirmTitle="UPDATE"
      class="fc-dialog-center-container new-add-duplicate-dialog fc-dialog-header-hide"
    >
      <error-banner
        :error.sync="error"
        :errorMessage.sync="errorText"
      ></error-banner>
      <div class="positon-relative duplicate-dialog-body">
        <div class="record-duplicate-dialog-form">
          <div
            class="record-duplicate-dialog-form-left"
            :class="{
              'duplicate-right-side-width': $validation.isEmpty(records),
            }"
          >
            <div
              class="label-txt-black text-uppercase fwBold duplicate-header-left"
            >
              {{ moduleName }} {{ $t('common._common.duplication') }}
            </div>
            <div class="pL30 pR30">
              <el-row class="mT30">
                <div class="fc-input-label-txt pB0">
                  {{ $t('common._common.name') }}
                </div>
                <el-col :span="24">
                  <div class="pT10">
                    <el-input
                      v-model="duplicateObj.name"
                      :placeholder="$t('common._common.enter_the_name')"
                      class="fc-input-full-border2 width100"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT30">
                <div class="fc-input-label-txt pB0">
                  {{ $t('common._common.starting_number') }}
                </div>
                <el-col :span="24">
                  <div class="pT10">
                    <el-input
                      type="number"
                      v-model="startingNumber"
                      :placeholder="
                        $t('common.placeholders.enter_the_starting_number')
                      "
                      :min="0"
                      class="fc-input-full-border2 width100"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT30">
                <div class="fc-input-label-txt pB0">
                  {{ $t('common._common.quantity') }}
                </div>
                <el-col :span="24">
                  <div class="pT10">
                    <el-input
                      type="number"
                      v-model="duplicateObj.quantity"
                      :placeholder="
                        $t('common.placeholders.enter_the_quantity')
                      "
                      :min="0"
                      :max="100"
                      class="fc-input-full-border2 width100"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT30">
                <div class="fc-input-label-txt pB0">
                  {{ $t('common._common.prefix') }}
                </div>
                <el-col :span="24">
                  <div class="pT10">
                    <el-input
                      v-model="duplicateObj.prefix"
                      :placeholder="$t('common.placeholders.enter_the_prefix')"
                      class="fc-input-full-border2 width100"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT30">
                <div class="fc-input-label-txt pB0">
                  {{ $t('common._common.suffix') }}
                </div>
                <el-col :span="24">
                  <div class="pT10">
                    <el-input
                      v-model="duplicateObj.sufix"
                      :placeholder="$t('common.placeholders.enter_the_suffix')"
                      class="fc-input-full-border2 width100"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <template v-if="!$validation.isEmpty(records)">
            <div
              class="record-duplicate-dialog-form-right"
              :class="{ 'duplicate-right-side-hide': !records }"
            >
              <div>
                <div class="flex-middle record-duplicate-left-header">
                  <div class="label-txt-black fwBold mL10">
                    {{ records.length }} {{ listName
                    }}{{ duplicateObj.quantity > 1 ? 's' : '' }}
                  </div>
                </div>
                <div
                  v-for="(value, index) in records"
                  :key="index"
                  v-if="index < 10"
                  class="pR30 pL30 pT3"
                >
                  <div
                    class="fc-black-13 text-left pT15 pB15 border-bottom3 bold"
                  >
                    {{ value }}
                  </div>
                </div>
                <div class="pR30 pL30 pT20" v-if="records.length > 10">
                  <i
                    class="el-icon-plus plus-button"
                    style="font-weight: 700;font-size: 12px;"
                  ></i>
                  <span class="fc-black-13 text-left pT15 pB15 bold">
                    {{ records.length - 10 }} {{ listName
                    }}{{ records.length - 10 > 1 ? 's' : '' }}</span
                  >
                </div>
              </div>
            </div>
          </template>
          <div class="modal-dialog-footer">
            <el-button
              @click=";(visible = false), actioncancel()"
              class="modal-btn-cancel"
              >{{ $t('common._common.cancel') }}</el-button
            >
            <el-button
              type="primary"
              @click="actionSave()"
              class="modal-btn-save"
              :loading="saving"
              >{{ saving ? 'Saving...' : 'Save' }}</el-button
            >
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import ErrorBanner from '@/ErrorBanner'
export default {
  data() {
    return {
      visible: true,
      saving: false,
      error: false,
      errorText: '',
      startingNumber: 1,
      duplicateObj: {
        name: null,
        quantity: '1',
        prefix: null,
        sufix: null,
      },
    }
  },
  computed: {
    records() {
      let { duplicateObj, startingNumber } = this
      let recordList = []
      if (!isEmpty(duplicateObj.name)) {
        for (let i = 0; i < duplicateObj.quantity; i++) {
          let value =
            (!isEmpty(duplicateObj.prefix) ? duplicateObj.prefix + ' ' : '') +
            (!isEmpty(duplicateObj.name) ? duplicateObj.name : '') +
            (!isEmpty(startingNumber) && startingNumber > 0 ? ' ' : '') +
            (!isEmpty(startingNumber) && startingNumber > 0
              ? startingNumber
              : '') +
            (!isEmpty(duplicateObj.sufix) ? ' ' + duplicateObj.sufix : '')
          if (value.length > 0) {
            recordList.push(value)
          }
          startingNumber++
        }
      }
      return recordList
    },
  },
  components: {
    ErrorBanner,
  },
  watch: {
    selectedRecordObj(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.init()
      }
    },
    visible(newVal, oldVal) {
      if (!newVal) {
        this.actioncancel()
      }
    },
    'duplicateObj.quantity'(newData) {
      if (newData > 100) {
        this.errorText = 'Quantity should not exceed 100'
        this.error = true
      } else {
        this.error = false
      }
    },
  },
  mounted() {
    this.init()
  },
  props: ['moduleName', 'selectedRecord', 'selectedRecordObj', 'listName'],
  methods: {
    init() {
      if (this.selectedRecordObj) {
        this.$set(
          this.duplicateObj,
          'name',
          !isEmpty(this.selectedRecordObj) ? this.selectedRecordObj.name : null
        )
      }
    },
    actioncancel() {
      this.$emit('closed')
    },
    actionSave() {
      let { moduleName, startingNumber, selectedRecord, duplicateObj } = this
      this.validation()
      if (this.error) return
      this.saving = true
      let url = '/v2/module/recordsDuplication'
      let params = {
        moduleName: moduleName,
        id: selectedRecord,
        startingNumber: parseInt(startingNumber),
        duplicateObj,
      }
      this.$http.post(url, params).then(response => {
        if (response.data && response.data.responseCode === 0) {
          this.$message({
            message: moduleName + ' duplicated successfully',
            type: 'success',
          })
          this.saving = false
          this.$emit('sucess')
          this.$emit('closed')
        } else {
          this.$error.message('Export failed')
          this.$emit('closed')
        }
      })
    },
    validation() {
      if (isEmpty(this.duplicateObj.name)) {
        this.errorText = 'Please enter the name'
        this.error = true
      } else if (isEmpty(this.duplicateObj.quantity)) {
        this.errorText = 'Please enter the quantity'
        this.error = true
      } else if (
        !isEmpty(this.duplicateObj.quantity) &&
        this.duplicateObj.quantity > 100
      ) {
        this.errorText = 'Quantity should not exceed 100'
        this.error = true
      } else if (isEmpty(this.startingNumber)) {
        this.errorText = 'Please enter the startingNumber'
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },
  },
}
</script>
<style lang="scss">
.record-duplicate-dialog-form {
  .record-duplicate-dialog-form-left {
    width: 70%;
    float: left;
    height: 100%;
    overflow-y: scroll;
    padding-bottom: 100px;
    border-right: 1px solid #ebedf4;
    max-height: 600px;
  }

  .record-duplicate-dialog-form-right {
    width: 30%;
    float: left;
    overflow-y: scroll;
    max-height: 600px;
    padding-bottom: 100px;
  }

  .duplicate-right-side-hide {
    display: none;
  }

  .duplicate-right-side-width {
    width: 100% !important;
    border-right: none;
    padding-right: 0;
  }

  .duplicate-header-left {
    border-bottom: 1px solid #ebedf4;
    padding: 15px 30px;
    background: #ffffff;
    position: sticky;
    top: 0;
    z-index: 1;
  }
  .record-duplicate-left-header {
    border-bottom: 1px solid #ebedf4;
    padding: 15px 30px;
    background: #ffffff;
    position: sticky;
    top: 0;
    z-index: 1;
  }
}
.duplicate-dialog-body {
  height: 100%;
  max-height: 600px;
  overflow: hidden;
}

.new-add-duplicate-dialog .el-dialog__body {
  padding: 0;
}
.new-add-duplicate-dialog .el-dialog {
  width: 60% !important;
}
.duplicate-dialog-body {
  height: 100%;
  max-height: 700px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.plus-button {
  color: #39b2c2;
}
</style>
