package com.companyname.parking.api.application.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Validation {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isEmpty(LocalDate obj) {
        return obj == null;
    }
    public static boolean isEmpty(Boolean obj) {
        return obj == null;
    }

    public static boolean isEmpty(List<Object> list) {
        return list == null || list.isEmpty();
    }
    public static boolean isEmpty(BigDecimal obj) {
        return obj == null;
    }
}
