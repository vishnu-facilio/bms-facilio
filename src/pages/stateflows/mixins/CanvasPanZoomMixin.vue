<script>
import { isEmpty } from '@facilio/utils/validation'
import { fabric } from 'fabric'
import throttle from 'raf-throttle'

export default {
  data() {
    return {
      currentZoomLevel: 1,
      isCanvasPanning: false,
      allowedCanvasPanSize: 1800, // How much further the canvas extends off screen (along both axis) in px
    }
  },
  created() {
    this.lastPosX = null
    this.lastPosY = null
  },
  computed: {
    formattedZoomLevel() {
      return Math.round(this.currentZoomLevel * 100)
    },
  },
  methods: {
    setDimensions(refElement) {
      let container = this.$refs[refElement]

      if (isEmpty(container)) return

      this.canvas.setHeight(container.offsetHeight)
      this.canvas.setWidth(container.offsetWidth)
      this.canvas.renderAll()
    },

    setInitialZoom() {
      let zoom = !isEmpty(this.diagramJson && this.diagramJson.zoom)
        ? this.diagramJson.zoom
        : 0.75

      this.canvas.setZoom(zoom)
      this.currentZoomLevel = zoom
    },

    adjustZoom(zoomIn = false, delta = 0.125) {
      let zoom = this.canvas.getZoom()

      if (zoomIn) {
        zoom += delta
      } else {
        zoom -= delta
      }

      if (zoom > 1.75) zoom = 1.75
      if (zoom < 0.5) zoom = 0.5

      this.canvas.setZoom(zoom)
      this.currentZoomLevel = zoom
      this.$nextTick(() => this.autoSave())
    },

    fitToScreen() {
      let delta = 0
      let edgeBuffer = 50 * 2 // Create a 70px buffer on both sides

      let canvas = this.canvas
      let zoom = canvas.getZoom()
      let canvasH = canvas.getHeight() - edgeBuffer
      let canvasW = canvas.getWidth() - edgeBuffer

      // Create a group with all objects and take the dimensions from it
      // https://github.com/fabricjs/fabric.js/issues/1745
      let objs = canvas.getObjects()
      let g = new fabric.Group(objs)
      let { width, height } = g.getBoundingRect()
      g.ungroupOnCanvas()

      width *= zoom
      height *= zoom

      let shouldScaleDown = width > canvasW || height > canvasH
      let shouldScaleUp = width < canvasW && height < canvasH

      if (shouldScaleDown || shouldScaleUp) {
        let Wx = canvasW / width
        let Hx = canvasH / height

        delta = Wx < Hx ? Wx : Hx

        if (zoom > 1.75) zoom = 1.75
        if (zoom < 0.5) zoom = 0.5

        canvas.setZoom(zoom * delta)
        this.currentZoomLevel = zoom * delta
      }
    },

    onCanvasPanStart({ e }) {
      this.isCanvasPanning = true
      this.lastPosX = e.clientX
      this.lastPosY = e.clientY

      this.canvas.defaultCursor = 'grabbing'
      this.canvas.renderAll()
    },

    onCanvasPan: throttle(function({ e }) {
      // https://stackoverflow.com/a/52504899
      if (this.isCanvasPanning) {
        let canvas = this.canvas

        let xOffset = e.clientX - this.lastPosX
        let yOffset = e.clientY - this.lastPosY
        let xDiffWidth = this.allowedCanvasPanSize - canvas.getWidth()
        let yDiffHeight = this.allowedCanvasPanSize - canvas.getHeight()

        let transformArray = canvas.viewportTransform

        let isXWithinBounds =
          transformArray[4] + xOffset >= -xDiffWidth &&
          transformArray[4] + xOffset <= 0
        let isYWithinBounds =
          transformArray[5] + yOffset >= -yDiffHeight &&
          transformArray[5] + yOffset <= 0

        if (isXWithinBounds) {
          transformArray[4] += xOffset
          this.lastPosX = e.clientX
          canvas.requestRenderAll()
        } else if (transformArray[4] + xOffset >= -xDiffWidth) {
          transformArray[4] = 0
          this.lastPosX = e.clientX
          canvas.requestRenderAll()
        }

        if (isYWithinBounds) {
          transformArray[5] += yOffset
          this.lastPosY = e.clientY
          canvas.requestRenderAll()
        } else if (transformArray[5] + yOffset >= -yDiffHeight) {
          transformArray[5] = 0
          this.lastPosY = e.clientY
          canvas.requestRenderAll()
        }
      }
    }),

    onCanvasPanEnd() {
      this.isCanvasPanning = false
      this.canvas.getObjects().forEach(obj => obj.setCoords())

      this.canvas.defaultCursor = 'grab'
      this.$nextTick(() => this.canvas.renderAll())
    },
  },
}
</script>
