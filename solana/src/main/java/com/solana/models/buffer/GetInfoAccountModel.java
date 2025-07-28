package com.solana.models.buffer;

import java.util.List;

public class GetInfoAccountModel {
    private Result result;

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public class Result {
        private Value value;

        public Value getValue() {
            return value;
        }
    }

    public class Value {
        private List<String> data;

        public List<String> getData() {
            return data;
        }
    }
}
