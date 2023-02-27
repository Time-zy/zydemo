package com.example.zydemo.utils.SubTableUtil;

public enum IndexTypeEnum {

    BPlusTree(0, "b+树索引"),
    BTREE_GIN(1, "b_gin索引"),
    GIN_TRGM_OPS(2, "pg_gin索引");

    private int type;

    private String desc;

    IndexTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
