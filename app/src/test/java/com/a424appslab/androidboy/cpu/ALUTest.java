package com.a424appslab.androidboy.cpu;

import com.a424appslab.androidboy.cpu.register.Reg8Bit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by shyri on 08/08/17.
 */

public class ALUTest {

    CPU cpu = new CPU();

    ALU alu;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cpu.clearFlags();
        alu = new ALU(cpu);
    }

    @Test
    public void incSimple() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0x10);
        alu.inc(reg);

        assertEquals(0x11, reg.getValue());
    }

    @Test
    public void incZeroFlag() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0xFF);
        alu.inc(reg);

        assertEquals(0x00, reg.getValue());

        assertTrue(cpu.isFlagSet(CPU.FLAG_ZERO));

        alu.inc(reg);
        assertFalse(cpu.isFlagSet(CPU.FLAG_ZERO));
    }

    @Test
    public void incResetNegFlag() {
        cpu.setFlag(CPU.FLAG_NEGATIVE);

        Reg8Bit reg = new Reg8Bit();
        alu.inc(reg);

        assertFalse(cpu.isFlagSet(CPU.FLAG_NEGATIVE));

        alu.inc(reg);

        assertFalse(cpu.isFlagSet(CPU.FLAG_NEGATIVE));
    }

    @Test
    public void incCarryFlag() {
        cpu.setFlag(CPU.FLAG_CARRY);

        Reg8Bit reg = new Reg8Bit();

        alu.inc(reg);
        assertTrue(cpu.isFlagSet(CPU.FLAG_CARRY));
        alu.inc(reg);
        assertTrue(cpu.isFlagSet(CPU.FLAG_CARRY));

        cpu.resetFlag(CPU.FLAG_CARRY);

        alu.inc(reg);
        assertFalse(cpu.isFlagSet(CPU.FLAG_CARRY));
        alu.inc(reg);
        assertFalse(cpu.isFlagSet(CPU.FLAG_CARRY));
    }

    @Test
    public void incHalfFlag() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0x0F);

        alu.inc(reg);

        assertEquals(0x10, reg.getValue());
        assertTrue(cpu.isFlagSet(CPU.FLAG_HALF));
    }

    @Test
    public void decSimple() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0x02);

        alu.dec(reg);

        assertEquals(0x01, reg.getValue());
    }

    @Test
    public void decZeroFlag() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0x01);

        alu.dec(reg);

        assertEquals(0x00, reg.getValue());
        assertTrue(cpu.isFlagSet(CPU.FLAG_ZERO));

        alu.dec(reg);
        assertEquals((byte) 0xFF, reg.getValue());
        assertFalse(cpu.isFlagSet(CPU.FLAG_ZERO));
    }

    @Test
    public void decNegFlag() {
        Reg8Bit reg = new Reg8Bit();
        alu.dec(reg);

        assertTrue(cpu.isFlagSet(CPU.FLAG_NEGATIVE));

        alu.dec(reg);

        assertTrue(cpu.isFlagSet(CPU.FLAG_NEGATIVE));
    }

    @Test
    public void decCarryFlag() {
        cpu.setFlag(CPU.FLAG_CARRY);

        Reg8Bit reg = new Reg8Bit();

        alu.dec(reg);
        assertTrue(cpu.isFlagSet(CPU.FLAG_CARRY));
        alu.dec(reg);
        assertTrue(cpu.isFlagSet(CPU.FLAG_CARRY));

        cpu.resetFlag(CPU.FLAG_CARRY);

        alu.dec(reg);
        assertFalse(cpu.isFlagSet(CPU.FLAG_CARRY));
        alu.dec(reg);
        assertFalse(cpu.isFlagSet(CPU.FLAG_CARRY));
    }

    @Test
    public void decHalfFlag() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0x0F);

        alu.dec(reg);

        assertEquals(0x0E, reg.getValue());
        assertFalse(cpu.isFlagSet(CPU.FLAG_HALF));

        reg.setValue((byte) 0x10);

        alu.dec(reg);

        assertEquals(0x0F, reg.getValue());
        assertTrue(cpu.isFlagSet(CPU.FLAG_HALF));

        reg.setValue((byte) 0xFF);

        alu.dec(reg);

        assertEquals((byte) 0xFE, reg.getValue());
        assertFalse(cpu.isFlagSet(CPU.FLAG_HALF));

        reg.setValue((byte) 0xF0);

        alu.dec(reg);

        assertEquals((byte) 0xEF, reg.getValue());
        assertTrue(cpu.isFlagSet(CPU.FLAG_HALF));
    }

    @Test
    public void addSimple() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0x02);

        alu.add(reg, (byte) 0x0F);
        assertEquals(0x11, reg.getValue());
    }

    @Test
    public void addZeroFlag() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0xF0);

        alu.add(reg, (byte) 0x10);

        assertEquals(0x00, reg.getValue());

        assertTrue(cpu.isFlagSet(CPU.FLAG_ZERO));

        alu.add(reg, (byte) 0x1A);
        assertFalse(cpu.isFlagSet(CPU.FLAG_ZERO));
    }

    @Test
    public void addResetNegFlag() {
        cpu.setFlag(CPU.FLAG_NEGATIVE);

        Reg8Bit reg = new Reg8Bit();
        alu.add(reg, (byte) 0x10);

        assertFalse(cpu.isFlagSet(CPU.FLAG_NEGATIVE));

        alu.add(reg, (byte) 0x10);

        assertFalse(cpu.isFlagSet(CPU.FLAG_NEGATIVE));
    }

    @Test
    public void addCarryFlag() {
        Reg8Bit reg = new Reg8Bit();
        reg.setValue((byte) 0xF0);

        alu.add(reg, (byte) 0xF0);

        assertTrue(cpu.isFlagSet(CPU.FLAG_CARRY));

        alu.add(reg, (byte) 0x01);
        assertFalse(cpu.isFlagSet(CPU.FLAG_CARRY));
    }
}
