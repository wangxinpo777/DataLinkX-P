<template>
  <a-card :loading="loading" :title="'数据源列表'" style="margin-bottom: 24px;border-radius: 8px;">
    <a-table
      size="small"
      :rowKey="(row, index) => {return index;}"
      :columns="columns"
      :dataSource="dsStatus"
      :transform-cell-text="({ text, column, record, index }) => text||'--'"
      @change="handleTableChange"
      :pagination="{ pageSize: pageSize,current: pageNo, total: dataTotal,showTotal: total => `共 ${total} 条`,
                     showSizeChanger: true, pageSizeOptions: ['5', '10', '20', '50'] }"
    />
  </a-card>
</template>
<script>
import { pageQuery } from '@/api/datasource/datasource'
import ChartCard from '@/components/Charts/ChartCard.vue'
import { dsTypeList } from '@/views/datasource/const'

export default {
  name: 'DsStatus',
  components: { ChartCard },
  data () {
    return {
      pageSize: 5,
      pageNo: 1,
      loading: false,
      dsStatus: [],
      dataTotal: 0,
      columns: [
        {
          title: '',
          customRender: (_, __, index) => index + 1, // 行号从1开始
          width: 60
        },
        {
          title: '数据源名称',
          dataIndex: 'name'
        },
        {
          title: '数据源类型',
          dataIndex: 'type',
          customRender: (text) => {
            // 根据 dsTypeKey 获取对应的图标和名称
            const ds = dsTypeList.find(item => item.dsTypeKey === text)
            return ds ? (
              <div style="display: flex; align-items: center;">
                <img src={ds.img} alt={ds.label} style="width: 20px; height: 20px; margin-right: 8px;" />
                {ds.label}
              </div>
            ) : '未知类型'
          }
        },
        {
          title: '地址',
          customRender: (record) => `${record.host}:${record.port}`
        },
        {
          title: '数据库',
          dataIndex: 'database'
        },
        {
          title: '用户名',
          dataIndex: 'username',
          key: 'username'
        },
        {
          title: '连接状态',
          dataIndex: 'status',
          customRender: (text) => {
            const statusMap = {
              0: { label: '未连接', color: 'cyan' },
              1: { label: '成功', color: 'green' },
              2: { label: '失败', color: 'red' }
            }
            const { label, color } = statusMap[text] || { label: '未知', color: 'cyan' }
            return <a-tag color={color}>{label}</a-tag>
          }
        },
        {
          title: '创建时间',
          dataIndex: 'ctime',
          key: 'ctime'
        },
        {
          title: '更新时间',
          dataIndex: 'utime',
          key: 'utime'
        }]
    }
  },
  methods: {
    initData () {
      this.loading = true
      pageQuery({
        pageNo: this.pageNo,
        pageSize: this.pageSize,
        type: '',
        name: ''
      }).then((res) => {
        this.loading = false
        this.dataTotal = res.result.total
        this.dsStatus = res.result.data
      })
        .catch((error) => {
          console.error(error)
          this.loading = false
        })
    },
    handleTableChange (pagination) {
      this.pageNo = pagination.current
      this.pageSize = pagination.pageSize
      this.initData()
    }
  },
  mounted () {
    this.initData()
  }
}
</script>

<style scoped lang="less">

</style>
