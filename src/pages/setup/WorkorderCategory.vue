<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Work Order Category</div>
        <div class="heading-description">List of all Category</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click=";(showDialog = true), (isNew = true)"
          class="setup-el-btn"
          >Add Category</el-button
        >
        <new-workorder-category
          v-if="showDialog"
          :visibility.sync="showDialog"
          :category="rule"
          :isNew="isNew"
        ></new-workorder-category>
      </div>
    </div>
    <subheader
      :menu="subheaderMenu"
      parent="app/setup/workordersettings/"
      maxVisibleMenu="5"
    ></subheader>
    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table mT70">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">CATEGORY</th>
                <th class="setting-table-th setting-th-text">DESCRIPTION</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                class="tablerow"
                v-for="(category, index) in ticketCategory"
                :key="index"
              >
                <td style="padding-top: 18px;padding-bottom: 18px;">
                  {{ category.name }}
                </td>
                <td>
                  {{ category.description }}
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editCategory(category)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteGroup(category, index)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewWorkorderCategory from '@/NewWorkorderCategory'
import Subheader from '@/Subheader'
// import Constants from 'util/constant'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import Data from './SetupWorkorderMixin'

export default {
  components: {
    NewWorkorderCategory,
    Subheader,
  },
  mixins: [Data],
  data() {
    // let subheaderMenu = Constants.WORKORDER_SUB_HEADER_MENU
    return {
      // subheaderMenu,
      isNew: false,
      showDialog: false,
      rule: null,
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
  },

  computed: {
    ...mapState({
      ticketCategory: state => state.ticketCategory,
    }),
  },

  methods: {
    editCategory(rule) {
      this.isNew = false
      this.rule = this.$helpers.cloneObject(rule)
      this.showDialog = true
    },
    deleteGroup(category, index) {
      this.$dialog
        .confirm({
          title: 'Delete Category',
          message: 'Are you sure you want to delete this category?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            let ticketCategory = {
              id: category.id,
            }
            API.post('/picklist/deleteTicketCategory', { ticketCategory }).then(
              ({ error }) => {
                if (error) {
                  this.$message.error(error.message || 'Error Occured')
                } else {
                  this.$dialog.notify('Successfully deleted the category')
                  this.$store.commit('GENERIC_DELETE', {
                    type: 'ticketCategory',
                    matches: index,
                  })
                }
              }
            )
          }
        })
    },
  },
}
</script>
<style>
.fc-create-record {
  width: 40% !important;
  height: 100% !important;
  max-height: 100% !important;
}
.user-layout .el-icon-close:before {
  content: '';
}
.more-actions:hover {
  background-color: #fafbfc;
  cursor: pointer;
}
.setting-header .add-btn {
  position: fixed;
  right: 0;
  z-index: 111;
}
.user-layout .setting-page-btn {
  top: 7.5rem !important;
}
.user-layout .setting-Rlayout {
  padding: 1rem 1.7rem !important;
}
.add-btn {
  position: relative;
  right: 21px;
  top: -47px;
  z-index: 1111;
}
</style>
