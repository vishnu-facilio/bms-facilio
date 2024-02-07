import * as chroma from 'chroma-js'
export default {
  simulateCache: {},
  confusionLines: {
    Protanope: {
      x: 0.7465,
      y: 0.2535,
      m: 1.273463,
      yint: -0.073894,
    },
    Deuteranope: {
      x: 1.4,
      y: -0.4,
      m: 0.968437,
      yint: 0.003331,
    },
    Tritanope: {
      x: 0.1748,
      y: 0.0,
      m: 0.062921,
      yint: 0.292119,
    },
  },
  checkForThis() {
    console.log(this.confusionLines)
  },
  checkLab(lab, checkColor) {
    // It will be necessary to check if a Lab color exists in the rgb space.
    let color = chroma.lab(lab[0], lab[1], lab[2])
    return this.validateLab(lab) && checkColor(color)
  },
  checkColor2(lab, checkColor) {
    let color = chroma.lab(lab)
    return this.validateLab(lab) && checkColor(color)
  },
  generate(
    colorsCount,
    checkColor,
    forceMode,
    quality,
    ultraPrecision,
    distanceType
  ) {
    if (colorsCount === undefined) {
      colorsCount = 8
    }
    if (checkColor === undefined) {
      checkColor = function(x) {
        return true
      }
    }
    if (forceMode === undefined) {
      forceMode = false
    }
    if (quality === undefined) {
      quality = 50
    }
    if (distanceType === undefined) {
      distanceType = 'Default'
    }
    ultraPrecision = ultraPrecision || false

    console.log(
      'Generate palettes for ' +
        colorsCount +
        ' colors using color distance "' +
        distanceType +
        '"'
    )
    console.log(typeof checkColor)
    if (forceMode) {
      // Force Vector Mode

      let colors = []

      // Init
      let vectors = {}
      for (let i = 0; i < colorsCount; i++) {
        // Find a valid Lab color
        let color = [
          100 * Math.random(),
          100 * (2 * Math.random() - 1),
          100 * (2 * Math.random() - 1),
        ]
        while (!this.checkLab(color, checkColor)) {
          color = [
            100 * Math.random(),
            100 * (2 * Math.random() - 1),
            100 * (2 * Math.random() - 1),
          ]
        }
        colors.push(color)
      }

      // Force vector: repulsion
      let repulsion = 100
      let speed = 100
      let steps = quality * 20
      while (steps-- > 0) {
        // Init
        for (let i = 0; i < colors.length; i++) {
          vectors[i] = { dl: 0, da: 0, db: 0 }
        }
        // Compute Force
        for (let i = 0; i < colors.length; i++) {
          let colorA = colors[i]
          for (let j = 0; j < i; j++) {
            let colorB = colors[j]

            // repulsion force
            let dl = colorA[0] - colorB[0]
            let da = colorA[1] - colorB[1]
            let db = colorA[2] - colorB[2]
            let d = this.getColorDistance(colorA, colorB, distanceType)
            if (d > 0) {
              let force = repulsion / Math.pow(d, 2)

              vectors[i].dl += (dl * force) / d
              vectors[i].da += (da * force) / d
              vectors[i].db += (db * force) / d

              vectors[j].dl -= (dl * force) / d
              vectors[j].da -= (da * force) / d
              vectors[j].db -= (db * force) / d
            } else {
              // Jitter
              vectors[j].dl += 2 - 4 * Math.random()
              vectors[j].da += 2 - 4 * Math.random()
              vectors[j].db += 2 - 4 * Math.random()
            }
          }
        }
        // Apply Force
        for (let i = 0; i < colors.length; i++) {
          let color = colors[i]
          let displacement =
            speed *
            Math.sqrt(
              Math.pow(vectors[i].dl, 2) +
                Math.pow(vectors[i].da, 2) +
                Math.pow(vectors[i].db, 2)
            )
          if (displacement > 0) {
            let ratio = (speed * Math.min(0.1, displacement)) / displacement
            let candidateLab = [
              color[0] + vectors[i].dl * ratio,
              color[1] + vectors[i].da * ratio,
              color[2] + vectors[i].db * ratio,
            ]
            if (this.checkLab(candidateLab, checkColor)) {
              colors[i] = candidateLab
            }
          }
        }
      }
      return colors.map(function(lab) {
        return chroma.lab(lab[0], lab[1], lab[2])
      })
    } else {
      // K-Means Mode
      let kMeans = []
      for (let i = 0; i < colorsCount; i++) {
        let lab = [
          100 * Math.random(),
          100 * (2 * Math.random() - 1),
          100 * (2 * Math.random() - 1),
        ]
        let failsafe = 10
        while (!this.checkColor2(lab, checkColor) && failsafe-- > 0) {
          lab = [
            100 * Math.random(),
            100 * (2 * Math.random() - 1),
            100 * (2 * Math.random() - 1),
          ]
        }
        kMeans.push(lab)
      }

      let colorSamples = []
      let samplesClosest = []
      if (ultraPrecision) {
        for (let l = 0; l <= 100; l += 1) {
          for (let a = -100; a <= 100; a += 5) {
            for (let b = -100; b <= 100; b += 5) {
              if (this.checkColor2([l, a, b], checkColor)) {
                colorSamples.push([l, a, b])
                samplesClosest.push(null)
              }
            }
          }
        }
      } else {
        for (let l = 0; l <= 100; l += 5) {
          for (let a = -100; a <= 100; a += 10) {
            for (let b = -100; b <= 100; b += 10) {
              if (this.checkColor2([l, a, b], checkColor)) {
                colorSamples.push([l, a, b])
                samplesClosest.push(null)
              }
            }
          }
        }
      }

      // Steps
      let steps = quality
      while (steps-- > 0) {
        // kMeans -> Samples Closest
        for (let i = 0; i < colorSamples.length; i++) {
          let lab = colorSamples[i]
          let minDistance = Infinity
          for (let j = 0; j < kMeans.length; j++) {
            let kMean = kMeans[j]
            let distance = this.getColorDistance(lab, kMean, distanceType)
            if (distance < minDistance) {
              minDistance = distance
              samplesClosest[i] = j
            }
          }
        }

        // Samples -> kMeans
        let freeColorSamples = colorSamples.slice(0)
        for (let j = 0; j < kMeans.length; j++) {
          let count = 0
          let candidateKMean = [0, 0, 0]
          for (let i = 0; i < colorSamples.length; i++) {
            if (samplesClosest[i] === j) {
              count++
              candidateKMean[0] += colorSamples[i][0]
              candidateKMean[1] += colorSamples[i][1]
              candidateKMean[2] += colorSamples[i][2]
            }
          }
          if (count !== 0) {
            candidateKMean[0] /= count
            candidateKMean[1] /= count
            candidateKMean[2] /= count
          }

          if (
            count !== 0 &&
            this.checkColor2(
              [candidateKMean[0], candidateKMean[1], candidateKMean[2]],
              checkColor
            ) &&
            candidateKMean
          ) {
            kMeans[j] = candidateKMean
          } else {
            // The candidate kMean is out of the boundaries of the color space, or unfound.
            if (freeColorSamples.length > 0) {
              // We just search for the closest FREE color of the candidate kMean
              let minDistance = Infinity
              let closest = -1
              for (let i = 0; i < freeColorSamples.length; i++) {
                let distance = this.getColorDistance(
                  freeColorSamples[i],
                  candidateKMean,
                  distanceType
                )
                if (distance < minDistance) {
                  minDistance = distance
                  closest = i
                }
              }
              if (closest >= 0) {
                kMeans[j] = colorSamples[closest]
              }
            } else {
              // Then we just search for the closest color of the candidate kMean
              let minDistance = Infinity
              let closest = -1
              for (let i = 0; i < colorSamples.length; i++) {
                let distance = this.getColorDistance(
                  colorSamples[i],
                  candidateKMean,
                  distanceType
                )
                if (distance < minDistance) {
                  minDistance = distance
                  closest = i
                }
              }
              if (closest >= 0) {
                kMeans[j] = colorSamples[closest]
              }
            }
          }
          freeColorSamples = freeColorSamples.filter(function(color) {
            return (
              color[0] !== kMeans[j][0] ||
              color[1] !== kMeans[j][1] ||
              color[2] !== kMeans[j][2]
            )
          })
        }
      }
      return kMeans.map(function(lab) {
        return chroma.lab(lab[0], lab[1], lab[2])
      })
    }
  },
  diffSort(colorsToSort, distanceType) {
    // Sort
    let diffColors = [colorsToSort.shift()]
    while (colorsToSort.length > 0) {
      let index = -1
      let maxDistance = -1
      for (
        let candidateIndex = 0;
        candidateIndex < colorsToSort.length;
        candidateIndex++
      ) {
        let d = Infinity
        for (let i = 0; i < diffColors.length; i++) {
          let colorA = colorsToSort[candidateIndex].lab()
          let colorB = diffColors[i].lab()
          d = this.getColorDistance(colorA, colorB, distanceType)
        }
        if (d > maxDistance) {
          maxDistance = d
          index = candidateIndex
        }
      }
      let color = colorsToSort[index]
      diffColors.push(color)
      colorsToSort = colorsToSort.filter(function(c, i) {
        return i !== index
      })
    }
    return diffColors
  },
  distanceColorblind(lab1, lab2, type) {
    let lab1Cb = this.simulate(lab1, type)
    let lab2Cb = this.simulate(lab2, type)
    return this._cmcDistance(lab1Cb, lab2Cb, 2, 1)
  },
  compromiseDistance(lab1, lab2) {
    let distances = []
    let coeffs = []
    distances.push(this._cmcDistance(lab1, lab2, 2, 1))
    coeffs.push(1000)
    let types = ['Protanope', 'Deuteranope', 'Tritanope']
    types.forEach(function(type) {
      let lab1Cb = this.simulate(lab1, type)
      let lab2Cb = this.simulate(lab2, type)
      if (!(lab1Cb.some(isNaN) || lab2Cb.some(isNaN))) {
        let c
        switch (type) {
          case 'Protanope':
            c = 100
            break
          case 'Deuteranope':
            c = 500
            break
          case 'Tritanope':
            c = 1
            break
        }
        distances.push(this._cmcDistance(lab1Cb, lab2Cb, 2, 1))
        coeffs.push(c)
      }
    })
    let total = 0
    let count = 0
    distances.forEach(function(d, i) {
      total += coeffs[i] * d
      count += coeffs[i]
    })
    return total / count
  },
  _cmcDistance(lab1, lab2, l, c) {
    let L1 = lab1[0]
    let L2 = lab2[0]
    let a1 = lab1[1]
    let a2 = lab2[1]
    let b1 = lab1[2]
    let b2 = lab2[2]
    let C1 = Math.sqrt(Math.pow(a1, 2) + Math.pow(b1, 2))
    let C2 = Math.sqrt(Math.pow(a2, 2) + Math.pow(b2, 2))
    let deltaC = C1 - C2
    let deltaL = L1 - L2
    let deltaa = a1 - a2
    let deltab = b1 - b2
    let deltaH = Math.sqrt(
      Math.pow(deltaa, 2) + Math.pow(deltab, 2) + Math.pow(deltaC, 2)
    )
    let H1 = Math.atan2(b1, a1) * (180 / Math.PI)
    while (H1 < 0) {
      H1 += 360
    }
    let F = Math.sqrt(Math.pow(C1, 4) / (Math.pow(C1, 4) + 1900))
    let T =
      H1 >= 164 && H1 <= 345
        ? 0.56 + Math.abs(0.2 * Math.cos(H1 + 168))
        : 0.36 + Math.abs(0.4 * Math.cos(H1 + 35))
    let S_L = lab1[0] < 16 ? 0.511 : (0.040975 * L1) / (1 + 0.01765 * L1)
    let S_C = (0.0638 * C1) / (1 + 0.0131 * C1) + 0.638
    let S_H = S_C * (F * T + 1 - F)
    let result = Math.sqrt(
      Math.pow(deltaL / (l * S_L), 2) +
        Math.pow(deltaC / (c * S_C), 2) +
        Math.pow(deltaH / S_H, 2)
    )
    return result
  },
  _euclidianDistance(lab1, lab2) {
    return Math.sqrt(
      Math.pow(lab1[0] - lab2[0], 2) +
        Math.pow(lab1[1] - lab2[1], 2) +
        Math.pow(lab1[2] - lab2[2], 2)
    )
  },
  getColorDistance(lab1, lab2, _type) {
    let type = _type || 'Default'

    if (type === 'Default') return this._euclidianDistance(lab1, lab2)
    if (type === 'Euclidian') return this._euclidianDistance(lab1, lab2)
    if (type === 'CMC') return this._cmcDistance(lab1, lab2, 2, 1)
    if (type === 'Compromise') return this.compromiseDistance(lab1, lab2)
    else return this.distanceColorblind(lab1, lab2, type)
    // http://www.brucelindbloom.com/index.html?Eqn_DeltaE_CMC.html
  },
  simulate(lab, type, _amount) {
    // WARNING: may return [NaN, NaN, NaN]

    let amount = _amount || 1

    // Cache
    let key = lab.join('-') + '-' + type + '-' + amount
    let cache = this.simulateCache[key]
    if (cache) return cache

    // Get data from type
    let confuseX = this.confusionLines[type].x
    let confuseY = this.confusionLines[type].y
    let confuseM = this.confusionLines[type].m
    let confuseYint = this.confusionLines[type].yint

    // Code adapted from http://galacticmilk.com/labs/Color-Vision/Javascript/Color.Vision.Simulate.js
    let color = chroma.lab(lab[0], lab[1], lab[2])
    let sr = color.rgb()[0]
    let sg = color.rgb()[1]
    let sb = color.rgb()[2]
    let dr = sr // destination color
    let dg = sg
    let db = sb
    // Convert source color into XYZ color space
    let powR = Math.pow(sr, 2.2)
    let powG = Math.pow(sg, 2.2)
    let powB = Math.pow(sb, 2.2)
    let X = powR * 0.412424 + powG * 0.357579 + powB * 0.180464 // RGB->XYZ (sRGB:D65)
    let Y = powR * 0.212656 + powG * 0.715158 + powB * 0.0721856
    let Z = powR * 0.0193324 + powG * 0.119193 + powB * 0.950444
    // Convert XYZ into xyY Chromacity Coordinates (xy) and Luminance (Y)
    let chromaX = X / (X + Y + Z)
    let chromaY = Y / (X + Y + Z)
    // Generate the "Confusion Line" between the source color and the Confusion Point
    let m = (chromaY - confuseY) / (chromaX - confuseX) // slope of Confusion Line
    let yint = chromaY - chromaX * m // y-intercept of confusion line (x-intercept = 0.0)
    // How far the xy coords deviate from the simulation
    let deviateX = (confuseYint - yint) / (m - confuseM)
    let deviateY = m * deviateX + yint
    // Compute the simulated color's XYZ coords
    X = (deviateX * Y) / deviateY
    Z = ((1.0 - (deviateX + deviateY)) * Y) / deviateY
    // Neutral grey calculated from luminance (in D65)
    let neutralX = (0.312713 * Y) / 0.329016
    let neutralZ = (0.358271 * Y) / 0.329016
    // Difference between simulated color and neutral grey
    let diffX = neutralX - X
    let diffZ = neutralZ - Z
    let diffR = diffX * 3.24071 + diffZ * -0.498571 // XYZ->RGB (sRGB:D65)
    let diffG = diffX * -0.969258 + diffZ * 0.0415557
    let diffB = diffX * 0.0556352 + diffZ * 1.05707
    // Convert to RGB color space
    dr = X * 3.24071 + Y * -1.53726 + Z * -0.498571 // XYZ->RGB (sRGB:D65)
    dg = X * -0.969258 + Y * 1.87599 + Z * 0.0415557
    db = X * 0.0556352 + Y * -0.203996 + Z * 1.05707
    // Compensate simulated color towards a neutral fit in RGB space
    let fitR = ((dr < 0.0 ? 0.0 : 1.0) - dr) / diffR
    let fitG = ((dg < 0.0 ? 0.0 : 1.0) - dg) / diffG
    let fitB = ((db < 0.0 ? 0.0 : 1.0) - db) / diffB
    let adjust = Math.max(
      // highest value
      fitR > 1.0 || fitR < 0.0 ? 0.0 : fitR,
      fitG > 1.0 || fitG < 0.0 ? 0.0 : fitG,
      fitB > 1.0 || fitB < 0.0 ? 0.0 : fitB
    )
    // Shift proportional to the greatest shift
    dr = dr + adjust * diffR
    dg = dg + adjust * diffG
    db = db + adjust * diffB
    // Apply gamma correction
    dr = Math.pow(dr, 1.0 / 2.2)
    dg = Math.pow(dg, 1.0 / 2.2)
    db = Math.pow(db, 1.0 / 2.2)
    // Anomylize colors
    dr = sr * (1.0 - amount) + dr * amount
    dg = sg * (1.0 - amount) + dg * amount
    db = sb * (1.0 - amount) + db * amount
    let dcolor = chroma.rgb(dr, dg, db)
    let result = dcolor.lab()
    this.simulateCache[key] = result
    return result
  },
  xyz_rgb(r) {
    return Math.round(
      255 * (r <= 0.00304 ? 12.92 * r : 1.055 * Math.pow(r, 1 / 2.4) - 0.055)
    )
  },
  lab_xyz(t, LAB_CONSTANTS) {
    return t > LAB_CONSTANTS.t1
      ? t * t * t
      : LAB_CONSTANTS.t2 * (t - LAB_CONSTANTS.t0)
  },
  validateLab(lab) {
    // Code from Chroma.js 2016
    let LAB_CONSTANTS = {
      // Corresponds roughly to RGB brighter/darker
      Kn: 18,
      // D65 standard referent
      Xn: 0.95047,
      Yn: 1,
      Zn: 1.08883,
      t0: 0.137931034, // 4 / 29
      t1: 0.206896552, // 6 / 29
      t2: 0.12841855, // 3 * t1 * t1
      t3: 0.008856452, // t1 * t1 * t1
    }

    let l = lab[0]
    let a = lab[1]
    let b = lab[2]

    let y = (l + 16) / 116
    let x = isNaN(a) ? y : y + a / 500
    let z = isNaN(b) ? y : y - b / 200

    y = LAB_CONSTANTS.Yn * this.lab_xyz(y, LAB_CONSTANTS)
    x = LAB_CONSTANTS.Xn * this.lab_xyz(x, LAB_CONSTANTS)
    z = LAB_CONSTANTS.Zn * this.lab_xyz(z, LAB_CONSTANTS)

    let r = this.xyz_rgb(3.2404542 * x - 1.5371385 * y - 0.4985314 * z) // D65 -> sRGB
    let g = this.xyz_rgb(-0.969266 * x + 1.8760108 * y + 0.041556 * z)
    b = this.xyz_rgb(0.0556434 * x - 0.2040259 * y + 1.0572252 * z)

    return r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255
  },
}
