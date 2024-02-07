module.exports = function(api) {
  let plugins = []

  if (api.env(['development', 'test'])) {
    // https://github.com/webpack/webpack/issues/8557#issuecomment-450347248
    plugins.push('dynamic-import-node')
  }

  return {
    presets: ['@vue/cli-plugin-babel/preset'],
    plugins,
  }
}
