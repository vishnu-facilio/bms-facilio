<script>
import JobPlanTasks from 'newapp/components/tasks-creation/TasksLayout'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: JobPlanTasks,
  created() {
    let { value, isEdit } = this
    this.loadWoForms()
    if (!isEmpty(value) && isEdit) {
      this.prefillJobPlanTasks()
    }
    if (isEmpty(value)) {
      this.addNewSection()
    }
  },
}
</script>
<style lang="scss" scoped>
.task-container {
  height: 100%;
  #task-list-container {
    padding-bottom: 70px;
  }
  .text-center {
    padding: 0px;
  }
  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid transparent;
    min-height: 36px;
    .btn-label {
      font-size: 12px;
      font-weight: 500;
      color: #39b2c2;
      letter-spacing: 0.5px;
    }
    img {
      width: 9px;
    }
  }
  .task-add-btn:hover,
  .task-add-btn.empty {
    background-color: #f7feff;
    border: 1px solid #39b2c2;
  }
  .task-empty-icon {
    width: 49px;
    height: 49px;
    vertical-align: middle;
  }
  .task-section {
    margin-bottom: 20px;
    cursor: pointer;
  }
  .fc-secondary-btn {
    font-size: 12px;
    color: #3ab2c2;
    background-color: #ffffff;
    height: 42px;
    line-height: 1;
    display: inline-block;
    border: none;
    text-transform: uppercase;
    letter-spacing: 0.7px;
    font-weight: bold;
    cursor: pointer;
    border-radius: 3px;
    border: 1px solid #3ab2c2;
    -webkit-transition: all 0.3s;
    -moz-transition: all 0.3s;
    transition: all 0.3s;
    &:hover {
      background-color: #3ab2c2;
      color: #ffffff;
    }
    &:focus {
      background: rgb(58 178 194 / 10%);
      color: #3ab2c2;
    }
    &:active {
      color: #3ab2c2;
      background-color: #ffffff;
      border: solid 1px #3ab2c2;
    }
  }
  .jp-section-header {
    height: 65px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .section-left {
      display: flex;
      .section-name {
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        color: #324056;
      }
    }
    .section-customization {
      display: flex;
      .section-setting i {
        font-size: 16px;
      }
    }
  }
  .task-row {
    height: 65px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .task-left {
      display: flex;
      .task-name {
        font-size: 12px;
        font-weight: 400;
      }
    }
    .delete-icon {
      color: #ec7c7c;
    }
    &:hover {
      background-color: #fafeff;
    }
  }
  .add-task-btn {
    margin-top: 35px;
    margin-bottom: 30px;
  }
  .add-section {
    display: flex;
    flex-direction: column;
    .add-section-btn {
      position: relative;
      margin-top: -5px;
      align-self: center;
    }
  }
  .seperator-line {
    position: absolute;
    width: 100%;
    margin: 15px 0px;
    border-bottom: 0.5px solid #d8d8d8;
  }
}
</style>

<style lang="scss">
.task-customization {
  .el-input__suffix {
    margin-top: 7px;
  }
}
.section-name {
  .el-input__inner {
    color: black;
    font-weight: 500;
  }
}
</style>
