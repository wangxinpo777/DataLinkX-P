<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="任务id">
              <a-input v-model="queryParam.jobId" placeholder="任务id"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-button @click="() => {this.queryData()}" type="primary">查询</a-button>
            <a-button @click="() => {queryParam = {};this.init()}" style="margin-left: 8px">重置</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      :columns="columns"
      :dataSource="tableData"
      :pagination="pagination"
      :rowKey="record => record.id"
      @change="handleTableChange"
    >
      <template v-slot:error_analysis="error_analysis">
        <a v-if="error_analysis" @click="errorLogData=error_analysis;errorLogVisible=true">
          查看
        </a>
        <span v-else>--</span>
      </template>
      <span slot="status" slot-scope="text, record" style="display: flex;align-items: center;">
        <div :style="getTableCss(record.status)"></div>
        <span>{{ record.status ? '失败' : '成功' }}</span>
      </span>
    </a-table>
    <a-modal
      title="错误日志分析"
      v-if="errorLogVisible"
      :visible="errorLogVisible"
      @cancel="() => {errorLogVisible = false}"
      :width="800"
      :dialog-style="{ top: '50px' }"
      :footer="null"
    >
      <div v-html="renderErrorLog" style="overflow-y: auto;max-height: 70vh"></div>
      <div style="text-align: end">
        <a-button type="primary" @click="errorLogVisible = false" style="margin-top: 16px;">确定</a-button>
      </div>
    </a-modal>
  </a-card>
</template>

<script>
import { pageQuery } from '@/api/job/joblog'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
// 0:CREATE|1:SYNCING|2:SYNC_FINISH|3:SYNC_ERROR|4:QUEUING
export default {
  name: 'ContainerBottom',
  components: {
    // JobSaveOrUpdate
  },
  data () {
    return {
      errorLogVisible: false,
      errorLogData: '',
      loading: false,
      columns: [
        {
          title: 'job_id',
          width: '10%',
          dataIndex: 'job_id'
        },
        {
          title: '执行日志',
          width: '10%',
          dataIndex: 'error_msg',
          ellipsis: true,
          render: value =>
            <Tooltip title={value}>
              <div className="ellipsis" style={{ float: 'left', maxWidth: '100%' }}>
                {value && value.substring(0, 10)}
              </div>
            </Tooltip>
        },
        {
          title: '开始时间',
          width: '10%',
          dataIndex: 'start_time',
          sorter: true
        },
        {
          title: '执行耗时s',
          width: '10%',
          dataIndex: 'cost_time',
          sorter: true
        },
        {
          title: '任务状态',
          width: '10%',
          dataIndex: 'status',
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '错误日志分析',
          width: '10%',
          dataIndex: 'error_analysis',
          ellipsis: true,
          scopedSlots: { customRender: 'error_analysis' }
        },
        {
          title: '新增条数',
          width: '10%',
          dataIndex: 'append_count',
          sorter: true
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
        page_size: 10,
        page_no: 1
      },
      queryParam: {
        jobId: ''
      },
      markdownRender: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight: (str, lang) => {
          if (lang && hljs.getLanguage(lang)) {
            try {
              return `<div class="code-wrapper"><pre class="hljs"><code>${hljs.highlight(lang, str, true).value}</code></pre></div>`
            } catch (err) {
              console.error('代码高亮失败:', err) // 可选：打印错误
            }
          }
          return `<div class="code-wrapper"><pre class="hljs"><code>` + this.markdownRender.utils.escapeHtml(str) + `</code></pre></div>`
        }
      })
    }
  },
  methods: {
    init () {
      this.loading = true
      pageQuery({
        ...this.pages,
        ...this.queryParam
      }).then(res => {
        this.tableData = res.result.data
        this.pagination.total = +res.result.total
        this.loading = false
      }).finally(() => {
        this.loading = false
      })
    },
    getTableCss (status) {
      return `width:12px;height:12px;border-radius:50%;margin-right:4px;background-color: ${status ? '#f00' : '#0f0'};`
    },
    handleTableChange (pagination, filters, sorter) {
      this.pagination = pagination
      this.pages.page_size = pagination.pageSize
      this.pages.page_no = pagination.current
      this.init()
    },
    handleOk () {
      this.init()
    },
    queryData () {
      this.pages.page_no = 1
      this.init()
    }
  },
  created () {
    const jobId = this.$route.query.jobId // 从 query 参数中获取 jobId
    if (jobId) {
      this.queryParam.jobId = jobId // 将 jobId 赋值给 queryParam
    }
    this.init()
  },
  computed: {
    renderErrorLog () {
      return this.markdownRender.render(this.errorLogData)
    }
  }
}
</script>

<style scoped lang="less">
@import '~highlight.js/styles/vs.css';
::v-deep pre{
  background-color: #f5f5f5;
  margin-bottom: 0;
}
</style>
