<template>
  <div class="url-container">
    <div class="url-field">
      <el-input
        class="fc-input-full-border2"
        type="text"
        :disabled="isDisabled"
        placeholder="https://"
        v-model="urlFieldValue.href"
      >
        <template class="title" slot="prepend">
          {{ $t('common.markdown.url_') }}</template
        >
      </el-input>
    </div>
    <div class="url-field" v-if="showName">
      <el-input
        class="fc-input-full-border2"
        type="text"
        :disabled="isDisabled"
        :placeholder="$t('setup.setupLabel.link_name')"
        v-model="urlFieldValue.name"
      >
        <template class="title" slot="prepend">
          <div class="name-field">
            {{ $t('common.products.name') }}
          </div>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['field', 'isDisabled', 'config', 'isEdit', 'value'],
  data() {
    return {
      showName: false,
    }
  },
  created() {
    this.prefillUrlField()
  },
  watch: {
    config: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          let { config } = this

          this.showName = (config || {}).showName
        }
      },
      immediate: true,
    },
  },
  computed: {
    urlFieldValue: {
      get() {
        let { value = {} } = this
        if (isEmpty(value)) {
          value = { href: '', name: '' }
        } else {
          if (typeof value === 'string') value = { href: value }
        }
        return value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
  },
  methods: {
    prefillUrlField() {
      let { field } = this
      let { value } = field || {}

      if (typeof value === 'string') value = { href: value }
      if (!isEmpty(value)) {
        this.$set(this, 'urlFieldValue', value)
      }
    },
  },
}
</script>
<style lang="scss">
.url-field {
  width: auto;
  height: 40px;
  display: flex;
  margin-bottom: 25px;
  font-size: 12px;
  font-weight: 400;
  .el-input-group__prepend {
    width: 70px !important;
    padding-left: 10px;
    border-right: none;
  }
  .name-field {
    max-width: 25px;
  }
  .el-input__inner {
    height: 40px !important;
    border-radius: 4px !important;
    border-top-left-radius: 0px !important;
    border-bottom-left-radius: 0px !important;
  }
}
</style>
