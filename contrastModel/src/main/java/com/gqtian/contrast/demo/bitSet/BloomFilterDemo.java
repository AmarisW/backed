package com.gqtian.contrast.demo.bitSet;

import java.util.BitSet;

import org.springframework.stereotype.Service;

/**
 * 🔍 布隆过滤器实现
 * 
 * 使用BitSet实现简单的布隆过滤器
 * 用于快速判断元素是否可能存在（允许一定的误判率）
 * 
 * 应用场景：
 * - 缓存穿透防护
 * - URL去重
 * - 垃圾邮件过滤
 * - 爬虫URL去重
 *
 * @author gqtian
 * @date 2026/02/04
 */
@Service
public class BloomFilterDemo {

    private static final int DEFAULT_SIZE = 10_000_000;
    private static final int HASH_COUNT = 3;
    
    private BitSet bitSet;
    private int bitSetSize;
    private int addedElements;

    public BloomFilterDemo() {
        this(DEFAULT_SIZE);
    }

    public BloomFilterDemo(int size) {
        this.bitSetSize = size;
        this.bitSet = new BitSet(size);
        this.addedElements = 0;
    }

    public void demo() {
        System.out.println("========== 布隆过滤器演示 ==========");
        
        BloomFilterDemo bloomFilter = new BloomFilterDemo(1_000_000);
        
        System.out.println("1. 添加100万个URL到布隆过滤器...");
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1_000_000; i++) {
            String url = "https://example.com/page/" + i;
            bloomFilter.add(url);
        }
        
        long addTime = System.currentTimeMillis() - startTime;
        System.out.println("   添加耗时: " + addTime + " ms");
        
        System.out.println("\n2. 检查已存在的URL:");
        for (int i = 0; i < 10; i++) {
            String url = "https://example.com/page/" + (i * 100000);
            boolean exists = bloomFilter.contains(url);
            System.out.println("   " + url + " 存在? " + exists);
        }
        
        System.out.println("\n3. 检查不存在的URL:");
        int falsePositive = 0;
        int testCount = 10000;
        for (int i = 1_000_000; i < 1_000_000 + testCount; i++) {
            String url = "https://example.com/page/" + i;
            if (bloomFilter.contains(url)) {
                falsePositive++;
            }
        }
        
        double falsePositiveRate = (falsePositive * 100.0) / testCount;
        System.out.println("   测试 " + testCount + " 个不存在的URL");
        System.out.println("   误判数量: " + falsePositive);
        System.out.println("   误判率: " + String.format("%.2f", falsePositiveRate) + "%");
        
        System.out.println("\n4. 布隆过滤器统计信息:");
        System.out.println("   总容量: " + bloomFilter.bitSetSize + " bits");
        System.out.println("   已添加元素: " + bloomFilter.addedElements);
        System.out.println("   已设置bit数: " + bloomFilter.bitSet.cardinality());
        System.out.println("   填充率: " + String.format("%.2f", bloomFilter.getFillRate() * 100) + "%");
        System.out.println("   估算内存: " + String.format("%.2f", bloomFilter.estimateMemoryUsage()) + " MB");
        
        System.out.println("===================================\n");
    }

    /**
     * 添加元素到布隆过滤器
     */
    public void add(String element) {
        int[] hashes = getHashes(element);
        for (int hash : hashes) {
            bitSet.set(Math.abs(hash % bitSetSize));
        }
        addedElements++;
    }

    /**
     * 检查元素是否可能存在
     * 返回true表示可能存在，返回false表示一定不存在
     */
    public boolean contains(String element) {
        int[] hashes = getHashes(element);
        for (int hash : hashes) {
            if (!bitSet.get(Math.abs(hash % bitSetSize))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成多个哈希值
     */
    private int[] getHashes(String element) {
        int[] hashes = new int[HASH_COUNT];
        int hash1 = element.hashCode();
        int hash2 = hash1 >>> 16;
        
        for (int i = 0; i < HASH_COUNT; i++) {
            hashes[i] = hash1 + i * hash2;
        }
        
        return hashes;
    }

    /**
     * 获取填充率
     */
    public double getFillRate() {
        return bitSet.cardinality() / (double) bitSetSize;
    }

    /**
     * 估算内存占用（MB）
     */
    public double estimateMemoryUsage() {
        return (bitSetSize / 8.0) / (1024 * 1024);
    }

    /**
     * 清空过滤器
     */
    public void clear() {
        bitSet.clear();
        addedElements = 0;
    }

    /**
     * 获取已添加元素数量
     */
    public int getAddedElements() {
        return addedElements;
    }
}
