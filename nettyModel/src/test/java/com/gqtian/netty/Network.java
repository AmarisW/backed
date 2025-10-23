package com.gqtian.netty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🤨 
 * @className Network
 * @author gqtian
 * @date: 2025/10/22 17:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Network {

    private String ip;

    private Integer port;
}