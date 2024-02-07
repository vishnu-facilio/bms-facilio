<template>
  <div class="p30" ref="external-attendee-container">
    <portal :to="widget.key + '-title-section'">
      <div class="facility-header">
        <div class="section-header-label">EXTERNAL ATTENDEE LIST</div>
        <div>
          <div class="d-flex flex-row ">
            <pagination
              :currentPage.sync="page"
              :total="totalCount"
              :perPage="perPage"
              class="self-center mL10"
            ></pagination>
          </div>
        </div>
      </div>
    </portal>
    <spinner v-if="loading" :show="true" size="80"></spinner>
    <div
      v-else-if="$validation.isEmpty(recordsList)"
      class="d-flex justify-content-center flex-direction-column align-center height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/commonempty"
          class="vertical-middle'"
          iconClass="icon icon-80"
        ></inline-svg>
      </div>
      <div class="fc-black-dark f18 self-center bold">
        No External Attendees registered
      </div>
    </div>
    <table v-else class="facility-summary-table width100">
      <thead>
        <tr>
          <th class="width300px">NAME</th>
          <th class="width300px">EMAIL</th>
          <th>CONTACT NO</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(attendee, index) in recordsList" :key="index">
          <td>{{ attendee.name || '---' }}</td>
          <td>{{ attendee.phone || '---' }}</td>
          <td>{{ attendee.email || '---' }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import isEqual from 'lodash/isEqual'
export default {
  components: {
    Pagination,
  },
  props: ['widget', 'details', 'resizeWidget'],
  data() {
    return {
      recordsList: [],
      totalCount: 0,
      perPage: 5,
      page: 1,
      loading: true,
    }
  },
  created() {
    this.loadRelatedRecords()
  },
  watch: {
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRelatedRecords()
      }
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['external-attendee-container'])) {
          let height =
            this.$refs['external-attendee-container'].scrollHeight + 55
          let width = this.$refs['external-attendee-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
    async loadRelatedRecords() {
      let recordFilter = {
        facilityBooking: { operatorId: 36, value: [`${this.details.id}`] },
      }
      let queryObj = {
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        filters: JSON.stringify(recordFilter),
      }
      this.loading = true
      let { list, error, meta } = await API.fetchAll(
        'facilityBookingExternalAttendee',
        queryObj
      )
      if (error) {
        let { message = 'Error loading external attendee list' } = error
        this.$message.error(message)
      } else {
        this.recordsList = list
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.loading = false
      this.autoResize()
    },
  },
}
</script>
