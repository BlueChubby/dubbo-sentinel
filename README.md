# Spring Boot + Dubbo + Sentinel 整合项目

## 项目简介

这是一个基于 **Spring Boot**、**Apache Dubbo** 和 **Alibaba Sentinel** 的微服务整合项目。该项目旨在展示如何使用 Dubbo 作为分布式服务框架，结合 Sentinel 进行流量控制和熔断降级等功能，确保系统的稳定性。

## 技术栈

- **Spring Boot**: 快速构建 Java 应用的开发框架。
- **Dubbo**: 高性能的分布式服务框架，提供 RPC 服务。
- **Sentinel**: 阿里巴巴开源的流量防护组件，支持流量控制、熔断降级、系统保护等功能。
- **Spring Cloud Alibaba**: 提供对 Alibaba 开源组件（如 Sentinel、Nacos）的整合。

## 项目功能

- 基于 Dubbo 的分布式服务调用。
- 集成 Sentinel 实现服务的流量监控、限流和熔断降级功能。
- 简单的 RESTful 接口供测试调用。
- 使用 Nacos 作为服务注册和配置中心（可选）。
  
## 环境要求

- JDK 8+
- Maven 3.6+
- Docker（可选，用于 Nacos 或其他服务的容器化）
- MySQL（可选，如果需要数据库支持）

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/BlueChubby/dubbo-sentinel.git
cd dubbo-sentinel
