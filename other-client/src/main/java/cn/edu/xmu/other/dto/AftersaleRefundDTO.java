package cn.edu.xmu.other.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleRefundDTO implements Serializable {

    private Long id;

    private Long customerId;

    private Byte beDeleted;

    private Long orderId;

    private Long shopId;
}
