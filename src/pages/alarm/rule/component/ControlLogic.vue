<template>
  <div>
    <div class="fc-text-pink text-uppercase mT20">Command Action</div>
    <el-collapse
      class="new-rule-collapse position-relative controllogic-collapse"
      v-model="activeNames"
      :accordion="true"
    >
      <el-collapse-item
        class="rule-border-blue mT20 position-relative"
        style="border-left: 1px solid rgb(228, 235, 241);"
        v-for="(action, cindex) in controlaction"
        :key="cindex"
        :name="action.name"
      >
        <template slot="title">
          {{ action.name }}
        </template>
        <div class="">
          <el-radio-group v-model="action.templateJson.actionType">
            <el-radio :label="1" class="fc-radio-btn">Point</el-radio>
            <el-radio :label="2" class="fc-radio-btn">Group</el-radio>
          </el-radio-group>
        </div>

        <div class="pT20" v-show="action.templateJson.actionType === 1">
          <el-row>
            <el-col :span="4">
              <div class="label-txt-black">Asset</div>
            </el-col>
            <el-col :span="15">
              <el-form-item>
                <el-select
                  v-model="action.templateJson.resource"
                  placeholder="Select asset"
                  class="fc-input-full-border2 width100"
                  @change="fetchFields"
                >
                  <el-option
                    v-for="(asset, index) in assets"
                    :key="index"
                    :label="asset.name"
                    :value="asset.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="">
            <el-col :span="4">
              <div class="label-txt-black">Field</div>
            </el-col>
            <el-col :span="15">
              <el-form-item>
                <el-select
                  v-model="action.templateJson.metric"
                  placeholder="Select fields"
                  class="fc-input-full-border2 width100"
                >
                  <el-option
                    v-for="(field, index) in assetFields[
                      action.templateJson.resource
                    ]"
                    :key="index"
                    :label="field.displayName"
                    :value="field.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="">
            <el-col :span="4">
              <div class="label-txt-black">Value</div>
            </el-col>
            <el-col :span="15">
              <el-form-item>
                <el-input
                  autofocus
                  v-model="action.templateJson.val"
                  placeholder="Enter the value"
                  type="number"
                  class="fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <img
            src="~assets/remove-icon.svg"
            style="height:18px;width:18px;margin-right: 3px;"
            @click="deleteaction(cindex)"
            class="delete-icon pointer"
          />
        </div>

        <div v-show="action.templateJson.actionType === 2" class="pT20">
          <el-row class="">
            <el-col :span="4">
              <div class="label-txt-black">Actions Group</div>
            </el-col>
            <el-col :span="15">
              <el-form-item>
                <el-select
                  v-model="action.templateJson.controlActionGroupId"
                  filterable
                  class="width100 fc-input-full-border2"
                >
                  <el-option
                    v-for="(grouprule, index) in rcagrouplist"
                    :key="index"
                    :label="grouprule.name"
                    :value="grouprule.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="">
            <el-col :span="4">
              <div class="label-txt-black">Value</div>
            </el-col>
            <el-col :span="15">
              <el-form-item>
                <el-input
                  autofocus
                  v-model="action.templateJson.val"
                  placeholder="Enter the value"
                  type="number"
                  class="fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <img
            src="~assets/remove-icon.svg"
            style="height:18px;width:18px;margin-right: 3px;"
            @click="deleteaction(cindex)"
            class="delete-icon pointer"
          />
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
export default {
  data() {
    return {
      activeNames: ['actionName0'],
      controlaction: [],
      assetFields: {},
      logicaction: {},
      assets: [],
      rcarules: [],
      rcagrouplist: [],
    }
  },
  mounted() {
    this.addaction()
  },
  methods: {
    addaction() {
      this.controlaction.push({
        actionType: 18,
        templateJson: {
          val: '',
          resource: null,
          metric: null,
          actionType: 1,
          controlActionGroupId: null,
        },
        name: 'Command Action' + (this.controlaction.length + 1),
      })
    },
    fetchFields(resourceId) {
      if (this.assetFields[resourceId]) {
        return
      }
      let self = this
      self.loading = true
      let url =
        '/v2/controlAction/getControllableFields?resourceId=' + resourceId
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.controllableFields) {
            self.$set(
              self.assetFields,
              resourceId,
              response.data.result.controllableFields
            )
          }
          self.loading = false
        }
      })
    },
    deleteaction(key) {
      this.controlaction.splice(key, 1)
    },
  },
}
</script>

<style scoped></style>
