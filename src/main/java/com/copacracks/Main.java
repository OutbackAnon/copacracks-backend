package com.copacracks;

import com.copacracks.infrastructure.config.Bootstrap;

@SuppressWarnings("PMD.UseUtilityClass")
public class Main {
    public static void main(String[] args) {
        new Bootstrap().start();
    }
}
