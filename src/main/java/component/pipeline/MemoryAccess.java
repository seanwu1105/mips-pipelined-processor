package component.pipeline;

import controller.MainController;

public class MemoryAccess implements Stage {

    public MemoryAccess(ExecutionToMemoryAccessRegister exMem) {
    }

    @Override
    public void run() {

    }

    public MainController.RegisterWrite getRegisterWrite() {
        return null;
    }

    public MainController.MemoryToRegister getMemoryToRegister() {
        return null;
    }

    public int getMemoryReadData() {
        return 0;
    }

    public int getAluResult() {
        return 0;
    }

    public int getWriteRegisterAddress() {
        return 0;
    }
}
