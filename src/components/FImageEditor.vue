<template>
  <el-dialog
    custom-class="f-image-editor fc-dialog-center-container"
    :modal-append-to-body="false"
    @open="initDialog"
    @before-close="resetDialog"
    @close="resetDialog"
    :visible.sync="visibility"
    :width="showAssetMapping ? '95%' : '80%'"
    :title="showAssetMapping ? 'Floor Plan - Asset Mapper' : 'Image Card'"
  >
    <div
      class="imgeditor-container"
      @mousemove="handleMouseMove"
      @mouseup="handleMouseUp"
      @dragover="handlePreventDefault"
      @dragend="handlePreventDefault"
      @drop="handleFileDrop"
    >
      <div class="imgeditor-main-area row">
        <div class="imgeditor-image-area col-9">
          <div class="imgeditor-upload-area" v-if="!imageLoaded">
            <input
              id="img"
              @change="handleFileChange($event.target.files[0])"
              type="file"
            />
            <!-- <el-input id="img" @change="handleFileChange($event.target.files[0])" type="file"/> -->
            <img src="~assets/picture.svg" width="150" height="150" />
            <div>
              {{
                $t('common.attachment_form.drag_and_drop_files') +
                  ' ' +
                  $t('common.attachment_form.click_to_browse')
              }}
            </div>
          </div>
          <!-- <el-button @click="zoomIn">Zoom In</el-button>
          <el-button @click="zoomOut">Zoom Out</el-button> -->
          <canvas ref="imageEditorCanvas"></canvas>
        </div>
        <div class="imgeditor-sidebar col-3" v-if="mapContext">
          <!-- <el-row>
            <el-col :span="20">
              <p>Image Scale</p>
              <el-radio-group size="mini" @change="changeImageScale" v-model="mapContext.imageScale">
                <el-radio-button label="small">Small</el-radio-button>
                <el-radio-button label="best">Best Fit</el-radio-button>
                <el-radio-button label="original">Original</el-radio-button>
              </el-radio-group>
            </el-col>
          </el-row> -->
          <el-row v-if="imageLoaded" class="p10">
            <el-col :span="24">
              <div class="f11 mT10" v-if="!showAssetMapping">
                <i class="el-icon-info mR5 fw-bold"></i>Double click the image
                anywhere to add link blocks.
              </div>
              <div class="f11 pT10 pB10" v-else>
                <i class="el-icon-info mR5 fw-bold"></i>Double click the image
                anywhere to add asset mapping.
              </div>
            </el-col>
          </el-row>
          <el-form
            v-if="selectedArea && !showAssetMapping"
            style="margin-top: 20px; border-top: 1px solid #e0e0e0;"
          >
            <div class="linkmap-form-header">Link Mapping</div>
            <el-row>
              <el-col :span="20">
                <el-form-item prop="linkType">
                  <div>Link Type</div>
                  <el-select v-model="selectedArea.linkType">
                    <el-option value="dashboard" label="Dashboard"></el-option>
                    <el-option value="report" label="Report"></el-option>
                    <el-option
                      value="asset"
                      label="Asset"
                      v-if="showAssetMapping"
                    ></el-option>
                    <el-option value="url" label="Custom URL"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20">
                <el-form-item prop="url" v-if="selectedArea.linkType === 'url'">
                  <div>Link</div>
                  <el-input v-model="selectedArea.url"></el-input>
                </el-form-item>
                <el-form-item
                  prop="dashboardId"
                  v-if="selectedArea.linkType === 'dashboard'"
                >
                  <p>Select Dashboard</p>
                  <el-select
                    v-model="selectedArea.dashboardId"
                    filterable
                    placeholder="Select Dashboard"
                  >
                    <el-option-group
                      v-for="(dfolder, index) in dashboardList"
                      v-if="dashboardList"
                      :key="index"
                      :label="dfolder.name"
                    >
                      <el-option
                        v-for="(dashboard, didx) in dfolder.dashboards"
                        :key="didx"
                        :label="dashboard.dashboardName"
                        :value="dashboard.linkName"
                      >
                      </el-option>
                    </el-option-group>
                  </el-select>
                </el-form-item>
                <el-form-item
                  prop="assetId"
                  v-if="selectedArea.linkType === 'asset'"
                >
                  <p>Select Asset</p>
                  <el-select
                    v-model="selectedArea.assetId"
                    @change="setAsset(selectedArea)"
                    filterable
                    placeholder="Select Asset width100"
                  >
                    <el-option
                      v-for="(asset, index) in assetList"
                      :key="index"
                      :label="asset.name"
                      :value="asset.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  prop="reportId"
                  v-if="selectedArea.linkType === 'report'"
                >
                  <p>Select Report</p>
                  <el-select
                    v-model="selectedArea.reportId"
                    filterable
                    placeholder="Select Report"
                  >
                    <el-option-group
                      v-for="(repotFolder, index) in reportList"
                      v-if="reportList"
                      :key="index"
                      :label="repotFolder.name"
                    >
                      <el-option
                        v-for="(report, ridx) in repotFolder.reports"
                        :key="ridx"
                        :label="report.name"
                        :value="report.id"
                      >
                      </el-option>
                    </el-option-group>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20">
                <el-form-item prop="alt" v-if="selectedArea.linkType === 'url'">
                  <p>Alt</p>
                  <el-input v-model="selectedArea.alt"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20">
                <el-form-item prop="target">
                  <p>Target</p>
                  <el-select v-model="selectedArea.target">
                    <el-option value="popup" label="Popup"></el-option>
                    <el-option value="_self" label="Same tab"></el-option>
                    <el-option value="_blank" label="New tab"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20">
                <el-form-item>
                  <el-button
                    type="danger"
                    size="mini"
                    @click="removeSelectedRect"
                    class="pointer"
                    >Remove Link</el-button
                  >
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <el-form
            v-if="selectedArea && showAssetMapping"
            class="pT10 pL20 pR20"
            style="margin-top: 20px; border-top: 1px solid #e0e0e0;"
          >
            <div class="linkmap-form-header">Asset Mapping</div>
            <el-row>
              <el-col :span="20">
                <el-form-item prop="assetId">
                  <p>Select Asset</p>
                  <el-select
                    v-model="selectedArea.assetId"
                    @change="setAsset(selectedArea)"
                    filterable
                    placeholder="Select Asset"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(asset, index) in assetList"
                      :key="index"
                      :label="asset.name"
                      :value="asset.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20">
                <el-form-item>
                  <el-button
                    type="danger"
                    size="mini"
                    @click="removeSelectedRect"
                    class="pointer"
                    >Remove</el-button
                  >
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <el-form
            v-if="imageLoaded && showAssetMapping"
            class="pT10 pL20 pR20"
            style="border-top: 1px solid #e0e0e0;"
          >
            <div class="linkmap-form-header">Customize Asset Map</div>
            <el-row v-if="imageLoaded && mapContext">
              <el-col :span="24">
                <el-form-item prop="showAssetName">
                  <p>Show Asset Name</p>
                  <el-checkbox
                    v-model="mapContext.assetMappingConfig.showAssetName"
                    @change="mapContext.redraw = true"
                  ></el-checkbox>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="defaultTextColor" class="fc-color-picker">
                  <p>Text Color</p>
                  <el-color-picker
                    @change="mapContext.redraw = true"
                    v-model="mapContext.assetMappingConfig.defaultTextColor"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="defaultDotColor" class="fc-color-picker">
                  <p>Dot Color</p>
                  <el-color-picker
                    @change="mapContext.redraw = true"
                    v-model="mapContext.assetMappingConfig.defaultDotColor"
                    :popper-class="'chart-custom-color-picker'"
                  ></el-color-picker>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
    </div>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="closeDialog"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="col-6 modal-btn-save"
        @click="updateImage"
        v-if="update"
        >UPDATE</el-button
      >
      <el-button
        type="primary"
        :disabled="uploadLoading"
        class="col-6 modal-btn-save"
        @click="uploadImage"
        v-else
        >{{ uploadLoading ? 'Uploading...' : 'Upload' }}</el-button
      >
    </span>
  </el-dialog>
</template>

<script>
import imagemap from 'util/imagemap'
import { API } from '@facilio/api'

const Status = {
  IDLE: 0,
  RESIZING: 1,
  DRAGGING: 2,
}

export default {
  props: [
    'visibility',
    'module',
    'conf',
    'editwidget',
    'showAssetMapping',
    'editAssetMapping',
    'spaceId',
    'uploadLoading',
  ],
  data() {
    return {
      imageLoaded: false,
      selectedArea: null,
      update: false,
      status: Status.IDLE,
      selectedFile: null,
      fileReader: null,
      mapContext: null,
      currentAnchor: -1,
      cursors: [
        'nw-resize',
        'n-resize',
        'ne-resize',
        'w-resize',
        'e-resize',
        'sw-resize',
        's-resize',
        'se-resize',
      ],
      mouseX: null,
      mouseY: null,
      mouseOffsetX: null,
      mouseOffsetY: null,
      dashboardList: null,
      reportList: null,
      assetList: null,
      currentScale: 1.0,
      scaleMultiplier: 0.8,
    }
  },
  mounted() {
    if (this.editAssetMapping) {
      this.initDialog(this.editAssetMapping.url)
      this.update = true
    } else if (this.showAssetMapping) {
      this.initDialog()
    } else {
      if (
        this.conf &&
        this.conf.widget &&
        this.conf.widget.id > -1 &&
        this.conf.widget.dataOptions &&
        this.conf.widget.dataOptions.metaJson
      ) {
        let data = JSON.parse(this.conf.widget.dataOptions.metaJson)
        this.initDialog(data.url)
        this.update = true
      }
      if (
        this.conf &&
        this.conf.widget &&
        this.conf.widget.id === -1 &&
        this.conf.widget.dataOptions &&
        this.conf.widget.dataOptions.imagecardData
      ) {
        let data = this.conf.widget.dataOptions.imagecardData
        this.initDialog(data.url)
        this.update = true
      }
    }
    this.loadDashboards()
    this.loadReports()
    this.loadAssets()
  },

  methods: {
    initDialog(url) {
      this.$nextTick(() => {
        this.initUpload(url)
      })
    },

    resetDialog() {
      this.mapContext = null
      this.update = false
      this.$emit('close')
    },

    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('close')
    },

    zoomIn() {
      this.currentScale += this.scaleMultiplier
      this.mapContext.context.scale(this.currentScale, this.currentScale)
      this.mapContext.redraw = true
    },

    zoomOut() {
      this.currentScale -= this.scaleMultiplier
      this.mapContext.context.scale(this.currentScale, this.currentScale)
      this.mapContext.redraw = true
    },

    uploadImage() {
      let imageObj = {
        file: this.selectedFile,
        areas: this.getAreas(),
        imageMeta: this.mapContext.imageMeta,
        assetMappingConfig: {
          showAssetName: this.mapContext.assetMappingConfig.showAssetName,
          defaultDotColor: this.mapContext.assetMappingConfig.defaultDotColor,
          defaultTextColor: this.mapContext.assetMappingConfig.defaultTextColor,
        },
      }
      this.$emit('onupload', imageObj)
    },
    updateImage() {
      if (this.showAssetMapping) {
        let data = {
          imageMeta: this.mapContext.imageMeta,
          areas: this.getAreas(),
          assetMappingConfig: {
            showAssetName: this.mapContext.assetMappingConfig.showAssetName,
            defaultDotColor: this.mapContext.assetMappingConfig.defaultDotColor,
            defaultTextColor: this.mapContext.assetMappingConfig
              .defaultTextColor,
          },
        }
        this.$emit('onupdate', data)
      } else {
        let imageObj = {
          file: this.selectedFile,
          areas: this.getAreas(),
          imageMeta: this.mapContext.imageMeta,
        }
        let data =
          this.conf && this.conf.widget && this.conf.widget.id > -1
            ? JSON.parse(this.conf.widget.dataOptions.metaJson)
            : this.conf.widget.dataOptions.imagecardData
        data.options = {
          imageMeta: imageObj.imageMeta,
          areas: imageObj.areas,
        }
        this.$emit('onupdate', data)
      }
    },
    handleMouseUp(e) {
      if (this.status !== Status.IDLE) {
        this.status = Status.IDLE
        this.currentAnchor = -1
      }
    },

    handlePreventDefault(e) {
      e.preventDefault()
    },

    handleMouseMove(e) {
      let offsets = {
        top: 0,
        left: 0,
      }
      if (this.mapContext) {
        offsets.top =
          this.mapContext.canvas.getBoundingClientRect().top + window.scrollY
        offsets.left =
          this.mapContext.canvas.getBoundingClientRect().left + window.scrollX
      }
      this.mouseX = Math.floor(Math.max(0, e.pageX - offsets.left))
      this.mouseY = Math.floor(Math.max(0, e.pageY - offsets.top))

      if (this.status === Status.DRAGGING) {
        let s = {
          x: this.mouseX - this.mouseOffsetX,
          y: this.mouseY - this.mouseOffsetY,
        }
        this.mapContext.selected.set(s)
        this.mapContext.redraw = true
      } else if (this.status === Status.RESIZING) {
        this.mapContext.selected.transform(
          this.currentAnchor,
          this.mouseX,
          this.mouseY
        )
        this.mapContext.redraw = true
      } else if (this.mapContext && this.mapContext.selected) {
        // see if we're hovering over an anchor
        let anchors = this.mapContext.selected.anchors()
        for (let i = 8; i--; ) {
          let anchor = anchors[i]
          // we dont need to use the ghost context because
          // selection handles will always be rectangles
          if (
            this.mouseX >= anchor.x &&
            this.mouseX <= anchor.x + 6 &&
            this.mouseY >= anchor.y &&
            this.mouseY <= anchor.y + 6
          ) {
            // we found one!
            this.currentAnchor = i
            this.mapContext.redraw = true
            e.target.style.cursor = this.cursors[i]
            return
          }
        }
        this.currentAnchor = -1
        e.target.style.cursor = this.mapContext.selected.isWithin(
          this.mouseX,
          this.mouseY
        )
          ? 'move'
          : 'auto'
      }
    },

    initUpload(editImageURL) {
      let self = this
      this.mapContext = {
        canvas: this.$refs['imageEditorCanvas'],
        imageScale: 'original',
        assetMappingConfig: {
          assetMap: {},
          showAssetName: false,
          defaultDotColor: '#FF0000',
          defaultTextColor: '#000000',
        },
      }

      if (this.editAssetMapping) {
        this.mapContext.assetMappingConfig.showAssetName = this.editAssetMapping.assetMappingConfig.showAssetName
        this.mapContext.assetMappingConfig.defaultDotColor = this.editAssetMapping.assetMappingConfig.defaultDotColor
        this.mapContext.assetMappingConfig.defaultTextColor = this.editAssetMapping.assetMappingConfig.defaultTextColor
      }

      this.mapContext.toggleControls = enable => {
        if (enable) {
          this.selectedArea = this.mapContext.selected
        } else {
          this.selectedArea = null
        }
      }

      imagemap.initMap(this.mapContext)

      this.fileReader = new FileReader()
      this.fileReader.onload = e => {
        this.mapContext.load(e.target.result)
        this.imageLoaded = true
      }

      self.mapContext.canvas.onselectstart = function() {
        return false
      }

      self.mapContext.canvas.ondblclick = function() {
        let rectContext = {
          canvas: self.mapContext.canvas,
        }
        imagemap.initRect(rectContext, self.mouseX, self.mouseY)
        self.mapContext.add(rectContext)
      }

      self.mapContext.canvas.onmousedown = function(e) {
        if (self.currentAnchor !== -1) {
          self.status = Status.RESIZING
        } else if (self.status !== Status.DRAGGING) {
          for (let i = self.mapContext.rects.length; i--; ) {
            let rect = self.mapContext.rects[i]
            if (rect.isWithin(self.mouseX, self.mouseY)) {
              self.status = Status.DRAGGING
              self.mouseOffsetX = self.mouseX - rect.x
              self.mouseOffsetY = self.mouseY - rect.y
              if (self.mapContext.selected !== rect) {
                self.mapContext.select(rect)
              }
              return
            }
          }
          self.status = Status.IDLE
          self.mapContext.deselect()
        }
      }
      if (editImageURL) {
        this.mapContext.onImageLoad = () => {
          this.loadAreas()
        }

        this.mapContext.load(editImageURL)
        this.imageLoaded = true
      }
      window.requestAnimationFrame =
        window.requestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        window.oRequestAnimationFrame

      function draw() {
        if (self.mapContext) {
          self.mapContext.draw()
          window.requestAnimationFrame(draw)
        }
      }
      window.requestAnimationFrame(draw)

      document
        .querySelector('.imgeditor-image-area')
        .addEventListener('paste', e => {
          this.handleFileChange(e.clipboardData.items[0].getAsFile())
        })
    },

    setAsset(selectedArea) {
      selectedArea.linkType = 'asset'
      let asset = this.assetList.find(at => at.id === selectedArea.assetId)
      this.mapContext.assetMappingConfig.assetMap[asset.id + ''] = {
        asset: asset,
      }
      this.mapContext.redraw = true
    },

    changeImageScale() {
      this.mapContext.changeImageScale(this.mapContext.imageScale)
    },

    handleFileDrop(e) {
      if (
        e.dataTransfer &&
        e.dataTransfer.files &&
        e.dataTransfer.files.length
      ) {
        this.handleFileChange(e.dataTransfer.files[0])
      }
    },

    handleFileChange(file) {
      this.selectedFile = file
      this.fileReader.readAsDataURL(file)
    },

    addRect() {
      let rectContext = {
        canvas: this.mapContext.canvas,
      }
      imagemap.initRect(rectContext, this.mouseX, this.mouseY)
      this.mapContext.add(rectContext)
    },

    getAreas() {
      let areas = []
      if (this.mapContext) {
        if (this.mapContext.rects && this.mapContext.rects.length) {
          for (let rect of this.mapContext.rects) {
            areas.push({
              x: rect.x,
              y: rect.y,
              width: rect.width,
              height: rect.height,
              type: 'rect',
              link: {
                type: rect.linkType,
                url: rect.url,
                alt: rect.alt,
                dashboardId: rect.dashboardId,
                reportId: rect.reportId,
                assetId: rect.assetId,
                target: rect.target,
              },
            })
          }
        }
      }
      return areas
    },
    loadAreas() {
      if (this.editAssetMapping) {
        for (let area of this.editAssetMapping.areas) {
          let rectContext = {
            canvas: this.mapContext.canvas,
            x: area.x,
            y: area.y,
            width: area.width,
            height: area.height,
            linkType: area.link.type,
            url: area.link.url,
            dashboardId: area.link.dashboardId,
            reportId: area.link.reportId,
            assetId: area.link.assetId,
            alt: area.link.alt,
            target: area.link.target,
          }
          imagemap.initRect(rectContext, this.mouseX, this.mouseY)
          this.mapContext.add(rectContext)
        }
        return
      }
      if (
        this.conf.widget &&
        this.conf.widget.id > -1 &&
        this.conf.widget.dataOptions &&
        this.conf.widget.dataOptions.metaJson
      ) {
        let data = JSON.parse(this.conf.widget.dataOptions.metaJson)
        if (data.options && data.options.areas && data.options.areas.length) {
          for (let area of data.options.areas) {
            let rectContext = {
              canvas: this.mapContext.canvas,
              x: area.x,
              y: area.y,
              width: area.width,
              height: area.height,
              linkType: area.link.type,
              url: area.link.url,
              dashboardId: area.link.dashboardId,
              reportId: area.link.reportId,
              alt: area.link.alt,
              target: area.link.target,
            }
            imagemap.initRect(rectContext, this.mouseX, this.mouseY)
            this.mapContext.add(rectContext)
          }
        }
      } else if (
        this.conf.widget &&
        this.conf.widget.id === -1 &&
        this.conf.widget.dataOptions &&
        this.conf.widget.dataOptions.imagecardData
      ) {
        let data = this.conf.widget.dataOptions.imagecardData
        if (data.options && data.options.areas && data.options.areas.length) {
          for (let area of data.options.areas) {
            let rectContext = {
              canvas: this.mapContext.canvas,
              x: area.x,
              y: area.y,
              width: area.width,
              height: area.height,
              linkType: area.link.type,
              url: area.link.url,
              dashboardId: area.link.dashboardId,
              reportId: area.link.reportId,
              alt: area.link.alt,
              target: area.link.target,
            }
            imagemap.initRect(rectContext, this.mouseX, this.mouseY)
            this.mapContext.add(rectContext)
          }
        }
      }
    },
    removeSelectedRect() {
      this.mapContext.remove()
    },

    reset() {
      this.mapContext.reset()
    },

    loadDashboards() {
      if (!this.module) {
        return
      }
      let self = this
      this.$http
        .get('/dashboardTree?moduleName=' + this.module)
        .then(function(response) {
          self.dashboardList = response.data.dashboardFolders
        })
    },

    loadReports() {
      if (!this.module) {
        return
      }
      let self = this

      API.get('/v3/report/folders?moduleName=' + this.module).then(response => {
        if (!response.error) {
          self.reportList = response.data ? response.data.reportFolders : null
        }
      })
    },

    loadAssets() {
      if (!this.module) {
        return
      }
      let self = this

      let url = '/asset/all'
      if (self.spaceId) {
        let params = ''
        params =
          params +
          '&filters=' +
          encodeURIComponent(
            JSON.stringify({
              space: [{ operatorId: 38, value: [this.spaceId + ''] }],
            })
          )
        params = params + '&moduleName=asset'
        params = params + '&includeParentFilter=' + true

        url = url + '?' + params
      }

      this.$http.get(url).then(function(response) {
        self.assetList = response.data.assets

        if (self.editAssetMapping) {
          for (let area of self.editAssetMapping.areas) {
            let asset = self.assetList.find(at => at.id === area.link.assetId)
            self.mapContext.assetMappingConfig.assetMap[
              area.link.assetId + ''
            ] = { asset: asset }
          }
          self.mapContext.redraw = true
        }
      })
    },
  },
}
</script>

<style>
.imgeditor-actions {
  text-align: right;
}

.imgeditor-toolbar {
  padding: 15px;
  background: #f4f4f4;
  border-bottom: 1px solid #e0e0e0;
}

.imgeditor-main-area {
  height: 600px;
  overflow: scroll;
  position: relative;
}

.imgeditor-sidebar {
  background: #f4f4f4;
  border-left: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  height: 100%;
  overflow: hidden;
}

.imgeditor-upload-area {
  width: 75%;
  padding: 100px;
  border: 1px solid #eee;
  border-style: dotted;
  border-radius: 4px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  top: 12%;
  background: #fafafa;
  cursor: pointer;
}

.imgeditor-upload-area span {
  top: 45%;
  position: relative;
  font-size: 18px;
}

.imgeditor-upload-area input[type='file'] {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: 1;
  left: 0;
  top: 0;
}

.imgeditor-image-area {
  text-align: center;
  overflow: scroll;
  padding-top: 20px;
  padding-bottom: 50px;
}
.imgeditor-container .linkmap-form-header {
  font-weight: 500;
  padding: 10px 0;
}

.imgeditor-sidebar .el-form-item__content p {
  line-height: 0px;
  font-size: 12px;
  margin-top: 10px;
}

.imgeditor-sidebar .el-form-item {
  margin-bottom: 18px !important;
}

.f-image-editor .el-dialog__body {
  padding: 0 0px !important;
}
.f-image-editor .el-dialog__header {
  padding-bottom: 32px;
}
.f-image-editor .el-dialog__header .el-dialog__title {
  float: left;
}
.f-image-editor {
  margin-top: 10vh !important;
}
.dashboard-container.editmode .fc-widget:hover .chart-delete-icon {
  background: #fff;
  opacity: 1;
  width: 20px;
  height: 30px;
  padding-bottom: 5px;
}
</style>
