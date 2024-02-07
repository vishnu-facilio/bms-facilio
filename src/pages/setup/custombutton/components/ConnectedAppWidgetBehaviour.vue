<template>
  <el-row>
    <el-col>
      <el-form>
        <el-form-item>
          <div>
            <p class="details-Heading pB10">
              {{ $t('setup.customButton.connected_app_name') }}
            </p>
          </div>
          <el-select
            :loading="appListLoader"
            v-model="selectedApp"
            clearable
            class="width100 pR20"
            :placeholder="
              $t('setup.customButton.connected_app_name_placeholder')
            "
          >
            <el-option
              v-for="apps in connectedAppList"
              :key="apps.id"
              :value="apps.id"
              :label="apps.name"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-col>

    <el-col>
      <el-form>
        <el-form-item>
          <div>
            <p class="details-Heading pB10">
              {{ $t('setup.customButton.connected_app_widget') }}
            </p>
          </div>
          <el-select
            v-model="selectedWidget"
            clearable
            class="width100 pR20"
            :placeholder="
              $t('setup.customButton.connected_app_widget_placeholder')
            "
          >
            <el-option
              v-for="widget in widgetList"
              :key="widget.id"
              :value="widget.id"
              :label="widget.widgetName"
            >
            </el-option>
          </el-select>
          <p v-if="!hasCustomButtonWidget" style="color: #fd6e6e" class="f12">
            {{ `Please select a form with Custom button widget` }}
          </p>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { ACTION_TYPES } from '../CustomButtonUtil'
export default {
  name: 'ConnectedAppWidgetBehaviour',
  props: ['customButtonObject'],
  data() {
    return {
      connectedAppList: [],
      selectedApp: null,
      appListLoader: false,
      widgetList: [],
      selectedWidget: null,
      hasCustomButtonWidget: true,
    }
  },
  created() {
    this.loadConnectedApps().then(() => {
      this.deserialize()
    })
  },
  methods: {
    deserialize() {
      let { config = {} } = this.customButtonObject
      let { appId, actionType, widgetId } = config || {}

      if (
        actionType &&
        actionType === ACTION_TYPES.CONNECTED_APPS &&
        widgetId &&
        appId
      ) {
        let currApp = this.connectedAppList.find(item => item.id === appId)

        this.selectedApp = currApp.id

        let currWidget = currApp.connectedAppWidgetsList.find(
          item => item.id === widgetId
        )
        this.selectedWidget = currWidget.id
      }
    },
    async loadConnectedApps() {
      this.appListLoader = true
      let { data } = await API.get('v2/connectedApps/all')
      this.connectedAppList = data.connectedApps
      this.appListLoader = false
    },
    setSelectedAppWidgets() {
      let { connectedAppList, selectedApp } = this
      let currSelectedApp = connectedAppList.find(app => app.id === selectedApp)
      let allWidgets = currSelectedApp.connectedAppWidgetsList
      if (!isEmpty(allWidgets)) {
        this.widgetList = allWidgets.filter(
          widget => widget.entityTypeEnum === 'CUSTOM_BUTTON'
        )
      }
      if (isEmpty(this.widgetList)) {
        this.hasCustomButtonWidget = false
      }
    },
    serializeData() {
      let params = {
        actionType: ACTION_TYPES.CONNECTED_APPS,
        appId: this.selectedApp,
        widgetId: this.selectedWidget,
      }
      this.$emit('setProperties', { config: params })
    },
  },
  watch: {
    selectedApp: 'setSelectedAppWidgets',
    selectedWidget: 'serializeData',
  },
}
</script>

<style></style>
