<template>
  <div class="mT20">
    <template>
      <div v-if="loading">
        <spinner
          :show="loading"
          size="80"
          class="flex-middle height450"
        ></spinner>
      </div>
      <div
        v-else
        class="occupantPortal-tabrow setting-Rlayout pL0 pR0 mT20 pB40"
      >
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
            <tbody v-if="$validation.isEmpty(modulelist)">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('setup.setup.no_modules') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow visibility-visible-actions"
                v-for="(mod, index) in modulelist"
                :key="index"
              >
                <td>{{ mod.name }}</td>
                <td class="d-flex pT20">
                  <i
                    class="visibility-hide-actions el-icon-delete pL10"
                    @click="removeMod(mod)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { loadAppTabs } from 'util/webtabUtil'
import { API } from '@facilio/api'
export default {
  props: ['appId'],
  data() {
    return {
      modulelist: null,
      loading: false,
    }
  },
  async created() {
    await this.loadSelectedModules()
  },
  methods: {
    async removeMod({ id }) {
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
