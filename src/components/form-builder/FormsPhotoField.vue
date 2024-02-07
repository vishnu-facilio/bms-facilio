<template>
  <div class="photo-field-content d-flex">
    <inline-svg
      src="svgs/photo"
      class="img-container"
      iconClass="icon icon-xl photo"
    ></inline-svg>
    <div class="img-name f14 mL10">{{ field.displayName }}</div>
    <div class="status-container mL-auto">
      <el-switch
        :width="30"
        v-model="proxyDataSetter"
        active-color="#39b2c2"
        inactive-color="#8ca1ad"
        @change="onToggle()"
      ></el-switch>
    </div>
  </div>
</template>
<script>
export default {
  props: ['field', 'activeField'],
  computed: {
    isPhotoFieldActive() {
      let { field, activeField } = this
      return field.name === activeField.name
    },
    proxyDataSetter: {
      get() {
        let { field } = this
        return !field.hideField
      },
      set(value) {
        let { field } = this
        this.$set(field, 'hideField', !value)
      },
    },
  },
  methods: {
    onToggle() {
      /*
        Usually updateproperty will trigger update field method in field properties component
        if 'photo field' is active field,
        so to trigger update field method if 'photo field' is not active this method is used
      */
      let { isPhotoFieldActive, field } = this
      if (!isPhotoFieldActive) {
        this.$emit('updatePhotoField', field)
      }
    },
  },
}
</script>
<style lang="scss">
.hide-field .photo-field-content {
  .img-container {
    opacity: 1;
  }
  .img-name {
    color: #324056;
    opacity: 1;
  }
}
.photo-field-content {
  .img-container {
    display: flex;
    border: solid 1px #8987a9;
    border-radius: 50%;
    padding: 10px;
    opacity: 0.3;
    .photo {
      fill: #8987a9;
    }
  }
  .img-name {
    letter-spacing: 0.47px;
    opacity: 0.3;
  }
  .status-container {
    .el-switch__core {
      height: 18px;
      &::after {
        width: 14px;
        height: 14px;
      }
    }
  }
}
</style>
