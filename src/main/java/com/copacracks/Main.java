package com.copacracks;

import io.javalin.Javalin;

@SuppressWarnings("PMD.ShortClassName")
public class Main {
  public static void main(String[] args) {
    Javalin.create().get("/", ctx -> ctx.result("Hello Copacracks Backend")).start(8080);
  }
}
