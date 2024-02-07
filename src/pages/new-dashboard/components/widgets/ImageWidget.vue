<template>
  <div class="dragabale-card imagecard" v-if="!localLoading">
    <template v-if="url && scaledSize && imageAreas">
      <img
        :usemap="'#' + imagecardData.photoId"
        class="card-img"
        :src="url"
        :width="scaledSize.width"
        :height="scaledSize.height"
      />
      <map :name="imagecardData.photoId">
        <area
          :shape="area.type"
          v-for="(area, index) in imageAreas"
          :key="index"
          :coords="getAreaCoords(area)"
          @click="openArea(area)"
          :alt="area.link.alt"
        />
      </map>
    </template>
    <img
      v-else-if="url && scaledSize"
      :class="{ 'old-card-img': isOldImage }"
      :src="url"
      :width="scaledSize.width"
      :height="scaledSize.height"
    />
    <f-photoUploader
      v-if="showImageEditer"
      :dialogVisible.sync="showImageEditer"
      :widget="clonedItem"
      :editwidget="true"
      @input="showImageEditer = false"
      @upload-failed="imageUploadFailed"
      @update="updateCard"
    />
  </div>
</template>
<script>
import { cloneDeep } from 'lodash'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
import FPhotoUploader from 'src/pages/new-dashboard/components/reusable-components/FPhotoUploader.vue'
export default {
  mixins: [BaseWidgetMixin],
  props: {
    updateWidget: {
      type: Function,
    },
    item: {
      required: true,
      type: Object,
    },
    config: {
      type: Object,
      default: () => ({
        width: 0,
        height: 0,
        actualWidth: 0,
        actualHeight: 0,
      }),
    },
  },
  components: {
    FPhotoUploader,
  },
  computed: {
    clonedItem() {
      const { item } = this
      return cloneDeep(item)
    },
    id() {
      const {
        item: { id },
      } = this
      return id
    },
    widgetConfig() {
      const { id } = this ?? {}
      return {
        id: id,
        minW: 25,
        maxW: 96,
        minH: 10,
        maxH: 50,
        showHeader: false,
        showExpand: false,
        noResize: false,
        showDropDown: true,
        editMenu: [
          {
            label: 'Edit',
            action: () => {
              this.showImageEditer = true
            },
            icon: 'el-icon-edit',
          },
        ],
        borderAroundWidget: true,
        viewMenu: [],
      }
    },
    widget() {
      return this.item.widget
    },
  },
  created() {
    this.initWidget(this.widgetConfig)
  },
  data() {
    return {
      showImageEditer: false,
      imagecardData: null,
      imageAreas: null,
      url: null,
      loading: false,
      oldScaledSize: null,
      scaledSize: null,
      isOldImage: false,
      localLoading: false,
    }
  },
  mounted() {
    if (
      this.widget &&
      this.widget.dataOptions &&
      this.widget.dataOptions.imagecardData
    ) {
      this.imagecardData = this.widget.dataOptions.imagecardData
        ? this.widget.dataOptions.imagecardData
        : null
      this.url = this.widget.dataOptions.imagecardData.url
        ? this.widget.dataOptions.imagecardData.url
        : null
      if (!this.imagecardData || !this.imagecardData.options) {
        this.isOldImage = true
      }
      this.setImageAreas()

      this.loadImage()
    } else {
      this.loadCardData()
    }
  },
  watch: {
    'config.width': function() {
      this.loadImage()
    },
    'config.height': function() {
      this.loadImage()
    },
    'config.editwidget': {
      // need to be removed
      handler(newData, oldData) {
        if (
          this.widget.id > -1 &&
          this.config.selectedWidgetId === this.widget.id &&
          newData !== oldData
        ) {
          this.localLoading = true
          if (this.widget.dataOptions && this.widget.dataOptions.metaJson) {
            this.imagecardData = JSON.parse(this.widget.dataOptions.metaJson)
            let oldScaledSize =
              this.imagecardData &&
              this.imagecardData.options &&
              this.imagecardData.options.imageMeta &&
              this.imagecardData.options.imageMeta.scaledSize
                ? this.imagecardData.options.imageMeta.scaledSize
                : this.oldScaledSize
            this.setImageAreas()
            this.resizeMap(oldScaledSize, this.scaledSize)
          }
        }
      },
      deep: true,
    },
    widget: {
      handler(newData, oldData) {
        if (this.widget.dataOptions && this.widget.dataOptions.metaJson) {
          this.localLoading = true
          this.imagecardData = JSON.parse(this.widget.dataOptions.metaJson)
          let oldScaledSize =
            this.imagecardData &&
            this.imagecardData.options &&
            this.imagecardData.options.imageMeta &&
            this.imagecardData.options.imageMeta.scaledSize
              ? this.imagecardData.options.imageMeta.scaledSize
              : this.oldScaledSize
          this.setImageAreas()
          this.resizeMap(oldScaledSize, this.scaledSize)
        }
      },
      deep: true,
    },
  },
  methods: {
    updateCard(metaJson) {
      const { item } = this
      const clonedCard = cloneDeep(item)
      clonedCard.widget.dataOptions.imagecardData = metaJson
      this.showImageEditer = false
      const { updateWidget } = this
      updateWidget(clonedCard)
    },
    imageUploadFailed() {
      this.$message.error('Image upload failed, please try again later.')
      this.showImageEditer = false
    },
    loadCardData() {
      let self = this
      let params = {
        widgetId: this.widget.id,
      }
      self.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          if (
            response.data.cardResult.widget &&
            response.data.cardResult.widget.metaJson
          ) {
            self.imagecardData = JSON.parse(
              response.data.cardResult.widget.metaJson
            )
            self.setImageAreas()
          } else {
            self.isOldImage = true
          }
          self.url =
            response.data.cardResult && response.data.cardResult.result
              ? response.data.cardResult.result
              : null
          self.loadImage()
          self.loading = false
        })
        .catch(function(error) {
          console.log(error)
        })
    },

    setImageAreas() {
      if (
        this.imagecardData &&
        this.imagecardData.options &&
        this.imagecardData.options.areas &&
        this.imagecardData.options.areas.length
      ) {
        let areaList = []
        for (let area of this.imagecardData.options.areas) {
          areaList.push({
            x: area.x,
            y: area.y,
            width: area.width,
            height: area.height,
            link: area.link,
          })
        }
        this.imageAreas = areaList
      } else if (
        this.imagecardData &&
        this.imagecardData.options &&
        this.imagecardData.options.areas &&
        this.imagecardData.options.areas.length === 0
      ) {
        this.imageAreas = []
      }
      this.localLoading = false
    },

    loadImage() {
      let scaleSize = {
        width: this.$el.offsetWidth,
        height: this.$el.offsetHeight,
      }
      if (!scaleSize.width) {
        // When offsetWidth == 0? Don't understand why this is here.
        scaleSize.width = this.config.actualWidth
      }
      if (!scaleSize.height) {
        scaleSize.height = this.config.actualHeight
      }

      if (
        this.imagecardData &&
        this.imagecardData.options &&
        this.imagecardData.options.imageMeta
      ) {
        let originalSize = this.imagecardData.options.imageMeta.originalSize
        this.oldScaledSize = this.scaledSize
          ? this.scaledSize
          : this.imagecardData.options.imageMeta.scaledSize
        this.scaledSize = this.scaleImage(scaleSize, originalSize)

        this.resizeMap(this.oldScaledSize, this.scaledSize)
      } else {
        let temp = new Image()
        temp.src = this.url
        temp.onload = () => {
          this.scaledSize = this.scaleImage(scaleSize, {
            width: this.width,
            height: this.height,
          })
        }
      }
    },

    getAreaCoords(area) {
      return (
        area.x +
        ',' +
        area.y +
        ',' +
        (area.x + area.width) +
        ',' +
        (area.y + area.height)
      )
    },

    openArea(area) {
      if (!this.$mobile) {
        if (area.link.target === 'popup' && this.$popupView) {
          this.$popupView.openPopup(area.link)
        } else {
          let urlToLoad = null
          if (area.link.type === 'url') {
            urlToLoad = area.link.url
          } else if (area.link.type === 'report') {
            urlToLoad =
              window.location.origin +
              '/app/em/reports/newview/' +
              area.link.reportId
          } else if (area.link.type === 'dashboard') {
            urlToLoad =
              window.location.origin +
              '/app/em/newdashboard/' +
              area.link.dashboardId
          }
          if (urlToLoad) {
            window.open(urlToLoad, area.link.target)
          }
        }
      }
    },

    scaleImage(scaleSize, originalSize) {
      let maxWidth = scaleSize.width
      let maxHeight = scaleSize.height
      let ratio = 0
      let width = originalSize.width
      let height = originalSize.height

      // Check if the current width is larger than the max
      if (width > maxWidth) {
        ratio = maxWidth / width
        height = height * ratio // Reset height to match scaled image
        width = width * ratio // Reset width to match scaled image
      }

      // Check if current height is larger than max
      if (height > maxHeight) {
        ratio = maxHeight / height // get ratio for scaling image
        width = width * ratio // Reset width to match scaled image
        height = height * ratio // Reset height to match scaled image
      }

      return {
        width: width,
        height: height,
      }
    },

    resizeMap(oldSize, newSize) {
      if (!this.imageAreas) {
        return
      }
      let oldWidth = oldSize && oldSize.width ? oldSize.width : null
      let newWidth = newSize && newSize.width ? newSize.width : null
      let oldHeight = oldSize && oldSize.height ? oldSize.height : null
      let newHeight = newSize && newSize.height ? newSize.height : null
      if (oldWidth && newWidth && oldHeight && newHeight) {
        let wPercent = newSize.width / oldSize.width
        let hPercent = newSize.height / oldSize.height
        for (let rect of this.imageAreas) {
          rect.x = rect.x * wPercent
          rect.width = rect.width * wPercent
          rect.y = rect.y * hPercent
          rect.height = rect.height * hPercent
        }
      }
    },
  },
}
</script>

<style>
.imagecard {
  height: 100%;
  width: 100%;
  align-items: center;
  display: flex;
}
.imagecard img {
  margin: auto;
}
.old-card-img {
  width: 100%;
  height: 100%;
}
</style>
