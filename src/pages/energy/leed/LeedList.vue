<template>
  <div v-if="!isLoginRequired">
    <div class="row layout-padding" v-if="!currentLeedId">
      <!-- div style="width:100%">
       <router-link :to="'new'" append style="padding:0;float:right">
          <button class="sh-button button-add  shadow-12"><q-icon name="add" style="font-size:18px" /></button>
        </router-link>
        <button @click="importArcAssets" class="sh-button button-add  shadow-12 pull-right"><q-icon name="add" style="font-size:18px" /></button>    </div -->
      <div class="fc-viewlayout">
        <div v-if="loading" style="padding: 50px">Loading...</div>
        <q-data-table
          :data="rows"
          :config="config"
          :columns="columns"
          @refresh="refresh"
          @selection="selection"
          @rowclick="rowClick"
          v-else
        >
          <template v-slot:col-message="cell">
            <span class="light-paragraph">{{ cell.data }}</span>
          </template>
          <template v-slot:col-source="cell">
            <div
              v-if="cell.data === 'Audit'"
              class="my-label text-white bg-primary"
            >
              Audit
              <q-tooltip>Some data</q-tooltip>
            </div>
            <div v-else class="my-label text-white bg-negative">
              {{ cell.data }}
            </div>
          </template>

          <template v-slot:selection="props">
            <q-btn flat color="primary" @click="changeMessage(props)">
              <q-icon name="edit" />
            </q-btn>
            <q-btn flat color="primary" @click="deleteRow(props)">
              <q-icon name="delete" />
            </q-btn>
          </template>
        </q-data-table>
      </div>
      <q-modal
        ref="createBuildings"
        position="right"
        content-classes="fc-create-building"
        class="createBuildings"
      >
        <q-btn flat @click="$refs.createBuildings.close()">
          <q-icon name="close" />
        </q-btn>
        <div class="fc-formlayout">
          <div>
            <q-toolbar inverted>
              <q-toolbar-title>New Leed Project</q-toolbar-title>
            </q-toolbar>
            <form v-on:submit.prevent="submit" style="padding:0 25px;">
              <div class="row md-gutter">
                <div class="col-lg-12 col-md-12">
                  <div>
                    <q-select
                      float-label="Building name"
                      :options="alarmtype"
                    />
                    <q-input float-label="Leed ID" />
                  </div>
                </div>
                <div
                  class="col-xs-12 col-sm-12 col-md-12 col-lg-12"
                  align="right"
                >
                  <q-btn
                    flat
                    type="button"
                    cancel
                    @click="$refs.createBuildings.close()"
                    >Cancel</q-btn
                  >
                  <q-btn loader color="primary">
                    Save
                    <span slot="loading">Saving...</span>
                  </q-btn>
                </div>
              </div>
            </form>
          </div>
        </div>
      </q-modal>
    </div>
    <router-view v-else></router-view>
  </div>
  <login @onlogin="onLogin" v-else></login>
</template>
<script>
import login from './LeedLogin'
import {
  QDataTable,
  QBtn,
  QIcon,
  QTooltip,
  QModal,
  QToolbar,
  QToolbarTitle,
  QInput,
  QSelect,
} from 'quasar'

export default {
  data() {
    return {
      loading: true,
      search: null,
      config: {
        title: 'Leeds',
        refresh: true,
        noHeader: false,
        columnPicker: false,
        leftStickyColumns: 0,
        rightStickyColumns: 2,
        bodyStyle: {
          maxHeight: '500px',
        },
        rowHeight: '50px',
        responsive: true,
        pagination: {
          rowsPerPage: 10,
          options: [5, 10, 15, 30, 50, 500],
        },
      },
      alarmtype: [
        {
          label: 'Maintenance',
          value: 1,
        },
        {
          label: 'Critical',
          value: 2,
        },
        {
          label: 'Life Safety',
          value: 3,
        },
      ],
      rows: [],
      columns: [],
      pagination: true,
      rowHeight: 50,
      bodyHeightProp: 'maxHeight',
      bodyHeight: 500,
    }
  },
  computed: {
    isLoginRequired() {
      return this.$store.state.leed.isLoginRequired
    },
    currentLeedId() {
      return this.$route.params.id
    },
    leeds() {
      return this.$store.state.leed.leeds
    },
    viewlayout() {
      return this.$store.state.leed.viewlayout
    },
  },
  mounted() {
    console.log('mounted......')
    this.loadAllLeeds()
  },
  title() {
    return 'Leed Console'
  },
  components: {
    QDataTable,
    QBtn,
    QIcon,
    QTooltip,
    QModal,
    QToolbar,
    QToolbarTitle,
    QInput,
    QSelect,
    login,
  },
  methods: {
    onLogin(value) {
      this.loadAllLeeds()
    },
    loadAllLeeds() {
      let self = this
      self.loading = true
      self.$store
        .dispatch('leed/fetchLeeds')
        .then(function(response) {
          self.constructTable()
          self.loading = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
    },
    importArcAssets() {
      this.$http
        .get('/leed/importleeds')
        .then(function(response) {
          let resp = response.data
          console.log(resp)
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    constructTable() {
      let meta = {}
      this.columns = []
      for (let column in this.viewlayout.columns) {
        let viewColumn = this.viewlayout.columns[column]
        console.log(viewColumn)
        if (viewColumn.columnType === 'LOOKUP') {
          meta[viewColumn.id] = {
            type: viewColumn.columnType,
            lookupId: viewColumn.lookupId,
          }
        } else {
          meta[viewColumn.id] = { type: viewColumn.columnType }
        }
        let tableColumn = {
          label: viewColumn.label,
          field: viewColumn.id,
          type: 'string',
          sort: true,
        }
        this.columns.push(tableColumn)
      }
      for (let record in this.leeds) {
        for (let cell in this.leeds[record]) {
          if (meta[cell] && meta[cell].type === 'LOOKUP') {
            if (this.leeds[record][cell]) {
              this.leeds[record][cell] = this.leeds[record][cell][
                meta[cell].lookupId
              ]
            } else {
              this.leeds[record][cell] = '---'
            }
            console.log('record')
            console.log(this.leeds[record])
          }
        }
      }
      this.rows = this.leeds
    },
    deleteRow(props) {
      props.rows.forEach(row => {
        this.table.splice(row.index, 1)
      })
    },
    refresh(done) {
      this.loadAllLeeds()
      this.timeout = setTimeout(() => {
        done()
      }, 500)
    },
    selection(number, rows) {
      console.log(`selected ${number}: ${rows}`)
    },
    rowClick(row) {
      console.log('clicked on a row' + row)
      console.log('ROW ID :' + row.id)
      this.$router.push({ path: '/app/em/leeds/' + row.id + '/energy' })
    },
  },
}
</script>
<style>
.fc-viewlayout {
  padding-top: 20px;
}
.fc-viewlayout .q-data-table {
  background: #fff;
}
.fc-create-building {
  width: 40% !important;
  height: 100% !important;
  max-height: 100% !important;
  max-width: 40wv !important;
}
.sh-button {
  background-color: #ff2d81;
  border: 0;
  color: #fff;
  padding: 5px 9px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 10px 2px;
  cursor: pointer;
  outline: none;
}
.fc-createBuildings {
  max-width: 40vw !important;
}
</style>
