package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.CustomerDTO;

public interface ICustomerService {

   public CustomerDTO getCustomerInfoById(Long id);
   public Boolean getPointFromRefund(Long userId,Integer point);

}
