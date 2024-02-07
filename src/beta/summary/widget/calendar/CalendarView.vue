<template>
  <FContainer height="100%" width="100%">
    <FContainer
      display="flex"
      justifyContent="space-between"
      alignItems="center"
      height="45px"
      padding="containerNone containerXLarge"
      border="1px solid"
      borderColor="borderNeutralGrey02Subtle"
      borderTopLeftRadius="medium"
      borderTopRightRadius="medium"
    >
      <FContainer display="flex">
        <FButton appearance="secondary" size="small" @click="todayClicked">
          {{ $t('calendar.today') }}
        </FButton>
        <FDivider height="25px" width="0px" marginLeft="containerLarge" />
      </FContainer>
      <WeekPicker
        :resetPicker="resetPicker"
        @goToPreviousWeek="goToPreviousWeek"
        @goToCurrentWeek="goToCurrentWeek"
        @goToNextWeek="goToNextWeek"
      />
      <FContainer display="flex" padding="containerXLarge">
        <!-- <FButton appearance="secondary" size="small" split disabled>
          {{ $t('calendar.only_preferred_events') }}
        </FButton> -->
        <FDivider height="25px" width="0px" marginLeft="containerLarge" />
        <FText
          appearance="headingMed14"
          color="textMain"
          paddingLeft="sectionXSmall"
          paddingTop="containerMedium"
        >
          {{ $t('calendar.week') }}
        </FText>
      </FContainer>
    </FContainer>
    <FContainer height="95%" width="100%" display="flex" flexDirection="column">
      <FCalendar
        ref="connected-calendar"
        :calendarEvents="calendarEvents"
        calendarView="week"
        :timezone="$timezone"
        :availableViews="['week']"
        :timeStep="3"
      />
      <FContainer
        height="35px"
        width="35px"
        position="absolute"
        top="80%"
        right="16px"
        borderRadius="medium"
        display="flex"
        justifyContent="center"
        alignItems="center"
        cursor="pointer"
        backgroundColor="backgroundPrimaryHovered"
        zIndex="13"
      >
        <FPopover placement="top" trigger="clickToToggle">
          <FContainer
            slot="content"
            height="300px"
            width="265px"
            borderRadius="medium"
            padding="containerXLarge"
          >
            <FContainer
              textAlign="left"
              fontStyle="italic"
              fontSize="14px"
              fontWeight="450"
              color="textMain"
              textTransform="uppercase"
            >
              {{ $t('calendar.preferred_event') }}
            </FContainer>
            <FContainer
              v-for="legend in calendarLegendOptions"
              :key="legend.color"
              :backgroundColor="legend.color"
              height="30px"
              display="flex"
              alignItems="center"
              borderRadius="medium"
              marginBottom="containerXLarge"
              marginTop="containerXLarge"
              fontWeight="400"
              color="textDefault"
              padding="containerXLarge"
              fontSize="14px"
            >
              <FContainer
                height="20px"
                width="20px"
                display="flex"
                justifyContent="center"
                alignItems="center"
                borderRadius="low"
                backgroundColor="backgroundMidgroundSubtle"
              >
                {{ `P${legend.priority}` }}
              </FContainer>
              <FContainer marginLeft="containerLarge" fontWeight="400">
                {{ $getProperty(legend, 'type', '') }}
              </FContainer>
            </FContainer>
          </FContainer>
          <fc-icon
            group="map-travel"
            name="compass"
            size="20"
            color="#FFF"
          ></fc-icon>
        </FPopover>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import {
  FContainer,
  FButton,
  FCalendar,
  FPopover,
  FText,
  FDivider,
} from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import WeekPicker from './WeekPicker.vue'

const calendarLegendOptions = [
  {
    priority: 1,
    type: 'Weekday',
    color: 'backgroundAccentBlueLight',
    borderColor: 'backgroundAccentBlueMedium',
  },
  {
    priority: 2,
    type: 'Seasonal',
    color: 'backgroundAccentYellowLight',
    borderColor: 'backgroundAccentYellowMedium',
  },
  {
    priority: 3,
    type: 'Month',
    color: 'backgroundAccentPinkLight',
    borderColor: 'backgroundAccentPinkMedium',
  },
  {
    priority: 4,
    type: 'Regular Maintenance',
    color: 'backgroundAccentVioletLight',
    borderColor: 'backgroundAccentVioletMedium',
  },
  {
    priority: 5,
    type: 'Holiday',
    color: 'backgroundAccentCyanLight',
    borderColor: 'backgroundAccentCyanMedium',
  },
  {
    priority: 6,
    type: 'One time event',
    color: 'backgroundNeutralGrey01Subtle',
    borderColor: 'backgroundNeutralGrey01Medium',
  },
]
export default {
  name: 'CalendarView',
  components: {
    FContainer,
    FButton,
    FCalendar,
    FPopover,
    FText,
    FDivider,
    WeekPicker,
  },
  data: () => ({
    showCalendarLegend: false,
    calendarEvents: [],
    calendarLegendOptions,
    resetPicker: false,
  }),
  computed: {
    calendarComponent() {
      let { $refs } = this
      return this.$getProperty($refs, 'connected-calendar', {})
    },
    isTodaySelected() {
      let { calendarComponent } = this
      let { selectedDate, currentDay } = calendarComponent || {}

      return currentDay === selectedDate
    },
  },
  methods: {
    todayClicked() {
      let { calendarComponent } = this
      this.resetPicker = true
      if (!isEmpty(calendarComponent)) {
        calendarComponent.gotoToday()
      }
    },
    goToPreviousWeek({ startDate, endDate }) {
      let { calendarComponent } = this
      if (!isEmpty(calendarComponent)) {
        calendarComponent.gotoPrevious()
      }
      this.loadEvents({ startDate, endDate })
    },
    goToCurrentWeek({ startDate, endDate }) {
      this.loadEvents({ startDate, endDate })
      this.resetPicker = false
    },
    goToNextWeek({ startDate, endDate }) {
      let { calendarComponent } = this
      if (!isEmpty(calendarComponent)) {
        calendarComponent.gotoNext()
      }
      this.loadEvents({ startDate, endDate })
    },
    toggleLegend(value) {
      this.showCalendarLegend = value
    },
    async loadEvents({ startDate, endDate }) {
      let params = {}
      if (!isEmpty(startDate) && !isEmpty(endDate)) {
        params = {
          ...params,
          calendarId: this.$getProperty(this, '$attrs.id', null),
          startTime: startDate,
          endTime: endDate,
        }
      }

      let { error, data } = await API.get(
        'v3/calendar/getCalendarSlots',
        params,
        { force: true }
      )

      if (!error) {
        let list = this.$getProperty(data, 'result', [])
        this.calendarEvents = list
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
    },
  },
}
</script>
