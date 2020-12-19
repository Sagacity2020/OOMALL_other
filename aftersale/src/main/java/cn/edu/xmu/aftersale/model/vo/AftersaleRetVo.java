package cn.edu.xmu.aftersale.model.vo;

import lombok.Data;

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

    private Integer type;

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
