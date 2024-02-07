<template>
  <el-dialog
    custom-class="f-image-editor fc-dialog-center-container fp-adder indoorfp-dialog"
    v-if="visibility"
    :visible.sync="visibility"
    :modal-append-to-body="false"
    :before-close="closeDialog"
    :width="'70%'"
    title="Floor Plan Upload"
  >
    <div class="height100">
      <div class="flex-container height100">
        <div class="infp-left-section width30 height100 p30">
          <el-form
            ref="form"
            :rules="rules"
            :model="floorplanObject"
            label-width="120px"
          >
            <el-row class="mB10">
              <el-col :span="24">
                <el-form-item prop="name" class="mB10 m0">
                  <p class="fc-input-label-txt pB5">Name</p>
                  <el-input
                    v-model="floorplanObject.name"
                    class="width100 fc-input-full-border2"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="24">
                <el-form-item prop="siteId" class="mB10 m0">
                  <p class="fc-input-label-txt pB5">Site</p>
                  <el-select
                    v-model="floorplanObject.site.id"
                    filterable
                    @change="loadFloors(floorplanObject.site.id)"
                    placeholder="Select Site"
                    class="width100 el-input-textbox-full-border"
                  >
                    <template v-for="(site, index) in sites">
                      <el-option
                        :key="index"
                        :label="site.name"
                        :value="site.id"
                      ></el-option>
                    </template>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row class="mB10">
              <el-col :span="24">
                <el-form-item prop="floorId" class="mB10 m0">
                  <p class="fc-input-label-txt pB5">Floor</p>
                  <el-select
                    filterable
                    v-model="floorplanObject.floor.id"
                    placeholder="Select Floor"
                    class="width100 el-input-textbox-full-border"
                  >
                    <el-option
                      v-for="(floor, index) in floors"
                      :key="index"
                      :label="floor.name"
                      :value="floor.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <div class="infp-right-section height100 width70 upload-conatiner">
          <ImageUpload
            v-model="floorplanObject['fileId']"
            class="flex  upload-section"
          ></ImageUpload>
        </div>
      </div>
      <span slot="footer" class="modal-dialog-footer row">
        <el-button class="col-6 modal-btn-cancel" @click="closeDialog"
          >CANCEL</el-button
        >
        <el-button
          :loading="uploading"
          type="primary"
          class="col-6 modal-btn-save"
          @click="save"
          >{{ uploading ? 'Uploading....' : 'UPLOAD' }}</el-button
        >
      </span>
    </div>
  </el-dialog>
</template>
<script>
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import ImageUpload from 'pages/card-builder/components/ImageUpload'
export default {
  props: ['visibility'],
  components: {
    ImageUpload,
  },
  data() {
    return {
      floorplanObject: {
        name: null,
        siteId: null,
        floor: {
          id: null,
        },
        site: {
          id: null,
        },
        fileId: null,
        fileSource: 1,
        width: 0,
        height: 0,
      },
      uploading: false,
      rules: {
        name: [
          {
            required: true,
            message: 'Please input Floor Plan name',
            trigger: 'blur',
          },
        ],
      },
      floorList: [],
    }
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    floors() {
      if (this.floorList) {
        return this.floorList
      }
      return this.floorList
    },
  },
  methods: {
    async loadFloors(siteId) {
      let { error, data } = await API.get(
        `/v2/module/data/list?moduleName=floor&page=1&perPage=10&viewName=hidden-all&filters=%7B%22${'site'}%22%3A%7B%22operatorId%22%3A36%2C%22value%22%3A%5B%22${siteId}%22%5D%7D%7D`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.floorList = data.moduleDatas || []
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    async updateFloor(indoorfloorplan) {
      let floor = this.floors.find(
        floor => floor.id === this.floorplanObject.floor.id
      )
      if (floor && indoorfloorplan && indoorfloorplan.id) {
        let url = 'floor/update'
        this.$set(floor, 'indoorFloorPlanId', indoorfloorplan.id)
        await API.post(url, { floor: floor })
      }
    },
    async save() {
      let url = 'v3/modules/data/create'
      let params = {
        moduleName: 'indoorfloorplan',
        data: this.floorplanObject,
      }
      this.uploading = true
      let { error, data } = await API.post(url, params)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.updateFloor(data.indoorfloorplan)
        this.closeDialog()
        this.uploading = false
      }
    },
  },
}
</script>

<style>
.upload-conatiner .card-img-upload {
  -webkit-box-align: center;
  -ms-flex-align: center;
  align-items: center;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  max-width: 400px;
  max-height: 300px;
  height: 300px;
  width: 400px;
}
.upload-section {
  height: 100%;
  align-items: center;
  display: flex;
  justify-content: center;
}
.indoorfp-dialog .el-dialog__body {
  padding: 0px;
  height: 500px;
}
.infp-left-section {
  border-right: 1px solid #eee;
}
.infp-left-section .el-form-item__content {
  margin: 0px !important;
}
.upload-conatiner .card-image-preview {
  max-width: 400px;
  max-height: 300px;
  height: 300px;
  width: 400px;
}
</style>
