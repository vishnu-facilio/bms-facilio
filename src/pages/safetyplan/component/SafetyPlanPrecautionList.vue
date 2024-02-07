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
        viewname="hidden-all"
        style="height:95%;"
      >
        <div class="pL10 pointer marginR-auto" slot="header-additional-actions">
          <FLookupField
            :disabled="$validation.isEmpty(hazardIds)"
            :model.sync="selectedHazard"
            :field="hazardField"
            :hideLookupIcon="true"
          />
        </div>
      </LineItemList>
    </template>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemListOld.vue'
import { API } from '@facilio/api'
import FLookupField from '@/forms/FLookupField'
import { isEmpty } from '@facilio/utils/validation'

import { eventBus } from '@/page/widget/utils/eventBus'

const EQUALS_OPERATOR = 9

export default {
  props: ['widget', 'details'],
  components: { LineItemList, FLookupField },
  data() {
    return {
      hazardIds: [],
      isLoading: false,
      selectedHazard: null,
    }
  },
  computed: {
    moduleName() {
      return 'hazardPrecaution'
    },
    moduleDisplayName() {
      return 'Precautions of'
    },
    filters() {
      let { widget, hazardIds, selectedHazard } = this
      let { relatedList } = widget || {}
      let { field } = relatedList || {}
      let fieldName = (field || {}).name || 'hazard'
      let filterObj = {}
      if (!isEmpty(selectedHazard)) {
        hazardIds = this.getStringifyHazards(selectedHazard)
      }
      if (!isEmpty(hazardIds)) {
        filterObj[fieldName] = { operatorId: 36, value: hazardIds }
      } else {
        filterObj[fieldName] = { operatorId: 36, value: ['0'] }
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
        canHideDelete: true,
      }
    },
    quickCreateConfig() {
      return {
        canHideAddBtn: true,
      }
    },
    widgetDetails() {
      let { moduleDisplayName } = this
      let emptyStateText = this.$t('common.safetyPlan.no_precaution_added', {
        moduleName: moduleDisplayName,
      })

      return {
        perPage: 7,
        emptyStateText,
        emptyStateBtnList: [],
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
        ...(columnCustomConfig || {}),
        ...(quickCreateConfig || {}),
        ...(searchAndFilterConfig || {}),
        canEnableMainFieldAction: false,
      }
    },
    renderKey() {
      let { selectedHazard } = this
      if (!isEmpty(selectedHazard) && Array.isArray(selectedHazard)) {
        return selectedHazard.length + selectedHazard[0]
      }
      return 1
    },
    hazardField() {
      let { hazardIds } = this
      let field = {
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
        placeHolderText: this.$t('common.safetyPlan.all_hazards'),
        selectedItems: [],
      }
      if (!isEmpty(hazardIds)) {
        field['filters'] = {
          id: { operatorId: 36, value: hazardIds },
        }
      }
      return field
    },
  },
  created() {
    this.fetchSafetyPlanHazards()
  },
  mounted() {
    eventBus.$on('refresh-sf-precautions', this.refreshList)
  },
  methods: {
    init() {
      this.hazardIds = []
      this.selectedHazard = null
    },
    async refreshList() {
      this.init()
      await this.fetchSafetyPlanHazards()
    },
    getStringifyHazards(selectedHazard) {
      if (!isEmpty(selectedHazard)) {
        let hazards = selectedHazard.map(id => {
          return `${id}`
        })
        return hazards
      }
      return null
    },
    getParentModuleName() {
      let pageModuleName = this.$attrs.moduleName
      let baseSpaces = ['site', 'building', 'floor', 'space']
      if (baseSpaces.includes(pageModuleName)) {
        return 'space'
      } else {
        return pageModuleName
      }
    },
    async fetchSafetyPlanHazards() {
      this.isLoading = true
      let parentModuleName = this.getParentModuleName()
      let filter = {
        [`${parentModuleName}`]: {
          operatorId: EQUALS_OPERATOR,
          value: [`${this.details?.id}`],
        },
      }
      let params = {
        withCount: true,
        filters: JSON.stringify(filter),
      }
      let { list, error } = await API.fetchAll(
        `${parentModuleName}Hazard`,
        params,
        {
          force: true,
        }
      )
      if (!error && list?.length > 0) {
        this.hazardIds = (list || [])
          .filter(data => {
            let { hazard } = data || {}
            let { id } = hazard || {}
            return !isEmpty(id)
          })
          .map(data => {
            let { hazard } = data || {}
            let { id } = hazard || {}
            return `${id}`
          })
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss" scoped>
.line-item-list {
  .line-item-list-header-container {
    .line-item-list-header-title {
      font-size: 17px;
      font-weight: 500;
      color: #324056;
      margin-right: 0 !important;
    }
  }
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
</style>
