<template>
  <div class="inline" v-html="computedSvg"></div>
</template>

<script>
export default {
  props: {
    src: {
      type: String,
      required: true,
    },
    iconClass: {
      type: String,
    },
    iconStyle: {
      type: String,
    },
  },
  data() {
    return {
      originalSvg: '',
    }
  },
  async mounted() {
    this.originalSvg = await new Promise(resolve => {
      require.ensure([], () =>
        resolve(
          require(`!html-loader!svgo-loader?externalConfig=svgo.config.yml!./../assets/${this.src}.svg`)
            .default
        )
      )
    })
  },
  computed: {
    computedSvg() {
      const styledSvg = `<svg class="${this.iconClass ?? 'icon'}" style="${this
        .iconStyle ?? ''}"`
      return (this.originalSvg ?? '').replace(/<svg/, styledSvg)
    },
  },
}
</script>
