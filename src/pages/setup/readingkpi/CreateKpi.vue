<template>
  <div class="formbuilder-fullscreen-popup sla-policy-container">
    <div class="setting-header">
      <div class="d-flex flex-direction-column">
        <div class="mT10 mB10 f22 fw3 letter-spacing0_5">{{ title }}</div>
      </div>
      <div class="fR stateflow-btn-wrapper">
        <AsyncButton buttonClass="asset-el-btn" :clickAction="goBack">
          {{ $t('common._common.cancel') }}
        </AsyncButton>
        <AsyncButton
          buttonType="primary"
          buttonClass="asset-el-btn"
          :clickAction="save"
        >
          {{ $t('common._common._save') }}
        </AsyncButton>
      </div>
    </div>
    <div class="d-flex setup-grey-bg">
      <div class="sla-sidebar pT10">
        <a
          id="kpidetails-link"
          @click="scrollTo('kpidetails')"
          class="sla-link active"
        >
          {{ $t('kpi.kpi.kpi_details') }}
        </a>
        <a id="kpitype-link" @click="scrollTo('kpitype')" class="sla-link">
          {{ $t('kpi.kpi.kpi_type') }}
        </a>
        <a
          id="formulaBuilder-link"
          @click="scrollTo('formulaBuilder')"
          class="sla-link"
        >
          {{ $t('kpi.kpi.kpi_formula_builder') }}
        </a>
      </div>
      <div class="scroll-container">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <KPIDetails
            id="kpidetails-section"
            ref="kpidetails-section"
            :isNew="!isEditForm"
            @kpiDetailsChange="setProperties"
            :kpiInfo="kpiData"
            class="mB20"
          ></KPIDetails>
          <KPITypeForm
            id="kpitype-section"
            ref="kpitype-section"
            :isNew="!isEditForm"
            @kpiTypeChange="setProperties"
            :kpiInfo="kpiData"
            class="mB20"
          ></KPITypeForm>
          <KPIFormulaBuilder
            id="formulaBuilder-section"
            ref="formulaBuilder-section"
            class="mB20 height74vh"
            :kpiInfo="kpiData"
            :isEditForm="isEditForm"
            @onConditionsChange="setProperties"
          >
          </KPIFormulaBuilder>
        </template>
        <div class="mT50"></div>
      </div>
    </div>
  </div>
</template>

<script>
import AsyncButton from '@/AsyncButton'
import KPIDetails from 'src/pages/setup/readingkpi/KPIDetails.vue'
import KPIFormulaBuilder from 'src/pages/setup/readingkpi/KPIFormulaBuilder.vue'
import KPITypeForm from 'src/pages/setup/readingkpi/KPITypeForm.vue'
import SidebarScrollMixin from 'pages/setup/sla/mixins/SidebarScrollMixin'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { CustomModuleData } from 'pages/custom-module/CustomModuleData'

const liveKpiFrequencies = [
  1,
  2,
  3,
  4,
  5,
  6,
  7,
  8,
  9,
  10,
  11,
  12,
  13,
  14,
  15,
  16,
]

const scheduledKpiFrequencies = [17, 18, 19, 20, 21]

export default {
  components: {
    AsyncButton,
    KPIDetails,
    KPIFormulaBuilder,
    KPITypeForm,
  },
  mounted() {
    this.$nextTick(this.registerScrollHandler)
  },
  mixins: [SidebarScrollMixin],
  async created() {
    let { isEditForm } = this
    if (isEditForm) {
      await this.prefillKpi()
      this.isNew = false
    }
  },
  data() {
    return {
      moduleName: 'readingkpi',
      isLoading: false,
      hasChanged: false,
      kpiData: {},
      rootElementForScroll: '.scroll-container',
      sidebarElements: [
        '#kpidetails-link',
        '#kpitype-link',
        '#formulaBuilder-link',
      ],
      sectionElements: [
        '#kpidetails-section',
        '#kpitype-section',
        '#formulaBuilder-section',
      ],
      isNew: true,
      liveKpiFrequencies: liveKpiFrequencies,
      scheduledKpiFrequencies: scheduledKpiFrequencies,
    }
  },
  computed: {
    title() {
      let { isNew } = this
      let title = isNew
        ? this.$t('kpi.kpi.new_kpi_defintion')
        : this.$t('kpi.kpi.edit_kpi')
      return title
    },
    isEditForm() {
      let id = this.$getProperty(this, '$route.params.id')
      return !isEmpty(id)
    },
    modelDataClass() {
      return CustomModuleData
    },
  },
  methods: {
    async save() {
      let { kpiData } = this
      this.serializeData()
      let isValidKpi = this.validateKpi(kpiData)
      let isValidFormula = this.validateFields(kpiData)
      if (isValidKpi && isValidFormula) {
        let { readingkpi, error } = await this.saveRecord(kpiData)
        if (isEmpty(readingkpi)) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$router.push({ name: 'readingkpi.list' })
        }
      } else {
        if (!isValidKpi) {
          this.$message.error(this.$t('rule.create.fill_mandatory_fields'))
        } else {
          this.$message.error(this.$t('rule.create.variables_must_be_unique'))
        }
      }
    },
    serializeData() {
      let { kpiData } = this
      let { assetCategoryId, resourceType, kpiType, assets } = kpiData || {}
      if (kpiType === 3) this.$set(this.kpiData, 'frequency', -1)
      this.$set(this.kpiData, 'resourceType', resourceType)
      this.$set(this.kpiData, 'assetCategory', {
        id: parseInt(assetCategoryId),
      })
      this.$set(this.kpiData.ns, 'includedAssetIds', assets)
    },
    async saveRecord(serilaizedData) {
      let { moduleName } = this
      let { id } = serilaizedData || {}

      if (isEmpty(id)) {
        return await API.createRecord(moduleName, { data: serilaizedData })
      } else {
        return await API.updateRecord(moduleName, {
          id: id,
          data: serilaizedData,
        })
      }
    },

    validateKpi(kpi) {
      let mandatoryFields = {}
      let {
        name,
        kpiCategory,
        resourceType,
        assetCategory,
        spaceCategory,
        kpiType,
        frequency,
        ns,
      } = kpi || {}
      let { workflowContext } = ns || {}
      let { workflowV2String } = workflowContext || {}

      mandatoryFields = {
        name,
        kpiCategory,
        resourceType,
        kpiType,
        workflowV2String,
      }
      if (kpiType !== 3) {
        mandatoryFields.frequency = frequency
      }
      let validKpi =
        Object.values(mandatoryFields).every(value => !isEmpty(value)) &&
        (!isEmpty(assetCategory) || !isEmpty(spaceCategory))

      return validKpi
    },

    validateFields(kpi) {
      let variables = []
      let isRelatedReadingPresent = false
      let isCurrentAssetAvailable = false
      let { ns } = kpi || {}
      let { fields } = ns || {}

      variables = fields

      const uniqueVariables = [
        ...new Set(variables.map(variable => variable.varName)),
      ]
      const finalReadings = []

      variables.forEach(variable => {
        let { fieldId, resourceId, relMapContext, nsFieldType } = variable || {}
        let { mappingLinkName } = relMapContext || {}

        if (!isEmpty(fieldId)) {
          finalReadings.push(fieldId)
        }
        let assetId = resourceId
        if (
          (assetId === -1 && nsFieldType === 'ASSET_READING') ||
          !isEmpty(mappingLinkName)
        ) {
          isCurrentAssetAvailable = isCurrentAssetAvailable || true
        }
        if (nsFieldType === 'RELATED_READING') {
          isRelatedReadingPresent = true
        }
      })

      if (
        uniqueVariables.length === variables.length &&
        finalReadings.length === variables.length &&
        (isCurrentAssetAvailable || isRelatedReadingPresent)
      ) {
        return true
      }

      return false
    },

    goBack() {
      this.$emit('closeEditForm', true)
      this.$router.push({
        name: 'readingkpi.list',
      })
    },
    setProperties(value) {
      this.kpiData = { ...this.kpiData, ...value }
    },
    async prefillKpi() {
      this.isLoading = true
      let id = this.$getProperty(this, '$route.params.id')
      await this.loadSummary(id)
      this.isLoading = false
    },

    async loadSummary(id, force = true) {
      let { moduleName } = this
      let { readingkpi } = await API.fetchRecord(
        moduleName,
        {
          id: id,
        },
        { force }
      )
      this.$set(this, 'kpiData', readingkpi)
    },
  },
}
</script>

<style lang="scss">
.sla-policy-container {
  border-left: 1px solid #e3e7ed;
  margin-left: 60px;
  margin-top: 50px;

  .setting-header {
    box-shadow: none;
    border-bottom: 1px solid #e3e7ed;
  }

  .sla-sidebar {
    background-color: #fff;
    min-width: 300px;
    height: 100vh;
    margin-right: 20px;
  }

  .scroll-container {
    flex-grow: 1;
    margin: 20px 20px 0 0;
    overflow-y: scroll;
    max-height: calc(100vh - 150px);
    position: relative;

    > * {
      background-color: #fff;
    }
  }

  .asset-el-btn {
    height: 40px !important;
    line-height: 1;
    display: inline-block;
    letter-spacing: 0.7px !important;
    border-radius: 3px;
  }

  .section-header {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1.6px;
    color: var(--fc-theme-color);
    text-transform: uppercase;
    margin: 0;
    padding: 28px 50px 20px;

    &.anchor-top {
      position: sticky;
      top: 0;
      width: 100%;
      background: #fff;
      z-index: 2;
      box-shadow: 0 2px 3px 0 rgba(233, 233, 226, 0.5);
    }
  }

  .sla-link {
    display: block;
    position: relative;
    padding: 11px 0px 11px 40px;
    margin: 0;
    color: #555;
    font-size: 14px;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;

    &.active {
      background: #f3f4f7;
    }
  }

  .el-form {
    width: 95%;
    max-width: 998px;
    padding-right: 20%;
  }

  .el-table::before {
    background: initial;
  }
  .el-table th .cell {
    letter-spacing: 1.6px;
    color: #385571;
    padding-left: 0;
  }
  .el-table--border td:first-child .cell {
    padding-left: initial;
  }
  .el-table__empty-block {
    border-bottom: 1px solid #ebeef5;
  }
  .el-table__row .actions {
    visibility: hidden;
  }
  .el-table__row:hover .actions {
    visibility: visible;
  }

  .sla-criteria .fc-modal-sub-title {
    color: #385571;
  }

  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid #39b2c2;
    background-color: #f7feff;
    min-height: 36px;
    &:hover {
      border: 1px solid #39b2c2;
      background-color: #f7feff;
    }
    .btn-label {
      font-size: 12px;
      font-weight: 500;
      color: #39b2c2;
      letter-spacing: 0.5px;
    }
    img {
      width: 9px;
    }
  }
  .disable-save {
    pointer-events: none;
    opacity: 0.4;
  }
  .height74vh {
    min-height: 74vh;
  }
}
</style>
