<template>
  <div>
    <div class="p20 con-continer">
      <code-mirror
        v-model="code.payload"
        class="left-con pT20 pR20 width50"
      ></code-mirror>
      <div class="code-run-btn">
        <el-button @click="submit()" class="btn-blue-fill p20">SEND</el-button>
      </div>
      <div class="error-tags-container ">
        <el-tag
          type="danger"
          class="m10"
          v-for="(error, index) in errors"
          :key="index"
          >{{ error }}</el-tag
        >
      </div>
      <el-input
        type="textarea"
        id="resultBox"
        :rows="2"
        class="right-con p20"
        placeholder="Result"
        v-model="code.result"
      >
      </el-input>
    </div>
    <f-chatbot> </f-chatbot>
  </div>
</template>

<script>
import CodeMirror from '@/CodeMirror'
import FChatbot from 'src/chatbot/FChatbotWrapper'

export default {
  name: 'textToSpeatch',
  data: function() {
    return {
      errors: [],
      pitch: 1.5,
      rate: 1,
      synth: window.speechSynthesis,
      validation: false,
      code: {
        payload: '',
        result: '',
      },
    }
  },
  components: { CodeMirror, FChatbot },
  methods: {
    submit() {
      this.setResult('')
      this.$http
        .post('/v2/cb/chat', {
          chatMessage: this.code.payload,
        })
        .then(response => {
          if (response.data.result.chatBotMessageString) {
            this.setResult(response.data.result.chatBotMessageString)
          }
        })
        .catch(() => {})
    },
    setError(errors) {
      if (errors) {
        this.errors = errors.errors || []
      } else {
        this.errors = []
      }
      this.errors = errors && errors.errors ? errors.errors : []
    },
    setResult(result) {
      if (this.synth.speaking) {
        console.error('speechSynthesis.speaking')
        return
      }
      if (result !== '') {
        this.validation = false
        let sInstance = new SpeechSynthesisUtterance(result)
        sInstance.onend = function() {}
        sInstance.onerror = function() {
          console.error('SpeechSynthesisUtterance.onerror')
        }
        sInstance.pitch = this.pitch
        sInstance.rate = this.rate
        sInstance.voice = window.speechSynthesis.getVoices()[33]
      } else {
        this.validation = true
      }

      this.code.result = this.code.result + '\n' + result
    },
  },
}
</script>
<style>
.left-con.el-textarea {
  width: 50%;
}
.right-con.el-textarea {
  width: 50%;
}
.con-continer {
  display: inline-flex;
  width: 100%;
}
.con-continer textarea.el-textarea__inner {
  min-height: 80vh !important;
}
.con-continer .CodeMirror {
  height: 85vh;
}
.code-run-btn {
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.error-tags-container {
  position: absolute;
  right: 80px;
  top: 80px;
  z-index: 10;
  display: grid;
  padding: 10px;
}
</style>
