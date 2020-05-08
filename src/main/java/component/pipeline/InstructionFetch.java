package component.pipeline;

import component.Memory;
import component.ProgramCounter;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import signal.Instruction;

public class InstructionFetch implements Stage {

    @NotNull
    final private Memory instructionMemory;
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
