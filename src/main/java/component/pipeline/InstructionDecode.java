package component.pipeline;

import component.Register;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import signal.Instruction;

import java.util.Objects;

public class InstructionDecode implements Stage {

    @NotNull
    private final InstructionFetchToInstructionDecodeRegister ifId;

    @NotNull
    private final MainController mainController;

    @NotNull
    private final Register register;

    private int programCounter;

    @Nullable
    private Instruction currentInstruction;

    public InstructionDecode(
            @NotNull InstructionFetchToInstructionDecodeRegister ifId,
            @NotNull MainController mainController,
            @NotNull Register register
    ) {
        this.ifId = ifId;
        this.mainController = mainController;
        this.register = register;
    }

    @Override
    public void run() {
        currentInstruction = ifId.getInstruction();
        mainController.setInstruction(currentInstruction);
        programCounter = ifId.getProgramCounter();
    }

    @NotNull
    public MainController.RegisterDestination getRegisterDestination() {
        return mainController.getRegisterDestination();
    }

    @NotNull
    public MainController.AluOperation getAluOperation() {
        return mainController.getAluOperation();
    }

    @NotNull
    public MainController.AluSource getAluSource() {
        return mainController.getAluSource();
    }

    @NotNull
    public MainController.Branch getBranch() {
        return mainController.getBranch();
    }

    @NotNull
    public MainController.MemoryRead getMemoryRead() {
        return mainController.getMemoryRead();
    }

    @NotNull
    public MainController.MemoryWrite getMemoryWrite() {
        return mainController.getMemoryWrite();
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return mainController.getRegisterWrite();
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return mainController.getMemoryToRegister();
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getRegisterData1() {
        register.setReadAddress1(Objects.requireNonNull(currentInstruction).getRs());
        return register.readData1();
    }

    public int getRegisterData2() {
        register.setReadAddress2(Objects.requireNonNull(currentInstruction).getRt());
        return register.readData2();
    }

    public int getImmediate() {
        return Objects.requireNonNull(currentInstruction).getImmediate();
    }

    public int getRt() {
        return Objects.requireNonNull(currentInstruction).getRt();
    }

    public int getRd() {
        return Objects.requireNonNull(currentInstruction).getRd();
    }
}
