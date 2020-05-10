package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;

public class InstructionFetchToInstructionDecodeRegister implements PipelineRegister {

    @NotNull
    private final InstructionFetch instructionFetch;

    private int programCounter;

    @NotNull
    private Instruction instruction = Instruction.NOP;

    public InstructionFetchToInstructionDecodeRegister(@NotNull final InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
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
        instruction = instructionFetch.getInstruction();
    }
}
