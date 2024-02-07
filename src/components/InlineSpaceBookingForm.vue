<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    :append-to-body="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog fc-floor-plan-booking"
  >
    <div class="fc-pm-main-content-H">{{ $t('tenant.occupant.booking') }}</div>
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      class="fp-bookings-form"
      v-else
      :form.sync="formObj"
      :module="moduleName"
      :moduleData="moduleData"
      :moduleDataId="moduleDataId"
      :moduleDisplayName="moduleDisplayName"
      :isSaving="isSaving"
      :isV3Api="isV3Api"
      :isEdit="true"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
      @onBlur="onBlurHook"
      @save="saveRecord"
      @cancel="closeDialog"
      formLabelPosition="top"
    ></f-webform>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'
import { isFunction } from '@facilio/utils/validation'
export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['formVisibility', 'formData', 'moduleName', 'recordData'],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return 'Booking'
    },
    moduleDataId() {
      let { moduleData } = this
      let { id } = moduleData || {}
      return id || null
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    init() {
      if (this.formData) {
        this.formObj = this.formData
        this.selectedForm = this.formData
      }
      if (this.recordData) {
        this.moduleData = this.recordData
      }
    },
    getFormattedSlotData(moduleData) {
      let { slotList = [] } = moduleData
      let slotData = []
      if (!isEmpty(slotList)) {
        slotList.forEach(record => {
          slotData.push(record.slot)
        })
      }
      return slotData
    },
    afterSerializeHook({ data }) {
      if (data) {
        //serializeHook
      }
    },
    afterSaveHook({ error }) {
      if (!error) {
        this.$emit('saved')
      }
    },
    closeDialog() {
      this.$emit('closeForm')
    },
    async saveRecord(formModel) {
      let { afterSaveHook, moduleName, moduleDataId } = this

      this.isSaving = true

      let response = await API.updateRecord(moduleName, {
        id: moduleDataId,
        data: formModel,
      })
      this.isSaving = false
      // Hook to handle notification after crud operation
      this.notificationHandler(response)

      // Hook to handle response after crud operation
      if (!isEmpty(afterSaveHook) && isFunction(afterSaveHook)) {
        this.afterSaveHook(response)
      }
    },
  },
}
</script>
<style lang="scss">
.fp-bookings-form {
  .f-webform-container {
    padding-top: 25px;
  }
  .time-slot-heading {
    margin-top: 20px;
    font-size: 12px;
    font-weight: bold;
    letter-spacing: 0.4px;
    color: #385571;
  }
  .f-booking-slots-container {
    padding-left: 10px;
    padding-right: 10px;
    margin-top: -20px;
  }
  .time-slot-container {
    padding: 4px 10px;
    border-radius: 3px;
    margin-bottom: 10px;
    color: #324056;
    background: #fff;
    font-weight: 500;
    border: 1px solid #39b3c2;
    cursor: pointer;
    font-size: 13px;
    text-align: center;
  }
  .f-booking-slots-container .time-slot-container.active {
    background: #38b2c2;
    color: #fff;
  }
  .f-booking-slots-container .f-slot-c2 .time-slot-data {
    padding-bottom: 7px;
    padding-top: 7px;
    border-bottom: solid 1px #f2f4fa;
  }
  .fc-currency-value {
    font-size: 14px;
    font-weight: 500;
  }
  .time-slot-data2 {
    padding-top: 10px;
    .time-slot-total-txt {
      font-weight: 600;
    }
  }
  .time-slot-text {
    .inline {
      margin-right: 5px !important;
    }
    .fill-grey3 {
      fill: #324056;
    }
  }
}
.fc-floor-plan-booking {
  .fc-available-slot {
    padding-top: 10px;
  }
  .f-booking-slots-container {
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
