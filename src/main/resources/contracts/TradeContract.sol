pragma solidity >= 0.4.25;

import "./Table.sol";

contract TradeContract {
    struct Trade{
        int256 tid;
        string sellusername;
        string buyusername;
        string name;
        string description;
        int256 price;
        string figpath;
        int256 status;
    }

    int256 internal numOfTrade = 0;

    event NewTrade(int256 ret, string sellusername, string name, string description, int256 price, string figpath);
    event UpdateTrade(int256 ret, int256 tid);
    event BuyTrade(int256 ret, int256 tid, string buyusername);
    event ApplyJudge(int256 ret, int256 tid);
    event Judge(int256 ret, int256 tid, bool pass);
    event DeleteTrade(int256 ret, int256 tid);

    constructor() public{
        createTable();
        numOfTrade = 10000;
    }

    function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // 创建表
        tf.createTable("t_trades", "identity", "tid,sellusername,buyusername,name,description,price,figpath,status");
    }

    function openTable() private returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_trades");
        return table;
    }

    function int2str(int _i) internal pure returns (string memory _intAsString) {
	    if (_i == 0) {
	        return "0";
	    }
	    int j = _i;
	    uint len;
	    while (j != 0) {
	        len++;
	        j /= 10;
	    }
	    bytes memory bstr = new bytes(len);
	    uint k = len - 1;
	    while (_i != 0) {
            bstr[k--] = byte(uint8(48 + _i % 10));
            _i /= 10;
	    }
	    return string(bstr);
    }

    /*
    描述 : 创建交易
    参数 :
        sellusername : 卖方用户名
        name : 商品名字
        description : 商品描述
        price : 价格
        figpath : 图片路径
    状态 : 
        0 : 等待卖出
        1 : 已卖出
        2 : 等待仲裁
        3 : 已下架
    返回值：
        0  订单创建成功
        -1 订单创建失败
    */
    function newTrade(string sellusername, string name, string description, int256 price, string figpath) public returns(int256){
        int256 ret_code = 0;
        Table table = openTable();

        numOfTrade = numOfTrade + 1;
        Entry entry = table.newEntry();
        string memory tid = int2str(numOfTrade);

        entry.set("identity", "trade");
        entry.set("tid", tid);
        entry.set("sellusername", sellusername);
        entry.set("buyusername", "##");
        entry.set("name", name);
        entry.set("description", description);
        entry.set("price", price);
        entry.set("figpath", figpath);
        entry.set("status", int256(0));
        // 插入
        int count = table.insert("trade", entry);
        if (count == 1) {
            // 成功
            ret_code = 0;
        } else {
            // 失败? 无权限或者其他错误
            ret_code = -1;
        }
        emit NewTrade(ret_code, sellusername, name, description, price, figpath);

        return ret_code;
    }

    function getTrade(string tid) internal constant returns (int256, Trade memory){
        Table table = openTable();

        Condition condition = table.newCondition();
        condition.EQ("tid", tid);

        Entries entries = table.select("trade", condition);
        if (0 == uint256(entries.size())) {
            Trade memory t;
            return (-1, t);
        } else {
            Entry entry = entries.get(0);
            Trade memory trade;
            trade.tid = entry.getInt("tid");
            trade.sellusername = entry.getString("sellusername");
            trade.buyusername = entry.getString("buyusername");
            trade.name = entry.getString("name");
            trade.description = entry.getString("description");
            trade.price = entry.getInt("price");
            trade.figpath = entry.getString("figpath");
            trade.status = entry.getInt("status");
            return (0, trade);
        }
    }

    function printTrade(string tid) public returns(int256, string, string, string, string, int256, string, int256){
        int256 ret;
        Trade memory trade;
        (ret, trade) = getTrade(tid);
        return (trade.tid, trade.sellusername, trade.buyusername, trade.name, trade.description, trade.price, trade.figpath, trade.status);
    }

    /*
    描述 : 更新交易
    参数 :
        trade : 更新的交易
    返回值：
        0  更新交易成功
        -1 更新交易失败
    */
    function updateTrade(int256 tid, string sellusername, string buyusername, string name, string description, int256 price, string figpath, int256 status) public returns(int256){
        //int256 ret_code = 0;
        Table table = openTable();
        Entry entry = table.newEntry();
        entry.set("identity", "trade");
        entry.set("tid", tid);
        entry.set("sellusername", sellusername);
        entry.set("buyusername", buyusername);
        entry.set("name", name);
        entry.set("description", description);
        entry.set("price", price);
        entry.set("figpath", figpath);
        entry.set("status", status);

        Condition condition = table.newCondition();
        condition.EQ("tid", int2str(tid));

        // 更新
        int count = table.update("trade", entry, condition);
        emit UpdateTrade(count-1, tid);
        if (count == 1) {
            // 成功
            return 0;
        } else {
            // 失败? 无权限或者其他错误
            return -1;
        }
    }

    /*
    描述 : 购买交易
    参数 :
        tid : 交易id
        buyusername : 买方用户名
    返回值：
        0  购买交易成功
        -1 购买交易失败
        -2 交易id不存在
        -3 交易状态并非可购买
    */
    function buyTrade(int256 tid, string buyusername) public returns(int256){
        int256 ret_code = 0;
        int256 ret;
        Trade memory trade;
        (ret, trade) = getTrade(int2str(tid));
        if (ret != 0){
            ret_code = -2;
        }
        else{            
            if (trade.status != 0){
                ret_code = -3;
            }
            else{
                Table table = openTable();
                Entry entry = table.newEntry();
                entry.set("identity", "trade");
                entry.set("tid", trade.tid);
                entry.set("sellusername", trade.sellusername);
                entry.set("buyusername", buyusername);
                entry.set("name", trade.name);
                entry.set("description", trade.description);
                entry.set("price", trade.price);
                entry.set("figpath", trade.figpath);
                entry.set("status", int256(1));

                Condition condition = table.newCondition();
                condition.EQ("tid", int2str(tid));
                
                // 更新
                int count = table.update("trade", entry, condition);
                if (count == 1) {
                    // 成功
                    ret_code = 0;
                } else {
                    // 失败? 无权限或者其他错误
                    ret_code = -1;
                }
            }
        }
        emit BuyTrade(ret_code, tid, buyusername);
        return ret_code;
    }

    /*
    描述 : 申请仲裁
    参数 :
        tid : 交易id
    返回值：
        0  申请仲裁成功
        -1 申请仲裁失败
        -2 交易id不存在
        -3 交易状态并非已购买
    */
    function applyJudge(int256 tid) public returns(int256){
        int256 ret_code = 0;
        int256 ret;
        Trade memory trade;
        (ret, trade) = getTrade(int2str(tid));
        if (ret != 0){
            ret_code = -2;
        }
        else{            
            if (trade.status != 1){
                ret_code = -3;
            }
            else{
                Table table = openTable();
                Entry entry = table.newEntry();
                entry.set("identity", "trade");
                entry.set("tid", trade.tid);
                entry.set("sellusername", trade.sellusername);
                entry.set("buyusername", trade.buyusername);
                entry.set("name", trade.name);
                entry.set("description", trade.description);
                entry.set("price", trade.price);
                entry.set("figpath", trade.figpath);
                entry.set("status", int256(2));

                Condition condition = table.newCondition();
                condition.EQ("tid", int2str(tid));

                // 更新
                int count = table.update("trade", entry, condition);
                if (count == 1) {
                    // 成功
                    ret_code = 0;
                } else {
                    // 失败? 无权限或者其他错误
                    ret_code = -1;
                }
            }
        }
        emit ApplyJudge(ret_code, tid);
        return ret_code;
    }

    /*
    描述 : 处理仲裁
    参数 :
        tid : 交易id
        pass : 是否通过
    返回值：
        0  仲裁成功
        -1 仲裁失败
        -2 交易id不存在
        -3 交易状态并非申请仲裁
    */
    function judge(int256 tid, bool pass) public returns(int256){
        int256 ret_code = 0;
        int256 ret;
        Trade memory trade;
        (ret, trade) = getTrade(int2str(tid));
        if (ret != 0){
            ret_code = -2;
        }
        else{            
            if (trade.status != 2){
                ret_code = -3;
            }
            else{
                Table table = openTable();
                Entry entry = table.newEntry();
                entry.set("identity", "trade");
                entry.set("tid", trade.tid);
                entry.set("sellusername", trade.sellusername);
                entry.set("buyusername", trade.buyusername);
                entry.set("name", trade.name);
                entry.set("description", trade.description);
                entry.set("price", trade.price);
                entry.set("figpath", trade.figpath);
                if (pass){ // 如果通过，则交易下架
                    entry.set("status", int256(3));
                } else {   // 如果未通过，则交易恢复已完成状态
                    entry.set("status", int256(1));
                }

                Condition condition = table.newCondition();
                condition.EQ("tid", int2str(tid));
                
                // 更新
                int count = table.update("trade", entry, condition);
                if (count == 1) {
                    // 成功
                    ret_code = 0;
                } else {
                    // 失败? 无权限或者其他错误
                    ret_code = -1;
                }
            }
        }
        emit Judge(ret_code, tid, pass);
        return ret_code;
    }

    /*
    描述 : 删除订单
    参数 :
        tid : 交易id
    返回值：
        0  删除订单成功
        -1 删除订单失败
        -2 交易id不存在
        -3 交易状态并非可购买
    */
    function deleteTrade(int256 tid) public returns(int256){
        int256 ret_code = 0;
        int256 ret;
        Trade memory trade;
        (ret, trade) = getTrade(int2str(tid));
        if (ret != 0){
            ret_code = -2;
        }
        else{            
            if (trade.status != 0){
                ret_code = -3;
            }
            else{
                Table table = openTable();
                Entry entry = table.newEntry();
                entry.set("identity", "trade");
                entry.set("tid", trade.tid);
                entry.set("sellusername", trade.sellusername);
                entry.set("buyusername", trade.buyusername);
                entry.set("name", trade.name);
                entry.set("description", trade.description);
                entry.set("price", trade.price);
                entry.set("figpath", trade.figpath);
                entry.set("status", int256(3));

                Condition condition = table.newCondition();
                condition.EQ("tid", int2str(tid));

                // 更新
                int count = table.update("trade", entry, condition);
                if (count == 1) {
                    // 成功
                    ret_code = 0;
                } else {
                    // 失败? 无权限或者其他错误
                    ret_code = -1;
                }
            }
        }
        emit DeleteTrade(ret_code, tid);
        return ret_code;
    }
}