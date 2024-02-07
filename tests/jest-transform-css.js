const { readFileSync } = require('fs')
const { process } = require('css')

module.exports = {
  // eslint-disable-next-line no-unused-vars
  process: (src, filename, config, options) => {
    const processed = process(readFileSync(filename, 'utf8'))
    return `module.exports = ${JSON.stringify(processed.css)}`
  },
}
