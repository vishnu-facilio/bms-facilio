<template>
  <div style="height:100%">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <LineItemList
        ref="lineItemComponent"
        v-bind="$attrs"
        :config="listConfiguration"
        :moduleDisplayName="moduleDisplayName"
        :moduleName="moduleName"
        :widgetDetails="widgetDetails"
        viewname="hidden-all"
        @emptyStateBtn="showWizard"
        style="height:100%"
      >
        <template #footer class="line-item-list-footer">
          <el-button
            type="primary"
            class="line-item-list-footer-add-btn"
            @click="showWizard()"
          >
            {{ $t('common.safetyPlan.select_workAsset') }}
          </el-button>
        </template>
      </LineItemList>
      <WorkAssetForm
        v-if="showMsgPopup"
        :key="showMsgPopup"
        :details="details"
        :availableAssets="availableAssets"
        :availableSpaces="availableSpaces"
        :moduleDisplayName="moduleDisplayName"
        @getWorkAssetHazards="getWorkAssetHazards"
        @closeDialog="closeDialog"
      ></WorkAssetForm>
    </template>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import FLookupField from '@/forms/FLookupField'
import WorkAssetForm from 'src/pages/safetyplan/sp/WorkAssetForm.vue'

const EQUALS_OPERATOR = 9

export default {
  props: ['widget', 'details'],
  components: {
    LineItemList,
    V3LookupFieldWizard,
    FLookupField,
    WorkAssetForm,
  },
  data() {
    return {
      isLoading: false,
      parentModuleName: null,
      showMsgPopup: false,
      availableAssets: [],
      availableSpaces: [],
    }
  },
  computed: {
    moduleName() {
      return 'workAsset'
    },
    moduleDisplayName() {
      return 'Work Assets'
    },
    filters() {
      let { details } = this
      let { id } = details
      let filterObj = {}
      filterObj['safetyPlan'] = { operatorId: 36, value: [`${id}`] }
      return filterObj
    },
    editConfig() {
      return {
        canHideEdit: true,
      }
    },
    deleteConfig() {
      return {
        canHideDelete: false,
      }
    },
    quickCreateConfig() {
      return {
        canHideAddBtn: true,
      }
    },
    searchAndFilterConfig() {
      return { canHideSearch: true, canHideFilter: false }
    },
    listConfiguration() {
      let {
        filters,
        editConfig,
        deleteConfig,
        searchAndFilterConfig,
        quickCreateConfig,
      } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        mainfieldAction: () => {},
        canHideFooter: true,
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(quickCreateConfig || {}),
        ...(searchAndFilterConfig || {}),
        canEnableMainFieldAction: false,
      }
    },
    widgetDetails() {
      let { moduleDisplayName } = this
      let emptyStateText = this.$t('common.safetyPlan.no_workasset_added', {
        moduleName: moduleDisplayName,
      })

      return {
        perPage: 7,
        emptyStateText,
        emptyStateBtnList: [
          { label: 'Add Work Asset', value: 'add_work_asset' },
        ],
      }
    },
  },
  methods: {
    init() {
      this.selectedSpace = null
      this.selectedAsset = null
      this.wizardField = null
    },
    async showWizard() {
      let component = this.$refs['lineItemComponent']
      if (component) {
        let workAssets = component.recordList
        this.getAvailableSpaceAndAssetIds(workAssets)
      }
      this.showMsgPopup = true
    },
    closeDialog() {
      this.showMsgPopup = false
    },
    async getWorkAssetHazards(selectedData, moduleName) {
      this.isLoading = true
      let filter = {
        [moduleName]: {
          operatorId: EQUALS_OPERATOR,
          value: [`${selectedData}`],
        },
      }
      let params = {
        withCount: true,
        filters: JSON.stringify(filter),
      }
      let { list, error } = await API.fetchAll(`${moduleName}Hazard`, params, {
        force: true,
      })
      if (!error) {
        if (!isEmpty(list)) this.addSpecialModuleHazards(list)
      }
      this.isLoading = false
    },
    async addSpecialModuleHazards(list) {
      let safetyPlanId = this.$getProperty(this, 'details.id', null)
      if (!isEmpty(list)) {
        let safetyPlanHazards = list.map(data => {
          let { hazard = {} } = data
          let { id = 0 } = hazard || {}
          if (!isEmpty(id))
            return { safetyPlan: { id: safetyPlanId }, hazard: { id } }
          return null
        })
        let safetyPlanHazardList = safetyPlanHazards.filter(
          data => !isEmpty(data)
        )

        let url = 'v3/modules/data/bulkCreate'
        let params = {
          data: {
            safetyPlanHazard: safetyPlanHazardList,
          },
          moduleName: 'safetyPlanHazard',
          params: {
            safetyPlan: safetyPlanId,
          },
        }
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          await eventBus.$emit('refresh-hazards')
        }
      }
    },
    getAvailableSpaceAndAssetIds(workAssets) {
      this.availableAssets = (workAssets || [])
        .filter(woAsset => !isEmpty(woAsset?.asset?.id))
        .map(woAsset => `${woAsset?.asset?.id}`)
      this.availableSpaces = (workAssets || [])
        .filter(woAsset => !isEmpty(woAsset?.space?.id))
        .map(woAsset => `${woAsset?.space?.id}`)
    },
    async refreshList() {
      let component = this.$refs['lineItemComponent']
      if (component) {
        component.loadRecords(true)
      }
    },
    getParentModuleName() {
      let pageModuleName = this.$attrs.moduleName
      let baseSpaces = ['site', 'building', 'floor', 'space']
      if (baseSpaces.includes(pageModuleName)) {
        this.parentModuleName = 'space'
      } else {
        this.parentModuleName = pageModuleName
      }
    },
    closeWizard() {
      this.$set(this.wizardField, 'selectedItems', [])
      this.canShowWizard = false
    },
  },
}
</script>

<style lang="scss" scoped>
.View-Precaution {
  font-size: 12px;
  line-height: 1;
  letter-spacing: 0.5px;
  color: #2b8bff;
}

.text_center {
  cursor: pointer;
  text-align: center;
  margin: 8px 0 0 4px;
}

.svg_center {
  display: flex;
  justify-content: center;
  padding: 0 35px 0 0;
}
.line-item-list-footer-add-btn {
  width: 168px !important;
  border-radius: 4px;
  border: solid 1px #38b2c2;
  background-color: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #324056;
  margin: 16px 24px;
}
</style>
