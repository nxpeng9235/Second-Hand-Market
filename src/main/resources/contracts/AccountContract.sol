pragma solidity >= 0.4.25;
//pragma experimental ABIEncoderV2;

import "./Table.sol";

contract AccountContract {
    // struct: User
    struct User{
        string username;
        string password;
        int256 balance;
        string addr;
    }

    // event
    event RegisterEvent(int256 ret, string username, string addr);
    event TransferEvent(int256 ret, string from_user, string to_user, int256 amount);
    event Deposit(int256 ret, string username, int256 amount);
    event Update(int256 ret, string username, string password, string addr);

    constructor() public {
        // 构造函数中创建t_users表
        createTable();
    }

    function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // 创建表
        tf.createTable("t_users", "identity", "username,password,balance,address");
    }

    function openTable() private returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_users");
        return table;
    }

    /*
    描述 : 根据用户名查询用户对象
    参数 ：
            username : 用户名

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，资产金额
    */
    function getUser(string username) internal constant returns(int256, User memory){
        Table table = openTable();
        Condition condition = table.newCondition();
        condition.EQ("username", username);
        Entries entries = table.select("user", condition);
        if (0 == uint256(entries.size())) {
            User memory u;
            return (-1, u);
        } else {
            Entry entry = entries.get(0);
            User memory user;
            user.username = entry.getString("username");
            user.password = entry.getString("password");
            user.balance = entry.getInt("balance");
            user.addr = entry.getString("address");
            return (0, user);
        }
    }

    /*
    描述 : 根据资产账户查询资产金额
    参数 ：
            balance : 资产账户

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，资产金额
    */
    function getBalance(string username) public constant returns(int256, int256) {
        int ret = 0;
        User memory user;
        (ret, user) = getUser(username);
        return (ret, user.balance);
    }
    
    /*
    描述 : 根据资产账户查询资产地址
    参数 ：
            balance : 资产账户

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，资产金额
    */
    function getAddress(string username) public constant returns(int256, string){
        int ret = 0;
        User memory user;
        (ret, user) = getUser(username);
        return (ret, user.addr);
    }

    function printAccount(string username) public returns (string, string, int256, string){
        int ret = 0;
        User memory user;
        (ret, user) = getUser(username);
        return (user.username, user.password, user.balance, user.addr);
    }

    /*
    描述 : 用户注册
    参数 ：
            username : 用户名
            password : 账户密码
            addr  : 用户地址
    返回值：
            0  注册成功
            -1 账户已存在
            -2 注册账户失败
    */
    function register(string username, string password, string addr) public returns(int256){
        int256 ret_code = 0;
        int256 ret= 0;
        User memory temp_user;
        // 查询账户是否存在
        (ret, temp_user) = getUser(username);
        if(ret != 0) {
            Table table = openTable();
            
            Entry entry = table.newEntry();
            entry.set("identity", "user");
            entry.set("username", username);
            entry.set("password", password);
            entry.set("balance", int256(0));
            entry.set("address", addr);
            // 插入
            int count = table.insert("user", entry);
            if (count == 1) {
                // 成功
                ret_code = 0;
            } else {
                // 失败? 无权限或者其他错误
                ret_code = -2;
            }
        } else {
            // 账户已存在
            ret_code = -1;
        }

        emit RegisterEvent(ret_code, username, addr);

        return ret_code;
    }

    /*
    描述 : 更新账户信息
    参数 : 
            username : 用户名
            password : 密码
            address : 地址
    返回值 : 
            0 : 更新成功
            -1 : 更新失败
            -2 : 账户不存在
    */
    function update(string username, string password, string addr) public returns(int256){
        int256 ret_code = 0;
        int256 ret= 0;
        User memory user;
        (ret, user) = getUser(username);
        if(ret == 0) {
            Table table = openTable();
            
            Entry entry = table.newEntry();
            entry.set("identity", "user");
            entry.set("username", user.username);
            entry.set("password", password);
            entry.set("balance", user.balance);
            entry.set("address", addr);

            Condition condition = table.newCondition();
            condition.EQ("username", username);

            // 插入
            int count = table.update("user", entry, condition);
            if (count == 1) {
                // 成功
                ret_code = 0;
            } else {
                // 失败? 无权限或者其他错误
                ret_code = -1;
            }
        } else {
            // 账户不存在
            ret_code = -2;
        }

        emit Update(ret_code, username, password, addr);

        return ret_code;
    }

    /*
    描述 : 账户充值
    参数 ：
            username : 用户名
            amount ： 存款金额
    返回值：
            0  账户充值成功
            -1 账户充值失败
            -2 账户不存在
    */
    function deposit(string username, int256 amount) public returns(int256){
        int256 ret_code = 0;
        int256 ret= 0;
        User memory user;
        (ret, user) = getUser(username);
        if(ret == 0) {
            Table table = openTable();
            
            Entry entry = table.newEntry();
            entry.set("identity", "user");
            entry.set("username", user.username);
            entry.set("password", user.password);
            entry.set("balance", (user.balance + amount));
            entry.set("address", user.addr);

            Condition condition = table.newCondition();
            condition.EQ("username", username);

            // 插入
            int count = table.update("user", entry, condition);
            if (count == 1) {
                // 成功
                ret_code = 0;
            } else {
                // 失败? 无权限或者其他错误
                ret_code = -1;
            }
        } else {
            // 账户不存在
            ret_code = -2;
        }

        emit Deposit(ret_code, username, amount);

        return ret_code;
    }

    /*
    描述 : 资产转移
    参数 ：
            from_account : 转移资产账户
            to_account ： 接收资产账户
            amount ： 转移金额
    返回值：
            0  资产转移成功
            -1 转移资产账户不存在
            -2 接收资产账户不存在
            -3 金额不足
            -4 金额溢出
            -5 其他错误
    */
    function transfer(string from_username, string to_username, int256 amount) public returns(int256) {
        // 查询转移资产账户信息
        int ret_code = 0;
        int256 ret = 0;
        User memory from_user;
        User memory to_user;

        // 转移账户是否存在?
        (ret, from_user) = getUser(from_username);
        if(ret != 0) {
            ret_code = -1;
            // 转移账户不存在
            emit TransferEvent(ret_code, from_username, to_username, amount);
            return ret_code;
        }

        // 接受账户是否存在?
        (ret, to_user) = getUser(to_username);
        if(ret != 0) {
            ret_code = -2;
            // 接收资产的账户不存在
            emit TransferEvent(ret_code, from_username, to_username, amount);
            return ret_code;
        }

        if(from_user.balance < amount) {
            ret_code = -3;
            // 转移资产的账户金额不足
            emit TransferEvent(ret_code, from_username, to_username, amount);
            return ret_code;
        }

        if(to_user.balance + amount < to_user.balance) {
            ret_code = -4;
            // 接收账户金额溢出
            emit TransferEvent(ret_code, from_username, to_username, amount);
            return ret_code;
        }

        Table table = openTable();

        Entry entry0 = table.newEntry();
        entry0.set("identity", "user");
        entry0.set("username", from_user.username);
        entry0.set("password", from_user.password);
        entry0.set("balance", (from_user.balance - amount));
        entry0.set("address", from_user.addr);

        Condition condition1 = table.newCondition();
        condition1.EQ("username", from_username);

        // 更新转账账户
        int count = table.update("user", entry0, condition1);
        if(count != 1) {
            ret_code = -5;
            // 失败? 无权限或者其他错误?
            emit TransferEvent(ret_code, from_username, to_username, amount);
            return ret_code;
        }

        Entry entry1 = table.newEntry();
        entry1.set("identity", "user");
        entry1.set("username", to_user.username);
        entry1.set("password", to_user.password);
        entry1.set("balance", (to_user.balance + amount));
        entry1.set("address", to_user.addr);

        Condition condition2 = table.newCondition();
        condition2.EQ("username", to_username);

        // 更新接收账户
        count = table.update("user", entry1, condition2);        
        if(count != 1) {
            ret_code = -6;
            // 失败? 无权限或者其他错误?
            emit TransferEvent(ret_code, from_username, to_username, amount);
            return ret_code;
        }

        emit TransferEvent(ret_code, from_username, to_username, amount);

        return ret_code;
    }
}