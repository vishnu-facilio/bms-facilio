<template>
  <el-button
    :loading="showLoading"
    :type="buttonType"
    :class="buttonClass"
    :size="size"
    :disabled="disabled"
    v-bind="$attrs"
    @click="save()"
  >
    <slot></slot>
  </el-button>
</template>
<script>
export default {
  props: [
    'clickAction',
    'actionParams',
    'buttonType',
    'buttonClass',
    'size',
    'disabled',
    'loading',
  ],

  data() {
    return {
      isLoading: false,
    }
  },
  computed: {
    showLoading() {
      return this.loading || this.isLoading
    },
  },

  methods: {
    save() {
      if (this.clickAction && typeof this.clickAction === 'function') {
        this.isLoading = true
        let returnPromise = this.actionParams
          ? this.clickAction(...this.actionParams)
          : this.clickAction()
        let minimumTimeout = this.$helpers.delay(1000)

        if (returnPromise) {
          Promise.all([returnPromise, minimumTimeout]).finally(() => {
            this.isLoading = false
          })
        } else {
          this.$helpers.delay(1000).then(() => (this.isLoading = false))
        }
      } else {
        console.error(
          '`clickAction` should be a function that returns a promise'
        )
      }
    },
  },
}
</script>
