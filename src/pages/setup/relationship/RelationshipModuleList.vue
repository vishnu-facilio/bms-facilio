<template>
  <div class="relation-setup-container m30">
    <portal to="module-summary-actions" slim>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" class="setup-el-btn" @click="openForm">
          {{ $t('setup.relationship.button_name') }}
        </el-button>
      </div>
    </portal>

    <portal to="module-summary-pagenation">
      <div class="d-flex align-center">
        <template v-if="canShowSearch">
          <el-input
            v-if="showMainFieldSearch"
            v-model="searchText"
            ref="mainFieldSearchInput"
            class="fc-input-full-border2 width-auto mL-auto"
            clearable
            @change="handleSearch"
            @blur="hideMainFieldSearch"
            @clear="hideMainFieldSearch"
            :placeholder="$t('common._common.search')"
          ></el-input>
          <span v-else @click="openMainFieldSearch">
            <inline-svg
              src="svgs/search"
              class="vertical-middle cursor-pointer mT4 mR5"
              iconClass="icon icon-sm search-icon"
            ></inline-svg>
          </span>
        </template>
        <pagination
          v-if="showPagination"
          :hasMoreList="hasMoreList"
          :currentPage.sync="page"
          :perPage="perPage"
          :currentCount="currentListCount"
          class="mL10"
        ></pagination>
      </div>
    </portal>
    <div class="container-scroll pB0">
      <div v-if="loading" class="flex-middle height-100">
        <spinner :show="loading" size="80"></spinner>
      </div>

      <el-table
        v-else
        :data="relationlist"
        height="100%"
        :header-cell-style="{ background: '#f3f1fc' }"
        :empty-text="$t('setup.relationship.no_relation')"
        class="form-list-table "
      >
        <el-table-column
          prop="name"
          :label="$t('setup.relationship.name')"
          width="150"
        >
        </el-table-column>
        <el-table-column
          prop="relationType"
          :label="$t('setup.relationship.type')"
          width="150"
        >
          <template v-slot="relation">
            {{ relationTypeVsName[relation.row.relationType] }}
          </template>
        </el-table-column>
        <el-table-column
          prop="toModuleDisplayName"
          :label="$t('setup.relationship.related_to')"
          width="150"
        >
        </el-table-column>
        <el-table-column
          prop="relationName"
          :label="$t('setup.relationship.forward_relationship')"
          width="250"
        >
        </el-table-column>
        <el-table-column
          prop="reverseRelationName"
          :label="$t('setup.relationship.reverse_relationship')"
          width="250"
        >
        </el-table-column>
        <el-table-column fixed="right">
          <template v-slot="relationship">
            <div class=" d-flex">
              <i
                class="el-icon-edit pointer visibility-hide-actions mL10"
                @click="editRelation(relationship.row.id)"
              ></i>
              <i
                class="el-icon-delete pointer visibility-hide-actions mL10"
                @click="deleteRelation(relationship.row)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <RelationshipForm
        v-if="showRelationshipForm"
        :relationId="selectedRelationId"
        :relationTypeVsName="relationTypeVsName"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        @onSave="getRelationList"
        @onClose="showRelationshipForm = false"
      />

      <WarningWithDeteleDialog
        v-if="showWarningForDelete"
        :deleteRelationRecord="deleteRelationRecord"
        @onClose="showWarningForDelete = false"
      ></WarningWithDeteleDialog>

      <DeleteConfirmDialog
        v-if="showConfirmationForDelete"
        :validateDeteleRelation="validateDeteleRelation"
        :deleteRelationRecord="deleteRelationRecord"
        @openConfirmation="showWarningForDelete = true"
        @onClose="showConfirmationForDelete = false"
      ></DeleteConfirmDialog>
    </div>
  </div>
</template>
<script>
import RelationshipForm from './RelationshipForm'
import Pagination from 'src/newapp/components/FPagination.vue'
import WarningWithDeteleDialog from './WarningWithDeteleDialog.vue'
import DeleteConfirmDialog from './DeleteConfirmDialog.vue'
import { isEmpty } from '@facilio/utils/validation'
import { RelationShip } from './RelationshipModel'
import { API } from '@facilio/api'
import isEqual from 'lodash/isEqual'

export default {
  name: 'RelationshipModuleList',
  props: ['moduleName', 'moduleDisplayName'],
  components: {
    RelationshipForm,
    WarningWithDeteleDialog,
    DeleteConfirmDialog,
    Pagination,
  },
  data() {
    return {
      searchText: '',
      perPage: 10,
      showPagination: false,
      currentListCount: 0,
      hasMoreList: false,
      page: 1,
      showMainFieldSearch: false,
      loading: false,
      showRelationshipForm: false,
      selectedRelationId: null,
      relationlist: null,
      showWarningForDelete: false,
      seletedRecordForDelete: null,
      showConfirmationForDelete: false,
      relationTypeVsName: {
        1: 'One to One',
        2: 'One to Many',
        3: 'Many to One',
        4: 'Many to Many',
      },
    }
  },
  created() {
    this.getRelationList()
  },
  computed: {
    canShowSearch() {
      return (
        !isEmpty(this.relationlist) ||
        this.page !== 1 ||
        !isEmpty(this.searchText)
      )
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.getRelationList()
      }
    },
  },

  methods: {
    async getRelationList() {
      let { page, perPage, searchText: search, moduleName } = this
      try {
        let params = {
          moduleName,
          page,
          perPage,
          search: !isEmpty(search) ? search : null,
        }

        this.loading = true
        this.relationlist = await RelationShip.fetchAll(params)
        this.currentListCount = this.relationlist?.length || 0
        this.showPagination =
          this.currentListCount === perPage || this.page != 1
        this.hasMoreList = this.currentListCount === perPage
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Error Occured')
      } finally {
        this.loading = false
      }
    },
    async validateDeteleRelation() {
      let { relationModuleName } = this.seletedRecordForDelete || {}
      let url = 'v3/modules/data/count'
      let { error, data } = await API.get(url, {
        moduleName: relationModuleName,
      })

      if (error) {
        this.$message.error(
          error.message || this.$t('setup.relationship.invalid_module')
        )
        return false
      } else {
        let { count } = data || {}
        return isEmpty(count) || count <= 0
      }
    },
    async deleteRelationRecord() {
      let { id } = this.seletedRecordForDelete || {}
      try {
        await RelationShip.delete({ id })
        let idx = this.relationlist.findIndex(relation => relation.id === id)

        if (!isEmpty(idx)) {
          this.relationlist.splice(idx, 1)
        }
        this.$message.success(this.$t('setup.relationship.delete_success'))
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async deleteRelation({ id, relationModuleName }) {
      this.seletedRecordForDelete = { id, relationModuleName }
      this.showConfirmationForDelete = true
    },
    editRelation(id) {
      this.selectedRelationId = id
      this.showRelationshipForm = true
    },
    openForm() {
      this.showRelationshipForm = true
      this.selectedRelationId = null
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
      if (isEmpty(this.searchText)) this.showMainFieldSearch = false
    },
    handleSearch() {
      this.page = 1
      this.getRelationList()
    },
  },
}
</script>
<style lang="scss">
.relation-setup-container {
  .form-list-table .el-table__cell {
    padding-left: 20px;
  }
}
</style>
