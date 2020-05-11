package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.HazardDetectionUnit;
import io.github.seanwu1105.mipsprocessor.component.Memory;
import io.github.seanwu1105.mipsprocessor.component.ProgramCounter;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InstructionFetch implements Stage {

    @NotNull
    private final Memory instructionMemory;
    @NotNull
    private final ProgramCounter programCounter = new ProgramCounter();
    @NotNull
    private Instruction currentInstruction = Instruction.NOP;
    @Nullable
    private HazardDetectionUnit hazardDetectionUnit;

    public InstructionFetch(@NotNull final Memory instructionMemory) {
        this.instructionMemory = instructionMemory;
        this.instructionMemory.setMemoryRead(MainController.MemoryRead.TRUE);
    }

    public void setHazardDetectionUnit(@NotNull final HazardDetectionUnit hazardDetectionUnit) {
        this.hazardDetectionUnit = hazardDetectionUnit;
    }

    public int getProgramCounter() {
        return programCounter.getCounter();
    }

    @NotNull
    public Instruction getInstruction() {
        return currentInstruction;
    }

    @Override
    public void run() {
        updateCurrentInstruction();

        assert hazardDetectionUnit != null;
        if (!hazardDetectionUnit.needStalling()) {
            programCounter.setCounter(programCounter.getCounter() + 4);
        }
    }

    private void updateCurrentInstruction() {
        instructionMemory.setAddress(programCounter.getCounter());
        currentInstruction = instructionMemory.readInstruction();
    }

    @Override
    public boolean hasInstruction() {
        return currentInstruction != Instruction.NOP;
    }
}
