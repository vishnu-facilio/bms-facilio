<template>
  <div class="fc-v1-rule-form-first-page">
    <div>
      <div
        class="width100 flex-middle position-relative multibox-hover pB10"
        v-for="(prob, index) in model"
        :key="index"
      >
        <div class="visibility-visible-actions width100 flex-middle ">
          <div>
            <el-input
              class="fc-input-full-border2 fc-v1-rule-form-label-width575"
              v-model="model[index]"
            ></el-input>
          </div>
          <div
            class="creteria-delete-icon flex-middle multibox-hover-icon visibility-hide-actions mL15 mT10"
          >
            <div>
              <img
                src="~assets/add-icon.svg"
                style="height:18px;width:18px;"
                class="pointer"
                @click="addTextBox"
              />
            </div>
            <div>
              <img
                src="~assets/remove-icon.svg"
                v-if="model.length > 1"
                style="height:18px;width:18px;"
                class="pointer mL10"
                @click="removeTextBox(index)"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    model: {
      type: Array,
    },
  },
  data() {
    return {
      multiTextValue: [],
    }
  },
  watch: {
    model() {
      if (!(this.model.length > 0)) {
        this.$set(this.model, 0, '')
        this.model = this.$helpers.cloneObject(this.model)
      }
    },
  },
  created() {
    if (!(this.model.length > 0)) {
      this.$set(this.model, 0, '')
      this.model = this.$helpers.cloneObject(this.model)
    }
  },
  methods: {
    addTextBox() {
      this.$set(this.model, this.model.length, '')
      this.model = this.$helpers.cloneObject(this.model)
    },
    removeTextBox(index) {
      this.$delete(this.model, index)
    },
  },
}
</script>
<style lang="scss">
.fc-v1-rule-form-label-width575 .el-input__inner {
  width: 575px !important;
}
.fc-v1-rule-form-first-page {
  .el-input__inner {
    width: 575px !important;
  }
}
</style>
