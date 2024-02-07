<template>
  <div class="fc-pm-form-right-main2">
    <div v-if="!model.isEdit" class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.new_pm') }}
    </div>
    <div v-else class="heading-black22 mT20 mB20">
      {{ $t('maintenance.wr_list.edit_pm') }}
    </div>
    <div v-if="loading" class="fc-pm-main-bg">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-show="!loading" class="fc-pm-main-bg">
      <div class="fc-pm-main-content2 pm-form-active-border-remove">
        <facilio-web-form
          :editObj="model.woData.woModel"
          :emitForm="model.emitForm"
          :name="'multi_web_pm'"
          :model.sync="model.woData.woModel"
          @loaded="setAfterLoadParams"
          @validated="data => relay('validated', data)"
          @failed="data => relay('failed', data)"
          class="fc-pm-form"
          :siteScope="model.woData.sites"
        >
        </facilio-web-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="moveToPrevious"
          >PREVIOUS</el-button
        >
        <el-button type="primary" class="modal-btn-save" @click="moveToNext"
          >PROCEED TO NEXT</el-button
        >
      </div>
    </div>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import PMMixin from '@/mixins/PMMixin'
export default {
  mixins: [PMMixin],
  props: ['model', 'tenant'],
  components: {
    FacilioWebForm,
  },
  data() {
    return {
      // loadingOption: false,
      // showFloorOption: true,
      // showSpaceCategOption: true,
      // showAssetCategOption: true,
      // fetchedAssets: [],
      // fetchedSpaces: [],
      // buildings: [],
      // dummyValue: [1],
      // dummyValue1: [1],
      // dummyValue2: [1],
      // chooserVisibility: false,
      // showAsset: true,
      // singleAssetSpacevisibility: false,
      loading: true,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups') // if required, get users/groups from pmnewform.vue as props, which is fetched from PMMixin.getScopedGroupsAndUsers().
    this.$store.dispatch('loadBuildings')
  },
  methods: {
    relay(event, data) {
      this.$emit(event, data)
    },
    // resetEmit() {
    //   this.model.emitForm = false
    // },
    setAfterLoadParams() {
      this.loading = false
      if (this.tenant) {
        this.model.woData.woModel.tenant.id = this.tenant
      }
    },
    // getScopeFilteredValues(siteId, buildingId) {
    //   if (!siteId && !buildingId) {
    //     return
    //   }
    //   let params = ''
    //   if (siteId) {
    //     params = `siteId=${siteId}`
    //   }
    //   if (buildingId) {
    //     params = params
    //       ? `${params}&buildingId=${buildingId}`
    //       : `buildingId=${buildingId}`
    //   }
    //   this.loadingOption = true
    //   console.log('getScopeFilteredValuesForPM from PMWOFORM')
    //   this.$http
    //     .get(`/workorder/getScopeFilteredValuesForPM?${params}`)
    //     .then(response => {
    //       this.showFloorOption = response.data.hasFloor
    //       this.fetchedAssets = response.data.assetCategoryIds
    //       this.fetchedSpaces = response.data.spaceCategoryIds
    //       this.buildings = response.data.buildings || []
    //       this.loadingOption = false
    //     })
    // },
    // onBuildingChange(buildingId) {
    //   this.getScopeFilteredValues(this.model.woData.woModel.site.id, buildingId)
    // },
    // resourceListHandler() {
    //   if (this.model.isEdit && this.model.isLoading) {
    //     return
    //   }
    //   if (this.model.woData.workOrderType === 'bulk') {
    //     if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
    //       if (
    //         this.model.woData.selectedSpaceList &&
    //         this.model.woData.selectedSpaceList.length !== 0
    //       ) {
    //         if (
    //           this.model.woData.isIncludeSpace === null ||
    //           this.model.woData.isIncludeSpace === undefined ||
    //           this.model.woData.isIncludeSpace
    //         ) {
    //           let presentInSpaceList = {}
    //           this.model.woData.selectedSpaceList.forEach(
    //             i => (presentInSpaceList[i.id] = true)
    //           )
    //           let presentInRPList = {}
    //           this.model.woData.resourceList.forEach(resource => {
    //             presentInRPList[resource.id] = true
    //           })
    //           this.model.woData.selectedSpaceList.forEach(i => {
    //             if (!presentInRPList[i.id]) {
    //               this.model.woData.resourceList.push({
    //                 id: i.id,
    //                 triggerNames: ['0'],
    //                 assignedTo: null,
    //                 notifications: [],
    //                 selected: false,
    //               })
    //             }
    //           })
    //           let newResourceList = []
    //           this.model.woData.resourceList.forEach(resource => {
    //             if (presentInSpaceList[resource.id]) {
    //               newResourceList.push(resource)
    //             }
    //           })
    //           this.$set(this.model.woData, 'resourceList', newResourceList)
    //         } else {
    //           if (
    //             this.model.woData.spacecategoryId &&
    //             this.model.woData.spacecategoryId > 0
    //           ) {
    //             this.$util
    //               .loadSpacesContext(4, null, [
    //                 {
    //                   key: 'site',
    //                   operator: 'is',
    //                   value: this.model.woData.woModel.site.id,
    //                 },
    //                 {
    //                   key: 'building',
    //                   operator: 'is',
    //                   value: this.model.woData.selectedBuilding,
    //                 },
    //                 {
    //                   key: 'spaceCategory',
    //                   operator: 'is',
    //                   value: this.model.woData.spacecategoryId,
    //                 },
    //               ])
    //               .then(response => {
    //                 this.$set(
    //                   this.model.woData,
    //                   'resourceList',
    //                   response.records
    //                 )
    //                 this.model.woData.resourceList = this.model.woData.resourceList.filter(
    //                   i => {
    //                     for (
    //                       let j = 0;
    //                       j < this.model.woData.selectedSpaceList.length;
    //                       j++
    //                     ) {
    //                       if (
    //                         this.model.woData.selectedSpaceList[j].id === i.id
    //                       ) {
    //                         return false
    //                       }
    //                     }
    //                     return true
    //                   }
    //                 )
    //                 this.model.woData.resourceList.forEach(resource => {
    //                   resource.triggerNames = ['0']
    //                   resource.assignedTo = null
    //                   resource.notifications = []
    //                   resource.selected = false
    //                 })
    //               })
    //           }
    //         }
    //       } else {
    //         if (
    //           this.model.woData.spacecategoryId &&
    //           this.model.woData.spacecategoryId > 0
    //         ) {
    //           this.$util
    //             .loadSpacesContext(4, null, [
    //               {
    //                 key: 'site',
    //                 operator: 'is',
    //                 value: this.model.woData.woModel.site.id,
    //               },
    //               {
    //                 key: 'building',
    //                 operator: 'is',
    //                 value: this.model.woData.selectedBuilding,
    //               },
    //               {
    //                 key: 'spaceCategory',
    //                 operator: 'is',
    //                 value: this.model.woData.spacecategoryId,
    //               },
    //             ])
    //             .then(response => {
    //               this.$set(this.model.woData, 'resourceList', response.records)
    //               this.model.woData.resourceList.forEach(resource => {
    //                 resource.triggerNames = ['0']
    //                 resource.assignedTo = null
    //                 resource.notifications = []
    //                 resource.selected = false
    //               })
    //             })
    //         }
    //       }
    //     } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
    //       if (
    //         this.model.woData.selectedResourceList &&
    //         this.model.woData.selectedResourceList.length !== 0
    //       ) {
    //         if (
    //           this.model.woData.isIncludeResource === null ||
    //           this.model.woData.isIncludeResource === undefined ||
    //           this.model.woData.isIncludeResource
    //         ) {
    //           let presentInAssetList = {}
    //           this.model.woData.selectedResourceList.forEach(
    //             i => (presentInAssetList[i.id] = true)
    //           )
    //           let presentInRPList = {}
    //           this.model.woData.resourceList.forEach(resource => {
    //             presentInRPList[resource.id] = true
    //           })
    //           this.model.woData.selectedResourceList.forEach(i => {
    //             if (!presentInRPList[i.id]) {
    //               this.model.woData.resourceList.push({
    //                 id: i.id,
    //                 triggerNames: ['0'],
    //                 assignedTo: null,
    //                 notifications: [],
    //                 selected: false,
    //               })
    //             }
    //           })
    //           let newResourceList = []
    //           this.model.woData.resourceList.forEach(resource => {
    //             if (presentInAssetList[resource.id]) {
    //               newResourceList.push(resource)
    //             }
    //           })
    //           this.$set(this.model.woData, 'resourceList', newResourceList)
    //         } else {
    //           if (
    //             this.model.woData.assetCategoryId &&
    //             this.model.woData.assetCategoryId > 0
    //           ) {
    //             let spaceId = this.model.woData.selectedBuilding
    //             if (
    //               !this.model.woData.selectedBuilding ||
    //               this.model.woData.selectedBuilding < 0
    //             ) {
    //               spaceId = this.model.woData.woModel.site.id
    //             }
    //             this.$util
    //               .loadAsset({
    //                 spaceId: spaceId,
    //                 categoryId: this.model.woData.assetCategoryId,
    //               })
    //               .then(response => {
    //                 this.$set(
    //                   this.model.woData,
    //                   'resourceList',
    //                   response.assets.filter(i => {
    //                     for (
    //                       let j = 0;
    //                       j < this.model.woData.selectedResourceList.length;
    //                       j++
    //                     ) {
    //                       if (
    //                         this.model.woData.selectedResourceList[j].id ===
    //                         i.id
    //                       ) {
    //                         return false
    //                       }
    //                     }
    //                     return true
    //                   })
    //                 )
    //                 this.model.woData.resourceList.forEach(resource => {
    //                   resource.triggerNames = ['0']
    //                   resource.assignedTo = null
    //                   resource.notifications = []
    //                   resource.selected = false
    //                 })
    //               })
    //           }
    //         }
    //       } else {
    //         if (
    //           this.model.woData.assetCategoryId &&
    //           this.model.woData.assetCategoryId > 0
    //         ) {
    //           let spaceId = this.model.woData.selectedBuilding
    //           if (
    //             !this.model.woData.selectedBuilding ||
    //             this.model.woData.selectedBuilding < 0
    //           ) {
    //             spaceId = this.model.woData.woModel.site.id
    //           }
    //           this.$util
    //             .loadAsset({
    //               spaceId: spaceId,
    //               categoryId: this.model.woData.assetCategoryId,
    //             })
    //             .then(response => {
    //               this.$set(this.model.woData, 'resourceList', response.assets)
    //               this.model.woData.resourceList.forEach(resource => {
    //                 resource.triggerNames = ['0']
    //                 resource.assignedTo = null
    //                 resource.notifications = []
    //                 resource.selected = false
    //               })
    //             })
    //         }
    //       }
    //     }
    //   }
    // },
    // resourceListAllFloorsHandler(newVal, oldVal) {
    //   if (this.model.isEdit && this.model.isLoading) {
    //     return
    //   }
    //   if (this.model.woData.workOrderType === 'bulk' && newVal !== oldVal) {
    //     if (this.model.woData.resourceType === 'ALL_FLOORS') {
    //       if (
    //         this.model.woData.selectedFloorList &&
    //         this.model.woData.selectedFloorList.length !== 0
    //       ) {
    //         if (
    //           this.model.woData.isIncludeFloor === null ||
    //           this.model.woData.isIncludeFloor === undefined ||
    //           this.model.woData.isIncludeFloor
    //         ) {
    //           let presentInFloorList = {}
    //           this.model.woData.selectedFloorList.forEach(
    //             i => (presentInFloorList[i.id] = true)
    //           )
    //           let presentInRPList = {}
    //           this.model.woData.resourceList.forEach(resource => {
    //             presentInRPList[resource.id] = true
    //           })
    //           this.model.woData.selectedFloorList.forEach(i => {
    //             if (!presentInRPList[i.id]) {
    //               this.model.woData.resourceList.push({
    //                 id: i.id,
    //                 triggerNames: ['0'],
    //                 assignedTo: null,
    //                 notifications: [],
    //                 selected: false,
    //               })
    //             }
    //           })
    //           let newResourceList = []
    //           this.model.woData.resourceList.forEach(resource => {
    //             if (presentInFloorList[resource.id]) {
    //               newResourceList.push(resource)
    //             }
    //           })
    //           this.$set(this.model.woData, 'resourceList', newResourceList)
    //         } else {
    //           this.$util
    //             .loadSpace([3], null, [
    //               { key: 'site', value: this.model.woData.woModel.site.id },
    //               {
    //                 key: 'building',
    //                 value: this.model.woData.selectedBuilding,
    //               },
    //             ])
    //             .then(data => {
    //               let rList = data.basespaces.filter(i => {
    //                 for (
    //                   let j = 0;
    //                   j < this.model.woData.selectedFloorList.length;
    //                   j++
    //                 ) {
    //                   if (this.model.woData.selectedFloorList[j].id === i.id) {
    //                     return false
    //                   }
    //                 }
    //                 return true
    //               })
    //               this.$set(this.model.woData, 'resourceList', rList)
    //               this.model.woData.resourceList.forEach(resource => {
    //                 resource.triggerNames = ['0']
    //                 resource.assignedTo = null
    //                 resource.notifications = []
    //                 resource.selected = false
    //               })
    //             })
    //         }
    //       } else {
    //         this.$util
    //           .loadSpace([3], null, [
    //             { key: 'site', value: this.model.woData.woModel.site.id },
    //             { key: 'building', value: this.model.woData.selectedBuilding },
    //           ])
    //           .then(data => {
    //             this.$set(this.model.woData, 'resourceList', data.basespaces)
    //             this.model.woData.resourceList.forEach(resource => {
    //               resource.triggerNames = ['0']
    //               resource.assignedTo = null
    //               resource.notifications = []
    //               resource.selected = false
    //             })
    //           })
    //       }
    //     }
    //   }
    // },
    // associateResource(selectedObj) {
    //   if (selectedObj.resourceList && selectedObj.resourceList.length) {
    //     if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
    //       this.model.woData.selectedResourceList = selectedObj.resourceList
    //       this.model.woData.isIncludeResource = selectedObj.isInclude
    //     } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
    //       this.model.woData.selectedSpaceList = selectedObj.resourceList
    //       this.model.woData.isIncludeSpace = selectedObj.isInclude
    //     } else if (this.model.woData.resourceType === 'ALL_FLOORS') {
    //       this.model.woData.selectedFloorList = selectedObj.resourceList
    //       this.model.woData.isIncludeFloor = selectedObj.isInclude
    //     }
    //     this.resourceListHandler()
    //   } else {
    //     this.model.woData.selectedResourceList = []
    //     this.model.woData.isIncludeResource = true
    //     this.model.woData.selectedSpaceList = []
    //     this.model.woData.isIncludeSpace = true
    //     this.model.woData.selectedFloorList = []
    //     this.model.woData.isIncludeFloor = true
    //   }
    //   this.chooserVisibility = false
    //   this.model.woData.resourceQuery = null
    // },
    // associateSingleAssetSpace(selectedObj) {
    //   this.singleAssetSpacevisibility = false
    //   this.model.woData.spaceAssetDisplayName = selectedObj.name
    //   this.model.woData.singleResource = selectedObj
    // },
    moveToNext() {
      // validateMultiSiteWO from mixin
      // let isValid = this.validateMultiSiteWO()
      // if (isValid) {
      this.$emit('next')
      // }
    },
    moveToPrevious() {
      // validateMultiSiteWO from mixin
      // let isValid = this.validateMultiSiteWO()
      // if (isValid) {
      this.$emit('previous')
      // }
    },
  },
}
</script>
<style lang="scss">
.suffix-disabled .el-select__caret {
  display: none;
}
.input-w35 {
  width: 35px;
}
</style>
