package de.exware.modbus;

enum FunctionCode
{
    HoldingRegister(3)
    , WriteMultipleRegister(16);

    private int code;

    FunctionCode(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}