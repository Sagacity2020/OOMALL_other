package cn.edu.xmu.address.service;

import cn.edu.xmu.address.model.bo.AddressPage;
import cn.edu.xmu.address.model.bo.Region;
import cn.edu.xmu.address.model.po.AddressPo;
import cn.edu.xmu.address.model.vo.RegionVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.address.dao.AddressDao;
import cn.edu.xmu.address.model.bo.Address;

import cn.edu.xmu.other.service.RegionServiceInterface;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@DubboService(version = "0.0.1")
@Service
public class AddressService implements RegionServiceInterface {


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
        ReturnObject<Boolean> booleanReturnObject=addressDao.isRegion(address.getRegionId());
        if(booleanReturnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST){
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if(booleanReturnObject.getData()==null){
            return booleanReturnObject;
        }
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
        PageHelper.startPage(page, pageSize);
        ReturnObject<PageInfo<AddressPo>> returnObject= addressDao.selectAllAddress(userId,page,pageSize);
        PageInfo<AddressPo> addressPoPageInfo=returnObject.getData();
        List<VoObject> ret=new ArrayList<>(addressPoPageInfo.getList().size());
        for(AddressPo po:addressPoPageInfo.getList()){
            AddressPage addressPage=new AddressPage(po);
            ret.add(addressPage);
        }
        PageInfo<VoObject> addressPage=new PageInfo<>(ret);
        addressPage.setPages(addressPoPageInfo.getPages());
        addressPage.setPageNum(addressPoPageInfo.getPageNum());
        addressPage.setPageSize(addressPoPageInfo.getPageSize());
        addressPage.setTotal(addressPoPageInfo.getTotal());
        return new ReturnObject<>(addressPage);
    }


    /**
     * @Created at 12/2 14:07
     * @author zrh
     * @param id
     * @return
     */
    @Transactional
    public ReturnObject setDefaultAddress(Long userId,Long id) {
        ReturnObject returnObject= addressDao.cancelDefaultAddress(userId);
        Boolean ret= (Boolean) returnObject.getData();
        if(ret==true){
            return addressDao.setDefaultAddress(userId,id);
        }
        else {
            return returnObject;
        }
    }




    /**
     * @Created at 12/3 15:30
     * @author zrh
     * @param address
     * @return
     */
    public ReturnObject updateAddress(Address address) {
        ReturnObject<Boolean> returnObject=addressDao.isRegion(address.getRegionId());
        if(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST){
            return new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        if(returnObject.getData()==null){
            return returnObject;
        }

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

    public ReturnObject queryPreRegion(Long id) {
        ReturnObject<Boolean> retObject=isRegion(id);
        if(retObject.getData()==null){
            return retObject;
        }
        ReturnObject<List<Region>> returnObject=addressDao.queryParentRegionAll(id);
        return returnObject;


    }


    /**
     * @Created at 12/18 1:49
     * @author zrh
     * @param id
     * @param vo
     * @return
     */
    public ReturnObject newSubRegion(Long id, RegionVo vo) {
        return addressDao.newSubRegion(id,vo);
    }

    /**
     * @Created at 12/9 19:40
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject<Boolean> isRegion(Long id){
        ReturnObject<Boolean> returnObject=addressDao.isRegion(id);
        return returnObject;
    }

    /**
     * @Created at 12/10 21:29
     * @author zrh
     * @param region
     * @return
     */
    public ReturnObject updateRegion(Region region) {
        ReturnObject returnObject=addressDao.updateRegion(region);
        return returnObject;
    }

    /**
     * @Created at 12/10 21:29
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject deleteRegion(Long id) {
//        Queue<Long> idQueue = new ArrayDeque<>();
        ReturnObject returnObject=addressDao.deleteRegion(id);
        List<Long> ids=addressDao.getChildRegion(id);
//        idQueue.addAll(ids);
//        if(!idQueue.isEmpty()){
//            Long childId=idQueue.poll();
//            regionDao.deleteChildRegionOnly(childId);
//            }
//
        return returnObject;
    }


    @Override
    public Long getParentRegionIdByChildId(Long regionId) {
        ReturnObject<Region> returnObject=addressDao.queryPreRegion(regionId);
        if(returnObject.getCode()== ResponseCode.REGION_OBSOLETE){
            return  null;
        }
        Region region=returnObject.getData();
        if(region!=null){
            return region.getId();
        }
        return 0L;
    }

}
