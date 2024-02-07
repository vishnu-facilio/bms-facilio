<template>
  <div class="spacebookingdetails-card" ref="spaceBookingOverviewCard">
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
                {{ formatDateWithoutTime(details.bookingStartTime) }} -
                {{ formatDateWithoutTime(details.bookingEndTime) }}
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
                  {{ `${getCount}` }}
                  {{ $t('setup.spaceBooking.attendees') }}
                </div>
              </div>
              <div class="attendee-container">
                <div class="avatar-container" v-if="getCount < 5">
                  <UserAvatar
                    class="pointer pB5 pT5"
                    size="md"
                    :user="getHost"
                    iconClass="icon text-center icon-xxl"
                  ></UserAvatar>
                  <UserAvatar
                    v-for="(user, index) in getAttendeeObject"
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
                    :avatars="getAttendeeObject"
                    :count="2"
                    iconClass="icon text-center icon-xxl"
                  >
                  </GroupAvatar>
                </div>
              </div>
            </div>
          </div>

          <div class="inline-block description" v-if="details.description">
            <div class="flex-middle">
              <div class="icon-container">
                <inline-svg
                  src="svgs/employeePortal/description"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
              </div>
              <div id="secondarydescription" class="hide-container">
                <p>
                  {{ `${details.description}` }}
                </p>
              </div>
              <div class="width100" id="more">
                <div class="width100 wrapper pL15 pR15">
                  <div class="width100 box">
                    <div class="float"></div>
                    <p>
                      {{ `${details.description}` }}
                    </p>
                  </div>
                </div>
              </div>
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
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import GroupAvatar from 'src/components/GroupedAvatar/GroupedAvatarUser.vue'
import { mapGetters } from 'vuex'

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
    GroupAvatar,
  },
  data() {
    return {
      editFormVisibility: false,
      forms: [],
      recordData: null,
      space: null,
      role: null,
      peopleType: {
        internalAttendee: null,
        externalAttendee: null,
      },
      notesModuleName: 'spaceBookingNotes',
      attachmentsModuleName: 'spaceBookingAttachments',
      booking: null,
    }
  },

  computed: {
    spacebookingData() {
      return this.details || this.booking
    },
    spaceId() {
      return this.spacebookingData?.space?.id
        ? this.spacebookingData.space.id
        : null
    },
    getBuilding() {
      let buildingName = ''
      if (this.space?.building?.name) {
        buildingName += `${this.space.building.name} - `
      }
      return buildingName
    },
    getFloor() {
      let floor = ''
      if (this.space?.floor?.name) {
        floor += `${this.space.floor.name}`
      }
      return floor
    },
    formObj() {
      if (this.forms.length) {
        return this.forms[0]
      }
      return null
    },
    getHost() {
      let host = null
      if (this.spacebookingData) {
        host = this.spacebookingData.host
        host.value = 'Host'
      }
      return host
    },
    getAttendee() {
      let attendee = {
        count: 0,
        hostCount: 0,
        internalAttendeesCount: 0,
        externalAttendeeCount: 0,
        internalAttendees: [],
        externalAttendee: [],
        allAttendee: {},
      }
      if (this.spacebookingData && this.spacebookingData.host) {
        attendee.hostCount = 1
      }
      if (
        this.spacebookingData &&
        this.spacebookingData.internalAttendees &&
        this.spacebookingData.internalAttendees.length
      ) {
        attendee.internalAttendeesCount = this.spacebookingData.internalAttendees.length
        attendee.internalAttendees = this.spacebookingData.internalAttendees.map(
          v => ({
            ...v,
            value: 'Internal Attendee',
          })
        )
      }
      if (
        this.spacebookingData &&
        this.spacebookingData.externalAttendee &&
        this.spacebookingData.externalAttendee.length
      ) {
        attendee.externalAttendeeCount = this.spacebookingData.externalAttendee.length
        attendee.externalAttendee = this.spacebookingData.externalAttendee.map(
          v => ({
            ...v,
            value: 'External Attendee',
          })
        )
        attendee.externalAttendee = this.spacebookingData.externalAttendee
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
    getCount() {
      let attendee = this.getAttendee
      return attendee.count
    },
    getAttendeeObject() {
      let attendee = this.getAttendee
      return attendee.allAttendee
    },
    sectionHeight() {
      const sectionDOM = document.getElementById('secondarydescription')
      return sectionDOM ? sectionDOM.offsetHeight : 0
    },
  },
  async mounted() {
    await this.loadFormsList()
    this.recordData = this.spacebookingData
    if (this.spaceId) {
      await this.loadSpaceDetails()
    }
    eventBus.$on('location-changed', id => {
      if (id) {
        this.isLoading = true
        this.loadSpaceDetails(id)
      }
    })
    if (this.spacebookingData?.description) {
      this.getMore()
    }
    this.afterloadHook()
    this.autoResize()
  },
  methods: {
    getWidgetHeight() {
      //
    },
    afterloadHook() {
      // after data load hook
    },
    ...mapGetters(['getUser']),
    async loadSpaceDetails(id) {
      let { error, data } = await API.get(
        `/v3/modules/data/summary?id=${id || this.spaceId}&moduleName=space`
      )
      if (error) {
        this.$message.success('Error while loading ')
      } else {
        let { space } = data
        if (space != null) {
          this.space = space
          this.isLoading = false
        }
      }
    },
    formatDate(date, exclTime, onlyTime) {
      return this.$options.filters.formatDate(date, exclTime, onlyTime)
    },
    formatDateWithoutTime(date) {
      return this.formatDate(date, false, false)
    },

    getMore() {
      let root = document.getElementById('more')
      let self = this
      const wrapper = document.querySelector('.wrapper')
      const box = wrapper.querySelector('.box')
      const float = wrapper.querySelector('.float')
      let btn = wrapper.querySelector('button')
      root.style.setProperty('--lines', 2)
      checkInit()

      function init() {
        float.innerHTML = '<button class="">... Read More</div>'
        btn = wrapper.querySelector('button')
        btn.addEventListener('click', function() {
          if (wrapper.classList.contains('expanded')) {
            wrapper.classList.remove('expanded')
            this.innerHTML = '... Read More'
            self.autoResize()
          } else {
            wrapper.classList.add('expanded')
            this.innerHTML = 'View Less'
            self.autoResize()
          }
        })
      }

      function destroy() {
        float.innerHTML = ''
        wrapper.classList.add('expanded')
      }

      function checkInit() {
        wrapper.classList.remove('expanded')
        let hasOverflow = box.scrollHeight > box.clientHeight ? true : false
        if (hasOverflow) {
          init()
        } else {
          destroy()
        }
      }

      window.addEventListener('resize', function() {
        checkInit()
      })

      document.addEventListener('DOMContentLoaded', function() {
        checkInit()
      })
    },
    getDayEnumValue(day) {
      if (day && day > 0 && day < 8) {
        return this.dayEnumMap[day]
      }
      return ''
    },
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
      this.$nextTick(() => {
        setTimeout(() => {
          let container = this.$refs['spaceBookingDetailCard']
          if (container) {
            let height = container.scrollHeight + 20
            let width = container.scrollWidth
            if (this.resizeWidget) {
              this.resizeWidget({ height: height, width })
            }
          }
        })
      })
    },
  },
}
</script>

<style>
.spacebookingdetails-container {
  height: 100%;
}
.spacebookingdetails-card p {
  margin: 0px;
  font-size: 14px;
  line-height: 20px;
}
.spacebookingdetails-card .show-hide-text {
  display: flex;
  flex-wrap: wrap;
}
.spacebookingdetails-card .show-hide-text a {
  order: 2;
}
.spacebookingdetails-card .show-hide-text p {
  position: relative;
  overflow: hidden;
  max-height: 60px;
  line-height: 20px;
}
.spacebookingdetails-card .show-hide-text p:after {
  content: '';
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100px;
  height: 20px;
  background: linear-gradient(to right, rgba(255, 255, 255, 0) 0%, white 100%);
}
@supports (-webkit-line-clamp: 3) {
  .spacebookingdetails-card .show-hide-text p {
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }
  .spacebookingdetails-card .show-hide-text p:after {
    display: none;
  }
}

.spacebookingdetails-card .show-less {
  display: none;
}
.spacebookingdetails-card .show-less:target {
  display: block;
}
.spacebookingdetails-card .show-less:target ~ p {
  display: block;
  max-height: 100%;
}
.spacebookingdetails-card .show-less:target + a {
  display: none;
}

.spacebookingdetails-card .wrapper p {
  font-size: 14px;
  line-height: 20px;
}
:root {
  --line-height: 1.5;
  --lines: 35;
  line-height: var(--line-height);
}

h1 {
  text-align: center;
}

.wrapper {
  font-size: 20px;
  position: relative;
  display: flex;
  height: calc(var(--line-height) * var(--lines) * 1em);
  overflow: hidden;
}

p {
  position: relative;
  margin: 0;
  pointer-events: none;
}

.box {
  text-align: justify;
}

.float {
  float: right;
  height: 100%;
  margin-left: 15px;
  display: flex;
  align-items: flex-end;
  shape-outside: inset(calc(100% - 1em) 0 0);
}
.spacebookingdetails-container .description button {
  font-size: 0.7em;
  padding: 0;
  margin: 0;
  /* height: calc(var(--line-height) * 1em); */
  border: 0;
  background: transparent;
  color: #489edc;
}

.wrapper.expanded {
  height: auto;
}

/* stle2 */

.spacebookingdetails-container .header {
  /* min-height: 32px; */
  min-height: 48px;
  max-height: 48px;
  padding: 20px 10px 10px 20px;
}
.spacebookingdetails-container .description {
  padding: 10px 20px 10px 20px;
}
.spacebookingdetails-container .description .flex-middle {
  align-items: normal;
}
.hide-container {
  position: absolute;
  left: -1000px;
  top: -1000px;
}
.spacebookingdetails-card .detail-container {
  min-height: 32px;
  max-height: 64px;
  padding: 10px 20px 10px 20px;
}
.spacebookingdetails-card .detail-container .flex-middle {
  align-items: normal;
}
.spacebookingdetails-container .people-container {
  padding: 10px 20px 10px 20px;
}
.spacebookingdetails-container .people-container .flex-middle {
  align-items: normal;
}
.spacebookingdetails-card .space-container {
  display: flex;
  flex-direction: column;
}
.spacebookingdetails-card .space-container .main-content {
  font-size: 14px;
}
.spacebookingdetails-card .space-container .icon-container {
  align-items: normal;
}
.spacebookingdetails-card .space-container .sub-content {
  font-size: 12px;
  display: flex;
  flex-direction: row;
  padding-top: 5px;
  color: #5f6368;
}
.spacebookingdetails-card .attendee-container {
  padding-left: 48px;
  margin-left: -8px;
}
.spacebookingdetails-card .attendee-container .avatar-container {
  display: flex;
  flex-direction: column;
}
.inline-grid {
  display: inline-grid;
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
