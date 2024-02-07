<template>
  <div class="classification-list-container-setup">
    <portal to="classification-btn-portal">
      <el-button
        type="primary"
        class="setup-el-btn pointer"
        @click="openClassificationDialog()"
      >
        {{ $t('setup.classification.new_classification') }}
      </el-button>
    </portal>
    <div class="d-flex search-page-container">
      <ClassificationBreadcrumb
        v-if="!showSearchResults"
        :breadCrumb="breadCrumb"
        @onGetBreadcrumbList="getBreadcrumbList"
      ></ClassificationBreadcrumb>
      <div v-else class="search-result">
        {{ $t('setup.classification.results') }}
      </div>
      <div class="d-flex search-page-items">
        <el-input
          v-if="showMainFieldSearch"
          v-model="classificationSearchData"
          ref="mainFieldSearchInput"
          class="fc-input-full-border2 width150px"
          clearable
          autofocus
          @blur="hideMainFieldSearch"
          @clear="hideMainFieldSearch"
          @change="loadClassificationListOnSearch"
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
          ref="pagination"
          :total="count"
          :pageNo="page"
          :perPage="perPage"
          @onPageChanged="onPageChange"
          class="mL20"
        ></Pagination>
      </div>
    </div>
    <div class="classification-list-table">
      <div v-if="isLoading" class="flex-middle height-100">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <el-table
        v-else
        :data="classificationModuleList"
        :header-cell-style="{ background: '#f3f1fc' }"
        style="width: 100%"
        height="100%"
        :empty-text="$t('setup.classification.empty_state_classification')"
        class="form-list-table overflow-y-scroll"
      >
        <el-table-column prop="name" label="Name" width="250"></el-table-column>
        <el-table-column
          prop="description"
          label="Description"
        ></el-table-column>
        <el-table-column
          v-if="showSearchResults"
          label="ClassificationPath"
          prop="classificationPath"
        ></el-table-column>
        <el-table-column width="60">
          <template v-slot="classification">
            <div class="visibility-hide-actions">
              <i
                class="el-icon-edit f14"
                @click="openClassificationDialog(classification.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template v-slot="classification">
            <i
              v-if="classification.row.hasChild && !showSearchResults"
              class="el-icon-arrow-right mL20 p3 fwBold clsfication-arrow-border page-widget-shadow"
              @click="getClassificationListChild(classification.row)"
            ></i>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <NewClassificationDialog
      v-if="classificationDialog"
      :selectedId="selectedId"
      @onClose="classificationDialog = false"
      @onSave="getClassificationList"
    >
    </NewClassificationDialog>
  </div>
</template>
<script>
import NewClassificationDialog from 'pages/setup/classification/NewClassificationDialog.vue'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from '@/list/FPagination'
import ClassificationBreadcrumb from 'pages/setup/classification/ClassificationBreadcrumb.vue'
import { ClassificationListModel } from 'pages/setup/classification/ClassificationModel'

export default {
  components: {
    NewClassificationDialog,
    Pagination,
    ClassificationBreadcrumb,
  },
  data() {
    return {
      classificationDialog: false,
      classificationModuleList: [],
      count: null,
      perPage: 10,
      page: 1,
      parentClassificationDataId: null,
      showSearchResults: false,
      classificationSearchData: null,
      showMainFieldSearch: false,
      isLoading: false,
      breadCrumb: [
        {
          name: 'All classification',
        },
      ],
    }
  },
  created() {
    this.getClassificationList()
  },
  watch: {
    page() {
      this.getClassificationList()
    },
  },
  computed: {
    lastChild() {
      return this.breadCrumb.length - 1
    },
  },
  methods: {
    async getClassificationList() {
      this.isLoading = true
      let { perPage, page, parentClassificationDataId } = this
      let search = this.classificationSearchData || null

      let params = {
        search,
        page,
        perPage,
        parentClassificationId: !isEmpty(parentClassificationDataId)
          ? parentClassificationDataId
          : null,
        withCount: true,
        fetchAll: true,
        hasChild: true,
        classificationResolvePath: !isEmpty(search),
      }
      this.classificationModuleList = await ClassificationListModel.fetchAll(
        params
      )
      this.count = ClassificationListModel.totalCount
      this.isLoading = false
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
    onPageChange(page) {
      this.page = page
    },
    hideMainFieldSearch() {
      if (isEmpty(this.classificationSearchData)) {
        this.showMainFieldSearch = false
        this.showSearchResults = false
      }
    },
    loadClassificationListOnSearch() {
      let id = this.breadCrumb[this.lastChild].id
      this.page = 1
      this.showSearchResults = true
      this.getClassificationList(id)
    },
    hasChildClassification(item) {
      let { hasChild } = item
      return !isEmpty(hasChild)
    },
    openClassificationDialog(value) {
      this.selectedId = value || null
      this.classificationDialog = true
    },
    getBreadcrumbList(item) {
      this.$refs.pagination.reset()
      let index = null
      let { id } = item || null

      index = this.breadCrumb.findIndex(obj => obj.id === id) || 0
      this.breadCrumb.splice(++index)
      this.parentClassificationDataId = item.id || null
      this.getClassificationList()
    },
    getClassificationListChild(item) {
      let { name, id } = item || {}
      this.page = 1
      this.breadCrumb.push({ name, id })
      this.parentClassificationDataId = id || null
      this.getClassificationList()
    },
  },
}
</script>
<style lang="scss">
.classification-list-container {
  overflow: hidden !important;
  .el-tabs__header.is-top {
    padding-left: 20px;
    background-color: white;
    border-top: 1px solid #f0f0f0;
    margin: 0px;
  }
  .search-page-container {
    height: 50px;
    width: 100%;
    border-top: 1px solid #f0f0f0;
    border-bottom: 1px solid #f0f0f0;
    background-color: white;
    justify-content: space-between;
    align-items: center;
  }
  .search-result {
    font-size: 16px;
    font-weight: 500;
  }
  .search-page-items {
    align-items: center;
    margin-right: 20px;
  }
  .clsfication-arrow-border {
    border-radius: 2px;
    box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.1);
    border: solid 0.5px #d0d9e2;
    background-color: #fff;
  }
  .classification-list-table {
    margin: 16px;
    height: calc(100vh - 280px);
    background: #fff;
  }
  .form-list-table {
    .el-table__cell {
      padding-left: 40px;
    }
    .el-table__body {
      font-size: 14px;
    }
  }
  .el-table--enable-row-hover .el-table__body tr:hover > td {
    background-image: linear-gradient(to bottom, #fff -44%, #f5f5f5 108%);
  }
}
</style>
