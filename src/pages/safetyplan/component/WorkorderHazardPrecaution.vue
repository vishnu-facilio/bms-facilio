<template>
  <div style="height: 100%;">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else>
      <LineItemList
        ref="lineItemComponent"
        :key="renderKey"
        v-bind="$attrs"
        :config="listConfiguration"
        :moduleDisplayName="moduleDisplayName"
        :moduleName="moduleName"
        :widgetDetails="widgetDetails"
        :viewname="viewname"
        @emptyStateBtn="() => (showMsgPopup = true)"
        style="height:100%;"
      >
        <div class="pL10 pointer marginR-auto" slot="header-additional-actions">
          <FLookupField
            :disabled="$validation.isEmpty(woHazards)"
            :model.sync="selectedHazard"
            :field="hazardField"
            :hideLookupIcon="true"
          />
        </div>
        <template #footer class="line-item-list-footer">
          <el-button
            type="primary"
            class="sf-line-item-list-footer-add-btn"
            @click="() => (showMsgPopup = true)"
          >
            {{ $t('common.safetyPlan.select_precaution') }}
          </el-button>
        </template>
      </LineItemList>
      <el-dialog
        v-if="showMsgPopup"
        :title="$t('common.safetyPlan.select_hazards')"
        :visible.sync="showMsgPopup"
        width="35%"
        :append-to-body="true"
        class="sf-precaution-dialog  agents-dialog fc-dialog-center-container"
      >
        <FLookupField
          :disabled="$validation.isEmpty(woHazards)"
          :model.sync="workorderHazard.id"
          :field="{ ...hazardField, multiple: false }"
          @showLookupWizard="() => (canShowWizard = true)"
          class="mB60"
        />
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel f13"
            @click="() => (showMsgPopup = false)"
            >{{ $t('agent.agent.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save f13"
            @click="() => (isHazardSelected = true)"
            >{{ $t('common._common._save') }}</el-button
          >
        </div>
      </el-dialog>
      <V3LookupFieldWizard
        v-if="canShowWizard || isHazardSelected"
        :canShowLookupWizard.sync="showWizard"
        :selectedLookupField="wizardField"
        @setLookupFieldValue="setLookupFieldValue"
      ></V3LookupFieldWizard>
    </template>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import { API } from '@facilio/api'
import FLookupField from '@/forms/FLookupField'
import { isEmpty } from '@facilio/utils/validation'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'

import { eventBus } from '@/page/widget/utils/eventBus'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

const EQUALS_OPERATOR = 9

export default {
  props: ['widget', 'details'],
  components: { LineItemList, FLookupField, V3LookupFieldWizard },
  data() {
    return {
      woHazards: [],
      isLoading: false,
      selectedHazard: null,
      showMsgPopup: false,
      workorderHazard: {
        hazard: null,
        id: null,
      },
      isHazardSelected: false,
      canShowWizard: false,
    }
  },
  computed: {
    moduleName() {
      return 'workorderHazardPrecaution'
    },
    moduleDisplayName() {
      return 'Precautions of'
    },
    viewname() {
      return 'hidden-all'
    },
    showWizard: {
      set(value) {
        this.isHazardSelected = false
        this.canShowWizard = value
      },
      get() {
        let { canShowWizard, isHazardSelected } = this
        return canShowWizard || isHazardSelected
      },
    },
    wizardField() {
      let params = {
        isDataLoading: false,
        options: [],
        forceFetchAlways: true,
        selectedItems: [],
      }
      if (!this.isHazardSelected)
        return {
          ...params,
          lookupModuleName: 'workorderHazard',
          field: {
            lookupModule: {
              name: 'hazard',
              displayName: 'Hazards',
            },
          },
          multiple: false,

          filters: {
            workorder: {
              operatorId: 36,
              value: [`${this.details.id}`],
            },
          },
        }
      else
        return {
          ...params,
          lookupModuleName: 'precaution',
          field: {
            lookupModule: {
              name: 'precaution',
              displayName: 'Precautions',
            },
          },
          multiple: true,
          additionalParams: {
            excludeAvailableWorkOrderHazardPrecautions: this.workorderHazard
              ?.id,
            workorder: this.details.id,
          },
        }
    },
    filters() {
      let { selectedHazard, details } = this
      let fieldName = 'workorderHazard'
      let filterObj = {
        workorder: {
          operatorId: 36,
          value: [`${details.id}`],
        },
      }
      if (!isEmpty(selectedHazard)) {
        let hazardIds = this.getStringifyHazards(selectedHazard)
        filterObj[fieldName] = { operatorId: 36, value: hazardIds }
      }
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
    perPage() {
      return 7
    },
    deletePopupDetails() {
      let moduleDisplayName = 'Precaution'
      return {
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        message: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
      }
    },
    widgetDetails() {
      let { widget, perPage } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('common.safetyPlan.no_precaution_added', {
        moduleName: 'Precautions',
      })

      return {
        perPage,
        summaryWidgetName,
        emptyStateText,
        emptyStateBtnList: [
          { label: 'Add Precautions', value: 'add_precaution' },
        ],
      }
    },
    listConfiguration() {
      let {
        filters,
        editConfig,
        deleteConfig,
        columnCustomConfig,
        searchAndFilterConfig,
        quickCreateConfig,
        deletePopupDetails,
        getViewDetails,
        getRecordList,
      } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        mainfieldAction: () => {},
        canHideFooter: true,
        deletePopupDetails,
        getViewDetails,
        getRecordList,
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(columnCustomConfig || {}),
        ...(quickCreateConfig || {}),
        ...(searchAndFilterConfig || {}),
        canEnableMainFieldAction: false,
      }
    },
    renderKey() {
      let { selectedHazard } = this
      if (!isEmpty(selectedHazard)) {
        return selectedHazard.length + selectedHazard[0]
      }
      return 1
    },
    hazardField() {
      let field = {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'workorderHazard',
        field: {
          lookupModule: {
            name: 'hazard',
            displayName: 'Hazards',
          },
        },
        multiple: true,
        forceFetchAlways: true,
        placeHolderText: this.$t('common.safetyPlan.all_hazards'),
        selectedItems: [],
        filters: {
          workorder: {
            operatorId: 36,
            value: [`${this.details.id}`],
          },
        },
      }
      return field
    },
  },
  created() {
    this.fetchWorkOrderHazards()
  },
  mounted() {
    eventBus.$on('refresh-sf-precautions', this.refreshList)
  },
  watch: {
    'workorderHazard.id': {
      handler(newVal, oldVal) {
        if (oldVal !== newVal) this.setWorkOrderHazard(newVal)
      },
      immediate: true,
    },
    showMsgPopup: {
      handler(newVal) {
        if (!newVal) this.init()
      },
    },
  },
  methods: {
    init() {
      this.canShowWizard = false
      this.showMsgPopup = false
      this.isHazardSelected = false
      this.selectedHazard = null
      this.workorderHazard = {
        hazard: null,
        id: null,
      }
    },
    async refreshList() {
      this.init()
      await this.fetchWorkOrderHazards()
    },
    setLookupFieldValue(props) {
      let { field } = props || {}
      !this.isHazardSelected
        ? this.setLookupFieldHazardValue(field)
        : this.setLookupFieldPrecautionValue(field)
    },
    setLookupFieldHazardValue(field) {
      let id = this.$getProperty(field, 'selectedItems.0.value', null)
      this.setWorkOrderHazard(id)
    },
    setLookupFieldPrecautionValue(field) {
      let { details, workorderHazard } = this
      let selectedData = this.$getProperty(field, 'selectedItems', [])
      let workorderPrecaution = {
        workorder: { id: details.id },
        workorderHazard: { id: workorderHazard.id },
      }
      let result = selectedData.map(data => {
        return {
          ...workorderPrecaution,
          precaution: {
            id: data?.value,
          },
        }
      })
      this.saveRecord(result)
    },
    async getViewDetails() {
      return await API.get(`v2/views/${this.viewname}`, {
        moduleName: 'hazardPrecaution',
      })
    },
    async getRecordList(extraParams) {
      let filter = {
        workorder: {
          operatorId: EQUALS_OPERATOR,
          value: [`${this.details?.id}`],
        },
      }
      let params = {
        moduleName: 'workorderHazardPrecaution',
        withCount: true,
        filters: filter,
        force: true,
        viewname: 'hidden-all',
        perPage: this.perPage,
        ...extraParams,
      }
      let recordList = (await CustomModuleData.fetchAll(params)) || []
      return recordList.map(data => {
        return new CustomModuleData({
          ...data,
          hazard: data.workorderHazard.hazard,
        })
      })
    },
    async saveRecord(records) {
      this.isLoading = true
      let url = 'v3/modules/data/bulkCreate'
      let params = {
        data: {
          workorderHazardPrecaution: records,
        },
        moduleName: this.moduleName,
        params: {
          return: true,
        },
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error?.message || this.$t('common.safetyPlan.error_occured')
        )
      } else {
        this.$message.success(this.$t('common.safetyPlan.precaution_added'))
      }
      this.init()
      this.isLoading = false
    },
    setWorkOrderHazard(id) {
      let result = (this.woHazards || []).find(hazard => hazard.id === id)
      let hazardId = this.$getProperty(result, 'hazard.id', null)
      this.workorderHazard = {
        hazard: hazardId,
        id,
      }
    },
    getStringifyHazards(selectedHazard) {
      if (!isEmpty(selectedHazard)) {
        return selectedHazard.map(id => {
          return `${id}`
        })
      }
      return null
    },
    async fetchWorkOrderHazards() {
      this.isLoading = true
      let { details } = this
      let { id } = details || {}
      let relatedFieldName = getRelatedFieldName('workorder', 'workorderHazard')
      let relatedConfig = {
        moduleName: 'workorder',
        id,
        relatedModuleName: 'workorderHazard',
        relatedFieldName,
      }
      let params = { viewname: 'hidden-all', perPage: 50 }
      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        params,
        { force: true }
      )

      if (!error) {
        if (!isEmpty(list)) {
          this.woHazards = list
        }
      } else {
        this.$error.message(error.message || 'Error Occured')
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss" scoped>
.sf-line-item-list-footer-add-btn {
  width: 168px !important;
  border-radius: 4px;
  border: solid 1px #38b2c2;
  background-color: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #324056;
  margin: 12px 24px;
}
</style>
