package com.kealliang.base.search;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lsr
 * @ClassName BaiduMapLocation
 * @Date 2019-02-16
 * @Desc Baidu地图位置信息
 * @Vertion 1.0
 */
public class BaiduMapLocation {

    // ES强制要求经度纬度字段名称为lon和lat
    // 经度
    @JsonProperty("lon")
    private double longitude;
    // 纬度
    @JsonProperty("lat")
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "BaiduMapLocation{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
