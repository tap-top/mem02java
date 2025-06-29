# 贡献指南

感谢您对 Mem0 Java 项目的关注！我们欢迎所有形式的贡献，包括但不限于：

- 🐛 Bug 报告
- 💡 功能建议
- 📝 文档改进
- 🔧 代码贡献
- 🧪 测试用例

## 开发环境设置

### 前置要求

- JDK 17+
- Maven 3.8+
- Docker & Docker Compose (可选)
- Git

### 本地开发环境

1. **克隆项目**
   ```bash
   git clone https://github.com/changyu496/mem0-java.git
   cd mem0-java
   ```

2. **配置环境变量**
   ```bash
   cp env.example .env
   # 编辑 .env 文件，设置您的 DashScope API Key
   ```

3. **编译项目**
   ```bash
   mvn clean install -DskipTests
   ```

4. **启动依赖服务**
   ```bash
   # 使用 Docker Compose
   docker-compose up -d mysql elasticsearch kibana
   
   # 或手动安装 MySQL 和 Elasticsearch
   ```

5. **运行应用**
   ```bash
   cd mem0-server
   mvn spring-boot:run
   ```

## 代码规范

### Java 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Java 17 语法特性
- 类名使用 PascalCase
- 方法名和变量名使用 camelCase
- 常量使用 UPPER_SNAKE_CASE

### 代码风格

```java
// ✅ 好的示例
@Service
@Slf4j
public class MemoryService {
    
    private final MemoryMapper memoryMapper;
    
    public MemoryService(MemoryMapper memoryMapper) {
        this.memoryMapper = memoryMapper;
    }
    
    public List<Memory> searchMemories(String query, Long userId) {
        log.info("搜索记忆: query={}, userId={}", query, userId);
        return memoryMapper.searchByQuery(query, userId);
    }
}

// ❌ 避免的写法
@Service
public class memoryservice {
    private MemoryMapper m;
    
    public List<Memory> search(String q, Long uid) {
        return m.search(q, uid);
    }
}
```

### 注释规范

- 所有公共方法必须有 JavaDoc 注释
- 复杂逻辑需要行内注释说明
- 使用中文注释，保持简洁明了

```java
/**
 * 搜索用户记忆
 * 
 * @param query 搜索查询
 * @param userId 用户ID
 * @param limit 返回结果数量限制
 * @return 匹配的记忆列表
 */
public List<Memory> searchMemories(String query, Long userId, int limit) {
    // 参数验证
    if (StringUtils.isEmpty(query)) {
        throw new IllegalArgumentException("搜索查询不能为空");
    }
    
    // 执行搜索
    return memoryMapper.searchByQuery(query, userId, limit);
}
```

## 提交规范

### Git 提交信息格式

使用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### 提交类型

- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

### 提交示例

```bash
# 新功能
git commit -m "feat: 添加记忆分页查询功能"

# Bug 修复
git commit -m "fix: 修复向量搜索维度不匹配问题"

# 文档更新
git commit -m "docs: 更新 README 部署说明"

# 代码重构
git commit -m "refactor: 重构记忆服务层代码"
```

## 测试规范

### 单元测试

- 核心业务逻辑必须有单元测试
- 测试覆盖率不低于 80%
- 使用描述性的测试方法名

```java
@Test
@DisplayName("应该成功添加记忆")
void shouldAddMemorySuccessfully() {
    // Given
    AddMemoryRequest request = createTestRequest();
    
    // When
    List<MemoryOperationResult> results = memoryService.addMemory(request);
    
    // Then
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getStatus()).isEqualTo("SUCCESS");
}
```

### 集成测试

- API 接口必须有集成测试
- 测试数据库操作和外部服务调用
- 使用测试容器或模拟服务

## Pull Request 流程

### 1. 创建分支

```bash
# 从主分支创建功能分支
git checkout -b feature/your-feature-name

# 或修复分支
git checkout -b fix/your-bug-fix
```

### 2. 开发功能

- 编写代码和测试
- 确保所有测试通过
- 遵循代码规范

### 3. 提交代码

```bash
# 添加文件
git add .

# 提交代码
git commit -m "feat: 添加新功能描述"

# 推送到远程分支
git push origin feature/your-feature-name
```

### 4. 创建 Pull Request

- 在 GitHub 上创建 Pull Request
- 填写详细的描述和测试说明
- 关联相关的 Issue

### 5. 代码审查

- 等待维护者审查代码
- 根据反馈进行修改
- 确保 CI/CD 检查通过

## Issue 报告

### Bug 报告

请包含以下信息：

1. **环境信息**
   - 操作系统版本
   - Java 版本
   - 项目版本

2. **重现步骤**
   - 详细的操作步骤
   - 期望结果
   - 实际结果

3. **错误信息**
   - 完整的错误堆栈
   - 日志信息

4. **其他信息**
   - 截图或录屏
   - 相关配置

### 功能建议

请包含以下信息：

1. **功能描述**
   - 详细的功能需求
   - 使用场景
   - 预期效果

2. **技术考虑**
   - 实现方案建议
   - 性能影响评估
   - 兼容性考虑

## 发布流程

### 版本号规范

遵循 [语义化版本](https://semver.org/lang/zh-CN/) 规范：

- `MAJOR.MINOR.PATCH`
- `MAJOR`: 不兼容的 API 修改
- `MINOR`: 向下兼容的功能性新增
- `PATCH`: 向下兼容的问题修正

### 发布步骤

1. **更新版本号**
   ```bash
   # 更新 pom.xml 中的版本号
   mvn versions:set -DnewVersion=1.1.0
   ```

2. **更新文档**
   - 更新 CHANGELOG.md
   - 更新 README.md（如需要）

3. **创建发布标签**
   ```bash
   git tag -a v1.1.0 -m "Release version 1.1.0"
   git push origin v1.1.0
   ```

4. **发布到 Maven Central**
   ```bash
   mvn clean deploy
   ```

## 社区准则

### 行为准则

- 尊重所有贡献者
- 保持友好和专业的交流
- 欢迎新手提问
- 提供建设性的反馈

### 沟通渠道

- **GitHub Issues**: 问题报告和功能讨论
- **GitHub Discussions**: 一般性讨论
- **Pull Requests**: 代码审查和讨论

## 致谢

感谢所有为 Mem0 Java 项目做出贡献的开发者！

---

如有任何问题，请随时联系项目维护者 [@changyu496](https://github.com/changyu496)。 