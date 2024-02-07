<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('setup.setup.enpi') }}</div>
        <div class="heading-description">{{ $t('setup.list.list_enpi') }}</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" class="setup-el-btn" @click="showNewEnpi">
          {{ $t('setup.add.add_enpi') }}
        </el-button>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <table class="setting-list-view-table">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">
                {{ $t('common.roles.name') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('maintenance.wr_list.space_asset') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('space.sites.site_freq') }}
              </th>
              <th class="setting-table-th setting-th-text"></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else-if="!enpilist.length">
            <tr>
              <td colspan="100%" class="text-center">
                {{ $t('setup.empty.empty_enpi') }}
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow"
              v-for="(enpi, index) in enpilist"
              :key="index"
              v-loading="loading"
            >
              <td>{{ enpi.name }}</td>
              <td>{{ getResourceName(enpi) }}</td>
              <td>
                {{ enpi.frequency ? frequencyTypes[enpi.frequency] : '---' }}
              </td>
              <td>
                <div
                  class="text-left actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i
                    class="el-icon-edit pointer"
                    @click="showEditEnpi(enpi)"
                  ></i>
                  &nbsp;&nbsp;
                  <i
                    class="el-icon-delete pointer"
                    @click="deleteEnpi(enpi.id, index)"
                  ></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <new-enpi
      v-if="loadForm"
      @saved="save"
      :isNew="isNew"
      :visibility.sync="loadForm"
      :enpi="selectedEnpi"
    ></new-enpi>
  </div>
</template>
<script>
import NewEnpi from 'pages/setup/new/NewEnPI'
export default {
  components: {
    NewEnpi,
  },
  title() {
    return 'ENPI'
  },
  data() {
    return {
      loading: true,
      enpilist: [],
      selectedEnpi: {},
      loadForm: false,
      isNew: false,
      rule: null,
    }
  },
  computed: {
    frequencyTypes() {
      let types = this.$helpers.cloneObject(this.$constants.FACILIO_FREQUENCY)
      types['8'] = 'Hourly'
      return types
    },
    assetCategoryList() {
      return this.$store.state.assetCategory
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {
    this.loadEnpi()
  },
  methods: {
    save() {
      // this.$refs.createNewEnpi.close()
      this.loadEnpi()
      this.showDialog = true
    },
    loadEnpi() {
      let self = this
      self.$http
        .get('/reading/allformulas?type=1')
        .then(function(response) {
          self.loading = false
          if (response.status === 200) {
            self.enpilist =
              response.data && response.data.formulaList
                ? response.data.formulaList
                : []
            let fieldVsRules = response.data.fieldVsRules
            if (fieldVsRules && self.enpilist && self.enpilist.length) {
              for (let i = 0; i < self.enpilist.length; i++) {
                if (self.enpilist[i].readingField) {
                  self.enpilist[i].readingField.readingRules =
                    fieldVsRules[self.enpilist[i].readingFieldId] || []
                }
              }
            }
          }
        })
        .catch(function(error) {
          self.loading = false
        })
    },
    showNewEnpi() {
      this.loadForm = true
      this.isNew = true
      this.selectedEnpi = {}
      // this.$refs.createNewEnpi.open()
    },
    showEditEnpi(enpi) {
      this.loadForm = true
      this.selectedEnpi = this.$helpers.cloneObject(enpi)
      this.isNew = false
      this.showDialog = true
      // this.$refs.createNewEnpi.open()
    },
    // closeForm () {
    //   this.$refs.createNewEnpi.close()
    // },
    deleteEnpi(id, idx) {
      this.$dialog
        .confirm({
          title: this.$t('setup.delete.delete_enpi'),
          message: this.$t('setup.delete.delete_enpi_confirm'),
          rbDanger: true,
          rbLabel: this.$t('setup.users_management.delete'),
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/reading/deleteformula', { id: id })
              .then(response => {
                if (
                  response.status === 200 &&
                  typeof response.data === 'object'
                ) {
                  this.$message.success(
                    this.$t('setup.delete.delete_enpi_success')
                  )
                  this.enpilist.splice(idx, 1)
                } else {
                  this.$message.error(
                    this.$t('setup.delete.delete_enpi_failed')
                  )
                }
              })
          }
        })
    },
    getResourceName(enpi) {
      if (enpi.matchedResourcesIds && enpi.matchedResourcesIds.length) {
        if (enpi.assetCategoryId > 0) {
          let message
          let selectedCount
          if (enpi.includedResources && enpi.includedResources.length) {
            selectedCount = enpi.includedResources.length
          }

          let categoryName = this.getCategoryName(enpi.assetCategoryId)
          if (selectedCount) {
            if (selectedCount === 1) {
              return enpi.matchedResources[0].name
            }
            message = selectedCount + ' ' + categoryName + 's'
          } else {
            message = 'All ' + categoryName + 's'
          }
          return message
        }
        let firstResource =
          enpi.matchedResources[Object.keys(enpi.matchedResources)[0]]
        if (enpi.matchedResources.length === 1) {
          return firstResource.name
        }
        return firstResource.resourceType === 1 ? 'Some Spaces' : 'Some Assets'
      }
      return '---'
    },
    getCategoryName(categoryId) {
      if (categoryId > 0 && this.assetCategoryList) {
        let category = this.assetCategoryList.find(
          category => category.id === categoryId
        )
        if (category) {
          return category.name
        }
      }
    },
  },
}
</script>
