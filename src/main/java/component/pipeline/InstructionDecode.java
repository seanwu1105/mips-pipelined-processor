package component.pipeline;

import controller.MainController;

public class InstructionDecode implements Stage {

    public InstructionDecode(InstructionFetchToInstructionDecodeRegister ifId) {
    }

    @Override
    public void run() {

    }

    public MainController.RegisterDestination getRegisterDestination() {
        return null;
    }

    public MainController.AluOperation getAluOperation() {
        return null;
    }

    public MainController.AluSource getAluSource() {
        return null;
    }

    public MainController.Branch getBranch() {
        return null;
    }

    public MainController.MemoryRead getMemoryRead() {
        return null;
    }

    public MainController.MemoryWrite getMemoryWrite() {
        return null;
    }

    public MainController.RegisterWrite getRegisterWrite() {
        return null;
    }

    public MainController.MemoryToRegister getMemoryToRegister() {
        return null;
    }
}
