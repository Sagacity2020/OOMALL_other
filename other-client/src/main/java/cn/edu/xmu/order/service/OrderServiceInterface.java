package cn.edu.xmu.order.service;

import cn.edu.xmu.order.dto.OrderAftersaleDTO;
import cn.edu.xmu.other.dto.AftersaleDTO;

public interface OrderServiceInterface {

    public OrderAftersaleDTO getAftersaleInfo(Long orderItemId);

    public Boolean createAftersaleOrder(AftersaleDTO aftersaleDTO);
}
