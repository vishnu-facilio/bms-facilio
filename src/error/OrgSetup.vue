<template>
  <div class="signup-progress-container">
    <img class="fc-rebrand-logo mB40" :src="brandConfig.logo" alt="Logo" />
    <template v-if="!signupFailed">
      <div style="max-width: 200px; margin: 0 auto;">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          x="0px"
          y="0px"
          viewBox="0 0 645 526"
          class="machine"
        >
          <defs></defs>
          <g>
            <path
              x="-173,694"
              y="-173,694"
              d="M645 194v-21l-29-4c-1-10-3-19-6-28l25-14 -8-19 -28 7c-5-8-10-16-16-24L602 68l-15-15 -23 17c-7-6-15-11-24-16l7-28 -19-8 -14 25c-9-3-18-5-28-6L482 10h-21l-4 29c-10 1-19 3-28 6l-14-25 -19 8 7 28c-8 5-16 10-24 16l-23-17L341 68l17 23c-6 7-11 15-16 24l-28-7 -8 19 25 14c-3 9-5 18-6 28l-29 4v21l29 4c1 10 3 19 6 28l-25 14 8 19 28-7c5 8 10 16 16 24l-17 23 15 15 23-17c7 6 15 11 24 16l-7 28 19 8 14-25c9 3 18 5 28 6l4 29h21l4-29c10-1 19-3 28-6l14 25 19-8 -7-28c8-5 16-10 24-16l23 17 15-15 -17-23c6-7 11-15 16-24l28 7 8-19 -25-14c3-9 5-18 6-28L645 194zM471 294c-61 0-110-49-110-110S411 74 471 74s110 49 110 110S532 294 471 294z"
              class="large-shadow"
            ></path>
          </g>
          <g>
            <path
              x="-136,996"
              y="-136,996"
              d="M402 400v-21l-28-4c-1-10-4-19-7-28l23-17 -11-18L352 323c-6-8-13-14-20-20l11-26 -18-11 -17 23c-9-4-18-6-28-7l-4-28h-21l-4 28c-10 1-19 4-28 7l-17-23 -18 11 11 26c-8 6-14 13-20 20l-26-11 -11 18 23 17c-4 9-6 18-7 28l-28 4v21l28 4c1 10 4 19 7 28l-23 17 11 18 26-11c6 8 13 14 20 20l-11 26 18 11 17-23c9 4 18 6 28 7l4 28h21l4-28c10-1 19-4 28-7l17 23 18-11 -11-26c8-6 14-13 20-20l26 11 11-18 -23-17c4-9 6-18 7-28L402 400zM265 463c-41 0-74-33-74-74 0-41 33-74 74-74 41 0 74 33 74 74C338 430 305 463 265 463z"
              class="medium-shadow"
            ></path>
          </g>
          <g>
            <path
              x="-100,136"
              y="-100,136"
              d="M210 246v-21l-29-4c-2-10-6-18-11-26l18-23 -15-15 -23 18c-8-5-17-9-26-11l-4-29H100l-4 29c-10 2-18 6-26 11l-23-18 -15 15 18 23c-5 8-9 17-11 26L10 225v21l29 4c2 10 6 18 11 26l-18 23 15 15 23-18c8 5 17 9 26 11l4 29h21l4-29c10-2 18-6 26-11l23 18 15-15 -18-23c5-8 9-17 11-26L210 246zM110 272c-20 0-37-17-37-37s17-37 37-37c20 0 37 17 37 37S131 272 110 272z"
              class="small-shadow"
            ></path>
          </g>
          <g>
            <path
              x="-100,136"
              y="-100,136"
              d="M200 236v-21l-29-4c-2-10-6-18-11-26l18-23 -15-15 -23 18c-8-5-17-9-26-11l-4-29H90l-4 29c-10 2-18 6-26 11l-23-18 -15 15 18 23c-5 8-9 17-11 26L0 215v21l29 4c2 10 6 18 11 26l-18 23 15 15 23-18c8 5 17 9 26 11l4 29h21l4-29c10-2 18-6 26-11l23 18 15-15 -18-23c5-8 9-17 11-26L200 236zM100 262c-20 0-37-17-37-37s17-37 37-37c20 0 37 17 37 37S121 262 100 262z"
              class="small"
            ></path>
          </g>
          <g>
            <path
              x="-173,694"
              y="-173,694"
              d="M635 184v-21l-29-4c-1-10-3-19-6-28l25-14 -8-19 -28 7c-5-8-10-16-16-24L592 58l-15-15 -23 17c-7-6-15-11-24-16l7-28 -19-8 -14 25c-9-3-18-5-28-6L472 0h-21l-4 29c-10 1-19 3-28 6L405 9l-19 8 7 28c-8 5-16 10-24 16l-23-17L331 58l17 23c-6 7-11 15-16 24l-28-7 -8 19 25 14c-3 9-5 18-6 28l-29 4v21l29 4c1 10 3 19 6 28l-25 14 8 19 28-7c5 8 10 16 16 24l-17 23 15 15 23-17c7 6 15 11 24 16l-7 28 19 8 14-25c9 3 18 5 28 6l4 29h21l4-29c10-1 19-3 28-6l14 25 19-8 -7-28c8-5 16-10 24-16l23 17 15-15 -17-23c6-7 11-15 16-24l28 7 8-19 -25-14c3-9 5-18 6-28L635 184zM461 284c-61 0-110-49-110-110S401 64 461 64s110 49 110 110S522 284 461 284z"
              class="large"
            ></path>
          </g>
          <g>
            <path
              x="-136,996"
              y="-136,996"
              d="M392 390v-21l-28-4c-1-10-4-19-7-28l23-17 -11-18L342 313c-6-8-13-14-20-20l11-26 -18-11 -17 23c-9-4-18-6-28-7l-4-28h-21l-4 28c-10 1-19 4-28 7l-17-23 -18 11 11 26c-8 6-14 13-20 20l-26-11 -11 18 23 17c-4 9-6 18-7 28l-28 4v21l28 4c1 10 4 19 7 28l-23 17 11 18 26-11c6 8 13 14 20 20l-11 26 18 11 17-23c9 4 18 6 28 7l4 28h21l4-28c10-1 19-4 28-7l17 23 18-11 -11-26c8-6 14-13 20-20l26 11 11-18 -23-17c4-9 6-18 7-28L392 390zM255 453c-41 0-74-33-74-74 0-41 33-74 74-74 41 0 74 33 74 74C328 420 295 453 255 453z"
              class="medium"
            ></path>
          </g>
        </svg>
      </div>
      <div style="margin-top: 30px;font-size: 18px;">
        Setting up your account, please wait a moment.
      </div>
    </template>
    <template v-else>
      <img
        src="data:image/svg+xml;charset=utf-8,%3Csvg width='84' height='84' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Ccircle stroke='%23E55E3F' stroke-width='3' cx='42' cy='42' r='40.5'/%3E%3Cpath d='M42 39.879l12.94-12.94 2.12 2.122L44.122 42l12.94 12.94-2.122 2.12L42 44.122l-12.94 12.94-2.12-2.122L39.878 42l-12.94-12.94 2.122-2.12L42 39.878z' fill='%23E55E3F' fill-rule='nonzero'/%3E%3C/g%3E%3C/svg%3E"
        style="width: 130px;"
      />
      <div style="margin-top: 30px;font-size: 18px;line-height: 30px;">
        The account setup process has failed. We kindly request that you please
        contact our support team for assistance."
      </div>
    </template>
  </div>
</template>
<script>
import { API } from '@facilio/api'
let intervalInstance = null
export default {
  data() {
    return {
      signupCompleted: false,
      signupFailed: false,
    }
  },
  computed: {
    brandConfig()
    {
      return window.brandConfig
    },
  },
  mounted() {
    intervalInstance = setInterval(() => {
      if (!this.signupCompleted && !this.signupFailed) {
        API.get('/v2/orgsetup').then(({ data }) => {
          if (data) {
            if (data.status == 'completed') {
              this.signupCompleted = true
              window.location.href = '/'
            } else if (data.status == 'failed') {
              this.signupFailed = true
            }
          }
        })
      }
    }, 5000)
  },
  destroyed() {
    clearInterval(intervalInstance)
  },
  methods: {},
}
</script>
<style>
.signup-progress-container {
  fill: #4e36a6;
  width: 500px;
  margin: 0 auto;
  border: 1px solid #eee;
  padding: 50px;
  text-align: center;
  margin-top: 100px;
}

.signup-progress-container .small-shadow,
.signup-progress-container .medium-shadow,
.signup-progress-container .large-shadow {
  fill: rgba(0, 0, 0, 0.05);
}

.signup-progress-container .small {
  -webkit-animation: counter-rotation 2.5s infinite linear;
  -moz-animation: counter-rotation 2.5s infinite linear;
  -o-animation: counter-rotation 2.5s infinite linear;
  animation: counter-rotation 2.5s infinite linear;
  -webkit-transform-origin: 100.136px 225.345px;
  -ms-transform-origin: 100.136px 225.345px;
  transform-origin: 100.136px 225.345px;
}

.signup-progress-container .small-shadow {
  -webkit-animation: counter-rotation 2.5s infinite linear;
  -moz-animation: counter-rotation 2.5s infinite linear;
  -o-animation: counter-rotation 2.5s infinite linear;
  animation: counter-rotation 2.5s infinite linear;
  -webkit-transform-origin: 110.136px 235.345px;
  -ms-transform-origin: 110.136px 235.345px;
  transform-origin: 110.136px 235.345px;
}

.signup-progress-container .medium {
  -webkit-animation: rotation 3.75s infinite linear;
  -moz-animation: rotation 3.75s infinite linear;
  -o-animation: rotation 3.75s infinite linear;
  animation: rotation 3.75s infinite linear;
  -webkit-transform-origin: 254.675px 379.447px;
  -ms-transform-origin: 254.675px 379.447px;
  transform-origin: 254.675px 379.447px;
}

.signup-progress-container .medium-shadow {
  -webkit-animation: rotation 3.75s infinite linear;
  -moz-animation: rotation 3.75s infinite linear;
  -o-animation: rotation 3.75s infinite linear;
  animation: rotation 3.75s infinite linear;
  -webkit-transform-origin: 264.675px 389.447px;
  -ms-transform-origin: 264.675px 389.447px;
  transform-origin: 264.675px 389.447px;
}

.signup-progress-container .large {
  -webkit-animation: counter-rotation 5s infinite linear;
  -moz-animation: counter-rotation 5s infinite linear;
  -o-animation: counter-rotation 5s infinite linear;
  animation: counter-rotation 5s infinite linear;
  -webkit-transform-origin: 461.37px 173.694px;
  -ms-transform-origin: 461.37px 173.694px;
  transform-origin: 461.37px 173.694px;
}

.signup-progress-container .large-shadow {
  -webkit-animation: counter-rotation 5s infinite linear;
  -moz-animation: counter-rotation 5s infinite linear;
  -o-animation: counter-rotation 5s infinite linear;
  animation: counter-rotation 5s infinite linear;
  -webkit-transform-origin: 471.37px 183.694px;
  -ms-transform-origin: 471.37px 183.694px;
  transform-origin: 471.37px 183.694px;
}

@-webkit-keyframes rotation {
  from {
    -webkit-transform: rotate(0deg);
  }
  to {
    -webkit-transform: rotate(359deg);
  }
}
@-moz-keyframes rotation {
  from {
    -moz-transform: rotate(0deg);
  }
  to {
    -moz-transform: rotate(359deg);
  }
}
@-o-keyframes rotation {
  from {
    -o-transform: rotate(0deg);
  }
  to {
    -o-transform: rotate(359deg);
  }
}
@keyframes rotation {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(359deg);
  }
}

@-webkit-keyframes counter-rotation {
  from {
    -webkit-transform: rotate(359deg);
  }
  to {
    -webkit-transform: rotate(0deg);
  }
}
@-moz-keyframes counter-rotation {
  from {
    -moz-transform: rotate(359deg);
  }
  to {
    -moz-transform: rotate(0deg);
  }
}
@-o-keyframes counter-rotation {
  from {
    -o-transform: rotate(359deg);
  }
  to {
    -o-transform: rotate(0deg);
  }
}
@keyframes counter-rotation {
  from {
    transform: rotate(359deg);
  }
  to {
    transform: rotate(0deg);
  }
}
</style>
