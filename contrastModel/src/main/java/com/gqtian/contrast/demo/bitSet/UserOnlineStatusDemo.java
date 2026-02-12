package com.gqtian.contrast.demo.bitSet;

import java.util.BitSet;
import java.util.Random;

import org.springframework.stereotype.Service;

/**
 * 🎯 用户在线状态管理
 * 
 * 使用BitSet高效管理百万级用户在线状态
 * 优势：相比HashMap<Integer, Boolean>，内存占用仅为1/32
 * 
 * 应用场景：
 * - 在线用户统计
 * - 活跃用户分析
 * - 用户状态快速查询
 *
 * @author gqtian
 * @date 2026/02/04
 */
@Service
public class UserOnlineStatusDemo {

    private static final int MAX_USER_ID = 10_000_000;
    
    private BitSet onlineUsers;

    public UserOnlineStatusDemo() {
        this.onlineUsers = new BitSet(MAX_USER_ID);
    }

    public void demo() {
        System.out.println("========== 用户在线状态管理演示 ==========");
        
        long startTime = System.currentTimeMillis();
        
        Random random = new Random();
        int onlineCount = 1_000_000;
        
        System.out.println("1. 模拟 " + onlineCount + " 个用户上线...");
        for (int i = 0; i < onlineCount; i++) {
            int userId = random.nextInt(MAX_USER_ID);
            userLogin(userId);
        }
        
        System.out.println("2. 当前在线用户数量: " + getOnlineUserCount());
        
        System.out.println("3. 检查特定用户是否在线:");
        int[] testUsers = {12345, 67890, 999999};
        for (int userId : testUsers) {
            System.out.println("   用户 " + userId + " 在线状态: " + isUserOnline(userId));
        }
        
        System.out.println("4. 模拟部分用户下线...");
        int offlineCount = 100_000;
        for (int i = 0; i < offlineCount; i++) {
            int userId = random.nextInt(MAX_USER_ID);
            userLogout(userId);
        }
        
        System.out.println("5. 下线后在线用户数量: " + getOnlineUserCount());
        
        System.out.println("6. 内存占用估算: " + estimateMemoryUsage() + " MB");
        
        long endTime = System.currentTimeMillis();
        System.out.println("7. 总耗时: " + (endTime - startTime) + " ms");
        
        System.out.println("========================================\n");
    }

    /**
     * 用户登录
     */
    public void userLogin(int userId) {
        if (userId >= 0 && userId < MAX_USER_ID) {
            onlineUsers.set(userId);
        }
    }

    /**
     * 用户登出
     */
    public void userLogout(int userId) {
        if (userId >= 0 && userId < MAX_USER_ID) {
            onlineUsers.clear(userId);
        }
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(int userId) {
        return userId >= 0 && userId < MAX_USER_ID && onlineUsers.get(userId);
    }

    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return onlineUsers.cardinality();
    }

    /**
     * 估算内存占用（MB）
     */
    public double estimateMemoryUsage() {
        return (MAX_USER_ID / 8.0) / (1024 * 1024);
    }

    /**
     * 清空所有在线用户
     */
    public void clearAllUsers() {
        onlineUsers.clear();
    }

    /**
     * 获取在线用户BitSet的副本
     */
    public BitSet getOnlineUsersBitSet() {
        return (BitSet) onlineUsers.clone();
    }
}
