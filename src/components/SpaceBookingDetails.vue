<template>
  <div class="spacebookingdetails-card">
    <div class="spacebookingdetails-container" ref="spaceBookingDetailCard">
      <!-- <div class="space-booking-header">
        <div class="pull-left ">
          {{ `Booking Details` }}
        </div>
        <div class="pull-right">
          <div @click="handleEditForm()" class="sp-bk-edit-icon">
            <inline-svg
              src="svgs/employeePortal/icon-edit"
              iconClass="icon text-center icon-md vertical-bottom"
            ></inline-svg>
          </div>
        </div>
      </div> -->
      <el-row :gutter="20">
        <el-col :span="24" class="inline-grid">
          <div class="inline-block p20 bold f16">
            <div class="flex-middle">
              <inline-svg
                src="svgs/employeePortal/icon-2-title"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <div class="fc-portal-txt-14 pL10 ellipsis">
                {{ `${details.name}` }}
              </div>
            </div>
            <div class="em-scn-text" v-if="details.description">
              {{ `${details.description}` }}
            </div>
          </div>
          <div class="inline-block p20 ">
            <div class="flex-middle">
              <inline-svg
                src="svgs/employeePortal/ic-date-range_1"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <div class="fc-portal-txt-14 pL10 ellipsis">
                {{ `${bookedDate}` }}
              </div>
            </div>
          </div>
          <div class="inline-block p20 ">
            <div class="flex-middle">
              <inline-svg
                src="svgs/employeePortal/icon-2-clock"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <div class="fc-portal-txt-14 pL10 ellipsis">
                {{ `${bookedTime}` }}
              </div>
            </div>
          </div>
          <div
            class="inline-block p20 "
            v-if="details.internalAttendees && details.internalAttendees.length"
          >
            <div class="flex-middle">
              <inline-svg
                src="svgs/employeePortal/ic-people-outline"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <div class="fc-portal-txt-14 pL10 ellipsis">
                {{ `${details.internalAttendees.length} Attendees` }}
              </div>
            </div>
            <div class="p20 pL30 inline-grid min-width-350">
              <UserAvatar
                v-for="(user, index) in details.internalAttendees"
                :key="index"
                class="pointer pB10 pT10 border-bottom-1px"
                size="md"
                :user="{ id: user.id, name: user.name ? user.name : '' }"
                iconClass="icon text-center icon-xxl"
              ></UserAvatar>
            </div>
          </div>

          <div
            class="inline-block p20 "
            v-if="details.externalAttendee && details.externalAttendee.length"
          >
            <div class="flex-middle">
              <inline-svg
                src="svgs/employeePortal/ic-people-outline"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <div class="fc-portal-txt-14 pL10 ellipsis">
                {{ `${details.externalAttendee.length} External Attendees` }}
              </div>
            </div>
            <div class="p20 pL30 inline-grid min-width-350">
              <UserAvatar
                v-for="(user, index) in details.externalAttendee"
                :key="index"
                class="pointer pB10 pT10 border-bottom-1px"
                size="md"
                :user="{ id: user.id, name: user.name ? user.name : '' }"
                iconClass="icon text-center icon-xxl"
              ></UserAvatar>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div
      v-if="editFormVisibility"
      class="spacebookingdetails-container booking-form-container"
    >
      <InlineSpaceBookingForm
        v-if="editFormVisibility"
        :moduleName="moduleName"
        @closeForm="closeForm"
        @saved="handleSaveForm"
        :formData.sync="formObj"
        :recordData="recordData"
      ></InlineSpaceBookingForm>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/UserAvatarDetails'
import InlineSpaceBookingForm from '@/InlineSpaceBookingForm'
import { API } from '@facilio/api'
import moment from 'moment-timezone'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  props: [
    'widget',
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'approvalMeta',
    'hideTitleSection',
  ],
  components: {
    UserAvatar,
    InlineSpaceBookingForm,
  },
  data() {
    return {
      editFormVisibility: false,
      forms: [],
      recordData: null,
    }
  },
  computed: {
    formObj() {
      if (this.forms.length) {
        return this.forms[0]
      }
      return null
    },
    bookedDate() {
      let { bookingStartTime, bookingEndTime } = this.details
      let startDate = new moment(bookingStartTime).format('YYYY MMM DD')
      let endDate = new moment(bookingEndTime).format('YYYY MMM DD')
      return startDate === endDate ? startDate : `${startDate} - ${endDate}`
    },
    bookedTime() {
      let { bookingStartTime, bookingEndTime } = this.details
      let startTime = new moment(bookingStartTime).format('hh:mm a')
      let endTime = new moment(bookingEndTime).format('hh:mm a')
      return `${startTime} - ${endTime}`
    },
  },
  mounted() {
    this.loadFormsList()
    this.recordData = this.details
    this.autoResize()
  },
  methods: {
    handleSaveForm() {
      this.editFormVisibility = false
      eventBus.$emit('refreshdata')
    },
    async loadFormsList() {
      let url = `/v2/forms?moduleName=${this.moduleName}`

      this.isLoading = true
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { forms = [] } = data || {}
        this.$set(this, 'forms', forms)
      }
    },
    handleEditForm() {
      this.editFormVisibility = true
    },
    closeForm() {
      this.editFormVisibility = false
    },
    autoResize() {
      let height = this.$refs['spaceBookingDetailCard'].scrollHeight
      let width = this.$refs['spaceBookingDetailCard'].scrollWidth
      if (height === 0) height += 192
      else if (height === 160) height += 32
      if (this.resizeWidget) {
        this.resizeWidget({ height, width })
      }
    },
  },
}
</script>

<style>
.space-booking-header {
  font-size: 18px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  padding: 20px;
  padding-bottom: 35px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}
.spacebookingdetails-container {
  height: fit-content;
}
.spacebookingdetails-card .em-scn-text {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.29;
  letter-spacing: normal;
  padding-top: 10px;
  color: #949a9e;
  padding-left: 30px;
}
.spacebookingdetails-card .el-carousel__container {
  height: 200px;
  width: 400px;
  background: #a5fba5;
  margin: auto;
  border-radius: 8px;
}
.inline-grid {
  display: inline-grid;
}
.border-bottom-1px {
  border-bottom: 1px solid #f2f2f2;
}
.min-width-350 {
  min-width: 350px;
}
.booking-form-container .fc-pm-main-content-H {
  font-size: 18px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  padding: 20px;
  border: 0;
  border-radius: 10px;
  border-bottom: 1px solid #f0f0f0;
  border-bottom-right-radius: 0px;
  border-bottom-left-radius: 0px;
}
.booking-form-container .fp-bookings-form .f-webform-container {
  padding: 0px;
}
.booking-form-container .fp-bookings-form.height-100 {
  min-height: 500px;
}
.booking-form-container .f-webform-container .el-form .section-container {
  padding: 20px;
  border: 0px;
  padding-top: 0px;
}
.booking-form-container .f-webform-container .form-btn.secondary {
  margin: 0px;
}
.booking-form-container .loading-container {
  min-height: 500px;
}
.sp-bk-edit-icon {
  padding: 10px;
  position: absolute;
  top: 10px;
  right: 10px;
  border-radius: 20px;
  width: 35px;
  height: 35px;
  padding-top: 5px;
  padding-left: 9px;
  cursor: pointer;
}
.sp-bk-edit-icon:hover {
  background: rgba(202, 212, 216, 0.3);
  -webkit-transition: 0.2s all;
  transition: 0.2s all;
}
</style>
