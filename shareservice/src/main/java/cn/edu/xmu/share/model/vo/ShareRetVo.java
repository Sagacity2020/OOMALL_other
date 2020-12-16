package cn.edu.xmu.share.model.vo;

import cn.edu.xmu.share.model.bo.Sku;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShareRetVo {
    private Long id;
    private Long sharerId;
    private Sku sku;
    private Integer quantity;
    private LocalDateTime gmtCreate;
}

