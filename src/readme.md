本项目没有使用gradle和maven管理依赖，需要手动做如下操作：
1. 手动导入fastjson和idea2024之前版本的json.jar
2. 构建这样的打包目录
![img.png](img.png)
3. 打完jar后手动将jar包内的json.jar依赖删除，然后使用该jar压缩成zip
