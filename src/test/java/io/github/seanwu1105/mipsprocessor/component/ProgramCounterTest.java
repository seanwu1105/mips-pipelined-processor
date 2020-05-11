package io.github.seanwu1105.mipsprocessor.component;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgramCounterTest {

    @NotNull
    private ProgramCounter pc;

    @BeforeEach
    void buildUp() {
        pc = new ProgramCounter();
    }

    @Test
    void testCounter() {
        assertEquals(0, pc.getCounter());
        pc.setCounter(5);
        assertEquals(5, pc.getCounter());
    }
}