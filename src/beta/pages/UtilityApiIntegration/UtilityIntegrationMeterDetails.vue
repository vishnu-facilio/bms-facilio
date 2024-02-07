<template>
  <FContainer
    borderRadius="high"
    borderColor="borderNeutralBaseSubtle"
    height="50%"
    width="100%"
    marginTop="containerXLarge"
    display="flex"
    flexDirection="column"
    overflowY="scroll"
  >
    <FTable
      v-if="!isEmpty(meters)"
      :columns="columns"
      :data="meters"
      :showSelectBar="false"
      :hideBorder="true"
    >
      <template #[`cell.id`]="{ row, prop }">#{{ row[prop] }}</template>
      <template #[`cell.utilityID`]="record">
        <FText color="textMain" appearance="headingMed14">{{
          getProperty(record, 'row.utilityID', '---')
        }}</FText>
      </template>
      <template #[`cell.isActivated`]="record">
        <FText color="textMain" appearance="headingMed14">
          {{ getProperty(record, 'row.isActivated') ? 'Yes' : 'No' }}</FText
        >
      </template>
      <template #[`cell.serviceTariff`]="record">
        <FText color="textMain" appearance="headingMed14">{{
          getProperty(record, 'row.serviceTariff', '---')
        }}</FText>
      </template>
      <template #[`cell.meterState`]="{ row, prop }">
        <FTags
          v-if="row[prop] !== '---'"
          appearance="status"
          :text="statusType(row[prop]).text"
          :statusType="statusType(row[prop]).type"
        />
        <div v-else>{{ row[prop] }}</div>
      </template>
      <template #[`cell.action`]="data">
        <FContainer display="flex">
          <FIcon
            group="dsm"
            name="info"
            size="16"
            v-tippy
            dataArrow="true"
            :title="$t('maintenance.calender.more_details')"
            @click="toggleSummaryDetailsDialog(data.row)"
            class="visibility-hide-actions pointer"
            marginRight="containerXLarge"
          ></FIcon>
          <FContainer marginRight="containerLarge">
            <FButton
              appearance="primary"
              @click="addRelatedRecordsToggle(data.row)"
            >
              {{ $t('common.utility.associate') }}
            </FButton>
          </FContainer>
          <FContainer marginRight="containerLarge">
            <FButton
              v-if="showActivation(data.row)"
              appearance="primary"
              @click="activateMeter(data.row)"
            >
              {{ $t('common.utility.activate') }}
            </FButton>
          </FContainer>
          <FContainer marginRight="containerLarge">
            <FButton
              v-if="showFetchBillButton(data.row)"
              appearance="primary"
              @click="fetchBills(data.row)"
            >
              {{ $t('common.utility.fetch_bill') }}
            </FButton>
          </FContainer>
        </FContainer>
      </template></FTable
    >
    <div v-else class="text-center p20">
      {{ $t('common.utility.no_meters_associated') }}
    </div>
    <LookupWizard
      v-if="associateRecordPopup"
      :canShowLookupWizard.sync="associateRecordPopup"
      @setLookupFieldValue="setLookupFieldValue"
      :field.sync="meterFieldObj"
    />
    <FetchBills
      v-if="canShowFetchBills"
      :meterID="meterID"
      @closeDialog="closeDialog"
    />
    <FModal
      v-if="canShowSummaryDetailsDialog"
      :title="$t('maintenance.calender.more_details')"
      :visible="canShowSummaryDetailsDialog"
      size="M"
      :hideFooter="true"
      @cancel="handleCancel"
    >
      <FContainer
        display="grid"
        gridTemplateColumns="1fr 1fr 1fr"
        gridTemplateRows="1fr 1fr 1fr 1fr"
        gap="containerMedium"
        alignItems="flex-start"
        paddingTop="containerXxLarge"
        paddingLeft="sectionSmall"
        paddingBottom="containerXxLarge"
      >
        <template v-for="(field, index) in summaryFields">
          <FContainer
            :key="index"
            width="100%"
            flexDirection="column"
            justifyContent="center"
            gap="containerMedium"
            alignItems="flex-start"
            marginBottom="containerXxLarge"
          >
            <FContainer
              display="flex"
              alignItems="center"
              gap="containerLarge"
              alignSelf="stretch"
            >
              <FText
                color="textMain"
                lineHeight="130%"
                appearance="captionMed12"
              >
                {{ field.label }}
              </FText>
            </FContainer>
            <FContainer
              display="flex"
              alignItems="center"
              paddingTop="containerMedium"
              alignSelf="stretch"
            >
              <a
                v-if="
                  field.label === 'Associated Meter Id' && field.value !== '---'
                "
                class="blue-link"
                color=" blue"
                textDecoration="underline"
                cursor="pointer"
                @click="redirectToSummary(field.value)"
              >
                {{ getProperty(field, 'value', '') }}
              </a>
              <span
                v-else
                class="not-clickable"
                color="inherit"
                cursor="default"
                textDecoration=" none"
                >{{ getProperty(field, 'value', '') }}</span
              >
            </FContainer>
          </FContainer>
        </template>
      </FContainer>
    </FModal>

    <portal :to="`footer-${widget.id}-${widget.name}`">
      <Pagination
        v-if="!isEmpty(recordCount) || !isEmpty(recordList)"
        :key="`pagination-${moduleName}`"
        :totalCount="recordCount"
        :currentPageNo.sync="page"
        :currentPageCount="(recordList || []).length"
        :perPage="perPage"
      />
    </portal>
  </FContainer>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

import {
  FContainer,
  FText,
  FTable,
  FTags,
  FIcon,
  FModal,
  FButton,
} from '@facilio/design-system'
import { LookupWizard } from '@facilio/ui/new-forms'
import FetchBills from 'src/beta/pages/UtilityApiIntegration/FetchBills.vue'
import Pagination from 'src/beta/list/Pagination'
import getProperty from 'dlv'
export const STATUS = {
  1: 'Unmapped',
  2: 'Pending for Activation',
  3: 'Activated',
  4: 'Errored',
}
export const STATUS_ENUM = {
  1: 'UNMAPPED',
  2: 'PENDING',
  3: 'ACTIVATED',
  4: 'ERRORED',
}

const meterFieldObj = {
  isDataLoading: false,
  options: [],

  lookupModuleName: 'meter',
  field: {
    lookupModule: {
      name: 'meter',
      displayName: 'Meters',
    },
  },
  selectedItems: [],

  multiple: false,
}
export default {
  props: ['details', 'widget'],
  components: {
    FText,
    FContainer,
    LookupWizard,
    FetchBills,
    FTable,
    FTags,
    FIcon,
    FButton,
    FModal,

    Pagination,
  },
  data() {
    return {
      recordList: [],
      recordCount: null,
      page: 1,
      canShowSummaryDetailsDialog: false,
      associateRecordPopup: false,
      meterFieldObj,
      isLoading: false,
      canShowFetchBills: false,
      canShowActivate: false,
      canShowAssociate: false,
      meterID: null,
      recordId: null,
      detailsLayoutProp: null,
      taskDetailsVisibility: false,
      visibility: false,
      meters: [],
      columns: [
        { displayName: 'Meter ID', name: 'id', id: 1, fixed: true, width: 50 },

        {
          displayName: 'Utility ID',
          name: 'utilityID',
          id: 2,
          fixed: true,
          resize: true,
          width: 100,
          clickable: true,
        },
        {
          displayName: 'Is Activated',
          name: 'isActivated',
          id: 3,
          width: 100,
        },
        {
          displayName: 'Service Tariff',
          name: 'serviceTariff',
          id: 5,
          width: 200,
        },
        {
          displayName: 'Meter State',
          name: 'meterState',
          id: 6,
          width: 100,
        },
      ],
      isEmpty,
      currentRecord: {},
      summaryFields: [],
    }
  },
  created() {
    this.getProperty = getProperty
    this.getMeterList()
  },
  computed: {
    moduleName() {
      return 'utilityIntegrationMeter'
    },
    moduleDisplayName() {
      return 'Meter Connections'
    },
    viewname() {
      return 'hidden-all'
    },

    perPage() {
      return 5
    },
    modelDataClass() {
      return CustomModuleData
    },
  },
  watch: {
    page() {
      this.getMeterList()
    },
  },

  methods: {
    formatDate(dateTime) {
      const date = moment(dateTime)

      if (!date.isValid()) {
        return 'Invalid Date'
      }

      return date.format('DD-MM-YYYY HH:mm')
    },
    closeDialog() {
      this.canShowFetchBills = false
    },
    showFetchBillButton(record) {
      let meterState = this.$getProperty(record, 'meterStateEnum')
      if (meterState === STATUS_ENUM[3]) {
        return true
      }
      return false
    },

    fetchBills(record) {
      let { id } = record || {}
      this.canShowFetchBills = true
      this.meterID = id
    },
    statusType(meterState) {
      let type = ''
      let statusDisplayName = ''
      switch (meterState) {
        case 1:
          type = 'information'
          statusDisplayName = STATUS[1]
          break
        case 2:
          type = 'warning'
          statusDisplayName = STATUS[2]
          break
        case 3:
          type = 'success'
          statusDisplayName = STATUS[3]
          break
        case 4:
          type = 'danger'
          statusDisplayName = STATUS[4]
          break
        default:
          statusDisplayName = '---'
      }

      return {
        text: statusDisplayName,
        type: type,
      }
    },

    showActivation(record) {
      let meterState = this.$getProperty(record, 'meterStateEnum')
      if (meterState === STATUS_ENUM[1]) {
        return true
      }
      return false // Hide Activate button by default
    },

    addRelatedRecordsToggle(record) {
      let { id } = record || {}
      this.meterID = id
      this.associateRecordPopup = !this.associateRecordPopup
    },
    async activateMeter(record) {
      this.isLoading = true
      let { id } = record || {}
      let { error, data } = await API.post(
        `v3/utilityIntegration/activateMeters`,
        {
          meterID: id,
        }
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(this.$t('common.utility.meter_activated'))
        this.getMeterList()
        this.isLoading = false
      }
    },
    async setLookupFieldValue(value) {
      let { field } = value
      let { selectedItems } = field || {}
      let meterIds = selectedItems ? selectedItems.map(item => item.value) : []

      const payload = {
        id: this.meterID,
        meterIds: meterIds,
      }
      let { error, data } = await API.post(
        'v3/utilityIntegration/associateMeter',
        payload
      )
      if (!error) {
        this.$message.success(
          this.$t('common.utility.meter_mapped_successfully')
        )
        this.getMeterList()
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
      this.meterFieldObj.selectedItems = []
      this.showLookupFieldWizard = false
    },

    async getMeterList() {
      this.isLoading = true
      let { details } = this
      let { id } = details || {}

      let params = {
        filters: JSON.stringify({
          utilityIntegrationCustomer: {
            operatorId: 36,
            value: [String(id)],
          },
        }),
      }
      let { list, error } = await API.fetchAll(this.moduleName, params)
      if (isArray(list)) this.recordList = list || []

      this.recordCount = this.modelDataClass.recordListCount || 0
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.meters = list
        this.isLoading = false
      }
    },

    cancelDialog() {
      this.visibility = false
      this.showEditMode = false
    },

    refreshData() {
      this.getMeterList()
    },
    toggleSummaryDetailsDialog(rowData) {
      this.currentRecord = rowData
      this.canShowSummaryDetailsDialog = !this.canShowSummaryDetailsDialog

      if (rowData && getProperty(rowData, 'meter.id') !== null) {
        this.summaryFields = [
          {
            label: this.$t('common.utility.authorization_submitted_time'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'createdTime') || '---'
            ),
          },
          {
            label: this.$t('common.utility.user_email'),
            value: getProperty(this.currentRecord, 'userEmail') || '---',
          },
          {
            label: this.$t('common.utility.associated_meter_id'),
            value: getProperty(this.currentRecord, 'meter.id') || '---',
          },
          {
            label: this.$t('common.utility.user_uid'),
            value: getProperty(this.currentRecord, 'userUid') || '---',
          },
          {
            label: this.$t('common._common.frequency'),
            value: getProperty(this.currentRecord, 'frequency') || '---',
          },
          {
            label: this.$t('common.utility.next_prepay'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'nextPrepay') || '---'
            ),
          },
          {
            label: this.$t('common.utility.next_refresh'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'nextRefresh') || '---'
            ),
          },
          {
            label: this.$t('common.utility.prepay'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'prepay') || '---'
            ),
          },
          {
            label: this.$t('common.failure_class.created_by'),
            value:
              getProperty(this.currentRecord, 'sysCreatedBy.name') || '---',
          },
          {
            label: this.$t('common.failure_class.created_time'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'sysCreatedTime') || '---'
            ),
          },
          {
            label: this.$t('common.failure_class.modified_by'),
            value:
              getProperty(this.currentRecord, 'sysModifiedBy.name') || '---',
          },
          {
            label: this.$t('common.failure_class.modified_time'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'sysModifiedTime') || '---'
            ),
          },
        ]
      } else {
        this.summaryFields = [
          {
            label: this.$t('common.utility.authorization_submitted_time'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'createdTime') || '---'
            ),
          },
          {
            label: this.$t('common.utility.user_email'),
            value: getProperty(this.currentRecord, 'userEmail') || '---',
          },
          {
            label: this.$t('common.utility.associated_meter_id'),
            value: '---',
          },
          {
            label: this.$t('common.utility.user_uid'),
            value: getProperty(this.currentRecord, 'userUid') || '---',
          },
          {
            label: this.$t('common._common.frequency'),
            value: getProperty(this.currentRecord, 'frequency') || '---',
          },
          {
            label: this.$t('common.utility.next_prepay'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'nextPrepay') || '---'
            ),
          },
          {
            label: this.$t('common.utility.next_refresh'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'nextRefresh') || '---'
            ),
          },
          {
            label: this.$t('common.utility.prepay'),
            value: getProperty(this.currentRecord, 'prepay') || '---',
          },
          {
            label: this.$t('common.failure_class.created_by'),
            value:
              getProperty(this.currentRecord, 'sysCreatedBy.name') || '---',
          },
          {
            label: this.$t('common.failure_class.created_time'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'sysCreatedTime') || '---'
            ),
          },
          {
            label: this.$t('common.failure_class.modified_by'),
            value:
              getProperty(this.currentRecord, 'sysModifiedBy.name') || '---',
          },
          {
            label: this.$t('common.failure_class.modified_time'),
            value: this.formatDate(
              getProperty(this.currentRecord, 'sysModifiedTime') || '--'
            ),
          },
        ]
      }

      this.canShowSummaryDetailsDialog = true
    },

    handleCancel() {
      this.canShowSummaryDetailsDialog = false
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled() && !isEmpty(id)) {
        let { name } = findRouteForModule('meter', pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: { id, viewname: 'all' },
          })
      }
    },
  },
}
</script>
