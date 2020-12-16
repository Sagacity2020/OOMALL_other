package cn.edu.xmu.other.service;

import cn.edu.xmu.other.dto.CustomerDTO;

public interface CustomerServiceInterface {

   public CustomerDTO getCustomerInfoById(Long id);
   public Boolean getPointFromRefund(Long userId,Integer point);
   public Boolean payWithPoint(Long userId,Integer amount);

}
