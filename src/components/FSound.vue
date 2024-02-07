<template>
  <div class="hide">
    <audio
      ref="fglobalSound"
      v-for="(sound, index) in sounds"
      :key="index"
      :src="sound.url"
    ></audio>
  </div>
</template>
<script>
export default {
  data() {
    return {
      activeSound: require('statics/sounds/notification.mp3'),
      sounds: [
        {
          name: 'default',
          url: require('statics/sounds/notification.mp3'),
        },
        {
          name: 'stuffed',
          url: require('statics/sounds/stuffed-and-dropped.mp3'),
        },
        {
          name: 'alarm',
          // url: require('statics/sounds/alarm_default.mp3'),
          url: require('statics/sounds/htc_single_beep.mp3'),
        },
      ],
    }
  },
  methods: {
    play(name) {
      let soundName = name || 'default'
      let sound = this.sounds.find(sound => sound.name === soundName)
      let soundIndex = this.sounds.indexOf(sound)
      this.$refs.fglobalSound[soundIndex].play()
    },
    stop(name) {
      let soundName = name || 'default'
      let sound = this.sounds.find(sound => sound.name === soundName)
      let soundIndex = this.sounds.indexOf(sound)
      this.$refs.fglobalSound[soundIndex].pause()
      this.$refs.fglobalSound[soundIndex].currentTime = 0
    },
  },
}
</script>
