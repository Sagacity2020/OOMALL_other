package cn.edu.xmu.other.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AftersaleRetVo {

    private Long id;

    private Long orderId;

    private String orderSn;

    private Long orderItemId;

    private Long skuId;

    private String skuName;

    private Long customerId;

    private Long shopId;

    private String serviceSn;

    private Byte type;

    private String reason;

    private String conclusion;

    private Long refund;

    private Integer quantity;

    private Long regionId;

    private String detail;

    private String consignee;

    private String mobile;

    private String customerLogSn;

    private String shopLogSn;

    private Byte state;
}
