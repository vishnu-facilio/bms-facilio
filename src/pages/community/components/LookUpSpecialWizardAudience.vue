<script>
import Wizard from 'src/components/FLookupFieldWizard.vue'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'

export default {
  extends: Wizard,

  data() {
    return {
      peopleType: null,
      contactFilters: [],
      tenantContactFields: [],
      vendorContactFields: [],
      buildingFields: [],
    }
  },
  props: ['selectedField'],
  created() {
    this.loadTenantContactFields()
    this.loadVendorContactFields()
  },
  computed: {
    viewColumns() {
      let columns = this.buildingFields
      let viewObject = this.viewDetail
        ? this.viewDetail
        : this.$store.state.view.currentViewDetail

      let { selectedField } = this
      let { lookupModuleName } = selectedField || {}
      if (
        viewObject &&
        viewObject.fields &&
        (lookupModuleName === 'tenantunit' ||
          lookupModuleName === 'people' ||
          lookupModuleName === 'building')
      ) {
        columns = viewObject.fields.map(vf => {
          vf.name = (vf.field || {}).name || vf.fieldName
          vf.fieldId = (vf.field || {}).fieldId
          let defaultColumnName =
            viewObject.defaultModuleFields &&
            viewObject.defaultModuleFields[vf.name]
              ? viewObject.defaultModuleFields[vf.name].columnDisplayName
              : null
          vf.displayName =
            vf.columnDisplayName || defaultColumnName || vf.field.displayName
          return vf
        })
      }
      if (this.peopleType == 'tenant') {
        let tenantcontactcolumns = this.getFieldsByModule('tenantcontact')
        let finalCols = []
        finalCols = tenantcontactcolumns.map(field => {
          let fieldObj = {}
          fieldObj.field = field
          fieldObj.name = field.name
          fieldObj.displayName = field.displayName
          return fieldObj
        })
        let resultFields = []
        if (!isEmpty(columns)) {
          resultFields.push(columns[0])
        }
        resultFields = resultFields.concat(finalCols)
        columns = resultFields
      }
      if (this.peopleType == 'vendors') {
        let vendorcontactcolumns = this.getFieldsByModule('vendorcontact')
        let finalCols = []
        finalCols = vendorcontactcolumns.map(field => {
          let fieldObj = {}
          fieldObj.field = field
          fieldObj.name = field.name
          fieldObj.displayName = field.displayName
          return fieldObj
        })
        let resultFields = []
        if (!isEmpty(columns)) {
          resultFields.push(columns[0])
        }
        resultFields = resultFields.concat(finalCols)
        columns = resultFields
      }
      return columns
    },
    selectedLookupField() {
      if (isEmpty(this.peopleType)) {
        return this.selectedField
      }
      return {
        isDataLoading: false,
        options: [],
        lookupModuleName:
          this.peopleType === 'tenant' ? 'tenantcontact' : 'vendorcontact',
        displayName: 'People',
        field: {
          lookupModule: {
            name:
              this.peopleType === 'tenant' ? 'tenantcontact' : 'vendorcontact',
            displayName: 'People',
          },
        },
        forceFetchAlways: true,
        filters: {},
        multiple: true,
        isDisabled: false,
      }
    },
  },
  methods: {
    loadTenantContactFields() {
      this.$util.loadFields('tenantcontact', false).then(fields => {
        this.tenantContactFields = fields
      })
    },
    loadVendorContactFields() {
      this.$util.loadFields('vendorcontact', false).then(fields => {
        this.vendorContactFields = fields
      })
    },
    getFieldsByModule(moduleName) {
      let fields = []
      if (moduleName === 'tenantcontact') {
        fields = this.tenantContactFields
      } else if (moduleName === 'vendorcontact') {
        fields = this.vendorContactFields
      }
      return fields
    },
    setPeopleTypeFilter(command) {
      this.clearAllFilters = true
      let value = []
      if (command === 'tenant_contact') {
        this.peopleType = 'tenant'
        value = ['1']
        this.quickFiltersContacts()
      } else if (command === 'vendor_contact') {
        this.peopleType = 'vendors'
        value = ['2']
        this.quickFiltersContacts()
      }
      this.clearFilter()
      this.specialFilterValue = { peopleType: { operatorId: 54, value } }
      this.setPeopleAdditionalFilters()
    },
    clearPeopleTypeFilter() {
      this.peopleType = null
      this.specialFilterValue = {
        peopleType: { operatorId: 54, value: ['1', '2'] },
      }
      this.setPeopleAdditionalFilters()
    },
    setPeopleAdditionalFilters() {
      if (!isEmpty(this.peopleType)) {
        if (this.peopleType === 'tenant') {
          this.quickFilters.push({
            value: null,
            key: 'Tenant',
            placeHolderText: 'Tenant',
            lookupModule: { name: 'tenant' },
            disabled: false,
            options: [],
          })
        }
        if (this.peopleType === 'vendors') {
          this.quickFilters.push({
            value: null,
            key: 'Vendor',
            placeHolderText: 'Vendor',
            lookupModule: { name: 'vendors' },
            disabled: false,
            options: [],
          })
        }
      } else {
        this.quickFilters = [this.quickFilters[0]]
      }
    },
    constructQuickFilters() {
      if (this.selectedField.lookupModuleName === 'people') {
        this.quickFiltersContacts()
      } else if (this.selectedField.lookupModuleName === 'tenantunit') {
        this.quickFilters = [
          {
            spaceType: 1,
            value: null,
            key: 'site',
            placeHolderText: 'Site',
            lookupModule: { name: 'site' },
            disabled: false,
            options: [],
          },
          {
            spaceType: 2,
            value: null,
            key: 'building',
            placeHolderText: 'Building',
            lookupModule: { name: 'building' },
            disabled: false,
            options: [],
          },
          {
            spaceType: 3,
            value: null,
            key: 'floor',
            placeHolderText: 'Floor',
            lookupModule: { name: 'floor' },
            disabled: false,
            options: [],
          },
        ]
      }
    },
    quickFiltersContacts() {
      this.quickFilters = [
        {
          value: null,
          key: 'peopletype',
          placeHolderText: 'People Type',
          disabled: false,
          lookupModule: { name: 'peopleType' },
          options: [
            { label: 'Tenant Contact', value: 1 },
            { label: 'Vendor Contact', value: 2 },
          ],
        },
      ]
    },
    resourceFilterConstruction(field) {
      if (isEmpty(field)) {
        return this.resourceFilterObj
      }
      if (field.key === 'peopletype') {
        let filter = {
          operatorId: 36,
          value: [`${value}`],
        }
        filters.peopleType = filter
        return filters
      }
      let {
        lookupModule: { name },
        value,
      } = field
      let filterHash = {
        site: 'siteId',
        building: 'building',
        floor: 'floor',
        basespace: 'space',
        tenant: 'tenant',
        vendors: 'vendor',
        peopleType: 'peopleType',
      }

      if (!isEmpty(value)) {
        let filter = {}
        filter = {
          operatorId: 36,
          value: [`${value}`],
        }
        this.$set(this.resourceFilterObj, filterHash[name], filter)
      } else {
        this.$delete(this.resourceFilterObj, filterHash[name])
      }

      let filters = {}

      if (name === 'basespace') {
        Object.entries(this.resourceFilterObj).forEach(([key, value]) => {
          if (['siteId', 'building', 'floor'].includes(key)) {
            filters[key] = value
          }
        })
      } else if (name === 'building') {
        Object.entries(this.resourceFilterObj).forEach(([key, value]) => {
          if (key === 'siteId') {
            filters[key] = value
          }
        })
      } else if (name === 'floor') {
        Object.entries(this.resourceFilterObj).forEach(([key, value]) => {
          if (['siteId', 'building'].includes(key)) {
            filters[key] = value
          }
        })
      }
      return filters
    },
    setLookUpFilter(field, valueObj) {
      if (field.key == 'peopletype') {
        this.resourceFilterObj = {}

        if (!isEmpty(valueObj.value)) {
          this.specialFilterValue = {
            peopleType: { operatorId: 54, value: [valueObj.value.toString()] },
          }
        } else {
          this.specialFilterValue = {
            peopleType: { operatorId: 54, value: ['1', '2'] },
          }
        }
      }
      if (field.key == 'peopletype') {
        if (valueObj.value == 1) {
          this.setPeopleTypeFilter('tenant_contact')
        } else if (valueObj.value == 2) {
          this.setPeopleTypeFilter('vendor_contact')
        } else {
          this.clearPeopleTypeFilter()
        }
      }
      let { spaceType } = field
      let { value } = valueObj || {}
      this.quickFilters.forEach(fld => {
        if (fld.key === field.key) {
          fld.value = !isEmpty(value) ? value : null
          this.resourceFilterConstruction(fld)
        }
      })

      this.quickFilters.forEach(fld => {
        let resetOptions = spaceType && spaceType < fld.spaceType

        fld.filters = this.resourceFilterConstruction(fld)
        if (resetOptions) {
          if (fld.key !== 'building') fld.disabled = isEmpty(value)
          fld.value = null
          this.resourceFilterConstruction(fld)
          value = null

          let params = {
            page: 1,
            perPage: 50,
          }
          getFieldOptions({ field: fld, ...params }).then(
            ({ error, options }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                fld.options = options
              }
            }
          )
        }
      })
    },
    setFilterForWizard(field, valObj) {
      this.wizardFilterValue = {}
      let { lookupModule } = field
      let { name } = lookupModule
      let value = []
      let operatorId = 2
      if (!isEmpty(valObj) && valObj.value) {
        value = [valObj.value.toString()]
        operatorId = 54
      }
      if (name === 'vendors') {
        this.specialFilterValue = {
          vendor: { operatorId, value },
        }
      } else {
        this.specialFilterValue = {
          tenant: { operatorId, value },
        }
      }
    },
  },
}
</script>
<style scoped>
.border-grey {
  border: 1px solid rgba(210, 217, 225, 1);
}
</style>
