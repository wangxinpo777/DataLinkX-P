<template>
  <a-card :bordered="false">
    <template v-if="!showJobCompute">
      <div class="table-operator">
        <a-button @click="newCompute" icon="plus" type="primary">新建</a-button>
      </div>
      <a-table
        :columns="columns"
        :dataSource="tableData"
        :loading="loading"
        :pagination="pagination"
        :rowKey="record => record.job_id"
        @change="handleTableChange"
      >
      </a-table>
    </template>
    <div v-if="showJobCompute" class="job-compute-overlay">
      <div class="table-operator">
        <a-button @click="showJobCompute=!showJobCompute" icon="plus" type="primary">返回</a-button>
      </div>
      <job-compute ref="JobCompute" :jobId="this.currentJobId"></job-compute>
    </div>
  </a-card>
</template>

<script>
import { delObj, exec, pageQuery, stop } from '@/api/job/job'
import JobCompute from '../job/JobCompute.vue'
// 0:CREATE|1:SYNCING|2:SYNC_FINISH|3:SYNC_ERROR|4:QUEUING
  const StatusType = [
    {
      label: '新建',
      value: 0
    },
    {
      label: '同步中',
      value: 1
    },
    {
      label: '同步完成',
      value: 2
    },
    {
      label: '同步失败',
      value: 3
    },
    {
      label: '同步停止',
      value: 4
    }
  ]
  export default {
    name: 'JobList',
    components: {
      JobCompute
    },
    data () {
      return {
        loading: false,
        currentJobId: '',
        showJobCompute: false,
        columns: [
          {
            title: 'job_id',
            width: '10%',
            dataIndex: 'job_id'
          },
          {
            title: '任务名称',
            width: '10%',
            dataIndex: 'job_name'
          },
          {
            title: '来源表',
            width: '10%',
            dataIndex: 'from_tb_name'
          },
          {
            title: '目标表',
            width: '10%',
            dataIndex: 'to_tb_name',
            sorter: true
          },
          {
            title: '任务状态',
            width: '10%',
            dataIndex: 'status',
            customRender: (text) => {
              return (
                <div>
                {StatusType.map(item => {
                    if (item.value === text) {
                      return <span>{item.label}</span>
                    }
                  })}
                </div>
            )
            }
          },
          {
            title: '查看日志',
            width: '10%',
            customRender: (record) => {
              return (
                <div>
                  <a href="javascript:;" onClick={() => this.goToJobLog(record.job_id)}>查看</a>
                </div>
              )
            }
          },
          {
            title: '任务上次执行时间',
            dataIndex: 'start_time',
            width: '10%',
            sorter: true
          },
          {
            title: '操作',
            width: '15%',
            customRender: (record) => {
              return (
                <div>
                <a onClick={(e) => this.edit(record)}>修改</a>
              <a-divider type="vertical" />
                <a-popconfirm title="是否删除" onConfirm={() => this.delete(record)} okText="是" cancelText="否">
                <a-icon slot="icon" type="question-circle-o" style="color: red" />
                <a href="javascript:;" style="color: red">删除</a>
                </a-popconfirm>
                <a-divider type="vertical" />
                <a href="javascript:;"onClick={(e) => this.execJob(record)}>手动触发</a>
              <a-divider type="vertical" />
                <a href="javascript:;"onClick={(e) => this.stopJob(record)}>停止同步</a>
              </div>
            )
            }
          }
        ],
        tableData: [],
        pagination: {
          pageSize: 10,
          current: 1,
          total: 0,
          showSizeChanger: true
        },
        pages: {
          size: 10,
          current: 1
        },
        queryParam: {
          'type': 2
        },
        source: null,
        refreshIntervalId: null // 用于存储定时器的 ID
      }
    },
    provide () {
      return {
        closeDraw: this.closeDraw
      }
    },
    methods: {
      init () {
        this.loading = true
        pageQuery({
          ...this.queryParam,
          ...this.pages
        }).then(res => {
          this.tableData = res.result.data
          this.pagination.total = +res.result.total
          this.loading = false
        }).finally(() => {
          this.loading = false
        })
      },
      newCompute () {
        this.showJobCompute = true
      },
      handleTableChange (pagination) {
        this.pagination = pagination
        this.pages.size = pagination.pageSize
        this.pages.current = pagination.current
        this.init()
      },
      edit (record) {
        console.log(record.job_id)
        this.showJobCompute = true
        setTimeout(() => {
          this.$refs.JobCompute.edit(record.job_id)
        }, 10)
      },
      closeDraw () {
        this.showJobCompute = false
        this.init()
      },
      delete (record) {
        this.loading = true
        delObj(record.job_id).then(res => {
          if (res.status === '0') {
            this.$message.info('删除成功')
            this.init()
          } else {
            this.$message.error(res.errstr)
          }
        }).finally(() => {
          this.loading = false
        })
      },
      execJob (record) {
        exec(record.job_id).then(res => {
          if (res.status === '0') {
            this.$message.info('触发成功')
            this.init()
          } else {
            this.$message.error(res.errstr)
          }
        }).finally(() => {
          this.loading = false
        })
      },
      stopJob (record) {
        stop(record.job_id).then(res => {
          if (res.status === '0') {
            this.$message.info('停止成功')
            this.init()
          } else {
            this.$message.error(res.errstr)
          }
        }).finally(() => {
          this.loading = false
        })
      },
      handleOk () {
        this.init()
      },
      queryData () {
        this.pages.current = 1
        this.init()
      },
      goToJobLog (jobId) {
        // 使用 Vue Router 的编程式导航跳转到 jobLog 页面
        this.$router.push({
          name: 'JobLog', // 路由名称
          query: { jobId } // 通过 query 参数传递 jobId
          // 或者使用 params（如果路由配置了动态参数）
          // params: { jobId }
        })
      },
      startRefresh () {
        this.refreshIntervalId = setInterval(() => {
          this.init()
        }, 60000) // 每 10 秒执行一次 init 方法
      },
      stopRefresh () {
        if (this.refreshIntervalId) {
          clearInterval(this.refreshIntervalId)
          this.refreshIntervalId = null
        }
      }
    },
    beforeDestroy () {
      this.eventSource && this.eventSource.close()
      this.stopRefresh() // 组件销毁时停止定时器
    },
    created () {
      this.init()
      this.startRefresh() // 组件创建时启动定时器
    }
  }
</script>

<style scoped>
  .job-compute-overlay {
    //position: fixed;
    //top: 0;
    //left: 0;
    //width: 100%;
    //height: 100%;
    //background: rgba(255, 255, 255, 1);
    //z-index: 19;
  }
</style>
