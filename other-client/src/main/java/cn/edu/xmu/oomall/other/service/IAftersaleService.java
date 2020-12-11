package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.model.AftersaleDTO;
import cn.edu.xmu.oomall.other.model.CustomerDTO;

/**
 * @author Caixin
 * @date 2020-12-05 21:45
 */
public interface IAftersaleService {

    /**
     * @author 洪晓杰
     * 通过aftersaleId查找orderItemId
     */
    Integer findAftersaleById(Long aftersaleId);


}