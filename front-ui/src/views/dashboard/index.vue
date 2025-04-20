<template>
  <div class="dash-main" style="overflow-y: hidden; padding: 20px">
    <a-row :gutter="16">
      <a-col :span="12">
        <SystemVisitStats />
      </a-col>
      <a-col :span="12">
        <ChartImagesCount />
      </a-col>
    </a-row>
    <DsStatus />
    <APITokenCount/>
    <JobCount/>
    <!--    <a-row :gutter="24">-->
    <!--      <a-col :sm="24" :md="12" :xl="12" :style="{ marginBottom: '24px' }">-->
    <!--        <chart-card :loading="loading" :title="$t('dashboard.analysis.total-sales')" total="￥126,560">-->
    <!--          <a-tooltip :title="$t('dashboard.analysis.introduce')" slot="action">-->
    <!--            <a-icon type="info-circle-o"/>-->
    <!--          </a-tooltip>-->
    <!--          <div>-->
    <!--            <trend flag="up" style="margin-right: 16px;">-->
    <!--              <span slot="term">{{ $t('dashboard.analysis.week') }}</span>-->
    <!--              12%-->
    <!--            </trend>-->
    <!--            <trend flag="down">-->
    <!--              <span slot="term">{{ $t('dashboard.analysis.day') }}</span>-->
    <!--              11%-->
    <!--            </trend>-->
    <!--          </div>-->
    <!--          <template slot="footer">{{ $t('dashboard.analysis.day-sales') }}<span>￥ 234.56</span></template>-->
    <!--        </chart-card>-->
    <!--      </a-col>-->
    <!--      <a-col :sm="24" :md="12" :xl="12" :style="{ marginBottom: '24px' }">-->
    <!--        <chart-card :loading="loading" :title="$t('dashboard.analysis.visits')" :total="8846 | NumberFormat">-->
    <!--          <a-tooltip :title="$t('dashboard.analysis.introduce')" slot="action">-->
    <!--            <a-icon type="info-circle-o"/>-->
    <!--          </a-tooltip>-->
    <!--          <div>-->
    <!--            <mini-area/>-->
    <!--          </div>-->
    <!--          <template slot="footer">{{ $t('dashboard.analysis.day-visits') }}<span> {{ '1234' | NumberFormat }}</span>-->
    <!--          </template>-->
    <!--        </chart-card>-->
    <!--      </a-col>-->
    <!--      <a-col :sm="24" :md="12" :xl="12" :style="{ marginBottom: '24px' }">-->
    <!--        <chart-card :loading="loading" :title="$t('dashboard.analysis.payments')" :total="6560 | NumberFormat">-->
    <!--          <a-tooltip :title="$t('dashboard.analysis.introduce')" slot="action">-->
    <!--            <a-icon type="info-circle-o"/>-->
    <!--          </a-tooltip>-->
    <!--          <div>-->
    <!--            <mini-bar/>-->
    <!--          </div>-->
    <!--          <template slot="footer">{{ $t('dashboard.analysis.conversion-rate') }} <span>60%</span></template>-->
    <!--        </chart-card>-->
    <!--      </a-col>-->
    <!--      <a-col :sm="24" :md="12" :xl="12" :style="{ marginBottom: '24px' }">-->
    <!--        <chart-card :loading="loading" :title="$t('dashboard.analysis.operational-effect')" total="78%">-->
    <!--          <a-tooltip :title="$t('dashboard.analysis.introduce')" slot="action">-->
    <!--            <a-icon type="info-circle-o"/>-->
    <!--          </a-tooltip>-->
    <!--          <div>-->
    <!--            <mini-progress color="rgb(19, 194, 194)" :target="80" :percentage="78" height="8px"/>-->
    <!--          </div>-->
    <!--          <template slot="footer">-->
    <!--            <trend flag="down" style="margin-right: 16px;">-->
    <!--              <span slot="term">{{ $t('dashboard.analysis.week') }}</span>-->
    <!--              12%-->
    <!--            </trend>-->
    <!--            <trend flag="up">-->
    <!--              <span slot="term">{{ $t('dashboard.analysis.day') }}</span>-->
    <!--              80%-->
    <!--            </trend>-->
    <!--          </template>-->
    <!--        </chart-card>-->
    <!--      </a-col>-->
    <!--    </a-row>-->
    <!--    <div class="antd-pro-pages-dashboard-analysis-twoColLayout" :class="!isMobile && 'desktop'">-->
    <!--      <a-row :gutter="24" type="flex">-->
    <!--        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">-->
    <!--          <a-card-->
    <!--            :loading="loading"-->
    <!--            :bordered="false"-->
    <!--            :title="$t('dashboard.analysis.online-top-search')"-->
    <!--            :style="{ height: '100%' }">-->
    <!--            <a-dropdown :trigger="['click']" placement="bottomLeft" slot="extra">-->
    <!--              <a class="ant-dropdown-link" href="#">-->
    <!--                <a-icon type="ellipsis"/>-->
    <!--              </a>-->
    <!--              <a-menu slot="overlay">-->
    <!--                <a-menu-item>-->
    <!--                  <a href="javascript:;">{{ $t('dashboard.analysis.dropdown-option-one') }}</a>-->
    <!--                </a-menu-item>-->
    <!--                <a-menu-item>-->
    <!--                  <a href="javascript:;">{{ $t('dashboard.analysis.dropdown-option-two') }}</a>-->
    <!--                </a-menu-item>-->
    <!--              </a-menu>-->
    <!--            </a-dropdown>-->
    <!--            <div class="ant-table-wrapper">-->
    <!--              <a-table-->
    <!--                row-key="index"-->
    <!--                size="small"-->
    <!--                :columns="searchTableColumns"-->
    <!--                :dataSource="searchData"-->
    <!--                :pagination="{ pageSize: 5 }"-->
    <!--              >-->
    <!--                <span slot="range" slot-scope="text, record">-->
    <!--                  <trend :flag="record.status === 0 ? 'up' : 'down'">-->
    <!--                    {{ text }}%-->
    <!--                  </trend>-->
    <!--                </span>-->
    <!--              </a-table>-->
    <!--            </div>-->
    <!--          </a-card>-->
    <!--        </a-col>-->
    <!--        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">-->
    <!--          <a-card-->
    <!--            class="antd-pro-pages-dashboard-analysis-salesCard"-->
    <!--            :loading="loading"-->
    <!--            :bordered="false"-->
    <!--            :title="$t('dashboard.analysis.the-proportion-of-sales')"-->
    <!--            :style="{ height: '100%' }">-->
    <!--            <div slot="extra" style="height: inherit;">-->
    <!--              &lt;!&ndash; style="bottom: 12px;display: inline-block;" &ndash;&gt;-->
    <!--              <span class="dashboard-analysis-iconGroup">-->
    <!--                <a-dropdown :trigger="['click']" placement="bottomLeft">-->
    <!--                  <a-icon type="ellipsis" class="ant-dropdown-link"/>-->
    <!--                  <a-menu slot="overlay">-->
    <!--                    <a-menu-item>-->
    <!--                      <a href="javascript:;">{{ $t('dashboard.analysis.dropdown-option-one') }}</a>-->
    <!--                    </a-menu-item>-->
    <!--                    <a-menu-item>-->
    <!--                      <a href="javascript:;">{{ $t('dashboard.analysis.dropdown-option-two') }}</a>-->
    <!--                    </a-menu-item>-->
    <!--                  </a-menu>-->
    <!--                </a-dropdown>-->
    <!--              </span>-->
    <!--              <div class="analysis-salesTypeRadio">-->
    <!--                <a-radio-group defaultValue="a">-->
    <!--                  <a-radio-button value="a">{{ $t('dashboard.analysis.channel.all') }}</a-radio-button>-->
    <!--                  <a-radio-button value="b">{{ $t('dashboard.analysis.channel.online') }}</a-radio-button>-->
    <!--                  <a-radio-button value="c">{{ $t('dashboard.analysis.channel.stores') }}</a-radio-button>-->
    <!--                </a-radio-group>-->
    <!--              </div>-->

    <!--            </div>-->
    <!--            <h4>{{ $t('dashboard.analysis.sales') }}</h4>-->
    <!--            <div>-->
    <!--              &lt;!&ndash; style="width: calc(100% - 240px);" &ndash;&gt;-->
    <!--              <div>-->
    <!--                <v-chart :force-fit="true" :height="405" :data="pieData" :scale="pieScale">-->
    <!--                  <v-tooltip :showTitle="false" dataKey="item*percent"/>-->
    <!--                  <v-axis/>-->
    <!--                  &lt;!&ndash; position="right" :offsetX="-140" &ndash;&gt;-->
    <!--                  <v-legend dataKey="item"/>-->
    <!--                  <v-pie position="percent" color="item" :vStyle="pieStyle"/>-->
    <!--                  <v-coord type="theta" :radius="0.75" :innerRadius="0.6"/>-->
    <!--                </v-chart>-->
    <!--              </div>-->

    <!--            </div>-->
    <!--          </a-card>-->
    <!--        </a-col>-->
    <!--      </a-row>-->
    <!--    </div>-->
  </div>
</template>

<script>
import moment from 'moment'
import {
  Bar,
  ChartCard,
  MiniArea,
  MiniBar,
  MiniProgress,
  MiniSmoothArea,
  NumberInfo,
  RankList,
  Trend
} from '@/components'
import { baseMixin } from '@/store/app-mixin'
import APITokenCount from '@/views/dashboard/APITokenCount.vue'
import JobCount from '@/views/dashboard/JobCount.vue'
import ChartImagesCount from '@/views/dashboard/ChartImagesCount.vue'
import SystemVisitStats from '@/views/dashboard/SystemVisitStats.vue'
import DsStatus from '@/views/dashboard/DsStatus.vue'

const barData = []
const barData2 = []
for (let i = 0; i < 12; i += 1) {
  barData.push({
    x: `${i + 1}月`,
    y: Math.floor(Math.random() * 1000) + 200
  })
  barData2.push({
    x: `${i + 1}月`,
    y: Math.floor(Math.random() * 1000) + 200
  })
}

const rankList = []
for (let i = 0; i < 7; i++) {
  rankList.push({
    name: '白鹭岛 ' + (i + 1) + ' 号店',
    total: 1234.56 - i * 100
  })
}

const searchUserData = []
for (let i = 0; i < 7; i++) {
  searchUserData.push({
    x: moment().add(i, 'days').format('YYYY-MM-DD'),
    y: Math.ceil(Math.random() * 10)
  })
}
const searchData = []
for (let i = 0; i < 50; i += 1) {
  searchData.push({
    index: i + 1,
    keyword: `搜索关键词-${i}`,
    count: Math.floor(Math.random() * 1000),
    range: Math.floor(Math.random() * 100),
    status: Math.floor((Math.random() * 10) % 2)
  })
}
const DataSet = require('@antv/data-set')

const sourceData = [
  { item: '家用电器', count: 32.2 },
  { item: '食用酒水', count: 21 },
  { item: '个护健康', count: 17 },
  { item: '服饰箱包', count: 13 },
  { item: '母婴产品', count: 9 },
  { item: '其他', count: 7.8 }
]

const pieScale = [{
  dataKey: 'percent',
  min: 0,
  formatter: '.0%'
}]

const dv = new DataSet.View().source(sourceData)
dv.transform({
  type: 'percent',
  field: 'count',
  dimension: 'item',
  as: 'percent'
})
const pieData = dv.rows

export default {
  name: 'Analysis',
  mixins: [baseMixin],
  components: {
    DsStatus,
    SystemVisitStats,
    ChartImagesCount,
    JobCount,
    APITokenCount,
    ChartCard,
    MiniArea,
    MiniBar,
    MiniProgress,
    RankList,
    Bar,
    Trend,
    NumberInfo,
    MiniSmoothArea
  },
  data () {
    return {
      rankList,

      // 搜索用户数
      searchUserData,
      searchData,
      barData,
      barData2,

      //
      pieScale,
      pieData,
      sourceData,
      pieStyle: {
        stroke: '#fff',
        lineWidth: 1
      }
    }
  },
  computed: {
    searchTableColumns () {
      return [
        {
          dataIndex: 'index',
          title: this.$t('dashboard.analysis.table.rank'),
          width: 90
        },
        {
          dataIndex: 'keyword',
          title: this.$t('dashboard.analysis.table.search-keyword')
        },
        {
          dataIndex: 'count',
          title: this.$t('dashboard.analysis.table.users')
        },
        {
          dataIndex: 'range',
          title: this.$t('dashboard.analysis.table.weekly-range'),
          align: 'right',
          sorter: (a, b) => a.range - b.range,
          scopedSlots: { customRender: 'range' }
        }
      ]
    }
  }
}
</script>

<style lang="less" scoped>

.antd-pro-pages-dashboard-analysis-twoColLayout {
  position: relative;
  display: flex;
  display: block;
  flex-flow: row wrap;
}

.antd-pro-pages-dashboard-analysis-salesCard {
  height: calc(100% - 24px);

  :deep(.ant-card-head) {
    position: relative;
  }
}

.dashboard-analysis-iconGroup {
  i {
    margin-left: 16px;
    color: rgba(0, 0, 0, .45);
    cursor: pointer;
    transition: color .32s;
    color: black;
  }
}

.analysis-salesTypeRadio {
  position: absolute;
  right: 54px;
  bottom: 12px;
}

::v-deep .ant-card-extra {
  padding: 0;
}

::v-deep .ant-menu-horizontal {
  border: none;
}
</style>
