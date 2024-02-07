<template>
  <div>
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div style="height:100%;" v-else>
      <LineItemList
        v-bind="$attrs"
        :config="listConfiguration"
        :moduleDisplayName="moduleDisplayName"
        :moduleName="moduleName"
        :widgetDetails="widgetDetails"
        viewname="hidden-all"
        @emptyStateBtn="showWizard"
        style="height:100%;"
      >
        <template #footer class="line-item-list-footer">
          <el-button
            type="primary"
            class="line-item-list-footer-add-btn"
            @click="() => (canShowWizard = true)"
          >
            {{ $t('common.safetyPlan.select_precaution') }}
          </el-button>
        </template>
      </LineItemList>
      <V3LookupFieldWizard
        v-if="canShowWizard"
        :canShowLookupWizard.sync="canShowWizard"
        :selectedLookupField="wizardField"
        @setLookupFieldValue="saveRecord"
      ></V3LookupFieldWizard>
    </div>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { API } from '@facilio/api'
import { isEmpty } from '../../../util/validation'

export default {
  props: ['widget', 'details'],
  components: { LineItemList, V3LookupFieldWizard },
  data() {
    return {
      canShowWizard: false,
      isLoading: false,
      wizardField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'precaution',
        field: {
          lookupModule: {
            name: 'precaution',
            displayName: 'Precautions',
          },
        },
        multiple: true,
        forceFetchAlways: true,
        selectedItems: [],
        additionalParams: {
          excludeAvailablePrecautions: this.details?.id,
        },
      },
    }
  },
  computed: {
    moduleName() {
      return 'hazardPrecaution'
    },
    moduleDisplayName() {
      return 'Associated Precautions'
    },
    filters() {
      let { details, widget } = this
      let { id } = details
      let { relatedList } = widget || {}
      let { field } = relatedList || {}
      let fieldName = (field || {}).name || 'hazard'
      let filterObj = {}

      filterObj[fieldName] = { operatorId: 36, value: [`${id}`] }
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
    columnCustomConfig() {
      return { canShowColumnConfig: false }
    },
    quickCreateConfig() {
      return {
        canHideAddBtn: true,
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
      } = this

      return {
        expand: false,
        isSelectionEnabled: false,
        isIndexEnabled: false,
        filters,
        canHideFooter: true,
        deletePopupDetails,
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
      let emptyStateText = this.$t('common.safetyPlan.no_precaution_added', {
        moduleName: moduleDisplayName,
      })

      return {
        perPage: 7,
        summaryWidgetName,
        emptyStateText,
        emptyStateBtnList: [
          { label: 'Add Precautions', value: 'select_precaution' },
        ],
      }
    },
    searchAndFilterConfig() {
      return { canHideSearch: false, canHideFilter: false }
    },
  },
  methods: {
    async showWizard() {
      this.canShowWizard = true
    },
    async saveRecord(value) {
      this.isLoading = true
      let { details } = this
      let { id } = details || {}
      let url = 'v3/modules/data/bulkCreate'
      let field = this.$getProperty(value, 'field')
      let selectedData = this.$getProperty(field, 'selectedItems')
      if (!isEmpty(selectedData)) {
        let records = selectedData.map(record => {
          let { value } = record
          return { hazard: { id }, precaution: { id: value } }
        })
        let params = {
          data: {
            hazardPrecaution: records,
          },
          moduleName: 'hazardPrecaution',
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
  margin: 16px 24px;
}
</style>
