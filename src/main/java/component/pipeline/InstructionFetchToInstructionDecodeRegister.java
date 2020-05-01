package component.pipeline;

import org.jetbrains.annotations.NotNull;
import signal.Instruction;

public class InstructionFetchToInstructionDecodeRegister implements PipelineRegister {

    @NotNull
    private final InstructionFetch instructionFetch;

    private int programCounter;

    @NotNull
    private Instruction instruction = Instruction.NOP;

    public InstructionFetchToInstructionDecodeRegister(@NotNull InstructionFetch instructionFetch) {
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
