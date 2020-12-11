package cn.edu.xmu.cart.dao;


import cn.edu.xmu.cart.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import cn.edu.xmu.cart.model.po.ShoppingCartPoExample;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Retention;
import java.util.List;


@Repository
public class CartDao {

    private static final Logger logger= LoggerFactory.getLogger(CartDao.class);

    @Autowired
    ShoppingCartPoMapper shoppingCartPoMapper;

    public ReturnObject<List<Cart>> seleteByUserId(Long userId) {
        ShoppingCartPoExample example=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        List<ShoppingCartPo> shoppingCartPos=null;
        try{
            shoppingCartPos=shoppingCartPoMapper.selectByExample(example);
        }catch (DataAccessException e){
            logger.error("selectAllAddress: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
           return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        List<Cart> carts=null;
        for(ShoppingCartPo po:shoppingCartPos){
            Cart cart=new Cart(po);
            carts.add(cart);
        }
        return new ReturnObject<>(carts);

    }

    /**
     * @Created at 12/11 18:17
     * @author zrh
     * @param userId
     * @return
     */
    public ReturnObject deleteAllCarts(Long userId) {
        ShoppingCartPoExample example=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        List<ShoppingCartPo> shoppingCartPos=null;

        try {
            shoppingCartPos=shoppingCartPoMapper.selectByExample(example);
            if(shoppingCartPos==null){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("购物车为空"));

            }
        }catch (DataAccessException e){
            logger.error("deleteAllCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        try{
            int ret=shoppingCartPoMapper.deleteByExample(example);
            if(ret==0){
                logger.debug("Cars is null");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("购物车为空"));
            }
            else{
                return new ReturnObject(ResponseCode.OK);
            }
        }catch (DataAccessException e){
            logger.error("deleteAllCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * @Created at 12/11 18:17
     * @author zrh
     * @param userId
     * @param id
     * @return
     */
    public ReturnObject deleteCarts(Long userId, Long id) {

        ShoppingCartPo po=null;
        try{
            po=shoppingCartPoMapper.selectByPrimaryKey(id);
            if(po==null){
                logger.debug("cart is not exist");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("购物车无该商品"));
            }
            else if (po.getCustomerId()!=userId){
                logger.debug("该商品不属于该用户");
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("购物车商品不属于该用户"));

            }
        }catch (DataAccessException e){
            logger.error("deleteCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        try{
            int ret=shoppingCartPoMapper.deleteByPrimaryKey(id);
            if(ret==0){
                logger.debug("cart is not exist");
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("购物车无该商品"));
            }
            else{
                return new ReturnObject(ResponseCode.OK);
            }
        }catch (DataAccessException e){
            logger.error("deleteCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }
}
