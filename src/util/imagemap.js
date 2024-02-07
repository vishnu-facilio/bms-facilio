const Anchor = {
  NW: 0,
  N: 1,
  NE: 2,
  W: 3,
  E: 4,
  SW: 5,
  S: 6,
  SE: 7,
}

const ImageScale = {
  small: {
    width: 150,
    height: 100,
  },
  best: {
    width: 600,
    height: 440,
  },
}

export default {
  initMap(mapContext) {
    let _this = this
    mapContext.context = mapContext.canvas.getContext('2d')
    if (!mapContext.imageScale) {
      mapContext.imageScale = 'best'
    }

    mapContext.image = new Image()
    mapContext.image.onload = function() {
      mapContext.imageMeta = {
        originalSize: {
          width: this.width,
          height: this.height,
        },
      }
      if (mapContext.imageScale === 'original') {
        mapContext.imageMeta.scaledSize = {
          width: this.width,
          height: this.height,
        }
      } else {
        let scaledSize = mapContext.scaleSize
          ? mapContext.scaleSize
          : _this.scaleImage(
              ImageScale[mapContext.imageScale],
              mapContext.imageMeta.originalSize
            )
        mapContext.imageMeta.scaledSize = scaledSize
      }
      mapContext.canvas.setAttribute(
        'width',
        mapContext.imageMeta.scaledSize.width
      )
      mapContext.canvas.setAttribute(
        'height',
        mapContext.imageMeta.scaledSize.height
      )

      mapContext.canvas
        .getContext('2d')
        .drawImage(
          mapContext.image,
          0,
          0,
          mapContext.imageMeta.scaledSize.width,
          mapContext.imageMeta.scaledSize.height
        )

      if (mapContext.onImageLoad) {
        mapContext.onImageLoad()
      }
    }

    mapContext.reset = () => {
      mapContext.rects = []
      mapContext.deselect()
    }

    mapContext.changeImageScale = scale => {
      let scaledSize = null
      if (typeof scale === 'object') {
        scaledSize = _this.scaleImage(scale, mapContext.imageMeta.originalSize)
      } else {
        if (scale === 'original') {
          scaledSize = {
            width: mapContext.image.width,
            height: mapContext.image.height,
          }
        } else {
          scaledSize = _this.scaleImage(
            ImageScale[mapContext.imageScale],
            mapContext.imageMeta.originalSize
          )
        }
      }
      let oldSize = {
        width: mapContext.imageMeta.scaledSize.width,
        height: mapContext.imageMeta.scaledSize.height,
      }
      mapContext.imageMeta.scaledSize = scaledSize

      mapContext.canvas.setAttribute(
        'width',
        mapContext.imageMeta.scaledSize.width
      )
      mapContext.canvas.setAttribute(
        'height',
        mapContext.imageMeta.scaledSize.height
      )

      mapContext.canvas
        .getContext('2d')
        .drawImage(
          mapContext.image,
          0,
          0,
          mapContext.imageMeta.scaledSize.width,
          mapContext.imageMeta.scaledSize.height
        )

      mapContext.resizeMap(oldSize, scaledSize)
    }

    mapContext.resizeMap = (oldSize, newSize) => {
      let wPercent = newSize.width / oldSize.width
      let hPercent = newSize.height / oldSize.height

      for (let rect of mapContext.rects) {
        rect.x = rect.x * wPercent
        rect.width = rect.width * wPercent
        rect.y = rect.y * hPercent
        rect.height = rect.height * hPercent
      }
      mapContext.redraw = true
    }

    mapContext.load = src => {
      mapContext.reset()
      mapContext.image.src = src
    }

    mapContext.remove = index => {
      mapContext.rects.splice(
        index || mapContext.rects.indexOf(mapContext.selected),
        1
      )
      if (mapContext.rects.length) {
        mapContext.select(mapContext.rects[0])
      } else {
        mapContext.deselect()
      }
      mapContext.redraw = true
    }

    mapContext.add = (area, noSelect) => {
      area.canvas = mapContext.canvas
      mapContext.rects.push(area)
      if (!noSelect) {
        mapContext.select(area)
      }
    }

    mapContext.select = area => {
      mapContext.selected = area
      mapContext.toggleControls(true)
      mapContext.redraw = true
    }

    mapContext.deselect = () => {
      mapContext.selected = null
      mapContext.toggleControls(false)
      mapContext.redraw = true
    }

    mapContext.clear = () => {
      mapContext.context.clearRect(
        0,
        0,
        mapContext.canvas.width,
        mapContext.canvas.height
      )
      if (mapContext.imageMeta && mapContext.imageMeta.scaledSize) {
        mapContext.canvas
          .getContext('2d')
          .drawImage(
            mapContext.image,
            0,
            0,
            mapContext.imageMeta.scaledSize.width,
            mapContext.imageMeta.scaledSize.height
          )
      }
    }

    mapContext.draw = () => {
      if (!mapContext.redraw) return
      mapContext.clear()
      for (let i = mapContext.rects.length; i--; ) {
        mapContext.rects[i].draw(
          mapContext.context,
          mapContext.selected,
          mapContext.assetMappingConfig
        )
      }
      mapContext.redraw = false
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

  initRect(rectContext, mouseX, mouseY) {
    rectContext.constants = {}
    rectContext.constants.FILL = 'rgba(102, 102, 102, 0.7)'
    rectContext.constants.SELECTED_STROKE = '#666666'
    rectContext.constants.SELECTED_FILL = 'rgba(102, 102, 102, 0.7)'
    rectContext.constants.DEFAULT_SIZE = 35
    rectContext.constants.ANCHOR_SIZE = 6
    rectContext.constants.ANCHOR_STROKE = '#666666'
    rectContext.constants.ANCHOR_FILL = '#666666'

    rectContext.x = rectContext.x || mouseX || 0
    rectContext.y = rectContext.y || mouseY || 0
    rectContext.width = rectContext.width || rectContext.constants.DEFAULT_SIZE // default width and height?
    rectContext.height =
      rectContext.height || rectContext.constants.DEFAULT_SIZE
    rectContext.linkType = rectContext.linkType || 'dashboard'
    rectContext.url = rectContext.url || ''
    rectContext.dashboardId = rectContext.dashboardId || ''
    rectContext.reportId = rectContext.reportId || ''
    rectContext.assetId = rectContext.assetId || ''
    rectContext.target = rectContext.target || 'popup'
    rectContext.alt = rectContext.alt || ''

    rectContext.anchors = () => {
      let x = rectContext.x - 6 / 2
      let y = rectContext.y - 6 / 2
      let w = rectContext.width,
        h = rectContext.height
      return [
        {
          x: x,
          y: y,
        },
        {
          x: x + w / 2,
          y: y,
        },
        {
          x: x + w,
          y: y,
        },
        {
          x: x,
          y: y + h / 2,
        },
        {
          x: x + w,
          y: y + h / 2,
        },
        {
          x: x,
          y: y + h,
        },
        {
          x: x + w / 2,
          y: y + h,
        },
        {
          x: x + w,
          y: y + h,
        },
      ]
    }

    rectContext.draw = (context, selected, assetMappingConfig) => {
      let isSelected = selected === rectContext

      if (rectContext.assetId) {
        if (
          assetMappingConfig.showAssetName &&
          assetMappingConfig.assetMap[rectContext.assetId + '']
        ) {
          rectContext.width =
            context.measureText(
              assetMappingConfig.assetMap[rectContext.assetId + ''].asset.name
            ).width +
            2 * 4 +
            22
        } else {
          rectContext.width = 30
        }
        rectContext.height = 35
      }
      context.fillStyle = 'rgba(255, 255, 255, 0.5)'
      context.fillRect(
        rectContext.x,
        rectContext.y,
        rectContext.width,
        rectContext.height
      )
      if (
        rectContext.assetId &&
        assetMappingConfig.assetMap[rectContext.assetId + '']
      ) {
        rectContext.drawTextInBox(
          context,
          assetMappingConfig,
          "'Aktiv-Grotesk', Helvetica, Arial, sans-serif",
          rectContext.x,
          rectContext.y,
          rectContext.width,
          rectContext.height,
          null
        )
      }

      if (isSelected) {
        context.lineWidth = 2
        context.strokeStyle = rectContext.constants.SELECTED_STROKE
        context.strokeRect(
          rectContext.x,
          rectContext.y,
          rectContext.width,
          rectContext.height
        )

        // top left, middle, right
        context.strokeStyle = rectContext.constants.ANCHOR_STROKE
        context.fillStyle = rectContext.constants.ANCHOR_FILL
        let anchors = rectContext.anchors()
        for (let i = 8; i--; ) {
          let anchor = anchors[i]
          context.strokeRect(
            anchor.x,
            anchor.y,
            rectContext.constants.ANCHOR_SIZE,
            rectContext.constants.ANCHOR_SIZE
          )
          context.fillRect(
            anchor.x,
            anchor.y,
            rectContext.constants.ANCHOR_SIZE,
            rectContext.constants.ANCHOR_SIZE
          )
        }
      }
    }

    rectContext.drawTextInBox = (
      context,
      assetMappingConfig,
      font,
      x,
      y,
      w,
      h,
      angle
    ) => {
      angle = angle || 0
      if (assetMappingConfig.showAssetName) {
        let fontHeight = 12
        let hMargin = 4
        context.font = fontHeight + 'px ' + font
        context.textAlign = 'left'
        context.textBaseline = 'top'
        let txtWidth =
          context.measureText(
            assetMappingConfig.assetMap[rectContext.assetId + ''].asset.name
          ).width +
          2 * hMargin
        context.fillStyle =
          assetMappingConfig.assetMap[rectContext.assetId + ''].textColor ||
          assetMappingConfig.defaultTextColor
        context.fillText(
          assetMappingConfig.assetMap[rectContext.assetId + ''].asset.name,
          x + 24,
          y + 11
        )
      }

      context.fillStyle =
        assetMappingConfig.assetMap[rectContext.assetId + ''].dotColor ||
        assetMappingConfig.defaultDotColor
      context.beginPath()
      context.arc(x + 14, y + 16, 6, 0, 2 * Math.PI)
      context.closePath()
      context.fill()

      return txtWidth + 14
    }

    rectContext.transform = (anchor, mouseX, mouseY) => {
      let x = rectContext.x,
        y = rectContext.y
      // w = rectContext.width,
      // h = rectContext.height
      let attrs = {}

      switch (anchor) {
        case Anchor.NW:
          attrs = {
            y: mouseY,
            x: mouseX,
          }
          break
        case Anchor.NE:
          attrs = {
            y: mouseY,
            width: mouseX - x,
          }
          break
        case Anchor.SE:
          attrs = {
            width: mouseX - x,
            height: mouseY - y,
          }
          break
        case Anchor.SW:
          attrs = {
            x: mouseX,
            height: mouseY - y,
          }
          break
        case Anchor.N:
          attrs = {
            y: mouseY,
          }
          break
        case Anchor.E:
          attrs = {
            width: mouseX - x,
          }
          break
        case Anchor.S:
          attrs = {
            height: mouseY - y,
          }
          break
        case Anchor.W:
          attrs = {
            x: mouseX,
          }
          break
      }
      rectContext.set(attrs, true)
    }

    rectContext.normalize = function() {
      let val = Math.round(Math.max(0, Math.min.apply(Math, arguments)))
      return val
    }

    rectContext.set = (attrs, shouldStretch) => {
      let canvasHeight = rectContext.canvas.height
      let canvasWidth = rectContext.canvas.width

      if ('url' in attrs) rectContext.url = attrs.url
      if ('target' in attrs) rectContext.target = attrs.target
      if ('alt' in attrs) rectContext.alt = rectContext.title = attrs.alt

      if ('x' in attrs) {
        if (shouldStretch) {
          let oldX = rectContext.x
          rectContext.x = rectContext.normalize(
            attrs.x,
            rectContext.x + rectContext.width
          )
          rectContext.width = rectContext.width + oldX - rectContext.x
          if (rectContext.width + rectContext.x > canvasWidth) {
            rectContext.width = canvasWidth - rectContext.x
          }
        } else {
          rectContext.x = rectContext.normalize(
            attrs.x,
            rectContext.x + rectContext.width,
            canvasWidth - rectContext.width
          )
        }
      }

      if ('y' in attrs) {
        if (shouldStretch) {
          let oldY = rectContext.y
          rectContext.y = rectContext.normalize(
            attrs.y,
            rectContext.y + rectContext.height
          )
          rectContext.height = rectContext.height + oldY - rectContext.y
          if (rectContext.height + rectContext.y > canvasHeight) {
            rectContext.height = canvasHeight - rectContext.y
          }
        } else {
          rectContext.y = rectContext.normalize(
            attrs.y,
            rectContext.y + rectContext.height,
            canvasHeight - rectContext.height
          )
        }
      }

      if ('width' in attrs && !(rectContext.x === 0 && 'x' in attrs)) {
        rectContext.width = rectContext.normalize(
          attrs.width,
          canvasWidth - rectContext.x
        )
      }

      if ('height' in attrs && !(rectContext.y === 0 && 'y' in attrs)) {
        rectContext.height = rectContext.normalize(
          attrs.height,
          canvasHeight - rectContext.y
        )
      }
    }

    rectContext.nudge = anchor => {
      switch (anchor) {
        case Anchor.N:
          rectContext.set({
            y: rectContext.y - 1,
          })
          break
        case Anchor.S:
          rectContext.set({
            y: rectContext.y + 1,
          })
          break
        case Anchor.W:
          rectContext.set({
            x: rectContext.x - 1,
          })
          break
        case Anchor.E:
          rectContext.set({
            x: rectContext.x + 1,
          })
          break
      }
    }

    rectContext.isWithin = (x, y) => {
      return (
        rectContext.x <= x &&
        rectContext.x + rectContext.width >= x &&
        rectContext.y <= y &&
        rectContext.y + rectContext.height >= y
      )
    }
  },

  updateGenerated(map, updateInputs) {},
}
