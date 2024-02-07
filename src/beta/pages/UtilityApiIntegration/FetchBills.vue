<template>
  <FModal
    :visible="true"
    :title="'Fetch Bills'"
    size="S"
    :hideFooter="false"
    @ok="fetchBills(meterID)"
    @cancel="closeDialog"
  >
    <FContainer padding="containerLarge">
      <FContainer
        class="date-range-label"
        marginLeft="sectionXSmall"
        width="auto"
      >
        {{ $t('common.date_picker.date_range') }}
      </FContainer>
      <FContainer
        class="date-range-container "
        padding="containerLarge"
        marginRight="sectionSmall"
      >
        <div class="date-picker-container">
          <FDatePicker
            v-model="fetchBillList"
            type="date-range"
            :value="dateValue"
            placeholder="Select a date"
            :timeFormat="{ is12Hour: true, interval: 15 }"
            displayFormat="YYYY-MM-DD HH:mm"
            :disabled="false"
            width="100%"
            height="auto"
            marginBottom="sectionMedium"
          />
        </div>
      </FContainer>
    </FContainer>
  </FModal>
</template>
<script>
import { FModal, FDatePicker, FContainer } from '@facilio/design-system'
import { API } from '@facilio/api'

export default {
  props: ['meterID'],
  data: () => ({
    fetchBillList: null,
    isLoading: false,
    records: [],
    recordCount: null,
  }),
  components: { FModal, FDatePicker, FContainer },
  computed: {
    dateValue() {
      return new Date().getTime()
    },
  },
  methods: {
    async fetchBills(meterID) {
      this.isLoading = true
      let from = ''
      let end = ''

      if (Array.isArray(this.fetchBillList) && this.fetchBillList.length >= 2) {
        const [fromDate, endDate] = this.fetchBillList

        if (fromDate && endDate) {
          from = new Date(fromDate).getTime().toString()
          end = new Date(endDate).getTime().toString()
        }
      }
      let { error, data } = await API.post(`v3/utilityIntegration/fetchBills`, {
        meterID: meterID,
        startTime: from,
        endTime: end,
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
        this.closeDialog()
      } else {
        this.$message.success(this.$t('common.utility.bills_fetched'))
        this.closeDialog()
        this.isLoading = false
      }
    },
    closeDialog() {
      this.$emit('closeDialog')
    },
  },
}
</script>
