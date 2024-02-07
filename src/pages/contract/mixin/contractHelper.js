import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      revisionHistoryJSON: {
        list: [],
        loading: false,
        visibility: false,
        id: null,
      },
    }
  },
  computed: {
    showRevisionHistory() {
      if (this.$route.query.showRevisionHistory) {
        return true
      } else {
        return false
      }
    },
    revisionHistoryObj() {
      if (this.$route.query.showRevisionHistory) {
        return JSON.parse(this.$route.query.showRevisionHistory)
      } else {
        return null
      }
    },
    getCurrentModule() {
      return this.$route.meta.module || null
    },
  },
  watch: {
    showRevisionHistory() {
      if (this.showRevisionHistory) {
        this.getContractRevisionHistory(
          this.revisionHistoryObj,
          this.moduleVsName()
        )
      }
    },
  },
  methods: {
    cancelNotificationPreference() {
      this.preferenceVisibility = false
    },
    back() {
      if (
        this.showRevisionHistory &&
        this.revisionHistoryObj &&
        this.revisionHistoryObj.id
      ) {
        this.$router.push({
          params: {
            id: this.revisionHistoryObj.id,
          },
        })
      } else {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.getCurrentModule, pageTypes.LIST) || {}
          if (name) {
            let {
              params: { viewname },
            } = this.$route
            this.$router.push({ name, params: { viewname } })
          }
        } else {
          let url =
            '/app/ct/' +
            this.getCurrentModule +
            '/' +
            this.$route.params.viewname
          this.$router.push({ path: url, query: this.$route.query })
        }
      }
    },
    getHistorySummaryLink(id) {
      this.$router.push({
        params: { id },
        query: this.$route.query,
      })
    },
    updateContractLineItem(message, name) {
      let param = {
        lineItems: this.summaryData.lineItems,
      }
      this.$http
        .post('v2/' + name + '/addOrUpdateLineItem', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(message)
            this.loadSummary()
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    addContractLineItem(lineItem, name) {
      let param = {
        lineItems: lineItem,
      }
      this.$http
        .post('v2/' + name + '/addOrUpdateLineItem', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Line Item Added Successfully')
            this.loadSummary()
            this.addLineItemsVisibility = false
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    deleteContractLineItem(recordIds, message, name) {
      let param = {
        recordIds: recordIds,
      }
      this.$http
        .post('v2/' + name + '/deleteLineItem', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(message)
            this.loadSummary()
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    revisionVisibilityToggle() {
      this.revisionHistoryJSON.visibility = false
      this.revisionHistoryJSON.list = []
    },
    isContractEditable(status) {
      if (
        (status === 1 || status === 6) &&
        this.$hasPermission('contract:UPDATE')
      ) {
        return true
      } else {
        return false
      }
    },
    isContractRemovable(status) {
      if (
        (status === 1 || status === 6) &&
        this.$hasPermission('contract:DELETE')
      ) {
        return true
      } else {
        return false
      }
    },
    updateContractStatus(status, message, name) {
      let param = {
        recordIds: [this.summaryData.id],
        status: status,
      }
      this.$http
        .post('/v2/' + name + '/changeStatus', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(message)
            this.loadSummary()
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    loadAssetsList(summaryData) {
      this.loading.list = true
      this.loadContractAssociatedAssetsList(summaryData.contractType)
      let itemTypeIds = []
      let toolTypeIds = []
      for (let data of summaryData.lineItems) {
        if (data.inventoryType === 1) {
          itemTypeIds.push(data.itemType.id)
        } else if (data.inventoryType === 2) {
          toolTypeIds.push(data.toolType.id)
        }
      }
      let param1 = { inventoryType: 1, itemTypeIds: itemTypeIds }
      let param2 = { inventoryType: 2, toolTypeIds: toolTypeIds }
      if (itemTypeIds.length > 0) {
        this.$http
          .post('v2/assets/type', param1)
          .then(response => {
            if (response.data.responseCode === 0) {
              this.assetListForItemType = response.data.result.assets
            } else {
              throw new Error(response.data.message)
            }
          })
          .catch(error => {
            this.$message.error(error)
          })
      }
      if (toolTypeIds.length > 0) {
        this.$http
          .post('v2/assets/type', param2)
          .then(response2 => {
            if (response2.data.responseCode === 0) {
              this.assetListForToolType = response2.data.result.assets
            } else {
              throw new Error(response2.data.message)
            }
          })
          .catch(error => {
            this.$message.error(error)
          })
      }
    },
    loadContractAssociatedAssetsList(type) {
      let param = {
        contractType: type,
      }
      this.$http
        .post('v2/contract/activeContractsAssociatedAssets', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.contractAssociatedAssetList = response.data.result.assetId
          } else {
            throw new Error(response.data.message)
          }
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
    associateTermsAndCondition(summaryData, termsAndConditionList, name) {
      this.loading.associateTerms = true
      let tempTandCList = []
      for (let i in termsAndConditionList) {
        tempTandCList.push({
          contractId: summaryData.id,
          terms: { id: termsAndConditionList[i].id },
        })
      }
      let param = {
        associatedTerms: tempTandCList,
        recordId: summaryData.id,
      }
      this.$http
        .post('v2/' + name + '/associateTerms', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Terms Associated Successfully')
            this.$emit('refreshList')
            this.cancelForm()
          } else {
            this.$message.error(response.data.message)
          }
          this.loading.associateTerms = false
        })
        .catch(error => {
          this.$message.error(error)
          this.loading.associateTerms = false
        })
    },
    deleteAssociatedTerms(termsId, name) {
      let param = {
        recordIds: [termsId],
      }
      this.loading.terms = true
      this.$http
        .post('v2/' + name + '/disassociateTerms', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Terms Deleted Successfully')
            this.$emit('refreshList')
          } else {
            this.$message.error(response.data.message)
          }
          this.loading.terms = false
        })
        .catch(error => {
          this.$message.error(error)
          this.loading.terms = false
        })
    },
    deleteAssociatedAsset(termsId) {
      let param = {
        recordIds: [termsId],
      }
      this.saving = true
      this.$http
        .post('v2/' + 'contract' + '/disassociateAssets', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Asset Disassociated Successfully')
            this.$emit('refreshList')
          } else {
            this.$message.error(response.data.message)
          }
          this.saving = false
        })
        .catch(error => {
          this.$message.error(error)
          this.saving = false
        })
    },
    associateAsset(data, addAssetList) {
      let param = {
        recordId: data.id,
        associatedAssets: addAssetList,
      }
      this.saving = true
      this.$http
        .post('v2/' + 'contract' + '/associateAsset', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Asset Associated Successfully')
            this.$emit('refreshList')
            this.cancelForm()
          } else {
            this.$message.error(response.data.message)
          }
          this.saving = false
        })
        .catch(error => {
          this.$message.error(error)
          this.saving = false
        })
    },
    refreshAssociatedAssets() {
      this.loadSummary()
    },
    purchaseAsset(data, name) {
      let param = this.$helpers.cloneObject(data)
      this.saving = true
      this.$http
        .post('v2/' + name + '/purchaseAsset', { associatedAssets: param })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Asset Purchased Successfully')
            this.$emit('refreshList')
            this.cancelPurchaseForm()
          } else {
            this.$message.error(response.data.message)
          }
          this.saving = false
        })
        .catch(error => {
          this.$message.error(error)
          this.saving = false
        })
    },
    returnAssetCall(data, name) {
      let param = this.$helpers.cloneObject(data)
      this.saving = true
      this.$http
        .post('v2/' + name + '/returnAsset', { associatedAssets: param })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success('Asset Returned Successfully')
            this.$emit('refreshList')
            this.cancelPurchaseForm()
          } else {
            this.$message.error(response.data.message)
          }
          this.saving = false
        })
        .catch(error => {
          this.$message.error(error)
          this.saving = false
        })
    },
    getContractRevisionHistory(contract, name) {
      let filters = {
        parentId: {
          operatorId: 9,
          value: [contract.parentId + ''],
        },
      }
      this.revisionHistoryJSON.loading = true
      let url = '/v2/' + name + '/list'
      this.$http
        .get(
          url +
            '?filters=' +
            encodeURIComponent(JSON.stringify(filters)) +
            '&includeParentFilter=true'
        )
        .then(response => {
          if (response.data.responseCode === 0) {
            this.revisionHistoryJSON.list = response.data.result[
              this.nameVsListResult(name)
            ].reverse()
            this.revisionHistoryJSON.visibility = true
            this.revisionHistoryJSON.loading = false
            this.revisionHistoryJSON.id = contract.id
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    moduleVsName() {
      if (this.getCurrentModule === 'purchasecontracts') {
        return 'purchasecontract'
      } else if (this.getCurrentModule === 'labourcontracts') {
        return 'labourcontract'
      } else if (this.getCurrentModule === 'rentalleasecontracts') {
        return 'rentalleasecontract'
      } else if (this.getCurrentModule === 'warrantycontracts') {
        return 'warrantycontract'
      }
    },
    nameVsListResult(name) {
      if (name === 'purchasecontract') {
        return 'purchasecontracts'
      } else if (name === 'labourcontract') {
        return 'labourcontracts'
      } else if (name === 'rentalleasecontract') {
        return 'rentalleasecontracts'
      } else if (name === 'warrantycontract') {
        return 'warrantycontracts'
      }
    },
    loadTermsAndConditionList() {
      this.loading.allTerms = true
      this.$http
        .get('v2/termsandconditions/all')
        .then(response => {
          if (response.data.responseCode === 0) {
            this.termsAndConditionList =
              response.data.result.termsandconditions || []
            if (!isEmpty(this.summaryData.termsAssociated)) {
              this.summaryData.termsAssociated.forEach(terms => {
                this.termsAndConditionList = this.termsAndConditionList.filter(
                  tandc => {
                    return tandc.id !== (terms.terms || {}).id
                  }
                )
              })
            }
          } else {
            throw new Error(response.data.message)
          }
          this.loading.allTerms = false
        })
        .catch(error => {
          this.$message.error(error)
          this.loading.allTerms = false
        })
    },
    reviseContract(summaryData, name) {
      let param = {
        [this.moduleNameVsParamName(name)]: summaryData,
      }
      let url = '/v2/' + name + '/reviseContract'
      this.$http
        .post(url, param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(
              '' + this.moduleNameVsDisplayName(name) + ' Revised Successfully'
            )
            this.$router.push({
              params: {
                id: response.data.result.revisedRecord.id,
              },
              query: {
                refresh: true,
              },
            })
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    moduleNameVsDisplayName(name) {
      if (name === 'purchasecontract') {
        return 'Purchase Contract'
      } else if (name === 'labourcontract') {
        return 'Labor Contract'
      } else if (name === 'rentalleasecontract') {
        return 'Lease/Rental Contract'
      } else if (name === 'warrantycontract') {
        return 'Warranty Contract'
      }
    },
    moduleNameVsParamName(name) {
      if (name === 'purchasecontract') {
        return 'purchaseContract'
      } else if (name === 'labourcontract') {
        return 'labourContract'
      } else if (name === 'rentalleasecontract') {
        return 'rentalLeaseContract'
      } else if (name === 'warrantycontract') {
        return 'warrantyContract'
      }
    },
    changeSingleContractStatus(recordIdList, updateStatus, name) {
      let self = this
      let param = {
        recordIds: recordIdList,
        status: updateStatus,
      }
      let url = '/v2/' + name + '/changeStatus'
      self.$http
        .post(url, param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(
              'Contract ' +
                this.getContractStatus(updateStatus) +
                ' Successfully'
            )
            this.loadPurchaseContracts()
            this.selectedPurchaseContract = []
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    getContractStatus(status) {
      switch (status) {
        case 1:
          return 'Waiting for Approval'
        case 2:
          return 'Approved'
        case 3:
          return 'Closed'
        case 4:
          return 'Cancelled'
        case 5:
          return 'Suspended'
        case 6:
          return 'Pending For Revision'
        case 7:
          return 'Revised'
      }
    },
    contractTypeEnumFormatter(val) {
      switch (val) {
        case 1:
          return 'Purchase'
        case 2:
          return 'Labor'
        case 3:
          return 'Warranty'
        case 4:
          return 'Lease/Rental'
      }
    },
    convertMilliSecondsToTimeHHMM(val) {
      if (val > 0) {
        return moment()
          .startOf('day')
          .milliseconds(val)
          .format('HH:mm')
      } else {
        return '00:00'
      }
    },
    updatePaymentActions(summaryData) {
      this.$msgbox({
        message: 'Have you made the previous Payment?',
        title: 'Payment',
        confirmButtonText: 'Yes',
        cancelButtonText: 'No',
        confirmButtonClass: 'modal-btn-save',
        cancelButtonClass: 'modal-btn-cancel',
        showClose: false,
        showCancelButton: true,
        customClass: 'contract-message-box',
      }).then(() => {
        this.updatePaymentApi(summaryData)
      })
    },
    updatePaymentApi(summaryData) {
      this.$http
        .post('v2/contract/changePaymentStatus', { recordId: summaryData.id })
        .then(response => {
          if (response.data.responseCode === 0) {
            let message
            if (
              this.summaryData.endDate > response.data.result.nextPaymentDate
            ) {
              message =
                'Your Next Payment Date is ' +
                this.$options.filters.formatDate(
                  response.data.result.nextPaymentDate,
                  true
                )
            } else {
              message = 'Payment Status Updated Successfully'
            }
            this.$message.success(message)
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(error => {
          this.$message.error(error)
        })
    },
  },
}
