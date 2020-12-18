package cn.edu.xmu.advertisement.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdvertisementRetVo {
    private Long id;

    private Long segId;

    private String link;

    private String content;

    private String imagePath;

    private Integer state;

    private String weight;

    private LocalDate beginDate;

    private LocalDate endDate;

    private Boolean repeat;

    private Boolean beDefault;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
