package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.other.model.po.AftersaleServicePo;
import cn.edu.xmu.other.model.vo.AftersaleDeliverVo;
import cn.edu.xmu.other.model.vo.AftersaleRetVo;
import cn.edu.xmu.other.model.vo.AftersaleSendbackVo;
import cn.edu.xmu.other.model.vo.AftersaleVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Aftersale implements VoObject {

    public static String AESPASS = "OOAD2020-11-01";

    /**
     * 后台用户状态
     */
    public enum State {
        CHECK(0, "待审核"),
        SENDBACKWAIT(1, "待买家发货"),
        SENDBACKING(2, "买家已发货"),
        REFUNDWAIT(3, "待店家退款"),
        DILIVERWAIT(4, "待店家发货"),
        DELIVERING(5, "店家已发货"),
        DISAGREE(6, "审核不通过"),
        CANCEL(7, "已取消"),
        SUCESS(8, "已结束");

        private static final Map<Integer, Aftersale.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (Aftersale.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Aftersale.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Type{
        BACK(0,"换货"),
        RETURN(1,"退货");

        private static final Map<Integer, Aftersale.Type> typeMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            typeMap = new HashMap();
            for (Aftersale.Type enum1 : values()) {
                typeMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        Type(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Aftersale.Type getTypeByCode(Integer code) {
            return typeMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;

    private Long orderItemId;

    private Long customerId;

    private Long shopId;

    private String serviceSn;

    private Byte type;

    private String reason;

    private String conclusion;

    private Long refund;

    private Integer quantity;

    private Long regionId;

    private String detail;

    private String consignee;

    private String mobile;

    private String customerLogSn;

    private String shopLogSn;

    private Byte state;

    private Byte beDeleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public Aftersale(AftersaleServicePo po){
        this.id=po.getId();
        this.orderItemId=po.getOrderItemId();
        this.customerId=po.getCustomerId();
        this.shopId=po.getShopId();
        this.serviceSn=po.getServiceSn();
        this.type=po.getType();
        this.reason=po.getReason();
        this.conclusion=po.getConclusion();
        this.refund=po.getRefund();
        this.quantity=po.getQuantity();
        this.regionId=po.getRegionId();
        this.detail=po.getDetail();
        this.consignee=po.getConsignee();
        this.mobile = AES.decrypt(po.getMobile(),AESPASS);
        this.customerLogSn=po.getCustomerLogSn();
        this.shopLogSn=po.getShopLogSn();
        this.state=po.getState();
        this.beDeleted=po.getBeDeleted();
        this.gmtCreated=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    public AftersaleServicePo createUpdatePo(AftersaleVo vo) {
        String newMobile = vo.getMobile() == null ? null : AES.encrypt(vo.getMobile(), Aftersale.AESPASS);

        Long refunds=(refund/quantity)*vo.getQuantity();

        AftersaleServicePo po = new AftersaleServicePo();
        po.setId(id);
        po.setReason(vo.getReason());
        po.setConclusion("");
        po.setRefund(refunds);
        po.setQuantity(vo.getQuantity());
        po.setRegionId(vo.getRegionId());
        po.setDetail(vo.getDetail());
        po.setConsignee(vo.getConsignee());
        po.setMobile(newMobile);
        po.setState(State.CHECK.getCode().byteValue());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }

    public AftersaleServicePo createSendbackPo(AftersaleSendbackVo vo){
        AftersaleServicePo po = new AftersaleServicePo();

        po.setId(id);
        po.setCustomerLogSn(vo.getCustomerLogSn());
        po.setState(State.SENDBACKING.getCode().byteValue());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }

    public AftersaleServicePo createConfirmPo(){
        AftersaleServicePo po = new AftersaleServicePo();

        po.setId(id);
        po.setState(State.SUCESS.getCode().byteValue());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }

    public AftersaleServicePo createDeliverPo(AftersaleDeliverVo vo){
        AftersaleServicePo po = new AftersaleServicePo();

        po.setId(id);
        po.setCustomerLogSn(vo.getShopLogSn());
        po.setState(State.DELIVERING.getCode().byteValue());
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        return po;
    }

    @Override
    public AftersaleRetVo createVo() {
        AftersaleRetVo vo=new AftersaleRetVo();

        vo.setId(id);
        vo.setOrderItemId(orderItemId);
        vo.setCustomerId(customerId);
        vo.setShopId(shopId);
        vo.setServiceSn(serviceSn);
        vo.setType(type);
        vo.setReason(reason);
        vo.setConclusion(conclusion);
        vo.setRefund(refund);
        vo.setQuantity(quantity);
        vo.setRegionId(regionId);
        vo.setDetail(detail);
        vo.setConsignee(consignee);
        vo.setMobile(AES.decrypt(mobile,Aftersale.AESPASS));
        vo.setCustomerLogSn(customerLogSn);
        vo.setShopLogSn(shopLogSn);
        vo.setState(state);

        return vo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
