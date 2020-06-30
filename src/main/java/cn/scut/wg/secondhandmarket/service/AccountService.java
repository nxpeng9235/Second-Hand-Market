package cn.scut.wg.secondhandmarket.service;

import cn.scut.wg.secondhandmarket.dao.AccountDao;
import cn.scut.wg.secondhandmarket.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class AccountService {

    @Autowired
    private volatile AccountDao accountDao;

    public synchronized Account login(Account account) throws Exception{
        return accountDao.selectByNameAndPwd(account);
    }

    public synchronized int register(Account account) throws Exception{
        return accountDao.insertAccount(account);
    }

    public synchronized int update(Account account) throws Exception{
        return accountDao.updateAccount(account);
    }

    public synchronized int deposit(Account account, BigInteger amount) throws Exception{
        return accountDao.deposit(account, amount);
    }

    public synchronized int transfer(String fromUsername, String toUsername, int amount) throws Exception{
        return accountDao.transfer(fromUsername, toUsername, amount);
    }
}
