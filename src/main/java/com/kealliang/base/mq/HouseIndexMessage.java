package com.kealliang.base.mq;

/**
 * @author lsr
 * @ClassName HouseIndexMessage
 * @Date 2019-02-10
 * @Desc kafka消息实体
 * @Vertion 1.0
 */
public class HouseIndexMessage {

    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    public static final int MAX_RETRY = 3;

    private Long houseId;
    private String operation;
    private int retry = 0;

    /**
     * 防止jackson序列化失败的默认构造器
     * @author lsr
     * @description HouseIndexMessage
     * @Date 0:59 2019/2/10
     * @Param []
     * @return
     */
    public HouseIndexMessage() {
    }

    public HouseIndexMessage(Long houseId, String operation, int retry) {
        this.houseId = houseId;
        this.operation = operation;
        this.retry = retry;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}
