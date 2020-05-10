package io.github.seanwu1105.mipsprocessor.component.pipeline;

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
    @Nullable
    private ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private Instruction currentInstruction = Instruction.NOP;

    public InstructionFetch(@NotNull final Memory instructionMemory) {
        this.instructionMemory = instructionMemory;
        this.instructionMemory.setMemoryRead(MainController.MemoryRead.TRUE);
    }

    public void setExecutionToMemoryAccessRegister(@NotNull final ExecutionToMemoryAccessRegister exeMem) {
        this.exeMem = exeMem;
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
        instructionMemory.setAddress(programCounter.getCounter());
        updateCurrentInstruction();

        if (exeMem != null && exeMem.shouldBranch())
            programCounter.setCounter(exeMem.getBranchResult());
        else
            programCounter.setCounter(programCounter.getCounter() + 4);
    }

    private void updateCurrentInstruction() {
        try {
            currentInstruction = instructionMemory.readInstruction();
        } catch (final NullPointerException e) {
            currentInstruction = Instruction.NOP;
        }
    }

    @Override
    public boolean hasInstruction() {
        return currentInstruction != Instruction.NOP;
    }
}
