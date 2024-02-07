import colors from './chartGroupedColors'
export default {
  stateTypeEnum: {
    1: 'NORMAL',
    2: 'RANK',
    3: 'HIGH',
  },

  colorPicker(dataPoints, stateType) {
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
          choosenColor.push(color)
          choosenShade.push(shades[shades.length - 1])
        }
        break
      }
      default: {
        choosenColor = []
        choosenShade = []
        let colorNames = Object.keys(colors.colors)
        for (let i = 0; i < dataPoints; i++) {
          let color = colorNames[Math.floor(Math.random() * colorNames.length)]
          let shades = colors.colors[color]
          choosenColor.push(color)
          choosenShade.push(shades[shades.length - 1])
        }
        break
      }
    }
    return choosenShade
  },

  stateNormal(dataPoints) {
    let choosenColor = []
    let choosenShade = []
    let colorNames = Object.keys(colors.colors)
    for (let i = 0; i < dataPoints; i++) {
      let color = colorNames[Math.floor(Math.random() * colorNames.length)]
      let shades = colors.colors[color]
      if (choosenColor.includes(color)) {
        let shade = shades[Math.floor(Math.random() * shades.length)]
        if (choosenShade.includes(shade)) {
          i = i - 1
          continue
        } else {
          choosenShade.push(shade)
        }
      } else {
        choosenColor.push(color)
        choosenShade.push(shades[Math.floor(Math.random() * shades.length)])
      }
      choosenColor.push(color)
      choosenShade.push(shades[Math.floor(Math.random() * shades.length)])
    }
    return choosenShade
  },
}
