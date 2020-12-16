package cn.edu.xmu.share.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShareActivityRetVo {
    private Long id;
    private Long shopId;
    private Long goodsSpuId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String strategy;
    private Byte state;

}
