package cn.edu.xmu.other.service;


import cn.edu.xmu.other.dto.AftersaleRefundDTO;
import cn.edu.xmu.other.dto.ReasonDTO;

public interface AftersaleServiceInterface {
    Boolean judgeNotBeingAfterSale(Long orderItemId);

    AftersaleRefundDTO getAftersaleById(Long aftersaleId);

    ReasonDTO CreateAftersalePaymentJudge(Long userId,Long aftersaleId);
}