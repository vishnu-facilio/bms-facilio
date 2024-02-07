<template>
  <div class="form-validation-list">
    <portal to="builder-btn-portal" slim>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          @click="openFormRuleCreation()"
        >
          {{ $t('setup.form_builder.validation.add_form_Validation') }}
        </el-button>
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          @click="redirect"
          >{{ $t('common._common.done') }}</el-button
        >
      </div>
    </portal>
    <portal to="builder-action-config" slim>
      <div class="flex-center-row-space line-height24">
        <el-input
          v-if="showMainFieldSearch"
          v-model="validationSearchData"
          ref="mainFieldSearchInput"
          class="fc-input-full-border2 width150px form-builder-validation-main-field-search"
          clearable
          autofocus
          @blur="hideMainFieldSearch"
          @clear="hideMainFieldSearch"
          @change="loadValidationList"
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
          v-if="showPagination"
          :hasMoreList="hasMoreList"
          :currentPage.sync="page"
          :perPage="perPage"
          :currentCount="currentListCount"
          class="mL10"
        ></Pagination>
      </div>
    </portal>
    <div class="validation-table-container m20">
      <div v-if="isLoading" class="flex-middle height-100">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <el-table
        v-else
        :data="formValidationList"
        height="100%"
        empty-text="No Validation Rules available"
        class="form-list-table overflow-y-scroll"
      >
        <el-table-column prop="name" label="Name" width="500">
        </el-table-column>
        <el-table-column
          prop="namedCriteriaDisplayName"
          label="Criteria Name"
          width="500"
        >
        </el-table-column>
        <el-table-column>
          <template v-slot="formValidation">
            <div class="hover-icons" style="margin-top:-3px;">
              <i
                class="el-icon-edit pointer"
                @click="openFormRuleCreation(formValidation.row.id)"
              ></i>
              &nbsp;&nbsp;
              <i
                class="el-icon-delete pointer"
                @click="showConfirmDelete(formValidation.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <RuleCreation
      v-if="openValidationDialog"
      :moduleName="moduleName"
      :selectedId="selectedId"
      :formId="formId"
      @onClose="openValidationDialog = false"
      @onSave="loadValidationList"
    ></RuleCreation>
  </div>
</template>
<script>
import RuleCreation from 'pages/setup/form-validation/FormValidationCreation'
import Pagination from 'src/newapp/components/FPagination'
import { FormValidation } from './FormValidationModel'
import { isFunction, isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleName', 'formId', 'redirectToFormsList'],
  components: { RuleCreation, Pagination },
  data() {
    return {
      isLoading: false,
      showConfirmationForDelete: false,
      openValidationDialog: false,
      showMainFieldSearch: false,
      formValidationList: [],
      selectedId: null,
      validationSearchData: '',
      page: 1,
      perPage: 10,
      showPagination: false,
      currentListCount: 0,
      hasMoreList: false,
    }
  },
  created() {
    this.loadValidationList()
  },
  watch: {
    page: {
      handler: 'loadValidationList',
    },
  },
  methods: {
    openFormRuleCreation(value = null) {
      this.selectedId = value
      this.openValidationDialog = true
    },
    redirect() {
      if (isFunction(this.redirectToFormsList)) {
        this.redirectToFormsList()
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
    hideMainFieldSearch() {
      if (isEmpty(this.validationSearchData)) this.showMainFieldSearch = false
    },
    async showConfirmDelete(id) {
      let value = await this.$dialog.confirm({
        title: this.$t('setup.form_builder.validation.delete_validation'),
        htmlMessage: this.$t(
          'setup.form_builder.validation.are_you_want_delete'
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.confirm'),
      })
      if (!value) return
      try {
        await FormValidation.delete({ id })
        let idx = this.formValidationList.findIndex(
          validation => validation.id === id
        )

        if (idx !== -1) {
          this.formValidationList.splice(idx, 1)
        }
        this.$message.success(
          this.$t('setup.form_builder.validation.delete_success')
        )
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async loadValidationList() {
      let { formId, page, perPage } = this
      try {
        this.isLoading = true
        let params = {
          formId,
          page,
          perPage,
          search: this.validationSearchData,
        }
        this.formValidationList = await FormValidation.fetchAll(params)
        this.currentListCount = this.formValidationList?.length || 0
        this.showPagination =
          this.currentListCount === perPage || this.page != 1
        this.hasMoreList = this.currentListCount === perPage
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.isLoading = false
    },
  },
}
</script>
<style lang="scss">
.form-validation-list {
  .body-row-cell {
    border-top: none;
    border-left: none;
    border-right: none;
    color: #333;
    font-size: 14px;
    border-collapse: separate;
    padding: 15px 30px;
    letter-spacing: 0.6px;
    font-weight: 400;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .lines {
    height: 15px;
    width: 90%;
    margin: 0px 20px 0px 20px;
    border-radius: 5px;
  }
  tr.el-table__row:hover .hover-icons {
    display: block !important;
  }
  .hover-icons {
    display: none;
  }
  .form-list-table .el-table__cell {
    padding-left: 20px;
  }
  .validation-table-container {
    margin: 20px;
    height: calc(100vh - 150px);
    background: #fff;
  }
}
.form-builder-validation-main-field-search {
  margin-top: -5px;

  .el-input__inner {
    height: 30px !important;
  }
  .el-input__suffix {
    margin-top: -4px;
  }
}
</style>
