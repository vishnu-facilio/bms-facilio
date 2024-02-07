<template>
  <div class="properties-section">
    <div class="new-header-container pT20">
      <div class="new-header-text relative">
        <div class="fc-setup-modal-title" v-if="nonReservableResource">
          {{ markerType == 'SPACE' ? 'Space properties' : 'Desk properties' }}
        </div>
        <div class="fc-setup-modal-title" v-else>
          {{ markerType == 'SPACE' ? 'Book Space' : 'Book Desk' }}
        </div>

        <div class="flex-middle">
          <div
            content="View Summary"
            v-tippy="{ arrow: true }"
            v-if="!isPortal && feature.desk && feature.desk.id"
            @click="openRecordSummary(feature.desk.id)"
          >
            <inline-svg
              src="svgs/external-link2"
              iconClass="icon icon-sm-md fill-blue mR10 pointer"
            ></inline-svg>
          </div>
          <div class="fc-setup-modal-close f18 pointer" @click="handleClose">
            <i class="el-icon-close fwBold"></i>
          </div>
        </div>
      </div>
    </div>
    <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
      <div class="fc-black-22 bold pT10 pB10">
        {{ feature.label }}
      </div>
      <el-row class="pT10" v-if="markerType == 'DESK'">
        <el-col :span="24">
          <div class="fc-input-label-txt pB5">
            Desk type
          </div>
          <div class="fc-black-14 text-left bold">
            {{ deskTypeEnumMap[feature.desk.deskType] }}
          </div>
        </el-col>
      </el-row>
      <el-row
        class="pT10"
        v-if="markerType == 'DESK' && feature.desk.department"
      >
        <el-col :span="24">
          <div class="fc-input-label-txt pB5">
            Department
          </div>
          <div class="fc-black-14 text-left bold">
            {{ feature.desk.department.name }}
          </div>
        </el-col>
      </el-row>

      <!-- SPACE CATRGORY -->
      <!-- No category don't display section -->
      <el-row
        class="pT10"
        v-if="
          markerType == 'SPACE' &&
            feature.space.spaceCategory &&
            feature.space.spaceCategory.name
        "
      >
        <el-col :span="24">
          <div class="fc-input-label-txt pB5">
            Space category
          </div>
          <div class="fc-black-14 text-left bold">
            {{ feature.space.spaceCategory.name }}
          </div>
        </el-col>
      </el-row>
      <el-row class="pT20" v-if="markerType == 'SPACE'">
        <el-col :span="24">
          <div class="fc-input-label-txt pB5">
            Reservable
          </div>
          <div class="fc-black-14 text-left bold">
            {{ feature.isReservable ? 'Yes' : 'No' }}
          </div>
        </el-col>
      </el-row>
      <el-row class="pT20" v-if="markerType == 'SPACE' && facility">
        <el-col :span="24">
          <div class="fc-input-label-txt pB5">
            Capacity
          </div>
          <div
            class="fc-black-14 text-left bold"
            style="color: rgb(163, 166, 177);"
          >
            {{ facility.usageCapacity }}
          </div>
        </el-col>
      </el-row>
      <el-row
        class="pT20"
        v-if="
          markerType == 'SPACE' &&
            facility &&
            facility.amenities &&
            amenities.length
        "
      >
        <el-col :span="24">
          <div class="fc-input-label-txt pB5">
            Amenities
          </div>
          <div class="fc-black-14 text-left bold d-flex flex-wrap">
            <span
              class="mR10 mB10"
              v-for="(amenity, index) in facility.amenities"
              :key="index"
            >
              {{ amenity.data.name }}
            </span>
          </div>
        </el-col>
      </el-row>
      <div class="reservable-marker-section" v-if="!nonReservableResource">
        <el-row class="pT20">
          <el-col :span="24">
            <div class="fc-input-label-txt pB5">
              Slots
            </div>
            <div class="fc-black-14 text-left bold">
              {{ currentDate }}
            </div>
          </el-col>
        </el-row>
        <div class="fc-tag-align-grid">
          <el-tag
            @click="slotClicked(slotGroup)"
            class="fc-tag-booking "
            :class="[
              { active: slotGroup.groupType == 'FREE' },
              { inactive: slotGroup.groupType == 'BOOKED' },
            ]"
            v-for="(slotGroup, index) in slotsToDisplay"
            :key="index"
          >
            {{ getConcatSlotGroupTime(slotGroup) }}
          </el-tag>
        </div>

        <div class="pT30">
          <el-button
            @click="$emit('book', [])"
            class="fc-pink-btn-full-width text-capitalize f14 fwBold bR3"
          >
            Book Now
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: [
    'deskTypeEnumMap',
    'feature',
    'facility',
    'bookings',
    'currentDate',
    'isPortal',
  ],
  computed: {
    slotsToDisplay() {
      if (!this.facility || this.nonReservableResource) {
        return
      }
      let slots = this.facility.slots
      //do a bookings check here
      let groupedSlots = []

      let currentGroup = []

      let currentGroupType = 'FREE'
      //set first group type based on first slot booked or not
      if (slots && slots.length) {
        let isFirstBooked = this.isSlotBooked(slots[0])
        if (isFirstBooked) {
          currentGroupType = 'BOOKED'
        }
      }

      //combine continuous free and continuous booked slots
      //the  Collation algorith is simple . when next slot and current slot are of same type, keep appedning to the same group
      //on state transitions create a new group and push exiting group into collection

      slots.forEach(slot => {
        if (currentGroupType == 'FREE' && !this.isSlotBooked(slot)) {
          currentGroup.push(slot)
        } else if (currentGroupType == 'BOOKED' && this.isSlotBooked(slot)) {
          currentGroup.push(slot)
        }
        //FREE SLOT -> BOOKED SLOT Transition
        else if (currentGroupType == 'FREE' && this.isSlotBooked(slot)) {
          if (currentGroup.length) {
            groupedSlots.push({ slots: currentGroup, groupType: 'FREE' })
            currentGroup = [slot]
          }
          currentGroupType = 'BOOKED'
        }
        //BOOKED SLOT->FREE SLOT Transition
        else if (currentGroupType == 'BOOKED' && !this.isSlotBooked(slot)) {
          if (currentGroup.length) {
            groupedSlots.push({ groupType: 'BOOKED', slots: currentGroup })
            currentGroup = [slot]
          }
          currentGroupType = 'FREE'
        }
      })
      //push last left over group, no transition in the end
      if (currentGroup.length) {
        //dont push any empty groups
        groupedSlots.push({ groupType: currentGroupType, slots: currentGroup })
      }

      return groupedSlots
    },
    markerType() {
      if (this.feature.desk) {
        return 'DESK'
      } else if (this.feature.space) {
        return 'SPACE'
      } else {
        return null
      }
    },
    nonReservableResource() {
      return (
        (this.markerType == 'DESK' && this.feature.desk.deskType == 1) ||
        (this.markerType == 'SPACE' && this.feature.isReservable == false)
      )
    },
  },
  methods: {
    openRecordSummary(id) {
      let viewname = 'alldesks'
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('desks', pageTypes.OVERVIEW) || {}

        if (name) {
          let routeData = this.$router.resolve({
            name,
            params: {
              viewname,
              id,
            },
          })
          window.open(routeData.href, '_blank')
        }
      } else {
        let routeData = this.$router.resolve({
          name: 'deskSummary',
          params: {
            viewname,
            id,
          },
        })
        window.open(routeData.href, '_blank')
      }
    },
    slotClicked(slotGroup) {
      if (slotGroup.groupType == 'FREE') {
        this.$emit('book', slotGroup.slots)
      }
    },
    getConcatSlotGroupTime(slotGroup) {
      return (
        this.$helpers
          .getOrgMoment(slotGroup.slots[0].slotStartTime)
          .format('HH:mm') +
        ' - ' +
        this.$helpers
          .getOrgMoment(slotGroup.slots[slotGroup.slots.length - 1].slotEndTime)
          .format('HH:mm')
      )
    },
    isSlotBooked(slot) {
      if (!this.bookings) return false
      let bookedItem = this.bookings.find(booking => {
        return booking.slot.id == slot.id
      })
      if (bookedItem) {
        return true
      } else {
        return false
      }
    },
    handleClose() {
      this.$emit('update:visibility', false)
      this.$emit('close')
    },
  },
}
</script>
<style lang="scss">
.fc-tag-align-grid {
  display: grid;
  grid-template-columns: auto auto auto;
  grid-column-gap: 10px;
  grid-row-gap: 10px;
  margin-top: 20px;
  .fc-tag-booking {
    text-align: center;
    font-weight: 500;
    font-size: 12px;
    cursor: text;
    &.active {
      color: #324056;
      background: rgb(57 179 194 / 20%);
      font-weight: 500;
      border: 1px solid #39b3c2;
      cursor: pointer;
    }
    &.inactive {
      cursor: not-allowed;
      color: #8c8c8c;
      background: #ededed;
      font-weight: 500;
      border: 1px solid #ededed;
    }
  }
}
</style>
