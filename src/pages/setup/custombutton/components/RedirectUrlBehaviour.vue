<template>
  <el-row class="custombtn-redirect">
    <el-col>
      <el-form>
        <el-form-item>
          <div class="display-flex-between-space pB10 pT10">
            <p class="details-Heading pB3">
              {{ $t('setup.customButton.redirect_url_label') }}
            </p>

            <PlaceholderPicker
              v-if="positionType !== POSITION.LIST_TOP"
              placement="left"
              :title="$t('common.header.placeholders')"
              @change="addPlaceholderToUrl"
              class="mR20 redirect-placeholder-text"
              :module="module"
            ></PlaceholderPicker>
          </div>
          <el-input
            class="mT3"
            :min-rows="2"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 4 }"
            @input.native="emitUrl"
            :placeholder="$t('setup.customButton.redirect_url_placeholder')"
            v-model="url"
          >
          </el-input>
          <p class="fc-sub-title-desc pT5">
            {{
              positionType === POSITION.LIST_TOP
                ? 'Eg: https://www.google.com'
                : 'Eg: https://www.google.com/search?q=${workorder.id:-}'
            }}
          </p>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<script>
import { ACTION_TYPES, POSITION_TYPE } from '../CustomButtonUtil'
import PlaceholderPicker from '@/placeholder/PlaceholderPicker'

export default {
  name: 'RedirectUrlBehaiour',
  props: ['module', 'customButtonObject', 'positionType'],
  components: { PlaceholderPicker },
  data() {
    return {
      url: '',
      POSITION: POSITION_TYPE,
    }
  },
  created() {
    this.deserialize()
  },
  methods: {
    emitUrl() {
      let params = {
        actionType: ACTION_TYPES.REDIRECT_URL,
        url: this.url,
      }
      this.$emit('setProperties', { config: params })
    },
    deserialize() {
      let { config = {} } = this.customButtonObject
      let { actionType, url } = config || {}
      if (actionType && url) {
        this.url = this.$getProperty(this, 'customButtonObject.config.url')
      }
    },
    addPlaceholderToUrl({ placeholderString }) {
      this.url = `${this.url}${placeholderString}`
      this.emitUrl()
    },
  },
}
</script>
<style scoped>
.redirect-placeholder-text {
  margin-top: -7px !important;
}
</style>
