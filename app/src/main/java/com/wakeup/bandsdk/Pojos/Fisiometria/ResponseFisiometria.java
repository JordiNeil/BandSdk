package com.wakeup.bandsdk.Pojos.Fisiometria;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseFisiometria {
    @SerializedName("body")
    @Expose
    private DataFisiometria dataFisiometria;
    /*@SerializedName("headers")
    @Expose
    private String status;*/

    //private Data data;

        public ResponseFisiometria(DataFisiometria dataFisiometria) {
            super();
            this.dataFisiometria = dataFisiometria;

        }

        public DataFisiometria getMsg() {
            return dataFisiometria;
        }

        public void setMsg(DataFisiometria dataFisiometria) {
            this.dataFisiometria = dataFisiometria;
        }


}
