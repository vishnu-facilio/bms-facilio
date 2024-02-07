<template>
  <FContainer class="form-style">
    <ConnectedForm
      v-if="!isLoading"
      :renderDetails="formObj"
      @closeForm="redirectToList"
    >
      <template #save-action>
        <FButton
          :disabled="isNotAllowSave"
          :loading="isSaving"
          appearance="primary"
          size="medium"
          @click="saveRecord"
          >{{ 'Save & Close' }}</FButton
        >
      </template>
      <template #1-content>
        <ControlDetailsActionsForm
          :basicDetail="basicDetails"
          @onDataChange="onDataChange"
        />
      </template>
      <template #2-content>
        <FContainer class="criteria-parent">
          <FContainer class="criteria-list-container">
            <FContainer>
              <CritriaListComponent :renderDetails="assetRenderDetails" />
            </FContainer>
            <FContainer class="mT15">
              <CritriaListComponent :renderDetails="controllerRenderDetails" />
            </FContainer>
          </FContainer>
        </FContainer>
      </template>
      <template #3-content>
        <FContainer class="criteria-parent">
          <FContainer class="criteria-list-container">
            <FContainer>
              <CritriaListComponent :renderDetails="siteRenderDetails" />
            </FContainer>
          </FContainer>
        </FContainer>
      </template>
    </ConnectedForm>
  </FContainer>
</template>
<script>
import { FContainer, FButton, FIcon } from '@facilio/design-system'
import { ConnectedForm } from '@facilio/ui/new-forms'
import ControlDetailsActionsForm from '../../components/ControlAction/ControlDetailsActionsForm.vue'
import CritriaListComponent from '../../components/ControlAction/CritriaListComponent.vue'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  data() {
    return {
      basicDetails: null,
      isSaving: false,
      formObj: {
        tabsList: [
          { label: 'Control details & action', value: '1' },
          { label: 'Asset and controller criteria', value: '2' },
          { label: 'Site criteria', value: '3' },
        ],
        headerObject: {
          title: 'New Control Action',
          description:
            'Establish a new control action for adjusting asset readings.',
          linearGradientObject: {
            direction: '344deg',
            startColor: '#311E77',
            endColor: '#7D63DC',
          },
        },
        isForm: true,
      },
      isLoading: false,
      moduleName: 'controlAction',
      assetRenderDetails: {
        headerName: 'Asset Criteria',
        moduleName: 'asset',
        infoBanner: true,
        criteria: null,
      },
      siteRenderDetails: {
        headerName: 'Site Criteria',
        moduleName: 'site',
        infoBanner: true,
        criteria: null,
      },
      controllerRenderDetails: {
        headerName: 'Controller Criteria',
        moduleName: 'controllers',
        infoBanner: true,
        criteria: null,
      },
    }
  },
  created() {
    let { $attrs } = this
    let { id } = $attrs || {}
    if (!isEmpty(id)) {
      this.loadFormData(id)
    }
  },
  components: {
    FContainer,
    CritriaListComponent,
    FButton,
    ConnectedForm,
    ControlDetailsActionsForm,
    FIcon,
  },
  computed: {
    isNotAllowSave() {
      let { basicDetails } = this
      let { name, controlActionType, assetCategory } = basicDetails || {}
      return (
        isEmpty(name) || isEmpty(controlActionType) || isEmpty(assetCategory)
      )
    },
  },
  methods: {
    onDataChange(updatedDetails) {
      this.basicDetails = cloneDeep(updatedDetails)
    },
    async loadFormData(id) {
      this.isLoading = true
      let { moduleName, basicDetails } = this
      let { error, [moduleName]: record } = await API.fetchRecord(moduleName, {
        id,
      })
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { assetCriteria, siteCriteria, controllerCriteria, assetCategory } =
          record || {}
        let { conditions: assetConditions, pattern: assetPattern } =
          assetCriteria || {}
        let { conditions: siteConditions, pattern: sitePattern } =
          siteCriteria || {}
        let { conditions: controllerConditions, pattern: controllerPattern } =
          controllerCriteria || {}
        let { id: categoryId } = assetCategory || {}
        this.basicDetails = {
          ...basicDetails,
          ...record,
          assetCategory: categoryId,
        }

        this.assetRenderDetails.criteria =
          !isEmpty(assetConditions) && !isEmpty(assetPattern)
            ? {
                conditions: assetConditions,
                pattern: assetPattern,
              }
            : null
        this.controllerRenderDetails.criteria =
          !isEmpty(controllerConditions) && !isEmpty(controllerPattern)
            ? {
                conditions: controllerConditions,
                pattern: controllerPattern,
              }
            : null
        this.siteRenderDetails.criteria =
          !isEmpty(siteConditions) && !isEmpty(sitePattern)
            ? {
                conditions: siteConditions,
                pattern: sitePattern,
              }
            : null
      }
      this.isLoading = false
    },
    serializedData() {
      let {
        basicDetails,
        assetRenderDetails,
        siteRenderDetails,
        controllerRenderDetails,
      } = this
      let {
        assetCategory,
        siteCriteriaId,
        assetCriteriaId,
        controllerCriteriaId,
      } = basicDetails || {}
      assetCategory = { id: assetCategory }
      let { criteria: assetCriteria } = assetRenderDetails || {}
      let { criteria: siteCriteria } = siteRenderDetails || {}
      let { criteria: controllerCriteria } = controllerRenderDetails || {}
      if (!isEmpty(siteCriteriaId)) {
        delete basicDetails['siteCriteriaId']
      }
      if (!isEmpty(assetCriteriaId)) {
        delete basicDetails['assetCriteriaId']
      }
      if (!isEmpty(controllerCriteriaId)) {
        delete basicDetails['controllerCriteriaId']
      }
      return {
        ...basicDetails,
        controlActionSourceType: 1,
        assetCategory,
        assetCriteria,
        siteCriteria,
        controllerCriteria,
      }
    },
    async saveRecord() {
      this.isSaving = true
      let { $attrs, moduleName } = this
      let { id } = $attrs || {}
      let data = this.serializedData()
      let response = {}
      let msg = ''
      if (isEmpty(id)) {
        response = await API.createRecord(moduleName, {
          data,
        })
        msg = `Control action created successfully!`
      } else {
        response = await API.updateRecord(moduleName, {
          id,
          data,
        })
        msg = 'Control action updated successfully!'
      }

      let { error, [this.moduleName]: record } = response || {}
      if (!error) {
        let { id: recordId } = record || {}
        this.$message.success(msg)
        this.redirectToSummary(recordId)
      } else {
        this.$message.error(error)
      }
      this.isSaving = false
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled() && !isEmpty(id)) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: { viewname: 'all', id },
          })
      }
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.form-style {
  margin: 21px 24px 0px;
}
.criteria-parent {
  display: flex;
  justify-content: center;
  margin-top: 15px;
  width: 100%;
}
.criteria-list-container {
  display: flex;
  flex-direction: column;
  width: 60%;
}
</style>
