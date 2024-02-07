<template>
  <div class="inventory-page-new-con">
    <div v-if="!shipment">
      <spinner :show="!shipment"></spinner>
    </div>
    <template v-else>
      <div class="inventory-header p20">
        <!-- man header -->
        <div class="fc-dark-grey-txt18">
          <div class="fc-id">#{{ shipment.id }}</div>
          <div class="fc-black-17 bold inline-flex">
            {{ shipment.fromStore.name + ' -> ' + shipment.toStore.name }}
            <div class="fc-newsummary-chip mL10 ">
              {{ getShipmentStatus() }}
            </div>
          </div>
        </div>
        <div class="fc__layout__align inventory-overview-btn-group">
          <el-button
            class="fc__add__btn"
            @click="stageShipment()"
            :loading="buttonLoading.stage"
            v-if="shipment.status === 1"
            >STAGE</el-button
          >
          <el-button
            class="fc__add__btn"
            @click="receiveShipment()"
            :loading="buttonLoading.receive"
            v-if="shipment.status === 2"
            >RECEIVE</el-button
          >
          <el-dropdown
            @command="handleCommand"
            v-if="shipment.status === 1"
            class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" height="18" width="18" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="delete">Delete</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <!-- main section -->
      <div class="fc__asset__main__scroll">
        <div class="fc__white__bg__asset">
          <el-row>
            <el-col :span="16" class="mB30">
              <el-col :span="12">
                <div class="fc-blue-label">From Storeroom</div>
                <div class="label-txt-black pT5">
                  <store-room-avatar
                    name="true"
                    size="lg"
                    :storeRoom="shipment.fromStore"
                  >
                  </store-room-avatar>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-blue-label">To Storeroom</div>
                <div class="label-txt-black pT5">
                  <store-room-avatar
                    name="true"
                    size="lg"
                    :storeRoom="shipment.toStore"
                  >
                  </store-room-avatar>
                </div>
              </el-col>
            </el-col>
            <el-col :span="6"></el-col>
            <el-col :span="16" class="">
              <el-col :span="12">
                <div
                  class="fc-blue-label"
                  v-if="shipment.transferredBy !== null"
                >
                  Transferred By
                </div>
                <div
                  class="label-txt-black pT5"
                  v-if="
                    shipment.transferredBy !== null &&
                      shipment.transferredBy &&
                      shipment.transferredBy.id
                  "
                >
                  <user-avatar
                    size="md"
                    :user="$store.getters.getUser(shipment.transferredBy.id)"
                    v-if="shipment.transferredBy && shipment.transferredBy.id"
                  ></user-avatar>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-blue-label" v-if="shipment.receivedBy !== null">
                  Received By
                </div>
                <div
                  class="label-txt-black pT5"
                  v-if="
                    shipment.receivedBy !== null &&
                      shipment.receivedBy &&
                      shipment.receivedBy.id
                  "
                >
                  <user-avatar
                    size="md"
                    :user="$store.getters.getUser(shipment.receivedBy.id)"
                    v-if="shipment.receivedBy && shipment.receivedBy.id"
                  ></user-avatar>
                </div>
              </el-col>
            </el-col>
          </el-row>
        </div>

        <div class="fc__white__bg__p0 mT20" :key="shipment.id">
          <el-tabs v-model="tabName">
            <el-tab-pane label="Notes" name="first">
              <div class="inventory-comments">
                <comments
                  v-if="shipment && shipment.id"
                  module="shipmentNotes"
                  placeholder="Add a note"
                  btnLabel="Add Note"
                  :record="shipment"
                  :notify="false"
                ></comments>
              </div>
            </el-tab-pane>
            <el-tab-pane label="Attachments" name="second">
              <attachments
                v-if="shipment && shipment.id"
                module="shipmentAttachments"
                :record="shipment"
              >
              </attachments>
            </el-tab-pane>
          </el-tabs>
        </div>
        <!-- notes & attachements -->
        <div class="fc__white__bg__p0 mT20">
          <el-tabs v-model="lineItemTab" class="fc-tab-hide">
            <el-tab-pane label="Line Items" name="lineItem" class="fc-tab-hide">
              <div>
                <el-table
                  :data="shipment.lineItems"
                  empty-text="No Line Items available."
                  class="width100 inventory-inner-table"
                >
                  <el-table-column label="LINE ITEM">
                    <template v-slot="scope">
                      <item-avatar
                        :name="'true'"
                        size="lg"
                        module="item"
                        :recordData="scope.row.itemType"
                        v-if="scope.row.itemType !== null"
                      ></item-avatar>
                      <tool-avatar
                        :name="'true'"
                        size="lg"
                        module="tool"
                        :recordData="scope.row.toolType"
                        v-else-if="scope.row.toolType !== null"
                      ></tool-avatar>
                    </template>
                  </el-table-column>
                  <el-table-column prop="quantity" sortable label="QUANTITY">
                  </el-table-column>
                  <el-table-column prop="unitPrice" sortable label="UNIT PRICE">
                  </el-table-column>
                  <el-table-column prop="asset.serialNumber" label="S NO">
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
        <!-- last section -->
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
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['viewname'],
  components: {
    Comments,
    Attachments,
    StoreRoomAvatar,
    UserAvatar,
    ItemAvatar,
    ToolAvatar,
  },
  data() {
    return {
      tabName: 'first',
      lineItemTab: 'lineItem',
      selectindex: false,
      loading: true,
      page: 1,
      fetchingMore: false,
      shipment: null,
      saving: false,
      buttonLoading: {
        stage: false,
        receive: false,
      },
    }
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', 'shipment')
  },
  watch: {
    currentShipmentId: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadShipmentDetails()
        }
      },
      immediate: true,
    },
    '$route.query.search': {
      handler(value) {
        let viewIndex = this.views.findIndex(
          view => view.name === 'filteredshipments'
        )

        if (value && isEmpty(viewIndex)) {
          this.views.push({
            displayName: 'Filtered Shipments',
            name: 'filteredshipments',
          })
        }
      },
    },
  },
  computed: {
    currentShipmentId() {
      return parseInt(this.$route.params.id)
    },
    currentView() {
      return this.$route.params.viewname
    },
    currentViewDetail() {
      if (this.$route.query.search) {
        return {
          displayName: 'Filtered Shipments',
          name: 'filteredShipments',
        }
      }
      return this.$store.state.view.currentViewDetail || {}
    },
    views() {
      return this.$store.state.view.views
    },
  },
  methods: {
    formFields(data) {
      this.customFields = data.fields.filter(
        field => field.field && field.field.default !== true
      )
    },
    getViewDetail() {
      this.$store.dispatch('view/loadViewDetail', {
        viewName: this.currentView,
        moduleName: 'shipment',
      })
    },
    async loadShipmentDetails() {
      let { error, data } = await API.get('/v2/shipment/getById', {
        recordId: this.currentShipmentId,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.shipment = data.shipment
        let title = '[#' + this.shipment.id + '] '
        this.setTitle(title)
      }
    },
    stageShipment() {
      let tempShipment = this.$helpers.cloneObject(this.shipment)
      tempShipment.status = 2
      this.buttonLoading.stage = true
      tempShipment.shipmentTrackingEnabled = true
      let param = {
        shipment: tempShipment,
      }
      let self = this
      this.$http
        .post('/v2/shipment/stageShipment', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success('Shipment Staged Successfully')
            self.buttonLoading.stage = false
            self.loadShipmentDetails()
          } else {
            self.$message.error(response.data.message)
            self.buttonLoading.stage = false
          }
        })
        .catch(() => {
          self.buttonLoading.stage = false
        })
    },
    receiveShipment() {
      let tempShipment = this.$helpers.cloneObject(this.shipment)
      tempShipment.status = 4
      tempShipment.shipmentTrackingEnabled = true
      this.buttonLoading.receive = true
      let param = {
        shipment: tempShipment,
      }
      let self = this
      this.$http
        .post('/v2/shipment/receiveShipment', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success('Shipment Received Successfully')
            self.loadShipmentDetails()
            self.buttonLoading.receive = false
          } else {
            self.$message.error(response.data.message)
            self.buttonLoading.receive = false
          }
        })
        .catch(() => {
          self.buttonLoading.receive = false
        })
    },
    getShipmentStatus() {
      switch (this.shipment.status) {
        case 1:
          return 'Not Staged'
        case 2:
          return 'Staged'
        case 3:
          return 'Shipped'
        case 4:
          return 'Received'
      }
    },
    handleCommand(command) {
      if (command === 'delete') {
        this.deleteShipment()
      }
    },
    deleteShipment() {
      let param = {
        recordIds: [this.shipment.id],
      }
      this.$http
        .post('/v2/shipment/delete', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.succes('Shipment Deleted Successfully')

            if (isWebTabsEnabled()) {
              let { name } = findRouteForModule('shipment', pageTypes.LIST)
              name &&
                this.$router.push({
                  name,
                  params: {
                    viewname: 'all',
                  },
                })
            } else {
              this.$router.push({
                path: '/app/inventory/shipment/' + 'all',
              })
            }
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
  },
}
</script>
