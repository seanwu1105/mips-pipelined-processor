#include <iostream>
#include <iomanip>
#include <fstream>
#include <cstdlib>
#include <climits>
#include <vector>
#include "common.h"

using namespace std;

bool isIF;
static size_t cc;
vector<string> instruction_set;
vector<int> registers(10);
vector<int> memory(5);
string no_instruction = "00000000000000000000000000000000";
static fstream fptr_out;
IFID ifid;
IDEX idex;
EXMEM exmem;
MEMWB memwb;

void StatusOutput(void);

int main()
{
    string input_filename[4] = {"General.txt", "Datahazard.txt", "Lwhazard.txt", "Branchhazard.txt"};
    string output_filename[4] = {"genResult.txt", "dataResult.txt", "loadResult.txt", "branchResult.txt"};
    for(size_t file_number = 0; file_number < 4; file_number++)
    {
        isIF = false;
        cc = 1;
        /// Registers and Data Memory Initialization
        registers.at(0) = 0;
        registers.at(1) = 9;
        registers.at(2) = 8;
        registers.at(3) = 7;
        registers.at(4) = 1;
        registers.at(5) = 2;
        registers.at(6) = 3;
        registers.at(7) = 4;
        registers.at(8) = 5;
        registers.at(9) = 6;
        memory.at(0) = 5;
        memory.at(1) = 9;
        memory.at(2) = 4;
        memory.at(3) = 8;
        memory.at(4) = 7;

        /// Data Flow Initialization
        ifid.PC = 0;
        ifid.Write = 1;
        ifid.instruction = idex.sign_ext = "00000000000000000000000000000000";
        idex.ReadData1 = idex.ReadData2 = 0;
        idex.Rs = idex.Rt = idex.Rd = idex.control.RegDst = 0;
        idex.control.ALUOp.op1 = idex.control.ALUOp.op0 = idex.control.ALUSrc = idex.control.Branch = 0;
        idex.control.MemRead = idex.control.MemWrite = idex.control.RegWrite = idex.control.MemToReg = 0;
        exmem.ALUout = exmem.WriteData = 0;
        exmem.RtRd = exmem.control.Branch = exmem.control.MemRead = exmem.control.MemWrite = 0;
        exmem.control.RegWrite = exmem.control.MemToReg = 0;
        memwb.ReadData = memwb.ALUout = 0;
        memwb.control.RegWrite = memwb.control.MemToReg = 0;

        /// Read Input File
        instruction_set.clear();
        fstream fptr_in(input_filename[file_number].c_str());
        if (fptr_in)
        {
            string temp_string;
            while (fptr_in >> temp_string)
                instruction_set.push_back(temp_string);

            fptr_out.open(output_filename[file_number].c_str(), ios::out | ios::trunc);
            long int hazardSignal = 0;  // 0 means no hazard; -1 means LW data hazard
            for (size_t i = 0; i < (instruction_set.size() + 4); i++)
            {
                /** The advantage of simulating the pipeline stages "backwards"
                *** (e.g. in reverse order like write_back, memory, ALU, register,
                *** decode, fetch) is that each stage can read the variables
                *** that represent the input latches and then simply overwrite
                *** the variables that represent the output latches. This will
                *** not work if you simulate the pipeline "forward" because each
                *** stage would overwrite the input of the following stage and
                *** the original input would be lost.
                **/
                WB();
                MEM();
                EX();
                hazardSignal = ID(isIF);
                if (i < instruction_set.size())  // new instruction input
                    IF(instruction_set.at(i));
                else                            // no instruction, Run till complete the last instruction
                    IF(no_instruction);
                if (hazardSignal == -1) i--; //LW-hazard, need to stall (frozen) PC for one cycle
                StatusOutput();
                if (hazardSignal > 0)   // Branch hazard
                {
                    // Give no_operation to ID/EX
                    ifid.instruction = no_instruction;
                    isIF = false;
                    i += (static_cast<size_t>(hazardSignal) - 1);   // -1 to cancel for-loop i++
                    ifid.PC += ((static_cast<unsigned int>(hazardSignal) - 1) * 4);   // -1 to cancel for-loop i++
                }
            }
            fptr_out.close();
        }
        else
            cerr << "Input file open failed." << endl;
        fptr_in.close();
    }
    return 0;
}

void StatusOutput(void)
{
    /// Screen output
    cout << "CC" << cc << ":" << endl << endl << "Registers:" << endl;
    for (size_t i = 0; i < 10; i++)
        cout << '$' << i << ": " << registers.at(i) << endl;
    cout << endl << "Data memory:" << endl;
    for (size_t i = 0; i < 5; i++)
        cout << "0x" << setfill('0') << setw(2) << hex << uppercase << i * 4 << ": " << memory.at(i) << endl;
    cout << dec << endl << "IF/ID :" << endl << "PC\t\t" << ifid.PC << endl
         << "Instruction\t" << ifid.instruction << endl << endl
         << "ID/EX :" << endl << "ReadData1\t" << idex.ReadData1 << endl
         << "ReadData2\t" << idex.ReadData2 << endl << "sign_ext\t" << strtol(idex.sign_ext.c_str(), NULL, 2) << endl
         << "Rs\t\t" << idex.Rs << endl << "Rt\t\t" << idex.Rt << endl << "Rd\t\t" << idex.Rd << endl
         << "Control Signals\t" << idex.control.RegDst << idex.control.ALUOp.op1
         << idex.control.ALUOp.op0 << idex.control.ALUSrc << idex.control.Branch << idex.control.MemRead
         << idex.control.MemWrite << idex.control.RegWrite << idex.control.MemToReg << endl << endl
         << "EX/MEM :" << endl << "ALUout\t\t" << exmem.ALUout << endl << "WriteData\t"
         << exmem.WriteData << endl << "Rt/Rd\t\t" << exmem.RtRd << endl << "Control Signals\t"
         << exmem.control.Branch << exmem.control.MemRead << exmem.control.MemWrite << exmem.control.RegWrite
         << exmem.control.MemToReg << endl << endl << "MEM/WB :" << endl << "ReadData\t" << memwb.ReadData
         << endl << "ALUout\t\t" << memwb.ALUout << endl << "Rt/Rd\t\t" << memwb.RtRd << endl
         << "Control Signals\t" << memwb.control.RegWrite << memwb.control.MemToReg << endl
         << "=================================================================" << endl;

    /// File output
    fptr_out <<  "CC" << cc << ":" << endl << endl << "Registers:" << endl;
    for (size_t i = 0; i < 10; i++)
        fptr_out << '$' << i << ": " << registers.at(i) << endl;
    fptr_out << endl << "Data memory:" << endl;
    for (size_t i = 0; i < 5; i++)
        fptr_out << "0x" << setfill('0') << setw(2) << hex << uppercase << i * 4 << ": " << memory.at(i) << endl;
    fptr_out << dec << endl << "IF/ID :" << endl << "PC\t\t" << ifid.PC << endl
             << "Instruction\t" << ifid.instruction << endl << endl
             << "ID/EX :" << endl << "ReadData1\t" << idex.ReadData1 << endl
             << "ReadData2\t" << idex.ReadData2 << endl << "sign_ext\t" << strtol(idex.sign_ext.c_str(), NULL, 2) << endl
             << "Rs\t\t" << idex.Rs << endl << "Rt\t\t" << idex.Rt << endl << "Rd\t\t" << idex.Rd << endl
             << "Control Signals\t" << idex.control.RegDst << idex.control.ALUOp.op1
             << idex.control.ALUOp.op0 << idex.control.ALUSrc << idex.control.Branch << idex.control.MemRead
             << idex.control.MemWrite << idex.control.RegWrite << idex.control.MemToReg << endl << endl
             << "EX/MEM :" << endl << "ALUout\t\t" << exmem.ALUout << endl << "WriteData\t"
             << exmem.WriteData << endl << "Rt/Rd\t\t" << exmem.RtRd << endl << "Control Signals\t"
             << exmem.control.Branch << exmem.control.MemRead << exmem.control.MemWrite << exmem.control.RegWrite
             << exmem.control.MemToReg << endl << endl << "MEM/WB :" << endl << "ReadData\t" << memwb.ReadData
             << endl << "ALUout\t\t" << memwb.ALUout << endl << "Rt/Rd\t\t" << memwb.RtRd << endl
             << "Control Signals\t" << memwb.control.RegWrite << memwb.control.MemToReg << endl
             << "=================================================================" << endl;
    cc++;
}
