<template>
  <div id="f-derivation">
    <div
      class="derivation-sidebar-category"
      :style="{ 'padding-top': isOld ? '20px' : '' }"
    >
      <div :style="{ 'padding-left': isOld ? '20px' : '21px' }">
        <img
          v-if="isOld"
          style="width: 18px; float: left; margin-right: 8px;"
          src="~statics/formula/formula.svg"
        />
        <span>Derivations</span>
        <span
          class="flRight dervn-add pointer"
          slot="reference"
          @click="showAddWorkflow"
          >+ADD</span
        >
      </div>
      <div class="derivation-sidebar-menulist">
        <div v-if="loading"><spinner :show="loading" size="80"></spinner></div>
        <div
          v-else
          class="derivation-sidebar-menu"
          v-for="(derivation, idx) in derivations"
          :key="derivation.id"
        >
          <div v-if="derivation.selected" class="flLeft dervn-selected">
            <i
              class="el-icon-check dervn-color"
              @click="derivationSelected(derivation)"
            ></i>
          </div>
          <div
            class="ellipsis inline flLeft"
            style="max-width: 62%;"
            :title="derivation.name"
            v-tippy
          >
            {{ derivation.name }}
          </div>
          <div class="inline pL10 dervn-edit pointer">
            <i
              class="el-icon-edit dervn-color"
              @click="showEditWorkflow(derivation)"
            ></i>
          </div>
          <div class="inline pointer  dervn-delete pL10">
            <i
              class="el-icon-delete dervn-color"
              @click="deleteDerivation(derivation.id, idx)"
            ></i>
          </div>
          <div
            class="flRight dervn-select"
            @click="derivationSelected(derivation)"
            v-if="!derivation.selected"
          >
            <i class="el-icon-plus dervn-color"></i>
          </div>
        </div>
      </div>
    </div>
    <f-dialog
      v-if="showForm"
      :visible.sync="showForm"
      :width="'38%'"
      :title="(isNew ? 'Add' : 'Edit') + ' Derivation'"
      @save="save"
      @close="reset"
    >
      <div slot="content">
        <div>Derivation Name</div>
        <el-input
          v-model="newderivation.name"
          :placeholder="$t('common._common.enter_name')"
          class="pB20"
        ></el-input>
        <formula
          v-model="newderivation.workflow"
          module="formulaField"
          :hideModeChange="true"
        ></formula>
      </div>
    </f-dialog>
  </div>
</template>
<script>
import Formula from '@/workflow/FFormulaBuilder'
import FDialog from '@/FDialogNew'
export default {
  props: ['type', 'isOld'], // isOld temporary until new analytics is finished
  components: { Formula, FDialog },
  data() {
    return {
      newderivation: {
        name: '',
        workflow: null,
        analyticsType: this.type,
      },
      loading: false,
      showForm: false,
      derivations: [],
      selectedDerivations: [], // For report
      isNew: true,
      selectedDerivation: null, // For edit
    }
  },
  mounted() {
    this.loadDerivations()
  },
  computed: {
    initialDerivations() {
      if (this.$route.query.derivations) {
        return JSON.parse(this.$route.query.derivations)
      }
      return []
    },
  },
  methods: {
    loadDerivations() {
      this.loading = true
      this.$http
        .post('/dashboard/derivationList', { type: this.type })
        .then(response => {
          this.loading = false
          this.derivations = response.data.derivations || []
          if (this.initialDerivations.length) {
            this.selectedDerivations = this.initialDerivations
            let initialIds = this.initialDerivations.map(d => d.id)
            this.derivations = this.derivations.map(d => {
              if (initialIds.includes(d.id)) {
                this.$set(d, 'selected', true)
              }
              return d
            })
          }
        })
    },
    derivationSelected(derivation, isId) {
      if (isId) {
        derivation = this.derivations.find(d => d.id === derivation)
      }
      let idx = this.selectedDerivations.findIndex(d => d.id === derivation.id)
      let query = {}
      if (idx !== -1) {
        this.$set(derivation, 'selected', false)
        this.selectedDerivations.splice(idx, 1)
      } else {
        this.$set(derivation, 'selected', true)
        this.selectedDerivations.push(derivation)
      }
      if (this.selectedDerivations.length) {
        let param = this.selectedDerivations.map(d => ({
          id: d.id,
          name: d.name,
          workflowId: d.workflowId,
        }))
        query = { derivations: JSON.stringify(param) }
      }
      this.$router.push({ query: query })
    },
    save() {
      let url
      let derivation
      if (this.isNew) {
        url = 'addDerivation'
        derivation = this.newderivation
      } else {
        url = 'updateDerivation'
        derivation = this.$helpers.compareObject(
          this.newderivation,
          this.selectedDerivation
        )
        derivation.id = this.selectedDerivation.id
      }
      this.$http
        .post('/dashboard/' + url, { derivation: derivation })
        .then(response => {
          if (typeof response.data === 'object') {
            let newDerivation = response.data.derivation
            if (this.isNew) {
              this.derivations.push(newDerivation)
            } else {
              let idx = this.derivations.findIndex(
                d => d.id === newDerivation.id
              )
              this.derivations[idx] = newDerivation
            }
            this.reset()
            this.derivationSelected(newDerivation)
          } else {
            this.$message.error(
              this.isNew
                ? 'Derivation addition failed'
                : 'Derivation updation failed'
            )
          }
        })
    },
    deleteDerivation(id, idx) {
      this.$dialog
        .confirm({
          title: 'Delete Derivation',
          message: 'Are you sure you want to delete this derivation?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/dashboard/deleteDerivation', { id: [id] })
              .then(response => {
                if (typeof response.data === 'object') {
                  this.derivations.splice(idx, 1)
                  this.$message.success('Derivation deleted successfully')
                } else {
                  this.$message.error('Derivation cannot be deleted')
                }
              })
          }
        })
    },
    reset() {
      this.newderivation.workflow = null
      this.newderivation.name = ''
    },
    showAddWorkflow() {
      this.isNew = true
      this.newderivation.name = ''
      this.newderivation.workflow = null
      this.showForm = true
    },
    showEditWorkflow(derivation) {
      this.isNew = false
      this.selectedDerivation = derivation
      this.newderivation.name = derivation.name
      this.newderivation.workflow = this.$helpers.cloneObject(
        derivation.workflow
      )
      this.showForm = true
    },
  },
}
</script>
<style>
.dervn-add {
  color: #ee518f !important;
  padding: 2px 18px 0 !important;
  font-size: 11px !important;
}
.dervn-edit,
.dervn-select,
.dervn-delete {
  visibility: hidden;
}

.dervn-delete {
  padding: 1px 0px 0px 6px;
}

.derivation-sidebar-category {
  padding-bottom: 10px;
}
.derivation-sidebar-category span {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-transform: uppercase;
  color: #000000;
}

.derivation-sidebar-menu {
  padding: 12px 20px 12px 19px;
  letter-spacing: 0.4px;
  cursor: pointer;
}

.derivation-sidebar-menulist {
  padding-top: 10px;
}

.derivation-sidebar-menu:hover,
.derivation-sidebar-menu.active {
  background-color: #f0f7f8;
}

.derivation-sidebar-menu:hover .dervn-edit,
.derivation-sidebar-menu:hover .dervn-select,
.derivation-sidebar-menu:hover .dervn-delete {
  visibility: visible;
}

.dervn-popover {
  padding: 20px;
}

.dervn-select {
  font-size: 15px;
}

.dervn-selected {
  color: #39b2c2;
  font-size: 16px;
  margin-left: -21px;
  padding-right: 7px;
}

.dervn-color {
  color: #39b2c2;
}
</style>
