package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemoryAccessToWriteBackRegisterTest {

    @NotNull
    private MemoryAccess memoryAccess;
    @NotNull
    private MemoryAccessToWriteBackRegister memWb;

    @BeforeEach
    void buildUp() {
        memoryAccess = mock(MemoryAccess.class);
        memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterControlSignals() {
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;

        when(memoryAccess.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(memoryAccess.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);

        memWb.update();

        assertEquals(expectedRegisterWrite, memWb.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, memWb.getMemoryToRegister());
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterProperties() {
        final var expectedMemoryReadData = 2;
        final var expectedAluResult = 3;
        final var expectedWriteRegisterAddress = 4;

        when(memoryAccess.getMemoryReadData()).thenReturn(expectedMemoryReadData);
        when(memoryAccess.getAluResult()).thenReturn(expectedAluResult);
        when(memoryAccess.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);

        when(memoryAccess.getMemoryToRegister()).thenReturn(MainController.MemoryToRegister.FROM_MEMORY);

        memWb.update();

        assertEquals(expectedWriteRegisterAddress, memWb.getWriteRegisterAddress());
        assertEquals(expectedMemoryReadData, memWb.getWriteRegisterData());

        when(memoryAccess.getMemoryToRegister()).thenReturn(MainController.MemoryToRegister.FROM_ALU_RESULT);

        memWb.update();

        assertEquals(expectedWriteRegisterAddress, memWb.getWriteRegisterAddress());
        assertEquals(expectedAluResult, memWb.getWriteRegisterData());
    }
}