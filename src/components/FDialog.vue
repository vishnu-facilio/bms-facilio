<template>
  <div :class="[options.className]">
    <transition :name="options.transition">
      <div
        :id="options.id"
        class="f-dialog-wrapper"
        v-if="visible"
        @click.self="maskClick"
        @keydown.esc="escape"
      >
        <div
          class="f-dialog-backdrop"
          :style="{ background: options.maskColor }"
        ></div>
        <div
          class="f-dialog-content"
          :class="{ 'f-dialog-wide': options.wide }"
          ref="dialogContent"
          tabindex="-1"
        >
          <div
            class="f-dialog-header"
            :class="{ 'with-body': options.message }"
          >
            <span class="content delete-heading"></span>
          </div>

          <div class="f-dialog-body" v-if="options.message">
            <h3 class="delete-heading">{{ options.title }}</h3>
            <span class="content delete-content">{{ options.message }}</span>
          </div>

          <div class="f-dialog-body" v-else-if="options.htmlMessage">
            <h3 class="delete-heading">{{ options.title }}</h3>
            <span
              class="content delete-content"
              v-html="options.htmlMessage"
            ></span>
          </div>

          <div class="f-dialog-body" v-else-if="options.mode === 'alert'">
            <span class="content delete-content">{{ options.message }}</span>
          </div>

          <div class="prompt-dialog" v-else-if="options.mode === 'prompt'">
            <div class="prompt-input-wrapper">
              <textarea
                class="prompt-input"
                style="resize: none;"
                v-if="options.promptType === 'textarea'"
                :placeholder="options.promptPlaceholder"
                v-model="input"
                @keydown.enter="rbClick"
                ref="promptInput"
              ></textarea>
              <input
                class="prompt-input"
                v-else
                :placeholder="options.promptPlaceholder"
                type="text"
                v-model="input"
                @keydown.enter="rbClick"
                ref="promptInput"
              />
            </div>
          </div>
          <div
            class="f-dialog-body"
            v-else-if="
              options.mode === 'confirm' &&
                options.confirmOptions &&
                options.confirmOptions.length
            "
          >
            <el-checkbox
              v-for="(item, idx) in options.confirmOptions"
              :key="idx"
              v-model="item.value"
              >{{ item.label }}</el-checkbox
            >
          </div>
          <div class="f-dialog-footer btn-block">
            <button
              type="button"
              class="btn btn--secondary del-cancel-btn"
              :class="{ red: options.lbDanger }"
              v-if="!options.lbHide"
              @click="lbClick"
              :value="options.lbLabel"
              ref="lbButton"
            >
              {{ options.lbLabel || $t('common._common.cancel') }}
            </button>
            <button
              type="button"
              class="btn btn--primary"
              :class="[
                options.rbDanger && 'btn--danger',
                options.rbClass ? options.rbClass : 'del-delete-btn',
              ]"
              v-if="!options.rbHide"
              @click="rbClick"
              :value="options.rbLabel"
              ref="rbButton"
              tabindex="-1"
              :disabled="loadingOnConfirm"
            >
              {{ options.rbLabel || 'Delete' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
const OPTIONS_TEMPLATE = {
  id: 'vue-fdialog-default', // DOM id
  className: '', // additional class name
  maskColor: 'rgba(40, 40, 40, 0.6)', // color of the mask area
  override: false, // current dialog overrides the queue and cancel any dialogs before it
  parent: 'body', // parent DOM node
  transition: 'f-dialog-transition', // transition name
  duration: 0, // milliseconds before auto close, set to 0 or any falsy value to disable auto close
  wide: false, // show as a wide dialog
  title: '', // dialog title
  message: '', // dialog message
  mode: 'alert', // alert, confirm or prompt
  forceStay: true, // set to true to prevent closing / canceling the dialog when mask area is clicked
  defaultButton: 'r', // set the button that gets focus when the dialog shows, available when at least two buttons are shown
  lbDanger: false, // set to true to style the left button as danger
  rbDanger: false, // set to true to style the right button as danger
  lbHide: false, // hide left button
  rbHide: false, // hide right button
  lbClass: '',
  rbClass: '',
  lbLabel: 'Cancel', // left button label text
  rbLabel: 'Ok', // right button label text
  actionAlt: null, // callback function when left button is clicked
  action: null, // callback function when right button is clicked
  promptType: 'text', // prompt type 'text' or 'textarea'
  promptPlaceholder: '',
  htmlMessage: '',
  confirmOptions: null,
  proceedFunc: null,
}
const CANCELLED = true
class Later {
  constructor() {
    this.promise = new Promise((resolve, reject) => {
      this.reject = reject
      this.resolve = resolve
    })
  }
}
export default {
  OPTIONS_TEMPLATE,
  props: [],
  components: {},
  computed: {
    options() {
      return Object.assign({}, OPTIONS_TEMPLATE, this.optionsData)
    },
  },
  watch: {},
  data() {
    return {
      queue: [],
      optionsData: {},
      visible: false,
      input: '',
      timeoutHandler: null,
      promiseHandler: null,
      loadingOnConfirm: false,
    }
  },
  methods: {
    enqueue(args) {
      let pending = this.queue.length
      if (args.override) {
        this.queue = [args]
        this.transit()
      } else {
        this.queue.push(args)
        if (pending === 0) this.transit()
      }
      args.promiseHandler = new Later()
      return args.promiseHandler.promise
    },
    consume(cancelled) {
      switch (this.options.mode) {
        case 'alert':
          this.optionsData.promiseHandler.resolve()
          break
        case 'confirm': {
          let value = !cancelled
          if (
            this.options.confirmOptions &&
            this.options.confirmOptions.length
          ) {
            value = [value, this.options.confirmOptions]
          }
          this.optionsData.promiseHandler.resolve(value)
          break
        }
        case 'prompt':
          if (cancelled) {
            this.optionsData.promiseHandler.resolve(null)
          } else {
            this.optionsData.promiseHandler.resolve(this.input)
          }
          break
        default:
          this.optionsData.promiseHandler.resolve()
          break
      }
      if (this.timeoutHandler) clearTimeout(this.timeoutHandler)
      this.timeoutHandler = null
      this.input = ''
      if (cancelled || !this.options.proceedFunc) {
        this.transit()
        this.queue.shift()
      } else {
        this.loadingOnConfirm = true
        this.options.proceedFunc().then(() => {
          this.close()
        })
      }
    },
    close() {
      this.loadingOnConfirm = false
      this.visible = false
      this.queue.shift()
    },
    transit() {
      this.visible = false
      setTimeout(() => {
        if (this.queue.length) {
          this.optionsData = this.queue[0]
          this.visible = true
          setTimeout(() => {
            if (this.options.mode === 'prompt') {
              this.$refs.promptInput.focus()
            } else {
              this.$refs.rbButton.focus()
            }
          })
          if (this.options.duration) {
            this.timeoutHandler = setTimeout(() => {
              this.consume()
            }, this.options.duration)
          }
        }
      })
    },
    escape() {
      this.consume(CANCELLED)
    },
    maskClick() {
      if (this.options.forceStay === false) {
        this.consume(CANCELLED)
        if (typeof this.options.actionAlt === 'function') {
          this.options.actionAlt()
        }
      }
    },
    lbClick() {
      this.consume(CANCELLED)
      if (typeof this.options.actionAlt === 'function') {
        this.options.actionAlt()
      }
    },
    rbClick() {
      this.consume()
      if (typeof this.options.action === 'function') {
        this.options.action()
      }
    },
  },
  mounted() {},
}
</script>

<style scoped>
.f-dialog-transition-leave {
  opacity: 1;
}
.f-dialog-transition-leave-active {
  transition: all 0.2s ease;
}
.f-dialog-transition-leave-to {
  opacity: 0;
}
.f-dialog-transition-enter .f-dialog-content,
.f-dialog-transition-leave-to .f-dialog-content {
  opacity: 0.8;
  top: 0 !important;
}

.f-dialog-wrapper {
  position: fixed;
  top: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
}
.f-dialog-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.f-dialog-content {
  width: 540px;
  top: 30%;
  min-height: 230px;
  padding: 0;
  position: relative;
  background-color: #ffffff;
  margin: 0 auto;
  overflow: hidden;
  outline: none;
  transition: all 0.3s ease;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.05), 0 1px 4px rgba(0, 0, 0, 0.15);
  border-bottom-right-radius: 2px;
  border-bottom-left-radius: 2px;
  max-width: 550px;
  background: #fff;
  -webkit-animation: dialogslidedown 0.3s ease;
  animation: dialogslidedown 0.3s ease;
}

@keyframes dialogslidedown {
  0% {
    transform: translateY(-100%);
    opacity: 0;
  }

  100% {
    transform: translateY(0);
    opacity: 1;
  }
}
.f-dialog-maxheight .f-dialog-body {
  padding: 0 60px 20px;
  height: 450px;
}
.f-dialog-minheight .f-dialog-body {
  padding: 0 60px 20px;
  height: 300px;
}
.f-dialog-header {
  padding: 20px 16px 20px;
  font-size: 16px;
  font-weight: 500;
}

.f-dialog-header.with-body {
  padding-bottom: 18px;
}

.f-dialog-body {
  padding: 0 60px 20px;
}

.f-dialog-footer {
  width: 100%;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  flex-direction: row;
  text-align: right;
  user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  -webkit-user-select: none;
  margin-top: 50px;
}

.prompt-input-wrapper {
  width: 100%;
  height: 100px;
  margin-top: 12px;
  margin-bottom: 40px;
}
.prompt-input-wrapper .prompt-input {
  width: 100%;
  height: 100%;
}
.f-dialog-wrapper .content {
  white-space: pre-wrap;
  /* word-break: break-all; */
  margin: 0;
}

.f-dialog-wrapper input {
  outline: none;
  -webkit-appearance: none;
}

.f-dialog-wrapper input.prompt-input {
  width: 100%;
  box-sizing: border-box;
  line-height: 22px;
  padding: 4px;
  border: 1px solid #d0d0d0;
}
.del-cancel-btn {
  width: 100%;
  background-color: #f4f4f4;
  border: transparent;
  border-radius: none;
  margin-left: 0 !important;
  padding-top: 20px;
  padding-bottom: 20px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 1.1px;
  text-align: center;
  color: #8f8f8f;
}
.del-delete-btn {
  width: 100%;
  background-color: #e47676;
  border: transparent;
  margin-left: 0 !important;
  padding-top: 20px;
  padding-bottom: 20px;
  border-radius: 0;
  font-size: 13px;
  font-weight: bold;
  letter-spacing: 1.1px;
  text-align: center;
  color: #ffffff;
}
.del-delete-btn:hover {
  background-color: #d95f5f;
}
.fdialog-edit {
  width: 100%;
  background-color: #39b2c2;
  border: transparent;
  margin-left: 0 !important;
  padding-top: 20px;
  padding-bottom: 20px;
  border-radius: 0;
  font-size: 13px;
  font-weight: bold;
  letter-spacing: 1.1px;
  text-align: center;
  color: #ffffff;
}
.delete-heading {
  font-size: 14px;
  font-weight: bold;
  letter-spacing: 1.3px;
  text-align: left;
  color: #000000;
  margin: 0;
  line-height: 2.71;
}
.delete-content {
  font-size: 14px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.29;
  letter-spacing: 0.9px;
  text-align: left;
  color: #333333;
}
.delete-body {
  width: 100%;
  display: flex;
  flex-wrap: nowrap;
  justify-content: center;
}
.delete-icon {
  width: 45px;
  float: left;
  position: relative;
  top: 11px;
}
.delete-body-content {
  width: 330px;
  float: left;
}
.prompt-dialog {
  padding: 0 40px 40px !important;
}
</style>
