<template>
  <div class="classification-attribute-container">
    <portal to="classification-btn-portal">
      <el-button type="primary" class="setup-el-btn" @click="openAttribute()">
        {{ $t('setup.classification.attributes.add_attributes') }}
      </el-button>
    </portal>
    <div
      class="d-flex attribute-search-page-container attribute-search-page-items p20"
    >
      <el-input
        v-if="showMainFieldSearch"
        v-model="attributeSearchData"
        ref="mainFieldSearchInput"
        class="fc-input-full-border2 width150px form-builder-validation-main-field-search"
        clearable
        autofocus
        @blur="hideMainFieldSearch"
        @clear="hideMainFieldSearch"
        @change="getAttributeList()"
        :placeholder="$t('common._common.search')"
      ></el-input>
      <span v-else @click="openMainFieldSearch" class="search-icon-val">
        <inline-svg
          src="svgs/search"
          class="vertical-middle cursor-pointer"
          iconClass="icon icon-sm"
        ></inline-svg>
      </span>
      <Pagination
        :total="count"
        :pageNo="page"
        :perPage="perPage"
        @onPageChanged="onPageChange"
        class="mL20"
      ></Pagination>
    </div>
    <div class="attribute-list-table">
      <div v-if="isLoading" class="flex-middle height-100">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <el-table
        v-else
        :data="setattributeList"
        :header-cell-style="{ background: '#f3f1fc' }"
        height="100%"
        style="width: 100%"
        :empty-text="$t('setup.classification.empty_state_attribution')"
        class="form-list-table overflow-y-scroll"
      >
        <el-table-column prop="name" label="Name"></el-table-column>
        <el-table-column
          prop="fieldTypeDisplayName"
          label="Field Type"
        ></el-table-column>
        <el-table-column width="120">
          <template v-slot="attribute">
            <div class="d-flex">
              <div class="visibility-hide-actions">
                <i
                  class="el-icon-edit f14"
                  @click="openAttribute(attribute.row.id)"
                ></i>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <NewAttributeDialog
      v-if="showAttributeDialog"
      :selectedId="selectedId"
      @onClose="showAttributeDialog = false"
      @onSave="getAttributeList"
    ></NewAttributeDialog>
  </div>
</template>
<script>
import NewAttributeDialog from 'pages/setup/classification/NewAttributeDialog.vue'
import Pagination from '@/list/FPagination'
import { isEmpty } from '@facilio/utils/validation'
import { AttributeListModel } from 'pages/setup/classification/AttributeModal'

export default {
  components: { NewAttributeDialog, Pagination },
  data() {
    return {
      showAttributeDialog: false,
      showMainFieldSearch: false,
      attributeSearchData: null,
      selectedId: null,
      perPage: 10,
      page: 1,
      count: null,
      isLoading: false,
      attributesList: [],
      dataTypes: {
        1: 'String',
        2: 'Number',
        3: 'Decimal',
        4: 'Boolean',
        6: 'DateTime',
      },
    }
  },
  created() {
    this.getAttributeList()
  },
  computed: {
    setattributeList() {
      return this.attributesList.map(item => {
        item.fieldTypeDisplayName = this.dataTypes[item.fieldType]
        return item
      })
    },
  },
  watch: {
    page() {
      this.getAttributeList()
    },
  },
  methods: {
    async getAttributeList() {
      this.isLoading = true
      let { perPage, page } = this
      let params = {
        search: this.attributeSearchData,
        page,
        perPage,
        withCount: true,
      }
      if (!isEmpty(params.search)) this.page = 1

      this.attributesList = await AttributeListModel.fetchAll(params)
      this.count = AttributeListModel.totalCount
      this.isLoading = false
    },
    onPageChange(page) {
      this.page = page
    },
    openAttribute(id = null) {
      this.selectedId = id
      this.showAttributeDialog = true
    },
    openMainFieldSearch() {
      this.showMainFieldSearch = true

      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']

        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },
    hideMainFieldSearch() {
      if (isEmpty(this.attributeSearchData)) this.showMainFieldSearch = false
    },
  },
}
</script>
<style lang="scss" scoped>
.attribute-search-page-items {
  align-items: center;
  margin-right: 20px;
  justify-content: flex-end;
}
.attribute-search-page-container {
  height: 50px;
  width: 100%;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fff;
}
.attribute-list-table {
  margin: 16px;
  height: calc(100vh - 280px);
  background: #fff;
}
</style>
<style lang="scss">
.classification-attribute-container {
  .el-table--enable-row-hover .el-table__body tr:hover > td {
    background-image: linear-gradient(to bottom, #fff -44%, #f5f5f5 108%);
  }
}
</style>
