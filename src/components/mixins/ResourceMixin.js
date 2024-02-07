import $util from 'util/util'
import { areValuesEmpty, isEmpty, isArray } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'

export default {
  props: [
    'query',
    'initialValues',
    'showAsset',
    'isService',
    'resourceType',
    'filter',
    'queryFilter', // For sending filter directly in api
    'withWritableReadings',
  ],
  data() {
    return {
      isAsset: this.showAsset,
      spaceList: [],
      assetList: [],
      loading: false,
      loadMore: false,
      fetchingMore: false,
      resourceLoading: false,
      quickSearchQuery: this.query,
      canLoadMore: false,
      initialData: null,
      paging: {
        page: 1,
        perPage: 40,
      },
      initialResourcesFecthed: false,
      assetCategory: {
        options: {},
        value: '',
        placeHolder: 'Category',
      },
      spaceCategory: {
        options: {},
        value: '',
        placeHolder: 'Category',
      },
      resourceConfig: [
        // Space hierarchy in the increasing order of spaceType.
        {
          options: [],
          spaceType: 1,
          value: '',
          key: 'site',
          placeHolder: 'Site',
          disabled: false,
        },
        {
          options: [],
          spaceType: 2,
          value: '',
          key: 'building',
          placeHolder: 'Building',
          disabled: false,
        },
        {
          options: [],
          spaceType: 3,
          value: '',
          key: 'floor',
          placeHolder: 'Floor',
          disabled: false,
        },
        {
          options: [],
          spaceType: 4,
          value: '',
          key: 'space',
          placeHolder: 'Space',
          disabled: false,
        },
      ],
    }
  },
  watch: {
    showAsset(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.isAsset = this.showAsset
      }
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
  },
  computed: {
    resourceList() {
      return this.isAsset ? this.assetList : this.spaceList
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
  },
  methods: {
    checkAndInitResourceData() {
      if (!this.resourceConfig[0].options.length) {
        this.initResourceData()
      } else {
        this.reInitResourceData()
      }
    },
    setDefaults() {
      if (this.filter) {
        this.resourceConfig.forEach(i => {
          if (this.filter[i.key]) {
            i.value = this.filter[i.key]
            i.disabled = true
          }
        })
      }
    },
    async initResourceData() {
      this.setDefaults()
      this.setInitialValues()
      this.loadResourceData()
      this.loadSpace(this.resourceConfig[0])
      let result = true
      this.resourceType &&
        this.resourceType.filter(elm => {
          if (elm > 1) {
            result = false
          }
        })

      //temp fix ,when both resourceType filter and filter{site:123} are enabled building filter in component doesnt work
      if (result == false && !areValuesEmpty(this.filter)) {
        result = true
      }
      if (result) {
        this.loadSpace(this.resourceConfig[1])
      }

      let {
        error: assetCategoryError,
        options: assetCategoryOptions,
      } = await getFieldOptions({
        field: { lookupModuleName: 'assetcategory', skipDeserialize: true },
      })

      if (assetCategoryError) {
        this.$message.error(assetCategoryError.message || 'Error Occured')
      } else {
        this.assetCategory.options = assetCategoryOptions
      }

      let {
        error: spaceCategoryError,
        options: spaceCategoryOptions,
      } = await getFieldOptions({
        field: { lookupModuleName: 'spacecategory', skipDeserialize: true },
      })

      if (spaceCategoryError) {
        this.$message.error(spaceCategoryError.message || 'Error Occured')
      } else {
        this.spaceCategory.options = spaceCategoryOptions
      }
    },
    reInitResourceData() {
      this.assetCategory.value = ''
      this.spaceCategory.value = ''
      for (let i = 0; i < this.resourceConfig.length; i++) {
        this.resourceConfig[i].value = ''
        this.resourceConfig[i].disabled = false
        if (this.resourceConfig[i].spaceType > 1) {
          this.resourceConfig[i].options = []
        }
      }
      this.setDefaults()
      this.setInitialValues()
      this.loadResourceData()
      let result = true
      this.resourceType &&
        this.resourceType.filter(elm => {
          if (elm > 1) {
            result = false
          }
        })
      //temp fix ,when both resourceType filter and filter{site:123} are enabled building filter in component doesnt work
      if (result == false && !areValuesEmpty(this.filter)) {
        result = true
      }
      if (result) {
        this.loadSpace(this.resourceConfig[1]) // Building options should also be populated by default
      }
    },
    nextPage() {
      if (this.canLoadMore && !this.loading && !this.fetchingMore) {
        this.fetchingMore = true
        this.loadMore = true
        this.loadResourceData()
      }
    },
    setInitialValues() {
      if (this.initialValues) {
        if (this.initialValues.assetCategory) {
          this.assetCategory.value = this.initialValues.assetCategory
        }
        if (this.initialValues.spaceCategory) {
          this.spaceCategory.value = this.initialValues.spaceCategory
        }
        this.initialData = this.initialValues
        this.initialResourcesFecthed = false
      }
    },
    onLocationSelected(field) {
      for (let i = field.spaceType; i < 4; i++) {
        this.resourceConfig[i].options = []
        this.resourceConfig[i].value = ''
      }

      if (field.spaceType === 1 || field.value) {
        this.loadSpace(this.resourceConfig[field.spaceType])
      }
      return this.loadResourceData()
    },
    onCategorySelected() {
      this.loadAsset()
    },
    onSpaceCategorySelected() {
      if (!isEmpty(this.spaceCategory) && !isEmpty(this.spaceCategory.value)) {
        this.loadSpaceContext()
      } else {
        this.loadSpace()
      }
    },
    loadResourceData() {
      if (this.isAsset) {
        return this.loadAsset()
      } else {
        if (this.spaceCategory.value) {
          return this.loadSpaceContext()
        } else {
          return this.loadSpace()
        }
      }
    },
    loadSpaceContext() {
      let filter = [
        {
          key: 'spaceCategory',
          operator: 'is',
          value: this.spaceCategory.value,
        },
      ]
      this.resourceConfig.forEach(i => {
        if (!isEmpty(i.value)) {
          filter.push({ key: i.key, operator: 'is', value: i.value })
        }
      })
      if (!this.loadMore) {
        this.paging.page = 1
      }
      return $util
        .loadSpacesContext(4, this.quickSearchQuery, filter, this.paging)
        .then(response => {
          if (this.loadMore) {
            this.loadMore = false
            this.spaceList.push(...response.records)
            this.setListSelected()
          } else {
            // this.spaceList = []
            // this.spaceList.push(...response.records)

            this.spaceList = response.records
          }
          this.canLoadMore = response.records.length === this.paging.perPage
          this.initialResourcesFecthed = true
          this.loading = false
          this.paging.page++
          this.fetchingMore = false
        })
    },
    loadSpace(field) {
      // If field is present, then the corresponding options will be fetched for the dropdown
      let spaceType
      if (field) {
        spaceType = field.spaceType
      } else {
        if (this.resourceType) {
          spaceType = this.resourceType
        }
        this.resourceLoading = true
      }

      let spaceFilters = this.resourceConfig
        .filter(data => data.value)
        .map(data => ({
          key: data.key,
          value: data.value,
        }))
      if (!this.loadMore) {
        this.paging.page = 1
      }
      if (this.queryFilter && !field) {
        for (let field in this.queryFilter) {
          spaceFilters.push({
            key: field,
            value: this.queryFilter[field].value,
            operator: this.queryFilter[field].operator,
          })
        }
      }

      if (
        (Array.isArray(spaceType) && spaceType.some(type => type === 4)) ||
        spaceType === 4
      ) {
        let paging = {
          page: 1,
          perPage: -1,
        }
        return $util
          .loadSpacesContext(
            spaceType,
            this.quickSearchQuery,
            spaceFilters,
            paging
          )
          .then(response => {
            this.resourceLoading = false
            if (field) {
              field.options = response.records
            } else {
              if (this.loadMore) {
                this.loadMore = false
                this.spaceList.push(...response.records)
                this.setListSelected()
              } else {
                this.spaceList = response.records
              }
              this.canLoadMore = response.records.length === this.paging.perPage
              this.initialResourcesFecthed = true
              this.loading = false
              this.paging.page++
              this.fetchingMore = false
            }
          })
      } else {
        let paging = this.paging
        if (spaceType === 3) {
          paging = {
            page: 1,
            perPage: -1,
          }
        }
        return $util
          .loadSpace(spaceType, this.quickSearchQuery, spaceFilters, paging)
          .then(response => {
            this.resourceLoading = false
            if (field) {
              field.options = response.basespaces
            } else {
              if (this.loadMore) {
                this.loadMore = false
                this.spaceList.push(...response.basespaces)
              } else {
                this.spaceList = response.basespaces
              }
              this.canLoadMore =
                response.basespaces.length === this.paging.perPage
              this.initialResourcesFecthed = true
              this.loading = false
              this.paging.page++
              this.fetchingMore = false
            }
          })
      }
    },
    loadAsset() {
      let spaceId
      let self = this
      for (let i = 3; i >= 0; i--) {
        if (this.resourceConfig[i].value) {
          spaceId = this.resourceConfig[i].value
          break
        }
      }
      if (!this.loadMore) {
        this.paging.page = 1
      }
      // self.loadMore ? self.fetchingMore = true : self.loading = true
      this.resourceLoading = true
      let params = {
        categoryId: this.assetCategory.value,
        searchText: this.quickSearchQuery,
        paging: this.paging,
        isService: this.isService,
        withWritableReadings: this.withWritableReadings,
      }
      if (isArray(spaceId)) {
        params = { ...params, spaceIds: spaceId || [] }
      } else {
        params = { ...params, spaceId: spaceId || '' }
      }
      return $util.loadAsset(params).then(response => {
        if (this.loadMore) {
          this.loadMore = false
          this.assetList.push(...response.assets)
          this.setListSelected()
        } else {
          this.assetList = response.assets
        }
        let { paging } = self || {}
        let selfPerPage = this.$getProperty(paging, 'perPage', null)

        this.canLoadMore = response.assets.length === selfPerPage
        this.resourceLoading = false
        this.loading = false
        this.fetchingMore = false
        this.paging.page++
        this.loadMore = false
        this.initialResourcesFecthed = true
      })
    },
  },
}
