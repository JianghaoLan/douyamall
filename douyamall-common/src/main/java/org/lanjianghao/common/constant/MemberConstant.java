package org.lanjianghao.common.constant;

public class MemberConstant {
    public enum MemberGenderEnum {
        MALE(2, "男"), FEMALE(1, "女"), UNKNOWN(0, "未知");

        private final int code;
        private final String msg;

        MemberGenderEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum LevelDefaultStatusEnum {
        DEFAULT(1, "默认等级"), NON_DEFAULT(0, "非默认等级");

        private final int code;
        private final String msg;

        LevelDefaultStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
