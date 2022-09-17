package com.dangerye.powerful.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Slf4j
public final class GrayRatioUtils {

    public static boolean isHitTheGray(String grayRatioConfig, String businessCode, String target) {
        GrayRatio grayRatio;
        try {
            grayRatio = JSON.parseObject(grayRatioConfig, GrayRatio.class);
        } catch (Exception e) {
            grayRatio = null;
        }
        if (grayRatio == null) {
            return false;
        }

        if (!CollectionUtils.isEmpty(grayRatio.getWhiteList())) {
            if (grayRatio.getWhiteList().contains(target)) {
                return true;
            }
        }

        int ratio = grayRatio.getRatio();
        if (ratio >= 100) {
            return true;
        }
        if (ratio <= 0) {
            return false;
        }

        int hash = getHashCode(businessCode, target);
        return hash < ratio;
    }

    private static int getHashCode(String businessCode, String target) {
        StringBuilder sb = new StringBuilder(businessCode);
        if (StringUtils.isNotBlank(target)) {
            sb.append(target);
        }

        int hashCode = DigestUtils.md5Hex(sb.toString()).hashCode();
        int result = Math.abs(hashCode % 100);
        LogUtils.debug(log, "GET_GRAY_VALUE",
                "businessCode:{}, target:{}, result:{}",
                businessCode, target, result);
        return result;
    }

    @Data
    private static class GrayRatio {
        private Set<String> whiteList;
        private int ratio;
    }
}
