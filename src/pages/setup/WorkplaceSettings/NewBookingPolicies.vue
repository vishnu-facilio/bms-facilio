<template>
  <el-dialog
    :visible.sync="visibility"
    :before-close="closeDialog"
    :append-to-body="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog fc-floor-plan-booking"
    style="z-index: 999999"
  >
    <div class="overflow-y-scroll pB70 pL5 pR5 booking-policies-container">
      <el-form ref="criteria-builder" :model="bookingpolicies">
        <el-row :gutter="20" class="mB10">
          <el-col :span="12">
            <p class="fc-input-label-txt">
              {{ $t('common.products.name') }}
            </p>
            <el-form-item prop="name">
              <el-input
                class="width65 fc-input-full-border2"
                autofocus
                v-model="bookingpolicies.name"
                :placeholder="$t('Name')"
                :required="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              :label="$t('servicecatalog.setup.module_name')"
              prop="moduleName"
              class="mL30 mB20"
            >
              <el-select
                v-model="bookingpolicies.moduleName"
                :placeholder="$t('servicecatalog.setup.selectmodules')"
                class="fc-input-full-border-select2 width100"
                @change="updateCriteria"
              >
                <el-option
                  v-for="moduleName in modulesList"
                  :key="moduleName.value"
                  :label="moduleName.displayName"
                  :value="moduleName.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item
          :label="$t('common.wo_report.report_description')"
          prop="description"
        >
          <el-input
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 4 }"
            class="fc-input-full-border-textarea"
            :placeholder="$t('setup.setupLabel.add_a_decs')"
            v-model="bookingpolicies.description"
            resize="none"
          ></el-input>
        </el-form-item>

        <el-row :gutter="20" class="mB10">
          <el-col :span="24">
            <el-collapse
              v-model="activeNames"
              accordion
              class="fc-agent-collapse fc-pushing-data-collapse"
            >
              <el-collapse-item
                title="Criteria"
                name="1"
                v-if="bookingpolicies.moduleName"
                class="fc-input-label-txt"
              >
                <el-form-item prop="criteria">
                  <CriteriaBuilder
                    v-if="showCriteria"
                    v-model="bookingpolicies.criteria"
                    :moduleName="bookingpolicies.moduleName"
                    :required="true"
                  />
                </el-form-item>
              </el-collapse-item>
            </el-collapse>
          </el-col>
          <div class="">
            <el-col :span="24">
              <el-collapse
                v-model="activeNames"
                accordion
                class="fc-agent-collapse fc-pushing-data-collapse card-scroll "
              >
                <el-collapse-item title="Policies list" name="2">
                  <el-form-item prop="policies">
                    <BookingPoliciesList
                      v-model="bookingpolicies.policy"
                    ></BookingPoliciesList>
                  </el-form-item>
                </el-collapse-item>
              </el-collapse>
            </el-col>
          </div>
        </el-row>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            @click="savePolicy"
            class="modal-btn-save"
            >{{ $t('common.roles.save') }}</el-button
          >
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>
<script>
import { CriteriaBuilder } from '@facilio/criteria'
import BookingPoliciesList from './BookingPoliciesList'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'visibility',
    'formData',
    'recordData',
    'moduleName',
    'selectedPolicy',
    'isNew',
    'id',
  ],
  created() {
    let { isNew, selectedPolicy } = this
    if (!isNew && !isEmpty(selectedPolicy)) {
      this.bookingpolicies = selectedPolicy
    }
  },
  components: { CriteriaBuilder, BookingPoliciesList },
  data() {
    return {
      displayName: '',
      isDefault: false,
      activeNames: '1',
      // criteria: null,
      bookingpolicies: {
        name: null,
        criteria: null,
        description: null,
        policy: {
          maximumAttendees: {
            name: 'Maximum Attendees',
            description:
              'Can specify Maximum number of Attendees for the selected space',
            enable: false,
            value: null,
            unit: null,
          },
          isChargeable: {
            name: 'Is Chargeable',
            description: 'Can specify the Charges for the selected space',
            enable: false,
            value: null,
            unit: null,
          },
          isMultipleDayBooking: {
            name: 'Is Multiple Day Booking',
            description: 'Can specify whether Multiple days booking is allowed',
            enable: false,
            value: null,
            unit: null,
          },
          securityDeposit: {
            name: 'Security Deposit',
            description: 'Can specify the deposit amount',
            enable: false,
            value: null,
            unit: null,
          },
          bookingAdvanceDays: {
            name: 'Booking Advance Days',
            description: 'Can specify Advance booking days',
            enable: false,
            value: null,
            unit: null,
          },
          bookingAdvanceHours: {
            name: 'Booking Advance Hours',
            description: 'Can specify Advance booking hours',
            enable: false,
            value: null,
            unit: null,
          },
          cancellationPeriodInDays: {
            name: 'Cancellation Period In Days',
            description: 'Can specify Cancellation period in days',
            enable: false,
            value: null,
            unit: null,
          },
          cancellationPeriodInHours: {
            name: 'Cancellation Period In Hours',
            description: 'Can specify Cancellation Period In Hours',
            enable: false,
            value: null,
            unit: null,
          },
          cancellationCharges: {
            name: 'Cancellation Charges',
            description: 'Can specify Cancellation Charges for the booking',
            enable: false,
            value: null,

            unit: null,
          },
        },
        isModulesLoading: true,
        moduleName: null,
        selectedModuleName: null,
      },

      // selectedModuleName: null,
      modulesList: [
        {
          value: 'space',
          displayName: 'Space',
        },
        {
          value: 'desks',
          displayName: 'Desk',
        },
        {
          value: 'parkingstall',
          displayName: 'Parking',
        },
      ],
      showCriteria: true,
    }
  },
  computed: {},
  mounted() {},
  methods: {
    close() {
      this.$emit('close')
    },
    updateCriteria() {
      this.rerenderCriteria()
    },
    rerenderCriteria() {
      this.showCriteria = false
      this.$nextTick(() => {
        this.showCriteria = true
      })
    },
    savePolicy() {
      this.$refs['criteria-builder'].validate(async valid => {
        if (!valid) return

        let { isNew } = this
        let self = this
        let id = this.id
        let spaceBookingPolicies = {
          name: this.bookingpolicies.name,
          description: this.bookingpolicies.description,
          moduleName: this.bookingpolicies.moduleName,
          criteria: this.bookingpolicies.criteria,
          policy: this.bookingpolicies.policy,
        }
        let promise

        if (this.isNew) {
          promise = await API.createRecord('spaceBookingPolicy', {
            data: spaceBookingPolicies,
          })
        } else {
          promise = await API.updateRecord('spaceBookingPolicy', {
            id,
            data: spaceBookingPolicies,
          })
        }

        let { error } = promise || {}
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$emit('savePolicy')
          if (isNew) {
            this.$message.success(this.$t('workplace.policy_status.added'))
          } else {
            this.$message.success(this.$t('workplace.policy_status.updated'))
          }
        }
      })
    },
    cancel() {
      this.$emit('canceled')
    },

    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style>
.booking-policies-container {
  padding-left: 30px !important;
  padding-right: 30px !important;
  padding-top: 20px;
}
.booking-policies-container .fc-input-label-txt {
  padding-bottom: 0px;
}
.booking-policies-container .el-collapse {
  border-top: 0px;
  border-bottom: 0px;
}
.booking-policies-container
  .fc-pushing-data-collapse
  .el-collapse-item__header {
  padding-left: 0px;
}
.booking-policies-container .fc-pushing-data-collapse {
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
}
.booking-policies-container
  .fc-pushing-data-collapse
  .el-collapse-item__content {
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
}
.booking-policies-container .card-scroll {
  position: relative;
  max-height: 500px;
  width: 100%;
  overflow: auto;
  padding-bottom: 80px;
}
</style>
