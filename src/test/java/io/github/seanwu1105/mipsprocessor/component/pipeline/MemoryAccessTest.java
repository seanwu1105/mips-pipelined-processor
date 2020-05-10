package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.Memory;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemoryAccessTest {

    private final ExecutionToMemoryAccessRegister exeMem = mock(ExecutionToMemoryAccessRegister.class);
    private final int expectedAluResult = 20;
    private final int expectedWriteRegisterAddress = 2;
    private final int expectedMemoryReadData = 5;
    @NotNull
    private Memory dataMemory;
    @NotNull
    private MemoryAccess memoryAccess;

    MemoryAccessTest() {
        when(exeMem.getAluResult()).thenReturn(expectedAluResult);
        when(exeMem.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);
    }

    @BeforeEach
    void buildUp() {
        dataMemory = new Memory();
        dataMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        dataMemory.setAddress(expectedAluResult);
        dataMemory.write(expectedMemoryReadData);
        dataMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);

        memoryAccess = new MemoryAccess(exeMem, dataMemory);
    }

    @Test
    void testPropertiesPass() {
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;

        when(exeMem.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(exeMem.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(exeMem.getMemoryRead()).thenReturn(MainController.MemoryRead.FALSE);
        when(exeMem.getMemoryWrite()).thenReturn(MainController.MemoryWrite.FALSE);

        memoryAccess.run();

        assertEquals(expectedRegisterWrite, memoryAccess.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, memoryAccess.getMemoryToRegister());
        assertEquals(expectedAluResult, memoryAccess.getAluResult());
        assertEquals(expectedWriteRegisterAddress, memoryAccess.getWriteRegisterAddress());
    }

    @Test
    void testRType() {
        when(exeMem.shouldBranch()).thenReturn(false);
        when(exeMem.getMemoryRead()).thenReturn(MainController.MemoryRead.FALSE);
        when(exeMem.getMemoryWrite()).thenReturn(MainController.MemoryWrite.FALSE);

        memoryAccess.run();

        assertEquals(expectedAluResult, memoryAccess.getAluResult());
    }

    @Test
    void testLoadWord() {
        when(exeMem.shouldBranch()).thenReturn(false);
        when(exeMem.getMemoryRead()).thenReturn(MainController.MemoryRead.TRUE);
        when(exeMem.getMemoryWrite()).thenReturn(MainController.MemoryWrite.FALSE);

        memoryAccess.run();

        assertEquals(expectedMemoryReadData, memoryAccess.getMemoryReadData());
    }

    @Test
    void testSaveWord() {
        when(exeMem.shouldBranch()).thenReturn(false);
        when(exeMem.getMemoryRead()).thenReturn(MainController.MemoryRead.FALSE);
        when(exeMem.getMemoryWrite()).thenReturn(MainController.MemoryWrite.TRUE);

        final var expectedRegisterData2 = 6;
        when(exeMem.getRegisterData2()).thenReturn(expectedRegisterData2);

        memoryAccess.run();

        dataMemory.setMemoryRead(MainController.MemoryRead.TRUE);
        assertEquals(expectedRegisterData2, dataMemory.read());
    }

    @AfterEach
    void tearDown() {
        dataMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
        dataMemory.setMemoryRead(MainController.MemoryRead.FALSE);
    }
}