package component.pipeline;

import component.Register;
import controller.MainController;
import org.jetbrains.annotations.NotNull;

public class InstructionDecode implements Stage {

    public InstructionDecode(InstructionFetchToInstructionDecodeRegister ifId, Register register) {
    }

    @Override
    public void run() {

    }

    @NotNull
    public MainController.RegisterDestination getRegisterDestination() {
        return null;
    }

    @NotNull
    public MainController.AluOperation getAluOperation() {
        return null;
    }

    @NotNull
    public MainController.AluSource getAluSource() {
        return null;
    }

    @NotNull
    public MainController.Branch getBranch() {
        return null;
    }

    @NotNull
    public MainController.MemoryRead getMemoryRead() {
        return null;
    }

    @NotNull
    public MainController.MemoryWrite getMemoryWrite() {
        return null;
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return null;
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return null;
    }

    public int getProgramCounter() {
        return 0;
    }

    public int getRegisterData1() {
        return 0;
    }

    public int getRegisterData2() {
        return 0;
    }

    public int getImmediate() {
        return 0;
    }

    public int getRt() {
        return 0;
    }

    public int getRd() {
        return 0;
    }
}
