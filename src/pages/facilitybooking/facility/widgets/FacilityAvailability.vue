<template>
  <div class="p30" ref="availability-container">
    <portal :to="widget.key + '-title-section'">
      <div class="facility-header">
        <div class="section-header-label">Special Availabilities</div>
        <div>
          <div class="d-flex flex-row ">
            <pagination
              :currentPage.sync="page"
              :total="totalCount"
              :perPage="perPage"
              class="self-center"
            ></pagination>
            <span
              v-if="
                !$validation.isEmpty(specialAvailabilityList) &&
                  !$helpers.isPortalUser()
              "
              class="separator self-center"
              >|</span
            >
            <div
              v-if="!$helpers.isPortalUser()"
              @click="toggleFormVisibility"
              class="fc-pink f13 bold pointer"
            >
              <i class="el-icon-plus pR5"></i>
              {{ `Add Special Availability` }}
            </div>
          </div>
        </div>
      </div>
    </portal>
    <spinner v-if="loading" :show="true" size="80"></spinner>
    <div
      v-else-if="$validation.isEmpty(specialAvailabilityList)"
      class="d-flex justify-content-center flex-direction-column align-center height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/commonempty"
          class="vertical-middle'"
          iconClass="icon icon-80"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        No Special Availabilities
      </div>
    </div>
    <table v-else class="facility-summary-table">
      <thead>
        <tr>
          <th>REMARK</th>
          <th>START DATE</th>
          <!-- <th>END DATE</th> -->
          <th>START TIME</th>
          <th>END TIME</th>
          <th>COST</th>
          <th>TYPE</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(availability, index) in specialAvailabilityList"
          :key="index"
        >
          <td>
            {{ availability.remarks || '---' }}
          </td>
          <td>
            {{
              $helpers.getFormattedValueForMillis(
                availability.startDate,
                'DD MMM'
              )
            }}
          </td>
          <!-- <td>
            {{
              $helpers.getFormattedValueForMillis(
                availability.endDate,
                'DD MMM'
              )
            }}
          </td> -->
          <td>{{ availability.startTime }}</td>
          <td>{{ availability.endTime }}</td>
          <td>
            <span v-if="availability.specialType === 1"
              ><currency :value="availability.cost || 0"></currency
            ></span>
            <span v-else>{{ 'N/A' }}</span>
          </td>
          <td
            :class="
              availability.specialType === 1
                ? 'available-text'
                : 'not-available-text'
            "
          >
            {{ availability.specialTypeEnum }}
          </td>
        </tr>
      </tbody>
    </table>
    <SpecialAvailabilityForm
      :facilityRecord="details"
      :visibility.sync="formVisibility"
      v-if="formVisibility"
      @saved="refreshDetails"
    ></SpecialAvailabilityForm>
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import SpecialAvailabilityForm from '../FacilitySpecialAvailabilityForm'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
export default {
  components: {
    Pagination,
    SpecialAvailabilityForm,
  },
  props: ['widget', 'details', 'resizeWidget'],
  data() {
    return {
      formVisibility: false,
      specialAvailabilityList: [],
      page: 1,
      totalCount: 0,
      loading: true,
      perPage: 5,
    }
  },
  watch: {
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadRelatedRecords()
      }
    },
  },
  created() {
    this.loadRelatedRecords()
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['availability-container'])) {
          let height = this.$refs['availability-container'].scrollHeight + 50
          let width = this.$refs['availability-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
    toggleFormVisibility() {
      this.formVisibility = true
    },
    refreshDetails() {
      eventBus.$emit('refesh-parent')
    },
    async loadRelatedRecords() {
      let recordFilter = {
        facility: { operatorId: 36, value: [`${this.details.id}`] },
      }
      let queryObj = {
        page: this.page,
        perPage: this.perPage,
        withCount: true,
        filters: JSON.stringify(recordFilter),
      }
      this.loading = true
      let { list, error, meta } = await API.fetchAll(
        'facilitySpecialAvailability',
        queryObj
      )
      if (error) {
        let { message = 'Error loading special availability list' } = error
        this.$message.error(message)
      } else {
        this.specialAvailabilityList = list
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount', null)
      }
      this.loading = false
      this.autoResize()
    },
  },
}
</script>
