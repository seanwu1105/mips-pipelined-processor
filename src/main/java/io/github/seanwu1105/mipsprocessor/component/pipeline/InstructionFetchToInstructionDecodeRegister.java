package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.HazardDetectionUnit;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InstructionFetchToInstructionDecodeRegister implements PipelineRegister {

    @NotNull
    private final InstructionFetch instructionFetch;
    @Nullable
    private HazardDetectionUnit hazardDetectionUnit;

    private int programCounter;

    @NotNull
    private Instruction instruction = Instruction.NOP;

    public InstructionFetchToInstructionDecodeRegister(@NotNull final InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
    }

    public void setHazardDetectionUnit(@NotNull final HazardDetectionUnit hazardDetectionUnit) {
        this.hazardDetectionUnit = hazardDetectionUnit;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    @NotNull
    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public void update() {
        programCounter = instructionFetch.getProgramCounter();

        assert hazardDetectionUnit != null;
        if (!hazardDetectionUnit.needStalling())
            instruction = instructionFetch.getInstruction();
    }
}
