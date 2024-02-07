import paletteGenerator from 'src/pages/report/mixins/paletteGenerator'
import colors from './chartGroupedColors'

export default {
  newColorPicker(dataPoints, existingColors) {
    let colors = paletteGenerator.generate(
      dataPoints, // Colors
      function(color) {
        // This function filters valid colors
        let hcl = color.hcl()
        return (
          0 <= hcl[0] <= 69 &&
          177 <= hcl[0] <= 360 &&
          hcl[1] >= 0 &&
          hcl[1] <= 85 &&
          hcl[2] >= 20 &&
          hcl[2] <= 95
        )
      },
      true, // Using Force Vector instead of k-Means
      50, // Steps (quality)
      false, // Ultra precision
      'Default' // Color distance type (colorblindness)
    )
    colors = colors.map(color => color.hex())
    if (existingColors && existingColors.length !== 0) {
      for (let color of colors) {
        if (existingColors.includes(color)) {
          let index = colors.indexOf(color)
          colors.splice(index, 1)
        }
      }
    }
    return colors
  },
  colorPicker(dataPoints, stateType) {
    if (!stateType) {
      stateType = 1
    }
    let choosenColor = []
    let choosenShade = []
    let state = this.stateTypeEnum[stateType]
    switch (state) {
      case 'NORMAL': {
        choosenShade = this.stateNormal(dataPoints)
        break
      }
      case 'RANK': {
        let colorNames = Object.keys(colors.colors)
        let color = colorNames[Math.floor(Math.random() * colorNames.length)]
        let shades = colors.colors[color]
        choosenColor.push(color)
        if (shades.length < dataPoints) {
          for (let i = 0; i < dataPoints; i++) {
            choosenShade.push(shades[i])
          }
        } else {
          choosenShade = shades
        }
        break
      }
      case 'HIGH': {
        choosenColor = []
        choosenShade = []
        let colorNames = Object.keys(colors.colors)
        for (let i = 0; i < dataPoints; i++) {
          let color = colorNames[Math.floor(Math.random() * colorNames.length)]
          let shades = colors.colors[color]
          if (choosenColor.includes(color)) {
            let presentShades = choosenShade.filter(element =>
              shades.includes(element)
            )
            let shade = presentShades[presentShades.length - 1]
            choosenShade.push(shades.indexOf(shade) - 1)
            continue
          } else {
            choosenColor.push(color)
            choosenShade.push(shades[shades.length - 1])
          }
        }
        break
      }
      default: {
        choosenColor = []
        choosenShade = []
        choosenShade = this.stateNormal(dataPoints)
        break
      }
    }
    // sorting colors using hexsorter
    let sortedShades = []
    for (let j = choosenShade.length - 1; j >= 0; j--) {
      let shade = choosenShade[j]
      sortedShades.push(shade)
      choosenShade.splice(choosenShade.indexOf(shade), 1)
    }
    return sortedShades.reverse()
  },

  stateNormal(dataPoints) {
    let choosenColor = []
    let choosenShade = []
    let colorNames = Object.keys(colors.colors)
    for (let i = 0; i < dataPoints; i++) {
      let color = colorNames[Math.floor(Math.random() * colorNames.length)]
      let shades = colors.colors[color]
      if (
        choosenColor.includes(color) &&
        choosenColor.length >= colorNames.length
      ) {
        let shade = shades[Math.floor(Math.random() * shades.length)]
        if (choosenShade.includes(shade)) {
          i = i - 1
          continue
        } else {
          choosenShade.push(shades[Math.floor(Math.random() * shades.length)])
        }
      } else {
        if (choosenColor.includes(color)) {
          i = i - 1
        } else {
          let shade = shades[Math.floor(Math.random() * shades.length)]
          choosenColor.push(color)
          choosenShade.push(shade)
        }
      }
    }
    return choosenShade
  },
}
