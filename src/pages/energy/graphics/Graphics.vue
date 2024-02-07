<template>
  <el-row class="reports-layout height100 graphics-page">
    <el-col :span="4">
      <div class="reports-sidebar">
        <div class="reports-sidebar-header">
          <div size="mini" split-button type="text" class="fc-black-13">
            {{ $t('common.header.all_graphics') }}
          </div>
          <div class="mR10">
            <button
              class="sh-button button-add shadow-1"
              style="padding: 6px 9px;"
              @click="newGraphicsVisibility = true"
            >
              <i
                class="el-icon-plus"
                style="font-weight: 700;font-size: 12px;"
              ></i>
            </button>
          </div>
        </div>
        <div
          class="report-sidebar-scroll"
          v-if="graphicsFolderList && graphicsFolderList.length > 0"
        >
          <div class="folder-scroll-container">
            <div
              v-for="(folder, index) in graphicsFolderList"
              :key="index"
              class="folder-container"
            >
              <div
                class="rfolder-name uppercase report-new-folder"
                @click="expand(folder)"
              >
                <div class="rfolder-icon fL">
                  <i class="el-icon-arrow-down" v-if="folder.expand"></i>
                  <i class="el-icon-arrow-right" v-else></i>
                </div>
                <div v-if="folder.id !== editFolderId" class="mL5">
                  {{ folder.name }}
                </div>
                <input
                  v-else
                  ref="FolderName"
                  @keyup.enter="editFolderName(folder)"
                  @blur="editFolderName(folder)"
                  v-model="folderState[folder.id].newName"
                />
              </div>
              <div class="edit-icon-set">
                <i
                  @click="toggleEditFolder(folder)"
                  class="el-icon-edit icon-display pointer"
                ></i>
                <i
                  class="el-icon-delete icon-display pointer"
                  @click="deleteReportFolder(folder)"
                ></i>
              </div>
              <div v-show="folder.expand" class="rfolder-children">
                <div
                  class="rempty"
                  v-if="folder.graphics && !folder.graphics.length"
                >
                  {{ $t('common.products.no_graphics') }}
                </div>
                <router-link
                  tag="div"
                  v-for="(graphic, gId) in folder.graphics"
                  :key="gId"
                  :to="'/app/em/graphics/view/' + graphic.id"
                  >{{ graphic.name }}</router-link
                >
              </div>
            </div>
          </div>
        </div>
        <div
          class="no-graphics-folder"
          v-else-if="
            !loading && (!graphicsFolderList || graphicsFolderList.length === 0)
          "
        >
          {{ $T('common.products.no_graphics_folder_avialable') }}
        </div>
        <!-- <div class="rtree" v-if="!loading">
          <div class="report-sidebar-scroll" v-if="graphicsList">
            <div v-for="(graphics, index) in graphicsList" :key="index" class="folder-container">
              <router-link tag="div" class="rfolder-name report-new-folder newreport-folder-name" :to="'/app/em/graphics/view/' + graphics.id">{{graphics.name}}</router-link>
          </div>
          <div v-else>
            No Graphics Available.
          </div>
        </div>-->
      </div>
    </el-col>
    <el-col :span="20" class="reports-summary height100">
      <router-view
        v-if="graphicsId"
        :key="graphicsId"
        @refresh="loadGraphicsFolders"
        @deleted="loadGraphicsFolders"
      />
    </el-col>
    <new-graphics-form
      :formVisibility.sync="newGraphicsVisibility"
      @saved="loadGraphicsFolders"
      v-if="newGraphicsVisibility"
    ></new-graphics-form>
    <f-graphics-builder
      v-if="editGraphicsObj"
      :graphicsContext="editGraphicsObj"
      @close="editorClose"
    ></f-graphics-builder>
  </el-row>
</template>
<script>
import NewGraphicsForm from './NewGraphicsForm'
export default {
  components: {
    NewGraphicsForm,
    FGraphicsBuilder: () => import('pages/assets/graphics/FGraphicsBuilder'),
  },
  title() {
    return 'Graphics'
  },
  data() {
    return {
      loading: true,
      graphicsList: null,
      graphicsFolderList: null,
      newGraphicsVisibility: false,
      editGraphicsObj: null,
      editFolderId: null,
      folderState: {},
    }
  },
  computed: {
    graphicsId() {
      if (this.$route.params.graphicsid) {
        return parseInt(this.$route.params.graphicsid)
      }
      return null
    },
  },
  mounted() {
    this.loadGraphicsFolders()
  },
  methods: {
    loadGraphicsFolders() {
      this.loading = true
      this.$http
        .post('/v2/graphicsFolder/list', { showChildrenGraphics: true })
        .then(response => {
          if (response.data.responseCode === 0) {
            this.graphicsFolderList = response.data.result.graphicsFolders
            for (let key in this.graphicsFolderList) {
              this.$set(this.graphicsFolderList[key], 'expand', false)
              let folder = this.graphicsFolderList[key]
              let folderState = {}
              folderState['name'] = folder.name
              folderState['newName'] = folder.name
              this.folderState[folder.id] = folderState
            }
            this.openGraphics()
          } else {
            this.$message.error(response.data.message)
          }
          this.loading = false
        })
        .catch(error => {
          this.$message.error(error)
          this.loading = false
        })
    },
    deleteReportFolder(folder) {
      if (folder.graphics.length > 0) {
        this.$message({
          message: this.$t('common.wo_report.cannot_delete_graphics'),
          type: 'error',
        })
        return
      }
      let promptObj = {
        title: this.$t('common.wo_report.delete_folder'),
        message: this.$t('common.wo_report.delete_folder_msg'),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      }
      this.$dialog.confirm(promptObj).then(value => {
        if (value) {
          this.$http
            .post('/v2/graphicsFolder/delete', { recordId: folder.id })
            .then(response => {
              if (response.data.responseCode === 1) {
                // delete failed
                this.$message({
                  message: this.$t('common.wo_report.cannot_delete_graphics'),
                  type: 'error',
                })
              } else {
                let index = this.graphicsFolderList.findIndex(
                  obj => obj.id === folder.id
                )
                this.graphicsFolderList.splice(index, 1)
                this.$message({
                  message: this.$t('common.wo_report.folder_delete_success'),
                  type: 'success',
                })
              }
            })
            .catch(error => {
              this.$message({
                message: this.$t('common.wo_report.cannot_delete_graphics'),
                type: 'error',
              })
            })
        }
      })
    },
    editFolderName(folder) {
      let newName = this.folderState[folder.id].newName
      if (newName !== '' && newName !== null) {
        let gFolder = {}
        gFolder['name'] = newName
        gFolder['id'] = folder.id
        this.editFolderId = null
        this.$http
          .post('/v2/graphicsFolder/update', { graphicsFolder: gFolder })
          .then(response => {
            folder.name = response.data.result.graphicsFolder.name
            this.folderState[folder.id].name = folder.name
            this.folderState[folder.id].newName = folder.name
          })
          .catch(() => {
            this.$message({
              message: this.$t('common.wo_report.cannot_rename_folder'),
              type: 'error',
            })
            folder.name = this.folderState[folder.id].name
          })
      } else {
        this.$message({
          message: this.$t('common.wo_report.foldername_cannot_empty'),
          type: 'error',
        })
      }
    },
    openGraphics() {
      if (!this.$route.params.graphicsid) {
        for (let folder of this.graphicsFolderList) {
          if (folder.graphics.length > 0) {
            this.$router.push({
              path: '/app/em/graphics/view/' + folder.graphics[0].id,
            })
            folder.expand = true
            return
          }
        }
      } else {
        if (this.graphicsId) {
          for (let folder of this.graphicsFolderList) {
            for (let grap of folder.graphics) {
              if (grap.id === this.graphicsId) {
                folder.expand = true
                return
              }
            }
          }
        }
      }
    },
    toggleEditFolder(folder) {
      this.editFolderId = folder.id
      this.$nextTick(() => {
        if (Array.isArray(this.$refs['FolderName'])) {
          this.$refs['FolderName'][0].focus()
        } else {
          this.$refs['FolderName'].focus()
        }
      })
    },
    loadGraphics(editGraphicsObj) {
      this.loading = true
      this.$http
        .get('/v2/graphics/list')
        .then(response => {
          if (response.data.responseCode === 0) {
            this.graphicsList = response.data.result.graphics_list

            if (!this.$route.params.graphicsid) {
              this.$router.push({
                path: '/app/em/graphics/view/' + this.graphicsList[0].id,
              })
            }
            this.loading = false
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(error => {
          this.$message.error(error)
          this.loading = false
        })

      if (editGraphicsObj) {
        this.editGraphicsObj = editGraphicsObj
      }
    },
    editGraphics(editGraphicsObj) {
      this.editGraphicsObj = editGraphicsObj
    },
    deleteGraphics(index, graphics) {
      let confirmed = confirm(
        this.$t('common._common.are_you_want_delete_graphics')
      )
      if (confirmed) {
        this.$http
          .post('/v2/graphics/delete', { recordId: graphics.id })
          .then(response => {
            if (response) {
              this.graphicsList.splice(index, 1)
            }
          })
      }
    },
    editorClose() {
      this.editGraphicsObj = null
    },
    expand(folder) {
      folder.expand = !folder.expand
    },
  },
}
</script>
<style scoped lang="scss">
.graphics-page {
  .edit-icon-set {
    position: absolute;
    right: 10px;
    top: 8px;
    z-index: 3;
  }
  .row.reports-layout {
    height: 100%;
  }

  .reports-sidebar {
    background: white;
    height: 100%;
    overflow-y: hidden;
    border-right: 1px solid #6666662f;
    position: relative;
  }

  .rtree {
    position: relative;
    padding-bottom: 70px;
  }

  .rfolder-name {
    padding: 14px 10px 14px 20px;
    cursor: pointer;
    font-size: 12px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.6px;
    text-align: left;
    color: #000000;
  }

  .folder-container {
    position: relative;
  }

  .folder-container:hover .icon-display {
    visibility: visible;
  }
  .icon-display {
    visibility: hidden;
    padding: 5px;
  }
  .rfolder-name i {
    margin-right: 6px;
    font-size: 16px;
    color: #66666696;
  }

  .rfolder-children div {
    padding: 12px 10px 12px 46px;
    display: flex;
    justify-content: space-between;
  }

  .rfolder-children div:not(.rempty) {
    cursor: pointer;
    font-size: 13px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    letter-spacing: 0.4px;
    text-align: left;
    color: #333333;
  }

  .rfolder-name.active,
  .rfolder-children div.active {
    background: #f0f7f8;
  }

  .rfolder-children div:not(.rempty):hover,
  .rfolder-name:hover {
    background: #f3f6f9;
  }

  .rfolder-icon {
    display: inline-block;
  }

  .rempty {
    font-size: 13px;
    color: rgba(102, 102, 102, 0.57);
  }
  .fc-chart-side-btn {
    padding: 0;
    font-size: 13px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 1px;
    text-align: left;
    float: left;
    padding-top: 17px;
    /* color: #717b85; */
  }
  .fc-chart-side-btn .el-button-group button.el-button {
    font-size: 13px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.9px;
    text-align: left;
    /* color: #000000; */
  }
  .m20 {
    margin: 20px;
  }
  .pR10 {
    padding-right: 10px;
  }
  .r-sidebar-btn {
    padding-left: 10px;
    color: #615f89;
  }
  .r-sidebar-btn .el-button {
    color: #615f89;
    border-left: none;
  }
  .r-sidebar-btn .el-button-group .el-button:last-child {
    border-left: none;
  }

  .new-folder-label {
    text-align: center;
    color: #303133;
    font-size: 14px;
    line-height: 1;
    margin-bottom: 12px;
    margin-top: 3px;
  }
  .new-folder-button {
    border: 0px;
    width: 100%;
  }
  .report-new-folder:hover .show-folder-delete {
    display: inline;
    float: right;
  }
  .ul-folder-list {
    list-style-type: none;
    padding-left: 0;
    margin-top: 0;
  }

  .ul-folder-list li {
    padding: 10px;
    cursor: pointer;
    line-height: 24px;
  }
  .ul-folder-list li:hover {
    background: #f0f7f8;
  }

  .folder-popover {
    height: 200px;
    max-width: 200px;
    overflow-y: scroll;
    overflow-x: hidden;
  }
  .show-folder-delete {
    display: none;
    margin-right: 0 !important;
  }
  .folder-list-txt span {
    max-width: 335px;
    line-height: 24px;
  }
  .report-sidebar-scroll {
    height: 100vh;
    overflow-y: scroll;
  }
  .no-graphics-folder {
    height: 100vh;
    text-align: center;
    margin-top: 40px;
  }
  .folder-scroll-container {
    height: 100%;
    padding-bottom: 100px;
    overflow-y: scroll;
  }
}
</style>
