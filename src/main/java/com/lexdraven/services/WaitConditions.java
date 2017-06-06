package com.lexdraven.services;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.function.Function;

public enum WaitConditions {
    visible(ExpectedConditions::visibilityOfElementLocated),
    exist(ExpectedConditions::presenceOfElementLocated),
    clickable(ExpectedConditions::elementToBeClickable),
    invisible(ExpectedConditions::invisibilityOfElementLocated);

    WaitConditions(Function<By, ExpectedCondition<?>> type) {
        this.type = type;
    }

    public Function<By, ExpectedCondition<?>> getType() {
        return type;
    }

    private final Function<By, ExpectedCondition<?>> type;

}
