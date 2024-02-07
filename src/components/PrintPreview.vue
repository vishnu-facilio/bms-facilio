<template>
  <div class="pdf-generic-component">
    <el-header height="80" class="width100 fc-pdf-header">
      <div class="flex-middle justify-between">
        <div class="fc-black3-16 bold text-capitalize">
          {{ moduleName }}
        </div>
        <div>
          <el-button @click="showSidebar">
            Export
          </el-button>
        </div>
      </div>
    </el-header>
    <el-container class="fc-pdf-block">
      <el-main class="fc-pdf-main-component">
        <div class="p10 fc-pdf-inner-component">
          Workorder summary
        </div>
      </el-main>
      <el-aside width="400px" v-if="sidebarShow" class="fc-pdf-settings">
        <div class="fc-pdf-settings-block">
          <div class="fc-black2-14 bold">
            Paper size
          </div>
          <div class="pT15">
            <el-select
              v-model="customPapersize"
              placeholder="Select"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="item in paperSizeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </div>
        </div>

        <div class="fc-pdf-settings-block">
          <div class="fc-black2-14 bold">
            Orientation
          </div>
          <div class="pT15">
            <el-radio-group v-model="orientationModel">
              <el-radio :label="3" class="fc-radio-btn">Portarit</el-radio>
              <el-radio :label="6" class="fc-radio-btn">Landscape</el-radio>
            </el-radio-group>
          </div>
        </div>

        <div class="fc-pdf-settings-block">
          <div class="fc-black2-14 bold">
            Scaling
          </div>
          <div class="pT15">
            <el-radio-group v-model="scalingModel">
              <el-radio :label="4" class="fc-radio-btn">Actual</el-radio>
              <el-radio :label="5" class="fc-radio-btn">Fit page</el-radio>
              <el-radio :label="7" class="fc-radio-btn">Custom</el-radio>
            </el-radio-group>
            <div class="pT15" v-if="scalingModel === 7">
              <el-select
                v-model="customScaling"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="item in scalingOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>

        <div class>
          <el-divider></el-divider>
        </div>

        <div class="fc-link-blue14 bold">
          More Settings
        </div>

        <div class="fc-pdf-settings-block mT15">
          <div class="fc-black2-14 bold">
            Margin
          </div>
          <div class="pT15">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-input
                  placeholder="Please input"
                  v-model="marginTop"
                  class="fc-input-full-border-select2 width100"
                >
                  <span slot="suffix" class="input-inch">in</span>
                </el-input>
                <div class="fc-grey-text text-capitalize pT5">
                  Top
                </div>
              </el-col>
              <el-col :span="6">
                <el-input
                  placeholder="Please input"
                  v-model="marginRight"
                  class="fc-input-full-border-select2 width100"
                >
                  <span slot="suffix" class="input-inch">in</span>
                </el-input>
                <div class="fc-grey-text text-capitalize pT5">
                  Right
                </div>
              </el-col>
              <el-col :span="6">
                <el-input
                  placeholder="Please input"
                  v-model="marginBottom"
                  class="fc-input-full-border-select2 width100"
                >
                  <span slot="suffix" class="input-inch">in</span>
                </el-input>
                <div class="fc-grey-text text-capitalize pT5">
                  Bottom
                </div>
              </el-col>
              <el-col :span="6">
                <el-input
                  placeholder="Please input"
                  v-model="marginLeft"
                  class="fc-input-full-border-select2 width100"
                >
                  <span slot="suffix" class="input-inch">in</span>
                </el-input>
                <div class="fc-grey-text text-capitalize pT5">
                  Left
                </div>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- header and footer tabs -->
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="Header" name="header">
            <el-row :gutter="20">
              <div class="fc-grey-text text-capitalize bold pB10 pL10">
                Left
              </div>
              <el-col :span="8">
                <el-select
                  v-model="headerLeft"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="left in headerLeftOptions"
                    :key="left.value"
                    :label="left.label"
                    :value="left.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="16">
                <el-input
                  v-if="headerLeft === 'none'"
                  placeholder=""
                  v-model="headerLeftNone"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="headerLeft === 'title'"
                  placeholder="Please input"
                  v-model="headerLeftTitle"
                  class="fc-input-full-border-select2 width100"
                >
                </el-input>
                <el-input
                  v-if="headerLeft === 'pageNo'"
                  placeholder="Please input"
                  v-model="headerLeftpageNo"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="headerLeft === 'dateTime'"
                  placeholder="Please input"
                  v-model="headerLeftdateTime"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
              </el-col>
            </el-row>
            <!-- center -->

            <el-row :gutter="20" class="pT20">
              <div class="fc-grey-text text-capitalize bold pB10 pL10">
                Center
              </div>
              <el-col :span="8">
                <el-select
                  v-model="headerCenter"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="center in headerCenterOptions"
                    :key="center.value"
                    :label="center.label"
                    :value="center.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="16">
                <el-input
                  v-if="headerCenter === 'none'"
                  placeholder=""
                  v-model="headerCenterNone"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="headerCenter === 'title'"
                  placeholder="Please input"
                  v-model="headerCenterTitle"
                  class="fc-input-full-border-select2 width100"
                >
                </el-input>
                <el-input
                  v-if="headerCenter === 'pageNo'"
                  placeholder="Please input"
                  v-model="headerCenterpageNo"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="headerCenter === 'dateTime'"
                  placeholder="Please input"
                  v-model="headerCenterdateTime"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
              </el-col>
            </el-row>
            <!-- right -->
            <el-row :gutter="20" class="pT20">
              <div class="fc-grey-text text-capitalize bold pB10 pL10">
                Right
              </div>
              <el-col :span="8">
                <el-select
                  v-model="headerRight"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="right in headerRightOptions"
                    :key="right.value"
                    :label="right.label"
                    :value="right.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="16">
                <el-input
                  v-if="headerRight === 'none'"
                  placeholder=""
                  v-model="headerRightNone"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="headerRight === 'title'"
                  placeholder="Please input"
                  v-model="headerRightTitle"
                  class="fc-input-full-border-select2 width100"
                >
                </el-input>
                <el-input
                  v-if="headerRight === 'pageNo'"
                  placeholder="Please input"
                  v-model="headerRightpageNo"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="headerRight === 'dateTime'"
                  placeholder="Please input"
                  v-model="headerRightdateTime"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="Footer" name="footer">
            <el-row :gutter="20">
              <div class="fc-grey-text text-capitalize bold pB10 pL10">
                Left
              </div>
              <el-col :span="8">
                <el-select
                  v-model="footerLeft"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="left in footerLeftOptions"
                    :key="left.value"
                    :label="left.label"
                    :value="left.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="16">
                <el-input
                  v-if="footerLeft === 'none'"
                  placeholder=""
                  v-model="footerLeftNone"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="footerLeft === 'title'"
                  placeholder="Please input"
                  v-model="footerLeftTitle"
                  class="fc-input-full-border-select2 width100"
                >
                </el-input>
                <el-input
                  v-if="footerLeft === 'pageNo'"
                  placeholder="Please input"
                  v-model="footerLeftpageNo"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="footerLeft === 'dateTime'"
                  placeholder="Please input"
                  v-model="footerLeftdateTime"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
              </el-col>
            </el-row>
            <!-- center -->

            <el-row :gutter="20" class="pT20">
              <div class="fc-grey-text text-capitalize bold pB10 pL10">
                Center
              </div>
              <el-col :span="8">
                <el-select
                  v-model="footerCenter"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="center in footerCenterOptions"
                    :key="center.value"
                    :label="center.label"
                    :value="center.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="16">
                <el-input
                  v-if="footerCenter === 'none'"
                  placeholder=""
                  v-model="footerCenterNone"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="footerCenter === 'title'"
                  placeholder="Please input"
                  v-model="footerCenterTitle"
                  class="fc-input-full-border-select2 width100"
                >
                </el-input>
                <el-input
                  v-if="footerCenter === 'pageNo'"
                  placeholder="Please input"
                  v-model="footerCenterpageNo"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="footerCenter === 'dateTime'"
                  placeholder="Please input"
                  v-model="footerCenterdateTime"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
              </el-col>
            </el-row>
            <!-- right -->
            <el-row :gutter="20" class="pT20">
              <div class="fc-grey-text text-capitalize bold pB10 pL10">
                Right
              </div>
              <el-col :span="8">
                <el-select
                  v-model="footerRight"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="right in footerRightOptions"
                    :key="right.value"
                    :label="right.label"
                    :value="right.value"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="16">
                <el-input
                  v-if="footerRight === 'none'"
                  placeholder=""
                  v-model="footerRightNone"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="footerRight === 'title'"
                  placeholder="Please input"
                  v-model="footerRightTitle"
                  class="fc-input-full-border-select2 width100"
                >
                </el-input>
                <el-input
                  v-if="footerRight === 'pageNo'"
                  placeholder="Please input"
                  v-model="footerRightpageNo"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
                <el-input
                  v-if="footerRight === 'dateTime'"
                  placeholder="Please input"
                  v-model="footerRightdateTime"
                  class="fc-input-full-border-select2 width100"
                  disabled
                >
                </el-input>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="showSidebar">
            Cancel
          </el-button>
          <el-button type="primary" class="modal-btn-save">
            Export
          </el-button>
        </div>
      </el-aside>
    </el-container>
    <component
      :is="pageViewComponent"
      :moduleName="moduleName"
      :summaryId="summaryId"
    ></component>
  </div>
</template>
<script>
import workorderPdf from '../pdf/pages/workorderPdf'
import assetPdf from '../pdf/pages/assetPdf'
export default {
  data() {
    return {
      pageViewComponent: this.moduleName,
      sidebarShow: false,
      orientationModel: 3,
      scalingModel: 4,
      customScaling: '',
      marginTop: 0.1,
      marginRight: 0.1,
      marginLeft: 0.1,
      marginBottom: 0.1,
      headerLeft: 'none',
      activeName: 'header',
      headerLeftNone: '',
      headerLeftTitle: this.moduleName,
      headerLeftpageNo: 'page 1',
      headerLeftdateTime: '31-jan-2023 11.00',
      headerLeftOptions: [
        {
          value: 'none',
          label: 'None',
        },
        {
          value: 'title',
          label: 'Title',
        },
        {
          value: 'pageNo',
          label: 'Page #',
        },
        {
          value: 'dateTime',
          label: 'Date & Time',
        },
      ],
      //header center option
      headerCenter: 'none',
      headerCenterNone: '',
      headerCenterTitle: this.moduleName,
      headerCenterpageNo: 'page 1',
      headerCenterdateTime: '31-jan-2023 11.00',
      headerCenterOptions: [
        {
          value: 'none',
          label: 'None',
        },
        {
          value: 'title',
          label: 'Title',
        },
        {
          value: 'pageNo',
          label: 'Page #',
        },
        {
          value: 'dateTime',
          label: 'Date & Time',
        },
      ],
      // header center
      headerRight: 'none',
      headerRightNone: '',
      headerRightTitle: this.moduleName,
      headerRightpageNo: 'page 1',
      headerRightdateTime: '31-jan-2023 11.00',
      headerRightOptions: [
        {
          value: 'none',
          label: 'None',
        },
        {
          value: 'title',
          label: 'Title',
        },
        {
          value: 'pageNo',
          label: 'Page #',
        },
        {
          value: 'dateTime',
          label: 'Date & Time',
        },
      ],
      // footer
      footerLeft: 'none',
      footerLeftNone: '',
      footerLeftTitle: this.moduleName,
      footerLeftpageNo: 'page 1',
      footerLeftdateTime: '31-jan-2023 11.00',
      footerLeftOptions: [
        {
          value: 'none',
          label: 'None',
        },
        {
          value: 'title',
          label: 'Title',
        },
        {
          value: 'pageNo',
          label: 'Page #',
        },
        {
          value: 'dateTime',
          label: 'Date & Time',
        },
      ],
      //header center option
      footerCenter: 'none',
      footerCenterNone: '',
      footerCenterTitle: this.moduleName,
      footerCenterpageNo: 'page 1',
      footerCenterdateTime: '31-jan-2023 11.00',
      footerCenterOptions: [
        {
          value: 'none',
          label: 'None',
        },
        {
          value: 'title',
          label: 'Title',
        },
        {
          value: 'pageNo',
          label: 'Page #',
        },
        {
          value: 'dateTime',
          label: 'Date & Time',
        },
      ],
      // header center
      footerRight: 'none',
      footerRightNone: '',
      footerRightTitle: this.moduleName,
      footerRightpageNo: 'page 1',
      footerRightdateTime: '31-jan-2023 11.00',
      footerRightOptions: [
        {
          value: 'none',
          label: 'None',
        },
        {
          value: 'title',
          label: 'Title',
        },
        {
          value: 'pageNo',
          label: 'Page #',
        },
        {
          value: 'dateTime',
          label: 'Date & Time',
        },
      ],
      scalingOptions: [
        {
          value: '50%',
          label: '50%',
        },
        {
          value: '75%',
          label: '75%',
        },
        {
          value: '100%',
          label: '100%',
        },
        {
          value: '125%',
          label: '125%',
        },
        {
          value: '150%',
          label: '150%',
        },
      ],
      value: '',
      customPapersize: 'A4',
      paperSizeOptions: [
        {
          value: 'a0',
          label: 'A0',
        },
        {
          value: 'a1',
          label: 'A1',
        },
        {
          value: 'a2',
          label: 'A2',
        },
        {
          value: 'a3',
          label: 'A3',
        },
        {
          value: 'a4',
          label: 'A4',
        },
        {
          value: 'a5',
          label: 'A5',
        },
        {
          value: 'b4',
          label: 'B4',
        },
        {
          value: 'b5',
          label: 'B5',
        },
        {
          value: 'legal',
          label: 'Legal',
        },
        {
          value: 'letter',
          label: 'Letter',
        },
      ],
    }
  },
  components: {
    workorderPdf,
    assetPdf,
  },
  computed: {
    moduleName() {
      if (this.$route?.params?.moduleName) {
        return this.$route?.params?.moduleName
      }
      return ''
    },
    summaryId() {
      if (this.$route?.params?.id) {
        return this.$route?.params?.id
      }
      return ''
    },
  },
  async created() {
    await this.componentRenderPage()
  },
  methods: {
    componentRenderPage() {
      let { pageViewComponent } = this
      let page = {
        workorderPdf,
        assetPdf,
      }
      let render = page[pageViewComponent]
      if (render) {
        this.pageViewComponent = page[pageViewComponent]
      }
    },
    showSidebar() {
      this.sidebarShow = !this.sidebarShow
    },
    handleClick() {
      console.log('Tab clicked')
    },
  },
}
</script>
<style lang="scss">
.fc-pdf-header {
  padding: 10px 20px;
  background: #f7f9fc;
  border-bottom: 1px solid #ebeff3;
  .el-button {
    font-size: 14px;
    padding: 10px;
    background: #0a7aff;
    border: 1px solid #0a7aff;
    color: #fff;
    &:hover {
      color: #fff;
      background: #0a7aff;
      border: 1px solid #0a7aff;
    }
  }
}
.fc-pdf-main-component {
  height: 100%;
  background: #f7f8f9;
  padding: 10px;
}
.fc-pdf-settings {
  height: 100%;
  border-left: 1px solid #ebeff3;
  padding: 15px;
  position: relative;
}
.fc-pdf-block {
  height: calc(100vh - 50px);
}
.fc-pdf-inner-component {
  height: 100vh;
  background: #fff;
}
.fc-pdf-settings-block {
  padding-bottom: 20px;
}
.input-inch {
  top: 12px;
  right: 5px;
  position: absolute;
}
.fc-serttings-btn {
  position: absoulte;
  bottom: 0;
  left: 0;
  right: 0;
}
</style>
