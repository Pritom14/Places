
package com.times.places.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.times.places.objects.Base;


public class Period extends Base {

    @SerializedName("close")
    @Expose
    private Close close;
    @SerializedName("open")
    @Expose
    private Open open;

    /**
     *
     * @return
     *     The close
     */
    public Close getClose() {
        return close;
    }

    /**
     *
     * @param close
     *     The close
     */
    public void setClose(Close close) {
        this.close = close;
    }

    /**
     *
     * @return
     *     The open
     */
    public Open getOpen() {
        return open;
    }

    /**
     *
     * @param open
     *     The open
     */
    public void setOpen(Open open) {
        this.open = open;
    }

}
