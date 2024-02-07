<template>
  <div class="p30" ref="internal-att-container">
    <portal :to="widget.key + '-title-section'">
      <div class="facility-header">
        <div class="section-header-label">INTERNAL ATTENDEE LIST</div>
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
    <div
      v-if="$validation.isEmpty(details.internalAttendees)"
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
        No Internal Attendees registered
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
        <tr v-for="(attendee, index) in getInternalAttendeeslist" :key="index">
          <td>{{ attendee.name || '---' }}</td>
          <td>{{ attendee.email || '---' }}</td>
          <td>{{ attendee.phone || '---' }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: {
    Pagination,
  },
  props: ['widget', 'details', 'resizeWidget'],
  data() {
    return {
      recordsList: [],
      page: 1,
      loading: true,
      perPage: 5,
    }
  },
  created() {
    this.autoResize()
  },
  computed: {
    getInternalAttendeeslist() {
      let { page, perPage } = this
      let list = this.$getProperty(this.details, 'internalAttendees', []) || []
      return list.slice((page - 1) * perPage, page * perPage) || []
    },
    totalCount() {
      let list = this.$getProperty(this.details, 'internalAttendees', []) || []
      return list.length
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['internal-att-container'])) {
          let height = this.$refs['internal-att-container'].scrollHeight + 50
          let width = this.$refs['internal-att-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
  },
}
</script>
