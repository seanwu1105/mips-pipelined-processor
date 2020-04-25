package component.pipeline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import signal.Instruction;

public class InstructionFetchToInstructionDecodeRegister implements PipelineRegister {

    @NotNull
    private final InstructionFetch instructionFetch;

    private int newProgramCounter;

    @Nullable
    private Instruction instruction;

    public InstructionFetchToInstructionDecodeRegister(@NotNull InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
    }

    public int getNewProgramCounter() {
        return newProgramCounter;
    }

    public @Nullable Instruction getInstruction() {
        return instruction;
    }

    @Override
    public void update() {
        newProgramCounter = instructionFetch.getNewProgramCounter();
        instruction = instructionFetch.getInstruction();
    }
}
