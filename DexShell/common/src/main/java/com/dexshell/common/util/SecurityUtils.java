package com.dexshell.common.util;

import java.security.MessageDigest;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Arrays;
import java.util.stream.Stream;

public class SecurityUtils {

    public static Stream<String> availableHashAlgorithms() {
        return Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream())
                .filter(s -> s.getType().equalsIgnoreCase(MessageDigest.class.getSimpleName()))
                .map(Service::getAlgorithm);
    }

    public static void main(String[] args) {
        availableHashAlgorithms().forEach(System.out::println);
    }

}
