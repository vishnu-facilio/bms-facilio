<template>
  <div>
    <el-table
      :data="summaryData.associatedAssets"
      v-loading="loading.associated"
      empty-text="No Associated Assets found."
      :default-sort="{ prop: 'asset.name', order: 'descending' }"
      class="width100 inventory-inner-table"
    >
      <el-table-column
        prop="asset.name"
        sortable
        label="NAME"
      ></el-table-column>
      <el-table-column
        prop="asset.serialNumber"
        sortable
        label="SERIAL NO"
      ></el-table-column>
      <el-table-column
        prop="asset.unitPrice"
        sortable
        label="UNIT PRICE"
      ></el-table-column>
      <el-table-column
        prop="leaseEndValue"
        sortable
        label="LEASE END VALUE"
      ></el-table-column>
      <el-table-column
        prop="totalCost"
        sortable
        label="TOTAL COST"
      ></el-table-column>
      <el-table-column sortable prop="poId" width="100" label="PO-ID">
        <template v-slot="scope">
          <router-link
            class="fc-id"
            :to="redirectToPurchaseSummary(scope.row.poId)"
            v-if="scope.row.poId > 0"
            >{{ '#' + scope.row.poId }}</router-link
          >
          <div v-else>---</div>
        </template>
      </el-table-column>
      <el-table-column prop="statusEnum" label="STATUS"></el-table-column>
      <el-table-column width="80">
        <template v-slot="scope">
          <div class="visibility-hide-actions export-dropdown-menu">
            <i
              class="el-icon-delete pointer edit-icon-color pL18"
              v-if="$hasPermission('contract:UPDATE')"
              data-arrow="true"
              title="Disassociate Asset"
              v-tippy
              @click="deleteAssociatedAsset(scope.row.id, type)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="associateAssetsVisibility">
      <el-dialog
        :visible.sync="associateAssetsVisibility"
        title="Associate Assets"
        :fullscreen="false"
        open="top"
        width="65%"
        :before-close="cancelForm"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
        :append-to-body="true"
      >
        <div>
          <table class="setting-list-view-table store-table">
            <thead>
              <tr>
                <th
                  class="setting-table-th setting-th-text uppercase width240px"
                >
                  Asset
                </th>
                <th
                  class="setting-table-th setting-th-text uppercase width240px"
                >
                  Total Cost
                </th>
                <th
                  class="setting-table-th setting-th-text uppercase width240px"
                >
                  Lease End Value
                </th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(formData, index) in associateAssetData"
                :key="index"
                class="visibility-visible-actions"
              >
                <td class="module-builder-td">
                  <el-select
                    filterable
                    clearable
                    v-model="formData.asset.id"
                    class="fc-input-full-border-select2 width250px"
                  >
                    <el-option
                      v-for="(option, i) in assetList"
                      :key="i"
                      :label="option.name"
                      :value="option.id"
                      v-if="!isSelected(option.id, index)"
                    >
                      <span class="fL">{{ option.name }}</span>
                      <span class="select-float-right-text13">{{
                        option.serialNumber
                      }}</span>
                    </el-option>
                  </el-select>
                </td>
                <td>
                  <el-input
                    placeholder="Total Cost"
                    type="number"
                    v-model="formData.totalCost"
                    class="fc-input-full-border-select2 width110px"
                  ></el-input>
                </td>
                <td>
                  <el-input
                    placeholder="Lease End Value"
                    type="number"
                    v-model="formData.leaseEndValue"
                    class="fc-input-full-border-select2 width110px"
                  ></el-input>
                </td>
                <td>
                  <div class="visibility-hide-actions export-dropdown-menu">
                    <span
                      @click="addFormDataEntry(associateAssetData)"
                      title="Add"
                      v-tippy
                    >
                      <img src="~assets/add-icon.svg" class="mR10" />
                    </span>
                    <span
                      v-if="associateAssetData.length > 1"
                      @click="removeFormDataEntry(associateAssetData, index)"
                      title="Remove"
                      v-tippy
                    >
                      <img src="~assets/remove-icon.svg" />
                    </span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()"
              >CLOSE</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="addFormData()"
              :loading="saving"
              >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
            >
          </div>
        </div>
      </el-dialog>
    </div>
    <div v-if="purchaseAssetVisibility || returnAssetVisibility">
      <el-dialog
        :visible.sync="
          purchaseAssetVisibility
            ? purchaseAssetVisibility
            : returnAssetVisibility
        "
        :title="purchaseAssetVisibility ? 'Purchase Assets' : 'Return Assets'"
        :fullscreen="false"
        open="top"
        width="65%"
        :before-close="cancelPurchaseForm"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
        :append-to-body="true"
      >
        <div class="height400">
          <el-table
            :data="purchaseAssetTableList"
            ref="table1"
            @selection-change="selectPurchaseAssetActions"
            height="350"
            empty-text="No Asset Available to Purchase"
            :default-sort="{ prop: 'asset.name', order: 'descending' }"
            class="inventory-inner-table width100"
          >
            <el-table-column width="60" type="selection"></el-table-column>
            <el-table-column
              prop="asset.name"
              sortable
              label="NAME"
            ></el-table-column>
            <el-table-column
              prop="asset.serialNumber"
              sortable
              label="SERIAL NO"
            ></el-table-column>
            <el-table-column
              prop="asset.unitPrice"
              label="UNIT PRICE"
            ></el-table-column>
            <el-table-column
              prop="leaseEndValue"
              label="LEASE END VALUE"
            ></el-table-column>
            <el-table-column
              prop="totalCost"
              label="TOTAL COST"
            ></el-table-column>
          </el-table>
        </div>

        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelPurchaseDialog()"
            >CANCEL</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="addPurchasedAssets()"
            v-if="purchaseAssetVisibility"
            :loading="loading.purchase"
            >PURCHASE</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            v-else-if="returnAssetVisibility"
            @click="returnAssets()"
            :loading="loading.return"
            >RETURN</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import contractMixin from 'pages/contract/mixin/contractHelper'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  props: [
    'summaryData',
    'type',
    'associateAssetsVisibility',
    'purchaseAssetVisibility',
    'returnAssetVisibility',
  ],
  mixins: [contractMixin],
  data() {
    return {
      associatedAssetList: null,
      loading: {
        associated: false,
        list: false,
        purchase: false,
        return: false,
      },
      saving: false,
      associateAssetData: [
        {
          asset: {
            id: null,
          },
          totalCost: null,
          leaseEndValue: null,
        },
      ],
      selectedPurchaseAssetObj: null,
      assetListForItemType: [],
      assetListForToolType: [],
      contractAssociatedAssetList: [],
    }
  },
  computed: {
    purchaseAssetTableList() {
      let list = this.summaryData.associatedAssets.filter(i => {
        return i.statusType < 0
      })
      return list
    },
    assetList() {
      let list = []
      if (this.assetListForItemType && this.assetListForItemType.length > 0) {
        list.push.apply(list, this.assetListForItemType)
      }
      if (this.assetListForToolType && this.assetListForToolType.length > 0) {
        list.push.apply(list, this.assetListForToolType)
      }
      if (
        this.contractAssociatedAssetList &&
        this.contractAssociatedAssetList.length > 0
      ) {
        let associatedListMap = new Set(
          this.contractAssociatedAssetList.map(val => val.asset)
        )
        list = list.filter(li => {
          return !associatedListMap.has(li.id)
        })
      }
      return list
    },
  },
  mounted() {
    // this.loadAssociatedAssets(this.summaryData, this.type)
  },
  watch: {
    associateAssetsVisibility(newVal) {
      if (newVal) {
        this.loadAssetsList(this.summaryData)
      }
    },
  },
  methods: {
    removeFormDataEntry(list, index) {
      list.splice(index, 1)
    },
    addFormDataEntry(field) {
      field.push({ asset: { id: null }, totalCost: null, leaseEndValue: null })
    },
    addFormData() {
      this.associateAsset(this.summaryData, this.associateAssetData, this.type)
    },
    cancelForm() {
      this.associateAssetData = [
        {
          asset: {
            id: null,
          },
          totalCost: null,
          leaseEndValue: null,
        },
      ]
      this.$emit('update:associateAssetsVisibility', false)
    },
    cancelPurchaseForm() {
      this.selectedPurchaseAssetObj = null
      if (this.purchaseAssetVisibility) {
        this.$emit('update:purchaseAssetVisibility', false)
      } else if (this.returnAssetVisibility) {
        this.$emit('update:returnAssetVisibility', false)
      }
    },
    cancelReturnForm() {
      this.selectedPurchaseAssetObj = null
      this.$emit('update:returnAssetVisibility', false)
    },
    selectPurchaseAssetActions(val) {
      this.selectedPurchaseAssetObj = val
    },
    addPurchasedAssets() {
      this.purchaseAsset(this.selectedPurchaseAssetObj, this.type)
    },
    returnAssets() {
      this.returnAssetCall(this.selectedPurchaseAssetObj, this.type)
    },
    isSelected(id, index) {
      for (let i in this.associateAssetData) {
        if (this.associateAssetData[i].asset.id === id) {
          if (parseInt(i) === index) {
            return false
          }
          return true
        }
      }
      return false
    },
    redirectToPurchaseSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('purchaseorder', pageTypes.OVERVIEW) || {}
        if (name) {
          return { name, params: { viewname: 'all', id } }
        }
      } else {
        return { path: '/app/purchase/po/all/summary/' + id }
      }
    },
  },
}
</script>
