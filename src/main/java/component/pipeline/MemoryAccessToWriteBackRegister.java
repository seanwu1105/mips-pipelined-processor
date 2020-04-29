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

    private int memoryReadData, aluResult, writeRegisterAddress;

    public MemoryAccessToWriteBackRegister(@NotNull MemoryAccess memoryAccess) {
        this.memoryAccess = memoryAccess;
    }

    @Override
    public void update() {
        registerWrite = memoryAccess.getRegisterWrite();
        memoryToRegister = memoryAccess.getMemoryToRegister();
        memoryReadData = memoryAccess.getMemoryReadData();
        aluResult = memoryAccess.getAluResult();
        writeRegisterAddress = memoryAccess.getWriteRegisterAddress();
    }

    @Nullable
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @Nullable
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }

    public int getMemoryReadData() {
        return memoryReadData;
    }

    public int getAluResult() {
        return aluResult;
    }

    public int getWriteRegisterAddress() {
        return writeRegisterAddress;
    }
}
