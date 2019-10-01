package com.dangerye.powerful;

import com.dangerye.powerful.utils.CharFilterUtils;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testCharFilterUtils() {
        String testString = "823@#4423@dsFweRgsd^&*!~";
        System.out.println(CharFilterUtils.filterChar(testString, " "));
        System.out.println(CharFilterUtils.filterChar(testString, ""));
        System.out.println(CharFilterUtils.filterChar(testString, null));
    }
}
