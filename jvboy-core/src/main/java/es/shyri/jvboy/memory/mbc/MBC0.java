package es.shyri.jvboy.memory.mbc;

public class MBC0 extends MBC {
    private byte[] rom;

    public MBC0(byte[] rom) {
        this.rom = rom;
    }

    @Override
    public byte read(int address) {
        return rom[address];
    }

    @Override
    public void write(int address, int value) {
        new IllegalAccessError("Trying to write to ROM... " + address + " bad boy ").printStackTrace();
    }
}
