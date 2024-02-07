<script>
import { isEmpty } from '@facilio/utils/validation'
import clone from 'lodash/clone'
import { getFieldOptions } from 'util/picklist'

export default {
  data() {
    return {
      resourceFilterObj: {},
      quickFilters: [],
      categoryIds: [],
      QUICK_FILTERS: [
        {
          value: null,
          key: 'category',
          placeHolderText: 'Category',
          lookupModule: { name: null },
          disabled: false,
          options: [],
        },
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
          disabled: true,
          options: [],
        },
        {
          spaceType: 4,
          value: null,
          key: 'space',
          placeHolderText: 'Space',
          lookupModule: { name: 'basespace' },
          disabled: true,
          options: [],
        },
      ],
    }
  },

  computed: {
    buildingIds() {
      let { resourceFilterObj } = this
      let { siteId, building, floor, space } = resourceFilterObj

      if (space) return space.value
      else if (floor) return floor.value
      else if (building) return building.value
      else if (siteId) return siteId.value
      else return []
    },
  },

  methods: {
    constructQuickFilters() {
      this.quickFilters = clone(this.QUICK_FILTERS)
      this.resourceFilterObj = {}

      let {
        quickFilters,
        filters: { categoryId, siteId, buildingId, floorId, spaceId } = {},
      } = this

      quickFilters.forEach(filter => {
        if (filter.key === 'category' && categoryId) {
          filter.value = categoryId
        } else if (filter.key === 'site' && siteId) {
          filter.value = siteId
        } else if (filter.key === 'building' && buildingId) {
          filter.value = buildingId
        } else if (filter.key === 'floor' && floorId) {
          filter.value = floorId
        } else if (filter.key === 'space' && spaceId) {
          filter.value = spaceId
        }
        this.resourceFilterConstruction(filter)
      })
    },

    resourceFilterConstruction(field) {
      let {
        lookupModule: { name },
        value,
      } = field
      let filters = {}

      if (field.key === 'category') {
        this.categoryIds = value ? [value] : []
      } else {
        let filterHash = {
          site: 'siteId',
          building: 'building',
          floor: 'floor',
          basespace: 'space',
        }

        if (!isEmpty(value)) {
          let filter = {
            operatorId: 36,
            value: [`${value}`],
          }

          this.$set(this.resourceFilterObj, filterHash[name], filter)
        } else {
          this.$delete(this.resourceFilterObj, filterHash[name])
        }

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
      }

      return filters
    },

    setLookUpFilter(field, valueObj) {
      let { spaceType } = field
      let { value } = valueObj || {}

      this.quickFilters.forEach(filter => {
        if (filter.key === field.key) {
          filter.value = !isEmpty(value) ? value : null

          this.resourceFilterConstruction(filter)
        }
      })

      let promises = []

      this.quickFilters.forEach(filterField => {
        if (filterField.key === 'category' && !isEmpty(this.buildingIds)) {
          promises.push(this.fetchCategoryOptions('', filterField))
        } else {
          let resetOptions = spaceType && spaceType < filterField.spaceType

          if (resetOptions) {
            if (filterField.key !== 'building') {
              filterField.disabled = !isEmpty(value) ? false : true
            }

            filterField.value = null
            value = null
            promises.push(
              this.fetchOptions('', filterField, {
                page: 1,
                perPage: 50,
              })
            )
          }
          filterField.filters = this.resourceFilterConstruction(filterField)
        }
      })

      Promise.all(promises).finally(this.loadData())
    },

    fetchOptions(query, field, params) {
      if (field.key === 'category') {
        return this.fetchCategoryOptions(query, field)
      }

      let {
        lookupModule: { name },
      } = field
      let fetchOptions = {
        ...params,
        lookupModuleName: name,
        searchText: query,
        filters: field.filters,
      }

      return getFieldOptions({ field: fetchOptions }).then(
        ({ error, options }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            field.options = options
          }
        }
      )
    },

    fetchCategoryOptions(query, field) {
      return this.$http
        .post('/asset/getAssetCategoryWithReadings', {
          buildingIds: this.buildingIds,
          search: query,
        })
        .then(({ data }) => {
          let options = []
          let category = data[this.categoryIds]

          this.categoryIds = category ? category : []
          Object.entries(data).forEach(([id, name]) => {
            let optionObj = {}

            optionObj['value'] = id
            optionObj['label'] = name
            options.push(optionObj)
          })
          field.options = options
          field.value = !isEmpty(this.categoryIds) ? this.categoryIds : null
        })
    },
  },
}
</script>
