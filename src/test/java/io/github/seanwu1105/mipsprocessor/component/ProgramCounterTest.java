package io.github.seanwu1105.mipsprocessor.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgramCounterTest {

    @Test
    void testCounter() {
        final var pc = new ProgramCounter();
        assertEquals(0, pc.getCounter());
        pc.setCounter(5);
        assertEquals(5, pc.getCounter());
    }
}