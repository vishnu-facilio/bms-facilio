<template>
  <div>
    <div class="box" ref="desc">
      <div class="label">{{ $t('common.shift.description') }}</div>
      <div class="description">
        {{ description }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['details', 'resizeWidget'],
  name: 'ShiftDescription',
  mounted() {
    this.$nextTick(() => {
      this.autoResize()
    })
  },
  computed: {
    widgetTitle() {
      return 'Shift Description'
    },
    shift() {
      return this.details
    },
    description() {
      let { description } = this.shift
      return description ? description : '---'
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          let container = this.$refs['desc']
          if (container) {
            let width = container.scrollWidth
            let height = container.scrollHeight + 50
            if (this.resizeWidget) {
              this.resizeWidget({ height, width })
            }
          }
        })
      })
    },
  },
}
</script>

<style scoped>
.box {
  padding: 20px;
}
.label {
  font-size: 1.1em;
  font-weight: bold;
  margin-right: 10px;
}

.description {
  margin-top: 10px;
}
</style>
