// defineConfig is used for better intellisense support in vs code
const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')
const aliases = require('./alias.config')
const indexHtmlParams = require('./lib/index-params')

const isProd = process.env.NODE_ENV === 'production'
const isDev = process.env.NODE_ENV === 'development'
const needsReport = process.env.GENERATE_REPORT === 'true'
const needsSourceMap = process.env.GENERATE_SOURCEMAP === 'true'

module.exports = defineConfig({
  lintOnSave: 'default',
  devServer: {
    port: 9090,
    hot: true,
    client: {
      overlay: {
        warnings: false,
        errors: true,
      },
    },
  },
  publicPath: isProd ? '{{staticURL}}' : '/',
  filenameHashing: true,
  css: {
    sourceMap: !isProd,
  },
  productionSourceMap: needsSourceMap,
  pluginOptions: {
    webpackBundleAnalyzer: {
      openAnalyzer: false,
      analyzerMode: needsReport ? 'static' : 'disabled',
    },
  },
  chainWebpack: config => {
    /* Vue cli 4 had built in file loader and url loader, which are removed in vue cli 5 in
    favor of asset modules (webpack 5). Therefore we had to manually clear all svg loaders
    and add a file loader for svgs.
    https://next.cli.vuejs.org/migrations/migrate-from-v4.html#asset-modules
    */

    config.module.rule('svg').uses.clear()
    config.module
      .rule('svg')
      .test(/\/icons\/other\/.*\.svg$/)
      .use('babel-loader')
      .loader('babel-loader')
      .end()

    config.module
      .rule('svg-file')
      .test(/\.svg$/)
      .use('file-loader')
      .loader('file-loader')
      .options({
        // had to be false so that images are not returned as es modules
        esModule: false,
      })
      .end()

    /* Add src as a valid location to resolve modules from. Not sure why it is needed */
    config.resolve.modules.add('src')

    /* Webpack 5 does no longer include polyfills for Node.js modules by default. Have to manully set fallbacks for
    the used node js modules */
    config.resolve.set('fallback', {
      os: false,
      vm: false,
      crypto: false,
    })

    /* Enable once we transition to modern mode */
    config.plugins.delete('prefetch')
    config.plugins.delete('preload')

    if (isProd) {
      /* Used to turn off hashing for css files ,
       * throwing error in dev mode , so disabling in dev for now
       */
      config.plugin('extract-css').tap(() => {
        return [
          {
            filename: 'css/[name].css',
            chunkFilename: 'css/[name].css',
          },
        ]
      })

      /* Use htmlwebpack plugin and hbs loader to generate index.html with necessary placeholders
       */
      config.plugin('html').tap(args => {
        args[0].template = '!!handlebars-loader!src/index.hbs'
        args[0].templateParameters = indexHtmlParams.production
        return args
      })
    } else if (isDev) {
      /* Remove bundle analyzer in dev mode */
      config.plugins.delete('webpack-bundle-analyzer')

      /* Vue-loader uses prettier inside to prettify the code for debugging, can be disabled for perf
       */
      config.module
        .rule('vue')
        .use('vue-loader')
        .loader('vue-loader')
        .tap(options => {
          options.prettify = false
          return options
        })

      /* Use htmlwebpack plugin and hbs loader to generate index.html and fill placeholders
       */
      config.plugin('html').tap(args => {
        args[0].template = '!!handlebars-loader!src/index.hbs'
        args[0].templateParameters = indexHtmlParams.development
        return args
      })
    }
  },

  configureWebpack: config => {
    if (isProd) {
      config.plugins = [
        ...config.plugins,
        new webpack.BannerPlugin({
          banner: `//# sourceMappingURL=http://localhost:5050/sourcemaps/js/[name].js.map`,
          footer: true,
          raw: true,
          exclude: /(.svg$)|(.css$)/,
        }),
      ]

      config.optimization.minimizer[0].options.minimizer.options.mangle = false
      config.optimization.minimizer[0].options.minimizer.options.compress.drop_console = true
      config.optimization.minimizer[0].options.minimizer.options.output = {
        comments: true,
      }
      // Css minifier requires to mention autoprefixer as a plugin since postcss 8 is used
      config.optimization.minimizer[1].options.minimizer.options = {
        preset: [
          'default',
          { mergeLonghand: false, cssDeclarationSorter: false },
        ],
        plugins: [['autoprefixer', {}]],
      }

      // uncomment this and run the build ( npm run sourcemap:enable )in local to enable sourcemaps for production

      // config.plugins = [
      //   ...config.plugins,
      //   new webpack.SourceMapDevToolPlugin({
      //     // this is the url of our local sourcemap server

      //     filename: 'sourcemaps/[file].map',
      //   }),
      // ]

      /* Set minimum bundle size to 50kb */
      config.optimization.splitChunks.minSize = 50 * 1000
      /* Include both initial and async bundles into common bundle and
       * limits size per bundle to 2mb but greater than 50Kb
       */
      config.optimization.splitChunks.cacheGroups.common = {
        ...config.optimization.splitChunks.cacheGroups.common,
        test: /\.js$/,
        maxSize: 2 * 1000 * 1000, // 2mb
        minSize: 50 * 1000, // 50kb
        chunks: 'all',
      }
      const languages = ['de', 'en', 'es', 'hu', 'it', 'pt']
      let languageGroups = {}
      for (let language of languages) {
        let regex = `[\\/]src[\\/]translations[\\/]lang[\\/]${language}`
        let regexExpression = new RegExp(regex)
        languageGroups[language] = {
          test: regexExpression,
          chunks: 'all',
        }
      }
      config.optimization.splitChunks.cacheGroups = {
        ...config.optimization.splitChunks.cacheGroups,
        ...languageGroups,
      }
    } else if (isDev) {
      config.devtool = 'eval-source-map'
      /* Make it easier to distinguish between sources in developer tools.
        webpack-generated is for transipiled/compiled files
      */
      config.output.devtoolModuleFilenameTemplate = info =>
        info.resourcePath.match(/^\.\/\S*?\.vue$/)
          ? `webpack-generated:///${info.resourcePath}?${info.hash}`
          : `webpack-vue:///${info.resourcePath}`

      config.output.devtoolFallbackModuleFilenameTemplate =
        'webpack:///[resource-path]?[hash]'
    }

    config.resolve.alias = {
      ...config.resolve.alias,
      ...aliases,
    }

    config.output = {
      ...config.output,
      ...{
        filename: 'js/[name].js',
        chunkFilename: 'js/[name].js',
      },
    }
  },
})
