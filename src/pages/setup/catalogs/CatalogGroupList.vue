<template>
  <el-dialog
    :title="$t('servicecatalog.setup.service_categories')"
    :visible.sync="canShowDialog"
    width="50%"
    class="fc-dialog-center-container service-categories"
    :append-to-body="true"
  >
    <div class="height550 overflow-auto">
      <div class="container-scroll catalog-setup-container">
        <div class="row setting-Rlayout">
          <div class="col-lg-12 col-md-12 overflow-x">
            <table class="setting-list-view-table">
              <thead>
                <tr>
                  <th
                    class="setting-table-th setting-th-text"
                    style="width: 25%"
                  >
                    {{ $t('common._common.name') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('common.wo_report.report_description') }}
                  </th>
                  <th class="setting-table-th setting-th-text"></th>
                </tr>
              </thead>
              <tbody v-if="catalogGroups.length === 0">
                <tr>
                  <td colspan="100%" class="text-center">
                    {{ $t('servicecatalog.setup.emptytext_category') }}
                  </td>
                </tr>
              </tbody>
              <tbody v-else>
                <tr
                  class="tablerow visibility-visible-actions "
                  v-for="(catalogGroup, index) in catalogGroups"
                  :key="index"
                >
                  <td>
                    <div>
                      <div class="mL10">
                        <div class="label-txt3-14">
                          {{ catalogGroup.name }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div class="label-txt3-14">
                      {{ catalogGroup.description }}
                    </div>
                  </td>
                  <td class="pR0" style="width: 20%">
                    <div
                      class="text-left actions text-center mL20"
                      style="margin-top:-3px;"
                    >
                      <i
                        class="el-icon-edit pointer"
                        @click="editCatalogGroup(catalogGroup)"
                      ></i>
                      &nbsp;&nbsp;
                      <i
                        class="el-icon-delete pointer"
                        @click="showConfirmDelete(catalogGroup)"
                      ></i>
                      &nbsp;&nbsp;
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['canShowGroupList', 'catalogGroups'],
  data() {
    return {}
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canShowGroupList
      },
      set(value) {
        this.$emit('update:canShowGroupList', value)
      },
    },
  },
  methods: {
    editCatalogGroup(catalogGroup) {
      this.$emit('openCreationDialog', catalogGroup, 'canAddCatalogGroup', true)
    },
    showConfirmDelete(catalogGroup) {
      let dialogObj = {
        title: 'Delete Service Catalog Group',
        message: 'Are you sure you want to delete this service category?',
        rbDanger: true,
        rbLabel: 'Delete',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteServiceCategory(catalogGroup)
        }
      })
    },
    deleteServiceCategory({ id }) {
      let url = `v2/servicecataloggroup/delete`
      let data = {
        id,
      }
      this.$http
        .post(url, data)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode === 0) {
            let serviceCategories = this.catalogGroups.filter(
              item => item.id !== id
            )
            this.$set(this, 'catalogGroups', serviceCategories)
            this.$message.success('Successfully Deleted')
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
  },
}
</script>
<style lang="scss">
.service-categories {
  .el-dialog__body {
    padding: 0px;
    .container-scroll {
      position: relative;
      height: calc(100vh - 100px);
      width: 100%;
      overflow: scroll;
      padding-bottom: 0px;
      .setting-Rlayout {
        padding: 0px !important;
      }
    }
  }
}
</style>
