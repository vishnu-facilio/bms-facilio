<template>
  <div v-if="isLoading" style="width:450px;background-color: #fff;">
    <spinner :show="isLoading" size="80"></spinner>
  </div>
  <div
    class="spacebookingdetails-card height100"
    ref="spaceBookingOverviewCard"
    v-else-if="!isLoading && booking"
  >
    <div class="booking-header">
      <span class="booking-state"
        ><div
          class="color-box"
          :style="{ 'background-color': selectedEvent.color }"
        ></div></span
      >{{ booking.name }}
    </div>
    <div class="spacebookingdetails-container" ref="spaceBookingDetailCard">
      <el-row :gutter="20">
        <el-col :span="24" class="inline-grid">
          <div class="inline-block   header">
            <div class="flex-middle">
              <inline-svg
                src="svgs/employeePortal/calendar"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
              <div class=" pL15 ellipsis main-content">
                {{ formatDateWithoutTime(booking.bookingStartTime) }} -
                {{ formatDateWithoutTime(booking.bookingEndTime) }}
              </div>
            </div>
          </div>
          <div class="inline-block  detail-container" v-if="space">
            <div class="flex-middle">
              <div class="icon-container">
                <inline-svg
                  src="svgs/employeePortal/new_location"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
              </div>
              <div class="space-container">
                <div class=" pL15 ellipsis main-content">
                  {{ `${space.name}` }}
                </div>
                <div class=" pL15 ellipsis sub-content">
                  <div v-if="space.building">
                    {{ `${space.building.name} ` }}
                  </div>
                  <div v-if="space.floor">
                    {{ `, ${space.floor.name}` }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div>
            <div class="inline-block people-container">
              <div class="flex-middle pB5">
                <inline-svg
                  src="svgs/employeePortal/people"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
                <div class=" pL15 ellipsis main-content">
                  {{ `${getAttendeeCount}` }}
                  {{ $t('setup.spaceBooking.attendees') }}
                </div>
              </div>
              <div class="attendee-container">
                <div class="avatar-container" v-if="getAttendeeCount < 5">
                  <UserAvatar
                    class="pointer pB5 pT5"
                    size="md"
                    :user="getHost"
                    iconClass="icon text-center icon-xxl"
                  ></UserAvatar>
                  <UserAvatar
                    v-for="(user, index) in getAttendeesObject"
                    :key="index"
                    class="pointer pB5 pT5"
                    size="md"
                    :user="user"
                    iconClass="icon text-center icon-xxl"
                  ></UserAvatar>
                </div>
                <div v-else>
                  <UserAvatar
                    class="pointer pB5 pT5"
                    size="md"
                    :user="getHost"
                    iconClass="icon text-center icon-xxl"
                  ></UserAvatar>
                  <GroupAvatar
                    size="md"
                    :avatars="getAttendeesObject"
                    :count="2"
                    iconClass="icon text-center icon-xxl"
                  >
                  </GroupAvatar>
                </div>
              </div>
            </div>
          </div>

          <div class="inline-block description" v-if="booking.description">
            <div class="flex-middle" style="height:inherit">
              <div class="icon-container mR15">
                <inline-svg
                  src="svgs/employeePortal/description"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
              </div>
              <read-more
                more-str="read more"
                :text="booking.description"
                link="#"
                less-str="read less"
                :max-chars="160"
                class="read-more"
              ></read-more>
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
import SpaceBookingOverview from 'src/components/SpaceBookingOverview.vue'
import { API } from '@facilio/api'

export default {
  extends: SpaceBookingOverview,
  props: ['bookingId', 'selectedEvent'],
  data() {
    return {
      isLoading: false,
    }
  },
  computed: {
    getAttendees() {
      let attendee = {
        count: 0,
        hostCount: 0,
        internalAttendeesCount: 0,
        externalAttendeeCount: 0,
        internalAttendees: [],
        externalAttendee: [],
        allAttendee: {},
      }
      if (this.booking && this.booking.host) {
        attendee.hostCount = 1
      }
      if (
        this.booking &&
        this.booking.internalAttendees &&
        this.booking.internalAttendees.length
      ) {
        attendee.internalAttendeesCount = this.booking.internalAttendees.length
        attendee.internalAttendees = this.booking.internalAttendees.map(v => ({
          ...v,
          value: 'Internal Attendee',
        }))
      }
      if (
        this.booking &&
        this.booking.externalAttendee &&
        this.booking.externalAttendee.length
      ) {
        attendee.externalAttendeeCount = this.booking.externalAttendee.length
        attendee.externalAttendee = this.booking.externalAttendee.map(v => ({
          ...v,
          value: 'External Attendee',
        }))
        attendee.externalAttendee = this.booking.externalAttendee
      }
      attendee.count +=
        attendee.hostCount +
        attendee.internalAttendeesCount +
        attendee.externalAttendeeCount
      attendee.allAttendee = {
        ...attendee.internalAttendees,
        ...attendee.externalAttendee,
      }

      return attendee
    },
    getAttendeeCount() {
      let attendee = this.getAttendees
      return attendee.count
    },
    getAttendeesObject() {
      let attendee = this.getAttendees
      return attendee.allAttendee
    },
  },
  async mounted() {
    if (this.bookingId) {
      this.isLoading = true
      await this.loadRecord(this.bookingId)

      await this.loadSpaceDetails(this.spaceId)

      this.isLoading = false
    }
  },
  methods: {
    async loadRecord(id) {
      this.booking = {}
      let { data, error } = await API.post('/v3/modules/data/summary', {
        moduleName: this.moduleName,
        id,
      })
      if (error) {
        this.$message.error(error)
      } else {
        this.booking = data.spacebooking || null
      }
    },
  },
}
</script>
<style lang="scss">
.read-more {
  position: relative;
  span {
    position: absolute;
    bottom: 0;
    right: 0;
    background-color: #fff;
  }
}
.booking-header {
  line-height: 28px;
  font-size: 22px;
  font-weight: 400;
  color: #3c4043;
  word-wrap: break-word;
  letter-spacing: 0.2px;
  padding: 15px 20px 10px;
  display: flex;
}
.booking-state {
  display: flex;
  align-items: center;
  margin-right: 15px;
  margin-left: 2px;
}
.color-box {
  width: 14px;
  height: 14px;
  background-color: rgb(0, 83, 204);
  border-radius: 4px;
}
</style>
