package component.pipeline;

import component.Memory;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import signal.Instruction;

public class InstructionFetch implements Stage {

    @NotNull
    final private Memory instructionMemory;

    @Nullable
    private ExecutionToMemoryAccessRegister exeMem;

    private int programCounter = 0;

    @NotNull
    private Instruction currentInstruction = Instruction.NOP;

    public InstructionFetch(@NotNull Memory instructionMemory) {
        this.instructionMemory = instructionMemory;
        this.instructionMemory.setMemoryRead(MainController.MemoryRead.TRUE);
    }

    public void setExecutionToMemoryAccessRegister(@NotNull ExecutionToMemoryAccessRegister exeMem) {
        this.exeMem = exeMem;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    @NotNull
    public Instruction getInstruction() {
        return currentInstruction;
    }

    @Override
    public void run() {
        instructionMemory.setAddress(programCounter);
        updateCurrentInstruction();

        if (exeMem != null && exeMem.shouldBranch())
            programCounter = exeMem.getBranchResult();
        else
            programCounter += 4;
    }

    private void updateCurrentInstruction() {
        try {
            currentInstruction = instructionMemory.readInstruction();
        } catch (NullPointerException e) {
            currentInstruction = Instruction.NOP;
        }
    }

    @Override
    public boolean hasInstruction() {
        return currentInstruction != Instruction.NOP;
    }
}
