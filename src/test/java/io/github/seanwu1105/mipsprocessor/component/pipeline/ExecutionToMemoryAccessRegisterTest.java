package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExecutionToMemoryAccessRegisterTest {

    @NotNull
    private final Execution execution = mock(Execution.class);

    @NotNull
    private ExecutionToMemoryAccessRegister exeMem;

    @BeforeEach
    void buildUp() {
        exeMem = new ExecutionToMemoryAccessRegister(execution);
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterControlSignals() {
        final var expectedRegisterWrite = MainController.RegisterWrite.TRUE;
        final var expectedMemoryToRegister = MainController.MemoryToRegister.FROM_MEMORY;
        final var expectedMemoryRead = MainController.MemoryRead.TRUE;
        final var expectedMemoryWrite = MainController.MemoryWrite.FALSE;
        final var expectedBranch = MainController.Branch.FALSE;
        final var expectedAluResult = 0;

        when(execution.getRegisterWrite()).thenReturn(expectedRegisterWrite);
        when(execution.getMemoryToRegister()).thenReturn(expectedMemoryToRegister);
        when(execution.getMemoryRead()).thenReturn(expectedMemoryRead);
        when(execution.getMemoryWrite()).thenReturn(expectedMemoryWrite);
        when(execution.getBranch()).thenReturn(expectedBranch);
        when(execution.getAluResult()).thenReturn(expectedAluResult);

        exeMem.update();

        assertEquals(expectedRegisterWrite, exeMem.getRegisterWrite());
        assertEquals(expectedMemoryToRegister, exeMem.getMemoryToRegister());
        assertFalse(exeMem.shouldBranch());
        assertEquals(expectedMemoryRead, exeMem.getMemoryRead());
        assertEquals(expectedMemoryWrite, exeMem.getMemoryWrite());
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterProperties() {
        final var expectedBranchResult = 5 * 4 + 12;
        final var expectedAluResult = 6;
        final var expectedRegisterData2 = 7;
        final var expectedWriteRegisterAddress = 2;

        when(execution.getBranchResult()).thenReturn(expectedBranchResult);
        when(execution.getAluResult()).thenReturn(expectedAluResult);
        when(execution.getRegisterData2()).thenReturn(expectedRegisterData2);
        when(execution.getWriteRegisterAddress()).thenReturn(expectedWriteRegisterAddress);

        exeMem.update();

        assertEquals(expectedBranchResult, exeMem.getBranchResult());
        assertEquals(expectedAluResult, exeMem.getAluResult());
        assertEquals(expectedRegisterData2, exeMem.getRegisterData2());
        assertEquals(expectedWriteRegisterAddress, exeMem.getWriteRegisterAddress());
    }
}