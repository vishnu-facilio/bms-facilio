<template>
  <FContainer
    display="flex"
    padding="containerXxLarge"
    flexDirection="column"
    alignSelf="stretch"
  >
    <FContainer
      display="flex"
      width="100%"
      flexDirection="column"
      alignItems="flex-start"
      gap="containerLarge"
    >
      <FContainer
        display="flex"
        width="100%"
        justifyContent="space-between"
        alignItems="flex-start"
      >
        <FContainer
          display="flex"
          paddingRight="containerNone"
          alignItems="center"
        >
          <wo-timer
            :actualWorkDurationField="actualWorkDurationField"
          ></wo-timer>
        </FContainer>
        <!-- task -->
        <FContainer display="flex" flexDirection="column" alignItems="center">
          <FContainer
            display="flex"
            justifyContent="center"
            textAlign="center"
            flexDirection="column"
            gap="containerXLarge"
            padding="containerMedium containerXLarge"
          >
            <FText appearance="headingMed16" color="textMain">{{
              getTotalTask
            }}</FText>
          </FContainer>
          <FContainer
            display="flex"
            justifyContent="center"
            textAlign="center"
            padding="containerMedium"
          >
            <FText appearance="captionReg12" color="textDescription">{{
              getTaskChart
            }}</FText>
          </FContainer>
        </FContainer>
      </FContainer>

      <!-- 2nd -->
      <FContainer
        width="100%"
        display="flex"
        flexDirection="column"
        padding="containerMedium containerNone"
        backgroundColor="borderNeutralGrey02Subtler"
        borderRadius="high"
        alignItems="flex-start"
      >
        <FContainer
          display="flex"
          width="100%"
          padding="containerMedium containerXLarge"
          gap="containerXLarge"
          ><FText appearance="captionMed12" color="textCaption">
            Scheduled
          </FText>
        </FContainer>

        <FContainer
          display="flex"
          justifyContent="space-between"
          width="100%"
          alignItems="center"
          v-if="
            (this.details.scheduledStart && this.details.scheduledStart > -1) ||
              (this.details.estimatedEnd && this.details.estimatedEnd > -1)
          "
        >
          <FContainer
            display="flex"
            flexDirection="column"
            alignItems="flex-start"
          >
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              width="80px"
              v-if="
                (this.details.scheduledStart &&
                  this.details.scheduledStart > -1) ||
                  (this.details.estimatedEnd && this.details.estimatedEnd > -1)
              "
            >
              <FText
                appearance="bodyReg14"
                textAlign="center"
                color="textMain"
                >{{
                  formatDateWithOnlyTime(this.details.scheduledStart)
                }}</FText
              >
            </FContainer>
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              alignItems="center"
              justifyContent="center"
            >
              <FText
                appearance="captionReg12"
                textAlign="left"
                color="textCaption"
              >
                {{ formatDateWithoutTime(this.details.scheduledStart) }}</FText
              >
            </FContainer>
          </FContainer>
          <FContainer
            display="flex"
            width="40px"
            padding="containerMedium containerNone"
            alignItems="center"
            justifyContent="center"
          >
            <FText appearance="bodyReg14" textAlign="center" color="textMain"
              >To</FText
            >
          </FContainer>
          <FContainer
            display="flex"
            flexDirection="column"
            alignItems="flex-start"
          >
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              width="80px"
            >
              <FText
                appearance="bodyReg14"
                textAlign="center"
                color="textMain"
                >{{
                  this.details.estimatedEnd
                    ? formatDateWithOnlyTime(this.details.estimatedEnd)
                    : '--'
                }}</FText
              >
            </FContainer>
            <FContainer
              v-if="this.details.estimatedEnd"
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              alignItems="center"
              justifyContent="center"
            >
              <FText
                appearance="captionReg12"
                textAlign="left"
                color="textCaption"
              >
                {{ formatDateWithoutTime(this.details.estimatedEnd) }}</FText
              >
            </FContainer>
          </FContainer>
        </FContainer>
        <FContainer v-else justifyContent="space-between">
          <FContainer
            ><FContainer gap="10px" padding="containerSmall containerXLarge">
              <FText appearance="bodyReg14" textAlign="center" color="textMain"
                >__</FText
              >
            </FContainer>
          </FContainer>
        </FContainer>
      </FContainer>

      <!-- 3 -->
      <FContainer
        width="100%"
        display="flex"
        flexDirection="column"
        padding="containerMedium containerNone"
        backgroundColor="borderNeutralGrey02Subtler"
        borderRadius="high"
        alignItems="flex-start"
      >
        <FContainer
          display="flex"
          width="100%"
          padding="containerMedium containerXLarge"
          gap="containerXLarge"
          ><FText
            appearance="captionMed12"
            weight="500px"
            color="textCaption"
            font="Roboto"
          >
            Actual
          </FText>
        </FContainer>

        <FContainer
          display="flex"
          justifyContent="space-between"
          width="100%"
          alignItems="center"
          v-if="
            (this.details.actualWorkStart &&
              this.details.actualWorkStart > -1) ||
              (this.details.actualWorkEnd && this.details.actualWorkEnd > -1)
          "
        >
          <FContainer
            display="flex"
            flexDirection="column"
            alignItems="flex-start"
          >
            <FContainer
              display="flex"
              alignItems="center"
              justifyContent="center"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              width="80px"
              v-if="
                (this.details.actualWorkStart &&
                  this.details.actualWorkStart > -1) ||
                  (this.details.actualWorkEnd &&
                    this.details.actualWorkEnd > -1)
              "
            >
              <FText
                appearance="bodyReg14"
                textAlign="center"
                color="textMain"
                >{{
                  formatDateWithOnlyTime(this.details.actualWorkStart)
                }}</FText
              >
            </FContainer>
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              alignItems="center"
              justifyContent="center"
            >
              <FText
                appearance="captionReg12"
                textAlign="left"
                color="textCaption"
              >
                {{ formatDateWithoutTime(this.details.actualWorkStart) }}</FText
              >
            </FContainer>
          </FContainer>
          <FContainer
            display="flex"
            width="40px"
            padding="containerMedium containerNone"
            alignItems="center"
            justifyContent="center"
          >
            <FText appearance="bodyReg14" textAlign="center" color="textMain"
              >To</FText
            >
          </FContainer>
          <FContainer
            display="flex"
            flexDirection="column"
            alignItems="flex-start"
          >
            <FContainer
              display="flex"
              alignItems="center"
              justifyContent="center"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              width="80px"
            >
              <FText
                appearance="bodyReg14"
                textAlign="center"
                color="textMain"
                >{{
                  this.details.actualWorkEnd
                    ? formatDateWithOnlyTime(this.details.actualWorkEnd)
                    : '--'
                }}</FText
              >
            </FContainer>
            <FContainer
              v-if="this.details.actualWorkEnd"
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              alignItems="center"
              justifyContent="center"
            >
              <FText
                appearance="captionReg12"
                textAlign="left"
                color="textCaption"
              >
                {{ formatDateWithoutTime(this.details.actualWorkEnd) }}</FText
              >
            </FContainer>
          </FContainer>
        </FContainer>
        <FContainer v-else justifyContent="space-between">
          <FContainer
            ><FContainer gap="10px" padding="containerSmall containerXLarge">
              <FText appearance="bodyReg14" textAlign="center" color="textMain"
                >__</FText
              >
            </FContainer>
          </FContainer>
        </FContainer>
      </FContainer>

      <!-- 4 -->

      <FContainer
        width="100%"
        display="flex"
        flexDirection="column"
        alignItems="flex-start"
      >
        <FContainer
          display="flex"
          justifyContent="space-between"
          width="100%"
          alignItems="flex-start"
        >
          <FContainer
            display="flex"
            width="133px"
            alignItems="center"
            padding="containerMedium containerXLarge"
            gap="containerXLarge"
          >
            <FText appearance="captionMed12" color="textCaption">
              Response Due Date</FText
            >
          </FContainer>
          <FContainer
            display="flex"
            width="121px"
            alignItems="center"
            padding="containerMedium containerXLarge"
            gap="containerXLarge"
            justifyContent="center"
            paddingRight="containerMedium"
          >
            <FText appearance="captionMed12" color="textCaption"
              >Due Date</FText
            >
          </FContainer>
        </FContainer>
        <FContainer
          display="flex"
          justifyContent="space-between"
          width="100%"
          alignItems="center"
        >
          <FContainer
            display="flex"
            flexDirection="column"
            alignItems="flex-start"
          >
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              width="80px"
            >
              <FText appearance="bodyReg14" color="textMain" textAlign="center">
                {{
                  this.details.responseDueDate
                    ? formatDateWithOnlyTime(this.details.responseDueDate)
                    : '__'
                }}</FText
              >
            </FContainer>
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              v-if="this.details.dueDate"
            >
              <FText appearance="captionReg12" color="textCaption">
                {{ formatDateWithoutTime(this.details.responseDueDate) }}</FText
              >
            </FContainer>
          </FContainer>
          <FContainer
            display="flex"
            flexDirection="column"
            alignItems="flex-start"
          >
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              width="80px"
            >
              <FText appearance="bodyReg14" color="textMain">
                {{
                  this.details.dueDate
                    ? formatDateWithOnlyTime(this.details.dueDate)
                    : '__'
                }}</FText
              >
            </FContainer>
            <FContainer
              display="flex"
              padding="containerSmall containerXLarge"
              gap="containerXLarge"
              v-if="this.details.dueDate"
            >
              <FText appearance="captionReg12" color="textCaption">{{
                formatDateWithoutTime(this.details.dueDate)
              }}</FText>
            </FContainer>
          </FContainer>
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>

<script>
import { FContainer, FText } from '@facilio/design-system'
import { API } from '@facilio/api'
import WoTimer from 'src/beta/summary/widgets/WorkorderWidget/NewWorkorderTime.vue'

import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
export default {
  name: 'WorkTimeDetails',
  mixins: [workorderMixin],
  props: ['details', 'moduleName'],
  components: {
    WoTimer,
    FContainer,
    FText,
  },

  created() {
    this.loadWorkorderSummary()
  },
  mounted() {
    this.loadFields()

    this.$root.$on('reloadWO', res => {
      if (res) {
        this.woBundle.workorder = res.wo
      } else {
        this.loadWorkorderSummary()
        this.loadWorkorderSurveys()
      }
    })
  },
  data() {
    return {
      loading: true,
      fields: [],
      woBundle: null,
    }
  },
  computed: {
    actualWorkDurationField() {
      let { fields } = this
      this.workorder = this.details
      return fields.find(field => field.name === 'actualWorkDuration')
    },
    getTotalTask() {
      return this.details.noOfClosedTasks > -1
        ? this.details.noOfClosedTasks
        : 0
    },
    getTaskChart() {
      return (
        this.$t('maintenance._workorder.completed') +
        ' ' +
        this.$t('maintenance._workorder.of') +
        ' ' +
        (this.details.noOfTasks > -1 ? this.details.noOfTasks : 0) +
        ' ' +
        this.$t('maintenance._workorder.tasks')
      )
    },
    workorder() {
      const wo = this?.woBundle?.workorder
      return wo ? wo : {}
    },
  },
  methods: {
    formatDate(date, exclTime, onlyTime) {
      return this.$options.filters.formatDate(date, exclTime, onlyTime)
    },
    formatDateWithoutTime(date) {
      return this.formatDate(date, true, false)
    },
    formatDateWithOnlyTime(date) {
      return this.formatDate(date, false, true)
    },
    loadWorkorderSummary(force = false, loadPage = true) {
      this.loading = loadPage
      API.fetchRecord(
        'workorder',
        {
          id: this.$route.params.id,
        },
        { force }
      )
        .then(res => {
          this.$set(this, 'woBundle', res)
          this.woBundle.id = this.woBundle.workorder.id
          this.$store.dispatch(
            'workorder/setCurrentWO',
            this.woBundle.workorder
          )
        })
        .then(() => {
          // filtering prerequisite sections
          this.woBundle.workorder.preReqSections = {}
          for (const sectionID in this.woBundle.workorder.taskSections) {
            const taskSection = this.woBundle.workorder.taskSections[sectionID]
            if (taskSection.preRequest) {
              this.woBundle.workorder.preReqSections[+sectionID] = taskSection
            }
          }

          let preReqSectionIDs = Object.keys(
            this.woBundle.workorder.preReqSections
          ).map(k => +k)

          // filtering prerequisite tasks
          this.woBundle.workorder.preReqTasks = {}
          for (const sectionID in this.woBundle.workorder.tasks) {
            if (preReqSectionIDs.includes(+sectionID)) {
              this.woBundle.workorder.preReqTasks[
                sectionID
              ] = this.woBundle.workorder.tasks[sectionID]
            }
          }
          this.loading = false
        })
        .catch(error => {
          let { message = 'Error occured while fetching workorkder' } = error
          this.$message.error(message)
        })
    },
  },
}
</script>
