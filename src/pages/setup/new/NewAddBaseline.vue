<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div>
      <!-- <div id="baselineform"> -->
      <error-banner :error="error"></error-banner>
      <el-form
        id="baseline"
        :model="baseLine"
        :rules="rules"
        :label-position="'top'"
        ref="newbaseLine"
      >
        <!-- header section start -->
        <error-banner :error="error"></error-banner>

        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ $t('setup.new.new_baseline') }}
            </div>
          </div>
        </div>
        <!-- header section end -->
        <!-- body section start -->
        <div class="new-body-modal">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item>
                <p class="fc-input-label-txt">
                  {{ $t('setup.new.base_name') }}
                </p>
                <el-input
                  v-model="baseLine.name"
                  placeholder="Enter the name"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item prop="space">
            <p class="fc-input-label-txt">{{ $t('alarm.alarm.space') }}</p>
            <el-select v-model="baseLine.spaceId" class="fc-input-full-border2">
              <el-option
                v-for="space in spaces"
                :key="space.id"
                :label="space.name"
                :value="space.id"
              ></el-option>
            </el-select>
          </el-form-item>

          <div class="fc-sub-title-container">
            <div class="fc-modal-sub-title">
              {{ $t('mv.summary.date_range') }}
            </div>
            <div class="fc-sub-title-desc">
              {{ $t('setup.setupLabel.choose_date_range') }}
            </div>
          </div>

          <date-picker-html @onSelect="emit"></date-picker-html>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('setup.users_management.cancel')
          }}</el-button>
          <el-button
            type="primary"
            @click="submitForm('newbaseLine')"
            class="modal-btn-save"
          >
            {{ $t('maintenance._workorder.save') }}
          </el-button>
        </div>
      </el-form>

      <!-- </div> -->
    </div>
  </el-dialog>
</template>
<script>
import DatePickerHtml from '@/NewDatePickerHTML'
import ErrorBanner from '@/ErrorBanner'
export default {
  props: ['visibility'],
  data() {
    return {
      error: false,
      pickerOptions2: {
        shortcuts: [
          {
            text: 'Last week',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            },
          },
          {
            text: 'Last month',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            },
          },
          {
            text: 'Last 3 months',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            },
          },
        ],
      },
      pickerOptions1: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
        shortcuts: [
          {
            text: 'Today',
            onClick(picker) {
              picker.$emit('pick', new Date())
            },
          },
          {
            text: 'Yesterday',
            onClick(picker) {
              const date = new Date()
              date.setTime(date.getTime() - 3600 * 1000 * 24)
              picker.$emit('pick', date)
            },
          },
          {
            text: 'A week ago',
            onClick(picker) {
              const date = new Date()
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', date)
            },
          },
        ],
      },
      baseLine: {
        name: '',
        rangeType: '8',
        spaceId: null,
        startTime: null,
        endTime: null,
      },
      rules: {
        name: [{ required: true, message: ' ', trigger: 'blur' }],
      },
    }
  },
  components: {
    ErrorBanner,
    DatePickerHtml,
  },
  computed: {
    spaces() {
      let spaceList = this.$store.state.spaces
      if (spaceList) {
        return spaceList
      }
      return []
    },
  },
  methods: {
    emit(date) {
      this.baseLine.startTime = date.time[0]
      this.baseLine.endTime = date.time[1]
      if (date.filterName === 'D') {
        this.baseLine.rangeType = '8'
      } else if (date.filterName === 'W') {
        this.baseLine.rangeType = '9'
      } else if (date.filterName === 'M') {
        this.baseLine.rangeType = '10'
      } else if (date.filterName === 'Y') {
        this.baseLine.rangeType = '11'
      } else if (date.filterName === 'R') {
        this.baseLine.rangeType = '12'
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    submitForm(newbaseLine) {
      let self = this
      this.$refs[newbaseLine].validate(valid => {
        if (valid) {
          let newAddBaseline = {}
          newAddBaseline.baseLine = {
            name: this.baseLine.name,
            spaceId: this.baseLine.spaceId ? this.baseLine.spaceId : -1,
            rangeType: this.baseLine.rangeType,
            startTime: this.baseLine.startTime,
            endTime: this.baseLine.endTime,
          }
          self.saving = true
          this.$http
            .post('/baseline/addBaseLine', newAddBaseline)
            .then(function(response) {
              JSON.stringify(response)
              if (response.status === 200) {
                self.$message(this.$t('setup.create.create_baseline_success'))
                self.saving = false
                self.$emit('saved')
                self.closeDialog()
              }
            })
            .catch(function(error) {
              console.log(error)
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
  },
}
</script>
<style>
#baseline .el-icon-date:before {
  content: '';
}
#baselineform
  .fc-form
  input:not(.q-input-target):not(.el-input__inner):not(.el-select__input):not(.btn),
.fc-form
  textarea:not(.q-input-target):not(.el-textarea__inner):not(.el-input__inner):not(.el-select__input),
.fc-form .fselect {
  width: 42%;
}
</style>
