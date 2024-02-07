<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog assetaddvaluedialog fc-web-form-dialog fc-item-type-summary-dialog"
    :before-close="closeDialog"
    style="z-index: 999999;"
  >
    <el-form ref="newAssetReadingForm" :model="module" :label-position="'top'">
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div class="setup-modal-title">Populate Data</div>
          </div>
        </div>
      </div>
      <div
        class="setup-formula-block2"
        style="border-top: none;background: #f5f8fb;border-bottom: 1px solid #e2e8ee;padding-top: 20px;padding-bottom: 27px;"
      >
        <el-row>
          <el-col :span="11">
            <div class="fc-input-label-txt">Time</div>
            <f-date-picker
              ref="datepick"
              autofocus
              v-model="ttime"
              @change="updateDuration()"
              :type="'datetime'"
              class="datepicker-icon input-background-remove border-fill"
            >
            </f-date-picker>
          </el-col>
          <el-col :span="8" v-if="field.categoryId">
            <p class="label-txt-blue">Category</p>
            <p class="label-txt-black">
              {{
                field.categoryId
                  ? getAssetCategory(parseInt(field.categoryId)).displayName
                  : ''
              }}
            </p>
          </el-col>
          <el-col :span="5">
            <p class="label-txt-blue">Field Name</p>
            <p class="label-txt-black">{{ field.displayName }}</p>
          </el-col>
        </el-row>
      </div>
      <div class="">
        <div
          class="new-body-modal"
          style="margin-top: 0;height: calc(100vh - 180px);padding-bottom: 50px;"
        >
          <div class="mT20">
            <div class="fc-text-pink2">Enter the reading value</div>
            <p class="grey-text2 line-height20">
              For each asset enter corresponding reading value
            </p>
          </div>
          <table
            class="setting-list-view-table overflow-scroll asset-add-table width100"
          >
            <thead>
              <tr>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 40%;"
                >
                  Asset NAME
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 60%;text-align: left;"
                >
                  Value
                </th>
                <div class="search-container">
                  <div
                    class="row"
                    style="margin-right: 20px"
                    v-show="showQuickSearch"
                  >
                    <div
                      class="fc-list-search"
                      style="width: 97%;background: #fff;position: absolute;z-index: 1;"
                    >
                      <div class="fc-list-search-wrapper relative">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="search-icon2"
                        >
                          <title>search</title>
                          <path
                            d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                          ></path>
                        </svg>
                        <input
                          ref="quickSearchQuery"
                          autofocus
                          type="text"
                          v-model="quickSearchQuery"
                          @keyup="quickSearch"
                          placeholder="Search"
                          class="quick-search-input2"
                        />
                        <svg
                          @click="closeSearch"
                          xmlns="http://www.w3.org/2000/svg"
                          width="32"
                          height="32"
                          viewBox="0 0 32 32"
                          class="close-icon2"
                          aria-hidden="true"
                        >
                          <title>close</title>
                          <path
                            d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                          ></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                  <div class="pointer" @click="toggleQuickSearch">
                    <i
                      class="fa fa-search"
                      aria-hidden="true"
                      style="font-size: 14px;position: absolute;right: 40px;top: 25px;"
                    ></i>
                  </div>
                </div>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!assetList.length">
              <tr>
                <td colspan="100%" class="text-center">No Asset available.</td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(asset, index) in filterAsset"
                :key="index"
                v-loading="loading"
              >
                <td style="width: 40%;">{{ asset.name }}</td>
                <td style="width: 60%;text-align: left;">
                  <el-input-number
                    v-if="['NUMBER', 'DECIMAL'].includes(field.dataTypeEnum)"
                    v-model="readingValues[asset.id]"
                    controls-position="right"
                    :min="null"
                    :controls="false"
                    class="border-fill"
                  ></el-input-number>
                  <el-select
                    v-else-if="['BOOLEAN'].includes(field.dataTypeEnum)"
                    v-model="readingValues[asset.id]"
                    class="border-fill boolean-select"
                  >
                    <el-option label="True" value="true"></el-option>
                    <el-option label="False" value="false"></el-option>
                  </el-select>
                  <el-input
                    v-else
                    v-model="readingValues[asset.id]"
                    class="input-background-remove border-fill addassetinput"
                  ></el-input>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="save" class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { mapGetters } from 'vuex'
export default {
  title() {
    return 'Reading Value'
  },
  components: {
    FDatePicker,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  props: ['field', 'visibility', 'module'],
  data() {
    return {
      loading: true,
      readingValues: {},
      readingData: {
        readingName: '',
        readingValues: [],
      },
      quickSearchQuery: null,
      ttime: '',
      filterAsset: [],
      showQuickSearch: false,
      assetList: [],
      showDialog: false,
      input: '',
    }
  },
  mounted() {
    this.loadAssetList()
    this.ttime = new Date().getTime()
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
  },
  methods: {
    closeSearch() {
      this.toggleQuickSearch()
      this.quickSearchQuery = null
      this.quickSearch()
    },
    updateDuration() {
      if (!this.ttime) {
        this.$nextTick(() => {
          this.$refs.datepick.focus()
        })
      }
    },
    toggleQuickSearch() {
      this.showQuickSearch = !this.showQuickSearch
      if (this.showQuickSearch) {
        this.$nextTick(() => {
          this.$refs.quickSearchQuery.focus()
        })
        this.$refs.quickSearchQuery.focus()
      }
    },
    save() {
      this.readingData.readingName = this.field.module.name
      for (let key in this.readingValues) {
        let value = this.readingValues[key]
        if (value) {
          this.readingData.readingValues.push({
            parentId: key,
            ttime: this.ttime ? this.ttime : -1,
            readings: { [this.field.name]: value },
          })
        }
      }
      this.$http
        .post('/reading/addassetdata', this.readingData)
        .then(response => {
          if (typeof response.data === 'object') {
            this.$message({
              message: 'Reading added successfully!',
              type: 'success',
            })
            this.$emit('update:visibility', false)
          } else {
            this.$message({
              message: 'Reading entry failed',
              type: 'error',
            })
          }
        })
    },
    quickSearch() {
      let string = this.quickSearchQuery
      if (string) {
        this.filterAsset = this.assetList.filter(function(cust) {
          return cust.name.toLowerCase().indexOf(string.toLowerCase()) >= 0
        })
      } else {
        this.filterAsset = this.assetList
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    loadAssetList() {
      this.$util
        .loadAsset({ readingId: this.field.id, inputType: 1 })
        .then(response => {
          if (response.assets) {
            this.assetList = response.assets
            this.filterAsset = this.assetList
            this.readingData.readingName = this.field.module.name
            this.loading = false
          }
        })
    },
  },
}
</script>

<style>
.new-header-container {
  margin-top: 0 !important;
}
.datepicker-icon {
  margin-top: 10px;
}
.datepicker-icon .el-input__prefix {
  right: 0;
  top: -4px;
  left: 86%;
}
.datepicker-icon .el-input__inner {
  height: 33px;
  padding-left: 10px;
}
.addassetinput .el-input__inner {
  padding-left: 10px;
  height: 30px;
}
.quick-search-input2 {
  transition: 0.2s linear;
  padding: 24px 40px 8px 38px !important;
  line-height: 1.8;
  width: 100%;
  margin-bottom: 5px;
  border: none !important;
  outline: none;
  background: transparent;
  height: 52px;
  font-size: 16px;
}
.fc-list-search-wrapper .search-icon2 {
  width: 17px;
  fill: #d0d9e2;
  height: 20px;
  top: 23px;
  left: 14px;
  position: absolute;
}
.fc-list-search-wrapper .close-icon2 {
  width: 20px;
  fill: #d0d9e2;
  height: 20px;
  position: absolute;
  right: 18px;
  top: 23px;
  cursor: pointer;
}
.search-container {
  position: absolute;
  width: 91%;
  left: 40px;
}
.asset-add-table tbody tr:hover {
  border: none !important;
}
.asset-add-table tbody tr:hover td {
  border-top: none !important;
}
.boolean-select .el-input__inner {
  width: 250px;
  height: 33px;
  padding-left: 12px;
}
.el-date-editor--datetime .el-input__suffix-inner .el-input__icon:before {
  right: 20px;
  top: -4px;
  position: relative;
}
</style>
