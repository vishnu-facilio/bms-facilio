<template>
  <div>
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">RateCards</div>
        <div class="heading-description">List of all RateCards</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" class="setup-el-btn" @click="add"
          >Add Ratecard</el-button
        >
        <add-rate-card
          v-if="showForm"
          :rateCard="selectedCard"
          :visibility.sync="showForm"
          @saved="loadRatecards"
        ></add-rate-card>
      </div>
    </div>

    <div class="row setting-Rlayout mT30">
      <div class="col-lg-12 col-md-12">
        <table class="setting-list-view-table">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">
                Name
              </th>
              <th class="setting-table-th setting-th-text">Description</th>
              <th></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr class="tablerow">
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="!ratecards.length">
            <tr class="tablerow">
              <td colspan="100%" class="text-center">
                No rate cards created yet.
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              v-for="(ratecard, index) in ratecards"
              class="tablerow"
              :key="index"
            >
              <td>{{ ratecard.name }}</td>
              <td>{{ ratecard.description }}</td>
              <td>
                <div
                  class="text-left actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i
                    class="el-icon-edit pointer"
                    @click="editRateCard(ratecard)"
                  ></i>
                  &nbsp;&nbsp;
                  <i
                    class="el-icon-delete pointer"
                    @click="deleteRateCard(ratecard.id, index)"
                  ></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import AddRateCard from 'pages/setup/bill/AddRateCard'
export default {
  data() {
    return {
      ratecards: [],
      loading: true,
      selectedCard: null,
      showForm: false,
    }
  },
  components: {
    AddRateCard,
  },
  mounted() {
    this.loadRatecards()
  },
  methods: {
    add() {
      this.showForm = true
      this.selectedCard = null
    },
    editRateCard(card) {
      this.showForm = true
      this.selectedCard = this.$helpers.cloneObject(card)
    },
    loadRatecards() {
      let self = this
      self.loading = true
      self.$http.get('/ratecards').then(function(response) {
        self.ratecards = response.data ? response.data : []
        self.loading = false
      })
    },
    deleteRateCard(id, idx) {
      this.$dialog
        .confirm({
          title: 'Delete Rate Card',
          message: 'Are you sure you want to delete this rate card?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http.post('/ratecard/delete', { id: id }).then(response => {
              if (typeof response.data === 'object') {
                this.ratecards.splice(idx, 1)
                this.$message.success('Rate card deleted successfully')
              } else {
                this.$message.error('Rate card cannot be deleted')
              }
            })
          }
        })
    },
  },
}
</script>
<style>
.fc-list-view-table tbody tr.tablerow:hover {
  background: #fafbfc;
}
.tablerow {
  cursor: pointer;
}
.tablerow.active {
  background: #f3f4f7 !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}

.fc-white-theme .tablerow.selected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.fc-black-theme .tablerow.selected {
  background: hsla(0, 0%, 100%, 0.1) !important;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
</style>
