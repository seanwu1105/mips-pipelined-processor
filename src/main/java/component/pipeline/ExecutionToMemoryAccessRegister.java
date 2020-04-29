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

    @Nullable
    public MainController.Branch getBranch() {
        return branch;
    }

    @Nullable
    public MainController.MemoryRead getMemoryRead() {
        return memoryRead;
    }

    @Nullable
    public MainController.MemoryWrite getMemoryWrite() {
        return memoryWrite;
    }

    @Override
    public void update() {
        registerWrite = execution.getRegisterWrite();
        memoryToRegister = execution.getMemoryToRegister();
        branch = execution.getBranch();
        memoryRead = execution.getMemoryRead();
        memoryWrite = execution.getMemoryWrite();
    }
}
