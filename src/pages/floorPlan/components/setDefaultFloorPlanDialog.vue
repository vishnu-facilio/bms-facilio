<template>
  <el-dialog
    custom-class="fc-dialog-center-container f-image-editor fc-dialog-center-container manage-fp"
    :visible.sync="visiblity"
    :modal-append-to-body="false"
    :width="'50%'"
    :show-close="false"
  >
    <template slot="title">
      <div class="header d-flex">
        <div class="el-dialog__title self-center">{{ 'FLOOR PLAN' }}</div>
        <el-input
          ref="mainFieldSearchInput"
          v-if="showMainFieldSearch"
          class="fc-input-full-border2 width-auto mL-auto"
          suffix-icon="el-icon-search"
          v-model="search"
          @blur="hideMainFieldSearch"
        ></el-input>
        <span v-else class="self-center mL-auto" @click="openMainFieldSearch">
          <inline-svg
            src="svgs/search"
            class="vertical-middle cursor-pointer"
            iconClass="icon icon-sm mT5 mR5 search-icon"
          ></inline-svg>
        </span>
        <span class="separator self-center">|</span>
        <span class="self-center" @click="addFloorplan()">
          <i class="el-icon-plus pointer mL-auto text-fc-pink"></i>
        </span>
        <span class="separator self-center">|</span>
        <div
          class="close-btn self-center cursor-pointer"
          @click="closeDialog()"
        >
          <i class="el-dialog__close el-icon el-icon-close"></i>
        </div>
      </div>
    </template>
    <div class="default-fp-body">
      <el-table
        :data="
          flooplans.filter(
            data =>
              !search || data.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        style="width: 100%"
        row-class-name="visibility-visible-actions"
      >
        <el-table-column label="Name" prop="name"> </el-table-column>
        <el-table-column align="right">
          <template v-slot="scope">
            <el-button
              v-if="scope.row.id === floorobj.defaultFloorPlanId"
              size="mini"
              :disabled="true"
              >Default</el-button
            >
            <span v-else class="visibility-hide-actions pL10">
              <el-button
                size="mini"
                @click="handleDefault(scope.$index, scope.row)"
                >Make Default</el-button
              >
            </span>
            <span class="visibility-hide-actions pL10">
              <i
                class="el-icon-edit"
                @click="handleEdit(scope.$index, scope.row)"
              ></i>
            </span>
            <span
              class="visibility-hide-actions pL10"
              v-if="scope.row.id !== floorobj.defaultFloorPlanId"
            >
              <i
                class="el-icon-delete"
                @click="handleDelete(scope.$index, scope.row)"
              ></i>
            </span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="closeDialog"
        >CANCEL</el-button
      >
      <el-button type="primary" class="col-6 modal-btn-save" @click="save"
        >SAVE</el-button
      >
    </span>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['floorobj'],
  data() {
    return {
      visiblity: true,
      flooplans: [],
      search: '',
      showMainFieldSearch: false,
    }
  },
  mounted() {
    this.visiblity = true
    this.loadFloorPlans()
  },
  methods: {
    hideMainFieldSearch() {
      let { search } = this
      if (isEmpty(search)) {
        this.$set(this, 'showMainFieldSearch', false)
      }
    },
    loadFloorPlans() {
      let floorPlan = {
        floorId: this.floorobj.id,
      }
      this.$http
        .post('/v2/floorPlan/getListOfFloorPlan', {
          floorPlan: floorPlan,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.flooplans = data.result.floorPlans
          }
        })
    },
    closeDialog() {
      this.visiblity = false
      this.$emit('close', false)
    },
    // openFloorPlanEditor() {
    //   this.$emit('openFloorPlanEditor', this.floorPlan)
    // },
    handleDefault(index, data) {
      this.$emit('setDefaultFloorplan', data.id)
    },
    handleEdit(index, data) {
      this.$emit('openFPEdit', data.id)
    },
    openMainFieldSearch() {
      this.$set(this, 'showMainFieldSearch', true)
      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']
        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },
    addFloorplan() {
      this.$emit('newfloorplan')
    },
    handleDelete(index, data) {
      let { id } = data
      this.$http
        .post('/v2/floorPlan/delete', {
          floorPlan: { floorPlanId: id, id: id },
        })
        .catch(() => {})
      this.$emit('deleteFloorplan', true)
    },
    save() {},
  },
}
</script>

<style>
.default-fp-body {
  height: 400px;
  overflow: auto;
  padding-bottom: 40px;
}

.manage-fp .el-dialog__header {
  padding-bottom: 20px;
}

.manage-fp .el-dialog__body {
  padding: 0px;
}

.fp-actions-hide {
  display: none;
}

.fp-actions-show:hover .fp-actions-hide {
  display: block;
}
</style>
