<template>
  <div>
    <el-dialog
      custom-class="fcu-card-builder"
      :modal-append-to-body="false"
      :visible.sync="visibility"
      width="30%"
      top="0%"
      title="SELECT FCU CARD"
      :before-close="closedialog"
    >
      <el-form :model="data" ref="FCUCARD" :label-position="'top'">
        <el-form-item prop="category">
          <p class="grey-text2">Select Building</p>
          <el-select
            style="width: 90%"
            filterable
            collapse-tags
            v-model="data.selectedBuilding"
          >
            <el-option
              v-for="(list, index) in buildingList"
              :key="index"
              :label="list.name"
              :value="list.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="category">
          <p class="grey-text2">Select Level</p>
          <el-select
            style="width: 90%"
            filterable
            collapse-tags
            v-model="data.level"
            @change="selectlevel()"
          >
            <el-option
              v-for="(list, index) in lists"
              :key="index"
              :label="list.label"
              :value="list.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="modal-dialog-footer row">
        <el-button class="col-6 modal-btn-cancel" @click="closedialog"
          >Cancel</el-button
        >
        <!-- <el-button type="primary" class="col-6 modal-btn-save" @click="update" v-if="assetcardEdit">Update</el-button> -->
        <el-button type="primary" class="col-6 modal-btn-save" @click="save"
          >Ok</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: ['visibility'],
  data() {
    return {
      data: {
        level: null,
        selectedBuilding: null,
      },
      buildingList: null,
      lists: [
        {
          label: 'level 1',
          value: 1,
        },
        {
          label: 'level 2',
          value: 2,
        },
        {
          label: 'level 3',
          value: 3,
        },
      ],
    }
  },
  created() {
    this.$store.dispatch('loadBuildings')
  },
  mounted() {
    this.loadBuilding()
  },
  methods: {
    closedialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    save() {
      this.$emit('update:visibility', false)
      this.$emit('save', this.data)
    },
    loadBuilding() {
      let self = this
      const formData = new FormData()
      let url = '/report/alarms/getAllBuildings'
      self.$http.post(url, formData).then(function(response) {
        self.buildingList = response.data
      })
    },
    selectlevel() {},
  },
}
</script>
<style>
.fcu-card-builder {
  margin-top: 15% !important;
}
</style>
