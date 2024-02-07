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
        :resizeWidget="resizeWidget"
        viewname="hidden-all"
        @onDelete="refreshPrecautions"
        @emptyStateBtn="showWizard"
        style="height:100%"
      >
        <template #footer class="line-item-list-footer">
          <el-button
            type="primary"
            class="line-item-list-footer-add-btn"
            @click="showWizard"
          >
            {{ $t('common.safetyPlan.select_hazards') }}
          </el-button>
        </template>
      </LineItemList>
      <V3LookupFieldWizard
        v-if="canShowWizard"
        :canShowLookupWizard.sync="canShowWizard"
        :selectedLookupField="wizardField"
        @setLookupFieldValue="saveRecord"
      ></V3LookupFieldWizard>
    </template>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['widget', 'details', 'resizeWidget'],
  components: { LineItemList, V3LookupFieldWizard },
  data() {
    return {
      canShowWizard: false,
      isLoading: false,
      parentModuleName: null,
    }
  },
  computed: {
    moduleName() {
      let { parentModuleName } = this
      return parentModuleName + 'Hazard'
    },
    moduleDisplayName() {
      return 'Associated Hazards'
    },
    filters() {
      let { details, parentModuleName } = this
      let { id } = details
      let filterObj = {}
      if (!isEmpty(parentModuleName))
        filterObj[parentModuleName] = { operatorId: 36, value: [`${id}`] }
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
    deletePopupDetails() {
      let moduleDisplayName = 'Hazard'
      return {
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        message: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
      }
    },
    quickCreateConfig() {
      return {
        canHideAddBtn: true,
      }
    },
    columnCustomConfig() {
      return { canShowColumnConfig: false }
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
      } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        canHideFooter: true,
        deletePopupDetails,
        mainfieldAction: () => {},
        ...(editConfig || {}),
        ...(deleteConfig || {}),
        ...(columnCustomConfig || {}),
        ...(quickCreateConfig || {}),
        ...(searchAndFilterConfig || {}),
        canEnableMainFieldAction: false,
      }
    },
    widgetDetails() {
      let { widget, moduleDisplayName } = this
      let { relatedList } = widget || {}
      let { summaryWidgetName } = relatedList || {}
      let emptyStateText = this.$t('common.safetyPlan.no_hazard_added', {
        moduleName: moduleDisplayName,
      })

      return {
        perPage: 7,
        summaryWidgetName,
        emptyStateText,
        emptyStateBtnList: [{ label: 'Add Hazards', value: 'add_hazard' }],
      }
    },
    searchAndFilterConfig() {
      return { canHideSearch: false, canHideFilter: false }
    },
    wizardField() {
      return {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'hazard',
        field: {
          lookupModule: {
            name: 'hazard',
            displayName: 'Hazards',
          },
        },
        multiple: true,
        forceFetchAlways: true,
        selectedItems: [],
        additionalParams: {
          excludeAvailableHazards: this.details?.id,
          parentModuleName: this.parentModuleName,
        },
      }
    },
  },
  created() {
    this.getParentModuleName()
  },
  mounted() {
    eventBus.$on('refresh-hazards', this.refreshList)
  },
  methods: {
    async showWizard() {
      this.canShowWizard = true
    },
    async refreshList() {
      let component = this.$refs['lineItemComponent']
      if (component) {
        component.refreshRecordList(true)
      }
      this.refreshPrecautions()
    },
    refreshPrecautions() {
      eventBus.$emit('refresh-sf-precautions')
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
    async saveRecord(value) {
      this.isLoading = true
      let { details, parentModuleName } = this
      let { id } = details || {}
      let url = 'v3/modules/data/bulkCreate'
      let field = this.$getProperty(value, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      let successMsg = `Hazards added successfully!`
      if (!isEmpty(selectedData)) {
        let records = selectedData.map(record => {
          let { value } = record
          let data = {
            [parentModuleName]: {
              id,
            },
          }
          return { data, hazard: { id: value } }
        })
        let moduleName = `${parentModuleName}Hazard`
        let params = {
          moduleName,
          params: {
            [parentModuleName]: id,
          },
          data: {
            [moduleName]: records,
          },
        }
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error?.message || this.$t('common.safetyPlan.error_occured')
          )
        } else {
          successMsg && this.$message.success(successMsg)
          this.refreshPrecautions()
        }
      } else {
        this.$message.error(this.$t('common.safetyPlan.error_occured'))
      }
      this.closeWizard()
      this.isLoading = false
    },
    closeWizard() {
      this.$set(this.wizardField, 'selectedItems', [])
      this.canShowWizard = false
    },
  },
}
</script>

<style lang="scss" scoped>
.line-item-list-footer-add-btn {
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
