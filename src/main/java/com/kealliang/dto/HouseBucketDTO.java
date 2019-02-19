package com.kealliang.dto;

/**
 * @author lsr
 * @ClassName HouseBucketDTO
 * @Date 2019-02-16
 * @Desc 地图找房-城市为单位聚合数据
 * @Vertion 1.0
 */
public class HouseBucketDTO {
    /** 
     * 聚合bucket的key
     */
    private String key;
    /** 
     * 聚合结果值
     */
    private long count;

    public HouseBucketDTO() {
    }

    public HouseBucketDTO(String key, long count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
