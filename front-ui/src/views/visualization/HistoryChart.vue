<template>
  <div class="main">
    <a-card title="历史图片" class="custom-card">
      <template #extra>
        <a-upload
          :showUploadList="false"
          :beforeUpload="handleUpload"
          accept="image/*"
        >
          <a-button type="primary">上传图片</a-button>
        </a-upload>
      </template>
      <!-- 图片列表 -->
      <a-skeleton active v-if="loading" />
      <div v-else class="image-grid" ref="viewerContainer">
        <div
          v-for="image in images"
          :key="image.id"
          class="image-item"
          @mouseenter="visibleActions = image.id"
          @mouseleave="visibleActions = null">
          <img :src="image.image" alt="历史图片" />
          <div class="info">
            <p class="desc">{{ image.description || '无描述' }}</p>
            <p class="time">🕒 {{ formatTime(image.createdTime) }}</p>
            <div style="text-align: end" class="actions" v-if="showActions(image.id)">
              <a-button style="margin-right: 10px" size="small" @click="editImage(image)">编辑</a-button>
              <a-button size="small" type="danger" @click="deleteImage(image.id)">删除</a-button>
            </div>
          </div>
        </div>
      </div>
      <a-empty v-if="!loading&&images.length === 0" description="暂无历史图片" style="text-align: center; margin-top: 20px;" />
      <div class="pagination" style="margin-top: 10px;text-align: end;">
        <a-pagination
          show-size-changer
          v-model="current"
          @showSizeChange="(page, pageSize)=>getVisualization(page, pageSize)"
          @change="(page, pageSize)=>getVisualization(page, pageSize)"
          :total="total"
          show-less-items />
      </div>
    </a-card>
    <a-modal
      :visible="saveModalVisible"
      @cancel="cancelChart()"
      title="请输入图片描述"
      ok-text="保存"
      cancel-text="取消"
      @ok="saveChart()"
    >
      <a-input
        v-model="chartDescription"
        placeholder="请输入该图表的描述"
      />
    </a-modal>
  </div>
</template>

<script>
import { deleteVisualization, getVisualization, saveVisualization, updateVisualization } from '@/api/visualization'
import Viewer from 'viewerjs'

export default {
  name: 'HistoryChart',
  data () {
    return {
      chartDescription: undefined, // 图片描述
      imageId: null, // 图片ID
      loading: true,
      images: [], // 历史图片数据
      viewer: null,
      visibleActions: null,
      saveModalVisible: false, // 控制保存图片描述的弹窗
      current: 1, // 分页当前页码
      total: 0 // 分页总数
    }
  },
  mounted () {
    this.getVisualization()
  },
  methods: {
    cancelChart () {
      this.saveModalVisible = false
      this.imageId = null
      this.chartDescription = undefined
    },
    deleteImage (imageId) {
      this.$confirm({
        title: '删除确认',
        content: '确定要删除这张图片吗？',
        onOk: () => {
          // 调用删除接口
          this.loading = true
          deleteVisualization({ id: imageId })
            .then(() => {
              this.$message.success('删除成功')
              this.getVisualization()
            })
            .catch(() => {
              this.$message.error('删除失败')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    editImage (image) {
      this.chartDescription = image.description || undefined
      this.imageId = image.id
      this.saveModalVisible = true
    },
    saveChart () {
      if (!this.chartDescription) {
        this.$message.error('请输入图片描述')
        return
      }
      updateVisualization({
        imageId: this.imageId,
        description: this.chartDescription
      }).then(res => {
        if (res.status === '0') {
          this.$message.success('编辑成功')
        } else {
          this.$message.error('编辑失败')
        }
        this.saveModalVisible = false
        this.getVisualization()
      })
    },
    handleUpload (file) {
      const reader = new FileReader()
      reader.onload = () => {
        const base64 = reader.result

        saveVisualization({
          userId: this.$store.getters.userInfo.userId,
          image: base64,
          description: this.chartDescription
        }).then(res => {
          if (res.result) {
            this.$message.success('保存成功')
          } else {
            this.$message.error('保存失败')
          }
          this.getVisualization()
        })
      }
      reader.readAsDataURL(file)
      return false // 阻止默认自动上传行为
    },
    getVisualization (page, size) {
      this.current = page || this.current
      this.loading = true
      getVisualization({ userId: this.$store.getters.userInfo.userId, pageNum: this.current, pageSize: size })
        .then((response) => {
          this.images = response.result.list
          this.total = response.result.total
        })
        .catch((error) => {
          console.error('获取历史图表失败:', error)
        })
        .finally(() => {
          this.loading = false
          this.$nextTick(() => {
            this.initViewer()
          })
        })
    },
    initViewer () {
      if (this.viewer) {
        this.viewer.destroy()
        this.viewer = null
      }
      const container = this.$refs.viewerContainer
      if (container && !this.viewer) {
        this.viewer = new Viewer(container, {
          toolbar: true,
          title: true,
          navbar: false,
          tooltip: true
        })
      }
    },
    formatTime (timeStr) {
      const date = new Date(timeStr)
      return date.toLocaleString()
    }
  },
  beforeDestroy () {
    if (this.viewer) {
      this.viewer.destroy()
      this.viewer = null
    }
  },
  computed: {
    showActions () {
      return (imageId) => {
        return this.visibleActions === imageId
      }
    }
  }
}
</script>

<style scoped>
@import 'viewerjs/dist/viewer.css';

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
  padding: 10px 0;
}

.image-item {
  background-color: #fafafa;
  border-radius: 10px;
  overflow: hidden;
  transition: box-shadow 0.3s, transform 0.3s;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  cursor: pointer;
}

.image-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.image-item img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-bottom: 1px solid #eee;
}

.info {
  padding: 10px;
  text-align: left;
  min-height: 110px;
}

.desc {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.time {
  font-size: 12px;
  color: #999;
}
</style>
