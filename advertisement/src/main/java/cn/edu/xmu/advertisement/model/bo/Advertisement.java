package cn.edu.xmu.advertisement.model.bo;

import cn.edu.xmu.advertisement.model.po.AdvertisementPo;
import cn.edu.xmu.advertisement.model.vo.AdvertisementRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Advertisement implements VoObject {

    /**
     * 广告状态
     */
    public enum State{
        CHECK(0,"审核"),
        ONSHELF(4,"上架"),
        OFFSHELF(6,"下架");

        private static final Map<Integer,State> stateMap;

        static {
            stateMap=new HashMap<>();
            for(State enum1:values()){
                stateMap.put(enum1.code,enum1);
            }
        }

        private int code;
        private String description;

        State(int code,String description){
            this.code=code;
            this.description=description;
        }

        public static State getTypeByCode(Integer code){return stateMap.get(code);}

        public Integer getCode(){return code;}

        public String getDescription(){return description;}
    }

    private Long id;

    private Long segId;

    private String link;

    private String content;

    private String imageUrl;

    private Byte state;

    private Integer weight;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Byte repeats;

    private String message;

    private Byte beDefault;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;


    public Advertisement(){

    }

    public Advertisement(AdvertisementPo po){
        this.id=po.getId();
        this.segId=po.getSegId();
        this.link=po.getLink();
        this.content=po.getContent();
        this.imageUrl=po.getImageUrl();
        this.state=po.getState();
        this.weight=po.getWeight();
        this.beginDate=po.getBeginDate();
        this.endDate=po.getEndDate();
        this.repeats=po.getRepeats();
        this.message=po.getMessage();
        this.beDefault=po.getBeDefault();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    public AdvertisementPo createInsertAdvertisement(){
        AdvertisementPo advertisementPo=new AdvertisementPo();

        advertisementPo.setContent(content);
        advertisementPo.setWeight(weight);
        advertisementPo.setSegId(segId);
        advertisementPo.setBeginDate(beginDate);
        advertisementPo.setEndDate(endDate);
        advertisementPo.setRepeats(repeats);
        advertisementPo.setLink(link);
        advertisementPo.setState(State.CHECK.getCode().byteValue());

        advertisementPo.setGmtCreate(LocalDateTime.now().withNano(0));
        advertisementPo.setGmtModified(null);

        return advertisementPo;
    }

    public AdvertisementPo createInsertAdvertisement1(Long segId){
        AdvertisementPo po=new AdvertisementPo();

        po.setId(id);
        po.setSegId(segId);
        po.setGmtModified(LocalDateTime.now().withNano(0));

        return po;
    }


    @Override
    public AdvertisementRetVo createVo() {
        AdvertisementRetVo vo=new AdvertisementRetVo();

        vo.setId(id);
        vo.setSegId(segId);
        vo.setLink(link);
        vo.setContent(content);
        vo.setImagePath(imageUrl);
        vo.setState(state.intValue());
        vo.setWeight(weight.toString());
        vo.setBeginDate(beginDate);
        vo.setEndDate(endDate);
        if(repeats.intValue()==1) {
            vo.setRepeat(true);
        }
        else{
            vo.setRepeat(false);
        }
        if(beDefault.intValue()==1) {
            vo.setBeDefault(true);
        }
        else{
            vo.setBeDefault(false);
        }
        vo.setGmtCreate(gmtCreate);
        vo.setGmtModified(gmtModified);

        return vo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
