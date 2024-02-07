<template>
  <FContainer padding="containerXLarge">
    <FContainer
      borderRadius="high"
      height="25%"
      width="100%"
      backgroundColor="backgroundMidgroundSubtle"
      display="flex"
      justifyContent="space-between"
    >
      <FContainer width="35%" padding="containerXLarge">
        <FText color="textMain" appearance="headingMed14">{{
          $t('common.utility.bill_details')
        }}</FText>
        <FContainer
          v-for="(label, key) in billDetails"
          :key="key"
          marginTop="containerXLarge"
          display="flex"
        >
          <FText color="textDescription" whiteSpace="pre">{{
            getBillLabel(label)
          }}</FText>
          <FText color="textMain" appearance="headingMed14">{{
            getBillDetail(key)
          }}</FText>
        </FContainer>
      </FContainer>
      <FContainer
        width="30%"
        padding="containerXLarge"
        display="flex"
        flexDirection="column"
        overflowY="scroll"
      >
        <FText
          color="textMain"
          alignSelf="flex-end"
          appearance="headingMed14"
          >{{ $t('common.utility.bill_address') }}</FText
        >
        <FText
          color="textMain"
          marginTop="containerXLarge"
          appearance="headingMed14"
          textAlign="justify"
          wordBreak="break-word"
          lineHeight="25px"
          style="padding-right: 0; margin-right: 0; align-self: flex-end;"
          >{{ $getProperty(utilityBill, 'billingAddress', '---') }}</FText
        >
      </FContainer>
    </FContainer>
    <FContainer
      borderRadius="high"
      border="1px solid"
      borderColor="borderNeutralBaseSubtle"
      height="15%"
      width="100%"
      marginTop="containerXLarge"
      display="flex"
      gap="sectionSmall"
    >
      <FContainer
        v-for="(label, key) in billType"
        :key="key"
        padding="containerXLarge"
        display="flex"
        flexDirection="column"
      >
        <FText color="textDescription">{{ label }}</FText>
        <FText
          color="textMain"
          appearance="headingMed14"
          marginTop="containerLarge"
          >{{ getBillDetail(key) }}</FText
        >
      </FContainer>
    </FContainer>
    <FContainer
      borderRadius="high"
      border="1px solid"
      borderColor="borderNeutralBaseSubtle"
      height="50%"
      width="100%"
      marginTop="containerXLarge"
      display="flex"
      flexDirection="column"
      overflow="auto"
    >
      <FTable
        class="utility-bill-item-table"
        :columns="billColumns"
        :data="utilityBill.utilityIntegrationLineItemContexts"
        :hideBorder="true"
        flex="1"
        overflow="auto"
      >
        <template #[`cell.sno`]="record">
          <FText color="textMain" appearance="headingMed14" v-if="record">{{
            $getProperty(record, 'row.index', '---')
          }}</FText>
        </template>
        <template #[`cell.name`]="record">
          <FContainer display="flex" flexDirection="column">
            <FText color="textMain" appearance="headingMed14">{{
              $getProperty(record, 'row.name', '---')
            }}</FText>
            <FText
              color="textDescription"
              marginTop="containerLarge"
              v-if="$getProperty(record, 'row.description', '')"
              >{{ $getProperty(record, 'row.description', '---') }}</FText
            >
          </FContainer>
        </template>
        <template #[`cell.type`]="record">
          <FTags
            v-if="
              record &&
                !(
                  record.row.rate === 0 ||
                  record.row.name.toLowerCase().includes('tax')
                )
            "
            appearance="status"
            statusType="success"
            text="CONSUMPTION"
            :disabled="false"
          />
          <span v-else>---</span>
        </template>
        <template #[`cell.volume`]="record">
          <FText color="textMain" appearance="headingMed14">{{
            getItemUsage(record)
          }}</FText>
        </template>
        <template #[`cell.cost`]="record">
          <FText color="textMain" appearance="headingMed14">{{
            getAmount(record, 'cost')
          }}</FText>
        </template>
        <template #[`cell.rate`]="record">
          <FText color="textMain" appearance="headingMed14">{{
            getAmount(record, 'rate')
          }}</FText>
        </template>
      </FTable>
      <FContainer
        alignSelf="flex-end"
        width="33%"
        padding="containerXLarge"
        position="sticky"
        bottom="0"
        marginRight="auto"
        justifyContent="space-between"
      >
        <FContainer display="flex" padding="containerXLarge">
          <FText>{{ $t('common.utility.amount') }}</FText>
          <FText
            color="textMain"
            appearance="headingMed14"
            class="amount-text"
            margin-left="160px !important"
            >{{ grossAmount }}</FText
          >
        </FContainer>
        <FDivider width="100%" />
        <FContainer display="flex" padding="containerXLarge">
          <FText>{{ $t('common.utility.tax') }}</FText>
          <FText
            color="textMain"
            appearance="headingMed14"
            class="tax-text"
            margin-left=" 190px !important;"
            >{{ taxAmount }}</FText
          >
        </FContainer>
        <FDivider width="100%" />
        <FContainer display="flex" padding="containerXLarge">
          <FText>{{ $t('common.header.total') }}</FText>
          <FText
            color="textMain"
            appearance="headingMed14"
            class="total-text"
            margin-left="180px !important;"
            >{{ totalAmount }}</FText
          >
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import {
  FContainer,
  FText,
  FTable,
  FTags,
  FDivider,
} from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers'
const { getOrgMoment: moment } = helpers

export default {
  name: 'UtilityBillSummary',
  components: { FContainer, FText, FTable, FTags, FDivider },
  data: () => ({
    billDetails: {
      billUid: 'ID',
      billStartDate: 'Start Date',
      billEndDate: 'End Date',
      billStatementDate: 'Statement Date',
    },
    billType: {
      serviceClass: 'Utility Type',
      supplierType: 'Supplier',
    },
    billColumns: [
      {
        displayName: 'S.No',
        name: 'sno',
        id: 1,
        fixed: false,
        resize: false,
        width: 100,
      },
      {
        displayName: 'Items & Description',
        name: 'name',
        id: 2,
        fixed: false,
        resize: false,
        width: 220,
      },
      {
        displayName: 'Type',
        name: 'type',
        id: 3,
        fixed: false,
        resize: false,
        width: 150,
      },
      {
        displayName: 'Usage',
        name: 'volume',
        id: 4,
        fixed: false,
        resize: false,
        width: 150,
      },
      {
        displayName: 'Cost',
        name: 'rate',
        id: 5,
        fixed: false,
        resize: false,
        width: 150,
      },
      {
        displayName: 'Amount',
        name: 'cost',
        id: 6,
        fixed: false,
        resize: false,
        width: 150,
      },
    ],
  }),
  computed: {
    utilityBill() {
      let { $attrs } = this
      let { details } = $attrs || {}
      let { utilityIntegrationLineItemContexts } = details || {}

      utilityIntegrationLineItemContexts = (
        utilityIntegrationLineItemContexts || []
      ).map((utility, index) => {
        index = index + 1
        if (index < 10) {
          index = `0${index}`
        }
        utility = { ...utility, index }
        return utility
      })
      details = { ...details, utilityIntegrationLineItemContexts }
      return details
    },
    grossAmount() {
      let { utilityBill, $currency } = this
      let totalAmount = this.$getProperty(utilityBill, 'billTotalCost', 0)

      totalAmount = totalAmount ? totalAmount.toFixed(2) : 0
      return `${$currency}${totalAmount}`
    },
    taxAmount() {
      let { utilityBill, $currency } = this
      let totalAmount = this.$getProperty(utilityBill, 'billTotalTax', 0)

      totalAmount = totalAmount ? totalAmount.toFixed(2) : 0
      return `${$currency}${totalAmount}`
    },
    totalAmount() {
      let { utilityBill, $currency } = this
      let totalAmount = this.$getProperty(utilityBill, 'billTotalCost', 0)
      let taxAmount = this.$getProperty(utilityBill, 'billTotalTax', 0)

      let result = totalAmount + taxAmount
      return `${$currency}${result}`
    },
  },
  methods: {
    getBillLabel(label) {
      return `${label} : `
    },
    getBillDetail(key) {
      let { utilityBill } = this
      let detail = ''
      if (['billStartDate', 'billEndDate', 'billStatementDate'].includes(key)) {
        let dateInfo = this.$getProperty(utilityBill, `${key}`, '')
        detail = !isEmpty(dateInfo)
          ? moment(dateInfo).format('DD MMM YYYY')
          : '---'
      } else if (key === 'billUid') {
        detail = `#${this.$getProperty(utilityBill, `billUid`, '---')}`
      } else {
        detail = this.$getProperty(utilityBill, `${key}`, '---')
      }
      return detail
    },
    getItemUsage(record) {
      let { row } = record || {}
      let unit = this.$getProperty(row, 'unit', '')
      let totalUsage = this.$getProperty(row, 'volume', 0)

      totalUsage = totalUsage ? totalUsage.toFixed(2) : 0
      return `${totalUsage} ${unit}`
    },
    getAmount(record, type) {
      let { $currency } = this
      let { row } = record || {}
      let amount = this.$getProperty(row, `${type}`, 0)
      amount = amount ? amount.toFixed(2) : 0

      return `${$currency}${amount}`
    },
  },
}
</script>
<style lang="scss">
.utility-bill-item-table {
  th {
    background-color: #f0f6ff;
  }
}
</style>
