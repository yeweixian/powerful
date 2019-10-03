package com.dangerye.powerful.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class IpUtils {

    public static List<String> getLocalIpList() {
        try {
            List<String> list = new ArrayList<>();
            Optional.ofNullable(NetworkInterface.getNetworkInterfaces())
                    .ifPresent(networkInterfaceEnumeration -> {
                        while (networkInterfaceEnumeration.hasMoreElements()) {
                            Optional.ofNullable(networkInterfaceEnumeration.nextElement())
                                    .map(NetworkInterface::getInetAddresses)
                                    .ifPresent(inetAddressEnumeration -> {
                                        while (inetAddressEnumeration.hasMoreElements()) {
                                            Optional.ofNullable(inetAddressEnumeration.nextElement())
                                                    .ifPresent(inetAddress -> {
                                                        if (inetAddress instanceof Inet4Address) {
                                                            String ip = inetAddress.getHostAddress();
                                                            list.add(ip);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return list;
        } catch (Exception e) {
            LogUtils.error(log, "IP_FAIL_EVENT", "getLocalIpList error", e);
            return null;
        }
    }
}
