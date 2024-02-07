<template>
  <el-dialog
    :append-to-body="true"
    :visible="true"
    width="37%"
    :before-close="closeDialog"
    lock-scroll
    custom-class="fc-dialog-center-container scale-up-center"
    class="clsfi"
  >
    <div class="clsfi-header" slot="title">
      <div class="flex-middle justify-between clsfi-title-border clsfi-title ">
        <span class="clsfi-title-text">
          {{ $t('setup.classification.classification') }}
        </span>
        <pagination
          :currentPage.sync="page"
          :total="count"
          :perPage="perPage"
          ref="f-page"
          class="mR20"
        ></pagination>
      </div>
      <div class="fc-list-search-wrapper clsfi-search clsfi-title-border">
        <el-input
          prefix-icon="el-icon-search"
          ref="quickSearchQuery"
          autofocus
          type="text"
          v-model="clsfiModuleSearch"
          clearable
          @change="
            loadClassificationListOnSearch(breadCrumb[breadCrumb.length - 1].id)
          "
          @clear="loadClassificationListOnClear"
          placeholder="Search Classification"
          class="quick-search-input-specification"
        ></el-input>
      </div>
      <div>
        <div v-if="!searchResult">
          <classification-breadcrumb
            :breadCrumb="breadCrumb"
            @onGetBreadcrumbList="getBreadcrumbList"
            class="f12 mT10 pL5"
          ></classification-breadcrumb>
        </div>
        <div v-else class="search-result">
          {{ $t('setup.classification.results') }}
        </div>
      </div>
    </div>
    <div class="cls-list pB30">
      <div v-if="searchLoading" class="flex-middle m10 height80">
        <spinner :show="searchLoading" size="80"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(clsfiModuleList)"
        class="d-flex empty-state-classification height100"
      >
        {{ $t('setup.classification.empty_state_classification') }}
      </div>
      <div
        v-else
        v-for="item in clsfiModuleList"
        :key="item.id"
        class="d-flex flex-middle pR20"
      >
        <div
          class="flex-middle justify-between clsfi-list pointer pR20"
          :class="{ 'clsfi-select': isSelected(item.id) }"
          @click="selectedClsfiModuleId = item.id"
        >
          <div class="text-containter-content">
            <div>
              {{ item.name }}
            </div>
            <div v-if="searchResult" class="clsfi-path op5 f12 pT5">
              <span>{{ item.classificationPath }}</span>
            </div>
          </div>

          <div class="d-flex flex-middle">
            <i
              v-if="item.id === selectedClsfiModuleId"
              class="el-icon-circle-check pointer f18 fw6 clsfi-circle"
            ></i>
          </div>
        </div>
        <div class="parent-icon">
          <i
            v-if="item.hasChild && !searchResult"
            class="el-icon-arrow-right clsfi-arrow pointer f16 p3 fw6 page-widget-shadow"
            :class="[
              isSelected(item.id) ? 'clsfi-arrow-border' : 'clsfi-border-none',
            ]"
            @click="getClassificationListChild(item)"
          ></i>
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer" slot="footer">
      <el-button
        @click="closeDialog()"
        class="modal-btn-cancel clsfi-cancel-btn"
        >{{ $t('common._common.cancel') }}</el-button
      >
      <el-button
        type="primary"
        class="modal-btn-save clsfi-save-btn"
        @click="saveSpecification()"
        :loading="isSaving"
        >{{ onSaveButton }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import classificationBreadcrumb from 'src/pages/setup/classification/ClassificationBreadcrumb'
import { isEmpty } from '@facilio/utils/validation'
import { ClassificationListModel } from 'pages/setup/classification/ClassificationModel'

export default {
  props: ['isAttributeInherited', 'fromModuleName'],
  components: {
    Pagination,
    classificationBreadcrumb,
  },
  data() {
    return {
      isSaving: false,
      searchLoading: false,
      clsfiModuleList: [],
      selectedClsfiModuleId: null,
      clsfiModuleSearch: null,
      count: null,
      parentClassificationDataId: null,
      searchResult: false,
      perPage: 10,
      page: 1,
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
  computed: {
    onSaveButton() {
      return this.isAttributeInherited
        ? this.$t('common._common.next')
        : this.$t('common._common.save')
    },
  },
  watch: {
    page() {
      this.getClassificationList()
    },
  },
  methods: {
    isSelected(currentId) {
      return this.selectedClsfiModuleId === currentId
    },
    getBreadcrumbList(item) {
      let index = null
      let { id } = item

      index = this.breadCrumb.findIndex(obj => obj.id === id) || 0
      this.breadCrumb.splice(++index)
      this.parentClassificationDataId = id || null
      this.getClassificationList()
    },
    loadClassificationListOnSearch(id = null) {
      this.searchResult = true
      this.page = 1
      this.parentClassificationDataId = id || null
      this.getClassificationList()
    },
    loadClassificationListOnClear() {
      this.searchResult = false
      this.getClassificationList()
    },
    getClassificationListChild(item) {
      let { name, id } = item || {}
      this.page = 1
      this.breadCrumb.push({ name, id })
      this.parentClassificationDataId = id || null
      this.getClassificationList()
    },
    async getClassificationList() {
      let { clsfiModuleSearch, parentClassificationDataId } = this
      this.searchLoading = true
      let { page, perPage, fromModuleName } = this
      let search = clsfiModuleSearch || null
      let params = {
        page,
        perPage,
        search,
        parentClassificationId: !isEmpty(parentClassificationDataId)
          ? parentClassificationDataId
          : null,
        withCount: true,
        fetchAll: isEmpty(fromModuleName),
        hasChild: true,
        classificationResolvePath: !isEmpty(search),
      }
      if (!isEmpty(fromModuleName)) params = { ...params, fromModuleName }

      this.clsfiModuleList = await ClassificationListModel.fetchAll(params)
      this.count = ClassificationListModel.totalCount
      this.searchLoading = false
    },
    async saveSpecification() {
      let { clsfiModuleList, selectedClsfiModuleId, breadCrumb } = this
      let selectedModule = clsfiModuleList.find(
        cls => cls.id === selectedClsfiModuleId
      )
      if (this.isAttributeInherited) {
        await this.onSaveClassification(selectedModule)
      } else {
        this.$emit('onSave', selectedModule, breadCrumb)
      }
      this.closeDialog()
    },
    closeDialog() {
      this.$emit('onClose')
    },
    async onSaveClassification(item) {
      this.isSaving = true
      let classificationData = await ClassificationListModel.fetch({
        id: item.id,
      })
      let attributesList = classificationData.attributes
      this.$emit('specificationAttributes', attributesList)
      this.$emit('onSave', item, this.breadCrumb)

      this.isSaving = false
    },
  },
}
</script>
<style lang="scss">
.clsfi {
  .el-dialog {
    border-radius: 10px;
    .el-dialog__headerbtn {
      top: 20px;
    }
    .el-dialog__header {
      border-bottom: none;
      padding: 0px;
      .el-dialog__title {
        font-size: 18px;
        font-weight: 500;
      }
    }
    .el-dialog__body {
      padding: 0px 0px 0px 32px;
      color: #2f4058;
    }
  }
  .empty-state-classification {
    justify-content: center;
    align-items: center;
  }
  .clsfi-cancel-btn {
    border-bottom-left-radius: 10px;
  }
  .clsfi-save-btn {
    border-bottom-right-radius: 10px;
  }
  .clsfi-list {
    flex-grow: 1;
  }
}
.cls-list {
  height: 400px;
  overflow: scroll;
  .text-containter-content {
    padding: 13px 0px;
    .clsfi-path {
      color: #2f4058;
    }
  }
}

.clsfi-select {
  color: #3ab2c1;
}
.clsfi-arrow-light {
  opacity: 0.5;
}
.clsfi-arrow-border {
  border-radius: 2px;
  box-shadow: 0 0 4px 0 rgba(0, 0, 0, 0.1);
  border: solid 0.5px #d0d9e2;
  background-color: #fff;
}
.clsfi-title-border {
  border-bottom: 1px solid #eee;
}
.clsfi-title {
  height: 60px;
  padding: 21px 32px;
  .clsfi-title-text {
    font-size: 18px;
    font-weight: 500;
    text-transform: uppercase;
    letter-spacing: 0.42px;
    color: #1a1a1a;
  }
}
.parent-icon {
  width: 24px;
}
.search-result {
  font-size: 16px;
  font-weight: 500;
  padding: 20px 32px 10px;
  color: rgba(47, 64, 88, 0.5);
  letter-spacing: 0.5px;
}
.clsfi-border-none {
  border: solid 1px rgb(230, 224, 224);
}
.clsfi-search {
  .quick-search-input-specification {
    border: none !important;
    .el-input__inner {
      border-bottom: none !important;
      padding: 20px 20px 20px 50px;
    }
    .el-input__prefix {
      padding-left: 20px;
    }
    .el-input__suffix {
      padding-right: 20px;
    }
  }
  .breadcrumb-container:not(:last-child) {
    opacity: 0.5;
  }
}
</style>
