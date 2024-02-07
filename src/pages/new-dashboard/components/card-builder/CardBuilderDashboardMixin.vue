<script>
export default {
  data() {
    return {}
  },
  methods: {
    addCard({ cardContext }, { w = 24, h = 12 } = {}) {
      this.showCardBuilderSetup = false
      const uid = this.get_uid()
      const widget = {
        id: uid,
        x: 0,
        y: 0,
        w: w,
        h: h,
        minW: w,
        minH: h,
        widget: {
          dataOptions: cardContext,
          type: 'card',
          id: uid,
          widgetSettings: {
            showHelpText: false,
          },
          helpText: '',
        },
      }
      this.addWidget(widget)
    },
  },
}
</script>
