package cn.edu.xmu.other.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleRefundDTO {

    private Long id;

    private Long customerId;

    private Byte beDeleted;

    private Long orderId;
}
