<template>
  <el-dialog
    :visible="true"
    width="35%"
    custom-class="fc-dialofc-dialog-center-container scale-up-center"
    class="attribute-list-dialog"
    :lock-scroll="false"
    :show-close="false"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <template slot="title">
      <div class="d-flex justify-between header-content-attribute fw6">
        <div class="attribute-add-header">
          {{ $t('setup.classification.attributes.add_attributes') }}
        </div>
        <div
          class="d-flex attribute-search-page-container attribute-search-page-items"
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
            @change="getAttributeList"
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
            :perPage="perPage"
            :total="count"
            :currentPage.sync="page"
            class="mL10"
          ></Pagination>
          <i
            class="el-dialog__close el-icon el-icon-close"
            @click="closeDialog"
          ></i>
        </div>
      </div>
    </template>
    <div v-if="isLoading" class="flex-middle height100 attributes-list ">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else class="attributes-list">
      <div
        v-if="!$validation.isEmpty(selectedItem)"
        class="pL20 pT10 pR20 fc-black-color letter-spacing0_5 f10 bold "
      >
        <div class="mR10 pB5 mT5 selected-list-sub">
          {{ $t('setup.classification.attributes.selected_attributes') }}
        </div>
        <div class="d-flex items-baseline flex-direction-row flex-wrap">
          <div
            v-for="(item, index) in selectedList"
            :key="`${item.name}-index-${index}`"
            class="action-attr-badge mR10 mb5 "
          >
            {{ item.name }}
            <i class="el-icon-close pointer" @click="handleClose(item.id)"></i>
          </div>
          <div v-if="!$validation.isEmpty(popupList)">
            <el-dropdown>
              <div class="pop-list pointer">
                {{ popupLength }}
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(item, index) in popupList"
                  :key="`${item.name}-popupList-${index}`"
                  ><div class="d-flex justify-between">
                    <div>
                      {{ item.name }}
                    </div>
                    <div>
                      <i
                        class="el-icon-close pointer pL20"
                        @click="handleClose(item.id)"
                      ></i>
                    </div></div
                ></el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
      <div
        :class="
          !$validation.isEmpty(selectedItem)
            ? 'attribute-table-height'
            : 'height100'
        "
      >
        <el-table
          :data="filteredAttributeList"
          :empty-text="
            $t('setup.classification.attributes.empty_state_classification')
          "
          height="100%"
          class="form-list-attribute"
          ref="table"
          @selection-change="handleSelection"
        >
          <el-table-column type="selection" width="70"> </el-table-column>
          <el-table-column prop="name" label="Name"></el-table-column>
          <el-table-column
            prop="fieldTypeDisplayName"
            label="Field Type"
          ></el-table-column>
        </el-table>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button
        @click="closeDialog"
        class="modal-btn-cancel clsfi-cancel-btn"
        >{{ $t('setup.form_builder.validation.cancel') }}</el-button
      >
      <el-button
        type="primary"
        class="modal-btn-save clsfi-save-btn"
        @click="onSave"
        >{{ $t('setup.form_builder.validation.save') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { AttributeListModel } from 'pages/setup/classification/AttributeModal'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'

export default {
  props: ['selectedAttributeList', 'classificationId'],
  components: { Pagination },
  data() {
    return {
      attributesList: [],
      showMainFieldSearch: false,
      page: 1,
      perPage: 10,
      count: null,
      attributeSearchData: null,
      selectedItem: [],
      isLoading: false,
      dataTypes: {
        1: 'String',
        2: 'Number',
        3: 'Decimal',
        4: 'Boolean',
        6: 'DateTime',
      },
      selectedItemId: [],
    }
  },
  async created() {
    await this.initSelectedItems()
    await this.getAttributeList()
  },
  computed: {
    filteredAttributeList() {
      if (!isEmpty(this.attributesList)) {
        return this.attributesList.map(item => {
          return {
            ...item,
            fieldTypeDisplayName: this.dataTypes[item.fieldType],
          }
        })
      }
      return []
    },
    popupLength() {
      let { popupList } = this
      return isEmpty(popupList) ? '' : `+${popupList.length}`
    },
    popupList() {
      return this.selectedItem.slice(2, this.selectedItem.length)
    },
    selectedList() {
      return this.selectedItem.slice(0, 2)
    },
  },
  watch: {
    async page() {
      await this.getAttributeList()
    },
  },
  methods: {
    async getAttributeList() {
      this.isLoading = true
      let { perPage, page, classificationId } = this
      let params = {
        search: this.attributeSearchData,
        ignoreClassificationId: classificationId,
        page,
        perPage,
        withCount: true,
      }
      if (!isEmpty(params.search)) this.page = 1

      this.attributesList = await AttributeListModel.fetchAll(params)
      this.count = AttributeListModel.totalCount
      this.isLoading = false
      this.onDataLoad()
    },
    initSelectedItems() {
      if (!isEmpty(this.selectedAttributeList)) {
        this.selectedItem = this.selectedAttributeList
        this.selectedItemId = this.selectedItem.map(item => item.id)
      }
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
    handleSelection(item) {
      let currentAttributeListIds = this.attributesList.map(attr => attr.id)
      let nonCurrentAttributes = this.selectedItem.filter(
        attr => !currentAttributeListIds.includes(attr.id)
      )

      this.selectedItem = [...nonCurrentAttributes, ...item]
      this.selectedItemId = this.selectedItem.map(item => item.id)
    },
    handleClose(id) {
      let index = this.selectedItem.findIndex(list => list.id === id)

      this.selectedItem.splice(index, 1)
      this.selectedItemId = this.selectedItem.map(item => item.id)
      this.onDataLoad()
    },
    onDataLoad() {
      this.$nextTick(() => {
        let currentSelectedList = this.filteredAttributeList.filter(list =>
          this.selectedItemId.find(item => item === list.id)
        )
        let ref = this.$refs.table

        this.$refs.table.clearSelection()

        if (!isEmpty(ref)) {
          currentSelectedList.forEach(item =>
            ref.toggleRowSelection(item, true)
          )
        }
      })
    },
    hideMainFieldSearch() {
      if (isEmpty(this.attributeSearchData)) this.showMainFieldSearch = false
    },
    closeDialog() {
      this.$emit('onClose')
    },
    onSave() {
      this.$emit('onSave', this.selectedItem)
      this.closeDialog()
    },
  },
}
</script>
<style lang="scss">
.attribute-list-dialog {
  .el-dialog {
    border-radius: 10px;
    .el-dialog__header {
      line-height: 40px;
      padding: 0px;
    }
    .el-dialog__body {
      padding: 0px 0px 60px 0px;
    }
  }
  .el-dropdown-menu__item:hover {
    background-color: #fafafa !important;
  }
  .header-content-attribute {
    padding: 9px 24px;
    border-bottom: solid 0.5px #d0d9e2;
    align-items: center;
    .attribute-search-page-items {
      align-items: center;
    }
    .attribute-add-header {
      text-transform: uppercase;
    }
  }
  .clsfi-cancel-btn {
    border-bottom-left-radius: 10px;
  }
  .clsfi-save-btn {
    border-bottom-right-radius: 10px;
  }
  .form-list-attribute {
    border-top: 1px solid #ebeef5;
    .el-table__cell {
      padding-left: 20px;
    }
  }
  .attributes-list {
    height: 350px;
  }
  .pop-list {
    font-size: 10px;
    border: solid 1px #3ab2c1;
    border-radius: 12px;
    background-color: #3ab2c1;
    padding: 4px 10px;
    color: #fff;
    .el-dropdown-menu__item {
      cursor: default;
    }
  }
  .selected-list-sub {
    letter-spacing: 0.19px;
    color: #324056;
    opacity: 0.6;
  }
  .attribute-table-height {
    height: calc(100% - 70px);
  }
  .action-attr-badge {
    border-radius: 12px;
    border: solid 1px #39b2c2;
    padding: 5px 10px;
  }
}
</style>
