package cn.edu.xmu.address.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.address.dao.AddressDao;
import cn.edu.xmu.address.model.bo.Address;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    private Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressDao addressDao;

    /**
     * @author zrh
     * @Created at 11/30 23:51
     * @modified at 12/3 11:43
     * @param address
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject insertAddress(Address address)
    {
        ReturnObject<Address> returnObject=addressDao.insertAddress(address);
        return returnObject;

    }

    /**
     * @author zrh
     * @Created at 12/1 0:22
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> selectAllAddreses(Long userId, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject= addressDao.selectAllAddress(userId,page,pageSize);
        return returnObject;
    }


    /**
     * @Created at 12/2 14:07
     * @author zrh
     * @param id
     * @return
     */
    @Transactional
    public ReturnObject setDefaultAddress(Long userId,Long id) {
        return addressDao.cancelDefaultAddress(userId,id);
    }


    /**
     * @Created at 12/3 15:30
     * @author zrh
     * @param address
     * @return
     */
    public ReturnObject updateAddress(Address address) {
        return addressDao.updateAddressInfo(address);

    }

    /**
     * @Created at 12/3 19:26
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject deleteAddress(Long id) {
        return addressDao.deleteAddress(id);
    }
}
