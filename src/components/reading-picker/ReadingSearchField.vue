<template>
  <el-select
    v-model="selectedItem"
    :multiple="multiSelect"
    filterable
    reserve-keyword
    remote
    :remote-method="query => fetchOptions(query)"
    :loading="loading"
    :loading-text="$t('common._common.searching')"
    clearable
    :class="['fc-select-multiple-tag width100', multiSelect && 'fc-tag']"
    placeholder="Select Reading"
    @change="recordSelected"
  >
    <el-option
      v-for="(name, id) in listToSelect"
      :key="id"
      :label="name"
      :value="id"
    ></el-option>
  </el-select>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'

export default {
  props: ['selectedList', 'multiSelect'],

  data() {
    return {
      loading: false,
      assetList: {},
      pointList: {},
      assetVsPoint: {},
      selectedItems: [],
    }
  },

  created() {
    this.selectedItems = [...this.selectedList]
    this.fetchOptions()
  },

  computed: {
    selectedItem: {
      get() {
        let { selectedItems, multiSelect } = this
        let list = selectedItems.map(
          item => `${item.assetId}_${item.readingId}`
        )

        return multiSelect ? list : list[0]
      },

      set(value) {
        let { assetList, pointList, multiSelect } = this
        let selectedList = []

        if (!isEmpty(value)) {
          if (multiSelect) {
            value.forEach(id => {
              let listObj = this.selectedItems.find(
                item => id === `${item.assetId}_${item.readingId}`
              )

              if (isEmpty(listObj)) {
                let [assetId, readingId] = id.split('_')

                listObj = {
                  assetId,
                  assetName: assetList[assetId],
                  readingId,
                  readingName: pointList[readingId],
                }
              }
              selectedList.push(listObj)
            })

            this.selectedItems = selectedList
          } else {
            let ids = value.split('_')
            let listObj = {
              assetId: ids[0],
              assetName: assetList[ids[0]],
              readingId: ids[1],
              readingName: pointList[ids[1]],
            }

            this.selectedItems = [listObj]
          }
        } else {
          this.selectedItems = []
        }
      },
    },

    listToSelect() {
      let { assetList, pointList, assetVsPoint } = this
      let optionList = {}

      Object.entries(assetVsPoint).forEach(([asset, readingArray]) => {
        readingArray.forEach(reading => {
          optionList[`${asset}_${reading}`] = `${assetList[asset]} / ${
            pointList[`${reading}`]
          }`
        })
      })

      return optionList
    },
  },

  methods: {
    fetchOptions: debounce(function(query = null) {
      this.loadFilteredData(query)
        .then(({ assets, pointList, AssetsWithReadings }) => {
          this.assetList = assets
          this.pointList = pointList
          this.assetVsPoint = AssetsWithReadings
        })
        .finally(() => (this.loading = false))
    }, 1000),

    loadFilteredData(filters) {
      let url = 'asset/getAssetsWithReadings'
      let params = {
        page: 1,
        perPage: 10,
      }

      if (!isEmpty(filters)) params.search = filters
      this.loading = true
      return API.post(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let {
            assets = {},
            fields = {},
            ReadingsWithAssets = {},
            AssetsWithReadings = {},
          } = data
          let pointList = {}

          Object.entries(fields).forEach(([key, value]) => {
            pointList[key] = value['displayName']
          })
          return { assets, pointList, ReadingsWithAssets, AssetsWithReadings }
        }
      })
    },

    recordSelected() {
      this.$emit('selectedItems', this.selectedItems)
    },
  },
}
</script>
