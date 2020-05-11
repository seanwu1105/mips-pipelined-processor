package io.github.seanwu1105.mipsprocessor.component;

import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionFetchToInstructionDecodeRegister;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HazardDetectionUnitTest {

    @NotNull
    private final InstructionFetchToInstructionDecodeRegister ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
    @NotNull
    private final InstructionDecodeToExecutionRegister idExe = mock(InstructionDecodeToExecutionRegister.class);
    @NotNull
    private HazardDetectionUnit hazardDetectionUnit;

    @BeforeEach
    void buildUp() {
        hazardDetectionUnit = new HazardDetectionUnit(ifId, idExe);
    }

    @Test
    void testNeedStalling() {
        when(idExe.getMemoryRead()).thenReturn(MainController.MemoryRead.TRUE);

        when(ifId.getInstruction()).thenReturn(new Instruction("000000 00100 00010 00001 00000 100000")); // add $1, $4, $2
        when(idExe.getRt()).thenReturn(4);
        assertTrue(hazardDetectionUnit.needStalling());

        when(ifId.getInstruction()).thenReturn(new Instruction("000000 00100 00010 00001 00000 100000")); // add $1, $4, $2
        when(idExe.getRt()).thenReturn(2);
        assertTrue(hazardDetectionUnit.needStalling());
    }
}