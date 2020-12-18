package cn.edu.xmu.other.service;


import cn.edu.xmu.other.dto.AftersaleRefundDTO;

public interface AftersaleServiceInterface {

    Boolean checkIsAftersale(Long orderItemId);

    AftersaleRefundDTO getAftersaleById(Long aftersaleId);
}