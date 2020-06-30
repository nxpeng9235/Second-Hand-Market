package cn.scut.wg.secondhandmarket.service;

import cn.scut.wg.secondhandmarket.dao.TradeDao;
import cn.scut.wg.secondhandmarket.domain.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeService {

    @Autowired
    private volatile TradeDao tradeDao;

    public synchronized List<Trade> getAllSellingTrade() throws Exception {
        return tradeDao.selectAllSellingTrade();
    }

    public synchronized Trade getTrade(int tid) throws Exception{
        return tradeDao.selectByTid(tid);
    }

    public synchronized String buyTrade(int tid, String username) throws Exception{
        return tradeDao.buyTrade(tid, username);
    }

    public synchronized List<Trade> getMyBuyTrade(String username) throws Exception {
        return tradeDao.selectMyBuyTrade(username);
    }

    public synchronized List<Trade> getMySellTrade(String username) throws Exception{
        List<Trade> list1 = tradeDao.selectMySellingTrade(username);
        List<Trade> list2 = tradeDao.selectMySellFinishTrade(username);
        List<Trade> list3 = tradeDao.selectMySellJudgingTrade(username);
        List<Trade> list4 = tradeDao.selectMySellJudgeSuccessTrade(username);
        list1.addAll(list2);
        list1.addAll(list3);
        list1.addAll(list4);
        return list1;
    }

    public synchronized List<Trade> getMyJudgeTrade(String username) throws Exception {
        List<Trade> list1 = tradeDao.selectMyJudgingTrade(username);
        List<Trade> list2 = tradeDao.selectMyJudgeSuccessTrade(username);
        list1.addAll(list2);
        return list1;
    }

    public synchronized int applyJudge(int tid) throws Exception {
        return tradeDao.applyJudge(tid);
    }

    public synchronized int deleteTrade(int tid) throws Exception {
        return tradeDao.deleteTrade(tid);
    }

    public synchronized List<Trade> getAllTrade() throws Exception {
        return tradeDao.selectAllTrade();
    }

    public synchronized List<Trade> getAllJudgeTrade() throws Exception {
        List<Trade> list1 = tradeDao.selectAllJudgingTrade();
        List<Trade> list2 = tradeDao.selectAllJudgeSuccessTrade();
        list1.addAll(list2);
        return list1;
    }

    public synchronized int judge(int tid, boolean pass) throws Exception {
        return tradeDao.judge(tid, pass);
    }

    public synchronized String newTrade(String username, String name, String description, int price, String imgPathsString) throws Exception {
        return tradeDao.newTrade(username, name, description, price, imgPathsString);
    }

    public synchronized String updateTrade(int tid, String sellusername, String buyusername, String name, String description, int price, String imgPathsString, int status) throws Exception {
        return tradeDao.updateTrade(tid, sellusername, buyusername, name, description, price, imgPathsString, status);
    }
}
