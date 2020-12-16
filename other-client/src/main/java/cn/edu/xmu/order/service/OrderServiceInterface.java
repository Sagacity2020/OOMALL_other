package cn.edu.xmu.order.service;

import cn.edu.xmu.order.dto.OrderAftersaleDTO;
import cn.edu.xmu.other.dto.AftersaleDTO;

import java.util.List;

public interface OrderServiceInterface {

    public OrderAftersaleDTO getAftersaleInfo(Long orderItemId);

    public Long createAftersaleOrder(AftersaleDTO aftersaleDTO);
}
