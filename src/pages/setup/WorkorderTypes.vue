<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Work Order Types</div>
        <div class="heading-description">List of all Types</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click=";(showDialog = true), (isNew = true)"
          class="setup-el-btn"
          >Add Type</el-button
        >
        <new-workorder-type
          v-if="showDialog"
          @reload="reloadfunc"
          :isNew="isNew"
          :type="selectedType"
          @onsave="newTypeAdded"
          :visibility.sync="showDialog"
        ></new-workorder-type>
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
                <th class="setting-table-th setting-th-text">TYPE</th>
                <th class="setting-table-th setting-th-text">DESCRIPTION</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                class="tablerow"
                v-for="(type, index) in ticketType"
                :key="index"
              >
                <td style="padding-top: 18px;padding-bottom: 18px;">
                  {{ type.name }}
                </td>
                <td style="padding-top: 18px;padding-bottom: 18px;">
                  {{ type.description }}
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editType(type, index)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteGroup(type)"
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
import NewWorkorderType from '@/NewWorkorderType'
import Subheader from '@/Subheader'
// import Constants from 'util/constant'
import { mapState } from 'vuex'
import Data from './SetupWorkorderMixin'

export default {
  components: {
    NewWorkorderType,
    Subheader,
  },
  mixins: [Data],
  data() {
    // let subheaderMenu = Constants.WORKORDER_SUB_HEADER_MENU
    return {
      // subheaderMenu,
      selectedType: null,
      isNew: false,
      loadForm: false,
      showDialog: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketType')
  },

  computed: {
    ...mapState({
      ticketType: state => state.ticketType,
    }),
  },

  methods: {
    reloadfunc(response) {
      self.$store.commit('GENERIC_ADD_OR_UPDATE', {
        type: 'ticketType',
        data: response,
      })
    },
    newType() {
      this.selectedType = null
      this.isNew = true
      this.loadForm = true
      this.showDialog = true
    },
    editType(type, index) {
      this.selectedType = this.$helpers.cloneObject(type)
      this.isNew = false
      this.loadForm = true
      this.showDialog = true
      this.index = index
    },
    newTypeAdded() {
      this.$refs.createNewModel.close()
    },
    deleteGroup(type) {
      let self = this
      let ticketType = {
        id: type.id,
      }
      self.$dialog
        .confirm({
          title: 'Delete Type',
          message: 'Are you sure you want to delete this Type?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/picklist/deleteTicketType', { ticketType })
              .then(function(response) {
                if (response.status === 200) {
                  self.$dialog.notify('Successfully deleted the Type')
                  self.$store.commit('GENERIC_DELETE', {
                    type: 'ticketType',
                    matches: self.$store.state.ticketType.indexOf(type),
                  })
                }
              })
              .catch(function(response) {
                console.log('Error in deleting')
              })
          }
        })
    },
  },
}
</script>
<style>
.el-icon-close:before {
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
