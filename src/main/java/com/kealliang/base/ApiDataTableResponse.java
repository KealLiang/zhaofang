package com.kealliang.base;

/**
 * @author lsr
 * @ClassName ApiDataTableResponse
 * @Date 2019-02-04
 * @Desc DataTable专用返回结构
 * @Vertion 1.0
 */
public class ApiDataTableResponse extends ApiResponse {

    private int draw;
    private long recordsTotal;
    private long recordsFiltered;

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

    public ApiDataTableResponse(Status status) {
        this(status.getCode(), status.getMessage(), null);
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
