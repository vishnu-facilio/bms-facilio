<template>
  <div class="people-mention-popover d-flex p20">
    <div class=" flex-middle">
      <PeopleAvatar
        :name="false"
        :users="users"
        size="lg"
        :people="people"
      ></PeopleAvatar>
    </div>
    <div class="mL20">
      <div class="bold f16 primary-font mT2">
        {{ people.name || '---' }}
      </div>
      <div class=" flex-middle mT5">
        <fc-icon
          color="#324056"
          class="pR5"
          size="16"
          group="communication"
          name="mail"
        ></fc-icon
        ><span class="primary-font f13 maxWidth200px textoverflow-ellipsis"
          >{{ people.email || '---' }}
        </span>
      </div>
      <div class="flex-middle mT5">
        <fc-icon
          color="#324056"
          size="16"
          class="pR5"
          group="communication"
          name="call"
        ></fc-icon>
        <span class="primary-font f13">{{ people.mobile || '---' }} </span>
      </div>
    </div>
  </div>
</template>
<script>
import PeopleAvatar from 'src/components/notes/PeopleAvatar.vue'
export default {
  props: ['people', 'users'],
  mounted() {},
  data() {
    return {
      defaultheight: '160px',
    }
  },
  components: {
    PeopleAvatar,
  },

  computed: {},
}
</script>
<!-- remove unused css -->
<style scoped lang="scss">
.people-mention-popover {
  border-radius: 4px;
  color: #606266;
  background: #fff;
  line-height: 1.4;
  text-align: justify;
  font-size: 14px;
  -webkit-box-shadow: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  word-break: break-all;
  background: white;
  min-width: 260px;
}
.maxWidth200px {
  max-width: 200px;
}
</style>
