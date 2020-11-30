package cn.edu.xmu.other.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dao.AddressDao;
import cn.edu.xmu.other.model.bo.Address;
import cn.edu.xmu.other.model.vo.AddressVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressDao addressDao;

    public ReturnObject<Address> insertAddress(Address address)
    {
        /**
        ReturnObject<Address> returnObject=addressDao.insertAddress(address);
        ReturnObject<Object> retAddress;
        if(returnObject.getCode().equals(ResponseCode.OK))
            retAddress=new ReturnObject<>(returnObject.getData());
        else retAddress=new ReturnObject<>(returnObject.getCode(),returnObject.getErrmsg());
       */
        return addressDao.insertAddress(address);
    }



}
