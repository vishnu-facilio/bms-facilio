<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="formDisplayName"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
    >
      <div v-if="!isLoading" class="fc-pm-main-content-H">
        {{ formDisplayName }}
      </div>
      <div
        v-if="!isLoading && facilityRecord.slotGeneratedUpto"
        class="fc-warning-tag-1 flex-middle justify-content-center tag-margin pB0 pT0 f13"
      >
        <inline-svg
          src="svgs/alert"
          iconClass="icon text-left icon-sm fill-yellow vertical-middle"
        ></inline-svg>
        <div class="pL10 break-word">
          <ol>
            <li class="pB5">
              {{
                `Please be noted that Special Availability will change the existing working hours to the selected working hours. Any bookings outside the new working hours will be cancelled.`
              }}
            </li>
            <li>
              {{
                `In the case of Special Unavailability, any bookings within the Special Unavailability will be cancelled.`
              }}
            </li>
          </ol>
        </div>
      </div>

      <div v-if="isLoading" class="loading-container d-flex mT10">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="formDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :moduleDataId="moduleDataId"
        :isEdit="!$validation.isEmpty(moduleDataId)"
        @save="saveRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['visibility', 'editId', 'facilityRecord'],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    formDisplayName() {
      return (this.formObj || {}).displayName
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    moduleDataId() {
      return this.editId
    },
    moduleName() {
      return 'facilitySpecialAvailability'
    },
  },
  methods: {
    afterSaveHook({ error, facilitySpecialAvailability }) {
      if (!error) this.afterSave(facilitySpecialAvailability)
    },
    afterSave(facilitySpecialAvailability) {
      if (
        facilitySpecialAvailability &&
        facilitySpecialAvailability?.bookingCanceledCount > 0
      ) {
        let count = facilitySpecialAvailability?.bookingCanceledCount
        let message =
          count > 1
            ? `${count} Bookings has been canceled.`
            : `${count} Booking has been canceled.`
        setTimeout(() => {
          this.$message.warning(message)
        }, 1000)
      } else {
        this.$emit('saved')
      }

      this.closeDialog()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    afterSerializeHook({ data }) {
      if (!isEmpty(this.facilityRecord)) {
        this.$setProperty(data, 'facility.id', this.facilityRecord.id)
      }
      if (!isEmpty(data.endTime) && !isEmpty(data.startTime)) {
        let endTime = data.endTime
        let startTime = data.startTime
        let readableEndTime = this.getFacilityTimeFormat(endTime)
        let readableStartTime = this.getFacilityTimeFormat(startTime)
        this.$setProperty(data, 'endTime', readableEndTime)
        this.$setProperty(data, 'startTime', readableStartTime)
      }
      return data
    },
    getFacilityTimeFormat(value) {
      let timeFormat = 'HH:mm'
      if (value > 0) {
        return moment()
          .startOf('day')
          .milliseconds(value)
          .format(timeFormat)
      } else {
        return '00:00'
      }
    },
  },
}
</script>
