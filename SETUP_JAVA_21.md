# 配置 Java 21 编译器

## 方法一：在 IntelliJ IDEA 中修改（推荐）

### 1. 设置项目 SDK
- 打开：`File` → `Project Structure`（或按 `Ctrl+Alt+Shift+S`）
- 在左侧选择 **"Project"**
- 设置 **Project SDK** 为 `Java 21`
- 如果没有 Java 21，点击 **"Add SDK"** → **"Download JDK"** 下载
- 设置 **Project language level** 为 `21 - Record patterns`
- 点击 **"OK"**

### 2. 设置编译器版本
- 打开：`File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Java Compiler`
- 找到 **"Override compiler parameters per-module"**
- 将每个模块的 Target bytecode version 设置为 `21`
- 或直接取消勾选 "Override compiler parameters per-module"，使用全局设置

### 3. 配置 Maven Runner
- 打开：`File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Maven` → `Runner`
- 设置 **JRE** 为 `JDK 21`
- 点击 **"OK"**

### 4. 重新加载 Maven 项目
- 右键点击项目
- 选择 `Maven` → `Reload Project`

---

## 方法二：在 Eclipse 中修改

### 1. 设置项目 JDK
- 右键项目 → `Properties`
- 选择 `Java Build Path` → `Libraries`
- 双击 `JRE System Library`
- 选择 `Execution Environment`：`JavaSE-21`
- 如果没有，点击 `Installed JREs` 添加 Java 21

### 2. 设置 Java 编译器
- 右键项目 → `Properties`
- 选择 `Java Compiler`
- 取消勾选 "Use compliance from '...'"
- 设置 **Compiler compliance level** 为 `21`
- 点击 **"Apply and Close"**

---

## 验证配置

创建测试文件验证 Java 版本：
```java
public class JavaVersionTest {
    public static void main(String[] args) {
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Home: " + System.getProperty("java.home"));
    }
}
```

运行后应该显示：
```
Java Version: 21.x.x
```

---

## 快速检查

在项目根目录运行：
```bash
java -version
```

应该显示 Java 21

