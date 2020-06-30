package cn.scut.wg.secondhandmarket.dao;

import cn.scut.wg.secondhandmarket.contract.TradeContract;
import cn.scut.wg.secondhandmarket.domain.Trade;
import org.fisco.bcos.web3j.precompile.crud.CRUDService;
import org.fisco.bcos.web3j.precompile.crud.Condition;
import org.fisco.bcos.web3j.precompile.crud.Table;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class TradeDao {

    @Autowired
    private CRUDService crudService;

    @Autowired
    private TradeContract tradeContract;

    private Table tableTrade;

    public Table getTableTrade() throws Exception {
        if (tableTrade == null){
            synchronized (Table.class){
                if (tableTrade == null){
                    tableTrade = crudService.desc("t_trades");
                }
            }
        }
        return tableTrade;
    }

    public List<Trade> selectAllSellingTrade() throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("status", "0");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public Trade selectByTid(int tid) throws Exception{
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("tid", String.valueOf(tid));
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        if (resultList == null || resultList.size() == 0)
            return null;
        Trade trade = new Trade(
                Integer.parseInt(resultList.get(0).get("tid")),
                resultList.get(0).get("sellusername"),
                resultList.get(0).get("buyusername"),
                resultList.get(0).get("name"),
                resultList.get(0).get("description"),
                Integer.parseInt(resultList.get(0).get("price")),
                resultList.get(0).get("figpath"),
                Integer.parseInt(resultList.get(0).get("status"))
        );
        return trade;
    }

    public String buyTrade(int tid, String username) throws Exception{
        int result = 0;
        TransactionReceipt receipt = tradeContract.buyTrade(new BigInteger(String.valueOf(tid)), username).send();
        result += tradeContract.getBuyTradeOutput(receipt).getValue1().intValue();
        if (result == 0){
            return receipt.getBlockHash();
        }
        return "Failed! Error Code: " + result;
    }

    public List<Trade> selectMyBuyTrade(String username) throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("buyusername", username);
        condition.EQ("status", "1");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectMySellingTrade(String username) throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("sellusername", username);
        condition.EQ("status", "0");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectMySellFinishTrade(String username) throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("sellusername", username);
        condition.EQ("status", "1");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectMySellJudgingTrade(String username) throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("sellusername", username);
        condition.EQ("status", "2");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectMySellJudgeSuccessTrade(String username) throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("sellusername", username);
        condition.NE("buyusername", "##");
        condition.EQ("status", "3");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectMyJudgingTrade(String username) throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("buyusername", username);
        condition.EQ("status", "2");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectMyJudgeSuccessTrade(String username) throws Exception{
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("buyusername", username);
        condition.EQ("status", "3");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public int applyJudge(int tid) throws Exception {
        int result = 0;
        TransactionReceipt receipt = tradeContract.applyJudge(new BigInteger(String.valueOf(tid))).send();
        result += tradeContract.getApplyJudgeOutput(receipt).getValue1().intValue();
        return result;
    }

    public int deleteTrade(int tid) throws Exception {
        int result = 0;
        TransactionReceipt receipt = tradeContract.deleteTrade(new BigInteger(String.valueOf(tid))).send();
        result += tradeContract.getApplyJudgeOutput(receipt).getValue1().intValue();
        return result;
    }

    public List<Trade> selectAllTrade() throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectAllJudgingTrade() throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.EQ("status", "2");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public List<Trade> selectAllJudgeSuccessTrade() throws Exception {
        Condition condition = getTableTrade().getCondition();
        getTableTrade().setKey("trade");
        condition.NE("buyusername", "##");
        condition.EQ("status", "3");
        List<Map<String, String>> resultList = crudService.select(getTableTrade(), condition);
        List<Trade> tradeList = new LinkedList<Trade>();
        for (Map<String, String> m : resultList){
            Trade t = new Trade(
                    Integer.parseInt(m.get("tid")),
                    m.get("sellusername"),
                    m.get("buyusername"),
                    m.get("name"),
                    m.get("description"),
                    Integer.parseInt(m.get("price")),
                    m.get("figpath"),
                    Integer.parseInt(m.get("status")));
            tradeList.add(t);
        }
        return tradeList;
    }

    public int judge(int tid, boolean pass) throws Exception {
        int result = 0;
        TransactionReceipt receipt = tradeContract.judge(new BigInteger(String.valueOf(tid)), pass).send();
        result += tradeContract.getJudgeOutput(receipt).getValue1().intValue();
        return result;
    }

    public String newTrade(String username, String name, String description, int price, String imgPathsString) throws Exception {
        int result = 0;
        TransactionReceipt receipt = tradeContract.newTrade(username, name, description, new BigInteger(String.valueOf(price)),imgPathsString).send();
        result += tradeContract.getNewTradeOutput(receipt).getValue1().intValue();
        if (result == 0){
            return receipt.getBlockHash();
        }
        return "Failed! Error Code: " + result;
    }

    public String updateTrade(int tid, String sellusername, String buyusername, String name, String description, int price, String imgPathsString, int status) throws Exception {
        int result = 0;
        TransactionReceipt receipt = tradeContract.updateTrade(new BigInteger(String.valueOf(tid)), sellusername, buyusername, name, description,
                new BigInteger(String.valueOf(price)), imgPathsString, new BigInteger(String.valueOf(status))).send();
        result += tradeContract.getUpdateTradeOutput(receipt).getValue1().intValue();
        if (result == 0){
            return receipt.getBlockHash();
        }
        return "Failed! Error Code: " + result;
    }
}
