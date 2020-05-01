package component.pipeline;

import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecutionToMemoryAccessRegister implements PipelineRegister {

    @NotNull
    private final Execution execution;

    @Nullable
    private MainController.RegisterWrite registerWrite;

    @Nullable
    private MainController.MemoryToRegister memoryToRegister;

    @Nullable
    private MainController.Branch branch;

    @Nullable
    private MainController.MemoryRead memoryRead;

    @Nullable
    private MainController.MemoryWrite memoryWrite;

    private int branchResult, aluResult, registerData2, writeRegisterAddress;

    public ExecutionToMemoryAccessRegister(@NotNull Execution execution) {
        this.execution = execution;
    }

    @Nullable
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @Nullable
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }

    public boolean shouldBranch() {
        return branch == MainController.Branch.TRUE && getAluResult() == 0;
    }

    @Nullable
    public MainController.MemoryRead getMemoryRead() {
        return memoryRead;
    }

    @Nullable
    public MainController.MemoryWrite getMemoryWrite() {
        return memoryWrite;
    }

    public int getBranchResult() {
        return branchResult;
    }

    public int getAluResult() {
        return aluResult;
    }

    public int getRegisterData2() {
        return registerData2;
    }

    public int getWriteRegisterAddress() {
        return writeRegisterAddress;
    }

    @Override
    public void update() {
        registerWrite = execution.getRegisterWrite();
        memoryToRegister = execution.getMemoryToRegister();
        branch = execution.getBranch();
        memoryRead = execution.getMemoryRead();
        memoryWrite = execution.getMemoryWrite();
        branchResult = execution.getBranchResult();
        aluResult = execution.getAluResult();
        registerData2 = execution.getRegisterData2();
        writeRegisterAddress = execution.getWriteRegisterAddress();
    }
}
