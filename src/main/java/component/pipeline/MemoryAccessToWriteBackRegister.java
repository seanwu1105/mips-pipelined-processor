package component.pipeline;

import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MemoryAccessToWriteBackRegister implements PipelineRegister {

    @NotNull
    private final MemoryAccess memoryAccess;

    @Nullable
    private MainController.RegisterWrite registerWrite;

    @Nullable
    private MainController.MemoryToRegister memoryToRegister;

    public MemoryAccessToWriteBackRegister(@NotNull MemoryAccess memoryAccess) {
        this.memoryAccess = memoryAccess;
    }

    @Override
    public void update() {
        registerWrite = memoryAccess.getRegisterWrite();
        memoryToRegister = memoryAccess.getMemoryToRegister();
    }

    @Nullable
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @Nullable
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }
}
