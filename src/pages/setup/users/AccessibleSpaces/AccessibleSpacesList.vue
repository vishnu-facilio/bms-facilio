<template>
  <div>
    <SetupLoader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </SetupLoader>
    <div v-else>
      <div class="fc-setup-actions-con">
        <div class="flex-middle">
          <div class="fc-black-15 fwBold">
            {{ $t('setup.setupLabel.accessible_spaces') }}
          </div>
        </div>
        <div class="flex-middle" style="height:30px">
          <pagination :total="totalCount" :perPage="20" class="nowrap">
          </pagination>
          <div class="flex-middle position-relative mR20">
            <i
              class="el-icon-search pointer fw6 fc-black3-16"
              @click="toggleSearch"
              v-if="searchIcon"
            ></i>
            <div v-else class="flex-middle">
              <el-input
                placeholder="Search"
                v-model="searchData"
                @change="accessibleSpacesSearch"
                class="fc-input-full-border2"
              ></el-input>
              <div>
                <i
                  class="el-icon-close fc-close-icon-search pointer"
                  @click="toggleSearch"
                ></i>
              </div>
            </div>
          </div>
          <div>
            <el-button
              class="fc-setup-green-inner-btn"
              @click="addAccessible"
              >{{ $t('setup.setupLabel.add_accessible_spaces') }}</el-button
            >
          </div>
        </div>
      </div>
      <div v-if="!$validation.isEmpty(selectedRecords)" class="pop-over-head ">
        <span>
          <button
            class="btn btn--tertiary wo-table-btn-spacing "
            @click="deleteAccessible(selectedRecords)"
            :class="{ disabled: loading }"
          >
            <inline-svg
              src="svgs/delete"
              iconClass="icon vertical-middle icon-sm fill-grey2"
            ></inline-svg>
            {{ $t('common._common.delete') }}
          </button>
        </span>
      </div>
      <el-table
        :data="accessibleList"
        style="width: 100%"
        height="calc(100vh - 330px)"
        class="fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
        :fit="true"
        @selection-change="selectAccessibleSpace"
      >
        <el-table-column type="selection" width="60"> </el-table-column>
        <template slot="empty">
          {{ $t('setup.users_management.all_sites_accessible') }}
        </template>
        <el-table-column prop="id" label="id" width="120">
          <template v-slot="space">
            <div class="fc-id f14">#{{ space.row.id }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="name">
          <template v-slot="space">
            {{ space.row.name }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="Space Type">
          <template v-slot="space">
            {{ space.row.spaceTypeVal }}
          </template>
        </el-table-column>
        <el-table-column prop="site" label="Site">
          <template v-slot="space">
            <div>
              {{
                $getProperty(
                  $store.getters.getSite(space.row.siteId),
                  'name',
                  '---'
                )
              }}
            </div>
          </template>
        </el-table-column>
        <el-table-column class="visibility-visible-actions">
          <template v-slot="space">
            <div class="pointer flex-middle">
              <div @click="deleteAccessible([space.row.id])">
                <i
                  class="el-icon-delete visibility-hide-actions f14"
                  data-arrow="true"
                  :title="$t('setup.delete.delete_accessible_btn')"
                  v-tippy
                ></i>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <AccessibleForm
      v-if="showAddAccessible"
      :delegateData="selectedAccessible"
      :isNew="isNew"
      @onSave="accessibleListLoad"
      @onClose="showAddAccessible = false"
    >
    </AccessibleForm>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import AccessibleForm from 'pages/setup/users/AccessibleSpaces/AccessibleSpacesForm'
import SetupLoader from 'pages/setup/components/SetupLoader'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import Pagination from 'src/components/list/FPagination'
export default {
  props: ['accessibleSpace'],
  data() {
    return {
      loading: true,
      accessibleList: [],
      showAddAccessible: false,
      isNew: false,
      selectedAccessible: null,
      searchIcon: true,
      totalCount: 0,
      searchData: '',
      selectedRecords: [],
    }
  },
  computed: {
    summaryId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    page() {
      let { query } = this.$route || {}
      let { page } = query || {}
      return page || 1
    },
  },
  components: {
    AccessibleForm,
    SetupLoader,
    Pagination,
  },
  watch: {
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.accessibleListLoad()
      }
    },
  },
  created() {
    this.accessibleListLoad()
  },
  methods: {
    async accessibleListLoad() {
      let { summaryId, page } = this
      this.loading = true
      let params = {
        ouId: summaryId,
        page,
        perPage: 20,
        withCount: true,
      }
      if (this.searchData) {
        this.$set(params, 'search', this.searchData)
      }
      let { error, data } = await API.post('/v3/accessiblespace/list', params)
      this.totalCount = this.$getProperty(data, 'count', null)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.accessibleList = (data.accessibleSpace || []).filter(
          item => !isEmpty(item)
        )
      }
      this.loading = false
    },
    addAccessible() {
      this.showAddAccessible = true
      this.isNew = true
      this.selectedAccessible = null
    },
    deleteAccessible(accessibleid) {
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_accessible'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_accessible'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async value => {
          if (!value) return
          let { error } = await API.post('v3/accessiblespace/delete', {
            spaceIds: accessibleid,
            ouId: this.summaryId,
          })
          this.selectedRecords = []
          if (error) {
            this.$message.error(
              error.message || this.$t('setup.delete.delete_accessible_failed')
            )
          } else {
            this.$message.success(
              this.$t('setup.delete.delete_accessible_space')
            )
            this.accessibleListLoad()
          }
        })
    },
    accessibleSpacesSearch() {
      if (this.page !== 1) this.$router.push({ query: {} })
      this.accessibleListLoad()
    },
    toggleSearch() {
      this.searchIcon = !this.searchIcon
    },
    selectAccessibleSpace(selectedAS) {
      this.selectedRecords = selectedAS.map(value => value.id)
    },
  },
}
</script>
<style scoped>
.pop-over-head {
  height: 22px;
  margin: 9px 0px;
  position: absolute;
  z-index: 1343;
  left: 70px;
  background: #fff;
  width: 90.9%;
  padding-top: 0px;
  float: left;
}
</style>
