<template>
  <div class="f-webform-container pm-scope-creation">
    <el-form
      :rules="rules"
      ref="scope-form"
      :model="scopeObj"
      :label-position="labelPosition"
    >
      <div class="form-one-column">
        <el-form-item
          prop="name"
          :label="$t('maintenance.pm_list.name')"
          class="section-items"
          :required="true"
        >
          <el-input
            class="fc-input-full-border2"
            v-model="scopeObj.name"
            :placeholder="$t('maintenance.pm.name_placeholder')"
          ></el-input>
        </el-form-item>
      </div>
      <div class="form-one-column">
        <el-form-item
          prop="sites"
          :label="$t('maintenance._workorder.site')"
          class="section-items"
          :required="true"
        >
          <FLookupField
            :field="fields.site"
            :model="scopeObj.sites"
            :fetchOptionsOnLoad="true"
            :canShowLookupWizard="showLookupFieldWizard"
            @showLookupWizard="showLookupWizard"
            @recordSelected="setSiteValue"
          />
        </el-form-item>
      </div>
      <div class="form-one-column">
        <el-form-item
          prop="assignmentTypeEnum"
          :label="$t('maintenance.pm.category')"
          class="section-items"
          :required="true"
        >
          <AssignmentTypeField
            v-model="scopeObj.assignmentTypeEnum"
            :options="categoryOptions"
            :disabled="isEdit"
            @onScopeChange="toggleCategoryLookup"
          />
        </el-form-item>
      </div>
      <div class="form-one-column">
        <el-form-item
          v-if="showCategoryLookup"
          :prop="categoryModuleName"
          :label="$t(categoryFieldLabel)"
          class="section-items"
          :required="true"
        >
          <FLookupField
            :canShowLookupWizard="false"
            :field="fields[categoryModuleName]"
            :model.sync="scopeObj[categoryModuleName]"
            :fetchOptionsOnLoad="true"
            :disabled="isEdit"
            @setLookupFieldValue="setCategoryFieldValue"
          />
        </el-form-item>
      </div>
      <div class="form-one-column d-flex mT25" v-if="false">
        <el-checkbox
          v-model="scopeObj.enableLeadTime"
          class="scope-border mT10"
        >
          {{ $t('maintenance.pm.lead_time') }}
        </el-checkbox>
        <div class="d-flex mL30">
          <div class="d-flex">
            <span class="mT12">{{ $t('maintenance.pm.days') }}</span>
            <el-input
              type="number"
              v-model="scopeObj.day"
              class="fc-input-full-border2 lead-time width50px mL10"
            />
          </div>
          <div class="d-flex mL15">
            <span class="mT12">{{ $t('maintenance.pm.hrs') }}</span>
            <el-input
              type="number"
              v-model="scopeObj.hour"
              class="fc-input-full-border2 lead-time width50px mL10"
            />
          </div>
        </div>
      </div>
      <div class="form-one-column d-flex mT20" v-if="false">
        <el-checkbox v-model="scopeObj.skipWo" class="scope-border">
          {{ $t('maintenance.pm.skip_wo') }}
        </el-checkbox>
      </div>
      <FLookupFieldWizard
        v-if="showLookupFieldWizard"
        :canShowLookupWizard.sync="showLookupFieldWizard"
        :selectedLookupField="fields.site"
        :withReadings="true"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </el-form>
  </div>
</template>

<script>
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { isEmpty, isObject } from '@facilio/utils/validation'
import AssignmentTypeField from './AssignmentTypeField'

export default {
  props: [
    'labelPosition',
    'scopeModel',
    'isScopeEdited',
    'isEdit',
    'deletedSiteObj',
  ],
  components: {
    FLookupField,
    FLookupFieldWizard,
    AssignmentTypeField,
  },
  created() {
    this.init()
  },
  mounted() {
    this.$nextTick(() => {
      let formComponent = this.$refs['scope-form']
      formComponent.clearValidate()
    })
  },
  data() {
    return {
      rules: {
        name: [
          {
            required: true,
            message: this.$t('maintenance.pm.name_required'),
            trigger: 'change',
          },
        ],
        assignmentTypeEnum: [
          {
            required: true,
            message: this.$t('common._common.please_select_category'),
            trigger: 'change',
          },
        ],
        sites: [
          {
            required: true,
            message: this.$t('maintenance.pm.site_required'),
            trigger: 'blur',
          },
        ],
      },
      scopeObj: {
        name: '',
        sites: [],
        assignmentTypeEnum: null,
        assetCategory: null,
        spaceCategory: null,
        enableLeadTime: false,
        day: 0,
        hour: 0,
        skipWo: false,
      },
      fields: {
        site: {
          isDataLoading: false,
          options: [],
          lookupModuleName: 'site',
          field: {
            lookupModule: {
              name: 'site',
              displayName: 'Site',
            },
          },
          multiple: true,
          forceFetchAlways: true,
          filters: {},
          isDisabled: false,
        },
        assetCategory: {
          lookupModuleName: 'assetCategory',
          config: { canShowLookupWizard: false },
        },
        spaceCategory: {
          lookupModuleName: 'spaceCategory',
          config: { canShowLookupWizard: false },
        },
      },
      showCategoryLookup: false,
      showLookupFieldWizard: false,
      selectedLookupField: {},
      categoryOptions: [
        { label: 'Assets', value: 'ASSETS' },
        { label: 'Spaces', value: 'SPACES' },
        { label: 'Asset Category', value: 'ASSETCATEGORY' },
        { label: 'Space Category', value: 'SPACECATEGORY' },
        { label: 'Buildings', value: 'BUILDINGS' },
        { label: 'Sites', value: 'SITES' },
        { label: 'Floors', value: 'FLOORS' },
      ],
      initScope: null,
    }
  },
  computed: {
    categoryModuleName() {
      let moduleName
      let { scopeObj } = this || {}
      let { assignmentTypeEnum } = scopeObj || {}

      if (assignmentTypeEnum === 'SPACECATEGORY') {
        moduleName = 'spaceCategory'
      } else if (assignmentTypeEnum === 'ASSETCATEGORY') {
        moduleName = 'assetCategory'
      }
      return moduleName
    },
    isCategorySelected() {
      let { scopeObj } = this || {}
      let { assignmentTypeEnum } = scopeObj || {}

      return ['SPACECATEGORY', 'ASSETCATEGORY'].includes(assignmentTypeEnum)
    },
    categoryFieldLabel() {
      let { scopeObj } = this || {}
      let { assignmentTypeEnum } = scopeObj || {}

      if (assignmentTypeEnum === 'SPACECATEGORY') {
        return 'maintenance.pm_list.space_category'
      } else {
        return 'maintenance.pm_list.asset_category'
      }
    },
  },
  watch: {
    scopeObj: {
      handler(value) {
        let deserializedData = {}
        let { sites, assetCategory, spaceCategory, day, hour } = value
        sites = sites.map(currSite => ({ id: currSite }))
        deserializedData = {
          ...value,
          sites,
          assetCategory: { id: assetCategory },
          spaceCategory: { id: spaceCategory },
          day: !isEmpty(day) ? Number(day) : 0,
          hour: !isEmpty(hour) ? Number(hour) : 0,
        }
        Object.entries(deserializedData).forEach(([key, value]) => {
          if (isObject(value)) {
            let { id } = deserializedData[key] || {}
            if (isEmpty(id)) {
              delete deserializedData[key]
            }
          } else if (isEmpty(value)) {
            delete deserializedData[key]
          }
        })
        this.$emit('onScopeChange', deserializedData)
      },
      deep: true,
    },
    deletedSiteObj: {
      async handler(newVal) {
        let { canRevert, deletedSites } = newVal || {}
        if (canRevert) {
          let { scopeObj, fields } = this
          let { site } = fields || {}
          let { selectedItems = [] } = site || {}
          let { sites = [] } = scopeObj || {}
          let revertedItems = deletedSites.map(value => {
            return { value }
          })

          if (!isEmpty(selectedItems)) {
            selectedItems = [...selectedItems, ...revertedItems]
          }
          if (!isEmpty(sites)) {
            sites = [...sites, ...deletedSites]
          }

          this.$set(this.scopeObj, 'sites', sites)
          this.$set(site, 'selectedItems', selectedItems)
          this.$set(this.fields, 'site', site)
          this.$set(this.selectedLookupField, 'selectedItems', selectedItems)
          this.$emit('update:deletedSiteObj', {
            deletedSiteCount: 0,
            deletedSites: [],
            canRevert: false,
          })
        }
      },
      deep: true,
    },
  },
  methods: {
    init() {
      let { scopeModel, scopeObj } = this || {}
      if (!isEmpty(scopeModel)) {
        let {
          assignmentTypeEnum,
          sites,
          name,
          day,
          hour,
          enableLeadTime,
          skipWo,
        } = scopeModel
        this.initScope = assignmentTypeEnum
        sites = (sites || []).map(site => {
          let { id } = site || {}
          return id
        })
        if (assignmentTypeEnum === 'SPACECATEGORY') {
          let spaceCategory = this.$getProperty(scopeModel, 'spaceCategory.id')
          scopeObj = { ...scopeObj, spaceCategory }
          this.showCategoryLookup = true
        } else if (assignmentTypeEnum === 'ASSETCATEGORY') {
          let assetCategory = this.$getProperty(scopeModel, 'assetCategory.id')
          scopeObj = { ...scopeObj, assetCategory }
          this.showCategoryLookup = true
        }
        this.scopeObj = {
          ...scopeObj,
          assignmentTypeEnum,
          sites,
          name,
          day,
          hour,
          enableLeadTime,
          skipWo,
        }
      }
    },
    showLookupWizard(field, canShow) {
      this.selectedLookupField = field
      this.showLookupFieldWizard = canShow
    },
    setCategoryFieldValue(value) {
      let { categoryModuleName } = this || {}
      let { field } = value
      let { selectedItems } = field
      this.scopeObj[categoryModuleName] = { id: selectedItems[0] }
    },
    updateSites(newSites = [], oldSites = []) {
      let { isEdit } = this
      let deletedSiteCount = 0
      let deletedSites = []

      if (isEdit) {
        oldSites.forEach(site => {
          if (!newSites.includes(site)) {
            deletedSiteCount += 1
            deletedSites.push(site)
          }
        })
      }
      this.$emit('update:deletedSiteObj', {
        deletedSiteCount,
        deletedSites,
        canRevert: false,
      })
      this.$set(this.scopeObj, 'sites', newSites)
    },
    setSiteValue(currentSites) {
      let { scopeObj } = this
      let { sites: previousSites } = scopeObj || {}

      currentSites = (currentSites || []).map(site => {
        let { value } = site || {}
        return value
      })
      this.updateSites(currentSites, previousSites)
    },
    setLookupFieldValue(value) {
      let { scopeObj } = this
      let { sites } = scopeObj || {}
      let { field } = value
      let { selectedItems, options } = field
      let selectedItemIds = selectedItems.map(item => item.value)

      this.updateSites(selectedItemIds, sites)
      if (!isEmpty(options)) {
        let ids = options.map(item => item.value)
        let newOptions = selectedItems.filter(item => !ids.includes(item.value))
        options.unshift(...newOptions)
      } else {
        options = [...selectedItems]
      }
      this.$set(this.selectedLookupField, 'options', options)
      this.showLookupFieldWizard = false
    },

    async toggleCategoryLookup() {
      let { isCategorySelected, scopeObj, isEdit, initScope } = this || {}
      let { assignmentTypeEnum } = scopeObj || {}
      // During edit and if old scope is not as new then show prompt
      if (isEdit && initScope !== assignmentTypeEnum) {
        this.$emit('update:isScopeEdited', true)
      } else if (isEdit && initScope === assignmentTypeEnum) {
        this.$emit('update:isScopeEdited', false)
      }
      if (isCategorySelected) {
        this.showCategoryLookup = false
        this.$nextTick(() => {
          this.showCategoryLookup = true
        })
      } else {
        this.showCategoryLookup = false
        this.scopeObj = {
          ...scopeObj,
          assetCategory: null,
          spaceCategory: null,
        }
      }
    },
    validate() {
      return new Promise(resolve => {
        this.$refs['scope-form'].validate(valid => {
          if (valid) {
            resolve(true)
          } else {
            resolve(false)
          }
        })
      })
    },
  },
}
</script>

<style lang="scss">
.icon-class-alignment {
  .inline {
    display: flex;
  }
}
.pm-scope-creation {
  .scope-border {
    .el-checkbox__input.is-checked + .el-checkbox__label,
    .el-checkbox__label {
      font-size: 14px !important;
      font-weight: normal !important;
      letter-spacing: 0.5px !important;
      color: #324056;
    }
    .el-radio__inner,
    .el-checkbox__inner {
      border: 1px solid #39b2c2 !important;
    }
    .lead-time {
      .el-input__inner {
        height: 30px !important;
      }
    }
  }
  .el-form-item__label {
    font-size: 13px;
  }
}
</style>
