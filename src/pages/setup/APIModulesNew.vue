<template>
  <div>
    <div
      class="fc-portal-inner-summary-header flex-middle justify-content-space"
    >
      <div class="fc-black2-18 text-left bold">
        {{ $t('common._common.apimodules') }}
      </div>
      <!-- <div class="flex-middle">
        <pagination
          ref="pagination"
          :total="usersCount"
          :perPage="perPage"
          :currentPage.sync="page"
          class="flex-middle justify-content-end p0 pL10"
        >
        </pagination>
      </div> -->
    </div>
    <div class="occupantPortal-tab">
      <SetupLoader class="m10 width98" v-if="loading">
        <template #setupLoading>
          <spinner :show="loading" size="80"></spinner>
        </template>
      </SetupLoader>
      <setup-empty
        v-else-if="$validation.isEmpty(modulelist) && !loading"
        class="m10 width98"
      >
        <template #emptyImage>
          <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
        </template>
        <template #emptyHeading>
          {{ $t('setup.empty.empty_modules') }}
        </template>
      </setup-empty>
      <div class="occupantPortal-tabrow" v-else>
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table" width="100%">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.approvalprocess.name') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                class="tablerow visibility-visible-actions"
                v-for="(mod, index) in modulelist"
                :key="index"
              >
                <td>
                  {{ mod.name }}
                </td>
                <td class="pL0 pR0 nowrap">
                  <i
                    class="visibility-hide-actions el-icon-delete fc-setup-list-delete"
                    @click="removeModule(mod)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <portal to="header-buttons">
      <div class="position-relative">
        <div style="height: 42px;">
          <el-button type="primary" class="setup-el-btn" @click="addModule()">
            {{ $t('common._common.add_module') }}
          </el-button>
        </div>
      </div>
    </portal>
    <new-api-module
      v-if="showNewApiModule"
      :appId="appId"
      @onClose="onCloseAddMod"
    />
  </div>
</template>
<script>
import ApiModules from './APIModules'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import { isEmpty } from '@facilio/utils/validation'
import { loadAppTabs } from 'util/webtabUtil'
import { API } from '@facilio/api'
import NewApiModule from './NewAPIModule'
export default {
  props: ['appId'],
  components: {
    Pagination,
    SetupLoader,
    SetupEmpty,
    ApiModules,
    NewApiModule,
  },
  data() {
    return {
      loading: false,
      perPage: 50,
      page: 1,
      userCount: null,
      showNewApiModule: false,
      modulelist: [],
    }
  },
  async created() {
    await this.loadSelectedModules()
  },
  methods: {
    addModule() {
      this.showNewApiModule = true
    },
    onCloseAddMod() {
      this.showNewApiModule = false
      this.loadSelectedModules()
    },
    async removeModule({ id }) {
      let dialogObj = {
        title: this.$t('common.wo_report.delete_module_title'),
        htmlMessage: this.$t(
          'common.wo_report.are_you_sure_want_to_delete_this_module'
        ),
        rbDanger: true,
        rbLabel: this.$t('common.login_expiry.rbLabel'),
      }

      let value = await this.$dialog.confirm(dialogObj)
      if (value) {
        this.loading = true
        let { error } = await API.post('/v2/tab/delete', { id })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let index = this.modulelist.findIndex(mod => mod.id === id)

          if (!isEmpty(index)) {
            this.modulelist.splice(index, 1)
          }
          this.$message.success(
            this.$t('common._common.module_deleted_success')
          )
          await this.loadSelectedModules()
        }
        this.loading = false
      }
    },
    async loadSelectedModules() {
      this.loading = true
      let { error, data } = await loadAppTabs(this.appId)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.modulelist = data
      }
      this.loading = false
    },
  },
}
</script>
