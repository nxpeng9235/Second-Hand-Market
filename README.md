# 二手物品交易系统

### 部署

* 打开 pom.xml 下载maven依赖。
* 将 fisco/nodes/127.0.0.1/sdk 文件夹中的5个密钥文件复制到 /src/resources 中。
* 打开 /src/resources/application.yml, 修改节点的ip地址和端口，确保电脑能连接上 fisco-bcos 服务。
* 将 /src/resources/contracts 中的两个.sol 合约部署到 fisco-bcos 中。
* 在 fisco 控制台中部署两个合约，获得合约地址，复制到 /src/main/java/.../secondhandmarket/util/Constant.java的对应位置。
* 将 Constant.java 中的 projectPath 和 projectBuildPath 字段修改为电脑中对应文件夹所属的***绝对路径***。
* 运行程序，在浏览器中打开 localhost:8000/secondhandmarket 即可进入系统。