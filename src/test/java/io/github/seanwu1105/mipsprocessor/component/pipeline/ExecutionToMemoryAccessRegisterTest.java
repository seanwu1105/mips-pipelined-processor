package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExecutionToMemoryAccessRegisterTest {

    @NotNull
    private Execution execution;
    @NotNull
    private ExecutionToMemoryAccessRegister exeMem;

    @BeforeEach
    void buildUp() {
        execution = mock(Execution.class);
        exeMem = new ExecutionToMemoryAccessRegister(execution);
    }

    @Test
    void testUpdate() {
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;
        final var expectedMemoryRead = MainController.MemoryRead.TRUE;
        final var expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final var expectedBranch = MainController.Branch.FALSE;
        final var expectedAluResult = 0;
        final var expectedRegisterData2 = 7;
        final var expectedWriteRegisterAddress = 2;

        when(execution.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(execution.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(execution.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(execution.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(execution.getBranch()).thenReturn(expectedBranch);
        when(execution.getAluResult()).thenReturn(expectedAluResult);
        when(execution.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(execution.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);

        exeMem.update();

        assertEquals(expectedRegisterWrite, exeMem.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, exeMem.getMemoryToRegister());
        assertEquals(expectedMemoryRead, exeMem.getMemoryRead());
        assertEquals(expectedMemoryWrite, exeMem.getMemoryWrite());
        assertEquals(expectedRegisterData2, exeMem.getRegisterData2());
        assertEquals(expectedWriteRegisterAddress, exeMem.getWriteRegisterAddress());
    }
}