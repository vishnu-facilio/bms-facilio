// this file used to get the dynamic UI properties
export default {
  // function to get the scale size for ui components
  responsiveScale(
    fixedWidth,
    fixedHeight,
    fixedScale,
    currentWidth,
    currentHeight
  ) {
    if (
      fixedWidth &&
      fixedHeight &&
      fixedScale &&
      currentWidth &&
      currentHeight
    ) {
      let scaleWidth = (currentWidth / fixedWidth) * fixedScale
      let scaleHeight = (currentHeight / fixedHeight) * fixedScale
      let scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth
      return {
        zoom: scale /* all browsers */,
        '-moz-transform': `scale('${scale}')` /* Firefox */,
      }
    }
    return {
      zoom: 1 /* all browsers */,
      '-moz-transform': 'scale(1)' /* Firefox */,
    }
  },
}
