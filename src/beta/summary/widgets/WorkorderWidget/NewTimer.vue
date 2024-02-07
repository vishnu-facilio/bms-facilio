<template>
  <FContainer
    v-if="days < 1 && hours < 1 && mins < 1 && secs < 1"
    display="flex"
    alignItems="flex-start"
    opacity="20%"
  >
    <!-- days -->
    <FContainer display="flex" alignItems="flex-start" v-if="days > 0">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="days < 9 && twoDigits"> 0{{ days }} </FContainer>
            <FContainer v-else>{{ days }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium "
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
          >
            <FContainer v-if="days < 2">
              {{ $t('maintenance._workorder.day') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.days') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
        padding="containerLarge containerNone"
        gap="containerXLarge"
        paddingTop="containerNone"
        height="22px"
      >
        <FText appearance="headingMed14" color="textMain">
          <FContainer v-if="days > 0">:</FContainer>
        </FText>
      </FContainer>
    </FContainer>

    <!-- hours -->
    <FContainer display="flex" alignItems="flex-start">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="hours < 9 && twoDigits">
              0{{ hours }}
            </FContainer>
            <FContainer v-else>{{ hours }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium "
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
          >
            <FContainer v-if="hours < 2">
              {{ $t('maintenance._workorder.hour') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.hours') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
        padding="containerLarge containerNone"
        gap="containerXLarge"
        paddingTop="containerNone"
        height="22px"
      >
        <FText appearance="headingMed14" color="textMain">
          <FContainer>:</FContainer>
        </FText>
      </FContainer>
    </FContainer>

    <!-- mins -->
    <FContainer display="flex" alignItems="flex-start">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="mins < 9 && twoDigits"> 0{{ mins }} </FContainer>
            <FContainer v-else>{{ mins }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
            alignItems="center"
          >
            <FContainer v-if="mins < 2">
              {{ $t('maintenance._workorder.min') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.mins') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
        padding="containerLarge containerNone"
        gap="containerXLarge"
        paddingTop="containerNone"
        height="22px"
      >
        <FText appearance="headingMed14" color="textMain">
          <FContainer v-if="days <= 0">:</FContainer>
        </FText>
      </FContainer>
    </FContainer>

    <!-- secs -->
    <FContainer v-if="days <= 0" display="flex" alignItems="flex-start">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="secs < 9 && twoDigits"> 0{{ secs }} </FContainer>
            <FContainer v-else>{{ secs }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
          >
            <FContainer v-if="secs < 2">
              {{ $t('maintenance._workorder.sec') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.secs') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
  <FContainer v-else display="flex" alignItems="flex-start">
    <!-- days -->
    <FContainer display="flex" alignItems="flex-start" v-if="days > 0">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="days < 9 && twoDigits"> 0{{ days }} </FContainer>
            <FContainer v-else>{{ days }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium "
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
          >
            <FContainer v-if="days < 2">
              {{ $t('maintenance._workorder.day') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.days') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
        padding="containerLarge containerNone"
        gap="containerXLarge"
        paddingTop="containerNone"
        height="22px"
      >
        <FText appearance="headingMed14" color="textMain">
          <FContainer v-if="days > 0">:</FContainer>
        </FText>
      </FContainer>
    </FContainer>

    <!-- hours -->
    <FContainer display="flex" alignItems="flex-start">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="hours < 9 && twoDigits">
              0{{ hours }}
            </FContainer>
            <FContainer v-else>{{ hours }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium "
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
          >
            <FContainer v-if="hours < 2">
              {{ $t('maintenance._workorder.hour') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.hours') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
        padding="containerLarge containerNone"
        gap="containerXLarge"
        paddingTop="containerNone"
        height="22px"
      >
        <FText appearance="headingMed14" color="textMain">
          <FContainer>:</FContainer>
        </FText>
      </FContainer>
    </FContainer>

    <!-- mins -->
    <FContainer display="flex" alignItems="flex-start">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="mins < 9 && twoDigits"> 0{{ mins }} </FContainer>
            <FContainer v-else>{{ mins }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
            alignItems="center"
          >
            <FContainer v-if="mins < 2">
              {{ $t('maintenance._workorder.min') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.mins') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
        padding="containerLarge containerNone"
        gap="containerXLarge"
        paddingTop="containerNone"
        height="22px"
      >
        <FText appearance="headingMed14" color="textMain">
          <FContainer v-if="days <= 0">:</FContainer>
        </FText>
      </FContainer>
    </FContainer>

    <!-- secs -->
    <FContainer v-if="days <= 0" display="flex" alignItems="flex-start">
      <FContainer
        display="flex"
        alignItems="center"
        justifyContent="center"
        flex-direction="column"
      >
        <FContainer
          display="flex"
          padding="containerMedium containerLarge"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
          paddingTop="containerNone"
        >
          <FText appearance="headingMed14" color="textMain">
            <FContainer v-if="secs < 9 && twoDigits"> 0{{ secs }} </FContainer>
            <FContainer v-else>{{ secs }}</FContainer>
          </FText>
        </FContainer>
        <FContainer
          display="flex"
          padding="containerLarge containerMedium"
          justifyContent="center"
          alignItems="center"
          gap="containerXLarge"
        >
          <FText
            appearance="captionReg12"
            color="textCaption"
            text-transform="uppercase"
          >
            <FContainer v-if="secs < 2">
              {{ $t('maintenance._workorder.sec') }}
            </FContainer>
            <FContainer v-else>
              {{ $t('maintenance._workorder.secs') }}
            </FContainer>
          </FText>
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
// import moment from 'moment-timezone'
import { FContainer, FText } from '@facilio/design-system'
export default {
  props: ['time', 'color', 'twoDigits', 'staticTime', 'static', 'duration'],
  data() {
    return {
      days: '',
      hours: '',
      mins: '',
      secs: '',
      bgColor: 'color: ' + this.color + ';',
      intervalObj: null,
    }
  },
  components: {
    FContainer,
    FText,
  },
  watch: {
    time: function() {
      this.initCounter()
    },
    staticTime: {
      handler: function(newValue, oldValue) {
        if (newValue !== oldValue) {
          if (this.staticTime) {
            this.initCounter()
          }
        }
      },
    },
  },
  mounted() {
    this.initCounter()
  },
  methods: {
    initCounter() {
      let dateObj = new Date(this.time)
      let date
      let currentTime = Date.now()
      if (this.staticTime) {
        this.days = ''
        this.hours = ''
        this.mins = ''
        this.secs = ''
        if (this.intervalObj) {
          clearInterval(this.intervalObj)
          this.intervalObj = null
        }
        date = dateObj / 1000
        if (date < 0 && date > -2) {
          date = 0
        }
        let d = Math.floor(date / (24 * 60 * 60))
        let h = Math.floor((date % (24 * 60 * 60)) / 3600)
        let m = Math.floor((date % 3600) / 60)
        let s = Math.floor((date % 3600) % 60)

        this.days = d >= 0 ? d : 0
        this.hours = h >= 0 ? h : 0
        this.mins = m >= 0 ? m : 0
        this.secs = s >= 0 ? s : 0
      } else if (this.static) {
        this.days = ''
        this.hours = ''
        this.mins = ''
        this.secs = ''
        date = this.duration
        if (date < 0 && date > -2) {
          date = 0
        }
        let d = Math.floor(date / (24 * 60 * 60))
        let h = Math.floor((date % (24 * 60 * 60)) / 3600)
        let m = Math.floor((date % 3600) / 60)
        let s = Math.floor((date % 3600) % 60)

        this.days = d >= 0 ? d : 0
        this.hours = h >= 0 ? h : 0
        this.mins = m >= 0 ? m : 0
        this.secs = s >= 0 ? s : 0
      } else {
        date = (currentTime - dateObj) / 1000
        if (date < 0 && date > -2) {
          date = 0
        }
        let d = Math.floor(date / (24 * 60 * 60))
        let h = Math.floor((date % (24 * 60 * 60)) / 3600)
        let m = Math.floor((date % 3600) / 60)
        let s = Math.floor((date % 3600) % 60)

        this.days = d >= 0 ? d : 0
        this.hours = h >= 0 ? h : 0
        this.mins = m >= 0 ? m : 0
        this.secs = s >= 0 ? s : 0
        this.startCounter()
      }
    },
    startCounter() {
      if (this.intervalObj) {
        clearInterval(this.intervalObj)
        this.intervalObj = null
      }

      let self = this
      this.intervalObj = setInterval(function() {
        self.secs++
        if (self.secs >= 60) {
          self.secs = 0
          self.mins++
        }

        if (self.mins >= 60) {
          self.mins = 0
          self.hours++
        }

        if (self.hours >= 24) {
          self.hours = 0
          self.days++
        }
      }, 1000)
    },
  },
}
</script>
