package io.github.seanwu1105.mipsprocessor.component;

import io.github.seanwu1105.mipsprocessor.component.pipeline.ExecutionToMemoryAccessRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionFetchToInstructionDecodeRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.MemoryAccessToWriteBackRegister;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HazardDetectionUnitTest {

    @NotNull
    private InstructionFetchToInstructionDecodeRegister ifId;
    @NotNull
    private InstructionDecodeToExecutionRegister idExe;
    @NotNull
    private ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private MemoryAccessToWriteBackRegister memWb;
    @NotNull
    private HazardDetectionUnit hazardDetectionUnit;

    @BeforeEach
    void buildUp() {
        ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        idExe = mock(InstructionDecodeToExecutionRegister.class);
        exeMem = mock(ExecutionToMemoryAccessRegister.class);
        memWb = mock(MemoryAccessToWriteBackRegister.class);
        hazardDetectionUnit = new HazardDetectionUnit(ifId, idExe, exeMem, memWb);
    }

    @Test
    void testMustStallWithLoadWord() {
        final var instruction = new Instruction("000000 00100 00010 00001 00000 100000"); // add $1, $4, $2
        when(ifId.getInstruction()).thenReturn(instruction);
        when(idExe.getMemoryRead()).thenReturn(MainController.MemoryRead.TRUE);
        when(exeMem.getWriteRegisterAddress()).thenReturn(9);
        when(memWb.getWriteRegisterAddress()).thenReturn(9);

        when(idExe.getRt()).thenReturn(instruction.getRs());
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getRt()).thenReturn(instruction.getRt());
        assertTrue(hazardDetectionUnit.mustStall());
    }

    @Test
    void testMustStallWithBranch() {
        final var instruction = new Instruction("000100 00001 00010 0000000000000001"); // beq $1, $2, 1
        when(ifId.getInstruction()).thenReturn(instruction);

        when(idExe.getWriteRegisterAddress()).thenReturn(instruction.getRs());
        when(exeMem.getWriteRegisterAddress()).thenReturn(9);
        when(memWb.getWriteRegisterAddress()).thenReturn(9);
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getWriteRegisterAddress()).thenReturn(instruction.getRt());
        when(exeMem.getWriteRegisterAddress()).thenReturn(9);
        when(memWb.getWriteRegisterAddress()).thenReturn(9);
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getWriteRegisterAddress()).thenReturn(9);
        when(exeMem.getWriteRegisterAddress()).thenReturn(instruction.getRs());
        when(memWb.getWriteRegisterAddress()).thenReturn(9);
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getWriteRegisterAddress()).thenReturn(9);
        when(exeMem.getWriteRegisterAddress()).thenReturn(instruction.getRt());
        when(memWb.getWriteRegisterAddress()).thenReturn(9);
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getWriteRegisterAddress()).thenReturn(9);
        when(exeMem.getWriteRegisterAddress()).thenReturn(9);
        when(memWb.getWriteRegisterAddress()).thenReturn(instruction.getRs());
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getWriteRegisterAddress()).thenReturn(9);
        when(exeMem.getWriteRegisterAddress()).thenReturn(9);
        when(memWb.getWriteRegisterAddress()).thenReturn(instruction.getRt());
        assertTrue(hazardDetectionUnit.mustStall());

        when(idExe.getWriteRegisterAddress()).thenReturn(9);
        when(exeMem.getWriteRegisterAddress()).thenReturn(9);
        when(memWb.getWriteRegisterAddress()).thenReturn(9);
        assertFalse(hazardDetectionUnit.mustStall());
    }
}