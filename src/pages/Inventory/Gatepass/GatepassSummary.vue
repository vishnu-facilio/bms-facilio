<template>
  <div class="inventory-page-new-con">
    <div v-if="!summaryData">
      <spinner :show="!summaryData"></spinner>
    </div>

    <template v-else>
      <!-- man header -->
      <div class="inventory-header p20">
        <div class="fc-dark-grey-txt18">
          <div class="fc-id">#{{ summaryData.id }}</div>
          <div class="fc-black-17 bold inline-flex">
            {{ summaryData.issuedTo }}
            <div class="fc-newsummary-chip mL10 ">
              {{ getGatePassStatus() }}
            </div>
          </div>
        </div>
        <div class="fc__layout__align inventory-overview-btn-group">
          <el-button
            class="fc__add__btn fc__border__btn mR10"
            v-if="summaryData.status === 2"
            style="font-size: 15px; padding: 10px 12px;"
            @click="printGatePass(summaryData)"
          >
            <i class="fa fa-print f16" aria-hidden="true"></i>
          </el-button>
          <el-popover placement="top-start" width="180" trigger="hover">
            <el-button
              v-if="$hasPermission('inventory:APPROVE_REJECT_WORKREQUEST')"
              slot="reference"
              class="fc__border__btn"
            >
              Change Status
              <i class="el-icon-arrow-down f14 text-right"></i
            ></el-button>
            <div
              v-if="summaryData.status !== 2"
              @click="updateStatus(2, 'Gate Pass Approved Successfully')"
              class="label-txt-black pT10 pB10 pointer pL10 list-hover"
            >
              Approve
            </div>
            <div
              v-if="summaryData.status !== 4"
              @click="updateStatus(4, 'Gate Pass Rejected Successfully')"
              class="label-txt-black pT10 pB10 pointer pL10 list-hover"
            >
              Reject
            </div>
            <div
              v-if="summaryData.status !== 5 && summaryData.returnable"
              @click="updateStatus(5, 'Gate Pass Returned Successfully')"
              class="label-txt-black pT10 pB10 pointer pL10 list-hover"
            >
              Return
            </div>
          </el-popover>
          <el-dropdown
            v-if="$hasPermission('inventory:DELETE')"
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
            @command="handleCommand"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-if="$hasPermission('inventory:DELETE')"
                command="delete"
                >Delete</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <!-- main section -->
      <div
        class="fc__layout__flexes fc__main__con__width"
        v-if="summaryData !== null"
      >
        <div class="fc__asset__main__scroll">
          <div class="fc__white__bg__asset">
            <el-row>
              <el-col :span="16" class="mB30">
                <el-col :span="24" class="mB30">
                  <div class="fc-blue-label">Type</div>
                  <div class="label-txt-black pT5">
                    {{ summaryData.gatePassType === 1 ? 'Inward' : 'Outward' }}
                  </div>
                </el-col>
                <el-col :span="12" class="">
                  <div
                    v-if="
                      summaryData.gatePassType === 2 &&
                        summaryData.fromStoreRoom
                    "
                    class="fc-blue-label"
                  >
                    From Storeroom
                  </div>
                  <div
                    v-else-if="
                      summaryData.gatePassType === 1 && summaryData.toStoreRoom
                    "
                    class="fc-blue-label"
                  >
                    To Storeroom
                  </div>
                  <div class="label-txt-black pT5">
                    <store-room-avatar
                      v-if="
                        summaryData.gatePassType === 2 &&
                          summaryData.fromStoreRoom
                      "
                      name="true"
                      size="lg"
                      :storeRoom="summaryData.fromStoreRoom"
                    ></store-room-avatar>
                    <store-room-avatar
                      v-else-if="
                        summaryData.gatePassType === 1 &&
                          summaryData.toStoreRoom
                      "
                      name="true"
                      size="lg"
                      :storeRoom="summaryData.toStoreRoom"
                    ></store-room-avatar>
                  </div>
                </el-col>
                <el-col
                  v-if="
                    summaryData.gatePassType === 1 && summaryData.parentPoId
                  "
                  :span="12"
                  class=""
                >
                  <div class="fc-blue-label">PO ID</div>
                  <div class="label-txt-black pT5">
                    <router-link
                      :to="
                        '/app/purchase/po/' +
                          'all' +
                          '/summary/' +
                          summaryData.parentPoId.id
                      "
                      >{{ '#' + summaryData.parentPoId.id }}</router-link
                    >
                  </div>
                </el-col>
              </el-col>
            </el-row>
          </div>
          <div class="mT20 mB10 inventory-section-header">
            Summary
          </div>
          <div class="fc__white__bg__info">
            <el-row class="border-bottom6 pB20">
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Issued By</div>
                </el-col>
                <el-col :span="12">
                  <user-avatar
                    size="md"
                    :user="$store.getters.getUser(summaryData.issuedBy.id)"
                    v-if="summaryData.issuedBy && summaryData.issuedBy.id"
                  ></user-avatar>
                  <div v-else>---</div>
                </el-col>
              </el-col>
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">
                    Issued To Phone Number
                  </div>
                </el-col>
                <el-col :span="12">
                  {{
                    summaryData.issuedToPhoneNumber
                      ? summaryData.issuedToPhoneNumber
                      : '---'
                  }}
                </el-col>
              </el-col>
            </el-row>
            <el-row class="border-bottom6 pB20 pT20">
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Is Returnable</div>
                </el-col>
                <el-col :span="12">
                  {{ summaryData.returnable ? 'Yes' : 'No' }}
                </el-col>
              </el-col>
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Return Time</div>
                </el-col>
                <el-col :span="12">
                  {{
                    summaryData.returnTime !== -1
                      ? this.$options.filters.formatDate(
                          summaryData.returnTime,
                          true
                        )
                      : '---'
                  }}
                </el-col>
              </el-col>
            </el-row>
            <el-row class="pT20">
              <el-col :span="12">
                <el-col :span="12">
                  <div class="fc-blue-label">Vehicle Number</div>
                </el-col>
                <el-col :span="12">
                  {{ summaryData.vehicleNo ? summaryData.vehicleNo : '---' }}
                </el-col>
              </el-col>
            </el-row>
          </div>
          <!-- line items -->
          <div class="fc__white__bg__p0 mT20">
            <el-tabs v-model="activeName1" class="fc-tab-hide">
              <el-tab-pane label="Line Items" name="first">
                <el-table
                  v-loading="gatePassLoading"
                  :data="summaryData.lineItems"
                  empty-text="No Line Items available."
                  class="width100 inventory-inner-table"
                >
                  <el-table-column label="LINE ITEM">
                    <template v-slot="gatePassScope">
                      <item-avatar
                        :name="'true'"
                        size="lg"
                        module="item"
                        :recordData="gatePassScope.row.itemType"
                        v-if="gatePassScope.row.itemType !== null"
                      ></item-avatar>
                      <tool-avatar
                        :name="'true'"
                        size="lg"
                        module="tool"
                        :recordData="gatePassScope.row.toolType"
                        v-else-if="gatePassScope.row.toolType !== null"
                      ></tool-avatar>
                    </template>
                  </el-table-column>
                  <el-table-column sortable prop="quantity" label="QUANTITY">
                    <template v-slot="scope">
                      <div
                        v-if="
                          summaryData.status === 6 &&
                            ((scope.row.inventoryType === 1 &&
                              scope.row.itemType.isRotating) ||
                              (scope.row.inventoryType === 2 &&
                                scope.row.toolType.isRotating))
                        "
                      >
                        <div>1</div>
                      </div>
                      <div v-else>
                        <div>{{ scope.row.quantity }}</div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="asset.serialNumber"
                    label="S NO"
                    v-if="summaryData.gatePassType === 2"
                    sortable
                  ></el-table-column>
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </div>
          <!-- notes & attachements -->
          <div class="fc__white__bg__p0 mT20" :key="summaryData.id">
            <el-tabs v-model="activeName">
              <el-tab-pane label="Notes" name="first">
                <div
                  v-if="activeName === 'first' && summaryData && summaryData.id"
                  class="inventory-comments"
                >
                  <comments
                    module="gatePassNotes"
                    placeholder="Add a note"
                    btnLabel="Add Note"
                    :record="summaryData"
                    :notify="false"
                  ></comments>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Attachments" name="second">
                <div v-if="activeName === 'second'">
                  <attachments
                    module="gatePassAttachments"
                    v-if="summaryData"
                    :record="summaryData"
                  ></attachments>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import Attachments from '@/relatedlist/Attachments'
import Comments from '@/relatedlist/Comments'
import UserAvatar from '@/avatar/User'
import StoreRoomAvatar from '@/avatar/Storeroom'
import ToolAvatar from '@/avatar/ItemTool'
import ItemAvatar from '@/avatar/ItemTool'
import { isEmpty } from '@facilio/utils/validation'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    Comments,
    Attachments,
    ToolAvatar,
    ItemAvatar,
    UserAvatar,
    StoreRoomAvatar,
  },
  props: ['viewname'],
  data() {
    return {
      activeName: 'first',
      activeName1: 'first',
      activeName2: 'first',
      status: {
        REQUESTED: 'REQUESTED',
        APPROVED: 'APPROVED',
        ISSUED: 'ISSUED',
        REJECTED: 'REJECTED',
        RETURNED: 'RETURNED',
        COMPLETED: 'COMPLETED',
      },
      selectindex: false,
      loading: true,
      page: 1,
      fetchingMore: false,
      summaryData: null,
      saving: false,
      gatePassLoading: false,
    }
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', this.getCurrentModule)
  },
  computed: {
    id() {
      return parseInt(this.$route.params.id)
    },
    getCurrentModule() {
      return 'gatePass'
    },
    currentView() {
      return this.$route.params.viewname
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Gate Pass',
          name: 'filteredgatepass',
        }
      }
      return this.$store.state.view.currentViewDetail || {}
    },
    views() {
      return this.$store.state.view.views
    },
  },
  watch: {
    id: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadSummary()
        }
      },
      immediate: true,
    },
  },
  methods: {
    updateStatus(status, message) {
      let self = this
      let tempGatePass = this.$helpers.cloneObject(this.summaryData)
      if (status && status !== 0) {
        tempGatePass.status = status
      }
      tempGatePass.status = status
      let param = {
        gatePass: tempGatePass,
      }
      this.$http
        .post('v2/gatePass/addOrUpdate', param)
        .then(function(response) {
          if (response.data.responseCode === 0) {
            self.$message.success(message)
            self.loadSummary()
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    loadSummary() {
      let self = this
      let url = '/v2/gatePass/'
      if (this.id) {
        this.$http
          .get(url + this.id)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.summaryData = response.data.result.gatePass
              let title =
                '[#' + self.summaryData.id + '] ' + self.summaryData.issuedTo
              self.setTitle(title)
              self.gatePassLoading = false
            }
          })
          .catch(() => {})
      }
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: this.getCurrentModule,
      })
    },
    handleCommand(command) {
      if (command === 'delete') {
        this.deleteGatePass()
      }
    },
    deleteGatePass() {
      let self = this
      let url = '/v2/gatePass/delete'
      let params = {
        gatePassIds: [this.summaryData.id],
      }
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success('Deleted Successfully')
            if (isWebTabsEnabled()) {
              let { name } =
                findRouteForModule('gatePass', pageTypes.LIST) || {}
              name &&
                this.$router.push({
                  name,
                })
            } else {
              this.$router.push({
                path: '/app/inventory/gatepass/' + 'all',
              })
            }
          }
        })
        .catch(() => {})
    },
    getGatePassStatus() {
      switch (this.summaryData.status) {
        case 1:
          return 'Waiting for Approval'
        case 2:
          return 'Approved'
        case 3:
          return 'Issued'
        case 4:
          return 'Rejected'
        case 5:
          return 'Returned'
        case 6:
          return 'Completed'
      }
    },
    printGatePass(gatePass) {
      window.open(
        window.location.protocol +
          '//' +
          window.location.host +
          '/app/at/gatepass/pdf/' +
          gatePass.id
      )
    },
  },
}
</script>
