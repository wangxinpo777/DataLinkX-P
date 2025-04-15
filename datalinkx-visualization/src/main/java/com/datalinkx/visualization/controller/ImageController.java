package com.datalinkx.visualization.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.common.utils.ObjectUtils;
import com.datalinkx.visualization.bean.domain.UserChartImageBean;
import com.datalinkx.visualization.bean.dto.ImageConfig;
import com.datalinkx.visualization.bean.dto.UserChartImageProjection;
import com.datalinkx.visualization.bean.model.SaveChartImageRequest;
import com.datalinkx.visualization.bean.vo.ChartImageVo;
import com.datalinkx.visualization.repository.UserChartImageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private UserChartImageRepository userChartImageRepository;

    @PostMapping("/save-chart-image")
    public WebResult<UserChartImageBean> saveChartImage(
            @RequestBody SaveChartImageRequest request) {
        try {
            // 去掉 Base64 前缀
            String base64Data = request.getImage().split(",")[1];
            // 解码 Base64 数据
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            if (request.getImageId() != null) {
                Optional<UserChartImageBean> byId = userChartImageRepository.findById(request.getImageId());
                if (byId.isPresent()) {
                    UserChartImageBean userChartImage = byId.get();
                    BeanUtils.copyProperties(request, userChartImage, ObjectUtils.getNullPropertyNames(request));
                    userChartImage.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                    userChartImage.setImage(imageBytes);
                    return WebResult.of(userChartImageRepository.save(userChartImage));
                } else {
                    throw new RuntimeException("图片不存在");
                }
            } else {
                UserChartImageBean userChartImage = UserChartImageBean.builder()
                        .userId(request.getUserId())
                        .image(imageBytes)
                        .chartJsonData(request.getChartJsonData())
                        .chartConfig(request.getChartConfig())
                        .type(request.getType() == null ? 0 : request.getType())
                        .description(request.getDescription())
                        .createdTime(new Timestamp(System.currentTimeMillis()))
                        .updatedTime(new Timestamp(System.currentTimeMillis()))
                        .isDel(0) // 默认不删除
                        .build();
                return WebResult.of(userChartImageRepository.save(userChartImage));
            }
        } catch (Exception e) {
            throw new RuntimeException("保存图片失败", e);
        }
    }

    @GetMapping("/get-chart-image")
    public WebResult<HashMap<String, Object>> getChartImage(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false)  String keyword) {
        try {
            PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
            if(StringUtils.isEmpty(keyword)){
                keyword = null;
            }
            Page<UserChartImageProjection> jobBeans = userChartImageRepository.pageQuery(pageRequest, keyword);

            List<ChartImageVo> ChartImageVoList = jobBeans.getContent().stream().map(userChartImageBean -> {
                // 将图片数据转换为 Base64 字符串
                String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(userChartImageBean.getImage());
                return ChartImageVo.builder()
                        .id(userChartImageBean.getId())
                        .userId(userChartImageBean.getUserId())
                        .userName(userChartImageBean.getUserName())
                        .image(base64Image)
                        .type(userChartImageBean.getType())
                        .description(userChartImageBean.getDescription())
                        .createdTime(userChartImageBean.getCreatedTime())
                        .updatedTime(userChartImageBean.getUpdatedTime())
                        .build();
            }).collect(Collectors.toList());
            return WebResult.of(new HashMap<String, Object>() {{
                put("total", jobBeans.getTotalElements());
                put("list", ChartImageVoList);
            }});
        } catch (Exception e) {
            throw new RuntimeException("获取图片失败", e);
        }
    }

    @DeleteMapping("/delete-chart-image")
    public void deleteChartImage(@RequestParam Integer id) {
        try {
            userChartImageRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("删除图片失败", e);
        }
    }

    @GetMapping("/get-chart-image-config")
    public WebResult<HashMap<String, Object>> getChartImageConfig(@RequestParam Integer imageId) {
        try {
            ImageConfig imageConfig = userChartImageRepository.findImageConfigById(imageId);
            HashMap<String, Object> result = new HashMap<>();
            result.put("chartConfig", imageConfig.getChartConfig());
            result.put("description", imageConfig.getDescription());
            result.put("chartJsonData", imageConfig.getChartJsonData());
            result.put("userId", imageConfig.getUserId());
            result.put("imageId", imageConfig.getId());
            return WebResult.of(result);
        } catch (Exception e) {
            throw new RuntimeException("获取图片配置失败", e);
        }
    }
}