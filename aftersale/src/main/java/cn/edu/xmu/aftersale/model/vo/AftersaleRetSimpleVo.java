package cn.edu.xmu.aftersale.model.vo;

import lombok.Data;

@Data
public class AftersaleRetSimpleVo {
    private Long id;

    private Long orderId;

    private Long orderItemId;

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
