<template>
  <div v-if="user || name" class="fc-avatar-inline">
    <img
      class="fc-avatar"
      :class="sizeClass"
      :src="avatarUrl"
      v-if="avatarUrl"
      alt="Profile Avatar"
    />
    <div class="fc-avatar" :class="sizeClass" :style="bgColor" v-else>
      {{ trimmedName }}
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'

export default {
  props: {
    size: {
      type: String,
    },
    user: {
      type: Object,
    },
    avatarStyle: {
      type: Object,
    },
    color: {
      type: String,
    },
    name: {
      type: Boolean,
    },
  },
  data() {
    return {
      sizeClass: 'fc-avatar-' + this.size,
      fullName: '',
      trimmedName: null,
      avatarUrl: null,
      bgColor:
        'background-color: ' +
        (this.color ? this.color : this.getRandomBgColor()),
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  mounted() {
    if (this.user) {
      let userName = ''
      if (this.user.avatarUrl) {
        this.avatarUrl = this.user.avatarUrl
      } else {
        if (this.user.id) {
          let userObj = this.users.find(user => user.id === this.user.id)
          if (userObj && userObj.avatarUrl) {
            this.avatarUrl = userObj.avatarUrl
          }
          if (userObj && userObj.name) {
            userName = userObj.name
          }
        }
      }
      userName = userName ? userName : this.user.name
      if (userName) {
        this.fullName = userName
        this.trimmedName = this.getAvatarName(userName)
        this.bgColor =
          'background-color: ' +
          (this.color ? this.color : this.getRandomBgColor())
      }
    } else if (this.name) {
      this.trimmedName = this.getAvatarName(this.name)
      this.bgColor =
        'background-color: ' +
        (this.color ? this.color : this.getRandomBgColor())
    }
  },
  watch: {
    user: function() {
      if (this.user.avatarUrl) {
        this.avatarUrl = this.user.avatarUrl
      } else {
        if (this.user.id) {
          let userObj = this.users.find(user => user.id === this.user.id)
          if (userObj && userObj.avatarUrl) {
            this.avatarUrl = userObj.avatarUrl
          } else {
            this.avatarUrl = null
          }
        } else {
          this.avatarUrl = null
        }
      }
      if (this.user.name) {
        this.fullName = this.user.name
        this.trimmedName = this.getAvatarName(this.user.name)
        this.bgColor =
          'background-color: ' +
          (this.color ? this.color : this.getRandomBgColor())
      }
    },
    'user.name': function() {
      if (this.user.name) {
        this.fullName = this.user.name
        this.trimmedName = this.getAvatarName(this.user.name)
        this.bgColor =
          'background-color: ' +
          (this.color ? this.color : this.getRandomBgColor())
      }
    },
    'user.avatarUrl': function() {
      if (this.user.avatarUrl) {
        this.avatarUrl = this.user.avatarUrl
      } else {
        this.avatarUrl = null
      }
    },
    'user.id': function() {
      if (this.user.id) {
        let userObj = this.users.find(user => user.id === this.user.id)
        if (userObj && userObj.avatarUrl) {
          this.avatarUrl = userObj.avatarUrl
        } else {
          this.avatarUrl = null
        }
      } else {
        this.avatarUrl = null
      }
    },
  },
  methods: {
    getAvatarName(name) {
      let parts = name.split(/[ -]/)

      let initials = ''
      let initialLen = 2
      let count = 0
      for (let i = 0; i < parts.length; i++) {
        if (parts[i].trim() !== '') {
          initials += parts[i].charAt(0)
          count++
          if (count >= initialLen) {
            break
          }
        }
      }

      if (initials.length < initialLen && name.length >= initialLen) {
        initials = name.trim().substring(0, initialLen)
      }
      let avatarName = initials.toUpperCase()
      return avatarName
    },
    getRandomBgColor() {
      let colors = [
        '#FFBA51',
        '#34BFA3',
        '#FF2F82',
        '#29D9A7',
        '#ECDC74',
        '#927FED',
        '#FF61A8',
        '#fbf383',
        '#ac4352',
        '#6db1f4',
      ]

      let userKey = this.user.email ? this.user.email : this.user.name
      let userUniqueNum = Array.from(userKey)
        .map(letter => letter.charCodeAt(0))
        .reduce((current, previous) => previous + current)

      let colorIndex = userUniqueNum % colors.length
      let color = colors[colorIndex]
      return color
    },
  },
}
</script>
