package cn.edu.xmu.order.service;

import cn.edu.xmu.order.dto.OrderAftersaleDTO;

public interface OrderServiceInterface {

    public OrderAftersaleDTO getAftersaleInfo(Long orderItemId);
}
