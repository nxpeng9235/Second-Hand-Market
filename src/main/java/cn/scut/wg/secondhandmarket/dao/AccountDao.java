package cn.scut.wg.secondhandmarket.dao;

import cn.scut.wg.secondhandmarket.contract.AccountContract;
import cn.scut.wg.secondhandmarket.domain.Account;
import org.fisco.bcos.web3j.precompile.crud.*;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Repository
public class AccountDao {

    @Autowired
    private CRUDService crudService;

    @Autowired
    private AccountContract accountContract;

    private Table tableAccount;

    public Table getTableAccount() throws Exception{
        if (tableAccount == null){
            synchronized (Table.class){
                if (tableAccount == null){
                    tableAccount = crudService.desc("t_users");
                }
            }
        }
        return tableAccount;
    }

    public Account selectByNameAndPwd(Account account) throws Exception {
        Condition condition = getTableAccount().getCondition();
        getTableAccount().setKey("user");
        condition.EQ("username", account.getUsername());
        condition.Limit(1);
        List<Map<String, String>> resultSelect = crudService.select(getTableAccount(), condition);

        if (resultSelect.size() == 0 || resultSelect == null){
            System.out.println("用户不存在！");
            return null;
        }
        if (account.getUsername().equals(resultSelect.get(0).get("username")) &&
                account.getPassword().equals(resultSelect.get(0).get("password"))){

            account.setBalance(Integer.parseInt(resultSelect.get(0).get("balance")));
            account.setAddress(resultSelect.get(0).get("address"));
            return account;
        }
        else
            return null;
    }

    public int insertAccount(Account account) throws Exception {
        int result = 0;
        TransactionReceipt receipt = accountContract.register(
                account.getUsername(), account.getPassword(), account.getAddress()).send();
        result += accountContract.getRegisterOutput(receipt).getValue1().intValue();
        return result;
    }

    public int updateAccount(Account account) throws Exception{
        int result = 0;
        TransactionReceipt receipt = accountContract.update(
                account.getUsername(), account.getPassword(), account.getAddress()).send();
        result += accountContract.getUpdateOutput(receipt).getValue1().intValue();
        return result;
    }

    public int deposit(Account account, BigInteger amount) throws Exception{
        int result = 0;
        TransactionReceipt receipt = accountContract.deposit(
                account.getUsername(), amount).send();
        result += accountContract.getDepositOutput(receipt).getValue1().intValue();
        return result;
    }

    public int transfer(String fromUsername, String toUsername, int amount) throws Exception {
        int result = 0;
        TransactionReceipt receipt = accountContract.transfer(fromUsername, toUsername, new BigInteger(String.valueOf(amount))).send();
        result += accountContract.getTransferOutput(receipt).getValue1().intValue();
        return result;
    }
}
