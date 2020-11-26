package cn.edu.xmu.other.model.po;

import java.time.LocalDateTime;

public class AdvertisementPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.seg_id
     *
     * @mbg.generated
     */
    private Long segId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.link
     *
     * @mbg.generated
     */
    private String link;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.content
     *
     * @mbg.generated
     */
    private String content;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.image_url
     *
     * @mbg.generated
     */
    private String imageUrl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.state
     *
     * @mbg.generated
     */
    private Byte state;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.weight
     *
     * @mbg.generated
     */
    private Integer weight;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.begin_date
     *
     * @mbg.generated
     */
    private LocalDateTime beginDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.end_date
     *
     * @mbg.generated
     */
    private LocalDateTime endDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.repeat
     *
     * @mbg.generated
     */
    private Byte repeat;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.message
     *
     * @mbg.generated
     */
    private String message;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.be_default
     *
     * @mbg.generated
     */
    private Byte beDefault;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.gmt_created
     *
     * @mbg.generated
     */
    private LocalDateTime gmtCreated;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column advertisement.gmt_modified
     *
     * @mbg.generated
     */
    private LocalDateTime gmtModified;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.id
     *
     * @return the value of advertisement.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.id
     *
     * @param id the value for advertisement.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.seg_id
     *
     * @return the value of advertisement.seg_id
     *
     * @mbg.generated
     */
    public Long getSegId() {
        return segId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.seg_id
     *
     * @param segId the value for advertisement.seg_id
     *
     * @mbg.generated
     */
    public void setSegId(Long segId) {
        this.segId = segId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.link
     *
     * @return the value of advertisement.link
     *
     * @mbg.generated
     */
    public String getLink() {
        return link;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.link
     *
     * @param link the value for advertisement.link
     *
     * @mbg.generated
     */
    public void setLink(String link) {
        this.link = link == null ? null : link.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.content
     *
     * @return the value of advertisement.content
     *
     * @mbg.generated
     */
    public String getContent() {
        return content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.content
     *
     * @param content the value for advertisement.content
     *
     * @mbg.generated
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.image_url
     *
     * @return the value of advertisement.image_url
     *
     * @mbg.generated
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.image_url
     *
     * @param imageUrl the value for advertisement.image_url
     *
     * @mbg.generated
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.state
     *
     * @return the value of advertisement.state
     *
     * @mbg.generated
     */
    public Byte getState() {
        return state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.state
     *
     * @param state the value for advertisement.state
     *
     * @mbg.generated
     */
    public void setState(Byte state) {
        this.state = state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.weight
     *
     * @return the value of advertisement.weight
     *
     * @mbg.generated
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.weight
     *
     * @param weight the value for advertisement.weight
     *
     * @mbg.generated
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.begin_date
     *
     * @return the value of advertisement.begin_date
     *
     * @mbg.generated
     */
    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.begin_date
     *
     * @param beginDate the value for advertisement.begin_date
     *
     * @mbg.generated
     */
    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.end_date
     *
     * @return the value of advertisement.end_date
     *
     * @mbg.generated
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.end_date
     *
     * @param endDate the value for advertisement.end_date
     *
     * @mbg.generated
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.repeat
     *
     * @return the value of advertisement.repeat
     *
     * @mbg.generated
     */
    public Byte getRepeat() {
        return repeat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.repeat
     *
     * @param repeat the value for advertisement.repeat
     *
     * @mbg.generated
     */
    public void setRepeat(Byte repeat) {
        this.repeat = repeat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.message
     *
     * @return the value of advertisement.message
     *
     * @mbg.generated
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.message
     *
     * @param message the value for advertisement.message
     *
     * @mbg.generated
     */
    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.be_default
     *
     * @return the value of advertisement.be_default
     *
     * @mbg.generated
     */
    public Byte getBeDefault() {
        return beDefault;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.be_default
     *
     * @param beDefault the value for advertisement.be_default
     *
     * @mbg.generated
     */
    public void setBeDefault(Byte beDefault) {
        this.beDefault = beDefault;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.gmt_created
     *
     * @return the value of advertisement.gmt_created
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.gmt_created
     *
     * @param gmtCreated the value for advertisement.gmt_created
     *
     * @mbg.generated
     */
    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column advertisement.gmt_modified
     *
     * @return the value of advertisement.gmt_modified
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column advertisement.gmt_modified
     *
     * @param gmtModified the value for advertisement.gmt_modified
     *
     * @mbg.generated
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}