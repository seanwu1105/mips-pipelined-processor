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
    private Instruction currentInstruction;

    public InstructionFetch(@NotNull Memory instructionMemory) {
        this.instructionMemory = instructionMemory;
        this.instructionMemory.setMemoryRead(MainController.MemoryRead.TRUE);
        this.instructionMemory.setAddress(programCounter);
        currentInstruction = this.instructionMemory.readInstruction();
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
        currentInstruction = instructionMemory.readInstruction();

        if (exeMem != null && exeMem.shouldBranch())
            programCounter = exeMem.getBranchResult();
        else
            programCounter += 4;
    }
}
