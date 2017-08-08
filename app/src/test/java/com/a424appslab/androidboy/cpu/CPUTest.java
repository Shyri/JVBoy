package com.a424appslab.androidboy.cpu;

import org.junit.Before;
import org.junit.Test;

import static com.a424appslab.androidboy.cpu.CPU.FLAG_CARRY;
import static com.a424appslab.androidboy.cpu.CPU.FLAG_HALF;
import static com.a424appslab.androidboy.cpu.CPU.FLAG_NEGATIVE;
import static com.a424appslab.androidboy.cpu.CPU.FLAG_ZERO;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by shyri on 08/08/17.
 */

public class CPUTest {
    CPU cpu;

    @Before
    public void setUp() {
        cpu = new CPU();
    }

    @Test
    public void zeroFlagRegSet() {
        cpu.setFlag(FLAG_ZERO);
        assertTrue((cpu.AF.getLow() & (0x01 << 7)) != 0);
    }

    @Test
    public void zeroFlagSet() {
        cpu.setFlag(FLAG_ZERO);

        assertTrue(cpu.isFlagSet(FLAG_ZERO));
    }

    @Test
    public void zeroFlagRegReset() {
        cpu.setFlag(FLAG_ZERO);
        cpu.resetFlag(FLAG_ZERO);

        assertTrue((cpu.AF.getLow() & (0x01 << 7)) == 0);
    }

    @Test
    public void zeroFlagReset() {
        cpu.setFlag(FLAG_ZERO);
        cpu.resetFlag(FLAG_ZERO);

        assertFalse(cpu.isFlagSet(FLAG_ZERO));
    }

    @Test
    public void negativeFlagReg() {
        cpu.setFlag(FLAG_NEGATIVE);
        assertTrue((cpu.AF.getLow() & (0x01 << 6)) != 0);
    }

    @Test
    public void negativeFlagSet() {
        cpu.setFlag(FLAG_NEGATIVE);

        assertTrue(cpu.isFlagSet(FLAG_NEGATIVE));
    }

    @Test
    public void halfFlagReg() {
        cpu.setFlag(FLAG_HALF);
        assertTrue((cpu.AF.getLow() & (0x01 << 5)) != 0);
    }

    @Test
    public void halfFlagSet() {
        cpu.setFlag(FLAG_HALF);

        assertTrue(cpu.isFlagSet(FLAG_HALF));
    }

    @Test
    public void carryFlagReg() {
        cpu.setFlag(FLAG_CARRY);
        assertTrue((cpu.AF.getLow() & (0x01 << 4)) != 0);
    }

    @Test
    public void carryFlagSet() {
        cpu.setFlag(FLAG_CARRY);

        assertTrue(cpu.isFlagSet(FLAG_CARRY));
    }

}
