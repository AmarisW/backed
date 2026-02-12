package com.gqtian.contrast.demo.bitSet;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * 🔐 权限管理系统
 * 
 * 使用BitSet实现高效的用户权限管理
 * 每个bit位代表一个权限，1表示拥有，0表示没有
 * 
 * 应用场景：
 * - RBAC权限控制
 * - 功能权限开关
 * - 资源访问控制
 *
 * @author gqtian
 * @date 2026/02/04
 */
@Service
public class PermissionManagementDemo {

    private static final int PERMISSION_SIZE = 64;
    
    // 权限常量定义
    private static final int READ = 0;
    private static final int WRITE = 1;
    private static final int DELETE = 2;
    private static final int EXECUTE = 3;
    private static final int ADMIN = 4;
    private static final int EXPORT = 5;
    private static final int IMPORT = 6;
    private static final int AUDIT = 7;
    
    // 用户权限存储
    private Map<Integer, BitSet> userPermissions;

    public PermissionManagementDemo() {
        this.userPermissions = new HashMap<>();
    }

    public void demo() {
        System.out.println("========== 权限管理系统演示 ==========");
        
        System.out.println("1. 为用户分配权限:");
        assignPermission(1001, READ);
        assignPermission(1001, WRITE);
        assignPermission(1001, EXPORT);
        System.out.println("   用户 1001 的权限: " + getPermissionList(1001));
        
        System.out.println("\n2. 检查用户权限:");
        System.out.println("   用户 1001 有读权限? " + hasPermission(1001, READ));
        System.out.println("   用户 1001 有删除权限? " + hasPermission(1001, DELETE));
        System.out.println("   用户 1001 有导出权限? " + hasPermission(1001, EXPORT));
        
        System.out.println("\n3. 撤销权限:");
        revokePermission(1001, WRITE);
        System.out.println("   撤销写权限后，用户 1001 的权限: " + getPermissionList(1001));
        
        System.out.println("\n4. 批量分配角色权限:");
        assignRole(2001, "ADMIN");
        assignRole(2002, "USER");
        assignRole(2003, "GUEST");
        System.out.println("   用户 2001 (管理员) 权限: " + getPermissionList(2001));
        System.out.println("   用户 2002 (普通用户) 权限: " + getPermissionList(2002));
        System.out.println("   用户 2003 (访客) 权限: " + getPermissionList(2003));
        
        System.out.println("\n5. 权限合并:");
        BitSet combined = combinePermissions(1001, 2002);
        System.out.println("   用户 1001 和 2002 的权限合并: " + bitSetToString(combined));
        
        System.out.println("\n6. 权限交集:");
        BitSet intersection = intersectPermissions(2001, 2002);
        System.out.println("   用户 2001 和 2002 的共同权限: " + bitSetToString(intersection));
        
        System.out.println("======================================\n");
    }

    /**
     * 分配权限
     */
    public void assignPermission(int userId, int permission) {
        BitSet permissions = userPermissions.computeIfAbsent(userId, k -> new BitSet(PERMISSION_SIZE));
        permissions.set(permission);
    }

    /**
     * 撤销权限
     */
    public void revokePermission(int userId, int permission) {
        BitSet permissions = userPermissions.get(userId);
        if (permissions != null) {
            permissions.clear(permission);
        }
    }

    /**
     * 检查是否有权限
     */
    public boolean hasPermission(int userId, int permission) {
        BitSet permissions = userPermissions.get(userId);
        return permissions != null && permissions.get(permission);
    }

    /**
     * 分配角色（批量分配权限）
     */
    public void assignRole(int userId, String roleName) {
        BitSet permissions = new BitSet(PERMISSION_SIZE);
        
        if ("ADMIN".equals(roleName)) {
            // 管理员拥有所有权限
            for (int i = 0; i < 8; i++) {
                permissions.set(i);
            }
        } else if ("USER".equals(roleName)) {
            // 普通用户只有读写权限
            permissions.set(READ);
            permissions.set(WRITE);
        } else if ("GUEST".equals(roleName)) {
            // 访客只有读权限
            permissions.set(READ);
        }
        
        userPermissions.put(userId, permissions);
    }

    /**
     * 合并两个用户的权限
     */
    public BitSet combinePermissions(int userId1, int userId2) {
        BitSet permissions1 = userPermissions.get(userId1);
        BitSet permissions2 = userPermissions.get(userId2);
        
        BitSet combined = new BitSet(PERMISSION_SIZE);
        if (permissions1 != null) {
            combined.or(permissions1);
        }
        if (permissions2 != null) {
            combined.or(permissions2);
        }
        
        return combined;
    }

    /**
     * 获取两个用户的权限交集
     */
    public BitSet intersectPermissions(int userId1, int userId2) {
        BitSet permissions1 = userPermissions.get(userId1);
        BitSet permissions2 = userPermissions.get(userId2);
        
        if (permissions1 == null || permissions2 == null) {
            return new BitSet(PERMISSION_SIZE);
        }
        
        BitSet intersection = (BitSet) permissions1.clone();
        intersection.and(permissions2);
        
        return intersection;
    }

    /**
     * 获取用户权限列表
     */
    public String getPermissionList(int userId) {
        BitSet permissions = userPermissions.get(userId);
        if (permissions == null) {
            return "[]";
        }
        return bitSetToString(permissions);
    }

    /**
     * 将BitSet转换为可读字符串
     */
    private String bitSetToString(BitSet bitSet) {
        StringBuilder sb = new StringBuilder("[");
        String[] permissionNames = {"读", "写", "删除", "执行", "管理", "导出", "导入", "审计"};
        
        boolean first = true;
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            if (!first) {
                sb.append(", ");
            }
            if (i < permissionNames.length) {
                sb.append(permissionNames[i]);
            } else {
                sb.append("权限" + i);
            }
            first = false;
        }
        
        sb.append("]");
        return sb.toString();
    }
}
