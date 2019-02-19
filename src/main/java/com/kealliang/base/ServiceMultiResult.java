package com.kealliang.base;

import org.springframework.util.CollectionUtils;
import sun.plugin2.message.GetNameSpaceMessage;

import java.util.List;

/**
 * @author lsr
 * @ClassName ServiceMultiResult
 * @Date 2019-02-03
 * @Desc service多结果查询通用返回
 * @Vertion 1.0
 */
public class ServiceMultiResult<T> {

    private long total;
    private List<T> result;

    public ServiceMultiResult(long total, List<T> result) {
        this.total = total;
        this.result = result;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getResultSize() {
        if (CollectionUtils.isEmpty(result)) {
            return 0;
        }
        return result.size();
    }
}
