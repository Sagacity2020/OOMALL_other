package cn.edu.xmu.share.model.vo;


import cn.edu.xmu.share.model.bo.Sku;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BeShareRetVo {
    private Long id;
    private Sku sku;
    private Long sharerId;
    private Long shareId;
    private Long customerId;
    private Long orderId;
    private Integer rebate;
    private LocalDateTime gmtCreate;
}
