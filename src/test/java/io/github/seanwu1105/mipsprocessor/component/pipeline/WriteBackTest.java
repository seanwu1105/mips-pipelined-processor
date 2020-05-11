package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.Register;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WriteBackTest {

    private final int expectedMemoryReadData = 11;
    private final int expectedAluResult = 12;
    private final int expectedRegisterWriteAddress = 1;
    @NotNull
    private MemoryAccessToWriteBackRegister memWb;
    @NotNull
    private WriteBack writeBack;
    @NotNull
    private Register register;

    @BeforeEach
    void buildUp() {
        memWb = mock(MemoryAccessToWriteBackRegister.class);
        when(memWb.getMemoryReadData()).thenReturn(expectedMemoryReadData);
        when(memWb.getAluResult()).thenReturn(expectedAluResult);
        when(memWb.getWriteRegisterAddress()).thenReturn(expectedRegisterWriteAddress);
        register = new Register();
        writeBack = new WriteBack(memWb, register);
    }

    @Test
    void testRType() {
        when(memWb.getWriteRegisterData()).thenReturn(expectedAluResult);
        when(memWb.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(memWb.getMemoryToRegister()).thenReturn(MainController.MemoryToRegister.FROM_ALU_RESULT);

        writeBack.run();

        register.setReadAddress1(expectedRegisterWriteAddress);
        assertEquals(expectedAluResult, register.readData1());
    }

    @Test
    void testLoadWord() {
        when(memWb.getWriteRegisterData()).thenReturn(expectedMemoryReadData);
        when(memWb.getRegisterWrite()).thenReturn(MainController.RegisterWrite.TRUE);
        when(memWb.getMemoryToRegister()).thenReturn(MainController.MemoryToRegister.FROM_MEMORY);

        writeBack.run();

        register.setReadAddress1(expectedRegisterWriteAddress);
        assertEquals(expectedMemoryReadData, register.readData1());
    }

    @AfterEach
    void tearDown() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }
}