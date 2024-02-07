<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="wos-id pointer mB10" @click="$router.back()" title="Back">
          <img
            src="~assets/arrow-pointing-to-left.svg"
            data-position="top"
            v-tippy="{ arrow: true, animation: 'perspective' }"
            width="12"
            height="12"
            class="vertical-middle mR5 mL3"
          />
          <span class="fc-id">Back To Stateflows</span>
        </div>
        <div class="setting-form-title">State Transitions</div>
        <div class="heading-description">
          List of configured state transitions
        </div>
      </div>
      <div class="action-btn setting-page-btn mT25">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="
            showEditTransitionDialog = true
            isNew = true
            activeTransitionObj = {}
          "
          >Add State Transition</el-button
        >
        <el-dialog
          v-if="showEditTransitionDialog"
          :visible="true"
          :fullscreen="true"
          :append-to-body="true"
          custom-class="fc-dialog-right setup-dialog40 new-state-transition-dialog"
          style="z-index: 1999;"
        >
          <new-state-transition
            :transitionObj="activeTransitionObj"
            :stateFlowId="stateFlowId"
            :stateFlowObj="stateFlow"
            :isNew="isNew"
            @onClose="closeDialog"
            @onTransitionUpdate="saveAndCloseDialog"
            @onTransitionCreate="saveAndCloseDialog"
            :module="module"
            @onError="showError"
            @onDelete="deleteTransitionObj"
          ></new-state-transition>
        </el-dialog>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">Name</th>
                <th class="setting-table-th setting-th-text">From</th>
                <th class="setting-table-th setting-th-text">To</th>
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
            <tbody v-else-if="!stateTransitions.length">
              <tr>
                <td colspan="100%" class="text-center">
                  No transitions created yet.
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(transition, index) in stateTransitions"
                :key="index"
              >
                <td @click="editTransition(transition)">
                  {{ transition.name }}
                  <!-- <i class="el-icon-edit pointer" ></i> -->
                </td>
                <td @click="editTransition(transition)">
                  {{
                    (
                      $store.getters.getTicketStatus(
                        transition.fromStateId,
                        module
                      ) || {}
                    ).displayName
                  }}
                </td>
                <td @click="editTransition(transition)">
                  {{
                    (
                      $store.getters.getTicketStatus(
                        transition.toStateId,
                        module
                      ) || {}
                    ).displayName
                  }}
                </td>
                <td style="width: 15%;">
                  <div class="text-left actions mT0 mR15 text-center">
                    <i
                      class="el-icon-delete pointer"
                      @click="
                        ;(showDeleteDialog = true),
                          (activeTransitionObj = transition)
                      "
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <delete-dialog
      v-if="showDeleteDialog"
      @onClose="showDeleteDialog = false"
      @onSave="deleteTransition(activeTransitionObj)"
    ></delete-dialog>
  </div>
</template>
<script>
import NewStateTransition from './NewStateTransition'
import { isEmpty } from '@facilio/utils/validation'
import DeleteDialog from './TransitionDeleteDialog'

export default {
  props: ['stateFlowId', 'moduleName'],
  components: { NewStateTransition, DeleteDialog },
  data() {
    return {
      module: null,
      loading: false,
      stateTransitions: [],
      showEditTransitionDialog: false,
      isNew: false,
      activeTransitionObj: {},
      stateFlow: {},
      showDeleteDialog: false,
    }
  },
  created() {
    if (isEmpty(this.module) && !isEmpty(this.moduleName)) {
      this.module = this.moduleName
    }
  },
  mounted() {
    this.$store.dispatch('loadTicketStatus', this.moduleName).then(() => {
      this.loadStateFlowDetails()
      this.getStateTransitionsList()
    })
  },
  methods: {
    loadStateFlowDetails() {
      this.$http
        .get(`/v2/stateflow/view?stateFlowId=${this.stateFlowId}`)
        .then(response => {
          this.stateFlow = response.data.result.stateFlow
        })
    },

    getStateTransitionsList() {
      let { module } = this
      this.loading = true
      return this.$http
        .post('/v2/statetransition/list', {
          moduleName: module,
          stateFlowId: this.stateFlowId,
        })
        .then(({ data }) => {
          this.stateTransitions = data.result.stateTransitionList || []
        })
        .catch(({ message = 'Error loading transitions' }) => {
          this.$message.error(message)
        })
        .finally(() => (this.loading = false))
    },

    editTransition(transition) {
      this.activeTransitionObj = this.$helpers.cloneObject(transition)
      this.isNew = false
      this.showEditTransitionDialog = true
    },

    // showDeleteDialog({ id }) {
    //   let dialogObj = {
    //     title: 'Confirm Delete',
    //     message: 'Are you sure you want to delete this transition?',
    //     rbDanger: true,
    //     rbLabel: 'Confirm',
    //   }
    //   this.$dialog.confirm(dialogObj).then(value => {
    //     if (value) {
    //       this.deleteTransition(id)
    //     }
    //   })
    // },

    deleteTransition({ id }) {
      return this.$http
        .post('/v2/statetransition/delete', {
          stateFlowId: this.stateFlowId,
          stateTransitionId: id,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            let index = this.stateTransitions.findIndex(
              transition => transition.id === id
            )
            this.stateTransitions.splice(index, 1)
            this.closeDialog()
          }
        })
        .catch(response => {
          this.$message.error(
            response.data.message || 'Error occurred while deleting transiion'
          )
        })
    },

    deleteTransitionObj(id) {
      let index = this.stateTransitions.findIndex(
        transition => transition.id === id
      )
      this.stateTransitions.splice(index, 1)
      this.closeDialog()
    },

    closeDialog(transitionObj, needsRefresh = false) {
      if (needsRefresh) {
        this.getStateTransitionsList()
      } else if (typeof transitionObj === 'object') {
        if (this.isNew) {
          this.stateTransitions.push(transitionObj)
        } else {
          let index = this.stateTransitions.findIndex(
            transition => transitionObj.id === transition.id
          )
          this.stateTransitions[index] = { ...transitionObj }
        }
      }
      this.showEditTransitionDialog = false
      this.isNew = false
      this.activeTransitionObj = {}
    },

    saveAndCloseDialog(transition) {
      this.$message({
        showClose: true,
        message: 'Transition updated successfully',
        type: 'success',
      })
      this.closeDialog(transition, !isEmpty(transition))
    },

    showError(error) {
      this.$message({
        showClose: true,
        message: error ? error.message : 'Could not create state transition.',
        type: 'error',
      })
    },

    goToStateFlowList() {
      this.$router.push({ path: 'stateflows' })
    },
  },
}
</script>
