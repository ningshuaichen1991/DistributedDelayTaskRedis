package com.enums;
import	java.util.stream.Collectors;

import java.util.Arrays;
import java.util.List;

public enum BusinessTypeEnum {

    /**
     * 开户
     */
    opendAccount("openAccount"),
    /**
     * 推送风控
     */
    pushRisk("pushRisk");


    private String businessValue;

    private BusinessTypeEnum(String businessValue) {
        this.businessValue = businessValue;
    }

    public String getBusinessValue() {
        return businessValue;
    }

    public static List<String> getAllBusinessTypeStringList(){
        return Arrays.stream(BusinessTypeEnum.values()).
                map(x -> x.getBusinessValue()).collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举对象
     * @param value
     * @return
     */
    public static BusinessTypeEnum getByValue(String value){
        return Arrays.stream(BusinessTypeEnum.values()).filter(v->v.getBusinessValue().equals(value)).findFirst().get();
    }

    public static List<BusinessTypeEnum> getAllBusinessTypeList(){
        return Arrays.stream(BusinessTypeEnum.values()).collect(Collectors.toList());
    }
}
