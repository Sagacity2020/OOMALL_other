package cn.edu.xmu.share.service.impl;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.order.dto.OrderDTO;
import cn.edu.xmu.order.service.ShareServiceInterface;
import cn.edu.xmu.other.service.AftersaleServiceInterface;
import cn.edu.xmu.other.service.CustomerServiceInterface;
import cn.edu.xmu.share.dao.BeShareDao;
import cn.edu.xmu.share.dao.ShareActivityDao;
import cn.edu.xmu.share.dao.ShareDao;
import cn.edu.xmu.share.model.bo.Rule;
import cn.edu.xmu.share.model.bo.ShareActivity;
import cn.edu.xmu.share.model.bo.Stategy;
import cn.edu.xmu.share.model.po.BeSharePo;
import cn.edu.xmu.share.model.po.SharePo;
import cn.edu.xmu.share.service.ShareService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DubboService(version = "0.0.1")
public class IShareServiceImpl implements ShareServiceInterface {

    private Logger logger = LoggerFactory.getLogger(ShareService.class);

    @Autowired
    private ShareDao shareDao;

    @Autowired
    private BeShareDao beShareDao;

    @Autowired
    private ShareActivityDao shareActivityDao;

    @DubboReference(version = "0.0.1")
    AftersaleServiceInterface aftersaleServiceInterface;

    @DubboReference(version = "0.0.1")
    CustomerServiceInterface customerServiceInterface;

    /**
     * 计算返点
     *
     * @author zxh
     * @param
     * @return rebate
     * @Date 2020/12/9 23:29
     */
    @Override
    public Integer calRebate(OrderDTO orderDTO)
    {
        try
        {
            //调用售后模块 判断是否有退货售后
            if(!aftersaleServiceInterface.checkIsAftersale(orderDTO.getOrderItemId()))
            {
                BeSharePo beSharePo = beShareDao.getBeShareById(orderDTO.getBeShareId());
                if(beSharePo == null)
                    return 0;
                //已经返过点
                if(beSharePo.getRebate() != 0)
                    return -1;
                SharePo sharePo = shareDao.getShareById(beSharePo.getShareId());
                if(sharePo == null)
                    return 0;
                ShareActivity shareActivity = shareActivityDao.getShareActivityById(sharePo.getShareActivityId());
                float rebate = 0f; //返点数
                int quantity = sharePo.getQuantity(); //分享中的数量
                int quantityOrder = 5; //改 订单中的数量
                float price = (orderDTO.getPrice()/100.0f)/ orderDTO.getQuantity(); //每件商品的价格
                Stategy stategy = JacksonUtil.toObj(shareActivity.getStrategy(), Stategy.class);
                List<Rule> rule = stategy.getRule();
                for(int i = 0; i < rule.size(); i++)
                {
                    if(quantity > rule.get(i+1).getNum() * 100)
                    {
                        continue;
                    }
                    else if((quantity >= rule.get(i).getNum() * 100) && (quantity + quantityOrder <= rule.get(i+1).getNum() * 100))
                    {
                        rebate += price * rule.get(i).getRate() * 0.01 * quantityOrder;
                        quantity += quantityOrder;
                        break;
                    }
                    else if((quantity >= rule.get(i).getNum() * 100) && (quantity + quantityOrder > rule.get(i+1).getNum() * 100))
                    {
                        rebate += price * rule.get(i).getRate() * 0.01 * (rule.get(i+1).getNum() * 100 - quantity);
                        quantityOrder -= rule.get(i+1).getNum() * 100 - quantity;
                        quantity += rule.get(i+1).getNum() * 100;
                    }
                }
                int temp = (int)(rebate *100);
                //更新用户的返点
                customerServiceInterface.payWithPoint(sharePo.getSharerId(), temp);
                //更新Share表的数量
                if(!shareDao.updateShareById(sharePo.getId(), quantity))
                {
                    return 0;
                }
                Long orderId =1L;
                //更新BeShare表的订单号和返点数量
                if(!beShareDao.updateBeShareById(beSharePo.getId(), orderId, temp))
                {
                    return 0;
                }

                return temp;
            }
            else
            {
                return 0;
            }
        }
        catch (DataAccessException e){
            logger.error("calRebate fail: DataAccessException:" + e.getMessage());
            return 0;
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return 0;
        }
    }


    /**
     * 获得beShareId
     *
     * @author zxh
     * @param customerId 被分享者Id
     * @param skuId skuId
     * @return Long
     * @Date 2020/12/15 20:37
     */
    public Long getBeShareId(Long customerId, Long skuId)
    {
        return beShareDao.getBeShareId(customerId, skuId);
    }

    /**
     * 查询一个商品是否可以分享
     *
     * @author zxh
     * @param shopId
     * @param goodsSkuId
     * @return Boolean
     * @Date 2020/12/15 21:37
     */
    public Boolean isShared(Long shopId, Long goodsSkuId)
    {
        return shareActivityDao.isShared(shopId, goodsSkuId);
    }
}
