package component.pipeline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import signal.Instruction;

public class InstructionFetchToInstructionDecodeRegister implements PipelineRegister {

    @NotNull
    private final InstructionFetch instructionFetch;

    private int programCounter;

    @Nullable
    private Instruction instruction;

    public InstructionFetchToInstructionDecodeRegister(@NotNull InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    @Nullable
    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public void update() {
        programCounter = instructionFetch.getProgramCounter();
        instruction = instructionFetch.getInstruction();
    }
}
