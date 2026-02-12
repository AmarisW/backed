package com.gqtian.contrast.demo.bitSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * 🎲 数据去重场景
 * 
 * 使用BitSet进行大数据量去重操作
 * 对比传统HashSet方式，展示BitSet的内存优势
 * 
 * 应用场景：
 * - 大量整数ID去重
 * - 订单号去重
 * - 用户ID去重
 * - 抽奖号码去重
 *
 * @author gqtian
 * @date 2026/02/04
 */
@Service
public class DataDeduplicationDemo {

    private static final int MAX_VALUE = 50_000_000;

    public void demo() {
        System.out.println("========== 数据去重场景演示 ==========");
        
        int dataSize = 10_000_000;
        Random random = new Random(42);
        
        System.out.println("1. 生成 " + dataSize + " 个随机数（包含重复）...");
        List<Integer> data = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            data.add(random.nextInt(MAX_VALUE));
        }
        System.out.println("   数据生成完成");
        
        System.out.println("\n2. 使用BitSet去重:");
        long bitsetStart = System.currentTimeMillis();
        Set<Integer> bitsetResult = deduplicateWithBitSet(data);
        long bitsetTime = System.currentTimeMillis() - bitsetStart;
        System.out.println("   去重后数量: " + bitsetResult.size());
        System.out.println("   耗时: " + bitsetTime + " ms");
        System.out.println("   估算内存: " + String.format("%.2f", (MAX_VALUE / 8.0) / (1024 * 1024)) + " MB");
        
        System.out.println("\n3. 使用HashSet去重:");
        long hashsetStart = System.currentTimeMillis();
        Set<Integer> hashsetResult = deduplicateWithHashSet(data);
        long hashsetTime = System.currentTimeMillis() - hashsetStart;
        System.out.println("   去重后数量: " + hashsetResult.size());
        System.out.println("   耗时: " + hashsetTime + " ms");
        System.out.println("   估算内存: " + String.format("%.2f", hashsetResult.size() * 32.0 / (1024 * 1024)) + " MB");
        
        System.out.println("\n4. 性能对比:");
        System.out.println("   BitSet 相比 HashSet:");
        if (hashsetTime > bitsetTime) {
            System.out.println("   - 速度提升: " + String.format("%.2f", (hashsetTime - bitsetTime) * 100.0 / hashsetTime) + "%");
        } else {
            System.out.println("   - 速度: HashSet更快 " + String.format("%.2f", (bitsetTime - hashsetTime) * 100.0 / bitsetTime) + "%");
        }
        System.out.println("   - 内存节省: 约 " + String.format("%.2f", (hashsetResult.size() * 32.0 - MAX_VALUE / 8.0) / (1024 * 1024)) + " MB");
        
        System.out.println("\n5. 查找指定数字是否存在:");
        int[] testNumbers = {12345, 67890, 999999, 88888888};
        BitSet bitSet = new BitSet(MAX_VALUE);
        data.forEach(num -> {
            if (num >= 0 && num < MAX_VALUE) {
                bitSet.set(num);
            }
        });
        
        for (int num : testNumbers) {
            boolean exists = num < MAX_VALUE && bitSet.get(num);
            System.out.println("   数字 " + num + " 是否存在: " + exists);
        }
        
        System.out.println("\n6. 找出两组数据的交集:");
        List<Integer> data2 = new ArrayList<>(dataSize / 2);
        for (int i = 0; i < dataSize / 2; i++) {
            data2.add(random.nextInt(MAX_VALUE));
        }
        
        Set<Integer> intersection = findIntersection(data, data2);
        System.out.println("   数据集1大小: " + data.size());
        System.out.println("   数据集2大小: " + data2.size());
        System.out.println("   交集大小: " + intersection.size());
        
        System.out.println("======================================\n");
    }

    /**
     * 使用BitSet去重
     */
    private Set<Integer> deduplicateWithBitSet(List<Integer> data) {
        BitSet bitSet = new BitSet(MAX_VALUE);
        
        for (Integer num : data) {
            if (num >= 0 && num < MAX_VALUE) {
                bitSet.set(num);
            }
        }
        
        Set<Integer> result = new HashSet<>();
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            result.add(i);
        }
        
        return result;
    }

    /**
     * 使用HashSet去重
     */
    private Set<Integer> deduplicateWithHashSet(List<Integer> data) {
        return new HashSet<>(data);
    }

    /**
     * 找出两个数据集的交集
     */
    private Set<Integer> findIntersection(List<Integer> data1, List<Integer> data2) {
        BitSet bitSet1 = new BitSet(MAX_VALUE);
        BitSet bitSet2 = new BitSet(MAX_VALUE);
        
        for (Integer num : data1) {
            if (num >= 0 && num < MAX_VALUE) {
                bitSet1.set(num);
            }
        }
        
        for (Integer num : data2) {
            if (num >= 0 && num < MAX_VALUE) {
                bitSet2.set(num);
            }
        }
        
        bitSet1.and(bitSet2);
        
        Set<Integer> result = new HashSet<>();
        for (int i = bitSet1.nextSetBit(0); i >= 0; i = bitSet1.nextSetBit(i + 1)) {
            result.add(i);
        }
        
        return result;
    }

    /**
     * 使用BitSet统计数字范围分布
     */
    public void analyzeDistribution(List<Integer> data, int rangeSize) {
        System.out.println("========== 数据分布分析 ==========");
        
        int ranges = MAX_VALUE / rangeSize;
        int[] distribution = new int[ranges + 1];
        
        BitSet bitSet = new BitSet(MAX_VALUE);
        for (Integer num : data) {
            if (num >= 0 && num < MAX_VALUE) {
                bitSet.set(num);
            }
        }
        
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            int rangeIndex = i / rangeSize;
            distribution[rangeIndex]++;
        }
        
        System.out.println("数据分布情况（每" + rangeSize + "个数字为一组）:");
        for (int i = 0; i < Math.min(10, distribution.length); i++) {
            int start = i * rangeSize;
            int end = start + rangeSize - 1;
            System.out.println("   范围 [" + start + " - " + end + "]: " + distribution[i] + " 个");
        }
        
        System.out.println("================================\n");
    }
}
