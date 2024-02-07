<template>
  <div>
    <div class="error-block" id="error" v-if="ruleId">
      <p class="error-txt">
        <i class="fa fa-info-circle" aria-hidden="true"></i>
        This template is already been used.
      </p>
    </div>
    <div class="rules-sum-header">
      <div class="fc-black-14 fwBold text-left text-uppercase">
        Template Summary
      </div>
    </div>
    <div class="pL30 pR30 pT20 height100 overflow-y-scroll pB100">
      <div
        class
        v-for="(templates, category) in groupedPlaceHolder"
        :key="category"
      >
        <!-- <div class="fc-modal-sub-title text-uppercase line-height18">{{category}}</div> -->
        <el-row
          v-show="fields.selection_criteria"
          class="pT10 pB10 flex-middle"
          v-for="(fields, index) in templates"
          :key="index"
        >
          <el-col :span="24">
            <div
              v-if="fields.selection_criteria.displayType === 'Field'"
              class="fc-input-label-txt"
            >
              Field {{ fields.label }}
            </div>
            <div v-else class="fc-input-label-txt">{{ fields.label }}</div>
            <div
              v-if="fields.selection_criteria.displayType === 'ASSETCHOOSER'"
            >
              <el-input
                v-model="resourceLabel"
                disabled
                class="fc-border-select fc-input-full-border-select2 width100"
              >
                <i
                  @click="chooserVisibility = true"
                  slot="suffix"
                  style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                  class="el-input__icon el-icon-search"
                ></i>
              </el-input>
            </div>
            <div
              v-else-if="fields.selection_criteria.displayType === 'DURATION'"
            >
              <el-select
                placeholder="Enter Interval"
                v-model="fields.default_value"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  :key="1"
                  label="Last 1 Hour"
                  :value="3600"
                ></el-option>
                <el-option
                  :key="3"
                  label="Last 3 Hour"
                  :value="10800"
                ></el-option>
                <el-option
                  :key="6"
                  label="Last 6 Hour"
                  :value="21600"
                ></el-option>
                <el-option
                  :key="12"
                  label="Last 12 Hour"
                  :value="43200"
                ></el-option>
                <!-- <el-option label="5 mins" :value="5"></el-option>
                  <el-option label="10 mins" :value="10"></el-option>
                  <el-option label="15 mins" :value="15"></el-option>
                  <el-option label="20 mins" :value="20"></el-option>
                  <el-option label="30 mins" :value="30"></el-option>
                <el-option label="1 hour" :value="60"></el-option>-->
              </el-select>
            </div>
            <div v-else-if="fields.selection_criteria.displayType === 'field'">
              <el-select
                filterable
                v-model="fields.default_value"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="field in thresholdFields"
                  :key="field.name + field.id"
                  :label="field.displayName"
                  :value="field.name"
                ></el-option>
              </el-select>
            </div>
            <div v-else-if="fields.selection_criteria.displayType === 'NUMBER'">
              <el-input
                v-model="fields.default_value"
                type="number"
                class="fc-border-select fc-input-full-border-select2 width100"
              ></el-input>
            </div>
            <div v-else-if="fields.selection_criteria.displayType === 'Radio'">
              <el-radio
                v-model="fields.default_value"
                :label="'true'"
                class="fc-radio-btn"
                >{{ fields.selection_criteria.trueVal }}</el-radio
              >
              <el-radio
                v-model="fields.default_value"
                :label="'false'"
                class="fc-radio-btn"
                >{{ fields.selection_criteria.falseVal }}</el-radio
              >
              <!-- <el-input v-model="fields.default_value" type="number"
                  class="fc-border-select fc-input-full-border-select2 width100">
              </el-input>-->
            </div>
            <el- :span="12" v-else class>
              <div class="fc-black-12 text-left bold">
                {{ fields.default_value }}
              </div>
            </el->
            <el-col :span="12" class="mT10 pL20"></el-col>
          </el-col>
        </el-row>
      </div>
    </div>

    <div v-if="dialogViewer === 2" class="modal-dialog-footer">
      <el-button
        :disabled="ruleId"
        type="button fc-full-btn-fill-green"
        @click="createRules()"
        >Create rule</el-button
      >
    </div>
    <div v-else class="modal-dialog-footer">
      <!-- <div class="modal-dialog-footer"> -->
      <el-button @click="backtomessage()" class="modal-btn-cancel">
        Cancel</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="createRules()"
        >Create rule</el-button
      >
      <!-- </div> -->
      <!-- <el-button
        class="btn-grey-fill width100"
        type="primary"
        @click="backtomessage()"
        >cancel</el-button
      >
      <el-button
        type="primary"
        @click="createRules()"
        class="fc-btn-green-lg-fill width100"
        >Create rule</el-button
      > -->
    </div>
    <el-dialog
      title="Message"
      :visible.sync="showDialog"
      class="fc-dialog-center-container"
      :append-to-body="true"
      width="30%"
    >
      <div class="height150">
        <div
          class="label-txt-black"
          v-if="appliedDetail.available == appliedDetail.totalAssets"
        >
          Rule successfully applied to all assets ({{ appliedDetail.available }}
          assets)
        </div>
        <div class="label-txt-black" v-else>
          Rule successfully applied to {{ appliedDetail.available }} assets
        </div>
        <div>Category: {{ ruleTemplate.category }}</div>
        <div>Message: {{ ruleTemplate.name }}</div>
      </div>
      <div slot="footer" class="dialpg-footer modal-dialog-footer">
        <el-button
          class="btn-grey-fill width100"
          type="primary"
          @click="pushPage()"
          >Close</el-button
        >
      </div>
    </el-dialog>
    <space-asset-multi-chooser
      @associate="data => associateResource(data, 'assets')"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :query="resourceQuery"
      :showAsset="true"
      :disable="true"
    ></space-asset-multi-chooser>
  </div>
</template>
<script>
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'
import { mapState } from 'vuex'

export default {
  mixins: [TemplateHelper],

  props: [
    'ruleTemplate',
    'templateId',
    'ruleId',
    'appliedDetail',
    'dialogViewer',
  ],
  components: {
    SpaceAssetMultiChooser,
  },
  data() {
    return {
      templates: [],
      groupedPlaceHolder: [],
      chooserVisibility: false,
      selectedResourceList: [],
      resourceQuery: null,
      showDialog: false,
      childDialogClose: false,
    }
  },
  mounted() {
    if (this.ruleTemplate) {
      this.groupedPlaceHolder = this.placeHolderGroupFields()
    }
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    selectedCategory() {
      if (this.ruleTemplate && this.ruleTemplate.category) {
        let category = this.assetCategory.filter(
          d => d.name === this.ruleTemplate.category
        )
        return category
      }
      return null
    },
    resourceData() {
      return {
        assetCategory: this.selectedCategory[0].id,
      }
    },
    resourceLabel() {
      if (this.selectedCategory) {
        let category = this.selectedCategory
        let message
        let selectedCount = this.selectedResourceList.length
        if (selectedCount) {
          let includeMsg = this.isIncludeResource ? 'included' : 'excluded'
          if (selectedCount === 1) {
            return this.selectedResourceList[0].name
          }
          message =
            selectedCount +
            ' ' +
            category[0].name +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + category[0].name + 's'
        }
        return message
      } else if (this.selectedResourceList.id > 0) {
        return this.selectedResourceList.name
      }
      return null
    },
  },
  methods: {
    backtomessage() {
      this.$emit('childToParent', true)
    },
    placeHolderGroupFields() {
      // this.checkFields()
      Object.keys(this.ruleTemplate.placeHolder).forEach(key => {
        this.templates.push(this.ruleTemplate.placeHolder[key])
      })
      return this.groupByField(this.templates, 'type')
    },
    checkFields() {
      this.loadThresholdFields(this.selectedCategory[0].id)
      console.log()
    },
    associateResource(selectedObj, type) {
      if (type === 'assets') {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.selectedResourceList = selectedObj.resourceList
          this.isIncludeResource = selectedObj.isInclude
        }
      } else {
        this.selectedResourceList = selectedObj
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
    pushPage() {
      this.showDialog = false
      this.childDialogClose = true
      this.$emit('childToParent', this.childDialogClose)
      let url = '/app/fa/rules/newtemplates'
      this.$router.push({
        path: url,
      })
    },
    createRules() {
      let placeHolder = {}
      let index = 0
      Object.keys(this.groupedPlaceHolder).forEach(key => {
        this.groupedPlaceHolder[key].forEach((d, i) => {
          if (
            d.selection_criteria.displayType === 'ASSETCHOOSER' &&
            this.selectedResourceList.length > 0
          ) {
            let fieldName = this.isIncludeResource
              ? 'includeResource'
              : 'excludeResource'
            d.uniqueId = fieldName
            if (this.selectedResourceList.length > 0) {
              d.default_value = this.selectedResourceList.map(
                resource => resource.id
              )
            } else {
              d.default_value = null
            }
          }
          placeHolder[index] = d
          index++
        })
      })
      let params = {
        id: this.ruleTemplate.id,
        placeHolder: placeHolder,
      }
      console.log('params' + params)
      let url = `/v2/rules/template/createRule`
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0 && response.data.result) {
            this.$message.success('Rule created successfully')
            // this.$router.replace({ path: 'app/fa/rules/active' })
            // this.$router.push({
            //   path: 'active'
            // })
          }
        })
        .catch(error => {
          this.$message.success('Something went wrong')
        })
      this.showDialog = true
    },
  },
}
</script>
<style>
.error-block {
  width: 100%;
  background-color: #f9ecec;
  border: none;
  height: 45px;
  padding-top: 8px;
  padding-bottom: 8px;
  position: relative;
  top: 0;
  right: 0;
  left: 0;
}
.error-block .error-txt {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  letter-spacing: 0.4px;
  text-align: center;
  color: #e16868;
}
.error-block .error-close-icon {
  position: absolute;
  right: 17px;
  top: 12px;
  color: #de5656;
  font-size: 18px;
  font-weight: 300;
  cursor: pointer;
}
</style>
