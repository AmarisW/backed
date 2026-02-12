package com.gqtian.contrast.demo.bitSet;

import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

import com.gqtian.common.globalProcess.GlobalException;

/**
 * BitSet工具 - 设备编码管理（1-999）
 * 用于管理设备编码的分配和释放，支持线程安全操作
 *
 * @className BitSetMange
 * @author gqtian
 * @date 2026/2/4 16:33
 */
public class BitSetMange {

    /**
     * BitSet用于标记编码是否被使用
     * bit位为true表示该编码已被使用，false表示未使用
     */
    private final BitSet bitSet;

    /**
     * 编码范围：1-999
     */
    private final int capacity;
    private final int minCode;
    private final int maxCode;

    /**
     * 可重入锁，保证线程安全
     */
    private final ReentrantLock lock;

    /**
     * 构造函数，初始化编码池
     */
    public BitSetMange(int minCode, int maxCode) {
        this.capacity = maxCode - minCode + 1;
        this.minCode = minCode;
        this.maxCode = maxCode;
        this.bitSet = new BitSet(this.capacity);
        this.lock = new ReentrantLock();
    }

    /**
     * 获取一个未使用的编码
     * 从编码池中获取第一个未被使用的编码，并标记为已使用
     *
     * @return 可用的编码（1-999）
     * @throws NoSuchElementException 当没有可用编码时抛出异常
     */
    private int acquireCode() {
        lock.lock();
        try {
            // 查找第一个未被使用的bit位（值为false的位）
            int index = bitSet.nextClearBit(0);

            // 检查是否超出范围
            if (index >= this.capacity) {
                throw new GlobalException("没有可用的编码，所有编码（1-999）都已被使用");
            }

            // 标记该位为已使用
            bitSet.set(index);

            // 返回实际编码（index + MIN_CODE）
            return index + this.minCode;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 释放已使用的编码
     * 将指定编码标记为未使用状态，使其可以被再次分配
     *
     * @param code 要释放的编码（1-999）
     * @throws IllegalArgumentException 当编码不在有效范围内时抛出异常
     */
    private void releaseCode(int code) {
        // 参数校验
        if (code < this.minCode || code > this.maxCode) {
            throw new GlobalException(
                String.format("编码必须在 %d-%d 范围内，当前值: %d", this.minCode, this.maxCode, code));
        }

        lock.lock();
        try {
            // 将编码转换为BitSet的索引
            int index = code - this.minCode;
            // 清除该位，标记为未使用
            bitSet.clear(index);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 标记指定编码为已使用
     * 将指定编码位置设置为true,表示该编码已被占用
     *
     * @param code 要标记的编码（1-999）
     * @throws IllegalArgumentException 当编码不在有效范围内时抛出异常
     */
    public void markAsUsed(int code) {
        // 参数校验
        if (code < this.minCode || code > this.maxCode) {
            throw new GlobalException(
                String.format("编码必须在 %d-%d 范围内，当前值: %d", this.minCode, this.maxCode, code));
        }

        lock.lock();
        try {
            // 将编码转换为BitSet的索引（1-based转0-based）
            int index = code - this.minCode;
            // 标记该位为已使用
            bitSet.set(index, true);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查指定编码是否已被使用
     *
     * @param code 要检查的编码（1-999）
     * @return true表示已使用，false表示未使用
     * @throws IllegalArgumentException 当编码不在有效范围内时抛出异常
     */
    private boolean isUsed(int code) {
        // 参数校验
        if (code < this.minCode || code > this.maxCode) {
            throw new GlobalException(
                String.format("编码必须在 %d-%d 范围内，当前值: %d", this.minCode, this.maxCode, code));
        }

        lock.lock();
        try {
            int index = code - this.minCode;
            return bitSet.get(index);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取已使用的编码数量
     *
     * @return 已使用的编码数量
     */
    private int getUsedCount() {
        lock.lock();
        try {
            return bitSet.cardinality();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取可用的编码数量
     *
     * @return 可用的编码数量
     */
    private int getAvailableCount() {
        return this.capacity - getUsedCount();
    }

    /**
     * 重置所有编码，将所有编码标记为未使用
     */
    private void reset() {
        lock.lock();
        try {
            bitSet.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 批量获取多个未使用的编码
     *
     * @param count 需要获取的编码数量
     * @return 编码数组
     * @throws IllegalArgumentException 当请求数量小于1时抛出异常
     * @throws NoSuchElementException 当可用编码不足时抛出异常
     */
    private int[] acquireCodes(int count) {
        if (count < 1) {
            throw new GlobalException("请求的编码数量必须大于0");
        }

        lock.lock();
        try {
            if (getAvailableCount() < count) {
                throw new GlobalException(String.format("可用编码不足，需要: %d, 可用: %d", count, getAvailableCount()));
            }

            int[] codes = new int[count];
            for (int i = 0; i < count; i++) {
                codes[i] = acquireCodeInternal();
            }
            return codes;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 内部方法：获取编码（无锁版本，需在锁内调用）
     */
    private int acquireCodeInternal() {
        int index = bitSet.nextClearBit(0);
        if (index >= this.capacity) {
            throw new GlobalException("没有可用的编码");
        }
        bitSet.set(index);
        return index + this.minCode;
    }

    /**
     * 批量标记多个编码为已使用
     *
     * @param codes 要标记的编码数组
     * @throws IllegalArgumentException 当任一编码不在有效范围内时抛出异常
     */
    public void markAsUsed(int[] codes) {
        if (codes == null || codes.length == 0) {
            return;
        }

        lock.lock();
        try {
            for (int code : codes) {
                // 参数校验
                if (code < this.minCode || code > this.maxCode) {
                    throw new GlobalException(
                        String.format("编码必须在 %d-%d 范围内，当前值: %d", this.minCode, this.maxCode, code));
                }
                // 将编码转换为BitSet的索引
                int index = code - this.minCode;
                bitSet.set(index, true);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 批量释放编码
     *
     * @param codes 要释放的编码数组
     */
    public void releaseCodes(int[] codes) {
        if (codes == null || codes.length == 0) {
            return;
        }

        lock.lock();
        try {
            for (int code : codes) {
                if (code >= this.minCode && code <= this.maxCode) {
                    int index = code - this.minCode;
                    bitSet.clear(index);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BitSetMange bitSetMange = new BitSetMange(1, 9);
        bitSetMange.markAsUsed(5);
        bitSetMange.markAsUsed(6);
        System.out.println(bitSetMange.getUsedCount());
        System.out.println(bitSetMange.isUsed(5));

        int[] ints = bitSetMange.acquireCodes(5);
        for (int i : ints) {
            System.out.println(i);
        }
    }

}
