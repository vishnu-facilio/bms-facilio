<template>
  <div class="height100">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openReservationId === -1"
    >
      <!-- table start -->
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(reservations) && !loading"
        class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="q-item-label nowo-label">
          No Reservation available
        </div>
      </div>
      <div v-if="!loading && !$validation.isEmpty(reservations)">
        <!-- <div class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          />
        </div> -->
        <el-table
          :data="reservations"
          class="width100"
          height="auto"
          :fit="true"
        >
          <el-table-column fixed prop label="ID" min-width="90">
            <template v-slot="reservation">
              <div class="fc-id">{{ '#' + reservation.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column fixed prop="name" label="Name" width="300">
            <template v-slot="reservation">
              <div
                v-tippy
                small
                @click="openReservationSummary(reservation.row.id)"
                :title="reservation.row.name"
                class="flex-middle"
              >
                <div>
                  <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                    {{ reservation.row.name }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :fixed="field.name === 'name'"
            :align="field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'"
            v-for="(field, index) in viewColumns"
            :key="index"
            :prop="field.name"
            :label="field.displayName"
            min-width="200"
            v-if="!isFixedColumn(field.name) || field.parentField"
          >
            <template v-slot="reservation">
              <div v-if="!isFixedColumn(field.name) || field.parentField">
                <div
                  class="table-subheading"
                  :class="{
                    'text-right': field.field.dataTypeEnum === 'DECIMAL',
                  }"
                >
                  {{ getColumnDisplayValue(field, reservation.row) || '---' }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="130"
            class="visibility-visible-actions"
            fixed="right"
          >
            <template v-slot="reservation">
              <div class="text-center">
                <i
                  class="el-icon-edit edit-icon-color visibility-hide-actions"
                  title="Edit Reservation"
                  data-arrow="true"
                  v-tippy
                  @click="editReservation(reservation.row)"
                ></i>
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  v-if="!reservation.row.defaultReservation"
                  data-arrow="true"
                  title="Delete Reservation"
                  v-tippy
                  @click="deleteReservation(reservation.row.id)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- table end -->
    </div>
    <div v-if="showCreateNewDialog">
      <reservation-form
        v-if="showCreateNewDialog"
        :editObjId="reservationObj.id"
        :showCreateNewDialog.sync="showCreateNewDialog"
        @saved="loadReservations"
      ></reservation-form>
    </div>
    <column-customization
      :visible.sync="showColumnSettings"
      moduleName="reservation"
      :viewName="currentView"
    ></column-customization>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ReservationForm from './ReservationForm'
export default {
  mixins: [ViewMixinHelper],
  components: {
    Spinner,
    ColumnCustomization,
    ReservationForm,
  },
  data() {
    return {
      reservationObj: '',
      showCreateNewDialog: false,
      loading: true,
      fetchingMore: false,
      actions: {
        delete: {
          loading: false,
        },
      },
      showColumnSettings: false,
    }
  },
  computed: {
    reservations() {
      return this.$store.state.reservation.reservations
    },
    openReservationId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    canLoadMore() {
      return this.$store.state.reservation.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    searchQuery() {
      return this.$store.state.reservation.quickSearchQuery
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  mounted() {
    this.loadReservations()
    // this.loadReservationsCount()
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadReservations()
        // this.loadReservationsCount()
      }
    },
    reservations: function() {},
    filters: function(newVal) {
      this.loadReservations()
      // this.loadReservationsCount()
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadReservations()
        // this.loadReservationsCount()
      }
    },
    searchQuery() {
      this.loadReservations()
      // this.loadReservationsCount()
    },
  },
  methods: {
    editReservation(reservation) {
      this.reservationObj = reservation
      this.showCreateNewDialog = true
    },
    closeNewDialog() {
      this.reservationObj = ''
      this.showCreateNewDialog = false
    },
    refreshReservationList() {
      this.loadReservations()
      this.showCreateNewDialog = false
    },
    loadReservations(loadMore) {
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.filters,
        search: this.searchQuery,
        isNew: true,
        includeParentFilter: this.includeParentFilter,
      }
      this.loading = true
      this.$store
        .dispatch('reservation/fetchReservations', queryObj)
        .then(response => {
          this.loading = false
          this.fetchingMore = false
          // this.page++
        })
        .catch(error => {
          if (error) {
            this.loading = false
            this.fetchingMore = false
          }
        })
    },
    loadReservationsCount() {
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/reservations/count?viewName=' + queryObj.viewname
      let params
      params = 'count=' + queryObj.count
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      if (queryObj.criteriaIds) {
        params = params + '&criteriaIds=' + queryObj.criteriaIds
      }
      if (queryObj.includeParentFilter) {
        params = params + '&includeParentFilter=' + queryObj.includeParentFilter
      }
      url = url + '&' + params
      this.$http
        .get(url)
        .then(response => {
          this.listcount = response.data.result.count
          this.$emit('syncCount', response.data.result.count)
        })
        .catch(error => {
          console.log(error)
        })
    },
    loadMore() {
      this.fetchingMore = true
      this.loadReservations(true)
    },
    deleteReservation(id) {
      this.$dialog
        .confirm({
          title: 'Delete Reservation',
          message: 'Are you sure you want to delete this Reservation?',
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/reservations/delete', { id: [id] })
              .then(response => {
                if (response.data.responseCode === 0) {
                  this.$message.success('Reservation Deleted Successfully')
                  this.loadReservations()
                } else {
                  this.$message.error(response.data.message)
                }
              })
          }
        })
    },
    cancelForm() {
      this.showCreateNewDialog = false
    },
    openReservationSummary(id) {
      this.$router.push({
        path: '/app/home/reservation/' + this.currentView + '/summary/' + id,
        query: this.$route.query,
      })
    },
  },
}
</script>
