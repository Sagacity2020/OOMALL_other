package cn.edu.xmu.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAftersaleDTO implements Serializable {

    Long skuId;

    String skuName;

    Long orderId;

    String orderSn;

    Long shopId;

    Long customerId;

    Long actualPaidPrice;
}
