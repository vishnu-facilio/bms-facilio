<template>
  <el-dialog
    :visible.sync="canShowWizard"
    width="70%"
    top="5vh"
    class="fc-dialog-center-container f-lookup-wizard scale-up-center"
    :append-to-body="true"
    :show-close="false"
    :before-close="resetDefaultValues"
  >
    <template slot="title">
      <div class="header d-flex">
        <div class="el-dialog__title self-center">{{ title }}</div>
        <el-input
          ref="mainFieldSearchInput"
          v-if="showMainFieldSearch"
          class="fc-input-full-border2 width-auto mL-auto"
          suffix-icon="el-icon-search"
          v-model="searchText"
          @blur="hideMainFieldSearch"
        ></el-input>
        <span v-else class="self-center mL-auto" @click="openMainFieldSearch">
          <inline-svg
            src="svgs/search"
            class="vertical-middle cursor-pointer"
            iconClass="icon icon-sm mT5 mR5 search-icon"
          ></inline-svg>
        </span>
        <span
          v-if="!$validation.isEmpty(totalCount)"
          class="separator self-center"
          >|</span
        >
        <pagination
          :currentPage.sync="page"
          :total="totalCount"
          :perPage="perPage"
          class="self-center"
        ></pagination>
        <span
          v-if="!$validation.isEmpty(totalCount)"
          class="separator pL0 self-center"
          >|</span
        >
        <div
          class="close-btn self-center cursor-pointer"
          @click="canShowWizard = false"
        >
          <i class="el-dialog__close el-icon el-icon-close"></i>
        </div>
      </div>
    </template>
    <div>
      <div
        v-if="isLoading"
        class="loading-container d-flex justify-content-center height550"
      >
        <Spinner :show="isLoading"></Spinner>
      </div>
      <div v-else class="height550">
        <div
          class="fc-list-view fc-list-table-container fc-table-td-height fc-table-viewchooser"
        >
          <el-table
            :data="spaces"
            style="width: 100%;"
            :fit="true"
            height="250"
            @selection-change="selectedItems"
          >
            <template slot="empty">
              <img
                class="mT20"
                src="~statics/noData-light.png"
                width="100"
                height="100"
              />
              <div class="mT10 label-txt-black f14">
                No
                {{ moduleDisplayName ? moduleDisplayName : moduleName }}
                available.
              </div>
            </template>
            <el-table-column
              type="selection"
              width="60"
              fixed
            ></el-table-column>

            <el-table-column label="ID" width="100">
              <template v-slot="item">
                <div>#{{ item.row.id }}</div>
              </template>
            </el-table-column>

            <el-table-column label="Name" width="180">
              <template v-slot="item">
                <div>{{ item.row.name }}</div>
              </template>
            </el-table-column>

            <el-table-column label="Type" width="180">
              <template v-slot="item">
                <div>{{ item.row.spaceTypeVal }}</div>
              </template>
            </el-table-column>
            <el-table-column label="Building" width="180">
              <template v-slot="item">
                <div>{{ getBuildingName(item.row) }}</div>
              </template>
            </el-table-column>
            <el-table-column label="Floor" width="180">
              <template v-slot="item">
                <div>{{ getFloorName(item.row) }}</div>
              </template>
            </el-table-column>
            <!-- <el-table-column label="Space" width="180">
              <template v-slot="item">
                <div>
                  {{ getSpaceName(item.row) }}
                </div>
              </template>
            </el-table-column>-->
          </el-table>
          <!-- <div
            v-if="!$validation.isEmpty(selectedItemName)"
            class="pT20 pL20 pB10 fc-black-color letter-spacing0_5 f13 bold"
          >
            {{ selectedLabel }} {{ selectedItemName }}
          </div>-->
        </div>
      </div>
    </div>
    <div v-if="!isLoading" class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="canShowWizard = false"
        >CANCEL</el-button
      >
      <el-button
        type="primary"
        :loading="saving"
        :disabled="$validation.isEmpty(selectedSpaces)"
        class="modal-btn-save"
        @click="updateSpaces()"
        >{{ saving ? 'updating...' : 'Update' }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'
import Constants from 'util/constant'
import isEqual from 'lodash/isEqual'

export default {
  props: ['canShowSpaceWizard', 'details', 'excludeSpaces'],
  components: { Pagination, Spinner },
  data() {
    return {
      isLoading: false,
      selectedSpaces: [],
      spaces: [],
      showMainFieldSearch: false,
      saving: false,
      searchText: null,
      page: 1,
      perPage: 10,
      totalCount: null,
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
    this.init()
  },
  computed: {
    ...mapGetters(['getSpaceCategoryPickList']),
    spaceCategories() {
      return this.getSpaceCategoryPickList()
    },
    moduleName() {
      return 'space'
    },
    moduleDisplayName() {
      return 'Spaces'
    },
    mainField() {
      let mainField = null
      mainField = {
        name: 'name',
        displayName: 'Name',
        field: {
          name: 'name',
          dataTypeEnum: 'STRING',
        },
      }
      return mainField
    },
    filterObj() {
      let filterObjs = {}
      let { mainField, searchText } = this

      if (
        !isEmpty(mainField) &&
        !isEmpty(searchText) &&
        searchText.length > 1
      ) {
        let { name, field } = mainField
        let { dataTypeEnum } = field
        let value = [searchText]
        let operatorId = Constants.FILTER_OPERATORID_HASH[dataTypeEnum]
        filterObjs[name] = {
          operatorId,
          value,
        }
      }
      return filterObjs
    },
    title() {
      return 'Choose Space'
    },
    canShowWizard: {
      get() {
        return this.canShowSpaceWizard
      },
      set(value) {
        this.$emit('update:canShowSpaceWizard', value)
      },
    },
  },
  watch: {
    filterObj(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.debounceMainFieldSearch()
      }
    },
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.initial()
        this.loadDataCount()
      }
    },
  },
  mounted() {
    this.initial()
    this.loadDataCount()
  },
  methods: {
    init() {
      this.debounceMainFieldSearch = this.$helpers.debounce(() => {
        this.initial()
        this.loadDataCount()
      }, 2000)
    },
    getBuildingName(basespace) {
      let { spaceTypeVal } = basespace
      if (spaceTypeVal === 'Space' || spaceTypeVal === 'Floor') {
        let { building } = basespace || {}
        if (!isEmpty(building)) {
          return building.name || ''
        } else {
          return '---'
        }
      }
      return '---'
    },
    getFloorName(basespace) {
      let { spaceTypeVal } = basespace
      if (spaceTypeVal === 'Space') {
        let { floor } = basespace || {}
        if (!isEmpty(floor)) {
          return floor.name || ''
        } else {
          return '---'
        }
      }
      return '---'
    },
    getSpaceName(basespace) {
      return '---'
    },
    loadDataCount() {
      let moduleName = 'basespace'
      let { details } = this
      let filters = {
        spaceType: {
          operator: '=',
          value: ['2, 3, 4'],
        },
        site: { operator: 'is', value: [String(details.siteId)] },
      }
      if (this.filterObj) {
        Object.assign(filters, this.filterObj)
      }
      let url = `v2/module/data/list?moduleName=${moduleName}&fetchCount=true`
      if (!isEmpty(filters)) {
        let encodedFilters = encodeURIComponent(JSON.stringify(filters))
        url = `${url}&filters=${encodedFilters}`
      }
      this.$http
        .get(url)
        .then(({ data: { message, responseCode, result = {} } }) => {
          if (responseCode === 0) {
            let { count } = result
            this.$set(this, 'totalCount', count)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(`${message} while fetching count`)
        })
    },
    initial() {
      let { details } = this
      this.isLoading = true
      let filters = {
        spaceType: {
          operator: '=',
          value: ['2, 3, 4'],
        },
        site: { operator: 'is', value: [String(details.siteId)] },
      }
      if (this.filterObj) {
        Object.assign(filters, this.filterObj)
      }
      let url = `/basespace?&orderBy=spaceType&fetchHierarchy=true&orderType=asc&page=${
        this.page
      }&perPage=${this.perPage}&filters=${encodeURIComponent(
        JSON.stringify(filters)
      )}`
      this.$http
        .get(url)
        .then(response => {
          if (response) {
            let { data } = response
            let { basespaces } = data
            if (!isEmpty(basespaces)) {
              if (!isEmpty(this.excludeSpaces)) {
                let ids = []
                let data = []
                this.excludeSpaces.forEach(sp => {
                  ids.push(sp.id)
                })
                data = basespaces.filter(bs => !ids.includes(bs.id))
                this.spaces = data
              } else {
                this.spaces = basespaces
              }
            } else {
              this.spaces = basespaces
            }
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
        .finally(() => (this.isLoading = false))
    },
    selectedItems(selectedItem) {
      this.selectedSpaces = selectedItem
    },
    resetDefaultValues() {
      this.$set(this, 'showMainFieldSearch', false)
    },
    updateSpaces() {
      let { details } = this
      this.saving = true
      let successMsg = ''
      let finalData = ''
      let data = {
        spacesUpdate: true,
        tenant: {
          id: details.id,
          spaces: [],
        },
      }
      data.tenant.spaces = this.selectedSpaces

      let url = `/v2/tenant/update`
      finalData = data
      this.$http
        .post(url, finalData)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            this.$emit('setSelectedSpaces', this.selectedSpaces)
            this.canShowWizard = false
            this.saving = false
            successMsg = `Space created successfully!`
            this.$message.success(successMsg)
            this.selectedSpaces = []
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
        .finally(() => (this.saving = false))
    },
    hideMainFieldSearch() {
      let { searchText } = this
      if (isEmpty(searchText)) {
        this.$set(this, 'showMainFieldSearch', false)
      }
    },
    openMainFieldSearch() {
      this.$set(this, 'showMainFieldSearch', true)
      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']
        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },
  },
}
</script>
