package com.demo.trans.specifications;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:53
 */
public class UndoEventCap extends AWTEvent {
    private boolean start;

    public UndoEventCap(Object source, boolean start) {
        super(source, AWTEvent.RESERVED_ID_MAX + 2);
        this.start = start;
    }

    public boolean isStart() {
        return this.start;
    }

    @Override
    public String toString() {
        return "UndoEventCap " + this.start;
    }
}
