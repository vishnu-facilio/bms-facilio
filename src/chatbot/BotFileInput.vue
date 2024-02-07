<template>
  <div class="bot-file-input">
    <div class="file-input-absolute">
      <div class="upload-text">
        Upload file
      </div>
      <inline-svg src="svgs/chatbot/attachments_icon"></inline-svg>
    </div>

    <input
      @change="handleFileSubmit"
      :disabled="disabled"
      ref="fileInput"
      class="file-input-tag"
      type="file"
      placeholder="Attach file"
    />
  </div>
</template>

<script>
export default {
  props: ['disabled'],
  methods: {
    handleFileSubmit(e) {
      let file = e.target.files[0]

      let formData = new FormData()
      formData.append('fileContent', file)

      this.$emit('attached', formData)
    },
  },
}
</script>

<style lang="scss">
.bot-file-input {
  width: 100%;
  height: 100%;
  position: relative;

  .file-input-absolute {
    background: #ffffff;
    position: absolute;
    z-index: 2;
    pointer-events: none;
    width: 100%;
    display: flex;
    height: 100%;
    justify-content: space-between;
    align-items: center;
    padding-left: 30px;
    padding-right: 30px;
  }

  .upload-text {
    font-size: 14px;
    color: #c0c4cc;
    letter-spacing: 0.5px;
    line-height: 16px;
  }

  .file-input-tag {
    width: 100%;
    opacity: 0;
    height: 100%;
    cursor: pointer;
  }
}
</style>
