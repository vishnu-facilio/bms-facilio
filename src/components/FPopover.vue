<template>
  <el-popover
    :ref="getRef"
    :popper-class="'f-popover'"
    v-model="showpopover"
    :width="width || width === '' ? width : 280"
    :placement="placement"
    :trigger="trigger || 'click'"
    @show="() => $emit('show')"
    hide="hide"
  >
    <div class="fc-popover-content">
      <slot name="header">
        <div
          class="title uppercase f12 bold"
          style="letter-spacing: 1.1px; color:#000000"
        >
          {{ title }}
        </div>
      </slot>
      <div>
        <slot name="content"></slot>
      </div>
    </div>
    <div class="f-footer row" style="height:46px;">
      <slot name="footer">
        <button
          type="button"
          class="footer-btn footer-btn-secondary col-6"
          @click="cancel"
        >
          <span>{{ $t('setup.users_management.cancel') }}</span>
        </button>
        <button
          type="button"
          class="footer-btn footer-btn-primary col-6"
          @click="save"
        >
          <span>{{
            confirmTitle ? confirmTitle : $t('common._common.ok')
          }}</span>
        </button>
      </slot>
    </div>
    <template slot="reference"><slot name="reference"></slot></template>
  </el-popover>
</template>
<script>
export default {
  props: [
    'popperClass',
    'placement',
    'value',
    'popperRef',
    'trigger',
    'title',
    'width',
    'maxHeight',
    'confirmTitle',
  ],
  data() {
    return {
      showpopover: false,
    }
  },
  watch: {
    showpopover(val) {
      if (this.value != null) {
        this.$emit('input', val)
      }
    },
    value(val) {
      this.showpopover = val
    },
  },
  mounted() {
    let element = this.$refs[this.getRef].$el
    if (this.popperClass) {
      element.firstElementChild.classList.add(this.popperClass)
    }
    if (this.maxHeight) {
      let content = element.querySelector('.fc-popover-content')
      content.classList.add('scrollable')
      content.style['max-height'] = this.maxHeight + 'px'
    }
  },
  computed: {
    getRef() {
      return this.popperRef ? this.popperRef : 'fpopup'
    },
  },
  methods: {
    save() {
      this.showpopover = false
      this.$emit('save')
    },
    cancel() {
      this.showpopover = false
      this.$emit('close')
    },
    hide() {
      this.$emit('hide')
    },
  },
}
</script>
<style></style>
