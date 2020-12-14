package cn.edu.xmu.footprint.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FootprintVo {
    private Long id;
    private Long goodsSkuId;
    private Long customerId;
    private LocalDateTime gmtCreate;
}
