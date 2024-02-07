<template>
  <div class="text-center pT20 asset-image-view-con">
    <spinner v-if="loading" :show="loading"></spinner>
    <div v-else>
      <div class="fplan-zoom-toolbar">
        <i class="el-icon-plus" @click="changeScale(0.1)"></i>
        <i class="el-icon-minus" @click="changeScale(-0.1)"></i>
        <el-popover
          v-model="zoomPopover"
          placement="right"
          popper-class="fplan-zoom-toolbar-popover"
        >
          <ul class="fplan-zoom-toolbar-menu">
            <li v-if="imageShowType === 1" @click="showOriginalImage()">
              Show Original Image
            </li>
            <li v-if="imageShowType === 2" @click="showScaledImage()">
              Show Scaled Image
            </li>
          </ul>
          <i slot="reference" class="el-icon-more"></i>
        </el-popover>
      </div>
      <canvas ref="imageEditorCanvas"></canvas>
      <div
        class="fplan-asset-tooltip-info"
        v-if="selectedAsset && selectedAsset.asset"
        :style="{
          top: selectedAsset.position.top + 'px',
          left: selectedAsset.position.left + 'px',
        }"
      >
        <el-row>
          <el-col :span="24">
            <i
              class="el-icon-close pointer fR"
              @click="selectedAsset.asset = null"
            ></i>
          </el-col>
          <el-col :span="12" class="asset-avatar-col">
            <asset-avatar
              size="shuge"
              :disableUpload="true"
              :disableSlide="true"
              name="false"
              :asset="selectedAsset.asset"
            ></asset-avatar>
          </el-col>
          <el-col :span="12">
            <div>
              <span class="fc-id">#{{ selectedAsset.asset.id }}</span>
            </div>
            <div class="fc-black-17 bold">{{ selectedAsset.asset.name }}</div>
            <div class="label-txt-black">
              {{
                selectedAsset.asset.category
                  ? getAssetCategory(selectedAsset.asset.category.id)
                      .displayName
                  : ''
              }}
            </div>
            <div class="mT30">
              <el-button
                size="mini"
                class="plain"
                @click="openAsset(selectedAsset.asset)"
                >Open Asset</el-button
              >
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import imagemap from 'util/imagemap'
import AssetAvatar from '@/avatar/Asset'
import { mapGetters } from 'vuex'
import { getBaseURL } from 'util/baseUrl'
export default {
  props: ['id', 'assetList'],
  components: {
    AssetAvatar,
  },
  data() {
    return {
      loading: true,
      floorObj: null,
      mapContext: null,
      assetListData: null,
      zoomPopover: false,
      selectedAsset: {
        asset: null,
        position: {
          top: null,
          left: null,
        },
      },
      imageShowType: 1, // 1 - scaled, 2 - original
      currentScaledSize: null,
    }
  },
  mounted() {
    this.loadFloorPlan()
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
  },
  watch: {
    id: function(newVal, oldVal) {
      this.loadFloorPlan()
    },
    assetList: function(newVal, oldVal) {
      this.loadFloorPlan()
    },
  },
  methods: {
    loadFloorPlan() {
      let self = this
      self.loading = true
      self.$http.get('/floor/' + this.id).then(function(response) {
        self.floorObj = response.data.record
        self.loading = false

        if (self.assetList) {
          self.assetListData = self.assetList
          self.$nextTick(() => {
            self.initMap()
          })
        } else {
          self.loadFloorAssets()
        }
      })
    },
    loadFloorAssets() {
      let self = this

      let params = ''
      params =
        params +
        '&filters=' +
        encodeURIComponent(
          JSON.stringify({ space: [{ operatorId: 38, value: [this.id + ''] }] })
        )
      params = params + '&moduleName=asset'
      params = params + '&includeParentFilter=' + true

      let url = '/asset/all' + '?' + params

      this.$http.get(url).then(function(response) {
        self.assetListData = response.data.assets
        self.initMap()
      })
    },
    getFloorPlanImageURL() {
      return getBaseURL() + '/v2/files/preview/' + this.floorObj.floorPlanId
    },
    initMap() {
      let self = this
      let assetMap = {}
      if (this.assetListData && this.assetListData.length) {
        for (let at of this.assetListData) {
          assetMap[at.id + ''] = { asset: at }
        }
      }

      let floorMapInfo = JSON.parse(this.floorObj.floorPlanInfo)

      if (!this.currentScaledSize) {
        if (this.imageShowType === 1) {
          let scaleSize = {
            width: this.$el.offsetWidth,
            height: this.$el.offsetHeight,
          }

          let ratio = Math.min(
            scaleSize.width / floorMapInfo.imageMeta.scaledSize.width,
            scaleSize.height / floorMapInfo.imageMeta.scaledSize.height
          )
          this.currentScaledSize = {
            width: floorMapInfo.imageMeta.scaledSize.width * ratio,
            height: floorMapInfo.imageMeta.scaledSize.height * ratio,
          }
        } else {
          this.currentScaledSize = floorMapInfo.imageMeta.scaledSize
        }
      }

      this.mapContext = {
        canvas: this.$refs['imageEditorCanvas'],
        imageScale: 'custom',
        scaleSize: this.currentScaledSize,
        assetMappingConfig: {
          assetMap: assetMap,
          showAssetName: floorMapInfo.assetMappingConfig.showAssetName,
          defaultDotColor: floorMapInfo.assetMappingConfig.defaultDotColor,
          defaultTextColor: floorMapInfo.assetMappingConfig.defaultTextColor,
        },
      }

      this.resizeMap(
        floorMapInfo.imageMeta.scaledSize,
        this.currentScaledSize,
        floorMapInfo.areas
      )

      this.mapContext.toggleControls = enable => {}

      imagemap.initMap(this.mapContext)

      this.fileReader = new FileReader()
      this.fileReader.onload = e => {
        this.mapContext.load(e.target.result)
        this.imageLoaded = true
      }

      self.mapContext.canvas.onselectstart = function() {
        return false
      }

      self.mapContext.canvas.ondblclick = function() {}

      self.mapContext.canvas.onmousedown = function(e) {}

      self.mapContext.canvas.onclick = function(event) {
        let x = event.offsetX
        let y = event.offsetY
        self.selectedAsset.asset = null

        self.mapContext.rects.forEach(function(rect) {
          if (
            y > rect.y &&
            y < rect.y + rect.height &&
            x > rect.x &&
            x < rect.x + rect.width
          ) {
            let l = event.clientX - 60
            if (l + 300 > window.innerWidth) {
              l -= 300
            }
            let t = event.clientY - 100
            if (t + 150 > window.innerHeight) {
              t -= 160
            }
            self.selectedAsset.position.top = t
            self.selectedAsset.position.left = l
            self.selectedAsset.asset =
              self.mapContext.assetMappingConfig.assetMap[
                rect.assetId + ''
              ].asset
          }
        })
      }

      this.mapContext.onImageLoad = () => {
        this.loadAreas(floorMapInfo.areas)

        this.mapContext.redraw = true
        this.mapContext.draw()
      }

      this.mapContext.load(this.getFloorPlanImageURL())
    },

    loadAreas(areas) {
      for (let area of areas) {
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
        this.mapContext.add(rectContext, true)
      }
    },

    resizeMap(oldSize, newSize, areas) {
      if (!areas) {
        return
      }
      let wPercent = newSize.width / oldSize.width
      let hPercent = newSize.height / oldSize.height
      for (let rect of areas) {
        rect.x = rect.x * wPercent - 14 * wPercent
        rect.width = rect.width * wPercent
        rect.y = rect.y * hPercent - 16 * hPercent
        rect.height = rect.height * hPercent
      }
    },

    changeScale(scalePercent) {
      if (this.currentScaledSize) {
        this.currentScaledSize.width =
          this.currentScaledSize.width +
          this.currentScaledSize.width * scalePercent
        this.currentScaledSize.height =
          this.currentScaledSize.height +
          this.currentScaledSize.height * scalePercent

        this.initMap()
      }
    },

    openAsset(asset) {
      this.$router.push({
        path: '/app/at/assets/all/' + asset.id + '/overview',
      })
      this.selectedAsset.asset = null
    },

    showOriginalImage() {
      this.imageShowType = 2
      this.currentScaledSize = null
      this.zoomPopover = false
      this.initMap()
    },

    showScaledImage() {
      this.imageShowType = 1
      this.currentScaledSize = null
      this.zoomPopover = false
      this.initMap()
    },
  },
}
</script>

<style>
.fplan-asset-tooltip-info {
  position: absolute;
  padding: 12px;
  min-width: 300px;
  text-align: left;
  background-color: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.asset-image-view-con {
  height: calc(100vh - 100px);
  overflow-y: scroll;
  padding-bottom: 180px;
}
.fplan-asset-tooltip-info .fc-avatar,
.fplan-asset-tooltip-info .fc-avatar-square {
  width: 80px !important;
  height: 80px !important;
}
.fplan-asset-tooltip-info .asset-avatar-col {
  max-width: 100px;
}

.fplan-zoom-toolbar {
  position: fixed;
  padding: 8px;
  margin-top: -15px;
}

.fplan-zoom-toolbar i {
  padding: 6px;
  border: 1px solid #39b2c2;
  margin: 6px;
  cursor: pointer;
  background: #39b2c2;
  display: block;
  color: #fff;
  font-weight: 500;
}
.fplan-zoom-toolbar-menu {
  padding: 0;
  margin: 0;
  list-style: none;
}
.fplan-zoom-toolbar-menu li {
  padding: 12px 15px;
  cursor: pointer;
}
.fplan-zoom-toolbar-popover {
  padding: 0px !important;
}
.fplan-zoom-toolbar-menu li:hover {
  background-color: hsla(0, 0%, 96%, 0.5);
}
</style>
