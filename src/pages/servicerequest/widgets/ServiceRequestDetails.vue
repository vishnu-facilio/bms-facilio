<template>
  <div class="sr-details-widget-parent" ref="sr-details-container">
    <WOSummaryResourceCard
      :record="details"
      moduleName="serviceRequest"
    ></WOSummaryResourceCard>
    <div class="mT20 white-bg details-card height500 overflow-y-scroll">
      <div class="label-txt-black fwBold pB20">
        {{ $t('serviceRequest.fields.requestDetails') }}
      </div>
      <div class="d-flex flex-col">
        <div class="sr-label-text">
          {{ $t('serviceRequest.fields.requester') }}
        </div>
        <div class="sr-content-text mT5">
          {{
            $getProperty(details, 'requester.name') ||
              $getProperty(details, 'requester.email') ||
              '---'
          }}
        </div>
      </div>
      <div class="d-flex flex-col mT20">
        <div class="sr-label-text">
          {{ $t('serviceRequest.fields.priority') }}
        </div>
        <div class="sr-content-text mT5 d-flex flex-row align-center">
          <i
            v-if="details.urgency"
            class="fa fa-circle mR10 f9"
            v-bind:style="{
              color: `#${details.urgency.colour}`,
            }"
            aria-hidden="true"
          ></i>
          {{ $getProperty(details, 'urgency.displayName') || '---' }}
        </div>
      </div>
      <div class="d-flex flex-col mT20">
        <div class="sr-label-text">
          {{ $t('serviceRequest.fields.assignedTo') }}
        </div>
        <div class="sr-content-text mT5">
          <UserAvatar
            size="md"
            :user="details.assignedTo"
            :group="details.assignmentGroup"
            :showPopover="true"
            :showLabel="true"
            moduleName="workorder"
          ></UserAvatar>
        </div>
      </div>
      <div class="d-flex flex-col mT20">
        <div class="sr-label-text">
          {{ $t('serviceRequest.fields.classificationType') }}
        </div>
        <div class="sr-content-text mT5">
          {{ getClassificationType }}
        </div>
      </div>
      <div class="d-flex flex-col mT20">
        <div class="sr-label-text">
          {{ $t('serviceRequest.fields.dueDate') }}
        </div>
        <div class="sr-content-text mT5">
          {{ formattedDueDate || '---' }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import WOSummaryResourceCard from 'src/pages/workorder/workorders/v1/WOSummaryResourceCard'
import FieldDetails from 'src/components/page/widget/common/field-details/HorizontalFieldDetails.vue'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: FieldDetails,
  props: ['details'],
  components: {
    UserAvatar,
    WOSummaryResourceCard,
  },
  computed: {
    formattedDueDate() {
      let { details } = this || {}
      let { dueDate } = details || {}

      if (dueDate !== 0 && !isEmpty(dueDate)) {
        return this.$options.filters.formatDate(dueDate)
      } else {
        return null
      }
    },
    getClassificationType() {
      let { fieldsList = [] } = this
      let obj =
        fieldsList.find(field => field.name === 'classificationType') || {}
      let classificationType = this.$getProperty(obj, 'displayValue', '---')
      return classificationType
    },
  },
}
</script>
