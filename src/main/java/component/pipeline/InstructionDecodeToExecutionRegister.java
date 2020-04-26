package component.pipeline;

import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InstructionDecodeToExecutionRegister implements PipelineRegister {

    @NotNull
    private final InstructionDecode instructionDecode;

    @Nullable
    private MainController.RegisterDestination registerDestination;

    @Nullable
    private MainController.AluOperation aluOperation;

    @Nullable
    private MainController.AluSource aluSource;

    @Nullable
    private MainController.Branch branch;

    @Nullable
    private MainController.MemoryRead memoryRead;

    @Nullable
    private MainController.MemoryWrite memoryWrite;

    @Nullable
    private MainController.RegisterWrite registerWrite;

    @Nullable
    private MainController.MemoryToRegister memoryToRegister;

    public InstructionDecodeToExecutionRegister(@NotNull InstructionDecode instructionDecode) {
        this.instructionDecode = instructionDecode;
    }

    @Override
    public void update() {
        registerDestination = instructionDecode.getRegisterDestination();
        aluOperation = instructionDecode.getAluOperation();
        aluSource = instructionDecode.getAluSource();
        branch = instructionDecode.getBranch();
        memoryRead = instructionDecode.getMemoryRead();
        memoryWrite = instructionDecode.getMemoryWrite();
        registerWrite = instructionDecode.getRegisterWrite();
        memoryToRegister = instructionDecode.getMemoryToRegister();
    }

    @Nullable
    public MainController.RegisterDestination getRegisterDestination() {
        return registerDestination;
    }

    @Nullable
    public MainController.AluOperation getAluOperation() {
        return aluOperation;
    }

    @Nullable
    public MainController.AluSource getAluSource() {
        return aluSource;
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

    @Nullable
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @Nullable
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }
}
