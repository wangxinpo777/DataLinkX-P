package com.datalinkx.visualization.controller;

import com.datalinkx.common.result.WebResult;
import com.datalinkx.visualization.bean.domain.UserChartImageBean;
import com.datalinkx.visualization.bean.model.SaveChartImageRequest;
import com.datalinkx.visualization.bean.vo.ChartImageVo;
import com.datalinkx.visualization.repository.UserChartImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
            UserChartImageBean userChartImage = UserChartImageBean.builder()
                    .userId(request.getUserId())
                    .image(imageBytes)
                    .description(request.getDescription())
                    .createdTime(new Timestamp(System.currentTimeMillis()))
                    .isDel(0) // 默认不删除
                    .build();
            return WebResult.of(userChartImageRepository.save(userChartImage));
        } catch (Exception e) {
            throw new RuntimeException("保存图片失败", e);
        }
    }

    @GetMapping("/get-chart-image")
    public WebResult<HashMap<String, Object>> getChartImage(@RequestParam Integer userId, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
            Page<UserChartImageBean> jobBeans = userChartImageRepository.pageQuery(pageRequest, userId);

            List<ChartImageVo> ChartImageVoList = jobBeans.getContent().stream().map(userChartImageBean -> {
                // 将图片数据转换为 Base64 字符串
                String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(userChartImageBean.getImage());
                return ChartImageVo.builder()
                        .id(userChartImageBean.getId())
                        .userId(userChartImageBean.getUserId())
                        .image(base64Image)
                        .description(userChartImageBean.getDescription())
                        .createdTime(userChartImageBean.getCreatedTime())
                        .build();
            }).sorted((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime())).collect(Collectors.toList());
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

    @PutMapping("/update-chart-image")
    public void updateChartImage(@RequestBody SaveChartImageRequest request) {
        userChartImageRepository.findById(request.getImageId()).ifPresent(userChartImage -> {
            userChartImage.setDescription(request.getDescription());
            userChartImage.setCreatedTime(new Timestamp(System.currentTimeMillis()));
            userChartImageRepository.save(userChartImage);
        });
    }
}