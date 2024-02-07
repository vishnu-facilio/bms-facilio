<template>
  <f-form
    ref="newLocationForm"
    type="dialog40"
    :title="formTitle"
    :model="newlocationmodel"
    @save="save"
    class="newLocationForm"
  >
    <template v-slot:form="fscope">
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.name"
            class="form-item"
            float-label="Name"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.street"
            class="form-item"
            float-label="Street"
            type="textarea"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.city"
            class="form-item"
            float-label="City"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.state"
            class="form-item"
            float-label="State"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.zip"
            class="form-item"
            float-label="ZipCode"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.country"
            class="form-item"
            float-label="Country"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.lat"
            class="form-item"
            float-label="Latitude"
            type="number"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.lng"
            class="form-item"
            float-label="Longitude"
            type="number"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-select
            v-model="fscope.formModel.contact.id"
            class="form-item"
            float-label="Contact Users"
            :options="users | options('user')"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.phone"
            class="form-item"
            float-label="Contact Phone"
            type="tel"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24">
          <q-input
            v-model="fscope.formModel.faxPhone"
            class="form-item"
            float-label="Fax Phone"
            type="tel"
          />
        </el-col>
      </el-row>
      <el-row :gutter="50" align="middle">
        <el-col :span="24"> </el-col>
      </el-row>
    </template>
  </f-form>
</template>
<script>
import { QInput, QSelect } from 'quasar'
import FForm from '@/FForm'
import clone from 'lodash/clone'

export default {
  props: ['data'],
  components: {
    QInput,
    QSelect,
    FForm,
  },
  data() {
    return {
      formTitle: 'New Location',
      newlocationmodel: {
        name: '',
        street: '',
        city: '',
        state: '',
        zip: '',
        country: '',
        lat: '',
        lng: '',
        phone: '',
        faxPhone: '',
        contact: {
          id: -1,
          name: '',
        },
        contactList: [],
      },
      supportedStates: [true, false],
    }
  },
  computed: {
    users() {
      let userList = this.$store.state.users
      if (userList) {
        return userList
      }
      return []
    },
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
  },
  methods: {
    open() {
      this.$refs.newLocationForm.open()
      this.categories = clone(this.$store.state.ticketCategory)
    },
    close() {
      this.$refs.newLocationForm.close()
    },
    save() {
      let formModel = this.$refs.newLocationForm.getFormModel()
      let self = this
      self.$http
        .post('/location/add', { location: formModel })
        .then(function(response) {
          self.$message.success(' New Location added Successfully.')
          self.close()
        })
    },
  },
  filters: {
    options: function(jsonobj, type) {
      let array = []
      if (type === 'category' || type === 'priority') {
        for (let jsonkey in jsonobj) {
          let val = jsonobj[jsonkey]
          array.push({ label: val, value: parseInt(jsonkey) })
        }
      } else if (type === 'user' || type === 'space') {
        for (let key in jsonobj) {
          array.push({ label: jsonobj[key].name, value: jsonobj[key].id })
        }
      }
      return array
    },
  },
}
</script>
<style>
.newLocationForm .form-content {
  overflow-y: scroll;
  height: 80vh;
}
</style>
