package cn.edu.xmu.cart.dao;


import cn.edu.xmu.cart.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.cart.model.bo.Cart;
import cn.edu.xmu.cart.model.po.ShoppingCartPo;
import cn.edu.xmu.cart.model.po.ShoppingCartPoExample;
import cn.edu.xmu.cart.model.vo.CartVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.support.SimpleTriggerContext;
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
            else if (!po.getCustomerId().equals(userId)){
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

    /**
     * @Created at 12/13 21:20
     * @author zrh
     * @param cart
     * @return
     */
    public ReturnObject<Cart> addCart(Cart cart) {
        ShoppingCartPoExample cartPoExample=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria = cartPoExample.createCriteria();
        criteria.andCustomerIdEqualTo(cart.getCustomerId());
        criteria.andGoodsSkuIdEqualTo(cart.getGoodsSkuId());
        List<ShoppingCartPo> po=null;
        try{
            po=shoppingCartPoMapper.selectByExample(cartPoExample);
            if(po!=null) {
                Integer num = po.get(0).getQuantity();
                po.get(0).setQuantity(num + cart.getQuantity());

                int ret = shoppingCartPoMapper.updateByPrimaryKey(po.get(0));
                if (ret == 0) {
                    return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("购物车商品不存在"));
                }
                cart.setQuantity(po.get(0).getQuantity());
                return new ReturnObject(cart);
            }else{
                ShoppingCartPo shoppingCartPo=cart.createPo();
                int ret= shoppingCartPoMapper.insertSelective(shoppingCartPo);
                return new ReturnObject(cart);
            }
        }catch (DataAccessException e){
            logger.error("addCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

    }

    /**
     * @Created at 12/13 21:20
     * @author zrh
     * @param id
     * @param userId
     * @return
     */
    public ReturnObject<ShoppingCartPo> seleteById(Long id,Long userId) {
        ShoppingCartPo po=null;
        try{
            po=shoppingCartPoMapper.selectByPrimaryKey(id);
            if (po==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("购物车id不存在"));
            }
            else if (!po.getCustomerId().equals(userId)){
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("用户未拥有"));
            }
        }catch (DataAccessException e){
            logger.error("addCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject(po);
    }

    /**
     * @Created at 12/13 21:20
     * @author zrh
     * @param id
     * @param userId
     * @param vo
     * @return
     */
    public ReturnObject changeCartInfo(Long id, Long userId, CartVo vo) {
        ShoppingCartPoExample example=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria=example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        criteria.andIdEqualTo(id);
        criteria.andGoodsSkuIdEqualTo(vo.getGoodSkuID());
        criteria.andQuantityEqualTo(vo.getQuantity());
        try {
            ShoppingCartPo po=shoppingCartPoMapper.selectByPrimaryKey(id);
            int ret=shoppingCartPoMapper.updateByExampleSelective(po,example);
            if(ret==0){
                return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST,String.format("购物车商品不存在"));
            }
        }catch (DataAccessException e){
            logger.error("addCarts: DataAccessException:" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject(ResponseCode.OK);
    }
}
