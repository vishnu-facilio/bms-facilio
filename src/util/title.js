function getTitle(vm) {
  const { title } = vm.$options
  if (title) {
    return typeof title === 'function' ? title.call(vm) : title
  }
}

const titleMixin = {
  created() {
    const title = getTitle(this)
    if (title) {
      this.setTitle(title)
    }
  },
  methods: {
    setTitle(title) {
      document.title = `${title} - ${
        window.brandConfig && window.brandConfig.name
          ? window.brandConfig.name
          : 'Facilio'
      }`
    },

    getStaticPath(path) {
      if (!path.startsWith('/')) {
        path = '/' + path
      }
      return '/statics' + path
    },
  },
}

export default titleMixin
