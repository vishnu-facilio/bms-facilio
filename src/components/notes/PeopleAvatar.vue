<template>
  <div v-if="people" class="inline">
    <div
      class="fc-avatar-element q-item-division relative-position cursor-pointer"
    >
      <div class="q-item-side q-item-side-left q-item-section relative">
        <div v-if="avatarUrl">
          <img class="fc-people-avatar" :src="avatarUrl" />
        </div>
        <div v-else class="fc-people-avatar" :style="bgColor">
          {{ trimmedName }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['size', 'people', 'color', 'users'],
  data() {
    return {
      avatarUrl: null,
      trimmedName: '',
      bgColor:
        'background-color: ' +
        (this.color ? this.color : this.getRandomBgColor()),
    }
  },
  mounted() {
    this.loadAvatarUrl()
  },

  watch: {
    people: function() {
      this.loadAvatarUrl()
    },
  },
  methods: {
    loadAvatarUrl() {
      let { people } = this
      if (people.avatarId && people.avatarId > 0) {
        this.avatarUrl = people.avatarUrl
        return
      } else if (people.id) {
        let userObj = this.users.find(user => user.peopleId === people.id)
        if (userObj && userObj.avatarUrl) {
          this.avatarUrl = userObj.avatarUrl
          return
        }
      }
      this.trimmedName = this.getAvatarName(this.people.name)
    },
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
    getDefaultAvatar() {
      let avatarUrl = ''
      avatarUrl = require('statics/space/building.svg')
      return avatarUrl
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

      let userKey = this.people.name
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
<style>
.upload-space-photos {
  position: absolute;
  bottom: 5px;
  right: 0px;
  display: none;
  z-index: 10;
  width: 100%;
  text-align: center;
  background: #fff;
  padding: 5px;
}

.fc-avatar-element:hover .upload-space-photos {
  display: block;
}

.fc-people-avatar {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  font-size: 30px;
  align-items: center;
  box-sizing: border-box;
  display: inline-flex;
  justify-content: center;
  position: relative;
  vertical-align: top;
  font-weight: 500;
  background: center/cover #f9f9f9;
  color: #fff;
}
</style>
