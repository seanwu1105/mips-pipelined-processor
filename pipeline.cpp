#include <iostream>
#include <vector>
#include <cstdlib>
#include <climits>
#include "common.h"

using namespace std;

MEMWB memwbTemp;

void IF(string current_instruction)
{
    if(ifid.Write)
    {
        ifid.PC += 4;
        ifid.instruction = current_instruction;
        if (ifid.instruction != no_instruction)
            isIF = true;
        else
            isIF = false;
    }
}

int ID(bool isIF)
// Return value identify the stall signal
// Parameter check whether the IF stage has been executed
{
    bool isBEQ = false, isBNE = false;
    string op_code, rs, rt, rd, imm;
    op_code.assign(ifid.instruction, 0, 6);
    rs.assign(ifid.instruction, 6, 5);
    rt.assign(ifid.instruction, 11, 5);
    rd.assign(ifid.instruction, 16, 5);
    imm.assign(ifid.instruction, 16, 16);

    /// Control Unit
    if (!isIF)
    {
        // Not yet fetch instruction or no more instruction to fetch (fix the
        // "backward" implementation) => NOP
        idex.control.RegDst = 0;
        idex.control.ALUOp.op1 = 0;
        idex.control.ALUOp.op0 = 0;
        idex.control.ALUSrc = 0;
        idex.control.Branch = 0;
        idex.control.MemRead = 0;
        idex.control.MemWrite = 0;
        idex.control.RegWrite = 0;
        idex.control.MemToReg = 0;
    }
    else if (op_code == "000000") // R-type
    {
        idex.control.RegDst = 1;
        idex.control.ALUOp.op1 = 1;
        idex.control.ALUOp.op0 = 0;
        idex.control.ALUSrc = 0;
        idex.control.Branch = 0;
        idex.control.MemRead = 0;
        idex.control.MemWrite = 0;
        idex.control.RegWrite = 1;
        idex.control.MemToReg = 0;
    }
    else    // I-type
    {
        if (op_code == "001000") // ADDI
        {
            idex.control.RegDst = 0;
            idex.control.ALUOp.op1 = 0;
            idex.control.ALUOp.op0 = 0;
            idex.control.ALUSrc = 1;
            idex.control.Branch = 0;
            idex.control.MemRead = 0;
            idex.control.MemWrite = 0;
            idex.control.RegWrite = 1;
            idex.control.MemToReg = 0;
        }
        else if (op_code == "100011")    // LW
        {
            idex.control.RegDst = 0;
            idex.control.ALUOp.op1 = 0;
            idex.control.ALUOp.op0 = 0;
            idex.control.ALUSrc = 1;
            idex.control.Branch = 0;
            idex.control.MemRead = 1;
            idex.control.MemWrite = 0;
            idex.control.RegWrite = 1;
            idex.control.MemToReg = 1;
        }
        else if (op_code == "101011")    // SW
        {
            idex.control.RegDst = 0; // Don't care default value
            idex.control.ALUOp.op1 = 0;
            idex.control.ALUOp.op0 = 0;
            idex.control.ALUSrc = 1;
            idex.control.Branch = 0;
            idex.control.MemRead = 0;
            idex.control.MemWrite = 1;
            idex.control.RegWrite = 0;
            idex.control.MemToReg = 0; // Don't care default value
        }
        else if (op_code == "001100")   // ANDI
        {
            idex.control.RegDst = 0;
            idex.control.ALUOp.op1 = 1;
            idex.control.ALUOp.op0 = 1;
            idex.control.ALUSrc = 1;
            idex.control.Branch = 0;
            idex.control.MemRead = 0;
            idex.control.MemWrite = 0;
            idex.control.RegWrite = 1;
            idex.control.MemToReg = 0;
        }
        else if (op_code == "000100")    // BEQ
        {
            isBEQ = true;
            idex.control.RegDst = 0; // Don't care default value
            idex.control.ALUOp.op1 = 0;
            idex.control.ALUOp.op0 = 1;
            idex.control.ALUSrc = 0;
            idex.control.Branch = 1;
            idex.control.MemRead = 0;
            idex.control.MemWrite = 0;
            idex.control.RegWrite = 0;
            idex.control.MemToReg = 0; // Don't care default value
        }
        else if (op_code == "000101")   // BNE
        {
            isBNE = true;
            idex.control.RegDst = 0; // Don't care default value
            idex.control.ALUOp.op1 = 0;
            idex.control.ALUOp.op0 = 1;
            idex.control.ALUSrc = 0;
            idex.control.Branch = 1;
            idex.control.MemRead = 0;
            idex.control.MemWrite = 0;
            idex.control.RegWrite = 0;
            idex.control.MemToReg = 0; // Don't care default value
        }
    }

    /// Data Flow
    idex.Rs = (strtol(rs.c_str(), NULL, 2) & INT_MAX);
    idex.Rt = (strtol(rt.c_str(), NULL, 2) & INT_MAX);
    idex.Rd = (strtol(rd.c_str(), NULL, 2) & INT_MAX);
    idex.sign_ext = imm;
    /// #Read Registers
    idex.ReadData1 = registers.at(idex.Rs);
    idex.ReadData2 = registers.at(idex.Rt);

    /// Load-data Hazard Detection
    if (exmem.control.MemRead)  // LW instruction in EXE stage
    {
        if (exmem.RtRd == idex.Rs || exmem.RtRd == idex.Rt)  // Data hazard with LW
        {
            ifid.Write = 0;
            idex.control.RegDst = 0;
            idex.control.ALUOp.op1 = 0;
            idex.control.ALUOp.op0 = 0;
            idex.control.ALUSrc = 0;
            idex.control.Branch = 0;
            idex.control.MemRead = 0;
            idex.control.MemWrite = 0;
            idex.control.RegWrite = 0;
            idex.control.MemToReg = 0;
            return -1;   // Tell main() to stall one CC
        }
    }

    /// Branch Hazard Detection
    if ((isBEQ && idex.ReadData1 == idex.ReadData2)||
            (isBNE && idex.ReadData1 != idex.ReadData2))
    {
        return (strtol(idex.sign_ext.c_str(), NULL, 2) & INT_MAX);
    }
    ifid.Write = 1;
    return 0;
}

void EX(void)
{
    int ALUctr[3], ForwardA[2], ForwardB[2];
    int ALU_a_src = 999, ALU_b_src = 999;
    ALUctr[0] = ALUctr[1] = ALUctr[2] = 999;
    string function_code;
    function_code.assign(idex.sign_ext, 10, 6);

    /// Control Signals
    /// #Forward Unit
    ForwardA[0] = ForwardA[1] = ForwardB[0] = ForwardB[1] = 0;  // default: no hazard
    if (exmem.control.RegWrite && exmem.RtRd && (exmem.RtRd == idex.Rs))    // Rs EX hazard
    {
        ForwardA[0] = 1;
        ForwardA[1] = 0;
    }
    if (exmem.control.RegWrite && exmem.RtRd && (exmem.RtRd == idex.Rt))    // Rt EX hazard
    {
        ForwardB[0] = 1;
        ForwardB[1] = 0;
    }
    if (memwbTemp.control.RegWrite && memwbTemp.RtRd
            && exmem.RtRd != idex.Rs && memwbTemp.RtRd == idex.Rs)  // Rs MEM hazard
    {
        ForwardA[0] = 0;
        ForwardA[1] = 1;
    }
    if (memwbTemp.control.RegWrite && memwbTemp.RtRd
            && exmem.RtRd != idex.Rt && memwbTemp.RtRd == idex.Rt)  // Rt MEM hazard
    {
        ForwardB[0] = 0;
        ForwardB[1] = 1;
    }

    exmem.control.RegWrite = idex.control.RegWrite;
    exmem.control.MemToReg = idex.control.MemToReg;
    exmem.control.Branch = idex.control.Branch;
    exmem.control.MemRead = idex.control.MemRead;
    exmem.control.MemWrite = idex.control.MemWrite;
    /// #ALU Control Unit
    if (idex.control.ALUOp.op1 == 0 && idex.control.ALUOp.op0 == 0)  // I-type Add
    {
        // Add operation
        ALUctr[0] = ALUctr[2] = 0;
        ALUctr[1] = 1;
    }
    else if (idex.control.ALUOp.op1 == 0 && idex.control.ALUOp.op0 == 1) // I-type Subtract
    {
        // Subtract operation
        ALUctr[0] = 0;
        ALUctr[1] = ALUctr[2] = 1;
    }
    else if (idex.control.ALUOp.op1 == 1 && idex.control.ALUOp.op0 == 1)    // I-type And
    {
        // And operation
        ALUctr[0] = ALUctr[1] = ALUctr[2] = 0;
    }
    else if (idex.control.ALUOp.op1 == 1 && idex.control.ALUOp.op0 == 0)  // R-type
    {
        if (function_code == "100000")   // ADD
        {
            // Add operation
            ALUctr[0] = ALUctr[2] = 0;
            ALUctr[1] = 1;
        }
        else if (function_code == "100010")  // SUB
        {
            // Subtract operation
            ALUctr[0] = 0;
            ALUctr[1] = ALUctr[2] = 1;
        }
        else if (function_code == "100100")  // AND
        {
            // And operation
            ALUctr[0] = ALUctr[1] = ALUctr[2] = 0;
        }
        else if (function_code == "100101")  // OR
        {
            // Or operation
            ALUctr[0] = 1;
            ALUctr[1] = ALUctr[2] = 0;
        }
        else if (function_code == "101010")  // SLT
            ALUctr[0] = ALUctr[1] = ALUctr[2] = 1;  // Set on less than operation
    }
    else
    {
        cerr << "Warning: An error occurs in ALU Control Unit." << endl;
        system("pause");
    }

    /// Data Flow
    if (idex.control.RegDst)
        exmem.RtRd = idex.Rd;
    else
        exmem.RtRd = idex.Rt;
    /// #ALU
    /// ##Source
    /// ###Forward Source
    if(ForwardA[0] == 0 && ForwardA[1] == 0)    // Rs no hazard
        ALU_a_src = idex.ReadData1;
    else if (ForwardA[0] == 1 && ForwardA[1] == 0)   // Rs EX data hazard
        ALU_a_src = exmem.ALUout;
    else if (ForwardA[0] == 0 && ForwardA[1] == 1)   // Rs MEM data hazard
        ALU_a_src = (memwbTemp.control.MemToReg ? memwbTemp.ReadData : memwbTemp.ALUout);
    else
    {
        cerr << "Warning: An error occurs in ALU-Source-Forward Source (A)." << endl;
        system("pause");
    }
    if (ForwardB[0] == 0 && ForwardB[1] == 0)    // Rt no hazard
        ALU_b_src = exmem.WriteData = idex.ReadData2;
    else if (ForwardB[0] == 1 && ForwardB[1] == 0)   // Rt EX data hazard
        ALU_b_src = exmem.WriteData = exmem.ALUout;
    else if (ForwardB[0] == 0 && ForwardB[1] == 1)   // Rt MEM data hazard
        ALU_b_src = exmem.WriteData = (memwbTemp.control.MemToReg ? memwbTemp.ReadData : memwbTemp.ALUout);
    else
    {
        cerr << "Warning: An error occurs in ALU-Source-Forward Source (B)." << endl;
        system("pause");
    }
    /// ###Immediate or Registers (Source B)
    if (idex.control.ALUSrc)
        ALU_b_src = (strtol(idex.sign_ext.c_str(), NULL, 2) & INT_MAX);
    /// ##Operation
    if (!ALUctr[2] && ALUctr[1] && !ALUctr[0])   // Add operation
        exmem.ALUout = ALU_a_src + ALU_b_src;
    else if (ALUctr[2] && ALUctr[1] && !ALUctr[0])   // Subtract operation
        exmem.ALUout = ALU_a_src - ALU_b_src;
    else if (!ALUctr[2] && !ALUctr[1] && !ALUctr[0])    // And operation
        exmem.ALUout = ALU_a_src & ALU_b_src;
    else if (!ALUctr[2] && !ALUctr[1] && ALUctr[0])   // Or operation
        exmem.ALUout = ALU_a_src | ALU_b_src;
    else if (ALUctr[2] && ALUctr[1] && ALUctr[0])   // Set on less than operation
        exmem.ALUout = ((ALU_a_src < ALU_b_src) ? 1 : 0);
}

void MEM(void)
{
    /// Control Signals
    memwb.control.RegWrite = exmem.control.RegWrite;
    memwb.control.MemToReg = exmem.control.MemToReg;

    /// Data Flow
    memwb.ALUout = exmem.ALUout;
    memwb.RtRd = exmem.RtRd;
    if (exmem.control.MemWrite)
        memory.at(exmem.ALUout/4) = exmem.WriteData;
    if (exmem.control.MemRead)
        memwb.ReadData = memory.at(exmem.ALUout/4);
    else
        memwb.ReadData = 0; // Don't care default value
}

void WB(void)
{
    if (memwb.control.RegWrite) //R-type or LW
    {
        if (!memwb.control.MemToReg) //R-type
            registers.at(memwb.RtRd) = memwb.ALUout;
        else                        // LW
            registers.at(memwb.RtRd) = memwb.ReadData;
    }
    memwbTemp = memwb;
}
